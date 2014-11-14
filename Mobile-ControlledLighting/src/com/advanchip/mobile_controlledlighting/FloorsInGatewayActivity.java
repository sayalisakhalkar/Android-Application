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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.advanchip.mobile_controlledlighting.GatesInGateway.CreateSwitchInGateway;
import com.advanchip.mobile_controlledlighting.adapters.GatewayFloorAdapter;
import com.advanchip.mobile_controlledlighting.adapters.GatewaySwitchAdapter;
import com.advanchip.mobile_controlledlighting.models.Floor;
import com.advanchip.mobile_controlledlighting.models.Switch;
import com.advanchip.mobile_controlledlighting.utils.Constants;
import com.advanchip.mobile_controlledlighting.utils.SharedPrefUtils;
import com.advanchip.mobile_controlledlighting.utils.Utils;

public class FloorsInGatewayActivity extends Activity {
	
	ProgressDialog dialog;
	ListView floorList;
	public static GatewayFloorAdapter  adapter;
	public static  List<Floor> floors = null;
	Floor floor;
	private static final String TAG = ":FloorsInGateway";
	public static Dialog dialogAddFloor;
	private EditText addFloorName;
	private static String gatewaySerial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialog = new ProgressDialog(this);
		setContentView(R.layout.acivity_floors_in_gateway);
		Bundle extras = getIntent().getExtras();
	    if(extras != null) {
	    	gatewaySerial = extras.getString("gatewaySerial");
	    }
		floors = new ArrayList<Floor>();
	    adapter = new GatewayFloorAdapter(this,R.layout.floors_in_gateway_list_item,floors);
	    adapter.setGatewaySerial(gatewaySerial);
	    
	    floorList = (ListView)findViewById(R.id.listFloorsInGateway);
	    floorList.setAdapter(adapter);
	   
	    floorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	           @Override
	           public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
	        	   
	        	   Intent intent = new Intent(FloorsInGatewayActivity.this,RoomsInFloorActivity.class);
	        	   intent.putExtra("gatewaySerial", gatewaySerial);
	        	   intent.putExtra("floorId", floors.get(position).getFloorId());
	        	   startActivity(intent);
	           }
	    });
	    new GetAllFloors().execute();
	    
		
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
	  			dialogAddFloor = new Dialog(FloorsInGatewayActivity.this);
	  			dialogAddFloor.setContentView(R.layout.dialog_add_floor);
	  			dialogAddFloor.setTitle("Add a new floor");
	  			
	  			//Get reference to the text boxes
	  			addFloorName = (EditText)dialogAddFloor.findViewById(R.id.addFloorName);
	  			
	   
	  			Button cancelButton = (Button) dialogAddFloor.findViewById(R.id.add_floor_cancel);
	  			// if button is clicked, close the custom dialog
	  			cancelButton.setOnClickListener(new OnClickListener() {
	  				@Override
	  				public void onClick(View v) {
	  					dialogAddFloor.dismiss();
	  				}
	  			});
	  			
	  			Button createSwitch = (Button) dialogAddFloor.findViewById(R.id.add_floor_button);
	  			// if button is clicked, close the custom dialog
	  			createSwitch.setOnClickListener(new OnClickListener() {
	  				@Override
	  				public void onClick(View v) {
	  					//Create a name value pairs for new gateway 
	  					
	  					
	  					/*
	  					 * To create a new floor in a gateway you need to pass name of the floor
	  					 */
	  					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	  					nameValuePairs.add(new BasicNameValuePair("name", addFloorName.getText().toString()));
						new CreateFloorInGateway().execute(nameValuePairs);
						
	  				}
	  			});
	   
	  			dialogAddFloor.show();
	              return true;
	          default:
	              return super.onOptionsItemSelected(item);
	      }
	  }
	
	
	class GetAllFloors extends AsyncTask<Void, String, String> {
		
		
		@Override
		protected void onPreExecute() {
			dialog.setTitle("Retrieving floors...");
			dialog.show();
		}

		protected String doInBackground(Void... input) {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet(Constants.URL_CREATE_GATEWAY +"/"+ gatewaySerial + "/floors");
			request.setHeader("Authorization", SharedPrefUtils.getTokenType(FloorsInGatewayActivity.this) + " " + SharedPrefUtils.getToken(FloorsInGatewayActivity.this));
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
				Utils.displayDialog(FloorsInGatewayActivity.this, "Error in Retrieving Switches",
						"Failed to get server response");
			} else {
				
				for (int i=0;i<json.length();i++){
					JSONObject floorObj;
					try {
						floorObj = json.getJSONObject(i);
						Floor floorObject = new Floor();
						floorObject.setFloorName(floorObj.getString("name"));
						floorObject.setFloorId(floorObj.getString("_id"));
						adapter.add(floorObject);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	 class CreateFloorInGateway extends AsyncTask<List<NameValuePair>, String, String> {
			
			
			@Override
			protected void onPreExecute() {
				dialog.setTitle("Creating a new Floor...");
				dialog.show();
			}

			@Override
			protected String doInBackground(List<NameValuePair>... input) {

				// 1. create HttpClient
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost request = new HttpPost(Constants.URL_CREATE_GATEWAY + "/" + gatewaySerial  + "/floors");
				
				
				try {
					request.setEntity(new UrlEncodedFormEntity(input[0]));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				request.setHeader("Authorization", SharedPrefUtils.getTokenType(FloorsInGatewayActivity.this) + " " + SharedPrefUtils.getToken(FloorsInGatewayActivity.this));
				
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
					Utils.displayDialog(FloorsInGatewayActivity.this, "Error in Creating a Switch",
							"Failed to get server response");
				} else {
					Log.d(TAG, json.toString());
					Floor got = new Floor();
					try {
						//gateways.clear();
						got.setFloorName(json.getString("name"));
						got.setFloorId(json.getString("_id"));
						adapter.add(got);
						dialog.dismiss();
						adapter.add(got);
						Intent intent = getIntent();
						intent.putExtra("gatewaySerial", gatewaySerial);
						finish();
						startActivity(intent);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}

			}
		}
}
