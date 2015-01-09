package com.example.Camera;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.TextView;

import com.example.Companion.ImageTransferHelper;
import com.example.Voice.VoiceActivity;
import com.example.launcher.R;
import com.example.util.IntentBuilder;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

public class ImageCommentDialog extends VoiceActivity {

	public static final String ImageCommentText="imageComment";
	public static final int ImageCommentResult=1000;
	private GestureDetector mGestureDetector;
	private final String TAG="ImageCommentDialog";
	private TextView commentView;
	private String Uri;
	private String comment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_dialog);
		mGestureDetector=createGestureDetector(this);
		commentView=(TextView)findViewById(R.id.comment_text);
		commentView.setText("");
		Uri=getIntent().getExtras().getString("Uri");
	}
	@Override
	public void onVoiceCommand(String command) {
		// TODO Auto-generated method stub
		Log.d(TAG, command);
		final String cmd=command;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				commentView.setText(cmd);
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		//super.onCreateOptionsMenu(menu);
		menu.add("Ok");
		menu.add("Cancel");
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getTitle().toString().equals("Ok"))
		{
			comment=commentView.getText().toString();
			Intent intent=IntentBuilder.CreateIntent(this, ImageTransferHelper.class).setUri(Uri)
															.setComment(comment).build();
			IntentBuilder.startActivityForResult(this, intent);
		}
		else if(item.getTitle().toString().equals("Cancel"))
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
