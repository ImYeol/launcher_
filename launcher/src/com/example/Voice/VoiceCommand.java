package com.example.Voice;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class VoiceCommand implements Parcelable{

	public List<String> commands;
	
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
	public VoiceCommand()
	{
		commands=new ArrayList<String>();
	}
	public VoiceCommand(Parcel in)
	{
		readFromParcel(in);
	}
	private void readFromParcel(Parcel in) {
		// TODO Auto-generated method stub
		in.readStringList(commands);
	}
	public VoiceCommand(List<String> commands)
	{
		this.commands=commands;
	}
	
	public List<String> getCommandList()
	{
		return this.commands;
	}
	
	public boolean contains(String recognizedCommand)
	{
		for(String command : commands)
		{
			if(recognizedCommand.contains(command))
				return true;
		}
		return false;
	}
	public void add(String command)
	{
		commands.add(command);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeStringList(commands);
	}
}
