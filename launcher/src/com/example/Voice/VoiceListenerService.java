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
	private VoiceListener mCallback;

	private VoiceCommand commands;
	private AudioManager audio;
	
	public class VoiceListenerBinder extends Binder {
		VoiceListenerService getService() {
			return VoiceListenerService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	public void registerCallback(VoiceListener callback) {
		this.mCallback = callback;
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
		mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
		mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
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
					//Log.d(TAG, "message start listening"); //$NON-NLS-1$
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
		super.onDestroy();

		if (mIsCountDownOn) {
			mNoSpeechCountDown.cancel();
		}
		if (mSpeechRecognizer != null) {
			mSpeechRecognizer.destroy();
		}
	}

	protected class SpeechRecognitionListener implements RecognitionListener {
		
		ProgressDialog voiceRecoginitionDialog;
		@Override
		public void onBeginningOfSpeech() {
			// speech input will be processed, so there is no need for count
			// down anymore
			if (mIsCountDownOn) {
				mIsCountDownOn = false;
				mNoSpeechCountDown.cancel();
			}
			voiceRecoginitionDialog=ProgressDialog.show(getApplicationContext(), null, null);
			audio.playSoundEffect(Sounds.SUCCESS);
			
			Log.d(tag, "onBeginingOfSpeech"); //$NON-NLS-1$
		}

		@Override
		public void onBufferReceived(byte[] buffer) {

		}

		@Override
		public void onEndOfSpeech() {
			Log.d(tag, "onEndOfSpeech"); //$NON-NLS-1$
		}

		@Override
		public void onError(int error) {
			Log.d(tag, "On error :" + error);
			if (mIsCountDownOn) {
				mIsCountDownOn = false;
				mNoSpeechCountDown.cancel();
			}
			StartListening();
		}

		@Override
		public void onEvent(int eventType, Bundle params) {

		}

		@Override
		public void onPartialResults(Bundle partialResults) {
			ArrayList<String> data = partialResults
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			String str = data.toString();
			str = str.subSequence(1, str.length() - 1).toString();
			Log.d(tag, "result " + str);

			if (commands !=null && commands.contains(str)) {
				mCallback.onVoiceCommand(str);
				ReStartListening();
				if(voiceRecoginitionDialog.isShowing())
					voiceRecoginitionDialog.dismiss();
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
			ArrayList<String> data = results
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			String str = data.toString();
			str = str.subSequence(1, str.length() - 1).toString();
			Log.d(tag, "result " + str);
			if (commands !=null && commands.contains(str)) {
				mCallback.onVoiceCommand(str);
			}
			ReStartListening();
			if(voiceRecoginitionDialog.isShowing())
				voiceRecoginitionDialog.dismiss();
		}

		@Override
		public void onRmsChanged(float rmsdB) {
		}

	}

	public void ReSetCommands() {
		// TODO Auto-generated method stub
		commands=null;
	}
}
