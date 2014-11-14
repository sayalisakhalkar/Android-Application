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
import android.widget.Toast;

import com.advanchip.mobile_controlledlighting.adapters.ManageGatewayAdapter;
import com.advanchip.mobile_controlledlighting.models.Gateway;
import com.advanchip.mobile_controlledlighting.utils.Constants;
import com.advanchip.mobile_controlledlighting.utils.SharedPrefUtils;
import com.advanchip.mobile_controlledlighting.utils.Utils;

	public class ManageGateway extends Activity {
		private final String TAG = ":ManageGateway";
	  ListView list;
	  
	  public static  List<Gateway> gateways = null;
	  Gateway gateway;
	  private EditText gatewaySerial,gatewayName;
	  public static ManageGatewayAdapter adapter;
	  ProgressDialog dialog = null;
	  public static Dialog dialogGateway;
	  
	
	  
	 
	  public static void update(String name, int position){
		  gateways.get(position).setGatewayName(name);
		  adapter.update();
	  }
	  
	  public static void closeDialog(){
		  dialogGateway.dismiss();
	  }
	  
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
		dialog = new ProgressDialog(this);
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_manage_gateway);
	    gateways = new ArrayList<Gateway>();
	    adapter = new ManageGatewayAdapter(this,R.layout.manage_gateway_list_item,gateways);
	    list=(ListView)findViewById(R.id.listGateways);
	    list.setAdapter(adapter);
	    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	           @Override
	           public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
	        	   
	        	   Intent intent = new Intent(ManageGateway.this,GatewayRouteActivity.class);
	        	   intent.putExtra("gatewaySerial", gateways.get(position).getSerialNumber());
	        	   startActivity(intent);
	           }
	    });
	    new GetAllGateways().execute();
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
		  gateways = new ArrayList<Gateway>();
		  
	      switch (item.getItemId()) {
	          case R.id.action_add_gateway:
	        	// custom dialog
	  			dialogGateway = new Dialog(ManageGateway.this);
	  			dialogGateway.setContentView(R.layout.dialog_add_gateway);
	  			dialogGateway.setTitle("Add a new gateway");
	  			
	  			//Handle for the textboxes
	  			gatewaySerial = (EditText) dialogGateway.findViewById(R.id.gatewaySerial);
	  			gatewayName = (EditText)dialogGateway.findViewById(R.id.gatewayName);
	  			
	   
	  			Button dialogButton = (Button) dialogGateway.findViewById(R.id.dialog_cancel);
	  			// if button is clicked, close the custom dialog
	  			dialogButton.setOnClickListener(new OnClickListener() {
	  				@Override
	  				public void onClick(View v) {
	  					dialog.dismiss();
	  				}
	  			});
	  			
	  			Button createGateway = (Button) dialogGateway.findViewById(R.id.add_gateway_button);
	  			// if button is clicked, close the custom dialog
	  			createGateway.setOnClickListener(new OnClickListener() {
	  				@Override
	  				public void onClick(View v) {
	  					//Create a name value pairs for new gateway 
	  					
	  					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	  					nameValuePairs.add(new BasicNameValuePair("serial_number", gatewaySerial.getText().toString()));
	  					nameValuePairs.add(new BasicNameValuePair("name", gatewayName.getText().toString()));
	  					nameValuePairs.add(new BasicNameValuePair("user_id", SharedPrefUtils.getUserId(getApplicationContext())));
	  					nameValuePairs.add(new BasicNameValuePair("user_email", SharedPrefUtils.getUserEmail(getApplicationContext())));
	  					nameValuePairs.add(new BasicNameValuePair("user_displayName", SharedPrefUtils.getUserEmail(getApplicationContext())));
						new CreateGateway().execute(nameValuePairs);
						
	  				}
	  			});
	   
	  			dialogGateway.show();
	              return true;
	          default:
	              return super.onOptionsItemSelected(item);
	      }
	  }
	  
	  
	  
	  
	  class CreateGateway extends AsyncTask<List<NameValuePair>, String, String> {
			
			
			@Override
			protected void onPreExecute() {
				dialog.setTitle("Creating a new Gateway...");
				dialog.show();
			}

			@Override
			protected String doInBackground(List<NameValuePair>... input) {

				// 1. create HttpClient
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost request = new HttpPost(Constants.URL_CREATE_GATEWAY);
				
				
				try {
					request.setEntity(new UrlEncodedFormEntity(input[0]));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				request.setHeader("Authorization", SharedPrefUtils.getTokenType(ManageGateway.this) + " " + SharedPrefUtils.getToken(ManageGateway.this));
				
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
					Utils.displayDialog(ManageGateway.this, "Error in Creating a Gateway",
							"Failed to get server response");
				} else {
					Log.d(TAG, json.toString());
					Gateway got = new Gateway();
					try {
						//gateways.clear();
						got.setGatewayName(json.getString("name"));
						got.setSerialNumber(json.getString("serialNumber"));
						closeDialog();
						adapter.add(got);
						Intent intent = getIntent();
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
	  
	  
	  class GetAllGateways extends AsyncTask<Void, String, String> {
			
			
			@Override
			protected void onPreExecute() {
				dialog.setTitle("Retrieving your gateways...");
				dialog.show();
			}

			protected String doInBackground(Void... input) {

				// 1. create HttpClient
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet request = new HttpGet(Constants.URL_CREATE_GATEWAY);
				
				
				
				request.setHeader("Authorization", SharedPrefUtils.getTokenType(ManageGateway.this) + " " + SharedPrefUtils.getToken(ManageGateway.this));
				request.setHeader("user.id", SharedPrefUtils.getUserId(ManageGateway.this));
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
				JSONArray json = null;
				try {
					json = new JSONArray(returned);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				if (json == null) {
					Utils.displayDialog(ManageGateway.this, "Error in Creating a Gateway",
							"Failed to get server response");
				} else {
					
					for (int i=0;i<json.length();i++){
						JSONObject gatewayObject;
						try {
							gatewayObject = json.getJSONObject(i);
							Gateway gateway = new Gateway();
							gateway.setGatewayName(gatewayObject.getString("name"));
							gateway.setSerialNumber(gatewayObject.getString("serialNumber"));
							Log.d(TAG,gateway.getGatewayName());
							adapter.add(gateway);
							
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	    
	}