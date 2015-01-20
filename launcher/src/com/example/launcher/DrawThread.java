package com.example.launcher;

import android.util.Log;


public class DrawThread extends Thread {
	private boolean IsRun;
	private GLView glview;
	private static final String TAG="DrawThread";
	
	public DrawThread(GLView glView)
	{
		IsRun=true;
		this.glview=glView;
	}
	public void setFlag(boolean IsRun)
	{
		this.IsRun=IsRun;
	}
	public boolean getFlag()
	{
		return IsRun;
	}
	public void run() {
		while (IsRun) {
			try {
				Log.d(TAG, "requestRender");
				glview.requestRender();
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
