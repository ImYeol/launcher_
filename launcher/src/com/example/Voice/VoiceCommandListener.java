package com.example.Voice;

import java.util.Arrays;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

import com.example.Voice.VoiceListenerService.VoiceListenerBinder;

public class VoiceCommandListener implements VoiceListener {

	private transient VoiceActivity mVoiceActivity;
//	private VoiceListenerService mService; // 연결 타입 서비스
	private transient boolean mBound = false; // 서비스 연결 여부
	private VoiceCommand commands;
	private IVoiceListenerService mService;
	private IVoiceListenerCallback mCallback=new IVoiceListenerCallback.Stub() {
		
		@Override
		public void onVoiceCommand(String command) throws RemoteException {
			// TODO Auto-generated method stub
			mVoiceActivity.onVoiceCommand(command);
		}
		
		@Override
		public void onVoiceCommand_int(int cmdId) throws RemoteException {
			// TODO Auto-generated method stub
			mVoiceActivity.onVoiceCommand(cmdId);
		}
		@Override
		public void onEndOfSpeech() throws RemoteException {
			// TODO Auto-generated method stub
			mVoiceActivity.onEndOfSpeech();
		}
		
		@Override
		public void onBeginSpeech() throws RemoteException {
			// TODO Auto-generated method stub
			mVoiceActivity.onBeginSpeech();
		}
		
		@Override
		public void onResultOfSpeech() throws RemoteException {
			// TODO Auto-generated method stub
			mVoiceActivity.onResultOfSpeech();
		}
		@Override
		public void onNotCommandError() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
	};
	
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
	public void onVoiceCommand(int cmdId)
	{
		mVoiceActivity.onVoiceCommand(cmdId);
	}
	public boolean IsBindToService()
	{
		return mBound;
	}
	public boolean BindService() {
/*		Intent intent = new Intent(mVoiceActivity, VoiceListenerService.class);
		return mVoiceActivity.bindService(intent, mConnection,
				Context.BIND_AUTO_CREATE);*/
		this.commands=mVoiceActivity.getVoiceCommand();
		Intent intent=new Intent(IVoiceListenerService.class.getName());
		return mVoiceActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	public void unBindService() {
		if (mBound) {
			mVoiceActivity.unbindService(mConnection);
			try {
				mService.ReSetCommands();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mBound = false;
		}
	}
	public void turnOffVoiceRecognize()
	{
		try {
			mService.turnOffVoiceRecognize();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void turnOnVoiceRecognize()
	{
		try {
			mService.turnOnVoiceRecognize();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean setCommands(VoiceCommand commands)
	{
		this.commands=commands;
		if(mService == null)
			return false;
		try {
			mService.setCommands(commands);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	public boolean ReSetCommands()
	{
		if(mService == null)
			return false;
		try {
			mService.ReSetCommands();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		//	VoiceListenerBinder binder = (VoiceListenerBinder) service;
			mService=IVoiceListenerService.Stub.asInterface(service);
		//	mService = binder.getService();
			mBound = true;
			
			try {
				mService.registerCallback(mCallback);
				if(commands !=null)
				{
					mService.setCommands(commands);
				}
				mService.turnOnVoiceRecognize();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				unBindService();
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mService=null;
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
