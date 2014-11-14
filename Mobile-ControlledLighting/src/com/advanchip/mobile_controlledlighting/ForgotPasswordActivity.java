package com.advanchip.mobile_controlledlighting;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.advanchip.mobile_controlledlighting.utils.Constants;
import com.advanchip.mobile_controlledlighting.utils.HTTPPostRequest;
import com.advanchip.mobile_controlledlighting.utils.Utils;

public class ForgotPasswordActivity extends Activity {

	private EditText passwordEmail;
	private String email;
	private Button submitForgotEmail;
	public Activity activity;
	ProgressDialog pDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);
		activity = this;
		passwordEmail= (EditText)findViewById(R.id.email);
		submitForgotEmail = (Button)findViewById(R.id.submit_forgot_password);
		
	
		 
			submitForgotEmail .setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					   email = passwordEmail.getText().toString();
					   boolean cancel = false;
					// Check for a valid email address.
						if (TextUtils.isEmpty(email)) {
							passwordEmail.setError(getString(R.string.error_field_required));
							cancel = true;
						} else if (!Utils.isEmailValid(email)) {
							passwordEmail.setError(getString(R.string.error_invalid_email));
							cancel = true;
						}

						if (cancel) {
							// There was an error; don't attempt login and focus the first
							// form field with an error.
							passwordEmail.requestFocus();
						} else {
							// Show a progress spinner, and kick off a background task to
							// perform the user login attempt.
							new ForgotPassword().execute(email);
						}
					   
					   
				     
				}
				 
				});
			
	}
	
	
	/**
	 * Represents an asynchronous ForgotPassword task used to authenticate
	 * the user.
	 */
	public class ForgotPassword extends AsyncTask<String, Void, String> {
	

		@Override
		protected String doInBackground(String... params) {
			// create a list to store HTTP variables and their values
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	                // add an HTTP variable and value pair
			nameValuePairs.add(new BasicNameValuePair("email", email));
			
			HTTPPostRequest request = new HTTPPostRequest();
			String result = request.executeHTTPPost(nameValuePairs,
					Constants.URL_FORGOT_PASSWORD, activity);
			
			
			/*HttpClient httpClient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.URL_FORGOT_PASSWORD);
			try {
				// create a list to store HTTP variables and their values
				List nameValuePairs = new ArrayList();
		                // add an HTTP variable and value pair
				nameValuePairs.add(new BasicNameValuePair("email", email));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		                // send the variable and value, in other words post, to the URL
				HttpResponse response = httpClient.execute(httppost);
			} catch (ClientProtocolException e) {
				// process execption
			} catch (IOException e) {
				// process execption
			}*/

			return result;
		}

		@Override
		protected void onPostExecute(String response) {
			// Dismiss the dialog after the request completed
			if (pDialog != null && pDialog.isShowing()) {
				pDialog.dismiss();
			}
			
			JSONObject json = null;
			try {
				json = new JSONObject(response);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			if (json == null) {
				Utils.displayDialog(activity, "Error",
						"Failed to get server response");
			} else {
				if (json.has("status")) {
					try {
						if(json.getInt("status") == 200){
							Toast.makeText(activity.getApplicationContext(),
									"Check your email for password", Toast.LENGTH_LONG).show();
							Intent resetPasswordIntent = new Intent(activity, ResetPasswordActivity.class);
							startActivity(resetPasswordIntent);
							activity.finish();
						} else {
							Utils.displayDialog(activity, "Error",
									json.getString("message"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
		
				} else {
					Utils.displayDialog(activity, "Error",
							"Failed to get server response");
				}
	
			}
			
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!Utils.haveNetworkConnection(activity.getApplicationContext())){
				Utils.displayDialog(activity, "Network Error",
						"No Internet Connection");
				this.cancel(true);
			} else {
			// Shows Progress Bar Dialog and then call doInBackground method
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Loading..."); // typically you will define such
			// strings in a remote file.
			pDialog.show();
			}
		
		}
	}
	
}
			

	
