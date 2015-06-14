package com.example.qr_readerexample;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.qr_readerexample.FreeDraw;
import com.example.qr_readerexample.R;

/**
 * Created by root on 13/06/15.
 */
public class BrushSizeDialog extends Dialog {

    ImageView preview;
    SeekBar seekbar;
    TextView text;
    Button okButton;
    FreeDraw drawwindow;
    public BrushSizeDialog(FreeDraw context, int tool,int currentSize) {
        super(context);
        setContentView(R.layout.brushsizedialog);
        setTitle("Select size");
        seekbar = (SeekBar) this.findViewById(R.id.brushsizedialogseekbar);
        text = (TextView) this.findViewById(R.id.brushsizedialogtext);
        preview = (ImageView) this.findViewById(R.id.brushsizedialogpreview);
        okButton  = (Button) this.findViewById(R.id.brushsizedialogbutton);
        if(tool==1){
            text.setText("Paint brush size");
        }else if(tool == 2){
            text.setText("Erase brush size");
        }
        drawwindow = context;
        seekbar.setProgress(currentSize-5);
        okButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setBrushSize();
                return false;
            }
        });
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                previewBrushSize(seekBar.getProgress()+5);
            }
        });
        previewBrushSize(currentSize);
        show();
    }
    public void setBrushSize(){
        drawwindow.setBrushSize(seekbar.getProgress() + 5);
    }



    public void previewBrushSize(int size){
        Bitmap bitmap = Bitmap.createBitmap(size,size, Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(bitmap);
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        //Draw the image bitmap into the cavas
        tempCanvas.drawBitmap(bitmap, 0, 0, null);

        //Draw everything else you want into the canvas, in this example a rectangle with rounded edges
        tempCanvas.drawCircle(size/2,size/2,size/2,p);

        //Attach the canvas to the ImageView
        ViewGroup.LayoutParams params  = preview.getLayoutParams();
        params.width = size;
        params.height = size;
        preview.setLayoutParams(params);
        preview.setImageBitmap(bitmap);
    }

}
