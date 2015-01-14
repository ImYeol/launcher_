package com.example.Camera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder mHolder;
	private Camera mCamera;
	private final static String TAG="cameraSurfaceView";
	
	public CameraSurfaceView(Context context) {
		super(context);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	public void releaseCamera()
	{
		mCamera.stopPreview();
       mCamera.release();
       mCamera = null;
	}
	public void setCamera(Camera camera)
	{
		mCamera=camera;
		Camera.Parameters parameters=mCamera.getParameters();
		parameters.setPreviewFpsRange(30000, 30000);
		parameters.setPreviewSize(640, 360);
		mCamera.setParameters(parameters);
	}
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the
		// preview.
		try {
			mCamera.setPreviewDisplay(holder);
		//	mCamera.startPreview();
		} catch (IOException e) {
			Log.d(TAG, "Error setting camera preview: " + e.getMessage());
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
		/* if (mCamera != null) {
	        	Log.d(TAG,"Stopping preview in SurfaceDestroyed()."); //
	    		mCamera.setPreviewCallback(null);					  //
	            mCamera.stopPreview();
	            mCamera.release();									  //
	        }
*/
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}
		if (mCamera != null) {
			// stop preview before making changes
			try {
				mCamera.stopPreview();
			} catch (Exception e) {
				// ignore: tried to stop a non-existent preview
			}

			// set preview size and make any resize, rotate or
			// reformatting changes here

			// start preview with new settings
			try {
				Camera.Parameters parameters = mCamera.getParameters();
	    		parameters.setPreviewFpsRange(30000, 30000);
	    		parameters.setPreviewSize(640,360);//mPreviewSize.width, mPreviewSize.height);
	    		mCamera.setParameters(parameters);
				//mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();

			} catch (Exception e) {
				Log.d(TAG, "Error starting camera preview: " + e.getMessage());
			}
		}
	}
	
}
