package com.example.qr_readerexample;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * Created by root on 14/06/15.
 */
public class TextDrawer {
    private int textwidth;
    private StaticLayout sl = null;
    private int paintColor;
    private int x=0;
    private int y=0;
    private String message;
    private float textsize;
    private float zoom;
    public TextDrawer(int textwidth,int paintColor,float textsize,float zoom){
        this.textwidth = textwidth;
        this.paintColor = paintColor;
        this.message = "message message message";
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
        sl =  new StaticLayout(message, tp, (int)(textwidth*zoom), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }
    public void traslate(int x, int y){
        this.x += x;
        this.y += y;
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
    public StaticLayout getSL(){
        return sl;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    protected void onDraw(Canvas canvas, float xtras, float ytras, float zoom) {
        this.zoom = zoom;
        redrawTextFrame();
        canvas.translate(xtras + x * zoom, ytras + y * zoom);
        sl.draw(canvas);
        Paint p = new Paint();
        p.setColor(Color.BLACK);
//        p.setStyle(Paint.Style.STROKE);
//        p.setPathEffect(new DashPathEffect(new float[] {10,20}, 1));
        canvas.drawLine(0, 0, 0, sl.getHeight(),p);
        canvas.drawLine(sl.getWidth(),sl.getHeight(),0,sl.getHeight(),p);
        canvas.drawLine(0,0,sl.getWidth(),0,p);
        canvas.drawLine(sl.getWidth(), 0, sl.getWidth(), sl.getHeight(), p);


    }

    public void singleTouch(float touchX, float touchY) {
    }
}
