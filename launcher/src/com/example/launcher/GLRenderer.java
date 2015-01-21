package com.example.launcher;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.Camera.CameraActivity;
import com.example.Voice.VoiceActivity;
import com.example.Voice.VoiceCommandListActivity;
import com.example.Voice.VoiceListenerService;
import com.google.android.glass.media.Sounds;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.nfc.cardemulation.OffHostApduService;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

public class GLRenderer implements GLSurfaceView.Renderer{
	public final static String TAG="GLRenderer";
	
	private Context context;
	private GLView glview;
	private FloatBuffer texBuffer;
	private FloatBuffer vertexBuffer;
	public final static int MAX_MODEL_NUM=10;
	public final static int NO_TEXTURE_AVAILABLE=-1;
	public final static int NO_INDEX=-2;	
	private List<ApplicationInfo> mApplications;
	private static int[] texIDS=new int[MAX_MODEL_NUM];
	private static boolean[] ListcheckID=new boolean[MAX_MODEL_NUM]; 
	private int CurIndex=0;
	private DrawThread thread;
	public static boolean IsReached=false;
	private boolean IsFirst=true;
	private List<DrawThread> ThreadList=new ArrayList<DrawThread>();
	private AudioManager audio;
	
	private float[] texCoords = { // Texture coords for the above face (NEW)
			0.0f, 1.0f, // A. left-bottom (NEW)
			1.0f, 1.0f, // B. right-bottom (NEW)
			0.0f, 0.0f, // C. left-top (NEW)
			1.0f, 0.0f // D. right-top (NEW)
	};
	private float[] vertices = { // Vertices for a face
			-1.0f, -1.0f, 0.0f, // 0. left-bottom-front
			1.0f, -1.0f, 0.0f, // 1. right-bottom-front
			-1.0f, 1.0f, 0.0f, // 2. left-top-front
			1.0f, 1.0f, 0.0f // 3. right-top-front
	};
	private PackageManager manager;
	private int distance=0;
	private Intent VoiceCommandStartIntent=new Intent(Constants.VoiceCommandAction);
	
	public GLRenderer(Context context,GLView glview)
	{
		this.context=context;	
		this.glview=glview;
		init_Values();
		init_buffer();
		loadApplications(false); 
		init_readyToMove();
		audio=(AudioManager)context.getSystemService(context.AUDIO_SERVICE);
	}
	private void init_readyToMove()
	{
		int pre=getPreIndex();
		int next=getNextIndex();
		setTexId(CurIndex);
		mApplications.get(CurIndex).setZ(Constants.displayedPlace);
		setTexId(pre);
		mApplications.get(pre).setZ(Constants.disapearedPlace);
		setTexId(next);
		mApplications.get(next).setZ(Constants.originPlace);
	}
	private void setTexId(int index)
	{
		int texID=getID();
		Log.d(TAG, "setTexId:"+texID);
		mApplications.get(index).setReady(texID);
		ListcheckID[texID]=true;
	}
	private void init_Values()
	{
		for(int i=0;i<MAX_MODEL_NUM;i++	)
		{
			ListcheckID[i]=false;
			texIDS[i]=0;
		}
		Log.d(TAG, "init_values"+ListcheckID);
	}
	private int getPreIndex()
	{
		int preIndex=CurIndex -1;
		if(preIndex < 0)
			return mApplications.size() -1;
		return preIndex;
	}
	private int getNextIndex()
	{
		int nextIndex=CurIndex +1;
		if(nextIndex >= mApplications.size())
			return 0;
		return nextIndex;
	}
	public DrawThread getDrawThread()
	{
		return this.thread;
	}
	private void init_buffer()
	{
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4 * 10);
		vbb.order(ByteOrder.nativeOrder()); // Use native byte order
		vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
		
		for(int i=0;i<MAX_MODEL_NUM;i++)
			vertexBuffer.put(vertices); // Copy data into buffer
		vertexBuffer.position(0); // Rewind
		
		// Setup texture-coords-array buffer, in float. An float has 4 bytes
		// (NEW)
		
		ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4 * 10);
		tbb.order(ByteOrder.nativeOrder());
		texBuffer = tbb.asFloatBuffer();
		
		for(int i=0;i<MAX_MODEL_NUM;i++)
			texBuffer.put(texCoords);
		texBuffer.position(0);
	}
	public void loadApplications(boolean isLaunching)
	{
		 if (isLaunching && mApplications != null) {
	            return;
	        }

	        PackageManager manager = context.getPackageManager();

	        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
	        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

	        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
	        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

	        if (apps != null) {
	            final int count = apps.size();

	            if (mApplications == null) {
	                mApplications = new ArrayList<ApplicationInfo>(count);
	            }
	            mApplications.clear();
	            Log.d("service", "count:" +count);
	            for (int i = 0; i < count; i++) {
	                ApplicationInfo application = new ApplicationInfo();
	                ResolveInfo info = apps.get(i);
	                
	                if (info.activityInfo.applicationInfo.packageName.equals("com.google.glass.home") ||
	                		info.activityInfo.applicationInfo.packageName.equals(context.getPackageName())) {
	                	continue;
	                }

	                application.title = info.loadLabel(manager);
	                application.setActivity(new ComponentName(
	                        info.activityInfo.applicationInfo.packageName,
	                        info.activityInfo.name),
	                        Intent.FLAG_ACTIVITY_NEW_TASK
	                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
	                application.icon = info.activityInfo.loadIcon(manager);

	                mApplications.add(application);
	                application.setIndex(mApplications.size()-1);  
	            }
	            addVoiceApp();
	        }
	}
	
	private void addVoiceApp() {
		// TODO Auto-generated method stub
		ApplicationInfo application = new ApplicationInfo();
		application.title = "Camera";
        application.icon = context.getResources().getDrawable(R.drawable.ic_camera_50);
        Intent intent=new Intent(context,CameraActivity.class);
        application.voiceTag=true;
        application.setIntent(intent);
        mApplications.add(application);
        application.setIndex(mApplications.size()-1); 
        
		application = new ApplicationInfo();
		application.title = "Google";
		intent=new Intent("com.google.glass.action.START_VOICE_SEARCH_ACTIVITY");
		application.setIntent(intent);
		application.voiceTag=true;
		application.icon = context.getResources().getDrawable(R.drawable.ic_search_50);
        mApplications.add(application);
        application.setIndex(mApplications.size()-1); 
	}
	public void performClick()
	{
		if (CurIndex == -1) {
			Log.d("performClick", "error curIndex is -1");
			return ;
		}
		audio.playSoundEffect(Sounds.SELECTED);
		Intent intent = mApplications.get(CurIndex).intent;
		context.startActivity(intent);
	}
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onSurfaceCreated");
//		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Set color's clear-value to
		gl.glClearColor(0.f, 0.f, 0.f, 0.f);
/////new/////		
		 gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
		 gl.glEnable(gl.GL_BLEND);
///////		    
		gl.glClearDepthf(1.0f); // Set depth's clear-value to farthest
		gl.glEnable(GL10.GL_DEPTH_TEST); // Enables depth-buffer for hidden
											// surface removal
		gl.glDepthFunc(GL10.GL_LEQUAL); // The type of depth testing to do
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); // nice
																		// perspective
		// view
		gl.glShadeModel(GL10.GL_SMOOTH); // Enable smooth shading of color
		gl.glDisable(GL10.GL_DITHER); // Disable dithering for better
										// performance
		// You OpenGL|ES initialization code here
		// ......
		gl.glGenTextures(MAX_MODEL_NUM, texIDS, 0);

//		glview.loadTexture(gl);    // Load image into Texture (NEW)
		gl.glEnable(gl.GL_TEXTURE_2D);  // Enable texture (NEW)
		
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onSurfacechanged");
		if (height == 0) height = 1;   // To prevent divide by zero
	      float aspect = (float)width / height;
	   
	      // Set the viewport (display area) to cover the entire window
	      gl.glViewport(0, 0, width, height);
	  
	      // Setup perspective projection, with aspect ratio matches viewport
	      gl.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
	      gl.glLoadIdentity();                 // Reset projection matrix
	      // Use perspective projection
	      GLU.gluPerspective(gl, 45, aspect, 0.1f, 100.f);
	  
	      gl.glMatrixMode(GL10.GL_MODELVIEW);  // Select model-view matrix
	      gl.glLoadIdentity();                 // Reset
	  
	      // You OpenGL|ES display re-sizing code here
	      // ......
	}

	private int getID()
	{
		for(int index=0; index < MAX_MODEL_NUM ; index++)
		{
			Log.d(TAG, "getId "+index+" : "+ListcheckID[index]);
			if(ListcheckID[index]==false)
			{
				return index;
			}
		}
		return NO_TEXTURE_AVAILABLE;
	}

	private void DrawObjects(GL10 gl) 
	{
		if (IsFirst) {
			mApplications.get(CurIndex).draw(gl);
			IsFirst = false;
		} else {
			checkOffset();
			Log.d(TAG, "DrawObjects offset:"+ApplicationInfo.getOffset());
			if (mApplications.get(CurIndex).getZ() > Constants.displayedPlace) {
				Log.d(TAG, "DrawObjects next:"+getNextIndex());
				mApplications.get(getNextIndex()).draw(gl);
				mApplications.get(CurIndex).draw(gl);
			} else if (mApplications.get(CurIndex).getZ() < Constants.displayedPlace) {
				Log.d(TAG, "DrawObjects pre:"+getPreIndex());
				mApplications.get(getPreIndex()).draw(gl);
				mApplications.get(CurIndex).draw(gl);
			}
			else 
			{
				if(ApplicationInfo.getOffset() >=0)
				{
					mApplications.get(getNextIndex()).draw(gl);
					mApplications.get(CurIndex).draw(gl);
				}
				else
				{
					mApplications.get(getPreIndex()).draw(gl);
					mApplications.get(CurIndex).draw(gl);
				}
			}
			if (ApplicationInfo.IsScrolling == false) {
				checkIfArrived();
			}
		}
	}
	private void checkOffset() {
		// TODO Auto-generated method stub
		float CurZ=mApplications.get(CurIndex).getZ();
		float diff=0;
		float offset=ApplicationInfo.getOffset();
		Log.d(TAG, "checkOffset: "+ApplicationInfo.Destination);
		if(ApplicationInfo.IsScrolling)
		{
			if(offset > 0 && CurZ < Constants.displayedPlace 
					&& (diff=glview.floorby2(Constants.displayedPlace - CurZ)) < offset)
			{
				offset=diff;
			}
			else if(offset < 0 && CurZ > Constants.displayedPlace 
					&& (diff=glview.floorby2(CurZ - Constants.displayedPlace)) < offset)
			{
				offset=-diff;
			}
			ApplicationInfo.setOffset(offset);
			return ;
		}
 		switch(ApplicationInfo.Destination)
 		{
 		case Constants.TO_BACK:
 			if((diff=glview.floorby2(CurZ - Constants.originPlace)) < offset)
 				offset=-diff;
 			break;
 		case Constants.TO_BACKWARD_CENTER:
 			if((diff=glview.floorby2(CurZ - Constants.displayedPlace)) < offset)
 				offset=-diff;
 			break;
 		case Constants.TO_FORWARD_CENTER:
 			if((diff=glview.floorby2(Constants.displayedPlace - CurZ)) < offset)
 				offset=diff;
 			break;
 		case Constants.TO_FRONT:
 			if((diff=glview.floorby2(Constants.disapearedPlace - CurZ)) < offset)
 				offset=diff;
 			break;
 		}
 		ApplicationInfo.setOffset(offset);
	}
	private void checkIfArrived()
	{
		ApplicationInfo cur=mApplications.get(CurIndex);
		if(cur.getZ() <= Constants.originPlace || cur.getZ()>= Constants.disapearedPlace)
		{
			changeCurIndex();
			mApplications.get(CurIndex).resetToCenterPlace();
			mApplications.get(getNextIndex()).resetToBackPlace();
			mApplications.get(getPreIndex()).resetToFrontPlace();
			ApplicationInfo.setOffset(0.f);
			ApplicationInfo.Destination=Constants.NO_DESTINATION;
			--distance;
			if(distance > 0)
			{
				Move(Constants.anim_speed);
				ApplicationInfo.Destination = Constants.TO_FRONT;
			}
			else if(ThreadList.size() > 0 && distance <=0)
			{
				ThreadList.remove(0);
				if(mApplications.get(CurIndex).voiceTag)
					broadcastVoiceCommandAction();
			}
			Log.d(TAG, "ifArrived: "+ApplicationInfo.Destination);
		}
	}
	private void broadcastVoiceCommandAction() {
		// TODO Auto-generated method stub
		VoiceCommandStartIntent.putExtra("command", mApplications.get(CurIndex).title);
		context.sendBroadcast(VoiceCommandStartIntent);
	}
	
	private void changeCurIndex() {
		// TODO Auto-generated method stub
		//unSetTexID(mApplications.get(CurIndex).getReady());
		int TexID=-1;
		if(ApplicationInfo.Destination == Constants.TO_FRONT)
		{
			CurIndex=++CurIndex >= mApplications.size()? 0 : CurIndex;
			if((TexID=mApplications.get(getPreIndex()).getReady()) != -1)
				unSetTexID(TexID);
 			mApplications.get(getPreIndex()).setReady(-1);
		}
		else if(ApplicationInfo.Destination == Constants.TO_BACK)
		{
			CurIndex=--CurIndex < 0? mApplications.size()-1 : CurIndex;
			if((TexID=mApplications.get(getNextIndex()).getReady()) != -1)
				unSetTexID(TexID);
			mApplications.get(getNextIndex()).setReady(-1);
		}
		Log.d(TAG, "changeCurIndex :"+CurIndex);
	}
	public static void unSetTexID(int TexID)
	{
		ListcheckID[TexID]=false; 
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub

		Log.d(TAG, "onDrawFrame"	);
			setGL(gl);
			DrawObjects(gl);
			unSetGl(gl);
	}
	private void setGL(GL10 gl)
	{
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity(); // Reset the model-view matrix
		
		gl.glScalef(0.8f, 0.8f, 0.8f);
		gl.glFrontFace(GL10.GL_CCW); // Front face in counter-clockwise
										// orientation
		gl.glEnable(GL10.GL_CULL_FACE); // Enable cull face
		gl.glCullFace(GL10.GL_BACK); // Cull the back face (don't display)

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY); // Enable
																// texture-coords-array
																// (NEW)
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer); // Define
																// texture-coords
																// buffer (NEW)
		gl.glColor4f(0.5f, 0.5f, 0.5f, 1.f);
	}
	private void unSetGl(GL10 gl)
	{
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);  // Disable texture-coords-array (NEW)
	    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	    gl.glDisable(GL10.GL_CULL_FACE);
	}
	public void setWhereToGo() {
		// TODO Auto-generated method stub
		float diffZ=mApplications.get(CurIndex).getZ() - Constants.displayedPlace;
		if( Math.abs(diffZ) < Constants.threshold)
		{
			if(diffZ >=0)
				goTo(Constants.TO_BACKWARD_CENTER);
			else
				goTo(Constants.TO_FORWARD_CENTER);
		}
		else if( diffZ >= 0)  // go to disappeared place
		{
			goTo(Constants.TO_FRONT);
		}
		else if( diffZ < 0)   // go to origin place
		{
			goTo(Constants.TO_BACK);
		}
	}
	public void goTo(int destination) {
		// TODO Auto-generated method stub
		distance=1;
		float speed=0;
		if(destination == Constants.TO_BACKWARD_CENTER || destination == Constants.TO_BACK)
		{
			speed=-Constants.speed;
			Move(speed);
		}
		else
		{
			speed=Constants.speed;
			Move(speed);
		}
		ApplicationInfo.Destination=destination;
		thread=new DrawThread(glview);
		thread.setRenderingCount(destination, mApplications.get(CurIndex).getZ(),speed,distance);
		ThreadList.add(thread);
		thread.start();
	}
	public void goToVoiceIcon(String command)
	{
		this.distance=caculateToVoiceIcon(command);
		Log.d(TAG, "goToVoiceIcon cnt:"+distance);
			Move(Constants.anim_speed);
			ApplicationInfo.Destination = Constants.TO_FRONT;
			thread=new DrawThread(glview);
			thread.setRenderingCount(Constants.TO_FRONT, mApplications.get(CurIndex).getZ(), 
					Constants.anim_speed,distance);
			ThreadList.add(thread);
			thread.start();
	}
	public void Move(float offset) {
		// TODO Auto-generated method stub
		if(mApplications.get(CurIndex).getZ() == Constants.displayedPlace)
		{
			Log.d(TAG, "Move offset:"+offset);
			if(offset > 0)
			{
				setReadyNext();
			}
			else if(offset < 0)
			{
				setReadyPre();
			}
		}
		ApplicationInfo.setOffset(offset);
	}
	private void setReadyPre() {
		// TODO Auto-generated method stub
		int preIndex=getPreIndex();
		Log.d(TAG, "setReadyPre: "+preIndex);
		mApplications.get(preIndex).resetToFrontPlace();
		if(mApplications.get(preIndex).getReady() ==-1)
			setTexId(preIndex);
	}
	private void setReadyNext() {
		// TODO Auto-generated method stub
		int nextIndex=getNextIndex();
		Log.d(TAG, "setReadyNext: "+nextIndex);
		mApplications.get(nextIndex).resetToBackPlace();
		if(mApplications.get(nextIndex).getReady() == -1)
			setTexId(nextIndex);
	}
	private int caculateToVoiceIcon(String command)
	{
		int cur=CurIndex;
		for(int i=0;i<mApplications.size();i++)
		{
			cur=++cur >= mApplications.size() ? 0:cur;
			ApplicationInfo info=mApplications.get(cur);
			if(info.voiceTag && info.title.equals(command))
			{
				return ++i;
			}
		}
		return -1;
	}
}
