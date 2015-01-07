package com.example.Voice;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.example.Voice.VoiceListenerService.VoiceListenerBinder;

public class VoiceCommandListener implements VoiceListener {

	private VoiceActivity mVoiceActivity;
	private VoiceListenerService mService; // 연결 타입 서비스
	private boolean mBound = false; // 서비스 연결 여부
	private VoiceCommand commands;

	public VoiceCommandListener(VoiceActivity voiceActtivity) {
		// TODO Auto-generated constructor stub
		this.mVoiceActivity = voiceActtivity;
		
	}

	@Override
	public void onVoiceCommand(String command) {
		// TODO Auto-generated method stub
		mVoiceActivity.onVoiceCommand(command);
	}

	public boolean BindService() {
		Intent intent = new Intent(mVoiceActivity, VoiceListenerService.class);
		return mVoiceActivity.bindService(intent, mConnection,
				Context.BIND_AUTO_CREATE);
	}

	public void unBindService() {
		if (mBound) {
			mVoiceActivity.unbindService(mConnection);
			mService.ReSetCommands();
			mBound = false;
		}
	}

	public boolean setCommands(VoiceCommand commands)
	{
		this.commands=commands;
		if(mService == null)
			return false;
		mService.setCommands(commands);
		return true;
	}
	
	public void onBeginSpeech()
	{
		mVoiceActivity.onBeginSpeech();
	}
	public void onEndOfSpeech()
	{
		mVoiceActivity.onEndOfSpeech();
	}
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			VoiceListenerBinder binder = (VoiceListenerBinder) service;
			mService = binder.getService();
			mBound = true;
			
			mService.registerCallback(VoiceCommandListener.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {

			mBound = false;
		}

	};
}
