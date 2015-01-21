/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.launcher;

import javax.microedition.khronos.opengles.GL10;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * Represents a launchable application. An application is made of a name (or title), an intent
 * and an icon.
 */
class ApplicationInfo {

	public final static String TAG="ApplicationInfo";
    CharSequence title;
    /**
     * The intent used to start the application.
     */
    Intent intent;

    /**
     * The application icon.
     */
    Drawable icon;

    /**
     * When set to true, indicates that the icon has been resized.
     */
    boolean voiceTag=false;
    
    private static float offset=0.f;
    public static int Destination=Constants.NO_DESTINATION;
	private Bitmap bitmap=null;
	private int texID=-1;
	private float x,y,z;
	private int index=0;
	public static boolean IsArrived=false;
	public static boolean IsScrolling=false;
    /**
     * Creates the application intent based on a component name and various launch flags.
     *
     * @param className the class name of the component representing the intent
     * @param launchFlags the launch flags
     */
	
	public ApplicationInfo() {
		// TODO Auto-generated constructor stub
		x=0.5f;y=0.f;z=Constants.originPlace;
	}
    final void setActivity(ComponentName className, int launchFlags) {
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(className);
        intent.setFlags(launchFlags);
    }
    public void setIntent(Intent intent)
    {
    	this.intent=intent;
    }
    public void resetToFrontPlace()
    {
    	x=0.5f;y=0.f;z=Constants.disapearedPlace;
    }
    public void resetToBackPlace()
    {
    	x=0.5f;y=0.f;z=Constants.originPlace;
    }
    public void resetToCenterPlace()
    {
    	x=0.5f;y=0.f;z=Constants.displayedPlace;
    }
    public void setIndex(int index)
    {
    	this.index=index;
    }
    public void setReady(int texID)
    {
		this.texID = texID;
    }
    public int getReady()
    {
    	return texID;
    }
    public void setZ(float z)
    {
    	this.z=z;
    }
    public float getZ()
    {
    	return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicationInfo)) {
            return false;
        }

        ApplicationInfo that = (ApplicationInfo) o;
        return title.equals(that.title) &&
                intent.getComponent().getClassName().equals(
                        that.intent.getComponent().getClassName());
    }

    @Override
    public int hashCode() {
        int result;
        result = (title != null ? title.hashCode() : 0);
        final String name = intent.getComponent().getClassName();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
    
    public static void setOffset(float offset)
    {
    	ApplicationInfo.offset=offset;
    }
    public static float getOffset()
    {
    	return ApplicationInfo.offset;
    }
    public boolean IsReadyToMove()
    {
    	if(texID == -1)
    	{
    		return false;
    	}
    	return true;
    }
 // Draw the shape
 	public void draw(GL10 gl) {

 		bitmap=Bitmap.createScaledBitmap(((BitmapDrawable)icon).getBitmap(), 128, 128, false);
		gl.glPushMatrix();

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texID); 
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		z+=offset;
		z=(float)Math.round(z * 100)/100;
		Log.d(TAG,"swipe"+index+" z: "+z);
			
		gl.glTranslatef(x, y, z); 
		
//		checkState();
		if( z > Constants.displayedPlace)
			gl.glColor4f(0.5f, 0.5f, 0.5f, (Constants.disapearedPlace - z) / Constants.alphaDownRegion);
		else
			gl.glColor4f(0.5f, 0.5f, 0.5f, 1.f);
		gl.glScalef(10, 10, 10);
		if(z > Constants.originPlace && z < Constants.disapearedPlace )
		{
			Log.d(TAG, "image"+index);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0); 
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		}
		
		if(ApplicationInfo.IsScrolling == false)
			checkArrived();
		gl.glPopMatrix();
		bitmap.recycle();

 	}
 	private void checkArrived()
 	{
 		if(z >= Constants.disapearedPlace || z <= Constants.originPlace)
 		{
 			ApplicationInfo.IsArrived=true;
 		}
 	}
	private float floorby2(float f)
	{
		return ((float)Math.floor(f*100))/100;
	}
}
