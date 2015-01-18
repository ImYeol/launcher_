package com.example.Voice;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.Camera.CameraActivity;
import com.example.launcher.R;
import com.example.util.IntentBuilder;
import com.example.util.WarningDialog;
import com.google.android.glass.widget.CardBuilder;

public class VoiceCommandListActivity extends VoiceActivity {

	private static final String TAG = "VoiceCommandListActivity";
	private TextView camera_label;
	private TextView search_label;
	private TextView finish_label;
	
	private View EmbeddedView;
	private View warningView;
	private FrameLayout mainFrame;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voice_commands_list_layout);
		mainFrame=(FrameLayout)findViewById(R.id.command_list_frame);
		EmbeddedView= new CardBuilder(this, CardBuilder.Layout.EMBED_INSIDE)
	    .setEmbeddedLayout(R.layout.voice_commands_list_view)
	    .setFootnote("Say Command")
	    .getView();
		mainFrame.addView(EmbeddedView);
		camera_label=(TextView)findViewById(R.id.camera_label);
		search_label=(TextView)findViewById(R.id.search_label);
		finish_label=(TextView)findViewById(R.id.finish_label);
		//tv=(TextView)findViewById(R.id.command_text);
		CommandList=new String[]{"camera","google","finish"};
		startService(new Intent(this,VoiceListenerService.class));
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initTextView();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onStop");
		super.onStop();
	//	mVoiceCommandListener.unBindService();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		stopService(new Intent(this,VoiceListenerService.class));
	}
	private void initTextView()
	{
		camera_label.setTextColor(Color.WHITE);
		search_label.setTextColor(Color.WHITE);
		finish_label.setTextColor(Color.WHITE);
	}
	@Override
	public void onBeginSpeech() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(warningView !=null)
				{
					warningView.setVisibility(View.GONE);
					mainFrame.removeView(warningView);
					mainFrame=null;
				}
			}
		});
	}
	@Override
	public void onVoiceCommand(String command) {
		final String cmd=command;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
					warningView=WarningDialog.getInstance().setLayout(VoiceCommandListActivity.this,mainFrame)
										.setText("Command Detection Error", "Detected Command : "+cmd).build();
			}
		});
	}
	@Override
	public void onVoiceCommand(int cmdId) {
		// TODO Auto-generated method stub
		final int id = cmdId;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(id == 0) // camera
				{
					camera_label.setTextColor(Color.BLUE);
					UnBindService();
					Intent intent=IntentBuilder.CreateIntent(VoiceCommandListActivity.this, CameraActivity.class).build();
					IntentBuilder.startActivity(VoiceCommandListActivity.this, intent);
				}
				else if(id == 1)
				{
					search_label.setTextColor(Color.BLUE);
					UnBindService();
					Intent intent=new Intent("com.google.glass.action.START_VOICE_SEARCH_ACTIVITY");
					startActivity(intent);
				}
				else if(id == 2)
				{
					finish_label.setTextColor(Color.BLUE);
					UnBindService();
				//	tv.setText(cmd);
					VoiceCommandListActivity.this.finish();
				}
			}
		});
	}

}
