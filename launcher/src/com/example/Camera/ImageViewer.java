package com.example.Camera;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.Voice.VoiceActivity;
import com.example.Voice.VoiceCommand;
import com.example.launcher.R;
import com.example.util.IntentBuilder;

public class ImageViewer extends VoiceActivity {

	private final String TAG="ImageViewer";
	private ImageView view;
	private File imgFile;
	private Uri uri;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_viewer);
		view = (ImageView)findViewById(R.id.imageView);
		String CacheKey=getIntent().getExtras().getString("CacheKey");
		uri=Uri.parse(CacheKey);
		CommandList=new String[]{"send","delete","finish"};
		view.setImageBitmap(TakePictureCallback.getBitmap(CacheKey));
/*		imgFile=new File(uri.getPath());
		if(imgFile.exists()){
		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		    view.setImageBitmap(myBitmap);
		}*/
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	//	setCommands();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK)
		{
			UnBindService();
			setResult(RESULT_OK);
			finish();
		}
	}
	@Override
	public void onVoiceCommand(String command) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onVoiceCommand(int cmdId) {
		// TODO Auto-generated method stub
		final int id=cmdId;
		if(id == 0)  // send
		{
			Log.d(TAG, "send selected");
			UnBindService();
			Intent intent=IntentBuilder.CreateIntent(ImageViewer.this, ImageCommentDialog.class).setUri(uri.toString()).build();
			IntentBuilder.startActivityForResult(ImageViewer.this, intent);
		}
		else if(id == 1) // delete
		{
			Log.d(TAG, "delete selected");
			imgFile=new File(uri.getPath());
			imgFile.delete();
			UnBindService();
			setResult(RESULT_OK);
			finish();
		}
		else if(id == 2)  // finish
		{
			UnBindService();
			setResult(RESULT_OK);
			finish();
		}
	}
	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		openOptionsMenu();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
			for(String command : CommandList)
			{
				MenuItem item=menu.add(command);
				if(command.equals("send"))
					item.setIcon(R.drawable.ic_share_50);
				else if(command.equals("delete"))
					item.setIcon(R.drawable.ic_delete_50);
				else if(command.equals("finish"))
					item.setIcon(R.drawable.ic_no_50);
			}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Log.d(TAG, "id:" + item.getItemId() + "title:"+ item.getTitle());
		if (item.getItemId() == -1) {
			return true;
		}
		else if(item.getTitle().toString().equals("send"))
		{
			Log.d(TAG, "send");
			UnBindService();
			Intent intent=new Intent(ImageViewer.this,ImageCommentDialog.class);
			intent.putExtra("Uri", uri.toString());
			startActivityForResult(intent, 0);
	//		Intent intent=IntentBuilder.CreateIntent(this, ImageCommentDialog.class).setCacheKey(uri.toString()).build();
	//		IntentBuilder.startActivityForResult(this, intent);
		}
		else if(item.getTitle().toString().equals("delete"))
		{	
			Log.d(TAG, "delete");
			UnBindService();
			imgFile=new File(uri.getPath());
			imgFile.delete();
			finish();
		}
		else if(item.getTitle().toString().equals("finish"))
		{
			Log.d(TAG, "finish");
			UnBindService();
			setResult(RESULT_OK);
			finish();
		}
		
		return true;
	}
}
