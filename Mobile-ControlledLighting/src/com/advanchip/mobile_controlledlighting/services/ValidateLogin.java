package com.advanchip.mobile_controlledlighting.services;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.advanchip.mobile_controlledlighting.MenuActivity;
import com.advanchip.mobile_controlledlighting.utils.Constants;
import com.advanchip.mobile_controlledlighting.utils.HTTPPostRequest;
import com.advanchip.mobile_controlledlighting.utils.Utils;

public class ValidateLogin extends AsyncTask<List<NameValuePair>, String, String> {
    private Context context;
    ProgressDialog dialog;
    private static final String TAG = ":ValidateLogin";
    private Activity activity;
    

        public ValidateLogin(Context cxt) {
            context = cxt;
            dialog = new ProgressDialog(context);
            activity = (Activity) context;
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle("Signing In...");
            dialog.show();
        }

        @Override
        protected String doInBackground(List<NameValuePair>...input) {
        		
        	
			HTTPPostRequest loginRequest = new HTTPPostRequest();
			

			String result = loginRequest.executeHTTPPost(input[0],
					Constants.URL_LOGIN, activity);
		
			return result;
        }

        @Override
        protected void onPostExecute(String returned) {
        	Log.d(TAG,returned);
            dialog.dismiss();
            
            if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			//JSONObject responceJson = new JSONObject();
			JSONObject json = null;
			try {
				json = new JSONObject(returned);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			if (json == null) {
				Utils.displayDialog(activity, "Error in Login",
						"Failed to get server response");
			} else {
				if (json.has("status")) {
					try {
						if(json.getInt("status") == 200){
							Utils.saveLoginInfo(
									activity.getApplicationContext(), json);
							Intent menuIntent = new Intent(activity, MenuActivity.class);
							context.startActivity(menuIntent);
						} else {
							Utils.displayDialog(activity, "Error in Login",
									json.getString("message"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
		
				} else {
					Utils.displayDialog(activity, "Error in Login",
							"Failed to get server response");
				}
	
			}
            
        }
    }
