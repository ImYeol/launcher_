package com.example.Voice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager.BadTokenException;

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
		mVoiceCommandListener.BindService();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mVoiceCommandListener.unBindService();
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
		// dialog.setMessage(Message);
		return dialog;
	}

	public void onBeginSpeech() {
/*		if (voiceRecoginitionStateDialog == null) {
			voiceRecoginitionStateDialog = createProgressDialog(this);
			voiceRecoginitionStateDialog.show();
		} else {
			voiceRecoginitionStateDialog.show();
		}*/
	}

	public void onEndOfSpeech() {
		/*if (voiceRecoginitionStateDialog.isShowing())
			voiceRecoginitionStateDialog.dismiss();*/
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
