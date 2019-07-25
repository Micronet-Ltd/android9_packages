package com.lovdream.factorykit.items;

import android.os.Handler;
import android.util.Log;
import android.graphics.Color;

import com.lovdream.factorykit.TestItemBase;
import com.lovdream.factorykit.R;
import com.swfp.utils.ServiceUtil;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;

public class LedTest extends TestItemBase {

	private static final String CONFIG_GREEN_PATH = "/sys/class/leds/green/brightness";
	private static final String CONFIG_RED_PATH = "/sys/class/leds/red/brightness";
	private static final String CONFIG_BLUE_PATH = "/sys/class/leds/blue/brightness";

	private Handler mHandler = new Handler();
	private boolean mIsInTest;
	private Context mContext;

	private int[] colors = { 0xffff0000, 0xff00ff00, 0xff0000ff };

	@Override
	public String getKey() {
		return "led_test";
	}

	@Override
	public String getTestMessage() {
		String[] msg = getParameter("msg");
		if ((msg != null) && (msg[0] != null)) {
			return msg[0];
		}
		return getString(R.string.two_color_led_test_mesg);
	}

	@Override
	public void onStartTest() {
		mContext = getActivity();
		String[] args = getParameter("colors");
		if (args != null) {
			try {
				int[] newColors = new int[args.length];
				for (int i = 0; i < newColors.length; i++) {
					newColors[i] = Color.parseColor(args[i]);
				}
				colors = newColors;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mIsInTest = true;
		new Thread(mRunnable, "t1").start();
	}

	@Override
	public void onStopTest() {
		mIsInTest = false;
	}

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			while (mIsInTest) {
				for (int color : colors) {
					setColor(color);
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
				}
			}
			setColor(-1);
		}
	};

	private void setColor(int color) {
		try {
			ServiceUtil.getInstance().setThreeLightColor(color, mContext);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
