package com.example.Camera;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.widget.ImageView;

import com.example.Voice.VoiceActivity;
import com.example.Voice.VoiceCommand;
import com.example.launcher.R;
import com.example.util.IntentBuilder;
import com.google.android.glass.media.Sounds;

public class ImageViewer extends VoiceActivity {

	private final String TAG="ImageViewer";
	private ImageView view;
	private File imgFile;
	private Uri uri;
	private AudioManager audio;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_viewer);
		view = (ImageView)findViewById(R.id.imageView);
		String CacheKey=getIntent().getExtras().getString("CacheKey");
		uri=Uri.parse(CacheKey);
		CommandList=new String[]{"send","san","sender","sand","delete","back","bec","bank","thank","thanked"};
		view.setImageBitmap(TakePictureCallback.getBitmap(CacheKey));
		audio=(AudioManager)getSystemService(AUDIO_SERVICE);
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
		if(id == 0 || id == 1 || id ==2 || id ==3)  // send
		{
			Log.d(TAG, "send selected");
			UnBindService();
			audio.playSoundEffect(SoundEffectConstants.CLICK);
			Intent intent=IntentBuilder.CreateIntent(ImageViewer.this, ImageCommentDialog.class).setUri(uri.toString()).build();
			IntentBuilder.startActivityForResult(ImageViewer.this, intent);
		}
		else if(id == 4) // delete
		{
			Log.d(TAG, "delete selected");
			imgFile=new File(uri.getPath());
			imgFile.delete();
			UnBindService();
			setResult(RESULT_OK);
			finish();
		}
		else // finish
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
				if(command.equals("send"))
				{
					MenuItem item=menu.add(command);
					item.setIcon(R.drawable.ic_share_50);
				}
				else if(command.equals("delete"))
				{
					MenuItem item=menu.add(command);
					item.setIcon(R.drawable.ic_delete_50);
				}
				else if(command.equals("back"))
				{
					MenuItem item=menu.add(command);
					item.setIcon(R.drawable.ic_no_50);
				}
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
		else if(item.getTitle().toString().equals("back"))
		{
			Log.d(TAG, "back");
			UnBindService();
			setResult(RESULT_OK);
			finish();
		}
		
		return true;
	}
}
