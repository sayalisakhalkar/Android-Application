package com.advanchip.mobile_controlledlighting;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.advanchip.mobile_controlledlighting.utils.Constants;
import com.advanchip.mobile_controlledlighting.utils.HTTPPostRequest;
import com.advanchip.mobile_controlledlighting.utils.SharedPrefUtils;
import com.advanchip.mobile_controlledlighting.utils.Utils;

public class MenuActivity extends Activity {

	private static Button manageUsersButton, manageGatewayButton,
			favoriteButton, manageSwitchButton;
	ProgressDialog pDialog = null;
	Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		setContentView(R.layout.activity_menu);
		manageUsersButton = (Button) findViewById(R.id.manageUsers);
		manageGatewayButton = (Button) findViewById(R.id.manageGateways);
		favoriteButton = (Button) findViewById(R.id.favorites);
		manageSwitchButton = (Button) findViewById(R.id.manageSwitches);
		

		manageUsersButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Intent intent = new Intent(MenuActivity.this,
						ManageUsersActivity.class);
				startActivity(intent);
			}
		});

		manageGatewayButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Intent intent = new Intent(MenuActivity.this,
						ManageGateway.class);
				startActivity(intent);
			}
		});

		favoriteButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Intent intent = new Intent(MenuActivity.this,
						FavoriteActivity.class);
				startActivity(intent);
			}
		});

		manageSwitchButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Intent intent = new Intent(MenuActivity.this,
						EditSwitchActivity.class);
				startActivity(intent);
			}
		});

	}

	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1:
				// DO something
				break;
			case R.id.button2:
				// DO something
				break;
			case R.id.button3:
				// DO something
				break;
			}

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_log_out) {
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					MenuActivity.this);

			// set title
			alertDialogBuilder.setTitle("Logout");

			// set dialog message
			alertDialogBuilder
					.setMessage("Do you want to log out?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if this button is clicked, close
									// current activity
									new LogoutAsyncTask().execute();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if this button is clicked, just close
									// the dialog box and do nothing
									dialog.cancel();
								}
							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
			
		}
		return super.onOptionsItemSelected(item);
		
	}
	
	class LogoutAsyncTask extends
	AsyncTask<Void, String, String> {

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (!Utils.haveNetworkConnection(MenuActivity.this)){
			Utils.displayDialog(getApplicationContext(), "Network Error",
					"No Internet Connection");
			this.cancel(true);
		} else {
		// Shows Progress Bar Dialog and then call doInBackground method
		pDialog = new ProgressDialog(MenuActivity.this);
		pDialog.setMessage("Signing Out..."); // typically you will define such
		// strings in a remote file.
		pDialog.show();
		}
	
	}
	
	@Override
	protected String doInBackground(Void... signUp) {
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("email", SharedPrefUtils.getUserEmail(getApplicationContext())));
		nameValuePairs.add(new BasicNameValuePair("user_id", SharedPrefUtils.getUserId(getApplicationContext())));
		HTTPPostRequest loginRequest = new HTTPPostRequest();
		String result = loginRequest.executeHTTPPost(nameValuePairs,
				Constants.URL_LOGOUT, MenuActivity.this);
		
		return result;
	}
	
	// While Downloading Music File
	protected void onProgressUpdate(String... progress) {
		// Set progress percentage
	
	}
	
	// Once Music File is downloaded
	@Override
	protected void onPostExecute(String response) {
		// Dismiss the dialog after the request completed
		if (pDialog != null && pDialog.isShowing()) {
			pDialog.dismiss();
		}
		//JSONObject responceJson = new JSONObject();
		JSONObject json = null;
		try {
			json = new JSONObject(response);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		if (json == null) {
			Utils.displayDialog(MenuActivity.this, "Error in Login",
					"Failed to get server response");
		} else {
			if (json.has("status")) {
				try {
					if(json.getInt("status") == 200){
						Utils.removeLoginInfo(MenuActivity.this);
						Intent resetPasswordIntent = new Intent(MenuActivity.this, EntryActivity.class);
						resetPasswordIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(resetPasswordIntent);
					} else {
						Utils.displayDialog(MenuActivity.this, "Error in Signin Out",
								json.getString("message"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
	
			} else {
				Utils.displayDialog(MenuActivity.this, "Error in Login",
						"Failed to get server response");
			}

		}
	
	}
}
}
