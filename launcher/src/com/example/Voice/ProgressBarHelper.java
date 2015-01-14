package com.example.Voice;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.GestureDetector;

public class ProgressBarHelper {
	
	private long start=0;
	private long end=0;
	private ProgressBar pb;
	private volatile Thread progressBarThread;
	private int CurrentPosition=0;
	private AudioManager audio;
	private int size;
	
	public ProgressBarHelper(ProgressBar pb,int size) {
		// TODO Auto-generated constructor stub
		this.pb=pb;
		this.size=size;
		this.pb.setVisibility(ProgressBar.GONE);
		start=System.currentTimeMillis();
	}
	public void startProgressBarThread()
	{
		pb.setVisibility(ProgressBar.VISIBLE);
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
		audio.playSoundEffect(Sounds.SUCCESS);
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
			private int size;
			public void init()
			{
				size=0;
			}
			public void handleMessage(android.os.Message msg) {
				size+=msg.what;
				
			}
		};
	};
}
