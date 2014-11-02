package com.example.launcher;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends Activity {

	private GLSurfaceView mGLView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGLView=new GLSurfaceView(this);
		mGLView.setEGLConfigChooser(8,8,8,8,14,0);
		mGLView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		mGLView.setOnDragListener(new glDragListener());
		mGLView.setRenderer(new GLRenderer(this));
		
		setContentView(mGLView);
	}
	
	@Override
	   protected void onPause() {
	      super.onPause();
	      mGLView.onPause();
	   }
	   
	   // Call back after onPause()
	   @Override
	   protected void onResume() {
	      super.onResume();
	      mGLView.onResume();
	   }
}
