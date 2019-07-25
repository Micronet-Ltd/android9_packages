package com.android.lovdream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.android.lovdream.R;

public class HomeActivity extends Activity {

	private GridView garidView;
	private String[] itemName = {"Red", "Green", "Blue","Receiver", "Vibration",
	                                              "Dimming", "Mega cam","Sensor", "Touch", "Sleep", "Speaker",
	                                              "Sub key", "Front cam","LowFrequency", "Black", "HALL IC", "MST Test",
	                                              "Emulator Test"};

	private SimpleAdapter sim_adapter;
	private List<Map<String, Object>> listItems;
//	private long lastTime = 0;
//	private boolean isExit = false;
	private int recovery = 0;
//	private static final int ACTIVITY_EXIT = 101;
//	private Handler mHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			isExit = false;
//		}
//	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
		garidView = (GridView) findViewById(R.id.grid_all_item);

		getdata();
		String[] from = {"name"};
		int [] to = {R.id.home_btn_item};
		sim_adapter = new SimpleAdapter(getApplicationContext(), listItems,
				R.layout.home_adapter_item, from, to);
		garidView.setAdapter(sim_adapter);

		setscreenBrightness();
	}

	private void setscreenBrightness() {
		recovery = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
		int brightness = 255;
		lp.screenBrightness = brightness/255.0f;
		getWindow().setAttributes(lp);
	}

	private void getdata() {
		listItems = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < itemName.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("name", 	itemName[i] );
			listItems.add(listItem);
		}
	}

	@Override
	public void onBackPressed() {
//		if (java.lang.System.currentTimeMillis() - lastTime > 800){
//			lastTime = java.lang.System.currentTimeMillis();
//		} else {
//			finish();
//		}
//        if (!isExit) {
//        	isExit = true;
//        	if(mHandler.hasMessages(ACTIVITY_EXIT)) mHandler.removeMessages(ACTIVITY_EXIT);
//        	mHandler.sendEmptyMessageDelayed(ACTIVITY_EXIT, 800);
//        } else {
        	finish();
//        }
	}

	@Override
	protected void onDestroy() {
		Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,recovery);
		super.onDestroy();
	}
}
