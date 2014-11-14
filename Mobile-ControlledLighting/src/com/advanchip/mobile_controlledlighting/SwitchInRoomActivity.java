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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.advanchip.mobile_controlledlighting.adapters.FloorRoomAdapter;
import com.advanchip.mobile_controlledlighting.adapters.SwitchRoomAdapter;
import com.advanchip.mobile_controlledlighting.models.Room;
import com.advanchip.mobile_controlledlighting.models.Switch;
import com.advanchip.mobile_controlledlighting.utils.Constants;
import com.advanchip.mobile_controlledlighting.utils.SharedPrefUtils;
import com.advanchip.mobile_controlledlighting.utils.Utils;

public class SwitchInRoomActivity extends Activity {
	
	
	ListView switchesInRoom;
	public static  List<Switch> rooms = null;
	Room room;
	public static SwitchRoomAdapter  adapter;
	public static Dialog dialogAddRoomSwitch;
	private EditText addRoomSwitchName;
	private String gatewaySerial,floorId,roomId;
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialog = new ProgressDialog(this);
		setContentView(R.layout.activity_switch_in_room);
		Bundle extras = getIntent().getExtras();
	    if(extras != null) {
	    	gatewaySerial = extras.getString("gatewaySerial");
	    	floorId = extras.getString("floorId");
	    	roomId = extras.getString("roomId");
	    }
		
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
	  			dialogAddRoomSwitch = new Dialog(SwitchInRoomActivity.this);
	  			dialogAddRoomSwitch.setContentView(R.layout.dialog_add_switch);
	  			dialogAddRoomSwitch.setTitle("Add a new room");
	  			
	  			//Get reference to the text boxes
	  			addRoomSwitchName = (EditText)dialogAddRoomSwitch.findViewById(R.id.addSwitchName);
	  			
	   
	  			Button cancelButton = (Button) dialogAddRoomSwitch.findViewById(R.id.add_switch_cancel);
	  			// if button is clicked, close the custom dialog
	  			cancelButton.setOnClickListener(new OnClickListener() {
	  				@Override
	  				public void onClick(View v) {
	  					dialogAddRoomSwitch.dismiss();
	  				}
	  			});
	  			
	  			Button createRoom = (Button) dialogAddRoomSwitch.findViewById(R.id.add_switch_button);
	  			// if button is clicked, close the custom dialog
	  			createRoom.setOnClickListener(new OnClickListener() {
	  				@Override
	  				public void onClick(View v) {
	  					//Create a name value pairs for new gateway 
	  					
	  					
	  					/*
	  					 * To create a new floor in a gateway you need to pass name of the floor
	  					 */
	  					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	  					nameValuePairs.add(new BasicNameValuePair("name", addRoomSwitchName.getText().toString()));
						new CreateSwitchInRoom().execute(nameValuePairs);
						
	  				}
	  			});
	   
	  			dialogAddRoomSwitch.show();
	              return true;
	          default:
	              return super.onOptionsItemSelected(item);
	      }
	  }
	
	class CreateSwitchInRoom extends AsyncTask<List<NameValuePair>, String, String> {
		
		
		@Override
		protected void onPreExecute() {
			dialog.setTitle("Creating a new Switch...");
			dialog.show();
		}

		@Override
		protected String doInBackground(List<NameValuePair>... input) {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost request = new HttpPost(Constants.URL_CREATE_GATEWAY + "/" + gatewaySerial  + "/floors/" + floorId + "/rooms/" + roomId + "/gates");
			
			
			try {
				request.setEntity(new UrlEncodedFormEntity(input[0]));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			request.setHeader("Authorization", SharedPrefUtils.getTokenType(SwitchInRoomActivity.this) + " " + SharedPrefUtils.getToken(SwitchInRoomActivity.this));
			
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
			dialog.dismiss();

			
			// JSONObject responceJson = new JSONObject();
			JSONObject json = null;
			try {
				json = new JSONObject(returned);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			if (json == null) {
				Utils.displayDialog(SwitchInRoomActivity.this, "Error in Creating a Switch",
						"Failed to get server response");
			} else {
				Switch got = new Switch();
				try {
					//gateways.clear();
					got.setSwitchName(json.getString("name"));
					got.setSwitchId(json.getString("_id"));
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
