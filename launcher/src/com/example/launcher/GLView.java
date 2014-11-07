package com.example.launcher;

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
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

public class GLView extends GLSurfaceView {

	private Context context;
	private GLRenderer renderer;
	private float DownX;
	private float DownY;
	private float UpX;
	private float UpY;
	private final static float EPSILON=10.f;
	private static int ListTextureSize=0;
	
	public final static int NoSwipe=0;
	public final static int BackwardSwipe=1;
	public final static int ForwardSwipe=2;
	
	private TextureImg CurAppIcon=null;
	private TextureImg NextAppIcon=null;
	private List<TextureImg> ListTexture;

	private static int indexOfCurAPK=0;
	private static int indexOfNextAPK=0;
	private PackageManager manager;

//	private List<ResolveInfo> apps;
	public GLView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;

		renderer= new GLRenderer(context,this);
		
		this.requestFocus();
		this.setFocusableInTouchMode(true);
		this.setEGLConfigChooser(8,8,8,8,14,0);
		this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		this.setRenderer(renderer);

	}

	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		// TODO Auto-generated method stub
		switch (evt.getAction()) {
		case MotionEvent.ACTION_DOWN:
			DownX = evt.getX();
			DownY = evt.getY();
		case MotionEvent.ACTION_UP:
			UpX = evt.getX();
			UpY = evt.getY();

			float diff = UpX - DownX;
			if (Math.abs(diff) >= EPSILON) {
				if (diff >= 0) {
					renderer.InvokeSwipe(ForwardSwipe);
				} else {
					renderer.InvokeSwipe(BackwardSwipe);
				}
			}
		}
		return true; // Event handled
	}
}
