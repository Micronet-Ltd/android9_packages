package com.lovdream.factorykit;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.app.Fragment;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.View;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Toast;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.lovdream.factorykit.Config.TestItem;
import com.swfp.utils.ServiceUtil;


public class AutoTestResult extends Fragment{

	private static final String TAG = Main.TAG;
	private static final String CURRENT = "/sys/class/power_supply/bms/current_now";
    StringBuilder data;
    public static final String PASS = "Pass,";
    public static final String FAIL = "Fail,";
    public static final String NULL = "NA,";
    FactoryKitApplication app;
    Config config;
    ArrayList<TestItem> mItems;

	private String buildTestResult(){
	
		app = (FactoryKitApplication)getActivity().getApplication();
		config = app.getTestConfig();
		mItems = config.getTestItems();
		
		saveResultsToCsv();
		
		String failItems = "";
		for(TestItem item : mItems){
			//xxf
			Log.d("xxfjjj", "=============================");
			Log.d("xxfjjj", "item---->"+item.key);
			Log.d("xxfjjj", "item.fm.testFlag---->"+(item.fm.testFlag));
			Log.d("xxfjjj", "=============================");
			if(item.inAutoTest && (config.getTestFlag(item.fm.testFlag) == Config.TEST_FLAG_FAIL)){
				failItems += item.displayName + "\n";
			}
		}

		if("".equals(failItems)){
			config.setAutoTestFt(true);
			return "整机自动测试\n所有测试通过!";
		}else{
			config.setAutoTestFt(false);
			return "整机自动测试\n部分测试项未通过：\n\n" + failItems;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		TextView tv = new TextView(getActivity());
		tv.setTextSize(24);
		tv.setText(buildTestResult());
		tv.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		return tv;
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		activity.setTitle(R.string.test_result);
	}

	@Override
	public void onDetach(){
		super.onDetach();
		getActivity().setTitle(R.string.app_name);
	}
	
	private void saveResultsToCsv() {
        data = getPhoneData(new StringBuilder());
        String filename = "/sdcard/test_results.csv";
        BufferedWriter bufferedWriter = null;
        try {
            File file = new File(filename);
            file.delete();
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(("1,"));
            bufferedWriter.write(data.substring(0, data.length()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private StringBuilder getPhoneData(StringBuilder results) {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        results.append(Build.getSerial() + ",");
        results.append(telephonyManager.getImei() + ",");
        String deviceId=telephonyManager.getImei();
        if (deviceId == null || !deviceId.startsWith("35343610") || deviceId.length()!=15 || !deviceId.matches("\\d+")) {
            Log.e(TAG,"Bad IMEI: "+deviceId);
            results.append(FAIL);
        } else {
            Log.i(TAG,"IMEI: "+deviceId);
            results.append(PASS);
        }
        results.append(getBuildVersion(Build.DISPLAY) + ",");
        results.append(wInfo.getMacAddress() + ",");
        results.append(getCurrent() + ",");

        for(TestItem item : mItems){
			if(item.inAutoTest){                
                if(config.getTestFlag(item.flagIndex) == Config.TEST_FLAG_FAIL){
                    results.append(FAIL);
                } else if(config.getTestFlag(item.flagIndex) == Config.TEST_FLAG_PASS) {
                    results.append(PASS);
                } else {
                    results.append(NULL);
                }
			}
		}
        
        return results;

    }
    
    private String getBuildVersion(String fullVersion){
        String temp = fullVersion.substring(0, Build.DISPLAY.lastIndexOf("_"));
        return temp.substring(temp.indexOf(Build.MODEL));
    }
    
    private int  getCurrent() {
		int mCurrent = 0;
		try {
			mCurrent=Integer.valueOf(ServiceUtil.getInstance().readFromFile(CURRENT));
			mCurrent=Math.abs(mCurrent);
		} catch (Exception e) {
			mCurrent =0;
			e.printStackTrace();
		}
	
		return  mCurrent;
	}
    
}
