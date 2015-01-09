package com.example.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class IntentBuilder {
	
	public final static int reqCode=0;
	private static IntentBuilder builder=new IntentBuilder();
	private Context context;
	private Class<?> cls;
	private String CacheKey;
	private String Uri;
	private String comment;
	
	public static IntentBuilder CreateIntent(Context context,Class<?> cls)
	{
		 builder.context=context;
		 builder.cls=cls;
		 builder.CacheKey=null;
		 builder.Uri=null;
		 builder.comment=null;
		 
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
	public static IntentBuilder setComment(String comment)
	{
		builder.comment=comment;
		return builder;
	}
	public Intent build()
	{
		Intent intent=new Intent(context,cls);
		intent.putExtra("CacheKey", CacheKey);
		intent.putExtra("Uri", Uri);
		intent.putExtra("comment", comment);
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
