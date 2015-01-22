package com.example.launcher;

import android.util.Log;


public class DrawThread extends Thread {
	private GLView glview;
	private static final String TAG="DrawThread";
	private int RenderingCount;
	
	public DrawThread(GLView glView)
	{
		this.glview=glView;
		this.RenderingCount=0;
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
	
	public void setRenderingCount(int destination,float z,float speed,int distance)
	{
		switch(destination)
		{
		case Constants.TO_BACK:
			this.RenderingCount=distance * caculateCount(Constants.originPlace,z,speed) + 1;
			break;
		case Constants.TO_FORWARD_CENTER:
			this.RenderingCount=distance * caculateCount(Constants.displayedPlace,z,speed) + 1;
			break; 
		case Constants.TO_BACKWARD_CENTER:
			this.RenderingCount=distance * caculateCount(Constants.displayedPlace,z,speed) + 1;
			break;
		case Constants.TO_FRONT:
			this.RenderingCount=distance * caculateCount(Constants.disapearedPlace,z,speed) + 1;
			break;
		}
	}
	private int caculateCount(float destination, float z, float speed) {
		// TODO Auto-generated method stub
		float diff= Math.abs(destination - z);
		int quotient =(int) Math.abs(diff / speed);
		int option= diff % speed > 0.f ? 1 : 0;
		Log.d(TAG, "DrawThread - diff:"+diff+" quotient:"+quotient+" option:"+option+ " sum:"+ (quotient+option) );
		return quotient+option;
	}
}
