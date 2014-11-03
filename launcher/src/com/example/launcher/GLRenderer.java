package com.example.launcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.IBinder;

public class GLRenderer implements GLSurfaceView.Renderer{
	
	private Context context;
	private GLView glview;

	private float far= -30.0f;
	private float placeOfCur=-10.f;
	private float z_cur=-30.0f;
	private float z_next=-30.0f;
	
	private boolean CurLoadState=false;
	private boolean NextLoadState=false;

	private static float anglePyramid = 0; // Rotational angle in degree for
											// pyramid (NEW)
	private static float angleCube = 0; // Rotational angle in degree for cube
										// (NEW)
	private static float speedPyramid = 2.0f; // Rotational speed for pyramid
												// (NEW)
	private static float speedCube = -1.5f; // Rotational speed for cube (NEW)


	public GLRenderer(Context context,GLView glview)
	{
		this.context=context;	
		this.glview=glview;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
//		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Set color's clear-value to

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
				
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
		
		glview.CurAPK().loadTexture(gl, context);    // Load image into Texture (NEW)
	    gl.glEnable(GL10.GL_TEXTURE_2D);  // Enable texture (NEW)
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

	private void CurApkDraw(GL10 gl)
	{
		gl.glTranslatef(1.5f, 0.0f, z_cur);
		gl.glScalef(0.8f, 0.8f, 0.8f); // Scale down (NEW)
		glview.CurAPK().draw(gl);
	}
	private void NextApkDraw(GL10 gl)
	{
		gl.glTranslatef(1.5f, 0.0f, z_next);
		gl.glScalef(0.8f, 0.8f, 0.8f); // Scale down (NEW)
		glview.NextAPK().draw(gl);
	}
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity(); // Reset the model-view matrix
//		gl.glTranslatef(1.5f, 0.0f, -6.0f); // Translate right and into the
											// screen
		gl.glTranslatef(1.5f, 0.0f, z_cur);
		
		gl.glScalef(0.8f, 0.8f, 0.8f); // Scale down (NEW)
		
//		gl.glRotatef(angleCube, 1.0f, 1.0f, 1.0f); // rotate about the axis
		if(glview.IsExistCur())											// (1,1,1) (NEW)		
			glview.CurAPK().draw(gl); // Draw the cube (NEW)
		
		if(glview.IsInvokedSwipe() && NextLoadState==false)
		{
			if (glview.CurAPK() == glview.NextAPK())    // initial load apkIcon
			{
				int nextPointer=glview.getIndexOfNextAPK(); 
				glview.NextAPK_PointNext();               // next pointer points next
				
				CurApkDraw(gl);

				if (z_cur <= placeOfCur) {
					CurLoadState=true;
					z_cur += 1.0f / 5.f;
				}
				glview.SwipeHandleDone();
			}
			else                      // not initial load apkIcon
			{
				if(CurLoadState==true && NextLoadState==false	)
				{
					NextLoadState=true;
					placeOfCur=10.f;
					CurApkDraw(gl);
					NextApkDraw(gl);
					
					glview.SwipeHandleDone();
				}
			}
		}
		else            // when both cur and next are loading image 
		{
			if(CurLoadState==true)
			{
				CurApkDraw(gl);
			}
			if(NextLoadState==true)
			{
				NextApkDraw(gl);
				if (z_next <= -10.f) {
					NextLoadState=true;
					z_next += 1.0f / 5.f;
				}
				else
				{
					NextLoadState=false;
				}
			}
			if (z_cur <= placeOfCur) {
				CurLoadState=true;
				z_cur += 1.0f / 5.f;
			}
			else                       // change next to cur when cur is done loading image
			{
				CurLoadState=false;
				if(NextLoadState==true)
				{
					CurLoadState=true;
					glview.changeNextToCur();
					NextLoadState=false;
				}
			}
		}
		// Update the rotational angle after each refresh (NEW)
		//angleCube += 10; // (NEW)
	/*	if(angleCube <=60.f)
			angleCube+=1; */
	}

}
