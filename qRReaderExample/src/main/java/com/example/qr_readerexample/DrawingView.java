package com.example.qr_readerexample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.example.qr_readerexample.com.example.qr_readerexample.model.QREntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

/**
 * Created by daniele on 11/06/15.
 */
public class DrawingView extends View {

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    private Bitmap bitmap = null;
    private QREntity qrEntity = null;
    private int tool = 1; // 1 : pennello , 2 : gomma , 3 : sposta , 4 : testo
    private float lastd = 0;
    private float lastx;
    private float lasty;
    private TextDrawer textDrawer;
    private Date lastTextTouch;
    //queste tre variabili definiscono l'inquadramento delle variabili
    //xtras : rappresenta la traslazione horizzontale dell'immagine prima del ridimensionamento
    //un valore positivo rappresenta una traslazione verso destra
    //ytras : idem con eccezione per la traslazione verticale, un valore positivo rappresenta una traslazione verso il basso
    //zoom : rappresenta il coefficente di ridimensionamento, se l'immagine iniziale ha dimensione 10x10 e 'zoom' vale 2
    //l'immagine rappresentata nell'Activity avrà una dimensione 20x20 (zoom*larghezza,zoom*altezza)
    private float xtras=0,ytras=0,zoom=1;

    public void setTool(int tool){
      this.tool = tool;
        if (tool == 2) {
            drawPaint.setColor(Color.WHITE);
        }else if(tool == 1){
            drawPaint.setColor(paintColor);
        }else if (tool == 4){
            drawPaint.setColor(paintColor);
            textDrawer = new TextDrawer(100,paintColor,10,zoom);
            requestLayout();
        }
    }




    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }
    public void setBrushSize(int s){
        this.drawPaint.setStrokeWidth(s);
    }
    public int getBrushSize(){
       return (int)drawPaint.getStrokeWidth();
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupDrawing(){
        //get drawing area setup for interaction
        this.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //view given size
        super.onSizeChanged(w, h, oldw, oldh);
        if(bitmap!= null){
            canvasBitmap = convertToMutable(Bitmap.createBitmap(bitmap,0, 0, w,h));
            centerImage();
        }else{
            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }



        drawCanvas = new Canvas(canvasBitmap);
    }
    public void redrawBitmap( int x, int y,int w, int h){
        canvasBitmap = convertToMutable(Bitmap.createBitmap(bitmap, x, y, w, h));
        drawCanvas = new Canvas(canvasBitmap);
        centerImage();
        requestLayout();
        invalidate();
    }
    public void centerImage(){
        int w = (int) (canvasBitmap.getWidth()*zoom);
        int h = (int) (canvasBitmap.getHeight()*zoom);
        if(w<getWidth()){
            if(h<getHeight()){
                xtras = (getWidth()-w)/2;
                ytras = (getHeight()-h)/2;

            }else{
                xtras = (getWidth()-w)/2;
            }
        }else{
            if(h<getHeight()){
                ytras = (getHeight()-h)/2;
            }
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        //draw view
        int w = (int) (canvasBitmap.getWidth()*zoom);
        int h = (int) (canvasBitmap.getHeight()*zoom);
        RectF rect = new RectF(xtras,ytras,xtras+w,ytras+h);

        canvas.drawBitmap(canvasBitmap,new Rect(0,0,canvasBitmap.getWidth(),canvasBitmap.getHeight()),rect,canvasPaint);
//        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        Path trasPat = new Path();
        Matrix matrix = new Matrix();
        matrix.setScale(1/zoom,1/zoom);
        matrix.postTranslate(xtras, ytras);
        trasPat.addPath(drawPath, matrix);
        drawPaint.setStrokeWidth(drawPaint.getStrokeWidth() * zoom);
        canvas.drawPath(trasPat, drawPaint);
        drawPaint.setStrokeWidth(drawPaint.getStrokeWidth() / zoom);
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setStrokeWidth(5f);
        canvas.drawLine(rect.left, rect.top, rect.right, rect.top, p);
        canvas.drawLine(rect.left,rect.bottom,rect.left,rect.top,p);
        canvas.drawLine(rect.right,rect.top,rect.right,rect.bottom,p);
        canvas.drawLine(rect.left,rect.bottom,rect.right,rect.bottom,p);
        if(tool == 4){
            textDrawer.onDraw(canvas,xtras,ytras,zoom);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //detect user touch
        float touchX = event.getX();
        float touchY = event.getY();
        if (tool == 1 || tool == 2) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    drawPath.moveTo((touchX-xtras)*zoom, (touchY-ytras)*zoom);
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawPath.lineTo((touchX - xtras)*zoom, (touchY-ytras)*zoom);
                    break;
                case MotionEvent.ACTION_UP:
                    Matrix matrix = new Matrix();
                    matrix.setScale(1/(zoom*zoom),1/(zoom*zoom));
//                    //matrix.postTranslate(xtras, ytras);
                    Path path = new Path();
                    path.addPath(drawPath,matrix);
                    drawCanvas.drawPath(path, drawPaint);
                    drawPath.reset();
                    break;
                default:
                    return false;
            }
        }else if(tool == 3){
            switch (event.getPointerCount()) {
                case 1:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lastx = touchX;
                            lasty = touchY;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            boolean canTraslate = (xtras<getWidth()-5 || (touchX - lastx)<0)
                                    && (ytras<getHeight()-5 || (touchY - lasty)<0)
                                    && (xtras+(canvasBitmap.getWidth()*zoom)>5 || (touchX - lastx)>0)
                                    && (ytras+(canvasBitmap.getHeight()*zoom)>5 || (touchY - lasty)>0)
                                    && (Math.sqrt(Math.pow((touchX - lastx),2)+Math.pow((touchY - lasty),2))<100);
                            if(canTraslate) {
                                xtras += touchX - lastx;
                                ytras += touchY - lasty;
                            }
                            lastx = touchX;
                            lasty = touchY;
                            break;
                        case MotionEvent.ACTION_UP:
                            lastx = touchX;
                            lasty = touchY;
                            break;
                        default:
                            return false;
                    }
                    break;
                case 2:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lastd = (float)Math.sqrt(Math.pow(event.getX(0) - event.getX(1), 2) + Math.pow(event.getY(0) - event.getY(1), 2));
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float dis =  (float)Math.sqrt(Math.pow(event.getX(0)-event.getX(1),2)+Math.pow(event.getY(0)-event.getY(1),2));
                            if(Math.abs(dis-lastd) < 15 && (zoom>0.3 || (dis - lastd)>0) && (zoom<30 || (dis - lastd)<0)) {
                                zoom += (dis - lastd) / 100;
                            }
                            lastd = dis;
                            break;
                        case MotionEvent.ACTION_UP:
                            lastd = (float)Math.sqrt(Math.pow(event.getX(0) - event.getX(1), 2) + Math.pow(event.getY(0) - event.getY(1), 2));
                            break;
                        default:
                            return false;
                    }
            }
        }else if(tool==4){
            switch (event.getPointerCount()) {
                case 1:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lastTextTouch = new Date();
                            lastx = touchX;
                            lasty = touchY;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            boolean canTraslate = (textDrawer.getX()+textDrawer.getDL().getWidth()<=(canvasBitmap.getWidth()*zoom)  || (touchX - lastx)<=0)
                                    && (textDrawer.getY()+textDrawer.getDL().getHeight()<=(canvasBitmap.getHeight()*zoom) || (touchY - lasty)<=0)
                                    && (textDrawer.getX()>=0 || (touchX - lastx)>=0)
                                    && (textDrawer.getY()>=0 || (touchY - lasty)>=0)
                                    && (Math.sqrt(Math.pow((touchX - lastx),2)+Math.pow((touchY - lasty),2))<50);
                            if(canTraslate) {
                                textDrawer.traslate((int)((touchX - lastx)/zoom),(int)((touchY - lasty)/zoom));
                            }
//                            if(textDrawer.getX()+textDrawer.getSL().getWidth()>(canvasBitmap.getWidth()*zoom)){
//                                textDrawer.traslate((int)((canvasBitmap.getWidth()*zoom)-textDrawer.getX()+textDrawer.getSL().getWidth()),0);
//                            }
//                            if(textDrawer.getY()+textDrawer.getSL().getHeight()>(canvasBitmap.getHeight()*zoom)){
//                                textDrawer.traslate(0,(int)((canvasBitmap.getHeight()*zoom)-textDrawer.getY()+textDrawer.getSL().getHeight()));
//                            }
//                            if(textDrawer.getX()<0){
//                                textDrawer.traslate(-textDrawer.getX(),0);
//                            }
//                            if(textDrawer.getY()<0){
//                                textDrawer.traslate(-textDrawer.getY(),0);
//                            }
                            lastx = touchX;
                            lasty = touchY;
                            break;
                        case MotionEvent.ACTION_UP:
                            Date textTouch = new Date();
                            Log.d("time","t:" +(textTouch.getTime()-lastTextTouch.getTime()));
                            if(textTouch.getTime()-lastTextTouch.getTime()<300 && Math.abs(touchX - lastx)<10 && Math.abs(touchY - lasty)<10){
                                ((FreeDraw)getContext()).openTextDialog();
                            }
                            lastx = touchX;
                            lasty = touchY;
                            break;
                        default:
                            return false;
                    }
                    break;
                case 2:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lastd = (float)Math.sqrt(Math.pow(event.getX(0) - event.getX(1), 2) + Math.pow(event.getY(0) - event.getY(1), 2));
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float dis =  (float)Math.sqrt(Math.pow(event.getX(0)-event.getX(1),2)+Math.pow(event.getY(0)-event.getY(1),2));
                            if(Math.abs(dis-lastd) < 15 && (zoom>0.3 || (dis - lastd)>0) && (zoom<30 || (dis - lastd)<0)) {
                                textDrawer.scaleTextWidth((int)((dis - lastd) / 5));
                            }
                            lastd = dis;
                            break;
                        case MotionEvent.ACTION_UP:
                            lastd = (float)Math.sqrt(Math.pow(event.getX(0) - event.getX(1), 2) + Math.pow(event.getY(0) - event.getY(1), 2));
                            break;
                        default:
                            return false;
                    }
            }
        }

        invalidate();
        return true;
    }


    public void setColor(String newColor){
        //set color
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
        if(textDrawer!=null){
            textDrawer.setColor(paintColor);
        }
    }

    public void setup(Bitmap bitmap, QREntity qrEntity) {
        this.qrEntity = qrEntity;
        this.bitmap = bitmap;
        int h = this.getHeight();
        int w = this.getWidth();
        if(h>bitmap.getHeight()){
            h = bitmap.getHeight();
        }
        if(w>bitmap.getWidth()){
            w = bitmap.getWidth();
        }

        redrawBitmap(0, 0, w, h);
    }
    /**
     * Converts a immutable bitmap to a mutable bitmap. This operation doesn't allocates
     * more memory that there is already allocated.
     *
     * @param imgIn - Source image. It will be released, and should not be used more
     * @return a copy of imgIn, but muttable.
     */
    public static Bitmap convertToMutable(Bitmap imgIn) {
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a image, it will store the raw image data.
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // get the width and height of the source bitmap.
            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes()*height);
            imgIn.copyPixelsToBuffer(map);
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle();
            System.gc();// try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map);
            //close the temporary file and channel , then delete that also
            channel.close();
            randomAccessFile.close();

            // delete the temp file
            file.delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgIn;
    }

    public void saveBitmap() {
        qrEntity.setRepresentation(canvasBitmap);

    }
    public TextDrawer getTextDrawer(){
        return textDrawer;
    }
    public void confirmText() {
        textDrawer.drawFinalText(drawCanvas);
        invalidate();
        textDrawer = null;
    }

    public void discardText() {
        textDrawer = null;
        invalidate();
    }

    public void setText( String t) {
        textDrawer.setMessage(t);
        invalidate();
    }

    public void setTextSize(int s) {
        textDrawer.setTextsize(s);
        invalidate();
    }
}
