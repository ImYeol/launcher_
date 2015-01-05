package com.example.Voice;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.Camera.CameraActivity;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.GestureDetector;

public class VoiceCommandListActivity extends VoiceActivity {

	private static final String TAG = "VoiceCommandListActivity";
	private TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voice_layout);
		tv=(TextView)findViewById(R.id.textView);
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mVoiceCommandListener.BindService();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mVoiceCommandListener.unBindService();
	}
	
	public void setCommands()
	{
		VoiceCommand voiceCommand=new VoiceCommand();
		voiceCommand.add("take a picture");
		voiceCommand.add("finish");
		mVoiceCommandListener.setCommands(voiceCommand);
	}
	
	public void onVoiceCommand(String command) {
		
	}

}
