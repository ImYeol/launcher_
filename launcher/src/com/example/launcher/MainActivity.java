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
		mGLView=new GLView(this);		
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
