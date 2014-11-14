package com.advanchip.mobile_controlledlighting;


import com.advanchip.mobile_controlledlighting.adapters.ManageUsersAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

	public class ManageUsersActivity extends Activity {
	  ListView list;
	  String[] web = {
	    "Anu",
	      "Alicejhgvhvvvvvvvvvvvvvvvvvvvvvvvjhvjhvjhvv",
	      "Ashu",
	      "Sagar",
	      "Priyank",
	      "Sayali",
	      "Corey",
	      "Anu",
	      "Alice",
	      "Ashu",
	      "Sagar",
	      "Priyank",
	      "Sayali",
	      "Corey"
	  } ;
	  
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	      // Inflate the menu items for use in the action bar
	      MenuInflater inflater = getMenuInflater();
	      inflater.inflate(R.menu.users, menu);
	      return super.onCreateOptionsMenu(menu);
	  }
	  
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	      // Handle presses on the action bar items
	      switch (item.getItemId()) {
	          case R.id.action_add_user:
	              Toast.makeText(getApplicationContext(), 
	            		  "action_add_user clicked", Toast.LENGTH_LONG).show();
	              return true;
	          default:
	              return super.onOptionsItemSelected(item);
	      }
	  }
	  
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_manage_users);
	    ManageUsersAdapter adapter = new
	    		ManageUsersAdapter(ManageUsersActivity.this, web);
	    list=(ListView)findViewById(R.id.list);
	        list.setAdapter(adapter);
	        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	                @Override
	                public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
	                    Toast.makeText(ManageUsersActivity.this, "You Clicked at " +web[+ position], Toast.LENGTH_SHORT).show();
	                }
	            });
	  }
	}