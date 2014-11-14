package com.advanchip.mobile_controlledlighting.adapters;

import com.advanchip.mobile_controlledlighting.R;
import com.advanchip.mobile_controlledlighting.R.id;
import com.advanchip.mobile_controlledlighting.R.layout;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ManageUsersAdapter extends ArrayAdapter<String> {
	private final Activity context;
	private final String[] web;

	public ManageUsersAdapter(Activity context, String[] web) {
		super(context, R.layout.manage_users_list_item, web);
		this.context = context;
		this.web = web;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.manage_users_list_item, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		txtTitle.setText(web[position]);
		return rowView;
	}
}