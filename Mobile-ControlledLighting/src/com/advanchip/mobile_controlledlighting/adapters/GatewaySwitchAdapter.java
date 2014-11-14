package com.advanchip.mobile_controlledlighting.adapters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
import com.advanchip.mobile_controlledlighting.models.Switch;
import com.advanchip.mobile_controlledlighting.utils.Constants;
import com.advanchip.mobile_controlledlighting.utils.SharedPrefUtils;
import com.advanchip.mobile_controlledlighting.utils.Utils;

public class GatewaySwitchAdapter extends ArrayAdapter<Switch> {
	ProgressDialog dialog = null;
	Context context;
	int resourceId;
	List<Switch> toShow = null;
	private final static String TAG = ":GatewaySwitchAdapter";
	public static int positionSelected;
	public static  Dialog dialogSwitch;
	private EditText switchName;

	public GatewaySwitchAdapter(Activity context, int resourceId,
			List<Switch> retrieved) {
		super(context, resourceId, retrieved);
		this.context = context;
		this.toShow = retrieved;
		this.resourceId = resourceId;
	}

	public long getItemId(int position) {
		return position;
	}

	/* private view holder class */
	private class SwitchHolder {
		TextView switchName;
		ImageButton editButton;
		ImageButton deleteButton;

	}

	public void update() {
		notifyDataSetChanged();
	}
	
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		View row = view;
		SwitchHolder holder = null;
		
		if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resourceId, null);

            holder = new SwitchHolder();
            holder.switchName = (TextView)row.findViewById(R.id.gatewaySwitchName);
            holder.deleteButton = (ImageButton)row.findViewById(R.id.delete_gateway_switch_button);
            holder.editButton = (ImageButton)row.findViewById(R.id.edit_gateway_switch_button);
            
            
            // Shows the dialog box when the user elects to edit the switch 
            // Takes appropriate input from the user and saves the switch 
            holder.editButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                	
                	dialogSwitch = new Dialog(getContext());
    	  			dialogSwitch.setContentView(R.layout.dialog_edit_switch);
    	  			dialogSwitch.setTitle("Edit Switch");
    	  			
    	  		//Handle for the textboxes
    	  			switchName = (EditText)dialogSwitch.findViewById(R.id.switchName);
    	  			Button dialogCancel = (Button) dialogSwitch.findViewById(R.id.dialog_cancel);
    	  			// if button is clicked, close the custom dialog
    	  			dialogCancel.setOnClickListener(new OnClickListener() {
    	  				@Override
    	  				public void onClick(View v) {
    	  					dialogSwitch.dismiss();
    	  				}
    	  			});
    	  			
    	  			Button  dialogEdit = (Button)dialogSwitch.findViewById(R.id.save_switch_button);
    	  			dialogEdit.setOnClickListener(new OnClickListener() {
    	  				@Override
    	  				public void onClick(View v) {
    	  					
    	  					// Call to async task to edit the switch
    	  					/*List<NameValuePair> param = new ArrayList<NameValuePair>(2);
    	  					param.add(new BasicNameValuePair("name", gatewayName.getText().toString()));
    	  					param.add(new BasicNameValuePair("user_id", SharedPrefUtils.getUserId(getContext())));*/
    	  					
    	  					//new EditGatewaySwitch().execute(param);
    	  					
    	  				}
    	  			});
                	
                	dialogSwitch.show();
                }
            });
            
            holder.deleteButton.setOnClickListener(new OnClickListener() {

            	 @Override
                 public void onClick(View v) {
            		 positionSelected = position;
            		 dialogSwitch = new Dialog(getContext());
     	  			dialogSwitch.setContentView(R.layout.dialog_delete_switch);
     	  			dialogSwitch.setTitle("Edit Switch");
     	  			
     	  		//Handle for the textboxes
     	  			switchName = (EditText)dialogSwitch.findViewById(R.id.switchName);
     	  			switchName.setText(toShow.get(positionSelected).getSwitchName());
     	  			Button dialogCancel = (Button) dialogSwitch.findViewById(R.id.dialog_cancel);
     	  			// if button is clicked, close the custom dialog
     	  			dialogCancel.setOnClickListener(new OnClickListener() {
     	  				@Override
     	  				public void onClick(View v) {
     	  					dialogSwitch.dismiss();
     	  				}
     	  			});
     	  			
     	  			Button  dialogDelete = (Button)dialogSwitch.findViewById(R.id.delete_switch_button);
     	  			dialogDelete.setOnClickListener(new OnClickListener() {
     	  				@Override
     	  				public void onClick(View v) {
     	  					// Call to asynctask to delete the switch
     	  				}
     	  			});
                 	dialogSwitch.show();
                 }  			
                
            });
            row.setTag(holder);
        }
        else
        {
            holder = (SwitchHolder)row.getTag();
        }
        
        Switch switchObject = toShow.get(position);
        holder.switchName.setText(switchObject.getSwitchName());
        return row;
		
	}
	
	
	/*
	 * Async class to edit information for a particular gateway
	 */
	class EditGatewaySwitch extends AsyncTask<List<NameValuePair>, String, String> {
		
		
		@Override
		protected void onPreExecute() {
		}

		protected String doInBackground(List<NameValuePair>... input) {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			HttpPut request = new HttpPut(Constants.URL_CREATE_GATEWAY + "/" + toShow.get(positionSelected).getSwitchId());
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
						
						String name = json.getJSONObject("gateway").getString("name");
						toShow.get(positionSelected).setSwitchName(name);
						//Log.d(TAG,"Response" + name);
						
						dialogSwitch.dismiss();
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
