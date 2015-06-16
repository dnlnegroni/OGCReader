package com.example.qr_readerexample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.qr_readerexample.com.example.qr_readerexample.model.QREntity;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.util.List;


public class FreeDraw extends Activity {

    private DrawingView drawView;
    private ImageButton currPaint;
    private String qrurl;
    private  QREntity qrEntity;
    private ImageButton saveButton;
    private ImageButton moveButton;
    private ImageButton drawButton;
    private ImageButton eraseButton;
    private ImageButton textButton;
    private ImageButton editTextSizeButton;
    private ImageButton confirmTextButton;
    private ImageButton discardTextButton;
    private BrushSizeDialog dialog;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_draw);
        drawView = (DrawingView) findViewById(R.id.drawing);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        saveButton = (ImageButton) findViewById(R.id.save_btn);
        moveButton = (ImageButton) findViewById(R.id.move_btn);
        drawButton = (ImageButton) findViewById(R.id.draw_btn);
        eraseButton = (ImageButton) findViewById(R.id.erase_btn);
        textButton = (ImageButton) findViewById(R.id.text_btn);
        editTextSizeButton = (ImageButton) findViewById(R.id.editTextSize_btn);
        confirmTextButton = (ImageButton) findViewById(R.id.confirm_btn);
        discardTextButton = (ImageButton) findViewById(R.id.discard_btn);
        Intent intent = getIntent();
        qrurl =  intent.getStringExtra("QRURL");

        if( qrurl!= null){
            ParseQuery<QREntity> query = ParseQuery.getQuery(QREntity.class);
            query.whereEqualTo("QRURL", qrurl);
            final String stext = qrurl;
            query.findInBackground(new FindCallback<QREntity>() {
                @Override
                public void done(List<QREntity> results, ParseException e) {
                    if (e == null) {
                        if (results.size() > 0) {
                            qrEntity = results.get(0) ;
                            ParseFile file =  qrEntity.getRepresentation();
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] bytes, ParseException e) {
                                    if(e==null){

                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        drawView.setup(bitmap,qrEntity);
                                        drawView.requestLayout();
                                    }else{
                                        Log.d("ParseException","Parse error while loading image : " + e.getMessage());
                                    }
                                }
                            });
                        } else {
                            Log.d("error QR", "error : non existing QR");
                            qrEntity = null;
                        }
                    } else {
                        Log.d("error QR", "error :" + e.getMessage());
                        qrEntity = null;
                    }
                }
            });
        }
        resetAllButtonsColors();
        drawButton.setBackgroundColor(Color.DKGRAY);
        saveButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                resetAllButtonsColors();
                saveButton.setBackgroundColor(Color.DKGRAY);
                drawView.saveBitmap();
                goBackToDecoderActivity();
                return false;
            }
        });
        moveButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                resetAllButtonsColors();
                moveButton.setBackgroundColor(Color.DKGRAY);
                drawView.setTool(3);
                return false;
            }
        });
        eraseButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                resetAllButtonsColors();
                eraseButton.setBackgroundColor(Color.DKGRAY);
                drawView.setTool(2);
                openBrushSizeDialog(2);
                return false;
            }
        });
        textButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                resetAllButtonsColors();
                textButton.setBackgroundColor(Color.DKGRAY);
                drawView.setTool(4);
                showTextButtons();
                return false;
            }
        });
        drawButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                resetAllButtonsColors();
                drawButton.setBackgroundColor(Color.DKGRAY);
                drawView.setTool(1);
                openBrushSizeDialog(1);
                return false;
            }
        });
    }
    public void openBrushSizeDialog(int i){
        if(dialog==null || !dialog.isShowing()){
            dialog = new BrushSizeDialog(this,i,drawView.getBrushSize());
        }


    }
    public void showTextButtons(){
        editTextSizeButton.setVisibility(View.VISIBLE);
        confirmTextButton.setVisibility(View.VISIBLE);
        discardTextButton.setVisibility(View.VISIBLE);
    }
    public void hideTextButtons(){
        editTextSizeButton.setVisibility(View.INVISIBLE);
        confirmTextButton.setVisibility(View.INVISIBLE);
        discardTextButton.setVisibility(View.INVISIBLE);
    }
    public void setBrushSize(int s){
        drawView.setBrushSize(s);
        dialog.hide();
        dialog.dismiss();
        dialog = null;
    }
    public void resetAllButtonsColors(){
        moveButton.setBackgroundColor(Color.GRAY);
        saveButton.setBackgroundColor(Color.GRAY);
        drawButton.setBackgroundColor(Color.GRAY);
        eraseButton.setBackgroundColor(Color.GRAY);
        textButton.setBackgroundColor(Color.GRAY);
        hideTextButtons();
    }
    public void goBackToDecoderActivity(){
        Intent intent = new Intent(this, DecoderActivity.class);
        startActivity(intent);
    }
    public void paintClicked(View view){
        //use chosen color
        if(view!=currPaint){
            //update color
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_free_draw, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
