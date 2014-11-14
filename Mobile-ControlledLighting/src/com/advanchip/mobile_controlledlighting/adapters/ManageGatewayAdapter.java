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
import android.content.Intent;
import android.os.AsyncTask;
import android.sax.StartElementListener;
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
import android.widget.Toast;

import com.advanchip.mobile_controlledlighting.ManageGateway;
import com.advanchip.mobile_controlledlighting.R;
import com.advanchip.mobile_controlledlighting.models.Gateway;
import com.advanchip.mobile_controlledlighting.utils.Constants;
import com.advanchip.mobile_controlledlighting.utils.SharedPrefUtils;
import com.advanchip.mobile_controlledlighting.utils.Utils;

public class ManageGatewayAdapter extends ArrayAdapter<Gateway> {

	ProgressDialog dialog = null;
	Context context;
	int resourceId;
	List<Gateway> toShow = null;
	int p;
	private EditText gatewayName;
	public static int positionSelected;
	public static final String TAG = "ManageGatewayAdapter";
	public static  Dialog dialogGateway;
	
	public  ManageGatewayAdapter(Activity context, int resourceId, List<Gateway> retrieved) {
		super(context, resourceId,retrieved);
		this.context = context;
		this.toShow = retrieved;
		this.resourceId = resourceId;
	}
	
	public void createGateway(Gateway gateway){		
		toShow.add(0,gateway);
		notifyDataSetChanged();
	}
	
	
	public static void closeDialog() {
		dialogGateway.dismiss();
		//Log.d(TAG,"Closing");
	}
	
	 public long getItemId(int position) {
	        return position;
	 }
	
	/*private view holder class*/
    private class GatewayHolder {
        TextView gatewayName;
        ImageButton editButton;
        ImageButton deleteButton;
        
    }
    
    public void update(){
    	notifyDataSetChanged();
    }
	

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		View row = view;
		GatewayHolder holder = null;
		Log.d(TAG,"GetView:" + String.valueOf(position));
		
		if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resourceId, null);

            holder = new GatewayHolder();
            holder.gatewayName = (TextView)row.findViewById(R.id.txt);
            holder.deleteButton = (ImageButton)row.findViewById(R.id.delete_gateway_button);
            holder.editButton = (ImageButton)row.findViewById(R.id.edit_gateway_button);
            
            holder.editButton.setOnClickListener(new OnClickListener() {
            

                @Override
                public void onClick(View v) {
                	
                	dialogGateway = new Dialog(getContext());
    	  			dialogGateway.setContentView(R.layout.dialog_edit_gateway);
    	  			dialogGateway.setTitle("Edit Gateway");
    	  			
    	  			//Handle for the textboxes
    	  			gatewayName = (EditText)dialogGateway.findViewById(R.id.gatewayName);
    	  			
    	   
    	  			Button dialogButton = (Button) dialogGateway.findViewById(R.id.dialog_cancel);
    	  			// if button is clicked, close the custom dialog
    	  			dialogButton.setOnClickListener(new OnClickListener() {
    	  				@Override
    	  				public void onClick(View v) {
    	  					dialogGateway.dismiss();
    	  				}
    	  			});
    	  			
    	  			Button saveGateway = (Button) dialogGateway.findViewById(R.id.save_gateway_button);
    	  			// if button is clicked, close the custom dialog
    	  			saveGateway.setOnClickListener(new OnClickListener() {
    	  				@Override
    	  				public void onClick(View v) {
    	  					positionSelected = position;
    	  					Log.d(TAG,String.valueOf(positionSelected));
    	  					List<NameValuePair> param = new ArrayList<NameValuePair>(2);
    	  					param.add(new BasicNameValuePair("name", gatewayName.getText().toString()));
    	  					param.add(new BasicNameValuePair("gatewayId", toShow.get(position).getSerialNumber()));
    	  					//Log.d(TAG,toShow.get(position).getSerialNumber());
    	  					param.add(new BasicNameValuePair("user_id", SharedPrefUtils.getUserId(getContext())));
    	  					new EditGateways().execute(param);
    	  				}
    	  			});
    	  			dialogGateway.show();
                }
            });
            
            holder.deleteButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                	dialogGateway = new Dialog(getContext());
    	  			dialogGateway.setContentView(R.layout.dialog_delete_gateway);
    	  			dialogGateway.setTitle("Delete gateway? ");
    	  			
    	  			//Handle for the textboxes
    	  			gatewayName = (EditText)dialogGateway.findViewById(R.id.gatewayName);
    	  			gatewayName.setText(toShow.get(position).getGatewayName());
    	  			Button dialogButton = (Button) dialogGateway.findViewById(R.id.dialog_cancel);
    	  			// if button is clicked, close the custom dialog
    	  			dialogButton.setOnClickListener(new OnClickListener() {
    	  				@Override
    	  				public void onClick(View v) {
    	  					dialogGateway.dismiss();
    	  				}
    	  			});
    	  			Button deleteGateway_btn = (Button) dialogGateway.findViewById(R.id.delete_gateway_button);
    	  			// if button is clicked, close the custom dialog
    	  			deleteGateway_btn.setOnClickListener(new OnClickListener() {
    	  				@Override
    	  				public void onClick(View v) {
    	  					positionSelected = position;
    	  					Log.d(TAG,String.valueOf(positionSelected));
    	  					//Toast.makeText(getContext(), position, Toast.LENGTH_SHORT).show();
    	  					new DeleteGateways().execute();
    	  					//Toast.makeText(getContext(), toShow.get(position).getGatewayName(), Toast.LENGTH_SHORT).show();
    	  					
    	  				}
    	  			});
    	  			dialogGateway.show();  			
                }
            });
            
            row.setTag(holder);
        }
        else
        {
            holder = (GatewayHolder)row.getTag();
        }
        
        Gateway gateway = toShow.get(position);
        holder.gatewayName.setText(gateway.getGatewayName());
        
        return row;
	}
	
	
	/*
	 * Async class to edit information for a particular gateway
	 */
	class EditGateways extends AsyncTask<List<NameValuePair>, String, String> {
		
		
		@Override
		protected void onPreExecute() {
		}

		protected String doInBackground(List<NameValuePair>... input) {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			HttpPut request = new HttpPut(Constants.URL_CREATE_GATEWAY + "/" + toShow.get(positionSelected).getSerialNumber());
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
						toShow.get(positionSelected).setGatewayName(name);
						//Log.d(TAG,"Response" + name);
						
						closeDialog();
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
	class DeleteGateways extends AsyncTask<List<NameValuePair>, String, String> {
		
		
		@Override
		protected void onPreExecute() {
			//dialog.setTitle("Retrieving your gateways...");
			//dialog.show();
		}

		protected String doInBackground(List<NameValuePair>... input) {

			
			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			HttpDelete request = new HttpDelete(Constants.URL_CREATE_GATEWAY + "/" + toShow.get(positionSelected).getSerialNumber().trim());
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
			//dialog.dismiss();

			
			// JSONObject responceJson = new JSONObject();
			JSONObject json = null;
			try {
				json = new JSONObject(returned);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			if (json == null) {
				Utils.displayDialog(getContext(), "Error in deleting this Gateway",
						"Failed to get server response");
			} else {
				
				try {
					if (json.getInt("status") == 200) {
						toShow.remove(positionSelected);
						closeDialog();
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