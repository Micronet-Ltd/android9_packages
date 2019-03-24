/**
 * 
 */
package lovdream.android.cit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

//import com.qualcomm.qcnvitems.QcNvItems;
import com.lovdream.util.SystemUtil;
import android.os.SystemProperties;

public class FactoryTestResult extends ListActivity{
	private String[] mTestResultString;
	private final static String tab = "    ";
	private final static int machineCit = 3;
//	private final static int machineCit2 = 4;
	private final static int pcbaCit = 4;
	private final static int SN = 7;
	private final static int BTFT = 8;
	//private QcNvItems mNv;
	public static final String TP_INFO = "/proc/tp_info";
	public static final String TP_VER = "/sys/devices/virtual/assist/ctp/fw_ver";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
//		mNv = new QcNvItems(this);
	}
	
	private static String getSystemproString(String property) {
		return SystemProperties.get(property, "unknown");
	}
	
	private String getSoftwareVersion() {
		String softwareTitle = getSystemproString("ro.build.display.id");
		String softwareTime = getSystemproString("ro.build.version.incremental");

		if (softwareTime.length() > 18) {
			int nPos = softwareTime.indexOf(".");
			nPos = softwareTime.indexOf(".", nPos);
			softwareTime = softwareTime.substring(nPos + 1);
			nPos = softwareTime.indexOf(".");
			nPos = softwareTime.indexOf(".", nPos);

			softwareTime = softwareTime.substring(nPos + 1);

			StringBuffer time = new StringBuffer(softwareTime);
			time.insert(4, '-');
			time.insert(7, '-');
			time.replace(10, 11, " ");
			time.insert(13, ':');
			time.insert(16, ':');

			softwareTitle += "\n" + time.toString();
		}

		return softwareTitle;

	}
	
	public String readFileSdcardFile(String filePath){   
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
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		mTestResultString = new String[16];
		mTestResultString[0] = getString(R.string.sn_model) + ":" + getSystemproString("ro.product.ui_model");
		mTestResultString[1] = getString(R.string.software_title) + ":"+ "\n" + getSoftwareVersion();
		mTestResultString[2] = getString(R.string.baseband_version) + ":" + SystemProperties.get("gsm.version.baseband",getString(R.string.cit_info_default));
		mTestResultString[3] = getString(R.string.tp_info) + ":" + readFileSdcardFile(TP_INFO);
		mTestResultString[4] = getString(R.string.tp_ver) + ":" + readFileSdcardFile(TP_VER);
		mTestResultString[5] = getString(R.string.audio_parameters) + ":" + getAudioParameters();
		mTestResultString[6] = getString(R.string.sn_version) + ":" + getSnVersion();

		TelephonyManager mtm = TelephonyManager.getDefault();
		if (mtm.isMultiSimEnabled()) {
			mTestResultString[7] = getString(R.string.meid_num) + mtm.getDeviceId(0) + "\n"
									+ getString(R.string.imei_num) + mtm.getDeviceId(1);
		} else {
			TelephonyManager tm = TelephonyManager.getDefault();
			mTestResultString[7] = getString(R.string.meid_num) + tm.getDeviceId();
		}
		mTestResultString[8] = getString(R.string.wifi_mac) + getWifiMac();
		mTestResultString[9] = getString(R.string.test_result);
		mTestResultString[10] = getString(R.string.sn_version) + tab + getResult(SN);
		mTestResultString[11] = getString(R.string.bt_ft) + tab + getResult(BTFT);
		mTestResultString[12] = getString(R.string.PCBA_auto_test) + tab + getResult(pcbaCit);
		mTestResultString[13] = getString(R.string.Machine_auto_test) + tab + getResult(machineCit);
		mTestResultString[14] = getString(R.string.fastmmi_test);
		mTestResultString[15] = getString(R.string.Clear);
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mTestResultString));
	}

	private final static int pcbaCitIdx = 12;
	private final static int machineCitIdx = 13;
	private final static int fastMMIIdx = 14;
	private final static int clear = 15;
	
	public static int resultType = -1;
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent();
		switch (position) {
		case pcbaCitIdx:
			resultType = CITMain.PCBA_AUTO_TEST;
			intent.setClass(this, FactoryTestResultDetail.class);
			startActivity(intent);
			break;
		case machineCitIdx:
			resultType = CITMain.MACHINE_AUTO_TEST;
			intent.setClass(this, FactoryTestResultDetail.class);
			startActivity(intent);
			break;
			
		case fastMMIIdx:
			resultType = CITMain.FASTMMI_TEST;
			intent.setClass(this, FactoryTestResultDetail.class);
			startActivity(intent);
			break;
			
		case clear:
			intent.setAction("android.settings.BACKUP_AND_RESET_SETTINGS");
			//### 0015274 fix restore item  of CIT was pop up windows.by zj. 2017.6.15
			intent.setComponent(new  ComponentName("com.android.settings", "com.android.settings.Settings$PrivacySettingsActivity"));
			startActivity(intent);
			break;

		default:
			break;
		}
		
	}
	
	private String getResult(int index) {
		byte[] bresult = null;
		String isPass = "";
		bresult = SystemUtil.getNvFactoryData3IByte();
		if (bresult == null) {
			bresult = new byte[128];
			for (int i = 0; i < bresult.length; i++) {
				bresult[i] = 'U';
			}
			SystemUtil.setNvFactoryData3IByte(bresult);
		}
		if(bresult.length <= index){
			Log.e("CIT","getResult(),getNvFactoryData3IByte length error");
			return isPass;
		}
		if (bresult[index] == 'P') {
			isPass = getString(R.string.result_pass);
		} else if (bresult[index] == 'F') {
			isPass = getString(R.string.result_fail);
		} else if (bresult[index] == 'U') {
			isPass = getString(R.string.result_not_tested);
		}
		return isPass;
	}
	
	public String getStr(int num, String str){
		StringBuffer sb = new StringBuffer("");
		for(int i=0;i<num;i++){
		   sb.append(str);
		}
		return sb.toString();
	}
	
	private String getWifiMac() {
		String macAddress = "";
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        macAddress =  wifiInfo == null ? "" : wifiInfo.getMacAddress();
        return macAddress;
	}
	
//	private String getMeid() {
//		String meidStr = "";
//		try {
//			QcNvItems qcObj = new QcNvItems();
//
//			if (null != qcObj)
//			{
//				meidStr = qcObj.getMEID();
//			}
//		} catch (IOException e) {
//			Log.e("FactoryTestResult", "showMEIDPanel error");
//		}
//		return meidStr;
//	}
//	
//	private String getImei() {
//		String imeiStr = "";
//		try {
//			QcNvItems qcObj = new QcNvItems();
//
//			if (null != qcObj)
//			{
//				imeiStr = qcObj.getIMEI();
//			}
//		} catch (IOException e) {
//			Log.e("FactoryTestResult", "showIMEIPanel error");
//		}
//		return imeiStr;
//	}
	
	private String getSnVersion() {
		String strSN = "";
		strSN = SystemUtil.getSN().substring(0, 16);
		return strSN;
	}
	
	private String getAudioParameters() {
		String strAudio = "";
//		strAudio = SystemUtil.getSN().substring(96, 127);
		return strAudio;
	}
}
