package com.example.launcher;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	private GLSurfaceView mGLView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	/*	requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		if(hasGLES20())
		{	*/
			mGLView=new GLView(this);	
			setContentView(mGLView);
/*		}
		else
			setContentView(R.layout.activity_main); */
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
	   
	   private boolean hasGLES20() {
		    ActivityManager am = (ActivityManager)
		                getSystemService(Context.ACTIVITY_SERVICE);
		    ConfigurationInfo info = am.getDeviceConfigurationInfo();
		    return info.reqGlEsVersion >= 0x20000;
		}
}
