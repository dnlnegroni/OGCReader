package com.example.qr_readerexample;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Emanuele on 11/06/2015.
 */
public class OgcApp extends Application {

    @Override
    public void onCreate() {
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "60uOLFg0uCx3rwfuvySqnejIR478Y8IjQDb6jf4h", "xVrs6cXx5gpgtjvFRf0f4Eh2poWHLEAEpvb24mq1");
    }
}
