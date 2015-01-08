package com.example.Voice;

import android.os.Parcelable;

public interface VoiceListener extends Parcelable{
	
	public void onVoiceCommand(String command);
	
	public boolean BindService();
	
	public void unBindService();
	
	public void onBeginSpeech();
	
	public void onEndOfSpeech();
	
}
