package com.example.qr_readerexample;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

/**
 * Created by root on 14/06/15.
 */
public class TextDrawer {
    private int textwidth;
    private DynamicLayout dl = null;
    private int paintColor;
    private int x=0;
    private int y=0;



    private String message;



    private float textsize;
    private float zoom;


    public TextDrawer(int textwidth,int paintColor,float textsize,float zoom){
        this.textwidth = textwidth;
        this.paintColor = paintColor;
        this.message = "Hello world!";
        this.textsize = textsize;
        this.zoom = zoom;
        redrawTextFrame();
    }
    public void setColor(int color){
        paintColor = color;
    }
    public void redrawTextFrame(){

        TextPaint tp = new TextPaint();
        tp.setColor(paintColor);
        tp.setTextSize(textsize*zoom);
        dl =  new DynamicLayout(message, tp, (int)(textwidth*zoom), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
    }
    public void traslate(int x, int y){
        this.x += x;
        this.y += y;
    }

    public int getY(){
        return y;
    }
    protected void onDraw(Canvas canvas, float xtras, float ytras, float zoom) {
        this.zoom = zoom;
        redrawTextFrame();
        canvas.translate(xtras + x * zoom, ytras + y * zoom);
        dl.draw(canvas);
        Paint p = new Paint();
        p.setColor(paintColor);
        p.setStyle(Paint.Style.STROKE);
        p.setPathEffect(new DashPathEffect(new float[] {4,8}, 1));
        canvas.drawLine(0, 0, 0, dl.getHeight(),p);
        canvas.drawLine(dl.getWidth(), dl.getHeight(),0, dl.getHeight(),p);
        canvas.drawLine(0,0, dl.getWidth(),0,p);
        canvas.drawLine(dl.getWidth(), 0, dl.getWidth(), dl.getHeight(), p);


    }





    public void drawFinalText(Canvas drawCanvas) {
        this.zoom = 1;
        redrawTextFrame();
        drawCanvas.translate(x, y);
        dl.draw(drawCanvas);
        drawCanvas.translate(-x, -y);
        
    }
    public void setMessage(String message){
        this.message = message;
    }
    public void setTextwidth(int textwidth){
        this.textwidth = textwidth;
    }
    public void scaleTextWidth(int scale){
        this.textwidth += scale;

    }
    public DynamicLayout getDL(){
        return dl;
    }
    public int getX(){
        return x;
    }
    public String getMessage() {
        return message;
    }
    public void setTextsize(float textsize) {
        this.textsize = textsize;
    }
    public float getTextsize() {
        return textsize;
    }
}
