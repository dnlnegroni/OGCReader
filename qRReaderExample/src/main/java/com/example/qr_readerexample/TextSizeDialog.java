package com.example.qr_readerexample;

import android.app.Dialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.qr_readerexample.FreeDraw;
import com.example.qr_readerexample.R;




/**
 * Created by root on 26/06/15.
 */
public class TextSizeDialog  extends Dialog {
    SeekBar seekbar;
    TextView text;
    Button okButton;
    FreeDraw drawwindow;
    public TextSizeDialog(FreeDraw context) {
        super(context);
        setContentView(R.layout.textsizedialog);
        setTitle("Text size");
        seekbar = (SeekBar) this.findViewById(R.id.textsizedialogseekbar);
        text = (TextView) this.findViewById(R.id.textsizedialogexampletext);
        okButton  = (Button) this.findViewById(R.id.textsizedialogbutton);

        drawwindow = context;

        int currentSize = (int) Math.floor(context.getDrawView().getTextDrawer().getTextsize());
        seekbar.setProgress(currentSize);
        okButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                confirmDialog();
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
                previewTextSize(seekBar.getProgress() + 5);
            }
        });
        previewTextSize(currentSize);
        show();
    }
    public void confirmDialog(){
        drawwindow.confirmTextSizeDialog(seekbar.getProgress() + 5);
    }



    public void previewTextSize(int size){
        text.setTextSize(size);
    }
}
