package com.example.Camera;

import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.Companion.ImageTransferHelper;
import com.example.Voice.VoiceActivity;
import com.example.Voice.VoiceCommand;
import com.example.launcher.R;
import com.example.util.IntentBuilder;
import com.example.util.WarningDialog;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;

public class ImageCommentDialog extends VoiceActivity {

	public static final String ImageCommentText="imageComment";
	public static final int ImageCommentResult=1000;
	private GestureDetector mGestureDetector;
	private final String TAG="ImageCommentDialog";
	private TextView commentView;
	private String Uri;
	private String comment;
	private boolean IsOnDialog=false;
	private View endOfSpeechDialog;
	private boolean IsCommandRecognized=false;
	private FrameLayout framelayout;
	private String preString="";
	private boolean IsOnCounting=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_dialog);
		commentView=(TextView)findViewById(R.id.comment_text);
		commentView.setText("");
		framelayout=(FrameLayout)findViewById(R.id.comment_layout);
		Uri=getIntent().getExtras().getString("Uri");
		CommandList=new String[]{"yes","No","clear","finish"};
		mGestureDetector=createGestureDetector(this);
	}
	
	@Override
	public void onVoiceCommand(String command) {
		// TODO Auto-generated method stub
		final String cmd=preString+" "+command;
		if(IsOnDialog == false)
		{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					commentView.setText(cmd);
				}
			});
		}
	}
	@Override
	public void onVoiceCommand(int cmdId) {
		// TODO Auto-generated method stub
		if (IsCommandRecognized == false) {
			final int id=cmdId;
			if (id == 0) {    // yes
				IsCommandRecognized=true;
				IsOnDialog = false;
				ReSetCommands();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						comment = commentView.getText().toString();
						endOfSpeechDialog.setVisibility(View.GONE);
					}
				});
				Intent intent = IntentBuilder
						.CreateIntent(this, ImageTransferHelper.class)
						.setUri(Uri).setComment(comment).build();
				IntentBuilder.startActivityForResult(this, intent);
				
			} else if (id == 1) {   // No
				IsCommandRecognized=true;
				IsOnDialog = false;
				preString=commentView.getText().toString();
				ReSetCommands();
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						endOfSpeechDialog.setVisibility(View.GONE);
					}
				});
			//	ReStartListening();
				Log.d(TAG, "No called");
			}
			else if(id == 2)   // clear
			{
				IsCommandRecognized=true;
				IsOnDialog = false;
				preString="";
				ReSetCommands();
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						endOfSpeechDialog.setVisibility(View.GONE);
						commentView.setText("");
					}
				});
			}
			else if(id == 3)  // finish
			{
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						endOfSpeechDialog.setVisibility(View.GONE);
					}
				});
				UnBindService();
				setResult(RESULT_OK);
				finish();
			}
		}
	}
	@Override
	public void onBeginSpeech() {
		if(IsOnCounting){
			runOnUiThread(new Runnable() {		
				@Override
				public void run() {
					// TODO Auto-generated method stub
					IsOnCounting=false;
					mNoSpeechCountDown.cancel();
				}
			});
		}
	}
	@Override
	public void onResultOfSpeech() {
		// TODO Auto-generated method stub
		if (IsOnDialog == false) {
			runOnUiThread(new Runnable() {		
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(IsOnCounting)
						mNoSpeechCountDown.cancel();
					IsOnCounting=true;
					mNoSpeechCountDown.start();
					preString=commentView.getText().toString();
				}
			});
		}
	}

	// Count down timer for Jelly Bean work around
	protected CountDownTimer mNoSpeechCountDown = new CountDownTimer(3000, 3000) {
		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFinish() {
			Log.d(TAG, "timer triggered");
			if (endOfSpeechDialog == null) {
				endOfSpeechDialog = WarningDialog
						.getInstance()
						.setLayout(ImageCommentDialog.this, framelayout)
						.setText("You want to send message?",
								"Yes : No : Clear : Finish").build();
			} else
				endOfSpeechDialog.setVisibility(View.VISIBLE);
			setCommands();
			IsOnDialog = true;
			IsCommandRecognized = false;
			IsOnCounting=false;
		}
	};
	@Override
	protected VoiceCommand getVoiceCommand() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setCommands() {
		// TODO Auto-generated method stub
		if(voiceCommand == null)
			voiceCommand=new VoiceCommand(CommandList);
		super.setCommands();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK)
		{
			UnBindService();
			setResult(RESULT_OK);
			finish();
		}
	}
	private GestureDetector createGestureDetector(Context context) {
		GestureDetector gestureDetector = new GestureDetector(context);
		// Create a base listener for generic gestures
		gestureDetector.setBaseListener(new GestureDetector.BaseListener() {

			@Override
			public boolean onGesture(Gesture gesture) {
				// TODO Auto-generated method stub
				if (gesture == Gesture.SWIPE_DOWN) {
					setResult(RESULT_OK);
					finish();
				}
				return false;
			}

		});
		return gestureDetector;
	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return mGestureDetector.onMotionEvent(event);
	}

}
