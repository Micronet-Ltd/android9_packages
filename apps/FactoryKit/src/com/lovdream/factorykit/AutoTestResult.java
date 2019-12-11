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
import android.os.SystemProperties;
import android.os.PowerManager;
import android.view.View;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Toast;
import android.widget.ScrollView;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.lovdream.factorykit.Config.TestItem;
import com.swfp.utils.ServiceUtil;
import android.os.BatteryManager;


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
    private static final int IGNITION_ON = 2;
    private int dockState = -1;
    private Context mContext;
    int currentTemp = 0;
    int batteryStatus = -1;
    int currentVoltage = 0;
    boolean isBatteryFull = false;
    TextView tv;
    String view;
    String results;
    boolean valuesReceived = false;

	private String buildTestResult(){
	
		app = (FactoryKitApplication)getActivity().getApplication();
		config = app.getTestConfig();
		mItems = config.getTestItems();
		
		//saveResultsToCsv();
		
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
		registerBroadCastReceiver();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ScrollView sv = new ScrollView(getActivity());
		tv = new TextView(getActivity());
		tv.setTextSize(24);
		results = buildTestResult();
		view = results + getBatteryMessage() + "\n\nPlease run batch script, and after that turn off the Ignition. \n Device will shutdown.";
		tv.setText(view);
		tv.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		mContext =  getActivity();
		sv.addView(tv);
		return sv;
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
            goAsync();
            if(intent.getAction().equals(Intent.ACTION_DOCK_EVENT)){
                dockState = intent.getIntExtra(Intent.EXTRA_DOCK_STATE, -1);
                if(dockState != Intent.EXTRA_DOCK_STATE_CAR){
                    PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
                    pm.shutdown(false,null,false);
                }
			} else if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
                if(!valuesReceived){
                    valuesReceived = true;
                    currentTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
                    batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                    isBatteryFull = (batteryStatus == BatteryManager.BATTERY_STATUS_FULL);
                    currentVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
                    Log.d("AutoTestResult", "intent battery_changed. currentTemp = " + currentTemp + "; batteryStatus = " + batteryStatus + "; currentVoltage = " + currentVoltage);
                    view = results + getBatteryMessage() + "\nPlease run batch script, and after that turn off the Ignition. \n Device will shutdown.";
                    tv.setText(view);
                    saveResultsToCsv();
                }

			}
		}
	};
	
	private void registerBroadCastReceiver(){
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		mFilter.addAction(Intent.ACTION_DOCK_EVENT);
		mContext.registerReceiver(mReceiver, mFilter);
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
            //bufferedWriter.write(("1,"));
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
        results.append(getProductType() + ",");
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
        results.append(getMcuVersion() + ",");
        results.append(wInfo.getMacAddress() + ",");
        results.append(getCurrent() + ",");
        results.append(currentVoltage + ",");

        for(TestItem item : mItems){
			if(item.inAutoTest){                
                if(config.getTestFlag(item.fm.testFlag) == Config.TEST_FLAG_FAIL){
                    results.append(FAIL);
                } else if(config.getTestFlag(item.fm.testFlag) == Config.TEST_FLAG_PASS) {
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
    
    private String getMcuVersion(){
        return SystemProperties.get("hw.build.version.mcu", "unknown");
    }
    
    private String getProductType(){
        String productType = "";
        int type = SystemProperties.getInt("hw.board.id", -1);
        switch (type){
            case 0:
            productType = "Tab8Full";
            break;
            
            case 1:
            productType = "Tab8LC";
            break;
            
            case 2:
            productType = "SCBasic";
            break;
            
            case 3:
            productType = "SCFull";
            break;
        }
        return productType;
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
	
	private String getBatteryMessage(){
        if(isBatteryFull)
            return "\n\nThe battery is full, discharge the battery before retesting.";
        else if(currentTemp > 449 || currentTemp < 1) 
            return "\n The battery don't charging because of high/low temperature (now: " + (float) currentTemp/10 + ")";
        else return "";
	}
    
}
