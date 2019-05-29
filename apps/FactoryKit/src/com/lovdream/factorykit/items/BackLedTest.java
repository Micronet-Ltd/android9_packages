package com.lovdream.factorykit.items;

import android.os.Handler;
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

import java.io.FileOutputStream;

import com.lovdream.factorykit.R;
import com.lovdream.factorykit.TestItemBase;
import com.swfp.utils.ProjectControlUtil;
import com.swfp.utils.ServiceUtil;

public class BackLedTest extends TestItemBase{


   private final String ledPath = "/sys/class/ext_dev/function/gpio_en";
    private Context mContext;

	@Override
	public String getKey(){
		return "back_led";
	}

	@Override
	public String getTestMessage(){
		return getActivity().getString(R.string.back_led_mesg);
	}

	@Override
	public void onStartTest(){
	                mContext = getActivity();
	                ServiceUtil.getInstance().writeToFile(ledPath, 1+"", mContext);
	}

	@Override
	public void onStopTest(){
		ServiceUtil.getInstance().writeToFile(ledPath, 0+"", mContext);
	}
}
