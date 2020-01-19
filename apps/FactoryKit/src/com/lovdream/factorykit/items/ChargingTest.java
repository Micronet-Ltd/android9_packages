package com.lovdream.factorykit.items;

import android.view.View;
import android.os.Vibrator;
import android.os.BatteryManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.util.Log;

import com.lovdream.factorykit.R;
import com.lovdream.factorykit.Utils;
import com.lovdream.factorykit.TestItemBase;
import com.swfp.utils.ServiceUtil;
import com.lovdream.factorykit.Main;

public class ChargingTest extends TestItemBase {

	private static final String CURRENT = "/sys/class/power_supply/battery/current_now";
	private static final String ISCHARGING = "/sys/class/power_supply/battery/status";
	private static final int DEFAULT_CURRENT = 0;
	private static final int NO_PASS = 0;
	private static final int SINGLE_PASS=1;
	private static final int DOUBLE_PASS=2;

	private TextView mInfoView;
	private Context mContext;
	
	private  int insertSuccessTimes = NO_PASS;
// 	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
					BatteryManager.BATTERY_STATUS_UNKNOWN);
			switch (status) {
		    case BatteryManager.BATTERY_STATUS_UNKNOWN:
		    case BatteryManager.BATTERY_STATUS_DISCHARGING:
		    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
		    	updateInfo(false);
		    	 break;
		    case BatteryManager.BATTERY_STATUS_CHARGING:
		    case BatteryManager.BATTERY_STATUS_FULL:
		    	updateInfo(true);
			    break;
            }
			if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
                    Main.currentTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
                    Main.isBatteryFull = (intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1) == BatteryManager.BATTERY_STATUS_FULL);
                    Main.currentVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
                    getBatteryMessage();
                    updateInfo(true);
			}
		}
	};

	@Override
	public String getKey() {
		return "charging_test";
	}

	@Override
	public String getTestMessage() {
		return getActivity().getString(R.string.charging_test_mesg);
	}

	@Override
	public void onStartTest() {
		insertSuccessTimes =NO_PASS;
		mContext =  getActivity();
		registerBroadCastReceiver();
	}
	@Override
	public void onStopTest() {
		uinRegisterBroadCastReceiver();
	}

	@Override
	public View getTestView(LayoutInflater inflater) {
		View v = inflater.inflate(R.layout.test_mesg_view, null);
		mInfoView = (TextView) v.findViewById(R.id.test_mesg_view);
		return v;
	}
	
	private void registerBroadCastReceiver(){
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
		mFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		mContext.registerReceiver(mReceiver, mFilter);
	}
	private void uinRegisterBroadCastReceiver(){
		try {
			getActivity().unregisterReceiver(mReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateInfo(boolean isConnect) {
		StringBuilder sb = new StringBuilder();
		if(!isConnect){
			sb.append(mContext.getString(R.string.charging_state_none));
		}else{
			int mCurrent = getCurrent();
			boolean isCanPass = mCurrent > 50;
			sb.append(mContext.getString(R.string.charging_state_charging)+"\n");
			sb.append(mContext.getString(R.string.charging_current_label, mCurrent)+"\n");
			if(isCanPass){
                if(Build.MODEL.equals("MSCAM")){
                    insertSuccessTimes = 2;
                    sb.append("");
                }else{
                    sb.append(++insertSuccessTimes<=SINGLE_PASS?mContext.getString(R.string.charging_type_c_turn):"");
                }
            }else{
				sb.append(mContext.getString(R.string.charging_current_low)+"\n");
				sb.append(Main.resultString + "\n");
				}
		}
		if (mInfoView != null) 
			mInfoView.setText(sb.toString());
		
		enableSuccess(insertSuccessTimes>=DOUBLE_PASS);
	}

	private int  getCurrent() {
		int mCurrent = DEFAULT_CURRENT;
		try {
			mCurrent=Integer.valueOf(ServiceUtil.getInstance().readFromFile(CURRENT));
			mCurrent=Math.abs(mCurrent/=1000);
		} catch (Exception e) {
			mCurrent =0;
			e.printStackTrace();
		}
		
		return  mCurrent;
	}

    public void getBatteryMessage(){
        if(Main.isBatteryFull || Integer.valueOf(ServiceUtil.getInstance().readFromFile(CURRENT)) == 152)
            Main.resultString = "The battery is full, discharge the battery before retesting.";
        else if(Main.currentTemp > 449 || Main.currentTemp < 1) 
            Main.resultString = "The battery don't charging because of high/low temperature (now: " + (float) Main.currentTemp/10 + ")";
        else Main.resultString = "";
	}
}
