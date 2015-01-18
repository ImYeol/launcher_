package com.example.Voice;


interface IVoiceListenerCallback{

	 void onBeginSpeech();
	
	 void onEndOfSpeech();
	 
	 void onResultOfSpeech();
	 
	 void onNotCommandError();
	
	 void onVoiceCommand(String command);
	 
	 void onVoiceCommand_int(int cmdId);
}