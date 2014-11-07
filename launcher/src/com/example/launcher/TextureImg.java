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
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

public class TextureImg {
	private FloatBuffer vertexBuffer; // Buffer for vertex-array
//	private FloatBuffer texBuffer; // Buffer for texture-coords-array (NEW)

	private ResolveInfo info;
	private State state; 
	private GLView glview;
	private int index=-1;
	
	private Drawable AppImage;
	private String AppName;
	private int texId;
	private float x=0.5f,y=0.0f,z=-30.0f;
	private int direction=0;
	private Bitmap bitmap;
	private GLRenderer controller;
	   // Constructor - Set up the buffers
	
	public TextureImg(GLRenderer controller, int index, int texId) {
		this.controller = controller;
		this.texId = texId;
		this.index = index;
		this.direction=controller.getDirection();
		if(direction == GLView.BackwardSwipe)
		{
			x=0.5f;y=0.0f;z= 10.0f;
		}
		else if(direction == GLView.ForwardSwipe)
		{
			x=0.5f;y=0.0f;z=-30.0f;
		}
		else
		{
			x=0.f;y=0.f;z=0.f;
		}
		this.state=new State();
	}

	public int getTexId() {
		return texId;
	}
	public void setTexId(int texId)
	{
		this.texId=texId;
	}
	public int getIndex() {
		return index;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap)
	{
		this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
	//	this.bitmap=bitmap;
	}
	public void setIndex(int index) {
		this.index = index;

	}

	public void setInfo(Drawable img, String name) {
		this.AppImage = img;
		this.AppName = name;
	}

	// Draw the shape
	public void draw(GL10 gl) {

		gl.glPushMatrix();

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texId); // Bind to texture ID
		// Set up texture filters
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
		if(z > state.displayedPlace && z <= state.disapearedPlace)
		{
			gl.glColor4f(1.f, 1.f, 1.f, 1.f + z * 0.03f);
		}
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		gl.glTranslatef(x, y, z);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glPopMatrix();

		if (direction == GLView.ForwardSwipe) {
			if (z <= state.getZ(direction))
				z += 3.f / 5;
			else if (z > state.disapearedPlace && state.getCurrent()==false) {
				state.IsLoading=false;
			}
		} else if (direction == GLView.BackwardSwipe) {
			if (z >= state.getZ(direction))
				z -= 3.f / 5;
			else if (z < state.originPlace && state.getCurrent()==false) {
				state.IsLoading=false;
			}
		}
		Log.d("direction", ""+direction);
		Log.d("z" + index, "z" + index + ": " + z);
	}

	public State getState() {
		return state;

	}

	public void initialize_Coordinate(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
}
