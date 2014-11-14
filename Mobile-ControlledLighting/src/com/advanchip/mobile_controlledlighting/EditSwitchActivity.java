package com.advanchip.mobile_controlledlighting;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

//edit switch activity
public class EditSwitchActivity extends Activity implements OnItemSelectedListener  {

	private Spinner spinner1, spinner2;
	private Button btnSubmit; 
	 
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_switch);
		
		addItemsOnSpinner2();
		addListenerOnButton();
		addListenerOnSpinnerItemSelection();

	}
	// add items into spinner dynamically
	private void addItemsOnSpinner2() {
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		List<String> list = new ArrayList<String>();
		list.add("bedroom");
		list.add("kitchen");
		list.add("living room");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
		android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(dataAdapter);
		
	}
	
	public void addListenerOnSpinnerItemSelection() {
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
		
	}

	public void addListenerOnButton() {
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
	 
		btnSubmit.setOnClickListener(new OnClickListener() {
			 
			  public void onClick(View v) {
				  Toast.makeText(EditSwitchActivity.this,
							"OnClickListener : " + 
					                "\nSpinner 1 : "+ String.valueOf(spinner1.getSelectedItem()) + 
					                "\nSpinner 2 : "+ String.valueOf(spinner2.getSelectedItem()),
								Toast.LENGTH_SHORT).show();
			  }
			  
		});
			  }
		


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_switch, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
			
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
}
