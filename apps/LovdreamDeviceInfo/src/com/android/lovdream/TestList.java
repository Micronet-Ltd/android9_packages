package com.android.lovdream;

import java.io.IOException;

//import com.qualcomm.qcnvitems.QcNvItems;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.PreferenceActivity;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;
import com.lovdream.util.SystemUtil;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class TestList extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

	//private QcNvItems mNv;
	private boolean mHasAutoReg = "1".equals(SystemProperties.get("ro.product.yl.reg.switch"));
	public static boolean NoGps = SystemProperties.get("ro.product.nogps","0").equals("1");
	/*add by hudayu for bug 0017847*/
	private static String KEY_SAR_SWITCH = "sar_switch";
	private SwitchPreference mSarSwitchPreference;
	private static boolean IS_c802 = "msm8953_64_c802".equals(android.os.SystemProperties.get("ro.build.product"));
	private static String SUPPORT_SAR = "persist.sys.support_sar";

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
//        mNv = new QcNvItems();
        addPreferencesFromResource(R.xml.test_list);
        
        if (!mHasAutoReg) {
        	if (null != findPreference("yl_auto_reg_switch")) {
        		getPreferenceScreen().removePreference(findPreference("yl_auto_reg_switch"));
        	}
        }
        
        if (NoGps) {
        	if (null != findPreference("gps")) {
        		getPreferenceScreen().removePreference(findPreference("gps"));
        	}
        }
        
        setStringSummary("inter_version", SystemProperties.get("ro.build.inter_version","unknown"));
      //add by xxf;for bug 0017947;
        try {
			 String modem_value_gbk = getSnVersion().substring(32, 95);
			 String modem_value_utf8 ="";
			try {
				 modem_value_utf8 = new String(modem_value_gbk.getBytes("gbk"), "utf-8");
				 //if on here still luanMa,we do this;
				 modem_value_utf8=getModemValue(modem_value_utf8);
			} catch (Exception e) {
				modem_value_utf8=getSnVersion();
			}
        	setStringSummary("modem_version", modem_value_utf8);
		} catch (Exception e) {
			// TODO: handle exception
		}
      //add by xxf;for bug 0017947;
		/*add by hudayu for bug 0017847*/
		mSarSwitchPreference = (SwitchPreference)findPreference(KEY_SAR_SWITCH);
		if(null != mSarSwitchPreference){
			if(!IS_c802){
				getPreferenceScreen().removePreference(mSarSwitchPreference);
			} else {
				boolean sarSwitch = SystemProperties.get(SUPPORT_SAR).equals("true");
				mSarSwitchPreference.setChecked(sarSwitch);
				mSarSwitchPreference.setOnPreferenceChangeListener(this);
			}
		}

	PreferenceScreen T_Card_Pf=(PreferenceScreen)this.findPreference("tcard");
	T_Card_Pf.setOnPreferenceClickListener(new OnPreferenceClickListener(){


    public boolean onPreferenceClick(Preference preference) {
      new TCardUpgrade();
        return false;
    }});

	Preference preferredNetworkMode = findPreference("preferred_network_mode_key");
	preferredNetworkMode.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		
		@Override
		public boolean onPreferenceClick(Preference arg0) {
			Intent intent = new Intent();
			//intent.setClassName("com.android.phone", "com.android.phone.MobileNetworkSettings");
            intent.setClassName("com.qualcomm.qti.networksetting", "com.qualcomm.qti.networksetting.MobileNetworkSettings");
			intent.putExtra("from", "com.lovdream.TestList");
			startActivity(intent);
			return true;
		}
	});

	/*
	Preference edl_switch = findPreference("edl_switch");
	edl_switch.setOnPreferenceClickListener(new OnPreferenceClickListener(){
		@Override
		public boolean onPreferenceClick(Preference pref){
			AlertDialog.Builder builder = new AlertDialog.Builder(TestList.this);
			builder.setMessage(R.string.edl_switch_confirm)
		       .setCancelable(false)
		       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
					   switchToEdl();
					   dialog.cancel();
		           }
		       })
		       .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
			   AlertDialog alert = builder.create();
			   alert.show();
			return true;
		}
	});
	*/

    }
    
  //add by xxf;for bug 0017947;
    private String getModemValue(String oldValue){
    	char [] oldArr = oldValue.toCharArray();
    	int spiltFlag = 0;
    	for (int i = oldArr.length-1; i >0; i--) {
    		String s = String.valueOf(oldArr[i]);
    		try {
				Integer.valueOf(s);
				spiltFlag=i;
				break;
			} catch (Exception e) {
				//continue;
			}			
		}
    	String newValue = oldValue.substring(0,spiltFlag+1);
    	return newValue;
    }
  //add by xxf;for bug 0017947;
    /*add by hudayu for bug 0017847*/
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        if (KEY_SAR_SWITCH.equals(key)){
            boolean value = (Boolean)(objValue);
            mSarSwitchPreference.setChecked(value);
            SystemProperties.set(SUPPORT_SAR, value ? "true" : "false");
            warningDialog(value);
            return true;
        }
        return false;
    }

    private void setStringSummary(String preference, String value) {
        try {
              if(value==null||value.equals(""))
               throw new RuntimeException();
            findPreference(preference).setSummary(value);
        } catch (RuntimeException e) {
            findPreference(preference).setSummary(
                getResources().getString(R.string.device_info_default));
        }
    }  
     
    private void switchToEdl() {
    	Intent intent = new Intent(Intent.ACTION_REBOOT);
    	intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
        intent.putExtra(Intent.EXTRA_REASON, "edl");
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(intent);
    }

    private String getSnVersion() {
		String strSN = "";
		strSN = SystemUtil.getSN();
//		try {
//			strSN = mNv.getSNNumber();
//		} catch (IOException e) {
//
//		}
		return strSN;
	}

    // add by hudayu for bug 0017847
    private void warningDialog(boolean sarValue){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.prompt_title);
        if(sarValue){
            builder.setMessage(R.string.prompt_open_message);
        }else{
            builder.setMessage(R.string.prompt_close_message);
        }

        builder.setNegativeButton(R.string.cancel, null);
        builder.create().show();
    }

}
