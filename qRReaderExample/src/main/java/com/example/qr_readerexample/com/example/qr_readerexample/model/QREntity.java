package com.example.qr_readerexample.com.example.qr_readerexample.model;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.GetDataCallback;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;


/**
 * Created by root on 11/06/15.
 */
@ParseClassName("QREntity")
public class QREntity extends ParseObject {

        public QREntity(){

        }

        public void setQRURL(String qrurl){
            this.put("QRURL",qrurl);
        }
        public String getQRURL(){
            return this.getString("QRURL");
        }
        public void setRepresentation(Bitmap bitmap){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();
            ParseFile file = new ParseFile("QRepresentation.png", image);
            this.put("ImageFile", file);
            this.saveInBackground();
        }
        public  ParseFile getRepresentation(){

            ParseFile file =  this.getParseFile("ImageFile");
            return file;
        }
}
