package com.example.Voice;

public interface VoiceListener {

	public void onVoiceCommand(String command);
	
	public boolean BindService();
	
	public void unBindService();
	
	public void onBeginSpeech();
	
	public void onEndOfSpeech();
	
}
