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
import com.advanchip.mobile_controlledlighting.adapters.ManageGatewayAdapter.EditGateways;
import com.advanchip.mobile_controlledlighting.models.Room;
import com.advanchip.mobile_controlledlighting.utils.Constants;
import com.advanchip.mobile_controlledlighting.utils.SharedPrefUtils;
import com.advanchip.mobile_controlledlighting.utils.Utils;

public class FloorRoomAdapter extends ArrayAdapter<Room> {
	ProgressDialog dialog = null;
	Context context;
	int resourceId;
	List<Room> toShow = null;
	private final static String TAG = ":FloorRoomAdapter";
	public static int positionSelected;
	public static Dialog dialogRoom;
	private EditText roomName;
	private static String gatewaySerial;
	private static String floorId;

	public void setSerialAndId(String gatewaySerial, String floorId) {
		this.gatewaySerial = gatewaySerial;
		this.floorId = floorId;
	}

	public FloorRoomAdapter(Activity context, int resourceId,
			List<Room> retrieved) {
		super(context, resourceId, retrieved);
		this.context = context;
		this.toShow = retrieved;
		this.resourceId = resourceId;
	}

	public long getItemId(int position) {
		return position;
	}

	/* private view holder class */
	private class RoomHolder {
		TextView roomName;
		ImageButton editButton;
		ImageButton deleteButton;

	}

	public void update() {
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		View row = view;
		RoomHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(resourceId, null);

			holder = new RoomHolder();
			holder.roomName = (TextView) row.findViewById(R.id.floorRoomName);
			holder.deleteButton = (ImageButton) row
					.findViewById(R.id.delete_floor_room_button);
			holder.editButton = (ImageButton) row
					.findViewById(R.id.edit_floor_room_button);

			holder.editButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					positionSelected = position;
					dialogRoom = new Dialog(getContext());
					dialogRoom.setContentView(R.layout.dialog_edit_room);
					dialogRoom.setTitle("Edit Room");

					// Handle for the textboxes
					roomName = (EditText) dialogRoom.findViewById(R.id.roomName);

					Button dialogButton = (Button) dialogRoom
							.findViewById(R.id.dialog_cancel);
					// if button is clicked, close the custom dialog
					dialogButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialogRoom.dismiss();
						}
					});

					Button saveGateway = (Button) dialogRoom
							.findViewById(R.id.save_room_button);
					// if button is clicked, close the custom dialog
					saveGateway.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							List<NameValuePair> param = new ArrayList<NameValuePair>(
									2);
							param.add(new BasicNameValuePair("name", roomName.getText().toString()));
							new EditRoom().execute(param);
						}
					});
					dialogRoom.show();

				}
			});

			holder.deleteButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					positionSelected = position;
					dialogRoom = new Dialog(getContext());
					dialogRoom.setContentView(R.layout.dialog_delete_room);
					dialogRoom.setTitle("Delete floor? ");

					// Handle for the textboxes
					roomName = (EditText) dialogRoom
							.findViewById(R.id.roomName);
					roomName.setText(toShow.get(position).getRoomName());
					Button dialogButton = (Button) dialogRoom
							.findViewById(R.id.dialog_cancel);
					// if button is clicked, close the custom dialog
					dialogButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialogRoom.dismiss();
						}
					});
					Button deleteGateway_btn = (Button) dialogRoom
							.findViewById(R.id.delete_room_button);
					// if button is clicked, close the custom dialog
					deleteGateway_btn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							List<NameValuePair> param = new ArrayList<NameValuePair>(
									2);
							param.add(new BasicNameValuePair("name", roomName
									.getText().toString()));
							new DeleteRoom().execute(param);
							
						}
					});
					dialogRoom.show();

				}

			});

			row.setTag(holder);
		} else {
			holder = (RoomHolder) row.getTag();
		}

		Room roomObject = toShow.get(position);
		holder.roomName.setText(roomObject.getRoomName());
		return row;

	}

	/*
	 * Async class to edit information for a particular gateway
	 */
	class EditRoom extends AsyncTask<List<NameValuePair>, String, String> {

		@Override
		protected void onPreExecute() {
		}

		protected String doInBackground(List<NameValuePair>... input) {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			HttpPut request = new HttpPut(Constants.URL_CREATE_GATEWAY + "/"
					+ gatewaySerial + "/floors/" + floorId + "/rooms/"
					+ toShow.get(positionSelected).getRoomId());
			
			request.setHeader("Authorization",
					SharedPrefUtils.getTokenType(getContext()) + " "
							+ SharedPrefUtils.getToken(getContext()));

			HttpResponse response;
			try {
				// Log.d(TAG,"sending request");
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
				// Log.d(TAG,json.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			if (json == null) {
				Utils.displayDialog(getContext(),
						"Error in updating this Gateway",
						"Failed to get server response");
			} else {

				try {

					if (json.getInt("status") == 200) {

						String name = json.getJSONObject("room").getString(
								"name");
						toShow.get(positionSelected).setRoomName(name);
						dialogRoom.dismiss();
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
	class DeleteRoom extends AsyncTask<List<NameValuePair>, String, String> {

		@Override
		protected void onPreExecute() {
			// dialog.setTitle("Retrieving your gateways...");
			// dialog.show();
		}

		protected String doInBackground(List<NameValuePair>... input) {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			HttpDelete request = new HttpDelete(Constants.URL_CREATE_GATEWAY
					+ "/" + gatewaySerial + "/floors/" + floorId + "/rooms/"
					+ toShow.get(positionSelected).getRoomId());
			request.setHeader("Authorization",
					SharedPrefUtils.getTokenType(getContext()) + " "
							+ SharedPrefUtils.getToken(getContext()));

			HttpResponse response;
			try {
				// Log.d(TAG,"sending request");
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
				Utils.displayDialog(getContext(),
						"Error in deleting this floor",
						"Failed to get server response");
			} else {

				try {
					if (json.getInt("status") == 200) {
						toShow.remove(positionSelected);
						dialogRoom.dismiss();
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
