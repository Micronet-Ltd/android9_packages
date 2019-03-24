/**
 * 
 */
package lovdream.android.cit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BluetoothTest extends Activity {
	private Button mBluetoothtest;
	private Button mBluetoothDut;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_test);
		mBluetoothtest = (Button)findViewById(R.id.bluetooth_test);
		mBluetoothDut = (Button)findViewById(R.id.bluetooth_dut);
		
		mBluetoothtest.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent("android.settings.BLUETOOTH_SETTINGS");
				startActivity(intent);
			}
		});
		mBluetoothDut.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent().setClass(BluetoothTest.this, BluetoothDut.class);
				startActivity(intent);
			}
		});
	}
}
