package com.example.qr_readerexample;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.open.CameraManager;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;

/*
 * Copyright 2014 David Lázaro Esparcia.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



/**
 * QRCodeReaderView - Class which uses ZXING lib and let you easily integrate a QR decoder view.
 * Take some classes and made some modifications in the original ZXING - Barcode Scanner project.  
 *
 * @author David L�zaro
 */
public class QRCodeReaderView extends SurfaceView implements Callback,Camera.PreviewCallback {

	public interface OnQRCodeReadListener {
		
		public void onQRCodeRead(String text, PointF[] points);
		public void cameraNotFound();
		public void QRCodeNotFoundOnCamImage();
	}
	
	private OnQRCodeReadListener mOnQRCodeReadListener;
	
	private static final String TAG = QRCodeReaderView.class.getName();
	
	private QRCodeReader mQRCodeReader;
    private int mPreviewWidth; 
    private int mPreviewHeight; 
    private SurfaceHolder mHolder;
    private CameraManager mCameraManager;
	private PointF one = null;
	private PointF two = null;
	private PointF three = null;
	private PointF four = null;

    
	public QRCodeReaderView(Context context) {
		super(context);
		init();
	}
	
	public QRCodeReaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public void setOnQRCodeReadListener(OnQRCodeReadListener onQRCodeReadListener) {
		mOnQRCodeReadListener = onQRCodeReadListener;
	}
	
	public CameraManager getCameraManager() {
		return mCameraManager;
	}

	@SuppressWarnings("deprecation")
	private void init() {
		if (checkCameraHardware(getContext())){
			mCameraManager = new CameraManager(getContext());

			mHolder = this.getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  // Need to set this flag despite it's deprecated
		} else {
			Log.e(TAG, "Error: Camera not found");
			if (mOnQRCodeReadListener != null) {
				mOnQRCodeReadListener.cameraNotFound();
			}
		}
	}
	

	
	/****************************************************
	 * SurfaceHolder.Callback,Camera.PreviewCallback
	 ****************************************************/
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			// Indicate camera, our View dimensions
			mCameraManager.openDriver(holder,this.getWidth(),this.getHeight());
		} catch (IOException e) {
			Log.w(TAG, "Can not openDriver: "+e.getMessage());
			mCameraManager.closeDriver();
		}

		try {
			mQRCodeReader = new QRCodeReader();
			mCameraManager.startPreview();
		} catch (Exception e) {
			Log.e(TAG, "Exception: " + e.getMessage());
			mCameraManager.closeDriver();
		}
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "surfaceDestroyed");
		mCameraManager.getCamera().setPreviewCallback(null);
		mCameraManager.getCamera().stopPreview();
		mCameraManager.getCamera().release();
		mCameraManager.closeDriver();
	}
	
	// Called when camera take a frame 
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {

		PlanarYUVLuminanceSource source = mCameraManager.buildLuminanceSource(data, mPreviewWidth, mPreviewHeight);

		HybridBinarizer hybBin = new HybridBinarizer(source);
		BinaryBitmap bitmap = new BinaryBitmap(hybBin);

		try {
			Result result = mQRCodeReader.decode(bitmap);
			// Notify we found a QRCode
			if (mOnQRCodeReadListener != null) {
				// Transform resultPoints to View coordinates
				PointF[] transformedPoints = transformToViewCoordinates(result.getResultPoints());
				one = transformedPoints[0];
				two = transformedPoints[1];
				three = transformedPoints[2];
				four = transformedPoints[3];
				requestLayout();
				invalidate();
				Log.d("Pointone", one.toString());
				Log.d("Pointtwo", two.toString());
				Log.d("Pointthree" , three.toString());
				Log.d("Pointfour" , four.toString());


					mOnQRCodeReadListener.onQRCodeRead(result.getText(), transformedPoints);


			} else {
				Log.d("Puttana", "null");
			}
			
		} catch (ChecksumException e) {
			Log.d(TAG, "ChecksumException");
			e.printStackTrace();
		} catch (NotFoundException e) {
			// Notify QR not found
			if (mOnQRCodeReadListener != null) {
				mOnQRCodeReadListener.QRCodeNotFoundOnCamImage();
			}
		} catch (FormatException e) {
			Log.d(TAG, "FormatException");
			e.printStackTrace();
		} finally {
			mQRCodeReader.reset();
		}
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.d("titolo", "qualcosa");
		/*Paint p1 = new Paint();
		p1.setColor(Color.RED);
		Paint p2 = new Paint();
		p2.setColor(Color.GREEN);
		Paint p3 = new Paint();
		p3.setColor(Color.BLUE);
		Paint p4 = new Paint();
		p4.setColor(Color.YELLOW);*/
		Paint p = new Paint();
		if(one != null && two != null  && three != null  && four != null ) {
			/*canvas.drawLine(one.x, one.y, two.x, two.y, p1);
			canvas.drawLine(two.x, two.y, three.x, three.y, p2);
			canvas.drawLine(three.x, three.y, three.x, one.y, p3);
			canvas.drawLine(three.x , one.y, one.x, one.y, p4);*/

			Paint wallpaint = new Paint();
			wallpaint.setColor(Color.GRAY);
			wallpaint.setStyle(Paint.Style.FILL);

			Path wallpath = new Path();
			wallpath.reset(); // only needed when reusing this path for a new build
			wallpath.moveTo(one.x, one.y); // used for first point
			wallpath.lineTo(two.x, two.y);
			wallpath.lineTo(three.x, three.y);
			wallpath.lineTo(four.x+0.1f*(four.x-two.x), four.y+0.1f*(four.y-two.y));
			wallpath.lineTo(one.x, one.y); // there is a setLastPoint action but i found it not to work as expected

			canvas.drawPath(wallpath, wallpaint);

			invalidate();
		}
	}


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.d(TAG, "surfaceChanged");

		if (mHolder.getSurface() == null){
			Log.e(TAG, "Error: preview surface does not exist");
			return;
		}

		//preview_width = width;
		//preview_height = height;

		mPreviewWidth = mCameraManager.getPreviewSize().x;
		mPreviewHeight = mCameraManager.getPreviewSize().y;

		
		mCameraManager.stopPreview();
		mCameraManager.getCamera().setPreviewCallback(this);
		mCameraManager.getCamera().setDisplayOrientation(90); // Portrait mode

		mCameraManager.startPreview();
	}
	
	/**
	 * Transform result to surfaceView coordinates
	 * 
	 * This method is needed because coordinates are given in landscape camera coordinates.
	 * Now is working but transform operations aren't very explained
	 * 
	 * TODO re-write this method explaining each single value    
	 * 
	 * @return a new PointF array with transformed points
	 */
	private PointF[] transformToViewCoordinates(ResultPoint[] resultPoints) {
		
		PointF[] transformedPoints = new PointF[resultPoints.length];
		int index = 0;
		if (resultPoints != null) {
			float previewX = mCameraManager.getPreviewSize().x;
			float previewY = mCameraManager.getPreviewSize().y;
			float scaleX = this.getWidth() / previewY;
			float scaleY = this.getHeight() / previewX;

			for (ResultPoint point :resultPoints){
				PointF tmppoint = new PointF((previewY- point.getY())*scaleX, point.getX()*scaleY);
				transformedPoints[index] = tmppoint;
				index++;
			}
		}
		return transformedPoints;
		
	}
	
	
	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
			// this device has a camera
			return true;
		} 
		else if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)){
			// this device has a front camera
			return true;
		}
		else if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
			// this device has any camera
			return true;
		}
		else {
			// no camera on this device
			return false;
		}
	}
	
}
