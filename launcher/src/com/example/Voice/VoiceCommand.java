package com.example.Voice;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class VoiceCommand implements Parcelable{

	public String[] commands;
	
	public static final Parcelable.Creator<VoiceCommand> CREATOR
    = new Parcelable.Creator<VoiceCommand>() {
         public VoiceCommand createFromParcel(Parcel in){
              return new VoiceCommand(in);
         }

         @Override
         public VoiceCommand[] newArray(int size) {
              return new VoiceCommand[size];
         }
    };

	public VoiceCommand(Parcel in)
	{
		readFromParcel(in);
	}
	private void readFromParcel(Parcel in) {
		// TODO Auto-generated method stub
		commands=new String[in.readInt()];
		in.readStringArray(commands);
	}
	public VoiceCommand(String[] commands)
	{
		this.commands=commands;
	}
	
	public String[] getCommandList()
	{
		return this.commands;
	}
	
	public int contains(String recognizedCommand)
	{
		for(int i=0; i < commands.length; i++)
		{
			if(recognizedCommand.equals(commands[i]))
				return i;
		}
		return -1;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(commands.length);
		dest.writeStringArray(commands);
	}
}
