package com.example.glasstest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ApplicationsIntentReceiver extends BroadcastReceiver {

	private GLRenderer renderer;

	public ApplicationsIntentReceiver(GLRenderer renderer) {
		// TODO Auto-generated constructor stub
		this.renderer=renderer;
	}
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
			renderer.loadApplications(false);
	}

}
