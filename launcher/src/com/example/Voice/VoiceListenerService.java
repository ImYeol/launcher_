package com.example.Voice;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.google.android.glass.media.Sounds;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.effect.Effect;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;

import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.SoundEffectConstants;

public class VoiceListenerService extends Service {

	protected AudioManager mAudioManager;
	protected SpeechRecognizer mSpeechRecognizer;
	protected Intent mSpeechRecognizerIntent;
	protected final Messenger mServerMessenger = new Messenger(
			new IncomingHandler(this));
	protected boolean mIsListening;
	protected volatile boolean mIsCountDownOn;
	static final String tag = "VoiceListenerService";

	static final int MSG_RECOGNIZER_START_LISTENING = 1;
	static final int MSG_RECOGNIZER_CANCEL = 2;

	private final IBinder mBinder = new VoiceListenerBinder();
	private IVoiceListenerCallback mCallback;

	private VoiceCommand commands;
	private AudioManager audio;
	private boolean IsCommandRecognized=false;
	private boolean NotCommand=false;
	private String preString="";
	private int preStringLen=0;

	public class VoiceListenerBinder extends IVoiceListenerService.Stub {

		@Override
		public void setCommands(VoiceCommand cmd) throws RemoteException {
			// TODO Auto-generated method stub
			VoiceListenerService.this.setCommands(cmd);
		}

		@Override
		public void ReSetCommands() throws RemoteException {
			// TODO Auto-generated method stub
			VoiceListenerService.this.ReSetCommands();
		}

		@Override
		public void registerCallback(IVoiceListenerCallback callback)
				throws RemoteException {
			// TODO Auto-generated method stub
			VoiceListenerService.this.registerCallback(callback);
		}

		@Override
		public void unRegisterCallback() throws RemoteException {
			// TODO Auto-generated method stub
			VoiceListenerService.this.unRegisterCallback();
		}
		@Override
		public void turnOffVoiceRecognize()
		{
			Log.d(tag, "turn off recognize");
			VoiceListenerService.this.turnOffVoiceRecognize();
		}
		@Override
		public void turnOnVoiceRecognize()
		{
			Log.d(tag, "turn on recognize");
			VoiceListenerService.this.turnOnVoiceRecognize();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(tag, "onBind");
		if(mSpeechRecognizer != null && mIsListening==false)
			StartListening();
		return mBinder;
	}
	public void turnOffVoiceRecognize()
	{
			if (mIsCountDownOn) {
				mIsCountDownOn = false;
				mNoSpeechCountDown.cancel();
			}
			StopListening();
			Log.d(tag, "turnOff - stopListening");
	}
	public void turnOnVoiceRecognize()
	{
		if(!mIsListening)
		{
			StartListening();
		}
	}
	public void registerCallback(IVoiceListenerCallback callback) {
		this.mCallback = callback;
	}
	public void unRegisterCallback()
	{
		this.mCallback = null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
		mSpeechRecognizer
				.setRecognitionListener(new SpeechRecognitionListener());
		mSpeechRecognizerIntent = new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
				"en-US");
		mSpeechRecognizerIntent.putExtra(
				RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
		mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
		mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
		mIsListening=true;
		audio=(AudioManager)getSystemService(AUDIO_SERVICE);
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	public void setCommands(VoiceCommand commands) {
		this.commands = commands;
	}

	protected static class IncomingHandler extends Handler {
		private WeakReference<VoiceListenerService> mtarget;

		IncomingHandler(VoiceListenerService target) {
			mtarget = new WeakReference<VoiceListenerService>(target);
		}

		@Override
		public void handleMessage(Message msg) {
			final VoiceListenerService target = mtarget.get();

			switch (msg.what) {
			case MSG_RECOGNIZER_START_LISTENING:

				if (!target.mIsListening) {
					target.mSpeechRecognizer
							.startListening(target.mSpeechRecognizerIntent);
					target.mIsListening = true;
			//		Log.d(tag, "message start listening"); //$NON-NLS-1$
				}
				break;

			case MSG_RECOGNIZER_CANCEL:
				target.mSpeechRecognizer.cancel();
				target.mIsListening = false;
				//Log.d(TAG, "message canceled recognizer"); //$NON-NLS-1$
				break;
			}
		}
	}

	protected void ReStartListening() {

		Message message = Message.obtain(null, MSG_RECOGNIZER_CANCEL);
		try {
			mServerMessenger.send(message);
			message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
			mServerMessenger.send(message);
		} catch (RemoteException e) {
			Log.d(tag, "onFinish exception" + e.getMessage());
		}

	}

	protected void StartListening() {
		mIsCountDownOn = false;
		Message message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
		try {
			mServerMessenger.send(message);
		} catch (RemoteException e) {
			Log.d(tag, "On error send msg error");
		}
	}
	protected void StopListening() {
		if(mIsListening)
		{
			Message message = Message.obtain(null, MSG_RECOGNIZER_CANCEL);
			try {
				mServerMessenger.send(message);
			} catch (RemoteException e) {
				Log.d(tag, "stopListening failed" + e.getMessage());
			}
		}
		
	}
	// Count down timer for Jelly Bean work around
	protected CountDownTimer mNoSpeechCountDown = new CountDownTimer(5000, 5000) {
		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFinish() {
			mIsCountDownOn = false;
			ReStartListening();
		}
	};

	@Override
	public void onDestroy() {
		Log.d(tag, "onDestroy");
		super.onDestroy();

		if (mIsCountDownOn) {
			mNoSpeechCountDown.cancel();
		}
		if (mSpeechRecognizer != null) {
			mSpeechRecognizer.destroy();
		}
	}

	protected class SpeechRecognitionListener implements RecognitionListener {
		
		@Override
		public void onBeginningOfSpeech() {
			// speech input will be processed, so there is no need for count
			// down anymore
			if (mIsCountDownOn) {
				mIsCountDownOn = false;
				mNoSpeechCountDown.cancel();
			}
			audio.playSoundEffect(Sounds.SUCCESS);
			IsCommandRecognized=false;
			NotCommand=false;
			preStringLen=0;
			try {
				mCallback.onBeginSpeech();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(tag, "onBeginingOfSpeech"); //$NON-NLS-1$
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
			Log.d(tag, "onBufferReceived");
		}

		@Override
		public void onEndOfSpeech() {
			Log.d(tag, "onEndOfSpeech"); //$NON-NLS-1$
			try {
				mCallback.onEndOfSpeech();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onError(int error) {
			Log.d(tag, "On error :" + error);
			if (mIsCountDownOn) {
				mIsCountDownOn = false;
				mNoSpeechCountDown.cancel();
			}
			ReStartListening();
		}

		@Override
		public void onEvent(int eventType, Bundle params) {
			Log.d(tag, "onEvent");
		}

		@Override
		public void onPartialResults(Bundle partialResults) {
			if(IsCommandRecognized || NotCommand)
				return ;
			ArrayList<String> data = partialResults
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			String str = data.toString();
			str = str.subSequence(1, str.length() - 1).toString();
			if(str.length() > 8 && commands != null)
				NotCommand=true;
			int cmdId=-1;
			if (commands == null) {
				if (str != null && preStringLen != str.length()) {
					preStringLen=str.length();
					onVoiceCommand(str);
				}
			} else if (commands != null && (cmdId = commands.contains(str)) != -1) {
				Log.d(tag, "par result :" + str);
				IsCommandRecognized = true;
				onVoiceCommand_int(cmdId);
				ReStartListening();
			}

		}

		@Override
		public void onReadyForSpeech(Bundle params) {
			mIsCountDownOn = true;
			mNoSpeechCountDown.start();
			Log.d(tag, "onReadyForSpeech"); //$NON-NLS-1$
		}

		@Override
		public void onResults(Bundle results) {
			//Log.d(TAG, "onResults"); //$NON-NLS-1$
			if(IsCommandRecognized || NotCommand)
			{
				ReStartListening();
				return ;
			}
			ArrayList<String> data = results
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			String str = data.toString();
			str = str.subSequence(1, str.length() - 1).toString();
			int cmdId=-1;
			if (commands == null) {
				if (preStringLen != str.length())
					onVoiceCommand(str);
				try {
					mCallback.onResultOfSpeech();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else if(commands !=null)
			{
				Log.d(tag, "command:"+str);
				if((cmdId=commands.contains(str)) != -1)
					onVoiceCommand_int(cmdId);
				else
					onVoiceCommand(str);
			}
			ReStartListening();

		}

		@Override
		public void onRmsChanged(float rmsdB) {
		}
		
		private void onVoiceCommand(String str)
		{
			try {
				mCallback.onVoiceCommand(str);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		private void onVoiceCommand_int(int cmdId)
		{
			try {
				mCallback.onVoiceCommand_int(cmdId);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void ReSetCommands() {
		// TODO Auto-generated method stub
		commands=null;
	}
}
