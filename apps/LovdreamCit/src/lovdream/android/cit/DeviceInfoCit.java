/**
 * 
 */
package lovdream.android.cit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class DeviceInfoCit extends PreferenceActivity {

	public static final String TP_INFO = "/sys/devices/virtual/assist/ctp/ic";
	public static final String TP_VER = "/sys/devices/virtual/assist/ctp/fw_ver";
	
	public static final String MCP_NAME = "/sys/class/mmc_host/mmc0/mmc0:0001/name";
	public static final String MCP_MANFID = "/sys/class/mmc_host/mmc0/mmc0:0001/manfid";
	public static final String MCP_OEMID = "/sys/class/mmc_host/mmc0/mmc0:0001/oemid";
	
	public static final String LCD_INFO = "/proc/cmdline";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.device_info_cit);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		setStringSummary("MCP", "MCP_NAME:" + readFile(MCP_NAME) + "\n" +
								"MCP_MANFID:" + readFile(MCP_MANFID) + "\n" +
								"MCP_OEMID:" + readFile(MCP_OEMID));
		setStringSummary("CAMERA", "CAMERA_IC:" + "gc2235" + "\n" +
								   "CAMERA_MODULE:" + "bolixin");
		String tp_info = "";
		String tp_ic = "";
		String tp_module = "";
		tp_info = readFile(TP_INFO);
		try {
			tp_ic = tp_info.substring(tp_info.indexOf(".")+1);
			tp_module = tp_info.substring(0, tp_info.indexOf("."));
		} catch (Exception e) {
			// TODO: handle exception
		}
		setStringSummary("TP", "TP_IC:" + tp_ic + "\n" +
							   "TP_MODULE:" + tp_module + "\n" +
							   "TP_VERSION:" + readFile(TP_VER));
		String lcd_info = "";
		try {
			lcd_info = readFile(LCD_INFO);
			lcd_info = lcd_info.substring(lcd_info.indexOf("mdss_dsi_")+9);
		} catch (Exception e) {
			// TODO: handle exception
		}
		setStringSummary("LCD", "LCD_INFO:" + lcd_info);
	}
	
	private String readFile(String filePath){   
		String res="";   
		try{   
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
			String str=null;
			while((str=br.readLine())!=null){
				res+=str;
			}  
        }   
		catch(Exception e){   
         e.printStackTrace();   
        }   
        return res;   
	}  
	
	private void setStringSummary(String preference, String value) {
		try {
			findPreference(preference).setSummary(value);
		} catch (RuntimeException e) {
			findPreference(preference).setSummary(
					getResources().getString(R.string.cit_info_default));
		}
	}
	
}
