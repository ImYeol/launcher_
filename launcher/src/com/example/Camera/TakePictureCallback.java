package com.example.Camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;

import com.example.util.IntentBuilder;

public class TakePictureCallback implements PictureCallback {

	private static final String tag = "TakePictureCallback";
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri takenPictureUri;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private Context context;
	
	private static LruCache<String, Bitmap> lruCache;

	public TakePictureCallback(Context context) {
		this.context = context;
		lruCache= new LruCache<String, Bitmap>(10);
	}

	public static Bitmap getBitmap(String key)
	{
		return lruCache.get(key);
	}
	public static void resetBitmap(String key)
	{
		lruCache.remove(key);
	}
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		Log.d(tag, "onPictureTaken");
		File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
		if (pictureFile == null) {
			Log.d(tag, "Error creating media file, check storage permissions: ");
			return;
		}

		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			fos.write(data);
			fos.close();
		} catch (FileNotFoundException e) {
			Log.d(tag, "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d(tag, "Error accessing file: " + e.getMessage());
		}
		Uri uri = Uri.fromFile(pictureFile);
		Bitmap bitmap=BitmapFactory.decodeByteArray(data, 0, data.length);
		lruCache.put(uri.toString(), bitmap);
		Intent intent=IntentBuilder.CreateIntent(context, ImageViewer.class).setCacheKey(uri.toString()).build();
		IntentBuilder.startActivity(context, intent);
		
	//	Intent i = new Intent(context, ImageViewer.class);
	//	i.putExtra("imgUri", uri.toString());
	//	context.startActivi(i);
	}

	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}
}
