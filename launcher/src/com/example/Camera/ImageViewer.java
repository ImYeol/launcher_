package com.example.Camera;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.Voice.VoiceActivity;
import com.example.Voice.VoiceCommand;
import com.example.launcher.R;
import com.example.util.IntentBuilder;

public class ImageViewer extends VoiceActivity {

	private ImageView view;
	private File imgFile;
	private BroadcastReceiver receiver;
	private Uri uri;
	private List<String> CommandList=Arrays.asList("Upload","Delete","Finish");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_viewer);
		view = (ImageView)findViewById(R.id.imageView);
		String CacheKey=getIntent().getExtras().getString("CacheKey");
		uri=Uri.parse(CacheKey);
		
		view.setImageBitmap(TakePictureCallback.getBitmap(CacheKey));
/*		imgFile=new File(uri.getPath());
		if(imgFile.exists()){
		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		    view.setImageBitmap(myBitmap);
		}*/
		
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		setCommands();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	@Override
	public void onVoiceCommand(String command) {
		// TODO Auto-generated method stub
		if(command.equals("upload"))
		{
			Intent intent=IntentBuilder.CreateIntent(this, ImageCommentDialog.class).setUri(uri.toString()).build();
			IntentBuilder.startActivity(this, intent);
		}
		else if(command.equals("delete"))
		{
			imgFile=new File(uri.getPath());
			imgFile.delete();
			finish();
		}
		else if(command.equals("finish"))
		{
			setResult(RESULT_OK);
			finish();
		}
	}
	@Override
	public void setCommands() {
		// TODO Auto-generated method stub
		VoiceCommand voiceCommand=new VoiceCommand(CommandList);
		mVoiceCommandListener.setCommands(voiceCommand);
		
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
				menu.add(command);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == -1) {
			return true;
		}
		else if(item.getItemId() == 0)
		{
			Intent intent=IntentBuilder.CreateIntent(this, ImageCommentDialog.class).setCacheKey(uri.toString()).build();
			IntentBuilder.startActivityForResult(this, intent);
		}
		else if(item.getItemId() == 1)
		{
			imgFile=new File(uri.getPath());
			imgFile.delete();
			finish();
		}
		else if(item.getItemId() == 2)
		{
			setResult(RESULT_OK);
			finish();
		}
		
		return true;
	}
	
	@Override
	public void onOptionsMenuClosed(Menu menu) {
	    finish();
	}
	
}
