package com.advanchip.mobile_controlledlighting.adapters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.advanchip.mobile_controlledlighting.R;
import com.advanchip.mobile_controlledlighting.adapters.ManageGatewayAdapter.DeleteGateways;
import com.advanchip.mobile_controlledlighting.adapters.ManageGatewayAdapter.EditGateways;
import com.advanchip.mobile_controlledlighting.models.Floor;
import com.advanchip.mobile_controlledlighting.models.Switch;
import com.advanchip.mobile_controlledlighting.utils.Constants;
import com.advanchip.mobile_controlledlighting.utils.SharedPrefUtils;
import com.advanchip.mobile_controlledlighting.utils.Utils;

public class GatewayFloorAdapter extends ArrayAdapter<Floor> {
	ProgressDialog dialog = null;
	Context context;
	int resourceId;
	List<Floor> toShow = null;
	private final static String TAG = ":GatewayFloorAdapter";
	public static int positionSelected;
	public static  Dialog dialogFloor;
	private EditText floorName;
	private static String gatewaySerial;
	
	
	public void setGatewaySerial(String gatewaySerial){
		this.gatewaySerial = gatewaySerial;
	}

	public GatewayFloorAdapter(Activity context, int resourceId,
			List<Floor> retrieved) {
		super(context, resourceId, retrieved);
		this.context = context;
		this.toShow = retrieved;
		this.resourceId = resourceId;
	}

	public long getItemId(int position) {
		return position;
	}

	/* private view holder class */
	private class FloorHolder {
		TextView floorName;
		ImageButton editButton;
		ImageButton deleteButton;

	}

	public void update() {
		notifyDataSetChanged();
	}
	
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		View row = view;
		FloorHolder holder = null;
		
		if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resourceId, null);

            holder = new FloorHolder();
            holder.floorName = (TextView)row.findViewById(R.id.gatewayFloorName);
            holder.deleteButton = (ImageButton)row.findViewById(R.id.delete_gateway_floor_button);
            holder.editButton = (ImageButton)row.findViewById(R.id.edit_gateway_floor_button);
            
            holder.editButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                	
                	dialogFloor = new Dialog(getContext());
    	  			dialogFloor.setContentView(R.layout.dialog_edit_floor);
    	  			dialogFloor.setTitle("Edit Floor");
    	  			
    	  			//Handle for the textboxes
    	  			floorName = (EditText)dialogFloor.findViewById(R.id.floorName);
    	  			
    	   
    	  			Button dialogButton = (Button) dialogFloor.findViewById(R.id.dialog_cancel);
    	  			// if button is clicked, close the custom dialog
    	  			dialogButton.setOnClickListener(new OnClickListener() {
    	  				@Override
    	  				public void onClick(View v) {
    	  					dialogFloor.dismiss();
    	  				}
    	  			});
    	  			
    	  			Button saveGateway = (Button) dialogFloor.findViewById(R.id.save_floor_button);
    	  			// if button is clicked, close the custom dialog
    	  			saveGateway.setOnClickListener(new OnClickListener() {
    	  				@Override
    	  				public void onClick(View v) {
    	  					positionSelected = position;
    	  					
    	  					Log.d(TAG,String.valueOf(positionSelected));
    	  					List<NameValuePair> param = new ArrayList<NameValuePair>(2);
    	  					param.add(new BasicNameValuePair("name", floorName.getText().toString()));
    	  					new EditFloor().execute(param);
    	  					
    	  				}
    	  			});
    	  			dialogFloor.show();
                }
            });
            
            holder.deleteButton.setOnClickListener(new OnClickListener() {

            	 @Override
                 public void onClick(View v) {
            		positionSelected = position;
            		dialogFloor = new Dialog(getContext());
     	  			dialogFloor.setContentView(R.layout.dialog_delete_floor);
     	  			dialogFloor.setTitle("Delete floor? ");
     	  			
     	  			//Handle for the textboxes
     	  			floorName = (EditText)dialogFloor.findViewById(R.id.floorName);
     	  			floorName.setText(toShow.get(position).getFloorName());
     	  			Button dialogButton = (Button) dialogFloor.findViewById(R.id.dialog_cancel);
     	  			// if button is clicked, close the custom dialog
     	  			dialogButton.setOnClickListener(new OnClickListener() {
     	  				@Override
     	  				public void onClick(View v) {
     	  					dialogFloor.dismiss();
     	  				}
     	  			});
     	  			Button deleteGateway_btn = (Button) dialogFloor.findViewById(R.id.delete_floor_button);
     	  			// if button is clicked, close the custom dialog
     	  			deleteGateway_btn.setOnClickListener(new OnClickListener() {
     	  				@Override
     	  				public void onClick(View v) {
     	  					
     	  					Log.d(TAG,String.valueOf(positionSelected));
    	  					List<NameValuePair> param = new ArrayList<NameValuePair>(2);
    	  					param.add(new BasicNameValuePair("name", floorName.getText().toString()));
    	  					new DeleteFloor().execute(param);
     	  					     	  					
     	  				}
     	  			});
     	  			dialogFloor.show(); 
                 }  			
                
            });
            
            row.setTag(holder);
        }
        else
        {
            holder = (FloorHolder)row.getTag();
        }
        
        Floor floorObject = toShow.get(position);
        holder.floorName.setText(floorObject.getFloorName());
        return row;
		
	}
	
	
	/*
	 * Async class to edit information for a particular gateway
	 */
	class EditFloor extends AsyncTask<List<NameValuePair>, String, String> {
		
		
		@Override
		protected void onPreExecute() {
		}

		protected String doInBackground(List<NameValuePair>... input) {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			HttpPut request = new HttpPut(Constants.URL_CREATE_GATEWAY + "/" + gatewaySerial + "/floors/" + toShow.get(positionSelected).getFloorId());
			request.setHeader("Authorization", SharedPrefUtils.getTokenType(getContext()) + " " + SharedPrefUtils.getToken(getContext()));
			
			HttpResponse response;
			try {
				//Log.d(TAG,"sending request");
				request.setEntity(new UrlEncodedFormEntity(input[0]));
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

			
			// JSONObject responceJson = new JSONObject();
			JSONObject json = null;
			try {
				json = new JSONObject(returned);
				//Log.d(TAG,json.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			if (json == null) {
				Utils.displayDialog(getContext(), "Error in updating this Gateway",
						"Failed to get server response");
			} else {
				
				try {
					
					
					if (json.getInt("status") == 200) {
						
						String name = json.getJSONObject("floor").getString("name");
						toShow.get(positionSelected).setFloorName(name);
						//Log.d(TAG,"Response" + name);
						
						dialogFloor.dismiss();
						notifyDataSetChanged();
						
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}
	
	
	
	/*
	 * Async class to delete a gateway
	 */
	class DeleteFloor extends AsyncTask<List<NameValuePair>, String, String> {
		
		
		@Override
		protected void onPreExecute() {
			//dialog.setTitle("Retrieving your gateways...");
			//dialog.show();
		}

		protected String doInBackground(List<NameValuePair>... input) {

			
			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			HttpDelete request = new HttpDelete(Constants.URL_CREATE_GATEWAY + "/" + gatewaySerial + "/floors/" + toShow.get(positionSelected).getFloorId());
			request.setHeader("Authorization", SharedPrefUtils.getTokenType(getContext()) + " " + SharedPrefUtils.getToken(getContext()));
			
			HttpResponse response;
			try {
				//Log.d(TAG,"sending request");
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

			
			JSONObject json = null;
			try {
				json = new JSONObject(returned);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			if (json == null) {
				Utils.displayDialog(getContext(), "Error in deleting this floor",
						"Failed to get server response");
			} else {
				
				try {
					if (json.getInt("status") == 200) {
						toShow.remove(positionSelected);
						dialogFloor.dismiss();
						notifyDataSetChanged();
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}
}
