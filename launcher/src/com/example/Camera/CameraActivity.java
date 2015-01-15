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

import java.util.Arrays;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.Voice.VoiceActivity;
import com.example.Voice.VoiceCommand;
import com.example.launcher.R;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;

public class CameraActivity extends VoiceActivity {

	// private SpeechBroadcastReceiver receiver;
	private Intent i;
	private static final String tag = "CameraActivity";
	private CameraSurfaceView cameraView;
	private Camera mCamera;
	private GestureDetector mGestureDetector;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private BroadcastReceiver receiver;

	private List<String> CommandList=Arrays.asList("shot","finish");
	private VoiceCommand voiceCommand;
	private TakePictureCallback mPictureCallback;
	private View guideView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);
		mGestureDetector = createGestureDetector(this);
	//	mCamera = getCameraInstance();
		cameraView = new CameraSurfaceView(this);
		guideView = new CardBuilder(this, CardBuilder.Layout.MENU)
	    .setFootnote("say shot")
	    .getView();
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(cameraView);
		preview.addView(guideView);
		guideView.bringToFront();
		mPictureCallback=new TakePictureCallback(this);
		voiceCommand=new VoiceCommand(CommandList);
		setCommands(voiceCommand);
		Log.d(tag, "onCreate");
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d(tag, "onStart");
	//	setCommands();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d(tag, "onStop");
	}
/*	@Override
	public void setCommands() {
		// TODO Auto-generated method stub
		voiceCommand=new VoiceCommand(CommandList);
		mVoiceCommandListener.setCommands(voiceCommand);
	} */
	
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
		Log.d(tag, "onPause");
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
		if(command.equals("shot"))
		{
			Log.d(tag, "onVoiceCommand: shot");
			StopListening();
			mCamera.takePicture(null, null, mPictureCallback);
		}
		else if(command.equals("finish"))
		{
			StopListening();
			setResult(RESULT_OK);
			finish();
		}
	}

	private GestureDetector createGestureDetector(Context context) {
		GestureDetector gestureDetector = new GestureDetector(context);
		// Create a base listener for generic gestures
		gestureDetector.setBaseListener(new GestureDetector.BaseListener() {

			@Override
			public boolean onGesture(Gesture gesture) {
				// TODO Auto-generated method stub
				if (gesture == Gesture.TAP) {
					Log.d(tag, "tap!!!!!!!!!");
					StopListening();
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
		Log.d(tag, "generic Event");
		return mGestureDetector.onMotionEvent(event);
	}

}
