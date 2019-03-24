/**
 * 
 */
package lovdream.android.cit;

import lovdream.android.cit.BluetoothDut;
import lovdream.android.cit.BluetoothTest;
import lovdream.android.cit.WifiAdjustment;
import lovdream.android.cit.WifiTest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WifiAndBt extends Activity {
	private Button mWifiAdjustment;
	private Button mBluetoothDut;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_and_bt);
		mWifiAdjustment = (Button)findViewById(R.id.wifi_adjustment);
		mBluetoothDut = (Button)findViewById(R.id.bluetooth_dut);
		
		mWifiAdjustment.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent().setClass(WifiAndBt.this, WifiAdjustment.class);
				startActivity(intent);
			}
		});
		mBluetoothDut.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent().setClass(WifiAndBt.this, BluetoothDut.class);
				startActivity(intent);
			}
		});
	}
}
