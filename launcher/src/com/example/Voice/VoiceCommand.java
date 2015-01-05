package com.example.Voice;

import java.util.ArrayList;
import java.util.List;

public class VoiceCommand {

	private List<String> commands;
	
	public VoiceCommand()
	{
		commands=new ArrayList<String>();
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
}
