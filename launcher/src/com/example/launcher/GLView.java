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
	private final static float EPSILON=20.f;
	private static int ListTextureSize=0;
	
	private TextureImg CurAppIcon=null;
	private TextureImg NextAppIcon=null;
	private List<TextureImg> ListTexture;
	private int CntSwipe=0;
	
	private int CntAlive=0;
	private static int indexOfCurAPK=0;
	private static int indexOfNextAPK=0;
	private PackageManager manager;
	
	private FloatBuffer texBuffer;
	
	private int[] textureIds=new int[5];
	float[] texCoords = { // Texture coords for the above face (NEW)
			0.0f, 1.0f, // A. left-bottom (NEW)
			1.0f, 1.0f, // B. right-bottom (NEW)
			0.0f, 0.0f, // C. left-top (NEW)
			1.0f, 0.0f // D. right-top (NEW)
	};
//	private List<ResolveInfo> apps;
	public GLView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;
		
		init_ListTexture();
		
		renderer= new GLRenderer(context,this);
		
		this.requestFocus();
		this.setFocusableInTouchMode(true);
		this.setEGLConfigChooser(8,8,8,8,14,0);
		this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		this.setRenderer(renderer);

	}

	private void init_ListTexture()
	{
        manager = context.getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        int i=0;
        ListTexture= new ArrayList<TextureImg>();
        for(ResolveInfo info : apps)
        {
        	ListTexture.add(new TextureImg(info,this));
        	ListTexture.get(ListTexture.size()-1).setInfo(info.loadIcon(manager), info.resolvePackageName);
        	ListTexture.get(ListTexture.size()-1).setIndex(i);
        	i++;
        }
        ListTextureSize=ListTexture.size();
        Log.d("list_texture_size", "size:!!!!!!!"+ListTextureSize);
        CurAppIcon=NextAppIcon=ListTexture.get(0);
	}
	public void changeCurrentAPK()
	{
		int index=indexOfCurAPK;
		if((++index)>=ListTextureSize)
			index=0;
		TextureImg temp=null;
		do
		{
			temp=ListTexture.get(index);
			if(temp.getState().IsLoading==true)
			{
				temp.getState().setCurrent(true);
				indexOfCurAPK=index;
				break;
			}				
		}while(temp.getState().IsLoading==false);
	}
	public int getSwipeCnt()
	{
		return CntSwipe;
	}
	public void setDisappearedPlace()
	{
		for(int i=0;i<CntAlive-1;i++){
			ListTexture.get(indexOfCurAPK+i).getState().setDissapearedZ();
		}
	}
	public void DrawObjects(GL10 gl)
	{
		int index=indexOfCurAPK;
		TextureImg temp=null;
		int id=0;
		while(index != indexOfNextAPK)
		{
			temp=ListTexture.get(index);
			if(temp.getState().IsLoading==true)
			{
				ListTexture.get(index).loadTexture(gl, context, id);
				ListTexture.get(index).draw(gl,texBuffer);
			}
			if((++index)>=ListTextureSize)
				index=0;
			id++;
		}
	}
	public void loadTexture(GL10 gl	)
	{
		
		gl.glGenTextures(5, textureIds,0);
		ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
	      tbb.order(ByteOrder.nativeOrder());
	      texBuffer = tbb.asFloatBuffer();
	      texBuffer.put(texCoords);
	      texBuffer.position(0);
	      
	/*	int index=indexOfCurAPK;
		while(index != indexOfNextAPK)
		{
			if(ListTexture.get(index).getState().IsLoading)
				ListTexture.get(index).loadTexture(gl, context);
			if((++index)>=ListTextureSize)
				index=0;
		} */
	}
	public boolean IsInvokedSwipe()
	{
		if(CntSwipe > 0)
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
		return ListTexture.get(indexOfCurAPK);
	}
	public TextureImg NextAPK()
	{
		return ListTexture.get(indexOfNextAPK);
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
		
		Log.d("changeNextToCur", "next index:"+indexOfNextAPK + " cur index:"+indexOfCurAPK);
		
	}
	public void NextAPK_PointNext()
	{
		do
		{
			if((indexOfNextAPK+=1) >= ListTextureSize)
			{
				indexOfNextAPK=0;
			}
			
		}while(indexOfNextAPK==indexOfCurAPK);
		
		Log.d("next apk point", "ListTextureSize:"+ ListTextureSize + " next index:"+indexOfNextAPK);
	}
	public void CurAPK_PointNext()
	{
		do
		{
			if((indexOfCurAPK+=1) >= ListTextureSize)
			{
				indexOfCurAPK=0;
			}
			
		}while(indexOfCurAPK==indexOfNextAPK);
		CurAppIcon=ListTexture.get(indexOfCurAPK);
	}
	public void APKAliveDecrement()
	{
		--CntAlive;
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
	        	 		NextAPK().initialize_Coordinate();
	        	 		NextAPK().getState().IsLoading=true;
	        	 		NextAPK_PointNext();  // next index is changed
	        	 		CntAlive++;
	        	 	}
	      }
	      return true;  // Event handled
	}
}
