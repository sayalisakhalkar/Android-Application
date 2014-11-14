package com.advanchip.mobile_controlledlighting.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class HTTPPostRequest {

	public static final String TAG = ": HTTPOSTrequest";
	private String errorMessage = null;

	/**
	 * executeHTTPPost executes the HTTPOST request to the server
	 * 
	 * @param nameValuePairs
	 *            data parameters
	 * @param serverURL
	 *            url of the server
	 * @return the response from the server
	 */
	public String executeHTTPPost(List<NameValuePair> nameValuePairs,
			String serverURL, Activity activity) {
		if (!Utils.haveNetworkConnection(activity.getApplicationContext())) {
			Log.e(TAG, "No Internet connection. Cancelled the http request");
			return "No Internet connection";
		}

		HttpPost request = new HttpPost(serverURL);

		try {
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {

			e1.printStackTrace();
			errorMessage = "Failed to set the parameters for the HTTPOST request";
			Log.e(Utils.TAG, errorMessage);
			return errorMessage;
		}

		// receiving the response

		HttpResponse response = null;

		HttpParams httpParams = new BasicHttpParams();

		HttpClient client = new DefaultHttpClient(httpParams);

		try {

			response = client.execute(request);

		} catch (Exception e) {

			e.printStackTrace();
			errorMessage = "The execution of the POST request failed";
			Log.e(Utils.TAG, errorMessage);
			return errorMessage;

		} catch (OutOfMemoryError e2) {
			e2.printStackTrace();
			return null;
		}

		String output = null;

		try {

			output = EntityUtils.toString(response.getEntity());

			Log.i(Utils.TAG + this, "server response: " + output);

		} catch (IllegalStateException e) {

			e.printStackTrace();
			errorMessage = "Error occured while converting the server output into the String: Illegal statement";
			Log.e(Utils.TAG, errorMessage);
			return errorMessage;

		} catch (IOException e) {

			e.printStackTrace();
			errorMessage = "Error occured while converting the server output into the String: IOException";
			Log.e(Utils.TAG, errorMessage);
			return errorMessage;

		}

		return output;
	}

	/**
	 * executeHTTPPost executes the HTTPOST request to the server (used for
	 * uploading images)
	 * 
	 * @param MultipartEntity
	 *            nameValuePairs data parameters
	 * @param serverURL
	 *            url of the server
	 * @return the response from the server
	 */
	public String executeHTTPPost(MultipartEntity entity, String serverURL) {

		// Add your data

		HttpPost request = new HttpPost(serverURL);

		request.setEntity(entity);

		// receiving the response

		HttpResponse response = null;

		HttpParams httpParams = new BasicHttpParams();

		HttpClient client = new DefaultHttpClient(httpParams);

		try {

			response = client.execute(request);

		} catch (Exception e) {

			e.printStackTrace();
			errorMessage = "The execution of the POST request failed";
			Log.e(Utils.TAG, errorMessage);
			return errorMessage;

		}

		String output = null;

		try {

			output = EntityUtils.toString(response.getEntity());

			Log.i(Utils.TAG, "server response: " + output);

		} catch (IllegalStateException e) {

			e.printStackTrace();
			errorMessage = "Error occured while converting the server output into the String: Illegal statement";
			Log.e(Utils.TAG, errorMessage);
			return errorMessage;

		} catch (IOException e) {

			e.printStackTrace();
			errorMessage = "Error occured while converting the server output into the String: IOException";
			Log.e(Utils.TAG, errorMessage);
			return errorMessage;

		}

		return output;
	}

}
