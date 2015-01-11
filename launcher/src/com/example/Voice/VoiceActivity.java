package com.example.Voice;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.BadTokenException;
import android.widget.ProgressBar;

import com.example.Voice.VoiceActivity.IncomingHandler;
import com.example.Voice.VoiceActivity.SpeechRecognitionListener;
import com.example.launcher.R;
import com.google.android.glass.media.Sounds;

public class VoiceActivity extends Activity{

	private final static String TAG="VoiceAcitivity";
	protected SpeechRecognitionListener mVoiceCommandListener;
	ProgressDialog voiceRecoginitionStateDialog;
	protected SpeechRecognizer mSpeechRecognizer;
	protected Intent mSpeechRecognizerIntent;
	protected final Messenger mServerMessenger = new Messenger(
			new IncomingHandler(this));
	protected boolean mIsListening;
	protected volatile boolean mIsCountDownOn;
	static final String tag = "VoiceActivity";

	static final int MSG_RECOGNIZER_START_LISTENING = 1;
	static final int MSG_RECOGNIZER_CANCEL = 2;
	private VoiceCommand commands;
	private AudioManager audio;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
		mVoiceCommandListener=new SpeechRecognitionListener();
		mSpeechRecognizer
				.setRecognitionListener(mVoiceCommandListener);
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
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	//	mVoiceCommandListener.BindService();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mSpeechRecognizer != null && mSpeechRecognizer.isRecognitionAvailable(this))
			mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	/*	if (mSpeechRecognizer != null) {
			mSpeechRecognizer.destroy();
			mSpeechRecognizer = null;
		}*/
	
	}
	public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
                dialog.show();
        } catch (BadTokenException e) {

		}
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.voice_recognition_state_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		// dialog.setMessage(Message);
		return dialog;
	}

	public void onVoiceCommand(String command)
	{
		Log.d(TAG, "Sub Class should implement onVoiceCommand");
	}

	public void setCommands(VoiceCommand commands) {
		this.commands = commands;
	}

	protected static class IncomingHandler extends Handler {
		private WeakReference<VoiceActivity> mtarget;

		IncomingHandler(VoiceActivity target) {
			mtarget = new WeakReference<VoiceActivity>(target);
		}

		@Override
		public void handleMessage(Message msg) {
			final VoiceActivity target = mtarget.get();

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
	//		Log.d(tag, "start msg");
		} catch (RemoteException e) {
			Log.d(tag, "On error send msg error");
		}
	}
	protected void StopListening() {
		Message message = Message.obtain(null, MSG_RECOGNIZER_CANCEL);
		try {
			mServerMessenger.send(message);
		} catch (RemoteException e) {
			Log.d(tag, "On error send msg error");
		}
		mIsCountDownOn=false;
		mNoSpeechCountDown.cancel();
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
			Log.d(tag, "onBeginingOfSpeech"); //$NON-NLS-1$
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
			Log.d(tag, "onBufferReceived");
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
				Log.d(tag, "mIsCountDownOn "+mIsCountDownOn);
			}
			StartListening();
		}

		@Override
		public void onEvent(int eventType, Bundle params) {
			Log.d(tag, "onEvent");
		}

		@Override
		public void onPartialResults(Bundle partialResults) {
			ArrayList<String> data = partialResults
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			String str = data.toString();
			str = str.subSequence(1, str.length() - 1).toString();
		//	Log.d(tag, "par result " + str);
			if(commands == null)
			{
				onVoiceCommand(str);
			}
			else if (commands !=null && commands.contains(str)) {
				Log.d(tag, "par result " + str);
				onVoiceCommand(str);
			//	ReStartListening();
				StopListening();
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
			Log.d(tag, "result " + str + "commands: "+commands);
			if(commands !=null)
			{
				Log.d(tag, "command: "+commands.commands.get(0));
			}
			if(commands == null)
			{
				onVoiceCommand(str);
				ReStartListening();
			}
			else if(commands !=null)
			{
				if(commands.contains(str))
				{
					onVoiceCommand(str);
					StopListening();
				}
				else
					ReStartListening();
			//	ReStartListening();
				
			}

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
