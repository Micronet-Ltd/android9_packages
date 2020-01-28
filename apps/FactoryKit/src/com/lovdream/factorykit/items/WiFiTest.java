
package com.lovdream.factorykit.items;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.LayoutInflater;

import java.util.List;

import com.lovdream.factorykit.R;
import com.lovdream.factorykit.Utils;
import com.lovdream.factorykit.TestItemBase;
import com.swfp.model.WifiModel;
import com.swfp.model.WifiModel.WifiBack;
import com.swfp.utils.WifiUtil;


public class WiFiTest extends TestItemBase implements WifiBack{

	WifiUtil wifiUtil;
	WifiModel wifiModel;
    static Context mContext = null;
    TextView mTextView;
    
	
	@Override
	public String getKey(){
		return "wifi_test";
	}

	@Override
	public String getTestMessage(){
		return mContext.getString(R.string.wifi_test_title);
	}

	@Override
	public void onStartTest(){

		mContext = getActivity();
		initWifiModel();
		wifiUtil = new WifiUtil(mContext,wifiModel).start();
	
	}
	
	private void initWifiModel(){
		wifiModel = new WifiModel(this);
		wifiModel.testSSID = getTestSSID();
	}

	@Override
	public void onStopTest(){
		wifiUtil.stop();
	}

	@Override
	public View getTestView(LayoutInflater inflater){
		View v = inflater.inflate(R.layout.wifi_test,null);
		bindView(v);
		return v;
	}


	 
	void bindView(View v) {

		mTextView = (TextView) v.findViewById(R.id.wifi_hint);
		mTextView.setText(getString(R.string.wifi_test_mesg));
	}



	protected String getTestSSID(){
		return "lovdream";
	}
	
	protected boolean  isFrequencyRight(int frequency){
		return frequency>2000 && frequency<5000;
	}

	

	@Override
	public void pass() {
		postSuccess();
	}

	@Override
	public void failed(String msg) {
		toast(msg);
		postFail();
	}

	@Override
	public boolean isFrequencyNotErr(int fre) {
		return isFrequencyRight(fre);
	}

	@Override
	public void showMsg(String msg) {
		mTextView.setText(msg);
	}

	@Override
	public void toast(String s) {
		if (s == null)
			return;
		Toast.makeText(mContext, s + "", Toast.LENGTH_SHORT).show();
		
	}

}
