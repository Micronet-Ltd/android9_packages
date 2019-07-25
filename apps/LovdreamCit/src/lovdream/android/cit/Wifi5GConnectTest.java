package lovdream.android.cit;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Wifi5GConnectTest extends Activity {
	
	private TextView mTextView;
	private Button mBtnSuccess, mBtnFail;
	private WifiManager mWifiManager; 
    private WifiInfo mWifiInfo; 
    private List<ScanResult> mWifiList; 
    private String mSSID1 = "lovdream5G";
    private String mPassWrod1 = "";
    
    private int mNetworkId = -1;
    private WifiManager.ActionListener mForgetListener;

	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.wifi_connect_test);
		initLayout();
		initWifi();
	}
	
	private void initLayout(){
		mTextView = (TextView)findViewById(R.id.textView1);
		mBtnSuccess = (Button)findViewById(R.id.success_test);
		mBtnFail = (Button)findViewById(R.id.fail_test);
		mBtnSuccess.setEnabled(false);
		
		final Bundle b = new Bundle();
		final Intent intent = new Intent();
		mBtnSuccess.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				b.putInt("test_result", 1);
				intent.putExtras(b);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		mBtnFail.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				b.putInt("test_result", 0);
				intent.putExtras(b);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	
	private void initWifi(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerReceiver(mWifiConnectReceiver, filter);
		mForgetListener = new WifiManager.ActionListener() {
	         @Override
	         public void onSuccess() {
	         }
	         @Override
	         public void onFailure(int reason) {
	        	 android.util.Log.e("...wt","fail to forget network -- "+reason);
	         }
	     };
       mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE); 
       if(!mWifiManager.isWifiEnabled()){
    	   mWifiManager.setWifiEnabled(true);
       }
       doAfterScanDone();
	}
	
	private void doAfterScanDone(){
		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE); 
		mWifiInfo = mWifiManager.getConnectionInfo();
	       if(mWifiInfo.getSSID().contains(mSSID1)){
	    	   mNetworkId = mWifiInfo.getNetworkId();
	    	   if(mNetworkId != -1){
	    		   return;
	    	   }
	       }
		mWifiList = mWifiManager.getScanResults(); 
		int i;
		int count = mWifiList.size();
		if(count == 0){
			mWifiManager.startScan();
			return;
		}
		for (i = 0; i < count; i++) {
			if (mWifiList.get(i).SSID.equals(mSSID1)) {
				addNetwork(CreateWifiInfo(mSSID1, mPassWrod1, 1));
				break;
			}
		}
		 if ( count != 0 && i == count) {
				mTextView.setText(R.string.wifi_scan_fail);
		}
	}
	
	private BroadcastReceiver mWifiConnectReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
	            Parcelable parcelableExtra = intent
	                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
	            if (null != parcelableExtra) {
	                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
	                State state = networkInfo.getState();
	                boolean isConnected = state == State.CONNECTED;
	                //fix bug is connect Lovdream5G  only by zzj start
	                String extraInfo=networkInfo.getExtraInfo().toString();
	                if (extraInfo !=null && extraInfo.contains(mSSID1) && isConnected) {
	                	mTextView.setText(context.getString(R.string.wifi_connect_success, mWifiInfo.getSSID()));
	                	mBtnSuccess.setEnabled(true);
	                	if(CITMain.CITtype == CITMain.PCBA_AUTO_TEST
								|| CITMain.CITtype == CITMain.MACHINE_AUTO_TEST){
	                		mBtnSuccess.performClick();
	                	}
	                }else if(extraInfo !=null && !extraInfo.contains(mSSID1) && isConnected ){
	                	mWifiManager.disconnect();
	                	doAfterScanDone();
	                }else{
	                	doAfterScanDone();
	                }
	                //fix bug is connect Lovdream5G  only by zzj end
	            }
	        }else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
	        	doAfterScanDone();
	        }
		}
	};
	
    public void addNetwork(WifiConfiguration wcg) { 
    	android.util.Log.i("....zzj","------addNetwork------wcg--"+wcg);
     mNetworkId = mWifiManager.addNetwork(wcg); 
     boolean enable =  mWifiManager.enableNetwork(mNetworkId, true); 
     if(!enable){
    	 mTextView.setText(R.string.wifi_enable_fail);
     }
    } 
 
	public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) 
    { 
		android.util.Log.i("....zzj","------CreateWifiInfo------wcg--"+SSID);
          WifiConfiguration config = new WifiConfiguration();   
           config.allowedAuthAlgorithms.clear(); 
           config.allowedGroupCiphers.clear(); 
           config.allowedKeyManagement.clear(); 
           config.allowedPairwiseCiphers.clear(); 
           config.allowedProtocols.clear(); 
          config.SSID = "\"" + SSID + "\"";   
          
//          WifiConfiguration tempConfig = this.IsExsits(SSID);           
//          if(tempConfig != null) {  
//        	  mWifiManager.forget(tempConfig.networkId, mForgetListener);
//          }
          
          if(Type == 1) //WIFICIPHER_NOPASS
          { 
               //config.wepKeys[0] = ""; 
               config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); 
               //config.wepTxKeyIndex = 0; 
          } 
          if(Type == 2) //WIFICIPHER_WEP
          { 
              config.hiddenSSID = true;
              config.wepKeys[0]= "\""+Password+"\""; 
              config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED); 
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP); 
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP); 
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40); 
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104); 
              config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); 
              config.wepTxKeyIndex = 0; 
          } 
          if(Type == 3) //WIFICIPHER_WPA
          { 
          config.preSharedKey = "\""+Password+"\""; 
          config.hiddenSSID = true;   
          config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);   
          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);                         
          config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);                         
          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);                    
          //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);  
          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
          config.status = WifiConfiguration.Status.ENABLED;   
          }
           return config; 
    }
	
//	private WifiConfiguration IsExsits(String SSID)  
//    {  
//        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();  
//           for (WifiConfiguration existingConfig : existingConfigs)   
//           {  
//             if (existingConfig.SSID.equals("\""+SSID+"\""))  
//             {  
//                 return existingConfig;  
//             }  
//           }  
//        return null;   
//    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mNetworkId != -1){
			mWifiManager.forget(mNetworkId, mForgetListener);
		}
		if(mWifiConnectReceiver != null){
			unregisterReceiver(mWifiConnectReceiver);
		}
		if(mWifiManager.isWifiEnabled()){
	    	   mWifiManager.setWifiEnabled(false);
	    }
	}
}
