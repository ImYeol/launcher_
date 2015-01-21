package com.example.launcher;

import android.util.Log;


public class DrawThread extends Thread {
	private boolean IsRun;
	private GLView glview;
	private static final String TAG="DrawThread";
	private int RenderingCount;
	
	public DrawThread(GLView glView)
	{
		this.IsRun=true;
		this.glview=glView;
		this.RenderingCount=0;
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
		while (RenderingCount > 0) {
			try {
				Log.d(TAG, "requestRender");
				glview.requestRender();
				RenderingCount--;
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setRenderingCount(int destination,float z,float speed)
	{
		switch(destination)
		{
		case Constants.TO_BACK:
			this.RenderingCount=caculateCount(Constants.originPlace,z,speed);
			break;
		case Constants.TO_FORWARD_CENTER:
			this.RenderingCount=caculateCount(Constants.originPlace,z,speed);
			break;
		case Constants.TO_BACKWARD_CENTER:
			this.RenderingCount=caculateCount(Constants.originPlace,z,speed);
			break;
		case Constants.TO_FRONT:
			this.RenderingCount=caculateCount(Constants.originPlace,z,speed);
			break;
		}
	}
	private int caculateCount(float destination, float z, float speed) {
		// TODO Auto-generated method stub
		float diff= Math.abs(destination - z);
		int quotient =(int) Math.abs(diff / speed);
		int option= diff % speed != 0 ? 1 : 0; 
		return quotient+option;
	}
}
