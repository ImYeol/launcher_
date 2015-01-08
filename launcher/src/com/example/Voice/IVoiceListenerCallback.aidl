package com.example.Voice;


interface IVoiceListenerCallback{

	 void onBeginSpeech();
	
	 void onEndOfSpeech();
	
	 void onVoiceCommand(String command);
}