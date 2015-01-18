package com.example.util;

import android.R.anim;
import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.FrameLayout;

import com.example.launcher.R;
import com.google.android.glass.widget.CardBuilder;

public class WarningDialog {
	private static WarningDialog instance=null;
	private Context context;
	private String text;
	private String footnote;
	private FrameLayout framelayout;
	
	public static WarningDialog getInstance() {
		if (instance == null) {
			instance = new WarningDialog();
		}
		return instance;
	}
	public WarningDialog setLayout(Context context,FrameLayout framelayout)
	{
		this.context=context;
		this.framelayout=framelayout;
		return this;
	}
	public WarningDialog setText(String text,String footnote)
	{
		this.text=text;
		this.footnote=footnote;
		return this;
	}
	public View build()
	{
		View localView = new CardBuilder(context,
				CardBuilder.Layout.ALERT)
				.setText(text)
				.setIcon(((BitmapDrawable)context.getResources().getDrawable(R.drawable.ic_warning_150)).getBitmap())
				.setFootnote(footnote).getView();
		localView.setAlpha(0.8f);
		localView.setBackgroundColor(Color.BLACK);
		framelayout.addView(localView);
		framelayout.bringChildToFront(localView);
		return localView;
	}
}
