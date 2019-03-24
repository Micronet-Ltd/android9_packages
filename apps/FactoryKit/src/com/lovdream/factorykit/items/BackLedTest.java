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
import com.lovdream.LovdreamDeviceManager;
import com.swfp.utils.ProjectControlUtil;

public class BackLedTest extends TestItemBase{


   private final String ledPath = "/sys/class/ext_dev/function/gpio_en";
    private LovdreamDeviceManager ldm;
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
                ldm = (LovdreamDeviceManager)mContext.getSystemService(Context.LOVDREAMDEVICES_SERVICE);
                ldm.writeToFile(ledPath, 1+"");
	}

	@Override
	public void onStopTest(){
	
		ldm.writeToFile(ledPath, 0+"");
	}
}
