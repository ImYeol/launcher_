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

package com.example.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.example.Camera.CameraActivity;
import com.example.Voice.VoiceActivity;
import com.example.Voice.VoiceCommandListActivity;
import com.example.Voice.VoiceListenerService;
import com.example.util.IntentBuilder;

public class MainActivity extends VoiceActivity {
	
	public static GLView glview;
	private final static String TAG="MainActivity";
//	private BroadcastReceiver VoiceCommandAnimFinishReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		glview=new GLView(this);
		setContentView(glview);
		CommandList=new String[]{"cam","can","come","Google","command"};
	//	VoiceCommandAnimFinishReceiver=new VoiceCommandStartReceiver();
		startService(new Intent(this,VoiceListenerService.class));
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "Main onResume");
		glview.resetDoNotReceiveInput();
//		turnOnVoiceRecognize();
//		registerReceiver(VoiceCommandAnimFinishReceiver, new IntentFilter(Constants.VoiceCommandAction));
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		unregisterReceiver(VoiceCommandAnimFinishReceiver);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopService(new Intent(this,VoiceListenerService.class));
	}
	public void turnOffVoiceRecognize()
	{
		mVoiceCommandListener.turnOffVoiceRecognize();
	}
	@Override
	public void onVoiceCommand(String command) {
	}
	@Override
	public void onVoiceCommand(int cmdId) {
		// TODO Auto-generated method stub
		final int id = cmdId;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(id == 0 || id==1 || id ==2) // camera
				{
					glview.animationFor("Camera");
					UnBindService();
				}
				else if(id == 3)  // google
				{
					mVoiceCommandListener.turnOffVoiceRecognize();
					glview.animationFor("Google");
					UnBindService();
				}
				else if(id == 4) // voice
				{
					UnBindService();
					Intent intent=new Intent(MainActivity.this,VoiceCommandListActivity.class);
					startActivity(intent);
				}
			}
		});
	}
/*	public class VoiceCommandStartReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(Constants.VoiceCommandAction))
			{
				Log.d(TAG, "onReceiver");
				String command=intent.getExtras().getString("command");
				if(command.equals("Camera"))
				{
					Log.d(TAG, "receiver Camera");
					Intent localIntent=IntentBuilder.CreateIntent(MainActivity.this, CameraActivity.class).build();
					IntentBuilder.startActivity(MainActivity.this, localIntent);
				}
				else if(command.equals("Google"))
				{
					Intent localIntent=new Intent("com.google.glass.action.START_VOICE_SEARCH_ACTIVITY");
					startActivity(localIntent);
				}
			}
		}
		
	}*/
}
