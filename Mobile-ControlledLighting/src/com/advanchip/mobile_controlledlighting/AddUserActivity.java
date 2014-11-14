package com.advanchip.mobile_controlledlighting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class AddUserActivity extends Activity{
	Context ctx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ctx = this;
		setContentView(R.layout.activity_add_user);
	}
}
