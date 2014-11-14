package com.advanchip.mobile_controlledlighting;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
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

public class ResetPasswordActivity extends Activity{
	private EditText oldPassword;
	private EditText newPassword;
	private EditText confirmPassword;
	private EditText emailView;
	private Button submitResetPassword;
	
	String old_pass, new_pass, confirm_pass, email;

	public Activity activity;
	ProgressDialog pDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password);
		activity = this;
		oldPassword = (EditText)findViewById(R.id.oldPassword);
		newPassword = (EditText)findViewById(R.id.newPassword);
		confirmPassword = (EditText)findViewById(R.id.confirmPasswordEditText);
		emailView = (EditText)findViewById(R.id.email);
		submitResetPassword = (Button)findViewById(R.id.submitButton);
		submitResetPassword.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				attemptResetPassword();
			}
			
		});
	}
	
	public void attemptResetPassword() {

		// Reset errors.
		oldPassword.setError(null);
		newPassword.setError(null);
		confirmPassword.setError(null);
		emailView.setError(null);

		// Store values at the time of the login attempt.
		old_pass = oldPassword.getText().toString();
		new_pass = newPassword.getText().toString();
		confirm_pass = confirmPassword.getText().toString();
		email = emailView.getText().toString();
		
		boolean cancel = false;
		View focusView = null;
		
		//Check if Password and Confirm password are same
		if(!TextUtils.equals(new_pass, confirm_pass)) {
			confirmPassword.setError("Both passwords should be equal");
			focusView = confirmPassword;
			cancel = true;
		}
			
		// Check for a valid password, if the user entered one.
		if (TextUtils.isEmpty(new_pass) || !Utils.isPasswordValid(new_pass)) {
			newPassword.setError(getString(R.string.error_invalid_password));
			focusView = newPassword;
			cancel = true;
		}
		
		// Check for a valid old password, if the user entered one.
		if (TextUtils.isEmpty(old_pass) || !Utils.isPasswordValid(old_pass)) {
			oldPassword.setError(getString(R.string.error_invalid_password));
			focusView = oldPassword;
			cancel = true;
		}
		
		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			emailView.setError(getString(R.string.error_field_required));
			focusView = emailView;
			cancel = true;
		} else if (!Utils.isEmailValid(email)) {
			emailView.setError(getString(R.string.error_invalid_email));
			focusView = emailView;
			cancel = true;
		}
		
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else 
		{

			new AsyncResetPassword().execute();
		}
	}
	
	class AsyncResetPassword extends
	AsyncTask<JSONObject, String, String> {

// Show Progress bar before downloading Music
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

@Override
protected String doInBackground(JSONObject... signUp) {
	
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	nameValuePairs.add(new BasicNameValuePair("email", email));
	nameValuePairs.add(new BasicNameValuePair("old_password", old_pass));
	nameValuePairs.add(new BasicNameValuePair("new_password", new_pass));
	
	HTTPPostRequest request = new HTTPPostRequest();
	String result = request.executeHTTPPost(nameValuePairs,
			Constants.URL_RESET_PASSWORD, activity);
	
	return result;
	
}

@Override
protected void onPostExecute(String response) {
	// Dismiss the dialog after the Music file was downloaded
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
					Toast.makeText(getApplicationContext(), "Please Login using your new password.", 
							Toast.LENGTH_LONG).show();
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
}
	
}
