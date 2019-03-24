package lovdream.android.cit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class WifiTestNew extends Activity{

	private WifiLock mWifiLock;
	private WifiManager mWifiManager;
	private List<ScanResult> wifiScanResult;
	private TextView mTextView;
	private boolean scanResultAvailabe = false;
	IntentFilter mFilter = new IntentFilter();

	static final int SCAN_INTERVAL = 4000;
	static final int OUT_TIME = 30000;
	static final int DELAY_OUT_TIME = 2000;
	static final int DELAY_INTERVAL = 1000;

	Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			String s = getString(R.string.wifi_test_is_openning) + "\n\n" + "AP List:\n";
			if(wifiScanResult != null && wifiScanResult.size() > 0){
				for(int i = 0; i < wifiScanResult.size(); i++){
					s += " " + i + ": " + wifiScanResult.get(i).SSID + "\n\n";
					mTextView.setText(s);
				}
				Toast.makeText(WifiTestNew.this,R.string.wifi_test_success,Toast.LENGTH_SHORT).show();
				mDelayTimer.start();
			}else {
				finishTest(getString(R.string.wifi_test_scan_null));
			}
		}
	};

	CountDownTimer mCountDownTimer = new CountDownTimer(OUT_TIME,SCAN_INTERVAL){
		private int tickCount = 0;
		@Override
		public void onTick(long arg0){
			tickCount++;
			if(mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED){
				//mWifiManager.startScanActive();
				mWifiManager.startScan();
                /*When screen is dim, SCAN_RESULTS_AVAILABLE_ACTION cannot be got.So get it actively*/
				/*At least conduct startScan() 3 times to ensure wifi's scan*/
				if(tickCount >= 4 && !scanResultAvailabe){
					wifiScanResult = mWifiManager.getScanResults();
					scanResultAvailabe = true;
					mHandler.sendEmptyMessage(0);
				}
			}
		}

		@Override
		public void onFinish(){
			if((wifiScanResult == null) || (wifiScanResult.size() == 0)){
				finishTest(getString(R.string.wifi_test_scan_null));
			}
		}
	};

	CountDownTimer mDelayTimer = new CountDownTimer(DELAY_OUT_TIME,DELAY_INTERVAL){
		@Override
		public void onTick(long arg0){
			/*do nothing,just delay*/
		}
		@Override
		public void onFinish(){
			finishTest(getString(R.string.wifi_test_success));
		}
	};

	private BroadcastReceiver mReceiver = new BroadcastReceiver(){
		public void onReceive(Context c, Intent intent){
			if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())){
				if(!scanResultAvailabe){
					wifiScanResult = mWifiManager.getScanResults();
					scanResultAvailabe = true;
					mHandler.sendEmptyMessage(0);
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setContentView(R.layout.wifi_test_new);
		mTextView = (TextView) findViewById(R.id.wifi_hint);
		mTextView.setText(getString(R.string.wifi_test_is_openning));

		mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		
		mWifiLock = mWifiManager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "WiFi");
		if(false == mWifiLock.isHeld()){
			mWifiLock.acquire();
		}

		switch (mWifiManager.getWifiState()){
			case WifiManager.WIFI_STATE_DISABLED:
				enableWifi(true);
				break;
			case WifiManager.WIFI_STATE_DISABLING:
				finishTest(getString(R.string.wifi_test_is_closing));
				break;
			case WifiManager.WIFI_STATE_UNKNOWN:
				finishTest(getString(R.string.wifi_test_state_unknown));
				break;
			default:
				break;
		}
		
		mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

		mCountDownTimer.start();
	}

	private void finishTest(String msg){
		if(msg != null){
			Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
		}
		finish();
	}

	private void enableWifi(boolean isEnable){
		if(mWifiManager != null){
			mWifiManager.setWifiEnabled(isEnable);
		}
	}

	@Override
	public void onResume(){
		registerReceiver(mReceiver,mFilter);
		super.onResume();
	}

	@Override
	public void onPause(){
		unregisterReceiver(mReceiver);
		super.onPause();
	}

	@Override
	public void finish(){
		/*manufactory's requirement,close wifi after test unconditional,for save charge?*/
		enableWifi(false);
		Bundle bundle = new Bundle();
		bundle.putInt("test_result",(wifiScanResult != null && wifiScanResult.size() > 0) ? 1 : 0);
		setResult(RESULT_OK,new Intent().putExtras(bundle));
		/*user may press back key while showing the AP list*/
		try{
			mCountDownTimer.cancel();
			if(true == mWifiLock.isHeld()){
				mWifiLock.release();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		super.finish();
	}
}
