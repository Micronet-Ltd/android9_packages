package com.android.lovdream;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.util.Config;
import android.util.Log;
import android.view.KeyEvent;


public class ShowDeviceInfoBroadcastReceiver extends BroadcastReceiver {
  
    public ShowDeviceInfoBroadcastReceiver() {
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.android.lovdream.deviceinfo")) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setClass(context, DeviceInfoShow.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        else if(intent.getAction().equals("com.android.lovdream.test_switch")){
        	 Intent i = new Intent();
             i.setClass(context, TestList.class);
             i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             context.startActivity(i);
        }
        else if(intent.getAction().equals("com.android.lovdream.wlaninfo")) {
             Intent i = new Intent();
             i.setClass(context, WlanInfo.class);
             i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             context.startActivity(i);
        }
        else if(intent.getAction().equals("com.android.lovdream.wlantest")) {
			 Log.e("ShowDeviceInfoBroadcastReceiver", "handlewlantest *#9556#");
             Intent i = new Intent();
             i.setClass(context, WlanTest.class);
             i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             context.startActivity(i);
        }
    }
}
