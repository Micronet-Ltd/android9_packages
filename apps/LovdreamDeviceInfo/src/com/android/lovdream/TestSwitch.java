package com.android.lovdream;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.CharBuffer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.provider.Settings;
//import com.android.internal.app.ShutdownThread;


public class TestSwitch extends Activity implements RadioGroup.OnCheckedChangeListener{
	private RadioButton switch_on;
	private RadioButton switch_off;
	private RadioGroup test_switch;
	private boolean mRebooting = false;
	private String USER_SWITCH = "userswitch";
	private String TEST_SWITCH = "testswitch";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_switch);
		switch_on=(RadioButton)findViewById(R.id.switch_on);
		switch_off=(RadioButton)findViewById(R.id.switch_off);
		test_switch=(RadioGroup)findViewById(R.id.test_switch);
		updateView();
		test_switch.setOnCheckedChangeListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mRebooting) {
			return;
		}
		updateView();
	}

	public void updateView(){
		if(!isUserMode())
		 switch_on.setChecked(true) ;
		else switch_off.setChecked(true);
	}
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId){
		case R.id.switch_on:
			configDialog(TEST_SWITCH);
			break;
		case R.id.switch_off:
			configDialog(USER_SWITCH);
			break;
		}
		
	}
	public void configDialog(final String reason){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.reboot_message)
		       .setCancelable(false)
		       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
					   		   enableAdb();
                               reboot(reason);
                               dialog.cancel();
		           }
		       })
		       .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		                finish();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void enableAdb(){
		//Settings.Global.putInt(getContentResolver(),Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 1);
		Settings.Global.putInt(getContentResolver(),Settings.Global.ADB_ENABLED, 1);
	}

    private void reboot(String reason) {
    	Intent intent = new Intent(Intent.ACTION_REBOOT);
    	intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
        intent.putExtra(Intent.EXTRA_REASON, reason);
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(intent);
        mRebooting = true;

//        ShutdownThread.reboot(TestSwitch.this, "usbmode", false);
    }

    public boolean isUserMode(){
		return "user".equals(SystemProperties.get("ro.boot.usermode"));
	}
  

}
