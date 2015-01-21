package com.example.launcher;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

public class GLView extends GLSurfaceView {

	public final static String TAG="GLView";
	public static final String UPDATE_RECEIVER = "com.example.glassTest.UPDATE_RECEIVER";
	private VelocityTracker mVelocityTracker=null;
	private Context context;
	private GLRenderer renderer;
	private float firstX=0.f;
	private float LastX=0.f;
	private float diffX=0.f;
	private boolean IsScroll=false;
	private GestureDetector mGestureDetector;
	
	public GLView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		mGestureDetector = createGestureDetector(context);
		renderer = new GLRenderer(context, this);
		// receiver=new ApplicationsIntentReceiver(renderer);
		this.requestFocus();
		this.setFocusableInTouchMode(true);
		this.setEGLConfigChooser(8, 8, 8, 8, 14, 0);
		this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		this.setRenderer(renderer);
		this.setRenderMode(RENDERMODE_WHEN_DIRTY);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		DrawThread t=renderer.getDrawThread();
		if(t !=null && t.isAlive())
			t.interrupt(); 
		super.onPause();
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	public void animationFor(String command)
	{
		final String cmd=command;
		queueEvent(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				renderer.goToVoiceIcon(cmd);
			}
		});
	}
	private GestureDetector createGestureDetector(Context context) {
	    GestureDetector gestureDetector = new GestureDetector(context);
	        //Create a base listener for generic gestures
	        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {				
				@Override
				public boolean onGesture(Gesture gesture) {
					// TODO Auto-generated method stub
					if(gesture == Gesture.TAP)
					{
						//renderer.performClick();
				//		animationFor("Google");
					}
					return false;
				}				
			});
	        
	        return gestureDetector;
	}
	
	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(mVelocityTracker==null)
			mVelocityTracker=VelocityTracker.obtain();
		mVelocityTracker.addMovement(event);
		
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			LastX = (int)event.getX();
			diffX=LastX;
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d(TAG, "--------Move----------");
			diffX=(int)(event.getX() - LastX);
			LastX+=diffX;
			Log.d(TAG, "move diff x: "+ diffX + " lastx :"+LastX);
			queueEvent(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ApplicationInfo.IsScrolling=true;
					renderer.Move(floorby2(diffX * Constants.SCALE));
				}
			});
			GLView.this.requestRender();
			break;
			
		case MotionEvent.ACTION_UP:
			mVelocityTracker.computeCurrentVelocity(1000);
			final int v=(int)mVelocityTracker.getXVelocity();
			queueEvent(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ApplicationInfo.IsScrolling=false;
					if(checkIfSwipe(v) == false)
					{           // not swipe -> check where to back
						scrollMove();
					}
				}
			});
			mVelocityTracker.recycle();
			mVelocityTracker=null;
			break;
		}
		return mGestureDetector.onMotionEvent(event);
	}
	private void scrollMove() {
		// TODO Auto-generated method stub
		renderer.setWhereToGo();	
	}

	private boolean checkIfSwipe(int v)
	{
		if(v > Constants.SNAP_VELOCITY )
		{
			Log.d(TAG, "swipe forward");
			swipeMove(Constants.TO_FRONT);
			return true;
		}
		else if(v < -Constants.SNAP_VELOCITY )
		{
			Log.d(TAG, "swipe backward");
			swipeMove(Constants.TO_BACK);
			return true;
		}
		return false;
	}

	private void swipeMove(int destination) {
		// TODO Auto-generated method stub
		renderer.goTo(destination);
	}

	public float floorby2(float f)
	{
		return ((float)Math.floor(f*100))/100;
	}
	
	public GLRenderer getRender()
	{
		return renderer;
	}
}
