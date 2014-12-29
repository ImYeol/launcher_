package com.example.glasstest;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
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
	public static boolean completed=false;
	
	private List<ApplicationInfo> mApplications;
	private static int[] texIDS=new int[MAX_MODEL_NUM];
	private static boolean[] ListcheckID=new boolean[MAX_MODEL_NUM]; 
	private int CurIndex=0;
	private int NextIndex=0;
	private int PreIndex=0;
	private float offset=0;
	private float remainedDistance;
	private int direction;
	private int state;
	public static boolean IsReached=false;
	
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
	
	public GLRenderer(Context context,GLView glview)
	{
		this.context=context;	
		this.glview=glview;
		init_Values();
		init_buffer();
		loadApplications(false); 
		init_CurEntity();
	}
	private void init_CurEntity()
	{
		int texID=getID();
		mApplications.get(CurIndex).setCurrent(true);
		mApplications.get(CurIndex).setReady(texID);
		ListcheckID[texID]=false;
	}
	private void init_Values()
	{
		for(int i=0;i<MAX_MODEL_NUM;i++	)
		{
			ListcheckID[i]=false;
			texIDS[i]=0;
		}
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
	public float getGap(int CurIndex)
	{
		return mApplications.get(CurIndex).getZ()- ApplicationInfo.displayedPlace;
	}
	public int getAppSize()
	{
		return mApplications.size();
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
	        }
	        Log.d("service", "ser mAppl:"+mApplications);
	}
	public float getCurZ(int cur)
	{
		return mApplications.get(cur).getZ();
	}
	
	public void performClick()
	{
		if (CurIndex == -1) {
			Log.d("performClick", "error curIndex is -1");
			return ;
		}
		
		Intent intent = mApplications.get(ApplicationInfo.CurIndex).intent;
		context.startActivity(intent);
	}
	public void performDoubleClick()
	{
		Intent i=new Intent(context,VoiceActivity.class);
		context.startActivity(i);
	}
	public void setOffset(float offset)
	{
		this.offset=offset;
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
		if(glview.isFirst)
		{
			glview.init_moving();
		}
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
			if(ListcheckID[index]==false)
			{
				return index;
			}
		}
		return NO_TEXTURE_AVAILABLE;
	}

	private void DrawObjects(GL10 gl) 
	{
		Log.d(TAG, "drawObjects - pre:"+ApplicationInfo.PreIndex+" next:"+ ApplicationInfo.NextIndex+ " Cur:"+ApplicationInfo.CurIndex);
			if(glview.isFirst)
				mApplications.get(0).draw(gl);
			else if(ApplicationInfo.getState() == GLView.Swipe)
			{
				if(ApplicationInfo.getDirection() == GLView.BackwardSwipe)
				{	
					mApplications.get(ApplicationInfo.NextIndex).draw(gl);
					mApplications.get(ApplicationInfo.CurIndex).draw(gl);
				}
				else if(ApplicationInfo.getDirection() == GLView.ForwardSwipe)
				{
					mApplications.get(ApplicationInfo.PreIndex).draw(gl);
					mApplications.get(ApplicationInfo.CurIndex).draw(gl);
				}
			}
			else if(ApplicationInfo.getState() == GLView.Scrolling)
			{
				if(ApplicationInfo.getDirection() == GLView.BackwardSwipe)
				{	
					mApplications.get(ApplicationInfo.CurIndex).draw(gl);
					mApplications.get(ApplicationInfo.PreIndex).draw(gl);
				}
				else if(ApplicationInfo.getDirection() == GLView.ForwardSwipe)
				{
					mApplications.get(ApplicationInfo.CurIndex).draw(gl);
					mApplications.get(ApplicationInfo.NextIndex).draw(gl);
				}
			}
		
	}
	private void setMovingOffset()
	{
//		float distance = ApplicationInfo.getDistance();
		float z= mApplications.get(ApplicationInfo.CurIndex).getZ();
		
		float offset=0;
		
		if(ApplicationInfo.getDirection() == GLView.BackwardSwipe)
		{
			offset=z - ApplicationInfo.displayedPlace;
			if(offset >= ApplicationInfo.speed)
				ApplicationInfo.setOffset(-ApplicationInfo.speed);
			else if(offset < ApplicationInfo.speed && offset > 0)
				ApplicationInfo.setOffset(-offset);
			else if(offset <= 0)
			{
				ApplicationInfo.setOffset(0.f);
				IsReached=true;
			}
		}
		else if(ApplicationInfo.getDirection() == GLView.ForwardSwipe)
		{
			offset=ApplicationInfo.displayedPlace - z;
			if(offset >= ApplicationInfo.speed)
				ApplicationInfo.setOffset(ApplicationInfo.speed);
			else if(offset < ApplicationInfo.speed && offset > 0)
				ApplicationInfo.setOffset(offset);
			else if(offset <= 0)
			{
				ApplicationInfo.setOffset(0.f);
				IsReached=true;
			}
		}
		Log.d(TAG, "cur:"+ApplicationInfo.CurIndex+" z:"+z);
		Log.d(TAG, "distance :"+offset);
	//	ApplicationInfo.setDistance(distance);
	}
	public static void unSetTexID(int TexID)
	{
		ListcheckID[TexID]=false; 
	}
	public void readyToMove(int tmpDirection)
	{
		int AppSize=mApplications.size();
		if(AppSize == 1)
		{
			return ;
		}
		else if( AppSize ==2)
		{
			ApplicationInfo next;
			if(CurIndex ==0)
				next=mApplications.get(1);
			else
				next=mApplications.get(0);
			next.setZ(ApplicationInfo.originPlace);
			if(next.IsReadyToMove() == false)
			{
				int texID=getID();
				next.setReady(texID);
				ListcheckID[texID]=true;
			}
		}
		else
		{
			NextIndex = (ApplicationInfo.CurIndex + 1) >= mApplications.size() ? 0 : (ApplicationInfo.CurIndex + 1);
			PreIndex = (ApplicationInfo.CurIndex - 1) < 0 ? (mApplications.size() - 1) : (ApplicationInfo.CurIndex - 1);
			ApplicationInfo preApp = mApplications.get(PreIndex);
			ApplicationInfo nextApp = mApplications.get(NextIndex);
			float tmpZ=mApplications.get(ApplicationInfo.CurIndex).getZ();
			if(tmpDirection == GLView.BackwardSwipe)
			{
				preApp.setZ(ApplicationInfo.disapearedPlace +( tmpZ - ApplicationInfo.displayedPlace));
				Log.d(TAG, "readyToMove setZ backswipe:"+ApplicationInfo.disapearedPlace +( tmpZ - ApplicationInfo.displayedPlace));
				if (preApp.IsReadyToMove() == false) {
					int texID = getID();
					preApp.setReady(texID);
					ListcheckID[texID] = true;
				}
			}
			else if(tmpDirection == GLView.ForwardSwipe)
			{
				nextApp.setZ(ApplicationInfo.originPlace +( tmpZ - ApplicationInfo.displayedPlace));
				Log.d(TAG, "readyToMove setZ backswipe:"+ApplicationInfo.originPlace +( tmpZ - ApplicationInfo.displayedPlace));
				if (nextApp.IsReadyToMove() == false) {
					int texID = getID();
					nextApp.setReady(texID);
					ListcheckID[texID] = true;
				}
			}

		}
		Log.d(TAG, "readyToMove - pre:"+PreIndex+" next:"+ NextIndex+ " Cur:"+CurIndex);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub

		Log.d(TAG, "onDrawFrame"	);
		ApplicationInfo.setOffset(offset);
		ApplicationInfo.setDirection(direction);
//		ApplicationInfo.setDistance(remainedDistance);
		ApplicationInfo.setState(state);
		ApplicationInfo.setIndexs(PreIndex,CurIndex,NextIndex);
		if (ApplicationInfo.getState() == GLView.Swipe) {
			if (IsReached== false) {
//				Log.d(TAG, "thread if");
				setMovingOffset();
			} else {
				if (glview.isFirst) {
					glview.isFirst = false;
				}
//				Log.d(TAG, "thread else");
				IsReached=false;
				glview.mDrawThread.IsRun = false;
				ApplicationInfo.setOffset(0.0f);
		//		ApplicationInfo.setState(GLView.NoState);
		//		return ;
			}
		}
			setGL(gl);
//			Log.d(TAG, "after setGL");
			DrawObjects(gl);
//			Log.d(TAG, "after DrawObjects");
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
	public void setDrawApps(int pre,int cur, int next) {
		// TODO Auto-generated method stub
		mApplications.get(cur).setCurrent(false);
		
		this.CurIndex=cur;
		if(pre != -1)
			this.PreIndex=pre;
		if(next != -1)
			this.NextIndex=next;
		
		mApplications.get(cur).setCurrent(true);
		Log.d(TAG, "setDrawApps- pre:"+PreIndex+" next"+NextIndex+" Cur:"+CurIndex);
		
	}
	public void setDistance(int direction) {
		// TODO Auto-generated method stub
		this.direction=direction;
	}
	public void setState(int state) {
		// TODO Auto-generated method stub
		this.state=state;
	}
}
