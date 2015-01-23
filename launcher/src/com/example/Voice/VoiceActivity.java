package com.example.Voice;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.BadTokenException;
import android.widget.ProgressBar;

import com.example.launcher.R;

public abstract class VoiceActivity extends Activity{

	private final static String TAG="VoiceAcitivity";
	protected VoiceCommandListener mVoiceCommandListener;
	ProgressDialog voiceRecoginitionStateDialog;
	protected VoiceCommand voiceCommand;
	protected String[] CommandList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		voiceCommand=null;
		CommandList=null;
		SetOnVoiceCommandListener();
	}
	
	protected VoiceCommand getVoiceCommand(){
		if(voiceCommand ==null)
		{
			this.voiceCommand=new VoiceCommand(CommandList);
		}
		return this.voiceCommand;
	}
	protected void turnOnVoiceRecognize() {
		mVoiceCommandListener.turnOnVoiceRecognize();
	}
	protected void turnOffVoiceRecognize() {
		mVoiceCommandListener.turnOffVoiceRecognize();
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	//	mVoiceCommandListener.BindService();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(mVoiceCommandListener ==null)
			SetOnVoiceCommandListener();
		mVoiceCommandListener.BindService();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		UnBindService();
	}
	protected void UnBindService() {
		if (mVoiceCommandListener != null && mVoiceCommandListener.IsBindToService()) {
			mVoiceCommandListener.unBindService();
			mVoiceCommandListener = null;
		}
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	//	mVoiceCommandListener.unBindService();
	}
	
	protected void SetOnVoiceCommandListener() {
		mVoiceCommandListener=new VoiceCommandListener(this);
	}
	public void onBeginSpeech() {
	}

	public void onEndOfSpeech() {

	}
	public void onResultOfSpeech()
	{
		
	}
	public abstract void onVoiceCommand(String command);
	public abstract void onVoiceCommand(int cmdId);
	public void setCommands()
	{
		mVoiceCommandListener.setCommands(this.voiceCommand);
	}
	public void ReSetCommands()
	{
		mVoiceCommandListener.ReSetCommands();
	}
}
