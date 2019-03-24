
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

public class ButtonBacklight extends TestItemBase implements Runnable{

	private static final String BUTTON_LED_PATH = "/sys/class/leds/button-backlight/brightness";

	private final int BLINK_INTERVAL = 800;
	private Handler mHandler = new Handler();
	private boolean lightEnabled = false;
        private LovdreamDeviceManager ldm;
        private Context mContext;
	@Override
	public String getKey(){
		return "button_light";
	}

	@Override
	public String getTestMessage(){
		return getActivity().getString(R.string.button_light_mesg);
	}

	@Override
	public void onStartTest(){
                mContext = getActivity();
                ldm = (LovdreamDeviceManager)mContext.getSystemService(Context.LOVDREAMDEVICES_SERVICE);
		mHandler.postDelayed(this,BLINK_INTERVAL);
	}

	@Override
	public void onStopTest(){
		mHandler.removeCallbacks(this);
		setBrightness(false);
	}

	@Override
	public void run(){
		lightEnabled = !lightEnabled;
		setBrightness(lightEnabled);
		mHandler.postDelayed(this,BLINK_INTERVAL);
	}
	
	private void setBrightness(boolean enable){
		try {
                        String value = enable ? "20": "0";
                        ldm.writeToFile(BUTTON_LED_PATH, value);
			/*FileOutputStream fos = new FileOutputStream(BUTTON_LED_PATH);
			String value = enable ? "20": "0";
			fos.write(value.getBytes());
			fos.flush();
			fos.close();*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
