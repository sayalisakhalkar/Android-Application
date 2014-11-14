package com.advanchip.mobile_controlledlighting;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.advanchip.mobile_controlledlighting.ManageGateway.CreateGateway;
import com.advanchip.mobile_controlledlighting.adapters.GatewaySwitchAdapter;
import com.advanchip.mobile_controlledlighting.models.Gateway;
import com.advanchip.mobile_controlledlighting.models.Switch;
import com.advanchip.mobile_controlledlighting.utils.Constants;
import com.advanchip.mobile_controlledlighting.utils.SharedPrefUtils;
import com.advanchip.mobile_controlledlighting.utils.Utils;

public class GatesInGateway extends Activity {
	
	ProgressDialog dialog;
	ListView switchList;
	public static GatewaySwitchAdapter adapter;
	public static  List<Switch> switches = null;
	Switch Switch;
	private static final String TAG = ":GatesInGateway";
	private static String gatewaySerial;
	private EditText addSwitchSerial,addSwitchName;
	public static Dialog dialogAddSwitch;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialog = new ProgressDialog(this);
		setContentView(R.layout.activity_gates_in_gateway);
		
		Bundle extras = getIntent().getExtras();
	    if(extras != null) {
	    	gatewaySerial = extras.getString("gatewaySerial");
	    }
	    Toast.makeText(getApplicationContext(), gatewaySerial, Toast.LENGTH_SHORT).show();
	    
		switches = new ArrayList<Switch>();
	    adapter = new GatewaySwitchAdapter(this,R.layout.gates_in_gateway_list_item,switches);
	    switchList = (ListView)findViewById(R.id.listGatesInGateway);
	    switchList.setAdapter(adapter);
	    new GetAllSwitches().execute();
		
	}
	
	 @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	      // Inflate the menu items for use in the action bar
	      MenuInflater inflater = getMenuInflater();
	      inflater.inflate(R.menu.menu_add_item, menu);
	      return super.onCreateOptionsMenu(menu);
	  }
	 
	 @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	      // Handle presses on the action bar items
		  
	      switch (item.getItemId()) {
	          case R.id.action_add_gateway:
	        	// custom dialog
	  			dialogAddSwitch = new Dialog(GatesInGateway.this);
	  			dialogAddSwitch.setContentView(R.layout.dialog_add_switch);
	  			dialogAddSwitch.setTitle("Add a new switch");
	  			
	  			//Get reference to the text boxes
	  			addSwitchSerial = (EditText) dialogAddSwitch.findViewById(R.id.addSwitchSerial);
	  			addSwitchName = (EditText)dialogAddSwitch.findViewById(R.id.addSwitchName);
	  			
	   
	  			Button cancelButton = (Button) dialogAddSwitch.findViewById(R.id.add_switch_cancel);
	  			// if button is clicked, close the custom dialog
	  			cancelButton.setOnClickListener(new OnClickListener() {
	  				@Override
	  				public void onClick(View v) {
	  					dialogAddSwitch.dismiss();
	  				}
	  			});
	  			
	  			Button createSwitch = (Button) dialogAddSwitch.findViewById(R.id.add_switch_button);
	  			// if button is clicked, close the custom dialog
	  			createSwitch.setOnClickListener(new OnClickListener() {
	  				@Override
	  				public void onClick(View v) {
	  					//Create a name value pairs for new gateway 
	  					
	  					
	  					/*
	  					 * To create a new switch you need to pass serial_number and name paramters of the switch
	  					 */
	  					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	  					nameValuePairs.add(new BasicNameValuePair("serial_number", addSwitchSerial.getText().toString()));
	  					nameValuePairs.add(new BasicNameValuePair("name", addSwitchName.getText().toString()));
						new CreateSwitchInGateway().execute(nameValuePairs);
						
	  				}
	  			});
	   
	  			dialogAddSwitch.show();
	              return true;
	          default:
	              return super.onOptionsItemSelected(item);
	      }
	  }
	 
	 
	 
	 class CreateSwitchInGateway extends AsyncTask<List<NameValuePair>, String, String> {
			
			
			@Override
			protected void onPreExecute() {
				dialog.setTitle("Creating a new Switch...");
				dialog.show();
			}

			@Override
			protected String doInBackground(List<NameValuePair>... input) {

				// 1. create HttpClient
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost request = new HttpPost(Constants.URL_CREATE_GATEWAY + "/" + gatewaySerial  + "/gates");
				
				
				try {
					request.setEntity(new UrlEncodedFormEntity(input[0]));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				request.setHeader("Authorization", SharedPrefUtils.getTokenType(GatesInGateway.this) + " " + SharedPrefUtils.getToken(GatesInGateway.this));
				
				HttpResponse response;
				try {

					response = httpclient.execute(request);
					try {
						return EntityUtils.toString(response.getEntity());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} catch (Exception e) {

					e.printStackTrace();

				} catch (OutOfMemoryError e2) {
					e2.printStackTrace();
					return null;
				}
				return null;

			}

			@Override
			protected void onPostExecute(String returned) {
				Log.d(TAG, returned);
				dialog.dismiss();

				
				// JSONObject responceJson = new JSONObject();
				JSONObject json = null;
				try {
					json = new JSONObject(returned);
					Log.d(TAG,json.toString());
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				if (json == null) {
					Utils.displayDialog(GatesInGateway.this, "Error in Creating a Switch",
							"Failed to get server response");
				} else {
					Log.d(TAG, json.toString());
					Switch got = new Switch();
					try {
						//gateways.clear();
						got.setSwitchName(json.getString("name"));
						got.setSwitchSerialNumber(json.getString("serialNumber"));
						dialog.dismiss();
						adapter.add(got);
						Intent intent = getIntent();
						intent.putExtra("gatewaySerial", gatewaySerial);
						finish();
						startActivity(intent);
						//adapter.add(got);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}

			}
		}
	
	
	class GetAllSwitches extends AsyncTask<Void, String, String> {
		
		
		@Override
		protected void onPreExecute() {
			dialog.setTitle("Retrieving your switches...");
			dialog.show();
		}

		protected String doInBackground(Void... input) {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet(Constants.URL_CREATE_GATEWAY +"/" + gatewaySerial + "/gates");
			request.setHeader("Authorization", SharedPrefUtils.getTokenType(GatesInGateway.this) + " " + SharedPrefUtils.getToken(GatesInGateway.this));
			HttpResponse response;
			try {

				response = httpclient.execute(request);
				try {
					Log.d(TAG,response.toString());
					return EntityUtils.toString(response.getEntity());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (Exception e) {

				e.printStackTrace();

			} catch (OutOfMemoryError e2) {
				e2.printStackTrace();
				return null;
			}
			return null;

		}

		@Override
		protected void onPostExecute(String returned) {
			dialog.dismiss();

			
			// JSONObject responceJson = new JSONObject();
			JSONArray json = null;
			try {
				json = new JSONArray(returned);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			if (json == null) {
				Utils.displayDialog(GatesInGateway.this, "Error in Retrieving Switches",
						"Failed to get server response");
			} else {
				
				for (int i=0;i<json.length();i++){
					JSONObject switchObj;
					try {
						switchObj = json.getJSONObject(i);
						Switch switchObject = new Switch();
						switchObject.setSwitchName(switchObj.getString("name"));
						adapter.add(switchObject);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
