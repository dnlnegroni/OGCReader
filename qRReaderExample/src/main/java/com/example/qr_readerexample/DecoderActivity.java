package com.example.qr_readerexample;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.qr_readerexample.QRCodeReaderView.OnQRCodeReadListener;
import com.example.qr_readerexample.com.example.qr_readerexample.model.QREntity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static com.example.qr_readerexample.R.drawable.android;


public class DecoderActivity extends Activity implements OnQRCodeReadListener {

    private TextView myTextView;
	private QRCodeReaderView mydecoderview;
	private ImageView line_image;
	private ToggleButton button;
	private final Context context = this;

	/**
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		Camera camera = mydecoderview.getCameraManager().getCamera();
		super.onStop();
		mydecoderview.hideRepresentation();
		if (camera != null) {
			camera.stopPreview();
		}
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_decoder);
		//parse tests

//		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//				android);
//		ByteArrayOutputStream stream = new ByteArrayOutputStream();
//		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//		byte[] image = stream.toByteArray();
//		ParseFile file = new ParseFile("android.png", image);
//		ParseObject testObject = new ParseObject("TestUploadFoto");
//		testObject.put("ImageName", "Android Logo");
//		testObject.put("ImageFile", file);
//		testObject.saveInBackground();
		//end of parse tests

		button = (ToggleButton) findViewById(R.id.togglebutton);
		final PackageManager pm = context.getPackageManager();
        mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);
        mydecoderview.setWillNotDraw(false);
        myTextView = (TextView) findViewById(R.id.exampleTextView);
        
        line_image = (ImageView) findViewById(R.id.red_line_image);
        
        TranslateAnimation mAnimation = new TranslateAnimation(
			TranslateAnimation.ABSOLUTE, 0f,
			TranslateAnimation.ABSOLUTE, 0f,
			TranslateAnimation.RELATIVE_TO_PARENT, 0f,
			TranslateAnimation.RELATIVE_TO_PARENT, 0.5f);
	   	mAnimation.setDuration(1000);
	   	mAnimation.setRepeatCount(-1);
	   	mAnimation.setRepeatMode(Animation.REVERSE);
	   	mAnimation.setInterpolator(new LinearInterpolator());
	   	line_image.setAnimation(mAnimation);
        
    }

    
    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed
	private String lastQRURL;
	@Override
	public void onQRCodeRead(String text, PointF[] points) {

		if(lastQRURL == null || !lastQRURL.equals(text)) {
			lastQRURL = text;
			ParseQuery<QREntity> query = ParseQuery.getQuery(QREntity.class);
			query.whereEqualTo("QRURL", text);
			final String stext = text;
			query.findInBackground(new FindCallback<QREntity>() {
				@Override
				public void done(List<QREntity> results, ParseException e) {
					if (e == null) {
						if (results.size() > 0) {
							for (QREntity qr : results) {
								mydecoderview.setQREntity(qr);
								Log.d("old QR", " QR id associated :" + qr.getObjectId());
							}
						} else {
							QREntity entity = new QREntity();
							entity.setQRURL(stext);
							Bitmap bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
							Canvas canvas = new Canvas(bitmap);
							canvas.drawColor(Color.argb(255, 255, 255, 255));
							entity.setRepresentation(bitmap);
							mydecoderview.setQREntity(entity);
							Log.d("new QR", " new string associated :" + stext);
						}
					} else {
						Log.d("error QR", "error :" + e.getMessage());
						mydecoderview.setQREntity(null);
					}
				}
			});
		}
		mydecoderview.invalidate();

	}

	
	// Called when your device have no camera
	@Override
	public void cameraNotFound() {
		
	}

	// Called when there's no QR codes in the camera preview image
	@Override
	public void QRCodeNotFoundOnCamImage() {
		
	}
    
	@Override
	protected void onResume() {
		super.onResume();
		Camera camera = mydecoderview.getCameraManager().getCamera();
		if(camera!=null) {
			mydecoderview.getCameraManager().startPreview();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mydecoderview.getCameraManager().stopPreview();
	}

	public void onToggleClicked(View view) {
		Camera camera = mydecoderview.getCameraManager().getCamera();
		PackageManager pm = context.getPackageManager();
		final Camera.Parameters p = camera.getParameters();
		boolean on = ((ToggleButton) view).isChecked();
		if (on) {
			Log.i("info", "torch is turn on!");
			p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			camera.setParameters(p);
		} else {
			Log.i("info", "torch is turn off!");
			p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			camera.setParameters(p);
		}
	}

}
