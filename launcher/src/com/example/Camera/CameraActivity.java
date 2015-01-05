/*
 * Copyright (C) 2013 Justin Driggers
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

package com.example.Camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.example.Voice.SpeechCommandList;
import com.example.Voice.VoiceActivity;
import com.google.android.glass.content.Intents;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

public class CameraActivity extends VoiceActivity {
	
//	private SpeechBroadcastReceiver receiver;
	private Intent i;
	private static final String tag="NewActivity";
	private static final int TAKE_PICTURE_REQUEST = 1;
	private CameraSurfaceView cameraView;
	private Camera mCamera; 
	private GestureDetector mGestureDetector;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;
	public static final int MEDIA_TYPE_IMAGE=1;
	public static final int MEDIA_TYPE_VIDEO=2;
	private BroadcastReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);
		mGestureDetector = createGestureDetector(this);
		mCamera=getCameraInstance();
		cameraView=new CameraSurfaceView(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(cameraView);
	}
	
	private class SpeechBroadcastReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String str=intent.getExtras().getString("Speech");
			Log.d("Main broad", "broadcast:"+str);
			if(str.equals("finish"))
			{
				Intent i=new Intent();
				((CameraActivity)context).setResult(RESULT_OK);
				((CameraActivity)context).finish();
			}
		}		
	}

	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    	Log.d(tag, "error open camera");
	    }
	    return c; // returns null if camera is unavailable
	}
	
	private PictureCallback mPicture = new PictureCallback() {

	    @Override
	    public void onPictureTaken(byte[] data, Camera camera) {
	    	Log.d(tag, "onPictureTaken");
	        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
	        if (pictureFile == null){
	            Log.d(tag, "Error creating media file, check storage permissions: ");
	            return;
	        }

	        try {
	            FileOutputStream fos = new FileOutputStream(pictureFile);
	            fos.write(data);
	            fos.close();
	        } catch (FileNotFoundException e) {
	            Log.d(tag, "File not found: " + e.getMessage());
	        } catch (IOException e) {
	            Log.d(tag, "Error accessing file: " + e.getMessage());
	        }
	        Log.d(tag, "iiiiiiiiiiiii!!!!!!!!!");
	        Uri uri=Uri.fromFile(pictureFile);
	        Intent i=new Intent(CameraActivity.this,ImageViewer.class);
	        i.putExtra("imgUri",uri.toString());
	        startActivity(i);
	    }	
	};
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(mCamera==null)
		{
			mCamera=getCameraInstance();
		}
		
		mCamera.startPreview();
		cameraView.setCamera(mCamera);
		
		if(receiver == null)
		{
			IntentFilter filter=new IntentFilter(SpeechCommandList.FILTER);
			receiver=new SpeechBroadcastReceiver();
			registerReceiver(receiver, filter);
		}
		super.onResume();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if(mCamera !=null)
		{
			//mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera=null;
		}
		if( receiver != null)
		{
			unregisterReceiver(receiver);
			receiver=null;
		}
		super.onPause();
	}
	
	private GestureDetector createGestureDetector(Context context) {
	    GestureDetector gestureDetector = new GestureDetector(context);
	        //Create a base listener for generic gestures
	        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
				
				@Override
				public boolean onGesture(Gesture gesture) {
					// TODO Auto-generated method stub
					if(gesture == Gesture.TAP)
					{
						Log.d(tag, "tap!!!!!!!!!");
						mCamera.takePicture(null, null, mPicture);
						
					}
					return false;
				}
					
			});
	        return gestureDetector;
	}
	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Log.d(tag, "generic Event");
		return mGestureDetector.onMotionEvent(event);
	}
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
}
