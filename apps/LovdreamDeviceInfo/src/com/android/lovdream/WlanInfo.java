package com.android.lovdream;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import android.os.Message;

public class WlanInfo extends Activity {

    static final int DIALOG_WAIT_ID = 0;
    static final int DIALOG_MAC_INFO_ID = 1;
    private int iCount=0;
    private static final int MAX_WAIT_TIME = 40000;
    private static final int NOTIFY = 0x0001; 
    private static final long WAIT_TIME=2000;
    private static String TAG ="WlanInfo";
    private static boolean bEnableWifi =false;
    private TextView macAddr;
    private  WifiManager wifiManager;
    private ProgressDialog prgDialog;
    private AlertDialog infoDialog;
    private Button mbtnConfirm;
    private Timer tm;
    private WaitTask waitTask;
    String macAddress;


    private  BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (wifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                if (wifiManager.WIFI_STATE_ENABLED
                    ==intent.getIntExtra(wifiManager.EXTRA_WIFI_STATE, wifiManager.WIFI_STATE_UNKNOWN)) {
                    tm = new Timer();
                    waitTask = new WaitTask();
                    tm.schedule(waitTask,0,WAIT_TIME);
                }
            }
        }
    };

    private Handler mHandler = new Handler(){  
        public void handleMessage(Message msg) {  
            switch (msg.what) {
            case NOTIFY:     
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                macAddress = wifiInfo == null ? null : wifiInfo.getMacAddress();
                if (null!=macAddress
                    ||WAIT_TIME*iCount>MAX_WAIT_TIME) {
                    Log.e(TAG,"wifiInfo:"+wifiInfo.toString());
                    tm.cancel();
                    waitTask.cancel();
                    iCount = 0; 
                    if (null!=prgDialog)prgDialog.cancel();
                    showDialog(DIALOG_MAC_INFO_ID) ; 
                } else {
                    iCount++;
                }
                break;      
            }      
            super.handleMessage(msg);  
        }  
    }; 

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.wlaninfo);

        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        IntentFilter it = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
        this.registerReceiver(mReceiver,it);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        macAddress =  wifiInfo == null ? null : wifiInfo.getMacAddress();

        if (null==macAddress
            &&!wifiManager.isWifiEnabled()) {
            bEnableWifi = true;
            wifiManager.setWifiEnabled(true);
            showDialog(DIALOG_WAIT_ID) ;
        } else {
            macAddress =  wifiInfo == null ? null : wifiInfo.getMacAddress();
            showDialog(DIALOG_MAC_INFO_ID) ;
        }

    }    

    @Override
    protected void onStop() {
        super.onStop();
        if (bEnableWifi) {
            wifiManager.setWifiEnabled(false);
            bEnableWifi = false;
        }
        this.unregisterReceiver(mReceiver);
    }

    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        switch (id) {
        case DIALOG_WAIT_ID:
            dialog = ProgressDialog.show(WlanInfo.this, "",getResources().getString(R.string.wlan_wait_msg), true);
            prgDialog =(ProgressDialog)dialog;
            break;
        case DIALOG_MAC_INFO_ID:

            if (macAddress == null  ) {
                macAddress = getResources().getString(R.string.wlan_acquire_mac_fail);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.wlan_dialog_title)
            .setMessage(macAddress)
            .setCancelable(false)
            .setPositiveButton(R.string.wlan_dialog_ok, new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int id) {
                                       WlanInfo.this.finish();
                                   }
                               });
            dialog  = builder.create();
            infoDialog=(AlertDialog)dialog;
            break;
        default:
            dialog = null;
        }
        return dialog;
    }

    class WaitTask extends TimerTask {
        @Override
        public void run(){
            Message message = new Message();      
            message.what = NOTIFY; 
            mHandler.sendMessage(message);    
        }
    }
}

