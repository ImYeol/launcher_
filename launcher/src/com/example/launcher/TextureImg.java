package com.example.launcher;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLUtils;
import android.util.Log;

public class TextureImg {
	private FloatBuffer vertexBuffer; // Buffer for vertex-array
//	private FloatBuffer texBuffer; // Buffer for texture-coords-array (NEW)

	private ResolveInfo info;
	private State state; 
	private GLView glview;
	private int index;
	
	private Drawable AppImage;
	private String AppName;
	private int textureId;
	private float x=1.5f,y=0.0f,z=-30.0f;
	
	private float[] vertices = { // Vertices for a face
			-1.0f, -1.0f, 0.0f, // 0. left-bottom-front
			1.0f, -1.0f, 0.0f, // 1. right-bottom-front
			-1.0f, 1.0f, 0.0f, // 2. left-top-front
			1.0f, 1.0f, 0.0f // 3. right-top-front
	};

	float[] texCoords = { // Texture coords for the above face (NEW)
			0.0f, 1.0f, // A. left-bottom (NEW)
			1.0f, 1.0f, // B. right-bottom (NEW)
			0.0f, 0.0f, // C. left-top (NEW)
			1.0f, 0.0f // D. right-top (NEW)
	};
	int[] textureIDs = new int[1]; // Array for 1 texture-ID (NEW)

	   // Constructor - Set up the buffers
	   public TextureImg(ResolveInfo info, GLView glview) {
		   
		   this.info= info;
		   this.state=new State();
		   this.glview=glview;
	      // Setup vertex-array buffer. Vertices in float. An float has 4 bytes
	      ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
	      vbb.order(ByteOrder.nativeOrder()); // Use native byte order
	      vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
	      vertexBuffer.put(vertices);         // Copy data into buffer
	      vertexBuffer.position(0);           // Rewind
	  
	      // Setup texture-coords-array buffer, in float. An float has 4 bytes (NEW)
	 /*     ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
	      tbb.order(ByteOrder.nativeOrder());
	      texBuffer = tbb.asFloatBuffer();
	      texBuffer.put(texCoords);
	      texBuffer.position(0);*/
	   }
	   public void setIndex(int index)
	   {
		   this.index=index;
		   
	   }
	   public void setInfo(Drawable img,String name)
	   {
		   this.AppImage=img;
		   this.AppName=name;
	   }
	   // Draw the shape
	   public void draw(GL10 gl,FloatBuffer texBuffer) {
		   
		 //  gl.glTranslatef(x,y,z);
		   gl.glScalef(0.8f, 0.8f, 0.8f); // Scale down (NEW)
//////////////////////////////////////////////////////////////////////////////////////////////////////
	      gl.glFrontFace(GL10.GL_CCW);    // Front face in counter-clockwise orientation
	      gl.glEnable(GL10.GL_CULL_FACE); // Enable cull face
	      gl.glCullFace(GL10.GL_BACK);    // Cull the back face (don't display) 
	   
	      gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	      gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
	      gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);  // Enable texture-coords-array (NEW)
	      gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer); // Define texture-coords buffer (NEW)
	      
	      // front
	      gl.glPushMatrix();
	      //gl.glTranslatef(0.0f, 0.0f,1.0f);
	      gl.glTranslatef(x,y,z);
	      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
	      gl.glPopMatrix();
	      
	      
	      gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);  // Disable texture-coords-array (NEW)
	      gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	      gl.glDisable(GL10.GL_CULL_FACE);

	      if(z> state.displayedPlace && z<= state.disspearedPlace)
	      {
	    	  if(state.IsCompleted()==false)
	    	  {
	    		 // glview.SwipeHandleDone();
	    		  state.setCompleted(true);
	    	  }
	      }
	      if(z<=state.getZ())
	      {
	    	  z+=3.f/15;
	      }
	      else if(z > state.disspearedPlace)
	      {
	    	  if(state.getCurrent())
	    	  {
	    		 glview.changeCurrentAPK();
	    		 state.setCurrent(false);
	    	  }
	    	  this.state.IsLoading=false;
	    	  //z=-30.0f;
	      }
	      Log.d("z"+index, "z"+index+": "+z);
	   }
	  public void loadTexture(GL10 gl,Context context,int id)
	  {
		  gl.glBindTexture(GL10.GL_TEXTURE_2D, id);   // Bind to texture ID
	      // Set up texture filters
	      gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	      gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	      PackageManager manager = context.getPackageManager();

	        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
	        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

	        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
	        
	        BitmapDrawable b=(BitmapDrawable)apps.get(index).loadIcon(manager);
	        Bitmap bitmap=b.getBitmap();
		  GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
	      //bitmap.recycle();
	  }
	   // Load an image into GL texture
	   public void loadTexture(GL10 gl, Context context) {
	      gl.glGenTextures(1, textureIDs, 0); // Generate texture-ID array

	      gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[0]);   // Bind to texture ID
	      // Set up texture filters
	      gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	      gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	 
	      // Construct an input stream to texture image "res\drawable\nehe.png"
	 //     InputStream istream = context.getResources().openRawResource(R.drawable.iron_man);
	      
	      
	//      BitmapDrawable drawableIcon=(BitmapDrawable)info.loadIcon(context.getPackageManager());
	//      Bitmap bitmap=drawableIcon.getBitmap();
	      BitmapDrawable drawableIcon=(BitmapDrawable)AppImage;
	      Bitmap bitmap=drawableIcon.getBitmap();

	      // Build Texture from loaded bitmap for the currently-bind texture ID
	      GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
	      bitmap.recycle();
	   }
	   public State getState()
	   {
		   return state;
		   
	   }
	   public void initialize_Coordinate()
	   {
		   this.x=1.5f;
		   this.y=0.0f;
		   this.z=-30.0f;
	   }
}
