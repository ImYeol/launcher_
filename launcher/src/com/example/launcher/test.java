package com.example.launcher;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;

public class test extends Activity {

	Intent intent;
	ImageView v1;
	ImageView v2;
	ImageView v3;
	ImageView v4;
	ImageView v5;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hhh);
		
		PackageManager manager = getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        v1=(ImageView)findViewById(R.id.imageView1);
        v2=(ImageView)findViewById(R.id.imageView2);
        v3=(ImageView)findViewById(R.id.imageView3);
        v4=(ImageView)findViewById(R.id.imageView4);
        v5=(ImageView)findViewById(R.id.imageView5);
       final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        
        for(int i=0;i<5;i++)
        {
        	BitmapDrawable b=(BitmapDrawable)apps.get(i).loadIcon(manager);
        	v1.setImageBitmap(b.getBitmap());
        }
		
	}
}
