package com.example.launcher;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GLView extends GLSurfaceView {

	private Context context;
	private GLRenderer renderer;
	private float DownX;
	private float DownY;
	private float UpX;
	private float UpY;
	private final static float EPSILON=20.f;
	private static int ListTextureSize=0;
	
	private TextureImg CurAppIcon=null;
	private TextureImg NextAppIcon=null;
	private List<TextureImg> ListTexture;
	private int CntSwipe=0;
	
	private static int indexOfCurAPK=0;
	private static int indexOfNextAPK=0;
	
	
	public GLView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;
		
		init_ListTexture();
		
		renderer= new GLRenderer(context,this);
		ListTexture= new ArrayList<TextureImg>();
		
		this.requestFocus();
		this.setFocusableInTouchMode(true);
		this.setEGLConfigChooser(8,8,8,8,14,0);
		this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		this.setRenderer(renderer);

	}

	private void init_ListTexture()
	{
       PackageManager manager = context.getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        
        ListTextureSize=apps.size();
        
        for(ResolveInfo info : apps)
        {
        	ListTexture.add(new TextureImg(info));
        }
        CurAppIcon=NextAppIcon=ListTexture.get(0);
        
	}
	public boolean IsExistCur()
	{
		if(CurAppIcon==null)
			return false;
		else
			return true;
	}
	public boolean IsInvokedSwipe()
	{
		if(CntSwipe >= 0)
		{
			return true;
		}
		return false;
	}
	public void SwipeHandleDone()
	{
		CntSwipe--;
		if(CntSwipe < 0)
			CntSwipe=0;
	}
	public TextureImg CurAPK()
	{
		return CurAppIcon;
	}
	public TextureImg NextAPK()
	{
		return NextAppIcon;
	}
	public int getListSize()
	{
		return ListTextureSize;
	}
	public int getIndexOfNextAPK()
	{
		return indexOfNextAPK;
	}
	public int getIndexOfCurAPK()
	{
		return indexOfCurAPK;
	}
	public void changeNextToCur()
	{
		CurAppIcon=NextAppIcon;
		int temp=indexOfNextAPK;
		NextAPK_PointNext();
		indexOfCurAPK=temp;
		
	}
	public void NextAPK_PointNext()
	{
		do
		{
			if((indexOfNextAPK+=1) >= ListTextureSize)
			{
				indexOfNextAPK=0;
			}
			
		}while(indexOfNextAPK!=indexOfCurAPK);
		NextAppIcon=ListTexture.get(indexOfNextAPK);
	}
	public void CurAPK_PointNext()
	{
		do
		{
			if((indexOfCurAPK+=1) >= ListTextureSize)
			{
				indexOfCurAPK=0;
			}
			
		}while(indexOfCurAPK!=indexOfNextAPK);
		CurAppIcon=ListTexture.get(indexOfCurAPK);
	}

	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		// TODO Auto-generated method stub
	      switch (evt.getAction()) {
	         case MotionEvent.ACTION_DOWN:
	     			DownX = evt.getX();
	     			DownY = evt.getY();
	         case MotionEvent.ACTION_UP:
	        	 	UpX=evt.getX();
	        	 	UpY=evt.getY();
	        	 	
	        	 	float diff=UpX-DownX;
	        	 	if(Math.abs(diff)>=EPSILON)
	        	 	{
	        	 		CntSwipe++;
	        	 	}
	      }
	      return true;  // Event handled
	}
}
