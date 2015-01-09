package com.example.Voice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.Camera.CameraActivity;
import com.example.launcher.R;
import com.example.util.IntentBuilder;

public class VoiceCommandListActivity extends VoiceActivity {

	private static final String TAG = "VoiceCommandListActivity";
	private TextView tv;
	private boolean IsCommandRecognized=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voice_commands_list_layout);
		tv=(TextView)findViewById(R.id.command_text);
		startService(new Intent(this,VoiceListenerService.class));
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		IsCommandRecognized=false;
	//	startService(new Intent(this,VoiceListenerService.class));
	//	startService(new Intent(IVoiceListenerService.class));
	//	mVoiceCommandListener.BindService();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
/*	public void setCommands()
	{
		VoiceCommand voiceCommand=new VoiceCommand();
		voiceCommand.add("take a picture");
		voiceCommand.add("finish");
		mVoiceCommandListener.setCommands(voiceCommand);
	} */
	
	@Override
	public void onVoiceCommand(String command) {
		
		if(IsCommandRecognized)
			return ;
		
		final String cmd=command;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(cmd.contains("take a picture"))
				{
					IsCommandRecognized=true;
					tv.setText(cmd);
					Intent intent=IntentBuilder.CreateIntent(VoiceCommandListActivity.this, CameraActivity.class).build();
					IntentBuilder.startActivity(VoiceCommandListActivity.this, intent);
				}
				else if(cmd.contains("finish"))
				{
					IsCommandRecognized=true;
					tv.setText(cmd);
					VoiceCommandListActivity.this.finish();
				}
				else
				{
					tv.setText(cmd);
				}
			}
		});
	}

}
