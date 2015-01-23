package com.example.Voice;

import com.example.Voice.VoiceCommand;
import com.example.Voice.IVoiceListenerCallback;

interface IVoiceListenerService{
	void setCommands(in VoiceCommand commands);
	
	void ReSetCommands();
	
	void registerCallback(in IVoiceListenerCallback callback);
	
	void unRegisterCallback();
	
	void turnOffVoiceRecognize();
	
	void turnOnVoiceRecognize();
}