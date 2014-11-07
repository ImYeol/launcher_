package com.example.launcher;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

public class GLRenderer implements GLSurfaceView.Renderer{
	
	private Context context;
	private GLSurfaceView glview;
	private int InvokedSwipe;
	private FloatBuffer texBuffer;
	private FloatBuffer vertexBuffer;
	
	public final static int MAX_MODEL_NUM=10;
	public final static int NO_TEXTURE_AVAILABLE=-1;
	public final static int NO_INDEX=-2;
	
	private List<TextureImg> ListTexture;
	private List<TextureImg> ListOutOfBoundary;
	private int[] texIDS=new int[MAX_MODEL_NUM];
	private boolean[] ListcheckID=new boolean[MAX_MODEL_NUM]; 
	private int CurModelIndex=0;
	private int NextModelIndex=0;
	private int INSTALLED_APK_Num=0;
	
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
	private List<ResolveInfo> apps;
	
	public GLRenderer(Context context,GLView glview)
	{
		this.context=context;	
		this.glview=glview;
		init_Values();
		init_buffer();
		GetAllOfInstalledAPKInfo();
	}
	private void init_Values()
	{
		InvokedSwipe= GLView.NoSwipe;
		ListTexture=new ArrayList<TextureImg>();
		ListOutOfBoundary=new ArrayList<TextureImg>();
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
	private void GetAllOfInstalledAPKInfo()
	{
		manager = context.getPackageManager();

		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		apps = manager.queryIntentActivities(mainIntent, 0);
		
		INSTALLED_APK_Num=apps.size();
		Log.d("list_texture_size", "size:!!!!!!!" + INSTALLED_APK_Num);

	}
	public void InvokeSwipe(int direction)
	{
		this.InvokedSwipe=direction;
	}
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
//		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Set color's clear-value to

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
/////new/////		
		 gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE);
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
		Iterator<TextureImg> iter=ListTexture.iterator();
		TextureImg model;
		while(iter.hasNext())
		{
			model=iter.next();
			if(InvokedSwipe != GLView.NoSwipe)
				model.setDirection(InvokedSwipe);
			model.draw(gl);
			if(model.getState().IsLoading == false)
			{
				ListOutOfBoundary.add(model);
				iter.remove();
			}
		}
		if(ListOutOfBoundary.size() >=3)
		{
			CleanListOutOfBoundary();
		}
	/*	for(TextureImg model : ListTexture){
			if(InvokedSwipe != GLView.NoSwipe)
				model.setDirection(InvokedSwipe);
			model.draw(gl);
			if(model.getState().IsLoading == false)
				AddOutOfBoundaryModel(model);
		}*/
	}
	private int nextCurIndex()
	{
		int nextIndex;
		if(InvokedSwipe == GLView.BackwardSwipe)
		{
			if(ListTexture.size() == 0)
				return CurModelIndex;
			
			NextModelIndex=CurModelIndex;
			CurModelIndex--;
			if(CurModelIndex < 0)
			{
				CurModelIndex=INSTALLED_APK_Num-1;
			}

			return CurModelIndex;		
		}
		else if(InvokedSwipe == GLView.ForwardSwipe)
		{		
			CurModelIndex=NextModelIndex;
			NextModelIndex++;
			if(NextModelIndex >=INSTALLED_APK_Num)
				NextModelIndex=0;
			//CurModelIndex++;
			return CurModelIndex;	
		}
		return NO_INDEX;
	}
	private TextureImg getCurModel()
	{
		for(TextureImg model : ListTexture)
		{
			if(model.getIndex() == CurModelIndex)
				return model;
		}
		return null;
	}
	private boolean SearchNextCurIndex(int nextIndex,int texID,TextureImg cur)
	{
		for(TextureImg model : ListTexture)
		{
			if(model.getIndex() == nextIndex)
			{
				model.getState().setCurrent(true);
				cur.getState().setCurrent(false);
			//	CurModelIndex=nextIndex;
				return true;
			}
		}
		Log.d("searchModel", "next: "+NextModelIndex);
		Log.d("searchModel", "cur: "+ CurModelIndex);
		Iterator<TextureImg> iter=ListOutOfBoundary.iterator();
		TextureImg model;
		while(iter.hasNext())
		{
			model=iter.next();
			if(model.getIndex() == nextIndex)
			{
				model.getState().setCurrent(true);
				model.getState().IsLoading=true;
				cur.getState().setCurrent(false);
				ListTexture.add(model);
				model.setTexId(texID);
				iter.remove();
			//	CurModelIndex=nextIndex;
				return true;
			}
		}

		return false;
	}
	private void createModelEntity()
	{
		int texID=0;
		TextureImg CurModel=getCurModel();
		int nextCurIndex=nextCurIndex();
		Log.d("test", "hhhhhhhhh");
		if(nextCurIndex == NO_INDEX)
			return ;
		
		texID=getID();
		if(texID == NO_TEXTURE_AVAILABLE)
		{
			CleanListOutOfBoundary();
		}
		if(SearchNextCurIndex(nextCurIndex,texID,CurModel)==true)
			return ;
		Log.d("test", "ttttttt");
		Log.d("texture id", "texId: "+texID);
		if(texID != NO_TEXTURE_AVAILABLE)
		{
			TextureImg model=new TextureImg(this,nextCurIndex,texID);
			model.setDirection(InvokedSwipe);
			model.getState().setCurrent(true);          // cur index changed
			model.getState().IsLoading=true;
			ListcheckID[texID]=true;
			Log.d("test", "aaaaaaaaa");
			model.setBitmap(loadBitmap(nextCurIndex));
			Log.d("aaa", ""+CurModel);
			if(ListTexture.size() != 0)
			{
				CurModel.getState().setCurrent(false);
				
			}
/*			if(InvokedSwipe == GLView.ForwardSwipe)
				NextModelIndex++;
			else
				NextModelIndex=CurModelIndex;
			CurModelIndex=nextIndex; */
			Log.d("Index", "cur :"+CurModelIndex);
			Log.d("Index", "next :"+NextModelIndex);
			ListTexture.add(model);
		}
			
	}
	private Bitmap loadBitmap(int index)
	{
		BitmapDrawable drawble=(BitmapDrawable) apps.get(index).loadIcon(manager);
		return drawble.getBitmap();
	}
	public int getDirection()
	{
		return InvokedSwipe;
	}
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		
		if(InvokedSwipe != GLView.NoSwipe)
		{
			Log.d("ForwardInVoked", "asdf/4444444");
			// glview.setDisappearedPlace(); //
			if(ListTexture.size() <= MAX_MODEL_NUM)
				createModelEntity();
		}
		setGL(gl);
		DrawObjects(gl);
		unSetGl(gl);
		InvokedSwipe = GLView.NoSwipe;
	}
	private void setGL(GL10 gl)
	{
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity(); // Reset the model-view matrix
		
		gl.glScalef(0.8f, 0.8f, 0.8f); // Scale down (NEW)
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
	}
	private void unSetGl(GL10 gl)
	{
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);  // Disable texture-coords-array (NEW)
	    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	    gl.glDisable(GL10.GL_CULL_FACE);
	}
    private void CleanListOutOfBoundary()
    {
    	Log.d("clean","clean!!!!!!!!!!!!!"	);
    	Iterator<TextureImg> iter=ListOutOfBoundary.iterator();
		TextureImg model;
		while(iter.hasNext())
		{
			model=iter.next();
    		ListcheckID[model.getTexId()]=false;
    		model.getBitmap().recycle();
    		iter.remove();
		}
    }
}
