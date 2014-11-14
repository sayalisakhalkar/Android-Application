package com.advanchip.mobile_controlledlighting;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.advanchip.mobile_controlledlighting.services.ValidateLogin;
import com.advanchip.mobile_controlledlighting.utils.Constants;
import com.advanchip.mobile_controlledlighting.utils.HTTPPostRequest;
import com.advanchip.mobile_controlledlighting.utils.Utils;

public class SignupFragment extends Fragment {

	
	/*
	 * Elements the refer to the UI elements
	 */
	private EditText fNameInput, mDisplayName;
	private EditText lNameInput;
	private EditText passwordInput;
	private EditText confirmPasswordInput;
	private AutoCompleteTextView emailInput;
	private Button signUpButton;
	ProgressDialog pDialog = null;
	
	private String fName,lName, password, confirmPassword, email, displayName;
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_signup, container,
				false);
		
		/*
		 * Bind the elements with the UI elements
		 */
		init(rootView);

		return rootView;
	}



	private void init(View rootView) {
		fNameInput = (EditText) rootView.findViewById(R.id.fnameEditText);
		lNameInput = (EditText) rootView.findViewById(R.id.lnameEditText);
		mDisplayName = (EditText) rootView.findViewById(R.id.displayName);
		passwordInput = (EditText) rootView.findViewById(R.id.passwordEditText);
		confirmPasswordInput = (EditText) rootView
				.findViewById(R.id.confirmPasswordEditText);
		emailInput = (AutoCompleteTextView) rootView
				.findViewById(R.id.emailEditText);
		signUpButton = (Button) rootView.findViewById(R.id.signUpButton);
		signUpButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				attemptSignUp();
			}
		});
	}
	
	

	protected void attemptSignUp() {
		
		fNameInput.setError(null);
		lNameInput.setError(null);
		mDisplayName.setError(null);
		passwordInput.setError(null);
		confirmPasswordInput.setError(null);
		emailInput.setError(null);
		
		fName = fNameInput.getText().toString();
		lName = lNameInput.getText().toString();
		displayName = mDisplayName.getText().toString();
		password = passwordInput.getText().toString();
		confirmPassword = confirmPasswordInput.getText().toString();
		email = emailInput.getText().toString();

		View focusView = null;
		boolean cancel = false;
		
		//Check if Password and Confirm password are same
		if(!TextUtils.equals(password, confirmPassword)) {
			confirmPasswordInput.setError("Both passwords should be equal");
			focusView = confirmPasswordInput;
			cancel = true;
		}
		
		// Check for a valid password, if the user entered one.
		if (TextUtils.isEmpty(password) || !Utils.isPasswordValid(password)) {
			passwordInput.setError(getString(R.string.error_invalid_password));
			focusView = passwordInput;
			cancel = true;
		}
		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			emailInput.setError(getString(R.string.error_field_required));
			focusView = emailInput;
			cancel = true;
		} else if (!Utils.isEmailValid(email)) {
			emailInput.setError(getString(R.string.error_invalid_email));
			focusView = emailInput;
			cancel = true;
		}
		
		if(TextUtils.isEmpty(fName)) {
			fNameInput.setError(getString(R.string.error_field_required));
			focusView = fNameInput;
			cancel = true;
		} else {
			if(TextUtils.isEmpty(displayName)) {
				displayName = fName;
			}
		}
		
		if(TextUtils.isEmpty(lName)) {
			fNameInput.setError(getString(R.string.error_field_required));
			focusView = fNameInput;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			new AsyncSignUp().execute();
		}
		
	}

	class AsyncSignUp extends
			AsyncTask<JSONObject, String, String> {

		// Show Progress bar before downloading Music
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!Utils.haveNetworkConnection(getActivity().getApplicationContext())){
				Utils.displayDialog(getActivity(), "Network Error",
						"No Internet Connection");
				this.cancel(true);
			} else {
			// Shows Progress Bar Dialog and then call doInBackground method
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Signing Up..."); // typically you will define such
			// strings in a remote file.
			pDialog.show();
			}

		}

		@Override
		protected String doInBackground(JSONObject... signUp) {
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));
			nameValuePairs.add(new BasicNameValuePair("client_id", "android_v1"));
			nameValuePairs.add(new BasicNameValuePair("client_secret",
					Constants.CLIENT_SECRET));
			nameValuePairs.add(new BasicNameValuePair("username", email));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("first_name", fName));
			nameValuePairs.add(new BasicNameValuePair("last_name", lName));
			nameValuePairs.add(new BasicNameValuePair("display_name", displayName));
			
			HTTPPostRequest request = new HTTPPostRequest();
			String result = request.executeHTTPPost(nameValuePairs,
					Constants.URL_SIGNUP, getActivity());
			
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
				Utils.displayDialog(getActivity(), "Error",
						"Failed to get server response");
			} else {
				if (json.has("status")) {
					try {
						if(json.getInt("status") == 200){
							/*Utils.displayDialog(getActivity(), "Sign-Up",
									"Sign-Up successful. Please Login using your email and password.");*/
							List<NameValuePair> loginParams = new ArrayList<NameValuePair>(2);
							loginParams.add(new BasicNameValuePair("grant_type", "password"));
							loginParams.add(new BasicNameValuePair("client_id", "android_v1"));
							loginParams.add(new BasicNameValuePair("client_secret",
									Constants.CLIENT_SECRET));
							loginParams.add(new BasicNameValuePair("username", email));
							loginParams.add(new BasicNameValuePair("password", password));
							new ValidateLogin(getActivity()).execute(loginParams);
						
						} else {
							Utils.displayDialog(getActivity(), "Error",
									json.getString("message"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
		
				} else {
					Utils.displayDialog(getActivity(), "Error",
							"Failed to get server response");
				}
	
			}

		}
	}

}
