package com.example.Camera;

import java.io.File;

import com.example.Voice.VoiceActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class ImageViewer extends VoiceActivity {

	private ImageView view;
	private File imgFile;
	private BroadcastReceiver receiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_viewer);
		view = (ImageView)findViewById(R.id.imageView);
		Uri uri=Uri.parse(getIntent().getExtras().getString("imgUri"));
		imgFile=new File(uri.getPath());
		if(imgFile.exists()){
		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		    view.setImageBitmap(myBitmap);
		}

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub	
		if(receiver == null)
		{
			IntentFilter filter=new IntentFilter(SpeechCommandList.FILTER);
			receiver=new SpeechBroadcastReceiver();
			registerReceiver(receiver, filter);
		}
		super.onResume();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if( receiver != null)
		{
			unregisterReceiver(receiver);
			receiver=null;
		}
		super.onPause();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
			MenuItem item;
			
			item=menu.add("ok");
		//	item.setIcon(an)
			menu.add("delete");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == -1) {
			return true;
		}
		if(item.getItemId() == 1)
		{
			imgFile.delete();
			finish();
		}
		return true;
	}
	
	@Override
	public void onOptionsMenuClosed(Menu menu) {
	    finish();
	}
	
	private class SpeechBroadcastReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String str=intent.getExtras().getString("Speech");
			Log.d("Main broad", "broadcast:"+str);
			if(str.equals("finish"))
			{
				Intent i=new Intent();
				((CameraActivity)context).setResult(RESULT_OK);
				((CameraActivity)context).finish();
			}
		}		
	}
}
