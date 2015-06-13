package com.example.qr_readerexample.com.example.qr_readerexample;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
        show();
    }
    public void setBrushSize(){
        drawwindow.setBrushSize(seekbar.getProgress()+5);
    }

}
