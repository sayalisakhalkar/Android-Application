package com.advanchip.mobile_controlledlighting.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPrefUtils {
	
	public static final String PREFS_NAME = "AdvanchipPrefsFile";
	public static final String TAG = ":SharedPrefUtils";
	
	public static final String KEY_TOKEN_VALUE = "KEY_TOKEN_VALUE";
	public static final String KEY_TOKEN_TYPE = "KEY_TOKEN_TYPE";
	public static final String KEY_REFRESH_TOKEN = "KEY_REFRESH_TOKEN";
	public static final String KEY_USER_ID = "KEY_USER_ID";
	public static final String KEY_LOGGED_IN = "KEY_LOGGED_IN";
	public static final String KEY_USER_EMAIL = "KEY_USER_EMAIL";

	
	public static SharedPreferences sp = null;
	
	public static void setLoggedIn(Context c, Boolean val){
		sp = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(KEY_LOGGED_IN, val);

		// Commit the edits!
		editor.commit();
	}

	public static boolean getLoggedIn(Context c){
		sp = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return sp.getBoolean(KEY_LOGGED_IN, false);
	}
	
	public static void setUserId(Context c, String val){
		sp = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(KEY_USER_ID, val);
		// Commit the edits!
		editor.commit();
	}

	public static String getUserId(Context c){
		sp = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return sp.getString(KEY_USER_ID, null);
	}
	
	public static void setToken(Context c, String val){
		sp = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(KEY_TOKEN_VALUE, val);

		// Commit the edits!
		editor.commit();
	}

	public static String getToken(Context c){
		sp = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return sp.getString(KEY_TOKEN_VALUE, null);
	}
	
	public static void setTokenType(Context c, String val){
		sp = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(KEY_TOKEN_TYPE, val);

		// Commit the edits!
		editor.commit();
	}

	public static String getTokenType(Context c){
		sp = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return sp.getString(KEY_TOKEN_TYPE, null);
	}
	
	public static void setRefreshToken(Context c, String val){
		sp = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(KEY_REFRESH_TOKEN, val);

		// Commit the edits!
		editor.commit();
	}

	public static String getRefreshToken(Context c){
		sp = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return sp.getString(KEY_REFRESH_TOKEN, null);
	}
	
	public static void seeMyPreferences(Context c){
		sp = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Log.d(TAG,sp.getString(KEY_TOKEN_TYPE,null));
		Log.d(TAG,sp.getString(KEY_REFRESH_TOKEN,null));
		Log.d(TAG,sp.getString(KEY_TOKEN_VALUE,null));
		Log.d(TAG,sp.getString(KEY_LOGGED_IN,null));
		Log.d(TAG,sp.getString(KEY_USER_ID,null));
	}
	
	public static void removePreferences(Context c){
		sp = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
	    editor.clear();
	    editor.commit(); 
	}

	public static void setUserEmail(Context c, String val) {
		// TODO Auto-generated method stub
		sp = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(KEY_USER_EMAIL, val);

		// Commit the edits!
		editor.commit();
		
	}
	
	public static String getUserEmail(Context c){
		sp = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return sp.getString(KEY_USER_EMAIL, null);
	}

}
