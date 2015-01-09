package com.example.Voice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.BadTokenException;
import android.widget.ProgressBar;

import com.example.launcher.R;

public class VoiceActivity extends Activity{

	private final static String TAG="VoiceAcitivity";
	protected VoiceCommandListener mVoiceCommandListener;
	ProgressDialog voiceRecoginitionStateDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SetOnVoiceCommandListener();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	//	mVoiceCommandListener.BindService();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mVoiceCommandListener.BindService();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mVoiceCommandListener.unBindService();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	//	mVoiceCommandListener.unBindService();
	}
	
	protected void SetOnVoiceCommandListener() {
		mVoiceCommandListener=new VoiceCommandListener(this);
	}
	
	public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
                dialog.show();
        } catch (BadTokenException e) {

		}
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.voice_recognition_state_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		// dialog.setMessage(Message);
		return dialog;
	}

	public void onBeginSpeech() {
	/*	runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (voiceRecoginitionStateDialog == null) {
					voiceRecoginitionStateDialog = VoiceActivity.this.createProgressDialog(VoiceActivity.this);
					voiceRecoginitionStateDialog.show();
					ProgressBar progressStateBar=(ProgressBar)voiceRecoginitionStateDialog.findViewById(R.id.recognition_state_progressbar);
					progressStateBar.setVisibility(View.VISIBLE);
				} else {
					voiceRecoginitionStateDialog.show();
				}
			}
		});*/
	}

	public void onEndOfSpeech() {
	/*	runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (voiceRecoginitionStateDialog.isShowing())
				{
				//	ProgressBar progressStateBar=(ProgressBar)findViewById(R.id.recognition_state_progressbar);
				//	progressStateBar.setVisibility(View.GONE);
					ProgressBar progressStateBar=(ProgressBar)voiceRecoginitionStateDialog.findViewById(R.id.recognition_state_progressbar);
					progressStateBar.setVisibility(View.GONE);
					voiceRecoginitionStateDialog.dismiss();
				}
			}
		});*/
	}
	public void onVoiceCommand(String command)
	{
		Log.d(TAG, "Sub Class should implement onVoiceCommand");
	}
	public void setCommands()
	{
		Log.d(TAG, "Sub Class should implement setCommands");
	}
}
