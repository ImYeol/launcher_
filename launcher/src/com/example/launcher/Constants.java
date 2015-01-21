package com.example.launcher;

public class Constants {
	public final static float displayedPlace= -40.f;
	public final static float disapearedPlace = 10.f;
	public final static float originPlace= -90.f;
	public final static float speed=5.f;
	public final static float anim_speed=10.f;
	public final static float alphaDownRegion=disapearedPlace-displayedPlace;
	public final static float threshold = (disapearedPlace - originPlace)/5;
	public static final float SCALE=0.01f;
	public final static int NoSwipe=0;
	public final static int BackwardSwipe=1;
	public final static int ForwardSwipe=2;
	
	public final static int Scrolling=1000;
	public final static int Swipe=1001;
	public final static int NoState=1002;
	
	public final static float THRESHOLD=10.f;
	public final static float STD_VELOCITY=20;
	public static final int SNAP_VELOCITY=2000;
	
	public static final int TO_BACKWARD_CENTER=100;
	public static final int TO_FORWARD_CENTER=101;
	public static final int TO_FRONT=102;
	public static final int TO_BACK=103;
	public static final int NO_DESTINATION=104;
	
	public static final String VoiceCommandAction="com.example.launcher.VOICE_COMMAND_ACTION";
}
