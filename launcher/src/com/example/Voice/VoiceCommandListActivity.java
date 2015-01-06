package com.example.Voice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.launcher.R;
import com.example.util.IntentBuilder;

public class VoiceCommandListActivity extends VoiceActivity {

	private static final String TAG = "VoiceCommandListActivity";
	private TextView tv;

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
		mVoiceCommandListener.BindService();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mVoiceCommandListener.unBindService();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
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
		if(command.contains("take a picture"))
		{
			tv.setText(command);
			Intent intent=IntentBuilder.CreateIntent(this, ImageView.class).build();
			IntentBuilder.startActivity(this, intent);
		}
		else if(command.contains("finish"))
		{
			tv.setText(command);
			this.finish();
		}
		else
		{
			tv.setText(command);
		}
	}

}
