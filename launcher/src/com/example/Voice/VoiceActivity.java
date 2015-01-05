package com.example.Voice;

import java.util.ArrayList;

import android.app.Activity;
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

public class VoiceActivity extends Activity{

	private final static String TAG="VoiceAcitivity";
	protected VoiceCommandListener mVoiceCommandListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SetOnVoiceCommandListener();
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
	
	protected void SetOnVoiceCommandListener() {
		mVoiceCommandListener=new VoiceCommandListener(this);
	}
	
	public void onVoiceCommand(String command)
	{
		Log.d(TAG, "Sub Class should implement onVoiceCommand");
	}
	public void setCommands()
	{
		Log.d(TAG, "Sub Class should implement setCommands");
	}
}
