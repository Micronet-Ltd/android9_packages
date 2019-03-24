/**
 * 
 */
package lovdream.android.cit;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.SystemProperties;

public class BluetoothDut extends Activity {
	private Button mOpenBluetooth;
	private Button mCloseBluetooth;
	private Button mDutBluetooth;
	private TextView mDutResult;
	public static boolean isBluetoothCalSuccess = SystemProperties.get("ro.product.bluetooth_dut","0").equals("1");
	private static final int BluetoothCalSuccess = 1;
	private static final int BluetoothCalFail = 0;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case BluetoothCalSuccess:
				mDutResult.setText(R.string.bluetooth_dut_result_success);
				break;
			case BluetoothCalFail:
				mDutResult.setText(R.string.bluetooth_dut_result_fail);
				break;
			default:
				break;
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_dut);
		mOpenBluetooth = (Button)findViewById(R.id.open_bluetooth);
		mCloseBluetooth = (Button)findViewById(R.id.close_bluetooth);
		mDutBluetooth = (Button)findViewById(R.id.bluetooth_dut_now);
		mDutResult = (TextView)findViewById(R.id.adjustment_result);
		mOpenBluetooth.setVisibility(View.GONE);
		mCloseBluetooth.setVisibility(View.GONE);
		mDutResult.setVisibility(View.INVISIBLE);
		mDutBluetooth.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDutResult.setVisibility(View.VISIBLE);
				mDutResult.setText(R.string.adjustment_result);
				SystemProperties.set("ctl.start", "bluetooth_dut");
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						int i = 5;
						while (i != 0) {
							try {
								Thread.sleep(2000);
							} catch (Exception e) {
								// TODO: handle exception
							}
							Message msg = new Message();
							if (SystemProperties.get("ro.product.bluetooth_dut","0").equals("1")) {
								msg.what = BluetoothCalSuccess;
								mHandler.sendMessage(msg);
								i = 0;
							}else {
								i--;
								if (i==0) {
									msg.what = BluetoothCalFail;
									mHandler.sendMessage(msg);
								}
							}
						}
					}
				}).start();
			}
		});
	}
}
