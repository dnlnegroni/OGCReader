package com.example.qr_readerexample;

import android.app.Dialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by root on 26/06/15.
 */
public class TextDialog  extends Dialog {
    EditText text;
    Button okButton;
    FreeDraw drawwindow;
    public TextDialog(FreeDraw context) {
        super(context);
        setContentView(R.layout.textdialog);
        setTitle("Edit text");
         text = (EditText) this.findViewById(R.id.textdialogtext);
        okButton  = (Button) this.findViewById(R.id.textdialogbutton);
        text.setText(context.getDrawView().getTextDrawer().getMessage());

        drawwindow = context;

        okButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                confirmDialog();
                return false;
            }
        });
        show();
    }
    public void confirmDialog(){
        drawwindow.confirmTextDialog(text.getText().toString());
    }

}
