package com.android.lovdream;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Runtime;
import java.io.BufferedReader;
import java.io.InputStreamReader;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


public class WlanTest extends Activity implements RadioGroup.OnCheckedChangeListener{
	private RadioButton switch_high;
	private RadioButton switch_middle;
	private RadioButton switch_low;	
	private RadioGroup wlan_switch;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wlan_switch);
		switch_high=(RadioButton)findViewById(R.id.switch_high);
		switch_middle=(RadioButton)findViewById(R.id.switch_middle);
		switch_low=(RadioButton)findViewById(R.id.switch_low);
		wlan_switch=(RadioGroup)findViewById(R.id.wlan_switch);
		do_exec("insmod /system/wifi/ar6000.ko testmode=1");
		configWlan(2);
		switch_high.setChecked(true) ;
		wlan_switch.setOnCheckedChangeListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId){
		case R.id.switch_low:
			configWlan(0);
			break;
		case R.id.switch_middle:
			configWlan(1);
			break;
		case R.id.switch_high:
			configWlan(2);			
			break;
		}
		
	}
	public void configWlan(final int flag){
		System.out.println("configWlan flag = " + flag); 
		if(flag == 2)
			do_exec("athtestcmd -i wlan0 --tx tx99 --txrate 11 --txpwr 14 --txfreq 13");
		else if	(flag == 1)
			//do_exec("athtestcmd");
			do_exec("athtestcmd -i wlan0 --tx tx99 --txrate 11 --txpwr 14 --txfreq 6");
		else
			//do_exec("ls /system/");	
			do_exec("athtestcmd -i wlan0 --tx tx99 --txrate 11 --txpwr 14 --txfreq 1");
	}

		public void do_exec(String cmd) {  
        String s = "\n";  
        try {  
            Process p = Runtime.getRuntime().exec(cmd);  
            BufferedReader in = new BufferedReader(  
                                new InputStreamReader(p.getInputStream()));  
            String line = null;  
            while ((line = in.readLine()) != null) {  
                s += line + "\n";                 
            }  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
 	     System.out.println(s);  
    }
}
