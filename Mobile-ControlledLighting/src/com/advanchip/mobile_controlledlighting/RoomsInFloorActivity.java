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

import com.advanchip.mobile_controlledlighting.adapters.FloorRoomAdapter;
import com.advanchip.mobile_controlledlighting.adapters.GatewayFloorAdapter;
import com.advanchip.mobile_controlledlighting.models.Floor;
import com.advanchip.mobile_controlledlighting.models.Room;
import com.advanchip.mobile_controlledlighting.utils.Constants;
import com.advanchip.mobile_controlledlighting.utils.SharedPrefUtils;
import com.advanchip.mobile_controlledlighting.utils.Utils;

public class RoomsInFloorActivity extends Activity {
	
	ProgressDialog dialog;
	ListView roomList;
	public static FloorRoomAdapter  adapter;
	public static  List<Room> rooms = null;
	Room room;
	private static final String TAG = ":RoomsInFloorActivity";
	public static Dialog dialogAddRoom;
	private EditText addRoomName;
	private static String gatewaySerial;
	private static String floorId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialog = new ProgressDialog(this);
		setContentView(R.layout.activity_rooms_in_floor);
		Bundle extras = getIntent().getExtras();
	    if(extras != null) {
	    	gatewaySerial = extras.getString("gatewaySerial");
	    	floorId = extras.getString("floorId");
	    }
	    
	    Toast.makeText(this, floorId, Toast.LENGTH_SHORT).show();;
		rooms = new ArrayList<Room>();
	    adapter = new FloorRoomAdapter(this,R.layout.rooms_in_floor_list_item,rooms);
	    adapter.setSerialAndId(gatewaySerial, floorId);
	    roomList = (ListView)findViewById(R.id.listRoomsInFloor);
	    roomList.setAdapter(adapter);
	    roomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	           @Override
	           public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
	        	   
	        	   Intent intent = new Intent(RoomsInFloorActivity.this,SwitchInRoomActivity.class);
	        	   intent.putExtra("gatewaySerial", gatewaySerial);
	        	   intent.putExtra("floorId", floorId);
	        	   intent.putExtra("roomId", rooms.get(position).getRoomId());
	        	   startActivity(intent);
	           }
	    });
	    new GetAllRooms().execute();
	    
	    
		
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
	  			dialogAddRoom = new Dialog(RoomsInFloorActivity.this);
	  			dialogAddRoom.setContentView(R.layout.dialog_add_room);
	  			dialogAddRoom.setTitle("Add a new room");
	  			
	  			//Get reference to the text boxes
	  			addRoomName = (EditText)dialogAddRoom.findViewById(R.id.addRoomName);
	  			
	   
	  			Button cancelButton = (Button) dialogAddRoom.findViewById(R.id.add_room_cancel);
	  			// if button is clicked, close the custom dialog
	  			cancelButton.setOnClickListener(new OnClickListener() {
	  				@Override
	  				public void onClick(View v) {
	  					dialogAddRoom.dismiss();
	  				}
	  			});
	  			
	  			Button createRoom = (Button) dialogAddRoom.findViewById(R.id.add_room_button);
	  			// if button is clicked, close the custom dialog
	  			createRoom.setOnClickListener(new OnClickListener() {
	  				@Override
	  				public void onClick(View v) {
	  					//Create a name value pairs for new gateway 
	  					
	  					
	  					/*
	  					 * To create a new floor in a gateway you need to pass name of the floor
	  					 */
	  					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	  					nameValuePairs.add(new BasicNameValuePair("name", addRoomName.getText().toString()));
						new CreateRoomInFloor().execute(nameValuePairs);
						
	  				}
	  			});
	   
	  			dialogAddRoom.show();
	              return true;
	          default:
	              return super.onOptionsItemSelected(item);
	      }
	  }
	
	
	class GetAllRooms extends AsyncTask<Void, String, String> {
		
		
		@Override
		protected void onPreExecute() {
			dialog.setTitle("Retrieving your rooms...");
			dialog.show();
		}

		protected String doInBackground(Void... input) {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet(Constants.URL_CREATE_GATEWAY +"/"+ gatewaySerial + "/floors/" +floorId + "/rooms");
			request.setHeader("Authorization", SharedPrefUtils.getTokenType(RoomsInFloorActivity.this) + " " + SharedPrefUtils.getToken(RoomsInFloorActivity.this));
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
				Utils.displayDialog(RoomsInFloorActivity.this, "Error in Retrieving Switches",
						"Failed to get server response");
			} else {
				
				for (int i=0;i<json.length();i++){
					JSONObject floorObj;
					try {
						floorObj = json.getJSONObject(i);
						Room roomObject = new Room();
						roomObject.setRoomName(floorObj.getString("name"));
						roomObject.setRoomId(floorObj.getString("_id"));
						adapter.add(roomObject);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	 class CreateRoomInFloor extends AsyncTask<List<NameValuePair>, String, String> {
			
			
			@Override
			protected void onPreExecute() {
				dialog.setTitle("Creating a new Floor...");
				dialog.show();
			}

			@Override
			protected String doInBackground(List<NameValuePair>... input) {

				// 1. create HttpClient
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost request = new HttpPost(Constants.URL_CREATE_GATEWAY + "/" + gatewaySerial  + "/floors/" + floorId + "/rooms");
				
				
				try {
					request.setEntity(new UrlEncodedFormEntity(input[0]));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				request.setHeader("Authorization", SharedPrefUtils.getTokenType(RoomsInFloorActivity.this) + " " + SharedPrefUtils.getToken(RoomsInFloorActivity.this));
				
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
					Utils.displayDialog(RoomsInFloorActivity.this, "Error in Creating a Switch",
							"Failed to get server response");
				} else {
					Log.d(TAG, json.toString());
					Room got = new Room();
					try {
						//gateways.clear();
						got.setRoomName(json.getString("name"));
						got.setRoomId(json.getString("_id"));
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
