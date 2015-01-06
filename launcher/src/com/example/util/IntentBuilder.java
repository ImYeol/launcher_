package com.example.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class IntentBuilder {
	
	public final static int reqCode=0;
	private static IntentBuilder builder=new IntentBuilder();
	private static Context context;
	private static Class<?> cls;
	private static String CacheKey;
	private static String Uri;
	
	public static IntentBuilder CreateIntent(Context context,Class<?> cls)
	{
		 builder.context=context;
		 builder.cls=cls;
		 return builder;
	}
	public static IntentBuilder setCacheKey(String key)
	{
		builder.CacheKey=key;
		return builder;
	}
	public static IntentBuilder setUri(String uri)
	{
		builder.Uri=uri;
		return builder;
	}
	public Intent build()
	{
		Intent intent=new Intent(context,cls);
		intent.putExtra("CacheKey", CacheKey);
		intent.putExtra("Uri", Uri);
		return intent;
	}
	public static void startActivityForResult(Context context,Intent intent)
	{
		((Activity)context).startActivityForResult(intent, reqCode);
	}
	public static void startActivity(Context context,Intent intent)
	{
		context.startActivity(intent);
	}
}
