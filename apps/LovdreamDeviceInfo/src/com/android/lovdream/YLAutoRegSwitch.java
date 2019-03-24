package com.android.lovdream;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


public class YLAutoRegSwitch extends Activity implements RadioGroup.OnCheckedChangeListener{
	private RadioButton switch_on;
	private RadioButton switch_off;
	private RadioGroup test_switch;
	  /** 注册 */
	private static final String REG_STATUS = "yulong.sms.background.register";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_switch_yl);
		switch_on=(RadioButton)findViewById(R.id.switch_on);
		switch_off=(RadioButton)findViewById(R.id.switch_off);
		test_switch=(RadioGroup)findViewById(R.id.test_switch);
		updateView();
		test_switch.setOnCheckedChangeListener(this);
	}
	
	@Override
	protected void onResume() {
		updateView();
		super.onResume();
	}

	public void updateView(){
		if("false".equals(getBackdoorStatus()))
			switch_off.setChecked(true);
		else switch_on.setChecked(true) ;
	}
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId){
		case R.id.switch_on:
			Log.d("LLS", "xxxxtrue");
			saveConfig("true");
			break;
		case R.id.switch_off:
			Log.d("LLS", "xxxxfalse");
			saveConfig("false");
			break;
		}
	}
	
	public void saveConfig(final String flag){
		if (!setBackdoorStatus(flag)) {
			Toast.makeText(YLAutoRegSwitch.this, R.string.failed,
					Toast.LENGTH_LONG);
		}
		finish();
	}

  /**
   * 判断后门 开关是否打开，null或者true会注册，false不注册。
   *
   * @return result
   * */
  private String getBackdoorStatus() {
      String result = null;
      result = Settings.System.getString(getContentResolver(), REG_STATUS);
      return result;
  }
  
  private boolean setBackdoorStatus(String flag) {
	  return Settings.System.putString(getContentResolver(), REG_STATUS, flag);
  }
}
