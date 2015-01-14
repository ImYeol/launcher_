package com.example.Camera;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
	private FrameLayout framelayout;
	private boolean IsOnDialog=false;
	private View endOfSpeechDialog;
	private boolean IsCommandRecognized=false;
	private VoiceCommand dialogCmd;
	private List<String> dialogMenu=Arrays.asList("yes","No");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_dialog);
		framelayout=(FrameLayout)findViewById(R.id.comment_layout);
		mGestureDetector=createGestureDetector(this);
		commentView=(TextView)findViewById(R.id.comment_text);
		commentView.setText("");
		Uri=getIntent().getExtras().getString("Uri");
		dialogCmd=new VoiceCommand(dialogMenu);
	}

	@Override
	public void onResultOfSpeech() {
		// TODO Auto-generated method stub
		if (IsOnDialog == false) {
			if (endOfSpeechDialog == null) {
				endOfSpeechDialog = new CardBuilder(this.getBaseContext(),
						CardBuilder.Layout.ALERT)
						.setText("You want to send message?")
						.setFootnote("YES  :   NO").getView();
				framelayout.addView(endOfSpeechDialog);
				framelayout.bringChildToFront(endOfSpeechDialog);
			}
			else
				endOfSpeechDialog.setVisibility(View.VISIBLE);
			setCommands(dialogCmd);
			IsOnDialog = true;
			IsCommandRecognized=false;
		}
	}
	@Override
	public void onVoiceCommand(String command) {
		// TODO Auto-generated method stub
		if(IsOnDialog == false)
		{
			commentView.setText(command);
			Log.d(TAG, "onVoiceCommand: "+command);
		}
		else {
			if (IsCommandRecognized == false) {
				if (command.contains("yes")) {
					IsCommandRecognized=true;
					IsOnDialog = false;
					comment = commentView.getText().toString();
					Intent intent = IntentBuilder
							.CreateIntent(this, ImageTransferHelper.class)
							.setUri(Uri).setComment(comment).build();
					IntentBuilder.startActivityForResult(this, intent);
					ReSetCommands();
					
				} else if (command.contains("No")) {
					IsCommandRecognized=true;
					endOfSpeechDialog.setVisibility(View.GONE);
					IsOnDialog = false;
					ReSetCommands();
					ReStartListening();
					Log.d(TAG, "No called");
				}
			}
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setCommands(null);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		//super.onCreateOptionsMenu(menu);
		menu.add("Yes");
		menu.add("No");
		return true;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK)
		{
			setResult(RESULT_OK);
			finish();
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId() ==0)
		{
			comment=commentView.getText().toString();
			Intent intent=IntentBuilder.CreateIntent(this, ImageTransferHelper.class).setUri(Uri)
															.setComment(comment).build();
			IntentBuilder.startActivityForResult(this, intent);
		}
		else if(item.getItemId() ==1)
		{
			setResult(RESULT_OK);
			finish();
		}
		return true;
	}
	private GestureDetector createGestureDetector(Context context) {
		GestureDetector gestureDetector = new GestureDetector(context);
		// Create a base listener for generic gestures
		gestureDetector.setBaseListener(new GestureDetector.BaseListener() {

			@Override
			public boolean onGesture(Gesture gesture) {
				// TODO Auto-generated method stub
				if (gesture == Gesture.TAP) {
					openOptionsMenu();
				}
				if (gesture == Gesture.SWIPE_DOWN)
				{
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
		Log.d(TAG, "generic Event");
		return mGestureDetector.onMotionEvent(event);
	}
}
