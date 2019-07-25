
package com.lovdream.fastmmiswitch;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.PowerManager;
import android.preference.PreferenceActivity;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Main extends PreferenceActivity implements OnPreferenceClickListener{

	@Override
	protected void onCreate(Bundle bundle){
		super.onCreate(bundle);
        addPreferencesFromResource(R.xml.main_list);


		PreferenceScreen mmiSwitch = (PreferenceScreen)findPreference("fast_mmi_switch");
		mmiSwitch.setOnPreferenceClickListener(this);
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference){
		if("fast_mmi_switch".equals(preference.getKey())){
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.mmi_switch_message).setCancelable(false)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						switchToMmiMode();
						dialog.cancel();
					}
				})
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						dialog.cancel();
					}
				});
				builder.create().show();
				return true;
		}
		return false;
	}

	private void switchToMmiMode(){
		SystemProperties.set("sys.boot_mode","ffbm");

		PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
		pm.reboot(null);
	}
}
