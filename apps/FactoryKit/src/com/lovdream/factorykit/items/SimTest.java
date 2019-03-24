
package com.lovdream.factorykit.items;

import android.view.View;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.content.Context;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.os.SystemProperties;
import android.util.Log;
import android.telephony.TelephonyManager;

import java.util.ArrayList;

import com.lovdream.factorykit.R;
import com.lovdream.factorykit.TestItemBase;

public class SimTest extends TestItemBase{

	private TextView mTv;

	String mSimInfo = "";
	boolean result;

	@Override
	public String getKey(){
		return "sim_test";
	}

	@Override
	public String getTestMessage(){
		return getActivity().getString(R.string.sim_test_mesg);
	}

	@Override
	public void onStartTest(){
		TelephonyManager tm = TelephonyManager.getDefault();
		int count = tm.isMultiSimEnabled() ? 2 : 1;

		result = true;
		for(int i = 0;i < count;i++){

			int type = tm.getNetworkType(i);
			int state = tm.getSimState(i);
			mSimInfo += getActivity().getString(R.string.sim_status_label,cardTypeToString(type),cardStateToString(state));

			result &= (state != TelephonyManager.SIM_STATE_ABSENT) && (state != TelephonyManager.SIM_STATE_UNKNOWN);
		}

		if(result){
			postSuccess();
		}else{
			postFail();
		}
	}

	@Override
	public void onStopTest(){
		mSimInfo = "";
	}
	
	@Override
	public View getTestView(LayoutInflater inflater){
		View v = inflater.inflate(R.layout.test_mesg_view,null);
		TextView tv = (TextView)v.findViewById(R.id.test_mesg_view);
		tv.setText(mSimInfo);
		enableSuccess(result);
		return v;
	}

	private String cardTypeToString(int type){
		switch(type){
			case TelephonyManager.NETWORK_TYPE_UMTS:
				return "USIM";
			case TelephonyManager.NETWORK_TYPE_CDMA:
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
			case TelephonyManager.NETWORK_TYPE_EVDO_B:
			case TelephonyManager.NETWORK_TYPE_1xRTT:
			case TelephonyManager.NETWORK_TYPE_LTE:
				return "UIM";
		}
		return "SIM";
	}

	private String cardStateToString(int state){
		switch(state){
			case TelephonyManager.SIM_STATE_ABSENT:
				return getActivity().getString(R.string.sim_status_no_card);
			case TelephonyManager.SIM_STATE_PIN_REQUIRED:
				return getActivity().getString(R.string.sim_status_pin_req);
			case TelephonyManager.SIM_STATE_PUK_REQUIRED:
				return getActivity().getString(R.string.sim_status_puk_req);
			case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
				return getActivity().getString(R.string.sim_status_locked);
			case TelephonyManager.SIM_STATE_READY:
				return getActivity().getString(R.string.sim_status_ready);
		}
		return getActivity().getString(R.string.sim_status_unknown);
	}
}
