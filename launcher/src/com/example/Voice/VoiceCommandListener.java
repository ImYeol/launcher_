package com.example.Voice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.Voice.VoiceListenerService.VoiceListenerBinder;

public class VoiceCommandListener implements VoiceListener {

	private transient VoiceActivity mVoiceActivity;
//	private VoiceListenerService mService; // 연결 타입 서비스
	private transient boolean mBound = false; // 서비스 연결 여부
	private VoiceCommand commands;

	
	public static final Parcelable.Creator<VoiceCommandListener> CREATOR
    = new Parcelable.Creator<VoiceCommandListener>() {
         public VoiceCommandListener createFromParcel(Parcel in){
              return new VoiceCommandListener(in);
         }

         @Override
         public VoiceCommandListener[] newArray(int size) {
              return new VoiceCommandListener[size];
         }
    };
    public VoiceCommandListener(Parcel in) {
		// TODO Auto-generated constructor stub
		readFromParcel(in);
	}
    
	public VoiceCommandListener(VoiceActivity voiceActtivity) {
		// TODO Auto-generated constructor stub
		this.mVoiceActivity = voiceActtivity;
		
	}
	private void readFromParcel(Parcel in) {
		// TODO Auto-generated method stub
		ClassLoader loader= VoiceCommand.class.getClassLoader();
		in.readParcelable(loader);
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

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeParcelable(commands,0);
	}
}
