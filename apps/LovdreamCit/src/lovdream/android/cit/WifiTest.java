/**
 * 
 */
package lovdream.android.cit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WifiTest extends Activity {
	
	private Button mWifiTest;
	private Button mWifiAdjustment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_test);
		mWifiTest = (Button)findViewById(R.id.wifi_test);
		mWifiAdjustment = (Button)findViewById(R.id.wifi_adjustment);
		
		mWifiTest.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent("android.settings.WIFI_SETTINGS");
				startActivity(intent);
			}
		});
		mWifiAdjustment.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent().setClass(WifiTest.this, WifiAdjustment.class);
				startActivity(intent);
			}
		});
	}
}
