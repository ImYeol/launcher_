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
	public final static int NoSwipe=0;
	public final static int BackwardSwipe=1;
	public final static int ForwardSwipe=2;
	
	public final static int Scrolling=1000;
	public final static int Swipe=1001;
	public final static int NoState=1002;
	
	public static DrawThread mDrawThread;
	public static final String UPDATE_RECEIVER = "com.example.glassTest.UPDATE_RECEIVER";
	private final static float THRESHOLD=10.f;
	
	
	private final static float STD_VELOCITY=20;
	private static final int SNAP_VELOCITY=2000;
	
	
	private VelocityTracker mVelocityTracker=null;
	private Context context;
	private GLRenderer renderer;
	private float LastX=0.f;
	private float DownX=0.f;
	private boolean IsScroll=false;
	private GestureDetector mGestureDetector;
	private int CurIndex=0;
	private int NextIndex=0;
	private int PreIndex=0;
	private int direction=0;
	public boolean isFirst=true;
	private int preDirection=0;
	
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
	//	init_moving();
	}
	public void init_moving()
	{
		mDrawThread=new DrawThread(context);
		isFirst=true;
		renderer.setState(GLView.Swipe);
		renderer.setDistance(GLView.ForwardSwipe);
		PreIndex=renderer.getAppSize()-1;
		NextIndex=renderer.getAppSize() > 2? 1 : 0;
		mDrawThread.start();  // set pause , resume
		
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		if(mDrawThread !=null && mDrawThread.isAlive())
			mDrawThread.interrupt();
		super.onPause();
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		if(mDrawThread == null || mDrawThread.isAlive()==false)
		{
			mDrawThread=new DrawThread(context);
			mDrawThread.start();
			
		}
		super.onResume();
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
						renderer.performClick();
					}
					else if(gesture == Gesture.LONG_PRESS)
					{
						renderer.performDoubleClick();
					}
					return false;
				}				
			});
	        
	        return gestureDetector;
	}
	
	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(isFirst)
			return false;
		if(mVelocityTracker==null)
			mVelocityTracker=VelocityTracker.obtain();
		mVelocityTracker.addMovement(event);
		
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			if(mDrawThread.isAlive() && mDrawThread.IsRun)
			{
				renderer.setState(GLView.NoState);
				mDrawThread.IsRun=false;
			}
			LastX = event.getX();
			DownX=LastX;
//			if(renderer.getCurZ(CurIndex) == ApplicationInfo.displayedPlace)
//				renderer.readyToMove();
			break;
		case MotionEvent.ACTION_MOVE:
			int x=(int)(event.getX() - LastX);
			renderer.setState(GLView.Scrolling);
			int tmpDirection= event.getX() - DownX >0? GLView.ForwardSwipe : GLView.BackwardSwipe;
			if(preDirection != tmpDirection)
			{
				renderer.readyToMove(tmpDirection);
				preDirection=tmpDirection;
				renderer.setDistance(tmpDirection);
			}
			
			if( x >= 100 || x <= -100)
			{
				if(x >=100)
					renderer.setOffset(ApplicationInfo.speed);
				else
					renderer.setOffset(-ApplicationInfo.speed);
			}
			else
				renderer.setOffset((int)(x * ApplicationInfo.SCALE));
			Log.d(TAG, "scroll :"+x+"cur :"+ CurIndex);
			MainActivity.glview.requestRender();
			LastX=event.getX();
			
			break;
		case MotionEvent.ACTION_UP:
			preDirection=0;
			LastX=event.getX();
			mVelocityTracker.computeCurrentVelocity(1000);
			int v=(int)mVelocityTracker.getXVelocity(); 
	//		Log.d("glView", "v : "+v);
	//		NextIndex=CurIndex;
			IsScroll=true;
			direction =0;
			direction=(LastX - DownX) >=0 ? GLView.ForwardSwipe : GLView.BackwardSwipe;
			float gap=renderer.getGap(CurIndex);
	//		Log.d("glView", "gap: "+gap);
			if(v > SNAP_VELOCITY || gap >= ApplicationInfo.threshold )
			{
				Log.d(TAG, "swipe forward");
				direction=GLView.ForwardSwipe;
				CurIndex=NextIndex;
				NextIndex= CurIndex +1 >=renderer.getAppSize() ? 0: CurIndex+1;
				PreIndex= CurIndex-1 < 0 ? renderer.getAppSize()-1 : CurIndex-1;
				IsScroll=false;
				renderer.setDrawApps(PreIndex, CurIndex, NextIndex);
				Log.d(TAG, "cur: "+CurIndex+" pre :"+ PreIndex);
			}
			else if(v < -SNAP_VELOCITY || gap <= -ApplicationInfo.threshold )
			{
				Log.d(TAG, "swipe backward");
				direction=GLView.BackwardSwipe;
				CurIndex=PreIndex;
				NextIndex= CurIndex +1 >=renderer.getAppSize() ? 0: CurIndex+1;
				PreIndex= CurIndex-1 < 0 ? renderer.getAppSize()-1 : CurIndex-1;
				IsScroll=false;
				Log.d(TAG, "cur: "+CurIndex+" next :"+NextIndex);
				renderer.setDrawApps(PreIndex, CurIndex, NextIndex);
			}
			
			float remainedDistance=0;
			if(IsScroll ==false)   // swipe
			{
				remainedDistance=DistanceToBoundary(direction);
			}
			else   // back to displayted place
			{
				Log.d(TAG, "direction back");
				direction = gap >=0 ? GLView.BackwardSwipe : GLView.ForwardSwipe;
				remainedDistance=Math.abs(gap);
			}
			renderer.setState(GLView.Swipe);
			renderer.setDistance(direction);
			Log.d(TAG, "remainedDistance: "+remainedDistance);
			mDrawThread.IsRun=true;
			
			mVelocityTracker.recycle();
			mVelocityTracker=null;

			break;
		}
		return mGestureDetector.onMotionEvent(event);
		//return true;
	}

	private float DistanceToBoundary(int direction)
	{
		float z= renderer.getCurZ(CurIndex);
		Log.d(TAG, "distanceToBoundary- z:"+z+" cur:"+CurIndex);
		if(direction == GLView.BackwardSwipe)
		{
			return (z - ApplicationInfo.displayedPlace);
		}
		else if(direction == GLView.ForwardSwipe)
		{
			return (ApplicationInfo.displayedPlace - z);
		}
		return 0.f;
	}
	private float floorby2(float f)
	{
		return ((float)Math.floor(f*100))/100;
	}
	public class DrawThread extends Thread{
		public boolean IsRun;
		public DrawThread(Context contexs)
		{
			IsRun=true;
		}

		public void run()
		{
			while (true) {
				if(IsRun)
				{
					try {
							MainActivity.glview.requestRender();
							Thread.sleep(30);
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
