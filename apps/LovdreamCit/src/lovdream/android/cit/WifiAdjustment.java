/**
 * 
 */
package lovdream.android.cit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.os.SystemProperties;

public class WifiAdjustment extends Activity {
	
	private Button mOpenWifi;
	private Button mCloseWifi;
	private Button mAdjustWifi;
	private TextView mAdjustmentResult;
	public static boolean isWifiCalSuccess = SystemProperties.get("ro.product.wifi_cal_ok","0").equals("1");
	private static final int WifiCalSuccess = 1;
	private static final int WifiCalFail = 0;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case WifiCalSuccess:
				mAdjustmentResult.setText(R.string.adjustment_result_success);
				break;
			case WifiCalFail:
				mAdjustmentResult.setText(R.string.adjustment_result_fail);
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
		setContentView(R.layout.wifi_adjustment);
		mOpenWifi = (Button)findViewById(R.id.open_wifi);
		mCloseWifi = (Button)findViewById(R.id.close_wifi);
		mAdjustWifi = (Button)findViewById(R.id.adjustment);
		mAdjustmentResult = (TextView)findViewById(R.id.adjustment_result);
		mOpenWifi.setVisibility(View.GONE);
		mCloseWifi.setVisibility(View.GONE);
		mAdjustmentResult.setVisibility(View.INVISIBLE);
		mAdjustWifi.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mAdjustmentResult.setVisibility(View.VISIBLE);
				mAdjustmentResult.setText(R.string.adjustment_result);
				SystemProperties.set("ctl.start", "wifi_cal");
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
							if (SystemProperties.get("ro.product.wifi_cal_ok","0").equals("1")) {
								msg.what = WifiCalSuccess;
								mHandler.sendMessage(msg);
								i = 0;
							}else {
								i--;
								if (i==0) {
									msg.what = WifiCalFail;
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
