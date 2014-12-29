package com.example.glasstest;

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

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.GestureDetector;

public class VoiceActivity extends Activity{
	private static final String TAG = "SREC Test";
	private static final int SPEECH_REQUEST = 0;
	private GestureDetector mGestureDetector;
	private SpeechRecognizer mRecognizer;
	private Intent i;
	private TextView tv;
	private long start=0;
	private long end=0;
	private ProgressBar pb;
	private volatile Thread progressBarThread;
	private int CurrentPosition=0;
	private AudioManager audio;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voice_layout);
		tv=(TextView)findViewById(R.id.textView);
		pb=(ProgressBar)findViewById(R.id.progressBar);
		pb.setVisibility(ProgressBar.GONE);
		audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // �����ν�
																	// intent����
		i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName()); // ������
																				// ����
		i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US"); // �����ν� ��� ����
		i.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
		i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,3);	
		mRecognizer = SpeechRecognizer.createSpeechRecognizer(this); // �����ν� ��ü
		mRecognizer.setRecognitionListener(listener); // �����ν� ������ ���
		start=System.currentTimeMillis();
		Log.d("main", "start!!!");
		Intent i;
		
//		mRecognizer.startListening(i);
	}
	public void startProgressBarThread()
	{
		pb.setVisibility(ProgressBar.VISIBLE);
		audio.playSoundEffect(Sounds.SUCCESS);
		if(progressBarThread == null)
		{
			progressBarThread=new Thread(null,backgroundThread);
			CurrentPosition=0;
			progressBarThread.start();
		}
	}
	public void stopProgressBarThread()
	{
		if(progressBarThread != null)
		{
			Thread tmpThread= progressBarThread;
			progressBarThread=null;
			tmpThread.interrupt();
		}
		pb.setVisibility(ProgressBar.GONE);
	}
	private Runnable backgroundThread = new Runnable(){
		public void run() {
			if(Thread.currentThread() == progressBarThread)
			{
				CurrentPosition=0;
				final int total =100;
				while(CurrentPosition < total)
				{
					try {
						progressBarHandle.sendMessage(progressBarHandle.obtainMessage());
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		Handler progressBarHandle=new Handler()
		{
			public void handleMessage(android.os.Message msg) {
				CurrentPosition++;
				pb.setProgress(CurrentPosition);
				if(CurrentPosition == 100)
				{
					CurrentPosition=0;
				}
			}
		};
	};
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		// glview.registerReceiver(receiver);
		if (mRecognizer != null && mRecognizer.isRecognitionAvailable(this))
			mRecognizer.startListening(i);
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// unregisterReceiver(receiver);
		if (mRecognizer != null) {
			mRecognizer.destroy();
			mRecognizer = null;
		}
		super.onDestroy();
	}

	private RecognitionListener listener = new RecognitionListener() {
		// �Է� �Ҹ� ���� ��
		@Override
		public void onRmsChanged(float rmsdB) {
		}

		// ���� �ν� ��� ����
		@Override
		public void onResults(Bundle results) {
			ArrayList<String> data = results
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			String tmpString=data.toString().substring(1, data.toString().length()-1);
			if(tmpString.equals("take a picture"))
			{
				stopProgressBarThread();
				Intent i=new Intent(VoiceActivity.this,NewActivity.class);
				Log.d("hhh", "intent to camera");
				startActivity(i);
			}
			else
			{
				mRecognizer.startListening(i);
			}
			Log.d("test", "result " + data.toString());
			
		}

		@Override
		public void onReadyForSpeech(Bundle params) {
		}

		// ���� �Է��� ��������
		@Override
		public void onEndOfSpeech() {
			audio.playSoundEffect(Sounds.SUCCESS);
			startProgressBarThread();

		}

		// ������ �߻��ϸ�
		@Override
		public void onError(int error) {
			Log.d("test", "on error:" + error);
			if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
				mRecognizer.startListening(i);
			}
		}

		@Override
		public void onBeginningOfSpeech() {
		} // �Է��� ���۵Ǹ�

		@Override
		public void onPartialResults(Bundle partialResults) {
			ArrayList<String> data = partialResults
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			if(data != null)
			{
				String tmpString=data.toString().substring(1, data.toString().length()-1);
				tv.setText(tmpString);
				Log.d("test", "par result " + data.toString());
			}
		} // �ν� ����� �Ϻΰ� ��ȿ�� ��

		// �̷��� �̺�Ʈ�� �߰��ϱ� ���� �̸� ����Ǿ��� �Լ�
		@Override
		public void onEvent(int eventType, Bundle params) {
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
		} // �� ���� �Ҹ��� ���� ��
	};


}
