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
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.advanchip.mobile_controlledlighting.services.ValidateLogin;
import com.advanchip.mobile_controlledlighting.utils.Constants;
import com.advanchip.mobile_controlledlighting.utils.HTTPPostRequest;
import com.advanchip.mobile_controlledlighting.utils.Utils;
 
public class LoginFragment extends Fragment{
 
	private TextView forgotPassword, resetPassword;
	private EditText mEmail, mPassword;
	private Button submit;
	public Activity activity;
	
	String email, password;

	ProgressDialog pDialog = null;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
    	
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        initAll(rootView);
        activity = getActivity();
        return rootView;
        
        
    }
    
	private void initAll(View rootView) {
		
		mEmail = (EditText)rootView.findViewById(R.id.email);
		mPassword = (EditText)rootView.findViewById(R.id.password);
		
		submit = (Button)rootView.findViewById(R.id.email_sign_in_button);
		
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				attempLogin();
			}
		});
		
		forgotPassword = (TextView)rootView.findViewById(R.id.forgotPasswordView);
		resetPassword = (TextView)rootView.findViewById(R.id.resetPasswordView);
        forgotPassword.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent forgotPasswordIntent = new Intent(getActivity(), ForgotPasswordActivity.class);
				startActivity(forgotPasswordIntent);
			}
		});
        
        resetPassword.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent resetPasswordIntent = new Intent(getActivity(), ResetPasswordActivity.class);
				startActivity(resetPasswordIntent);
			}
		});
	}

	protected void attempLogin() {
		// Reset errors.
		mEmail.setError(null);
		mPassword.setError(null);
		
		// Store values at the time of the login attempt.
		email = mEmail.getText().toString();
		password = mPassword.getText().toString();
		
		View focusView = null;
		boolean cancel = false;
		
		// Check for a valid password, if the user entered one.
		if (TextUtils.isEmpty(password) || !Utils.isPasswordValid(password)) {
			mPassword.setError(getString(R.string.error_invalid_password));
			focusView = mPassword;
			cancel = true;
		}
		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			mEmail.setError(getString(R.string.error_field_required));
			focusView = mEmail;
			cancel = true;
		} else if (!Utils.isEmailValid(email)) {
			mEmail.setError(getString(R.string.error_invalid_email));
			focusView = mEmail;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			List<NameValuePair> loginParams = getPairs();
			
			new ValidateLogin(this.getActivity()).execute(loginParams);
		}
	}
	
	private List<NameValuePair> getPairs() {
		// TODO Auto-generated method stub
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));
		nameValuePairs.add(new BasicNameValuePair("client_id", "android_v1"));
		nameValuePairs.add(new BasicNameValuePair("client_secret",
				Constants.CLIENT_SECRET));
		nameValuePairs.add(new BasicNameValuePair("username", email));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		return nameValuePairs;
	}

}
