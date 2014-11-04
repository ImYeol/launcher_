package com.example.launcher;

public class State {

	public final static float displayedPlace= -10.f;
	public final static float disspearedPlace = 10.f;
	
	private float destination_z;
	public boolean IsLoading;
	private boolean Current;
	private boolean completed;
	
	public State(){
		
		this.destination_z=displayedPlace;
		IsLoading=false;
		Current=false;
		completed=false;
	}
	public boolean IsCompleted()
	{
		return completed;
	}
	public void setCompleted(boolean completed)
	{
		this.completed=completed;
	}
	public void setDissapearedZ()
	{
		destination_z=disspearedPlace;
	}
	public void setDisplayedZ()
	{
		destination_z=disspearedPlace;
	}
	public float getZ()
	{
		return destination_z;
	}
	public void setCurrent(boolean cur)
	{
		Current=cur;
	}
	public boolean getCurrent()
	{
		return Current;
	}
	
}
