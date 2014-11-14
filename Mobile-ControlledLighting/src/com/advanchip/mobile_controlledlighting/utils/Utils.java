package com.advanchip.mobile_controlledlighting.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Utils {
	public static final String TAG = "Mobile-ControlledLighting";
	
	public static void saveLoginInfo(Context c, JSONObject json) throws JSONException {

		SharedPrefUtils.setLoggedIn(c, true);
		SharedPrefUtils.setUserId(c, json.getString("user_id"));
		SharedPrefUtils.setRefreshToken(c, json.getString("refresh_token"));
		SharedPrefUtils.setToken(c, json.getString("token_value"));
		SharedPrefUtils.setTokenType(c, json.getString("token_type"));
		SharedPrefUtils.setUserEmail(c, json.getString("user_email"));
		
	}
	
	// This method is for debugging only
	public static void seeLoginInfo(Context c) {
		SharedPrefUtils.seeMyPreferences(c);
	}
	
	public static void removeLoginInfo(Context c) {
		SharedPrefUtils.removePreferences(c);
	}
	
	public static void logout(Context c) {

		SharedPrefUtils.setLoggedIn(c, false);
		
	}
	
	 public static boolean haveNetworkConnection(Context c) {
	        boolean haveConnectedWifi = false;
	        boolean haveConnectedMobile = false;
	        

	        try {
				ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo[] netInfo = cm.getAllNetworkInfo();
				for (NetworkInfo ni : netInfo) {
				    if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				        if (ni.isConnected())
				            haveConnectedWifi = true;
				    if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				        if (ni.isConnected())
				            haveConnectedMobile = true;
				}
			} catch(NullPointerException e){
				Log.e(TAG, "null pointer exception in the internet connection check. The context is "+c);
				return true;
			}
	        return haveConnectedWifi || haveConnectedMobile;
	        
	    }
	
	public static boolean isEmailValid(String email) {
		String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		return email.matches(EMAIL_REGEX);
	}

	public static boolean isPasswordValid(String password) {
		return password.length() > 4;
	}
	
	public static JSONObject getJsonObjectFromMap(List<NameValuePair> parameters)
			throws JSONException {

		// Stores JSON
		JSONObject holder = new JSONObject();

		// using the earlier example your first entry would get email
		// and the inner while would get the value which would be 'foo@bar.com'
		// { fan: { email : 'foo@bar.com' } }

		// While there is another entry

		for (NameValuePair param : parameters) {
			Log.d("Params",
					"Params:" + param.getName() + ":" + param.getValue());
			holder.put(param.getName(), param.getValue());
		}

		return holder;
	}

	public static String stringifyResponse(HttpResponse response) {
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));

			StringBuffer sb = new StringBuffer("");
			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();

			return sb.toString();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	public static void displayDialog(Context context, String title, String message ){
		
		new AlertDialog.Builder(context)
	    .setTitle(title)
	    .setMessage(message)
	    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	dialog.dismiss();
	        }
	     })
	    .setIcon(android.R.drawable.ic_dialog_alert)
	     .show();
	}

}
