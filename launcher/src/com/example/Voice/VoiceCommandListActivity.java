package com.example.Voice;

import java.util.Arrays;
import java.util.List;

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

	private VoiceCommand cmd;
	private List<String> cmdList=Arrays.asList("camera","finish");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voice_commands_list_layout);
		tv=(TextView)findViewById(R.id.command_text);
		cmd=new VoiceCommand(cmdList);
		setCommands(cmd);
	//	startService(new Intent(this,VoiceListenerService.class));
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
		tv.setText("");
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onStop");
		super.onStop();
	//	mVoiceCommandListener.unBindService();
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
	//	runOnUiThread(new Runnable() {
			
//			@Override
//			public void run() {
				// TODO Auto-generated method stub
				if(cmd.contains("camera"))
				{
					IsCommandRecognized=true;
					tv.setText(cmd);
					//mSpeechRecognizer.destroy();
					//mSpeechRecognizer = null;
					StopListening();
					//stoplisten();
					Log.d(TAG, "onVoice: "+command);
					Intent intent=IntentBuilder.CreateIntent(VoiceCommandListActivity.this, CameraActivity.class).build();
					IntentBuilder.startActivity(VoiceCommandListActivity.this, intent);
				}
				else if(cmd.contains("finish"))
				{
					StopListening();
					IsCommandRecognized=true;
					tv.setText(cmd);
					VoiceCommandListActivity.this.finish();
				}
				else
				{
					tv.setText(cmd);
				}
//			}
//		});
	}

}
