package com.example.Voice;

import com.example.Voice.VoiceCommand;
import com.example.Voice.VoiceListener;

interface IVoiceListenerService{
	void setCommands(in VoiceCommand commands);
	
	void ReSetCommands();
	
	void registerCallback(in VoiceListener callback);
	
	void unRegisterCallback();
}