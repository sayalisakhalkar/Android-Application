package com.advanchip.mobile_controlledlighting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class GatewayRouteActivity extends Activity {
	
	private Button switchRoute,roomRoute,floorRoute;
	String gatewaySerial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gateway_route);
		
		Bundle extras = getIntent().getExtras();
	    if(extras != null) {
	    	gatewaySerial = extras.getString("gatewaySerial");
	    }
	    Toast.makeText(getApplicationContext(), gatewaySerial, Toast.LENGTH_SHORT).show();
		
		switchRoute = (Button)findViewById(R.id.switchRouter);
		roomRoute = (Button)findViewById(R.id.roomRouter);
		floorRoute = (Button) findViewById(R.id.floorRouter);
		
		switchRoute.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click   

                Intent activityChangeIntent = new Intent(GatewayRouteActivity.this, GatesInGateway.class);
                activityChangeIntent.putExtra("gatewaySerial", gatewaySerial);
                startActivity(activityChangeIntent);
            }
        });
		
		floorRoute.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click   

                Intent activityChangeIntent = new Intent(GatewayRouteActivity.this, FloorsInGatewayActivity.class);
                activityChangeIntent.putExtra("gatewaySerial", gatewaySerial);
                startActivity(activityChangeIntent);
            }
        });
		
		
		
	}
}
