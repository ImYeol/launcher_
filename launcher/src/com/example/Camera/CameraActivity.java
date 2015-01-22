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

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.ShutterCallback;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.Voice.VoiceActivity;
import com.example.launcher.R;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

public class CameraActivity extends VoiceActivity {

	// private SpeechBroadcastReceiver receiver;
	private Intent i;
	private static final String tag = "CameraActivity";
	private CameraSurfaceView cameraView;
	private Camera mCamera;
	private GestureDetector mGestureDetector;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private TakePictureCallback mPictureCallback;
	private View guideView;
	private FrameLayout preview;
	private TextView catch_label;
	private TextView finish_label;
	private int shutterSound;
	private SoundPool soundPool;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);
		catch_label=(TextView)findViewById(R.id.camera_catch_label);
		finish_label=(TextView)findViewById(R.id.camera_finish_label);
		mGestureDetector = createGestureDetector(this);
		cameraView = new CameraSurfaceView(this);
		preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(cameraView);
		((FrameLayout)findViewById(R.id.camera_overlayview)).bringToFront();
		CommandList=new String[]{"catch","finish"};
		mPictureCallback=new TakePictureCallback(this);
	       soundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);
       	shutterSound = soundPool.load(this, R.raw.camera_click, 0);
		
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	//	setCommands();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
			Log.d(tag, "error open camera");
			e.printStackTrace();
		}
		return c; // returns null if camera is unavailable
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (mCamera == null) {
			mCamera = getCameraInstance();
		}
		mCamera.startPreview();
		cameraView.setCamera(mCamera);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		super.onPause();
	}

	@Override
	public void onVoiceCommand(String command) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onVoiceCommand(int cmdId) {
		// TODO Auto-generated method stub
		final int id = cmdId;
		if(id== 0) // catch
		{

			UnBindService();
			soundPool.play(shutterSound, 1f, 1f, 0, 0, 1);
			mCamera.takePicture(null, null, mPictureCallback);
		}
		else if(id == 1) // finish
		{

			UnBindService();
			setResult(RESULT_OK);
			finish();
		}
	}
/*	private final ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
        	SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);
        	int shutterSound = soundPool.load(this, R.raw.camera_click, 0);
        	soundPool.play(shutterSound, 1f, 1f, 0, 0, 1);
        }
    };*/
	private GestureDetector createGestureDetector(Context context) {
		GestureDetector gestureDetector = new GestureDetector(context);
		// Create a base listener for generic gestures
		gestureDetector.setBaseListener(new GestureDetector.BaseListener() {

			@Override
			public boolean onGesture(Gesture gesture) {
				// TODO Auto-generated method stub
				if (gesture == Gesture.TAP) {
					Log.d(tag, "tap!!!!!!!!!");
		        	soundPool.play(shutterSound, 1f, 1f, 0, 0, 1);
					mCamera.takePicture(null, null, mPictureCallback);

				}
				return false;
			}

		});
		return gestureDetector;
	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return mGestureDetector.onMotionEvent(event);
	}
}
