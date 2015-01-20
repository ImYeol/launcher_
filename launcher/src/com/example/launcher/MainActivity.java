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

import java.util.ArrayList;

import com.example.Camera.CameraActivity;
import com.example.Voice.VoiceActivity;
import com.example.Voice.VoiceCommandListActivity;
import com.example.Voice.VoiceListenerService;
import com.example.util.IntentBuilder;
import com.example.util.WarningDialog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

public class MainActivity extends Activity {
	
	public static GLView glview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		glview=new GLView(this);
		setContentView(glview);
	//	CommandList=new String[]{"camera","google","command"};
		//startService(new Intent(this,VoiceListenerService.class));
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//stopService(new Intent(this,VoiceListenerService.class));
	}
/*	
	@Override
	public void onVoiceCommand(String command) {
		final String cmd=command;
	}
	@Override
	public void onVoiceCommand(int cmdId) {
		// TODO Auto-generated method stub
		final int id = cmdId;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(id == 0) // camera
				{
					UnBindService();
					Intent intent=IntentBuilder.CreateIntent(MainActivity.this, CameraActivity.class).build();
					IntentBuilder.startActivity(MainActivity.this, intent);
				}
				else if(id == 1)  // google
				{
					UnBindService();
					Intent intent=new Intent("com.google.glass.action.START_VOICE_SEARCH_ACTIVITY");
					startActivity(intent);
				}
				else if(id == 2) // voice
				{
					UnBindService();
					Intent intent=new Intent(MainActivity.this,VoiceCommandListActivity.class);
					startActivity(intent);
				}
			}
		});
	}*/
}
