package lovdream.android.cit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
//import com.qualcomm.qcnvitems.QcNvItems;
import com.lovdream.util.SystemUtil;
import android.os.PowerManager.WakeLock;
import android.os.PowerManager;
import android.provider.Settings;
import android.os.SystemProperties;
public class CITAuto extends Activity implements View.OnClickListener {
	private AudioManager mAudioManager;
	private boolean mFirstFlag = false;
	private String[] AcitItemStr = null;
	private String[] AllItemStr = null;
	private int test_idx = 0;
	private int test_cnt = 0;

	private ArrayList<HashMap<String, String>> FailItems = new ArrayList<HashMap<String, String>>();

	private int[] mResult = new int[100];
	private TextView txt_item_name;
	private TextView txt_descript;
	private TextView txt_testType;
	public static Button btn_success;
	private Button btn_fail;
	private Button btn_vibration_test;
	// ///////////////////////////////////////////////////////////////////////////
	boolean m_bBatteryTesting;
	boolean m_bTemperature;
	boolean m_bHeadsetTest;
	int nCurrentMusicVolume;
	boolean testDone = false;
	private PowerManager mPowerManager;
	private WakeLock mWakeLock;

	String batteryStatus = "";
	AudioManager am = null;

	private Vibrator vibrator = null;
	private MediaPlayer mp = null;
	private boolean mIsA420 =android.os. SystemProperties.get("ro.product.name").equals("msm8916_64_a420");
	public boolean IsAutoCITTesting = false;
	private static final int REQUEST_ASK = 1;
	boolean P800 = true;//SystemProperties.get("ro.product.name").equals("msm8939_64_p800");
	private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (m_bBatteryTesting) {
				if (!mHandler.hasMessages(0)) {
					mHandler.sendEmptyMessage(0);
				}
				showChargeTestInfo(intent, s);
			}
			if (m_bTemperature) {	
				showTemperatureInfo(intent, s);
			}
		}
	};
	
	private BroadcastReceiver mheadSetReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String s1 = getString(R.string.test_item_heakloopback);
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
				if (intent.getIntExtra("state", 0) == 1) {
					//i = R.string.test_item_heakloopback;
					s1 = getString(R.string.headloopback_test_message);
					am.setParameters("loopback=headset");
					btn_success.setEnabled(true);
					btn_fail.setEnabled(true);
					txt_descript.setText(s1);
					ShowTestResult(test_idx);
				} else {
					s1 = getString(R.string.headset_plug_warning);
					if(m_bHeadsetTest){
						btn_success.setEnabled(false);
						btn_fail.setEnabled(false);
						txt_descript.setText(s1);
						ShowTestResult(test_idx);
					}else{
						btn_success.setEnabled(true);
						btn_fail.setEnabled(true);
					}
				}
			}
		}
	};
	
	private BroadcastReceiver mCameraReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			btn_success.setEnabled(true);
			btn_success.performClick();
		}
	};
	
	private BroadcastReceiver mMusicReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			btn_success.setEnabled(true);
		}
	};

	private void ShowTestResult(int idx) {
		int str_title = R.string.app_name;
		String strMessage = "";
		String str;

		if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
			str_title = R.string.Machine_auto_test;

		} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
			str_title = R.string.PCBA_auto_test;
		}

		if (idx < test_cnt) {
			strMessage = AcitItemStr[idx] + " " + "(" + (idx + 1) + "/"
					+ test_cnt + ")\n";
			txt_item_name.setText(strMessage);

		} else {
			HashMap<String, String> map;
			Entry entry = null;
			setContentView(R.layout.autotest_result_all);
			TextView testType1 = (TextView) findViewById(R.id.autotest_type_1);
			TextView resultTxt1 = (TextView) findViewById(R.id.failitems_1);
			TextView failItems1 = (TextView) findViewById(R.id.item1_1);
			testType1.setText(str_title);
			if (FailItems.isEmpty()) {
				resultTxt1.setText(R.string.result_all_success);
				if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
					rewriteOneByteOfNv2499New(MachineTestIdx, 'P');
					//resultTxt1.setText(R.string.result_all_success_end);
					//myHandler.sendEmptyMessageDelayed(0, 10000);
				}/* else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
					rewriteOneByteOfNv2499(MachineTest2Idx, 'P');
				}*/ else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
					rewriteOneByteOfNv2499New(PCBATestIdx, 'P');
				}
			} else {
				for (int i = 0; i < FailItems.size(); i++) {
					map = FailItems.get(i);
					Iterator it = map.entrySet().iterator();
					entry = (Entry) it.next();
					strMessage += (String) entry.getValue();
					strMessage += "\n";
				}
				if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
					rewriteOneByteOfNv2499New(MachineTestIdx, 'F');
				}/* else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
					rewriteOneByteOfNv2499(MachineTest2Idx, 'F');
				}*/ else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
					rewriteOneByteOfNv2499New(PCBATestIdx, 'F');
				}
				resultTxt1.setText(R.string.result_fail_items);
				failItems1.setText(strMessage);
			}
		}
	}

	private void speakandStorageTest() {
		boolean flag = false;
		String pathMusic;
		int id = R.string.speak_test_message;
		int i = am.getStreamMaxVolume(3);
		int j = am.getStreamVolume(3);
		nCurrentMusicVolume = j;
		am.setStreamVolume(3, i, 0);
	
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_REMOVED)) {
			mp = MediaPlayer.create(this, R.raw.test);
			id = R.string.speak_test_hasnot_sdcard;
			flag = true;
		} else if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			pathMusic = Environment.getExternalStorageState().toString()
					+ "/test.mp3";
			File f = new File(pathMusic);
			if (!f.exists()) {
				mp = MediaPlayer.create(this, R.raw.test);
				id = R.string.speak_test_hasnot_sdcard_file;
			} else {
				Uri u = Uri.parse(pathMusic);
				mp = MediaPlayer.create(this, u);
				id = R.string.speak_test_message;
			}
		} else {
			return;
		}
		mp.setLooping(true);
		mp.start();

		String s = getString(id);
		if (flag) {
			s += "\n\n";
			s += getString(R.string.fail);
		}
		else{
			s += "\n\n";
			s += getString(R.string.success);			
		}
		txt_descript.setText(s);
		ShowTestResult(test_idx);
	}
	
	private void SIMOrUIMCardTest() {
		btn_success.setEnabled(false);
//		btn_fail.setEnabled(false);
		boolean flagSim1 = false;
		boolean flagSim2 = false;
		TelephonyManager mtm = TelephonyManager.getDefault();
		int stateSim1 = mtm.getSimState(0);
		int stateSim2 = mtm.getSimState(1);
		int SIMTypeSim1 = mtm.getNetworkType(0);
		int SIMTypeSim2 = mtm.getNetworkType(1);
		int card1Type = mtm.getCurrentPhoneType(0);
		int card2Type = mtm.getCurrentPhoneType(1);
		
		if (!mtm.isMultiSimEnabled()) {
			TelephonyManager tm2 = TelephonyManager.getDefault();
			stateSim1 = tm2.getSimState();
			SIMTypeSim1 = tm2.getNetworkType();
			card1Type = tm2.getCurrentPhoneType();
		}
		
		String strSim1 = "";
		String strSim2 = "";
		switch(SIMTypeSim1){
		case TelephonyManager.NETWORK_TYPE_UMTS:
			strSim1 += "USIM";
			break;
		case TelephonyManager.NETWORK_TYPE_CDMA:
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
		case TelephonyManager.NETWORK_TYPE_1xRTT:
		case TelephonyManager.NETWORK_TYPE_LTE:
			
			strSim1 += "UIM";
			break;
		default:strSim1 += "SIM";
			break;
		}
		
		switch(SIMTypeSim2){
		
		case TelephonyManager.NETWORK_TYPE_UMTS:
			strSim2 += "USIM";
			break;
		case TelephonyManager.NETWORK_TYPE_CDMA:
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
		case TelephonyManager.NETWORK_TYPE_1xRTT:
		case TelephonyManager.NETWORK_TYPE_LTE:
			strSim2 += "UIM";
			break;
		default:strSim2 += "SIM";
			break;
		}
		
		String s = "";
		String s1 = "";
		String s2 = "";
		switch (stateSim1) {
		case TelephonyManager.SIM_STATE_UNKNOWN:
			s1 = getString(R.string.test_sim_status_0);
			flagSim1=true;
			break;
		case TelephonyManager.SIM_STATE_ABSENT:
			s1 = getString(R.string.test_sim_status_1);
			flagSim1=true;
			break;
		case TelephonyManager.SIM_STATE_PIN_REQUIRED:
			s1 = strSim1+getString(R.string.test_sim_status_2);
			break;
		case TelephonyManager.SIM_STATE_PUK_REQUIRED:
			s1 = strSim1+getString(R.string.test_sim_status_3);
			break;
		case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
			s1 = strSim1+getString(R.string.test_sim_status_4);
			break;
		case TelephonyManager.SIM_STATE_READY:
			s1 = strSim1+getString(R.string.test_sim_status_5);
			break;
		default:
			s1 = getString(R.string.test_sim_status_0);
			break;
		}
		
		switch (stateSim2) {
		case TelephonyManager.SIM_STATE_UNKNOWN:
			s2 = getString(R.string.test_sim_status_0);
			flagSim2=true;
			break;
		case TelephonyManager.SIM_STATE_ABSENT:
			s2 = getString(R.string.test_sim_status_1);
			flagSim2=true;
			break;
		case TelephonyManager.SIM_STATE_PIN_REQUIRED:
			s2 = strSim2+getString(R.string.test_sim_status_2);
			break;
		case TelephonyManager.SIM_STATE_PUK_REQUIRED:
			s2 = strSim2+getString(R.string.test_sim_status_3);
			break;
		case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
			s2 = strSim2+getString(R.string.test_sim_status_4);
			break;
		case TelephonyManager.SIM_STATE_READY:
			s2 = strSim2+getString(R.string.test_sim_status_5);
			break;
		default:
			s2 = getString(R.string.test_sim_status_0);
			break;
		}
		s1 += "\n";
		if (flagSim1) {
			s1 += getResources().getString(R.string.fail);
		}else {
			s1 += getResources().getString(R.string.success);
		}
		if (mtm.isMultiSimEnabled()) {
			s2 += "\n";
			if (flagSim2) {
				s2 += getResources().getString(R.string.fail);
			} else {
				s2 += getResources().getString(R.string.success);
			}
		} else {
			s2 = "";
		}
		s = s1+"\n"+s2;
		
		boolean success = true;
		if (mtm.isMultiSimEnabled()) {
			if (!flagSim1 && !flagSim2) {
				success = true;
			} else {
				success = false;
			}
		} else {
			success = !flagSim1;
		}
		if (!success) {
			s += "\n\n";
			s += getResources().getString(R.string.fail);
		}else{
			s += "\n\n";
			btn_success.setEnabled(true);
			btn_fail.setEnabled(true);
		}
		txt_descript.setText(s);
		ShowTestResult(test_idx);
		
		if(success){
			btn_success.performClick();
		}else{
			btn_fail.performClick();
		}
		
		return;
	}

	private void cameraTest() {
		try {
			Intent intent = new Intent(
					"android.media.action.STILL_IMAGE_CAMERA");
			intent.putExtra("FromCit", true);
			IsAutoCITTesting = true;
			startActivity(intent);
			this.registerReceiver(mCameraReceiver, new IntentFilter("lovdream.android.cit.cameratest"));
		} catch (ActivityNotFoundException exception) {
			Log.d("CIT", "the camera activity is not exist");
			String s = getString(R.string.device_not_exist);
			s += "\n\n";
			s += getResources().getString(R.string.fail);
			txt_descript.setText(s);
		}
	}

	private void LoopbackTest(boolean flag) {
		int i;
		String s1;

		if (flag) {
			i = R.string.test_item_reciveloopback;
			s1 = getString(R.string.reciveloopback_test_message);
			am.setParameters("loopback=receiver");
			txt_descript.setText(s1);
			ShowTestResult(test_idx);
		} else {
			s1 = getString(R.string.headset_plug_warning);
			btn_success.setEnabled(false);
			btn_fail.setEnabled(false);
			txt_descript.setText(s1);
			ShowTestResult(test_idx);
			
			this.registerReceiver(mheadSetReceiver, new IntentFilter(
					Intent.ACTION_HEADSET_PLUG));
		}	
	}

	private void chargeTest() {
		m_bBatteryTesting = true;
		IntentFilter filter = new IntentFilter(
				"android.intent.action.BATTERY_CHANGED");
		registerReceiver(mBatteryInfoReceiver, filter);

	}

	private void showTemperatureInfo(Intent intent, String s) {
		if ("android.intent.action.BATTERY_CHANGED".equals(s)) {
			int i = intent.getIntExtra("temperature", 0);
			i = i/10;
			String s1 = getString(R.string.test_temperature_info) + i;
			txt_descript.setText(s1);
			ShowTestResult(test_idx);
		}
	}
	
	private int readFileFile(String filePath){   
		String res = "";  
		int ret = 0;
		try{   
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
			String str=null;
			while((str=br.readLine())!=null){
				res+=str;
			}
			ret = Integer.valueOf(res)/1000;
        }   
		catch(Exception e){   
         e.printStackTrace();   
        }   
		//fix charge test too slow by wt start
        return ret>0?ret:-ret;
      //fix charge test too slow by wt end
	}  
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			
			System.out.println("ddd:"+msg.what+","+System.currentTimeMillis());
			//chart  current show wrong by zzj start
			//current = readFileFile("/sys/class/power_supply/battery/current_now");
			//chart  current show wrong by zzj end
			showChargeTestInfo(null,"");
			
			mHandler.sendEmptyMessageDelayed(0, 5000);
		}
	};
	
	private Handler myHandler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Intent newIntent = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);
			newIntent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
			newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(newIntent);
		}
		
	};
	
	private int current = 0;

	private void showChargeTestInfo(Intent intent, String s) {
		int i = 0, j = 0, k = 0;//, current = 0;

		if ("android.intent.action.BATTERY_CHANGED".equals(s)) {
			i = intent.getIntExtra("plugged", 0);
			j = intent.getIntExtra("status", 1);
			k = intent.getIntExtra("temperature", 0);
//			current = intent.getIntExtra("battery_current",0);
//			Log.d("CJ", "current="+current);
//			
//			if (current < 0) {
//				current = -1 * current;
//			}else {
//				current = 0;
//			}
//			current = current / 1000;
			//fix charge test too slow by wt start
			if(i > 0){
				current = readFileFile("/sys/class/power_supply/battery/current_now");
			}else{
				current = 0;
			}
			
			//fix charge test too slow by wt end
			if (j == 2) {
				if (i > 0) {
					if (i == 1)
						batteryStatus = getString(R.string.battery_info_status_charging_ac);
					else
						batteryStatus = getString(R.string.battery_info_status_charging_usb);
				}
			} else if (j == 3) {
				batteryStatus = getString(R.string.battery_info_status_discharging);
			} else if (j == 4) {
				batteryStatus = getString(R.string.battery_info_status_notcharging);
			} else if (j == 5) {
				batteryStatus = getString(R.string.battery_info_status_full);
			} else {
				batteryStatus = getString(R.string.battery_info_status_unknow);
			}
		}
		String s1 = batteryStatus;
		if (j == 4) {
			btn_success.setEnabled(false);
//			btn_fail.setEnabled(false);
		} else {
			btn_success.setEnabled(true);
			btn_fail.setEnabled(true);
			
			s1 += "\n";
			s1 += getString(R.string.charge_current) + current;
			if(current <= 50){
				s1 += "("+getString(R.string.abnormal_value)+")";
			}
			// it's no need to judge temperature temporarily. keep these code			
			/*s1 += "\n";
			s1 += getString(R.string.test_temperature_info) + k;
			if(k > 45){
				s1 += "("+getString(R.string.abnormal_value)+")";
			}*/
			s1 += "\n\n";
			if (/*k > 45 || */current <= 50) {/* current <= 300 provided by driver */
				btn_success.setEnabled(false);
				s1 += getString(R.string.fail);
			}else{
				btn_success.setEnabled(true);
				btn_fail.setEnabled(true);
				s1 += getString(R.string.success);
				btn_success.performClick();
			}
		}
		try {
			txt_descript.setText(s1);
			ShowTestResult(test_idx);
			if(j==2){
				btn_success.performClick();
			}else{
				//btn_fail.performClick();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	protected void powerTest() {
		txt_descript.setText(R.string.power_message);
		ShowTestResult(test_idx);
	}

	protected void vibrationTest() {
		btn_success.setEnabled(false);
		btn_fail.setEnabled(false);
		vibrator = (Vibrator) getSystemService("vibrator");
		btn_vibration_test.setVisibility(View.VISIBLE);
//		long al[] = { 0l, 1000L };
//		vibrator.vibrate(al, 0);
		txt_descript.setText(R.string.vibration_message);
		ShowTestResult(test_idx);
	}

	protected void temperatureTest() {
		m_bTemperature = true;
		BroadcastReceiver broadcastreceiver = mBatteryInfoReceiver;
		IntentFilter intentfilter = new IntentFilter(
				"android.intent.action.BATTERY_CHANGED");
		registerReceiver(broadcastreceiver, intentfilter);
	}

	protected void wifitest() {
//		Intent intent = new Intent("android.intent.action.CIT_WIFI_TEST");
//		IsAutoCITTesting = true;
//		startActivity(intent);
		Intent intent = new Intent(this,WifiConnectTest.class);
		startActivityForResult(intent, REQUEST_ASK);
	}

	protected void bluetoothtest() {
//		Intent intent = new Intent("android.settings.BLUETOOTH_SETTINGS");
//		IsAutoCITTesting = true;
//		startActivity(intent);
		Intent intent = new Intent(this,BluetoothTestNew.class);
		startActivityForResult(intent, REQUEST_ASK);
	}

	private void masterclear() {
		Intent intent = new Intent();
		intent.setClassName("com.android.settings",
				"com.android.settings.MasterClear");
		IsAutoCITTesting = true;
		startActivity(intent);
	}

	private void CommonTest(String strAct/* , int type */) {
		int i = 0;
		btn_success.setEnabled(true);
		String ItemStr[] = getResources().getStringArray(
				R.array.CommonTestStrings);
		for (i = 0; i < ItemStr.length; i++) {
			if (strAct.equals(ItemStr[i])) {
				break;
			}
		}
		Intent intent = new Intent();
		switch (i) {
		case 0:
//			speakandStorageTest();
			/*int z = am.getStreamMaxVolume(3);
			int j = am.getStreamVolume(3);
			nCurrentMusicVolume = j;
			am.setStreamVolume(3, z, 0);
			intent = new Intent("android.intent.action.MUSIC_PLAYER");
			intent.putExtra("FromCit", true);
			IsAutoCITTesting = true;
			startActivity(intent);
			this.registerReceiver(mMusicReceiver, new IntentFilter("lovdream.android.cit.musictest"));
			//btn_success.setEnabled(false);*/
			intent.setClass(this, SpeakAndStorageTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 1:
			//this.unregisterReceiver(mMusicReceiver);
			SIMOrUIMCardTest();
			break;
		case 2:
			cameraTest();
			btn_success.setEnabled(false);
			break;
		case 3:
			
			this.unregisterReceiver(mCameraReceiver);
			chargeTest();
			break;
		case 4:
			powerTest();
			break;
		case 5:
			vibrationTest();
			break;
		case 6:
//			LoopbackTest(true);
//			intent.setClass(this, Recerver.class);
			intent.setClassName("com.android.soundrecorder", "com.android.soundrecorder.Recerver");
			intent.putExtra("com.android.soundrecorder.CIT","lovdream.android.cit.testsoundrecorderRecerver");
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 7:
//			m_bHeadsetTest = true;
//			LoopbackTest(false);
//			intent.setClass(this, Headset.class);
			intent.setClassName("com.android.soundrecorder", "com.android.soundrecorder.Headset");
			intent.putExtra("com.android.soundrecorder.CIT","lovdream.android.cit.testsoundrecorderHeadset");
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 8:
			intent.setClass(this, KeyTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 9:
			intent.setClass(this, MotionSenorTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		/*case 10:
			intent.setClass(this, MagneticTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;*/
		case 10:
			btn_success.setEnabled(true);
			btn_fail.setEnabled(true);
			wifitest();
			break;
		case 11:
			btn_success.setEnabled(true);
			btn_fail.setEnabled(true);
			bluetoothtest();
			break;
		case 12:
			btn_success.setEnabled(true);
			btn_fail.setEnabled(true);
			Log.i("BACKPRESSED","------------------->LCD");
			intent.setClass(this, LCDTest.class);
			IsAutoCITTesting = true;
			startActivity(intent);
			break;
		case 13:
//			if (CITOneByOne.mIsMsm8x25q_v5_w850 || CITOneByOne.mIsMsm8x25q_d10s_e656) {
				intent.setClass(this, TouchTest3.class);
//			} else {
//				intent.setClass(this, TouchTest.class);
//			}
			IsAutoCITTesting = true;
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 14:
			temperatureTest();
			break;
		case 15:
			intent.setClass(this, GPSTest.class);
			btn_fail.setEnabled(true);
	//		IsAutoCITTesting = true;
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 16:
			m_bHeadsetTest = true;
			intent.setClass(this, FMTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 17:
			intent.setClass(this, LightSenor.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 18:
			intent.setClass(this, ProximitySenor.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 19:
			intent.setClassName("com.android.settings",
					"com.android.settings.MasterClear");
			IsAutoCITTesting = true;
			startActivity(intent);
			break;
		case 20:
			btn_success.setEnabled(true);
			intent.setClass(this, Version.class);
			IsAutoCITTesting = true;
			startActivity(intent);
			break;
		case 21:
			intent.setClass(this, BacklightTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 22:
			intent.setClass(this, ButtonLights.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 23:
			intent.setClass(this, TouchProximitySenor.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 24:
			intent.setClass(this, FlashTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 25:
			intent.setClass(this, RebootTest.class);
			startActivity(intent);
			break;
		case 26:
			intent.setClass(this, BreathingLight.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 27:
			intent.setClass(this, GyroSensor.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 28:
			intent.setClass(this, NFCTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 29:
			intent.setClass(this,PCBANfc.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 30:
			IsAutoCITTesting = true;
			intent.setClass(this, TouchTest2.class);
			startActivity(intent);
			break;
		case 31:
			IsAutoCITTesting = true;
			intent.setClass(this, DeviceInfoCit.class);
			startActivity(intent);
			break;
		case 32:
			tpTest();
			break;
		case 33:
			intent.setClass(this, HallTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		
		case 34:
			intent.setClass(this, AllKeyTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		
		case 36:
			intent.setClass(this, CompassTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
			
		case 37:
			intent.setClass(this, HeartBeatTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
			
		case 38:
			intent.setClass(this, HotTFTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		
		case 39:
			intent.setClass(this, OtgTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		
		case 40:
			intent.setClassName("com.android.soundrecorder", "com.android.soundrecorder.BackRecerver");
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 43:
			intent.setClass(this, EightPinTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 45:
			intent.setClass(this, EightPinOtgTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 46:
			intent.setClass(this, TwoColorLights.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 47:
			chargeTest();
			break;
		case 49:
			wifi5Gtest();
			break;
		case 51:
                        intent.setClass(this, EightPinTest.class);
                        startActivityForResult(intent, REQUEST_ASK);
                        break;
        // add side otg test by zzj start 
		case 52:
            intent.setClass(this, SideOtgTest.class);
            startActivityForResult(intent, REQUEST_ASK);
            break;
            
		 case 53:
			   intent.setClass(this, FingerPrintTest.class);
	            startActivityForResult(intent, REQUEST_ASK);
			 break;
			 
		 case 55:
				   intent.setClass(this, ExternalAntennaTest.class);
		            startActivityForResult(intent, REQUEST_ASK);
				break;
		 case 56:
				intent.setClass(this, FlashTestFront.class);
				startActivityForResult(intent, REQUEST_ASK);
				break;
		}
		
		//add side otg test by zzj end 
	}
	private void wifi5Gtest() {
		Intent intent = new Intent(this,Wifi5GConnectTest.class);
		startActivityForResult(intent, REQUEST_ASK);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Bundle bundle;
		int result = 0;
		if (requestCode == REQUEST_ASK) {
			if (resultCode == RESULT_OK) {
				bundle = data.getExtras();
				result = bundle.getInt("test_result");
				if (result == 0) {
					int i = 0;
					HashMap<String, String> map;
					for (i = 0; i < FailItems.size(); i++) {
						map = FailItems.get(i);
						if (map.containsKey(Integer.toString(test_idx))) {
							FailItems.remove(i);
						}
					}
					HashMap<String, String> map1 = new HashMap<String, String>();
					map1.put(Integer.toString(test_idx), AcitItemStr[test_idx]);
					FailItems.add(map1);
					saveToNv2499(false);
					mResult[test_idx] = 0;
				}if(result == 2){
					saveToNv2499(true);
					mResult[test_idx] = 1;
					this.finish();
				}
				else {
					saveToNv2499(true);
					mResult[test_idx] = 1;
				}
			}
			if(!(result == 2)){
			test_idx++;
			if (test_idx >= test_cnt) {
				ShowTestResult(test_idx);
			} else {
				CommonTest(AcitItemStr[test_idx]);
			}
			}
		}
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		// TODO Auto-generated method stub
		setContentView(R.layout.autotest_result);
		Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.unloadSoundEffects();
		txt_testType = (TextView) findViewById(R.id.autotest_type);
		txt_item_name = (TextView) findViewById(R.id.item_name);
		txt_descript = (TextView) findViewById(R.id.descript);

		btn_success = (Button) findViewById(R.id.btn_success);
		btn_success.setOnClickListener(this);
		btn_fail = (Button) findViewById(R.id.btn_fail);
		btn_fail.setOnClickListener(this);
		btn_vibration_test = (Button) findViewById(R.id.vibration_test);
		btn_vibration_test.setOnClickListener(this);
//		mNv = new QcNvItems(this);
		
		mPowerManager = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
		mWakeLock = this.mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");

		if (CITMain.NoBt) {
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				rewriteOneByteOfNv2499(MachineBluetooth, 'B');
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				rewriteOneByteOfNv2499(PCBABluetooth, 'B');
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				
			}
		}
		
		if (CITMain.NoFm) {
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				rewriteOneByteOfNv2499(MachineFMRadio, 'B');
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				rewriteOneByteOfNv2499(PCBAFMRadio, 'B');
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				
			}
		}

		if (CITMain.NoWifi) {
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				rewriteOneByteOfNv2499(MachineWifi, 'B');
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				rewriteOneByteOfNv2499(PCBAWifi, 'B');
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				rewriteOneByteOfNv2499(MachineWifi2, 'B');
			}
		}
		
		if (CITMain.NoGps) {
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				rewriteOneByteOfNv2499(MachineGPSTest, 'B');
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				rewriteOneByteOfNv2499(PCBAGPSTest, 'B');
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				
			}
		}
		
		if (CITMain.NoDistancesensor) {
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				rewriteOneByteOfNv2499(MachineProximitySenor, 'B');
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				rewriteOneByteOfNv2499(PCBAProximitySenor, 'B');
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				
			}
		}
		
		if (CITMain.NoLightsensor) {
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				rewriteOneByteOfNv2499(MachineLightSenor, 'B');
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				rewriteOneByteOfNv2499(PCBALightSenor, 'B');
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				
			}
		}
		
		if (!CITMain.HasButtonLight) {
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				rewriteOneByteOfNv2499(MachineButtonlights, 'B');
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				rewriteOneByteOfNv2499(PCBAButtonlights, 'B');
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				
			}
		}
		
		if (CITMain.NoGsensor) {
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				rewriteOneByteOfNv2499(MachineMotionSensor, 'B');
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				rewriteOneByteOfNv2499(PCBAMotionSensor, 'B');
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				
			}
		}
		
		if (!CITMain.HasTpinsteadsensor) {
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				rewriteOneByteOfNv2499(MachineTPProximitySenor, 'B');
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				rewriteOneByteOfNv2499(PCBATPProximitySenor, 'B');
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				
			}
		}
		if (CITMain.NoFlashLight) {
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				rewriteOneByteOfNv2499(MachineFlashlight, 'B');
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				rewriteOneByteOfNv2499(PCBAFlashlight, 'B');
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				
			}
		}
		AllItemStr = getResources().getStringArray(R.array.CommonTestStrings);
		
		if (AcitItemStr == null) {
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				String[] s1 = getResources().getStringArray(
						R.array.MachineAutoTestStrings);
				ArrayList<String> d = new ArrayList<String>();
				int len = s1.length;
				for(int i=0;i<len;i++)
				{	
					if(mIsA420){
						d.add(s1[i]);
				     }else{
					if (!((!CITOneByOne.HasBackMic && i == 4) ||   (CITOneByOne.NoFm && i==7) || (CITOneByOne.NoGsensor && i==9) 
							|| (!CITOneByOne.HasButtonLight && i==10) || (CITOneByOne.NoFlashLight && i==11) || (!CITOneByOne.HasBreathingLight && i == 15) 
							|| (!CITOneByOne.HasGyroSensor && i == 16) || ((CITMain.mIsA500 || !CITOneByOne.HasNFC) && i==17) || (!CITOneByOne.HasHall && i==18)
							|| (!CITOneByOne.HasCompass && i == 19) || (!CITOneByOne.HasOTG && i == 20) || (!CITOneByOne.Has8Pin && i==21) || (!CITOneByOne.Has8PinOtg && i==22) 
							|| (CITOneByOne.NoWifi && i == 24) || (CITOneByOne.NoBt && i==25) || (CITOneByOne.NoLightsensor && i==27) 
							|| (CITOneByOne.NoDistancesensor && i==28) || (CITOneByOne.NoGps && i==29) || (!CITOneByOne.HasTwoColorLights && i==30)
							|| (!CITOneByOne.mIsDoubleScreen && i == 31) || (!CITOneByOne.HasBackCharging && i == 32) || (!CITOneByOne.Has5gwifi && i==33)
							|| (!CITOneByOne.Has14Pin && i==34) || (!CITOneByOne.HasSideOtg && i==35) || (!CITOneByOne.HasExternalAnte && i==36) || (!CITOneByOne.HasFingerPrint && i==37)|| (!CITOneByOne.HasFrontFlashLight && i==38))){
						d.add(s1[i]);
					}}
				}
				len = d.size();
				AcitItemStr = new String[len];
				AcitItemStr = d.toArray(AcitItemStr);
				d.clear();
				d = null;
				
				txt_testType.setText(R.string.Machine_auto_test);
			} else {
				String[] s1 = getResources().getStringArray(
						R.array.PCBAAutoTestStrings);
				ArrayList<String> d = new ArrayList<String>();
				int len = s1.length;
				for(int i=0;i<len;i++)
				{
					if(mIsA420){
						d.add(s1[i]);
					}else{
					if (!((!CITOneByOne.HasBackMic && i == 4) || (CITOneByOne.NoFm && i==7) || (CITOneByOne.NoGsensor && i==9) 
							|| (!CITOneByOne.HasButtonLight && i==10) || (CITOneByOne.NoFlashLight && i==11) || (!CITOneByOne.HasBreathingLight && i == 13) 
							|| (!CITOneByOne.HasGyroSensor && i == 14) || ((CITMain.mIsA500 || !CITOneByOne.HasNFC) && i==15) || (!CITOneByOne.HasHall && i==16) 
							|| (!CITOneByOne.HasCompass && i == 17) || (!CITOneByOne.HasOTG && i == 18) || (!CITOneByOne.Has8Pin && i == 19) || (!CITOneByOne.Has8PinOtg && i == 20) 
							|| (CITOneByOne.NoWifi && i==22) || (CITOneByOne.NoBt && i==23) || (CITOneByOne.NoLightsensor && i==25) 
							|| (CITOneByOne.NoDistancesensor && i==26) || (CITOneByOne.NoGps && i==27) || (!CITOneByOne.HasTwoColorLights && i==28)
							|| (!CITOneByOne.mIsDoubleScreen && i == 29) || (!CITOneByOne.HasBackCharging && i == 30))){
						d.add(s1[i]);
					}}
				}
				len = d.size();
				AcitItemStr = new String[len];
				AcitItemStr = d.toArray(AcitItemStr);
				d.clear();
				d = null;
				txt_testType.setText(R.string.PCBA_auto_test);
			}
			test_cnt = AcitItemStr.length;
		}

		am = (AudioManager) getSystemService("audio");
		int j = am.getStreamVolume(3);
		nCurrentMusicVolume = j;
		mFirstFlag = true;
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		testDone = false;
		if (mFirstFlag) {
			mFirstFlag = false;
			CommonTest(AcitItemStr[test_idx]);
		}
		mWakeLock.acquire();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mWakeLock.release();
	}

	@Override
	protected void onRestart() {
		if (IsAutoCITTesting) {
			IsAutoCITTesting = false;
			txt_descript.setText(R.string.complete);
			ShowTestResult(test_idx);
		}
		super.onRestart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if (test_idx >= test_cnt) {
			test_idx = test_cnt - 1;
			testDone = true;
		}
		HandleAfterTest(AcitItemStr[test_idx]);
		
		if (!FailItems.isEmpty()) {
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				rewriteOneByteOfNv2499(MachineTestIdx, 'F');
			} /*else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				rewriteOneByteOfNv2499(MachineTest2Idx, 'F');
			}*/ else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				rewriteOneByteOfNv2499(PCBATestIdx, 'F');
			}
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 1);
		mAudioManager.loadSoundEffects();
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = sDateFormat.format(new java.util.Date());
		String result = "";
		Log.d("CJ", "date="+date);
		Log.d("CJ", "mResult.length="+mResult.length);
		if (testDone) {
			test_idx++;
		}
		for (int i = test_idx; i < mResult.length; i++) {
			mResult[i] = 2;
		}
		for (int i = 0; i < mResult.length; i++) {
			result = result + String.valueOf(mResult[i]);
		}
		for (int i = 0; i < mResult.length; i++) {
			Log.d("CJ", "mResult[i]="+mResult[i]);
		}
		Log.d("CJ", "result="+result);
		if (test_idx != 0) {
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				storeToDb("citMachineTestResult", date, result);

			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				storeToDb("citPCBATestResult", date, result);
			}
		}
		am.setStreamVolume(3, nCurrentMusicVolume, 0);
	}
	
	private void storeToDb (String table, String time, String result) {
		ContentValues values = new ContentValues();  
	    DatabaseHelper dbHelper = new DatabaseHelper(this,  
	            "cit_test_result_db", 2);   
	    SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();  
	//      values.put("id", 1);  
		values.put("time", time);
		values.put("result", result);
		sqliteDatabase.insert(table, null, values);
		Log.d("CJ", "getCursorOfDb(table).getCount()="+getCursorOfDb(table).getCount());
		Cursor cursor = getCursorOfDb(table);
		if (cursor.getCount() > 3) {
			deleteFromDb(table);
		}
		cursor.close();
	}

	private Cursor getCursorOfDb (String table){
		DatabaseHelper dbHelper = new DatabaseHelper(this,  
                "cit_test_result_db", 2);
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query(table, null, null, null, null, null, null);
        return cursor;
	}
	
	private void deleteFromDb (String table){
		Log.d("CJ", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$44");
		String _id = null;
		Cursor cursor = getCursorOfDb(table);
		cursor.moveToFirst();
		_id = cursor.getString(cursor.getColumnIndex("_id"));
		Log.d("CJ", "_id="+_id);
		DatabaseHelper dbHelper = new DatabaseHelper(this,  
	            "cit_test_result_db", 2);   
	    SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase(); 
	    sqliteDatabase.delete(table, "_id=?", new String[]{_id});
	    cursor.close();
	}

	public void onClick(View v) {
		int id = v.getId();
		int i = 0;
		HashMap<String, String> map;
		for (i = 0; i < FailItems.size(); i++) {
			map = FailItems.get(i);
			if (map.containsKey(Integer.toString(test_idx))) {
				FailItems.remove(i);
			}
		}
		
		if (id ==  R.id.vibration_test) {
			btn_success.setEnabled(true);
			btn_fail.setEnabled(true);
			btn_vibration_test.setVisibility(View.GONE);
			long al[] = { 0,1000L,1000l, 1000l,1000l };
			if(vibrator == null){
				vibrator = (Vibrator) getSystemService("vibrator");
				Log.i("GSPH", ";;;;;;;;;;;;;;;;;;;;;;;;;");
			}
			vibrator.vibrate(al, 0);
			return;
		}

		switch (id) {
		case R.id.btn_success:
			saveToNv2499(true);
			mResult[test_idx] = 1;
			break;
		case R.id.btn_fail:
			saveToNv2499(false);
			HashMap<String, String> map1 = new HashMap<String, String>();
			map1.put(Integer.toString(test_idx), AcitItemStr[test_idx]);
			FailItems.add(map1);
			mResult[test_idx] = 0;
			break;
		}
		
		//start defend  exception 12519 add by chr  
		try {
			HandleAfterTest(AcitItemStr[test_idx]);
		} catch (Exception e) {
			// TODO: handle exception
			Log.v("Demo321", "exception AcitItemStr[test_idx]");
		}
		//end 
		
		test_idx++;
		if (test_idx >= test_cnt) {
			ShowTestResult(test_idx);
		} else {
			CommonTest(AcitItemStr[test_idx]);
		}
	}

	private void HandleAfterTest(String strAct) {
		int i = 0;
		String ItemStr[] = getResources().getStringArray(
				R.array.CommonTestStrings);
		for (i = 0; i < ItemStr.length; i++) {
			if (strAct.equals(ItemStr[i])) {
				break;
			}
		}
		switch (i) {
		case 0:
			if(mp!=null){
				mp.stop();
				mp.release();
				mp=null;
			}
//			am.setStreamVolume(3, nCurrentMusicVolume, 0);
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			if(m_bBatteryTesting)
			{
				this.unregisterReceiver(mBatteryInfoReceiver);
				mHandler.removeMessages(0);
				current = 0;
				m_bBatteryTesting = false;
			}
			break;
		case 4:
			break;
		case 5:
			if(vibrator == null)
			{
				vibrationTest();
			}
			vibrator.cancel();
			vibrator=null;
			break;
		case 6:
			am.setParameters("loopback=off");
			break;
		case 7:
//			am.setParameters("loopback=off");
//			this.unregisterReceiver(mheadSetReceiver);
//			m_bHeadsetTest = false;
			break;
		case 8:
			break;
		case 9:
			break;
		case 10:
			break;
		case 11:
			break;
		case 12:
			break;
		case 13:
			break;
		case 14:
			if(m_bTemperature)
			{
				this.unregisterReceiver(mBatteryInfoReceiver);
				m_bTemperature = false;
			}
			break;
		case 15:
			//m_bTemperature = false;
			//this.unregisterReceiver(mBatteryInfoReceiver);
			break;
		case 16:
			break;
		case 17:
			m_bHeadsetTest = false;
			break;
		case 18:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		/*if (test_idx < test_cnt){
			menu.add(1, Menu.FIRST, 0, R.string.retest);
		}*/
		menu.add(2, Menu.FIRST + 1, 0, R.string.exit);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 1:
			HandleAfterTest(AcitItemStr[test_idx]);
			CommonTest(AcitItemStr[test_idx]);
			break;
		case 2:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	//private QcNvItems mNv;
	
	private final static int MachineTestIdx = 3;
//	private final static int MachineTest2Idx = 4;
	private final static int PCBATestIdx = 4;
	
	private final static int SpeakerAndStorage = 0;
	private final static int CheckSIM = 1;
	private final static int Camera = 2;
	private final static int Charge = 3;
//	private final static int SleepWake = 4;
	private final static int Vibrate = 5;
	private final static int RecerverLoopback = 6;
	private final static int HeadsetLoopback = 7;
	private final static int Keypad = 8;
	private final static int MotionSensor = 9;
	private final static int Wifi = 10;
	private final static int Bluetooth = 11;    
	private final static int LCD = 12;
	private final static int Touch = 13;
	private final static int Temperature = 14;
	private final static int GPSTest = 15;
	private final static int FMRadio = 16;
	private final static int LightSenor = 17;
	private final static int ProximitySenor = 18;
//	private final static int MasterClear = 19;
	private final static int Version = 20;
	private final static int Backlighttest = 21;
	private final static int Buttonlights = 22;
	private final static int TPProximitySenor = 23;
	private final static int Flashlight = 24;
	
	private final static int MachineSpeakerAndStorage = 32;
	private final static int MachineCamera = 33;
	private final static int MachineCheckSIM = 34;
	private final static int MachineRecerverLoopback = 35;
	private final static int MachineVibrate = 36;
	private final static int MachineHeadsetLoopback = 37;
	private final static int MachineFMRadio = 38;
	private final static int MachineKeypad = 39;
	private final static int MachineMotionSensor = 40;
	private final static int MachineWifi = 41;
	private final static int MachineBluetooth = 42;
	private final static int MachineCharge = 43;
	private final static int MachineLightSenor = 44;
	private final static int MachineProximitySenor = 45;
	private final static int MachineTPProximitySenor = 46;
	private final static int MachineBacklighttest = 47;
	private final static int MachineButtonlights = 48;
	private final static int MachineFlashlight = 49;
	private final static int MachineGPSTest = 50;
	private final static int MachineLCD = 51;
	private final static int MachineTouch = 52;
	private final static int MachineVersion = 53;
	
	private final static int MachineKeypad2 = 96;
	private final static int MachineRecerverLoopback2 = 97;
	private final static int MachineCamera2 = 98;
	private final static int MachineSpeakerAndStorage2 = 99;
	private final static int MachineVibrate2 = 100;
	private final static int MachineHeadsetLoopback2 = 101;
	private final static int MachineWifi2 = 102;
	private final static int MachineTouch2 = 103;
	
	private final static int PCBASpeakerAndStorage = 64;
	private final static int PCBACamera = 65;
	private final static int PCBACheckSIM = 66;
	private final static int PCBARecerverLoopback = 67;
	private final static int PCBAVibrate = 68;
	private final static int PCBAHeadsetLoopback = 69;
	private final static int PCBAFMRadio = 70;
	private final static int PCBAKeypad = 71;
	private final static int PCBAMotionSensor = 72;
	private final static int PCBAWifi = 73;
	private final static int PCBABluetooth = 74;
	private final static int PCBACharge = 75;
	private final static int PCBALightSenor = 76;
	private final static int PCBAProximitySenor = 77;
	private final static int PCBATPProximitySenor = 78;
	private final static int PCBABacklighttest = 79;
	private final static int PCBAButtonlights = 80;
	private final static int PCBAFlashlight = 81;
	private final static int PCBAGPSTest = 82;
	private final static int PCBAVersion = 83;
	
	
	private boolean mSetWifiResult = false;
	
	private void rewriteOneByteOfNv2499(int idx, char testResult) {
		if (!CITMain.mSaveToNv2499) {
			return;
		}
		byte[] bresult = null;
		bresult = SystemUtil.getNvFactoryData3IByte();
		if (bresult == null) {
			bresult = new byte[128];
			for (int i = 0; i < bresult.length; i++) {
				bresult[i] = 'U';
			}
		}
		if(bresult.length <= idx){
			Log.e("CIT","rewriteOneByteOfNv2499New(),getNvFactoryData3IByte length error");
			return;
		}
		bresult[idx] = (byte) testResult;
		SystemUtil.setNvFactoryData3IByte(bresult);
	}
	
	private void rewriteOneByteOfNv2499New(int idx, char testResult) {
		byte[] bresult = null;
		bresult = SystemUtil.getNvFactoryData3IByte();
		if (bresult == null) {
			bresult = new byte[128];
			for (int i = 0; i < bresult.length; i++) {
				bresult[i] = 'U';
			}
		}
		if(bresult.length <= idx){
			Log.e("CIT","rewriteOneByteOfNv2499New(),getNvFactoryData3IByte length error");
			return;
		}
		bresult[idx] = (byte) testResult;
		SystemUtil.setNvFactoryData3IByte(bresult);
	}
	
	public String getStr(int num, String str){
		StringBuffer sb = new StringBuffer("");
		for(int i=0;i<num;i++){
		   sb.append(str);
		}
		return sb.toString();
	}
	
	public void saveToNv2499(Boolean pass) {
		if (!CITMain.mSaveToNv2499) {
			return;
		}
		int len = AllItemStr.length;
		int testNowItem = 100;
		for (int i = 0; i < len; i++) {
			if (AcitItemStr[test_idx].equals(AllItemStr[i])) {
				testNowItem = i;
			}
		}
		switch (testNowItem) {
		case SpeakerAndStorage:
			Log.d("CJ", "SpeakerAndStorage");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineSpeakerAndStorage, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineSpeakerAndStorage, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBASpeakerAndStorage, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBASpeakerAndStorage, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineSpeakerAndStorage2, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineSpeakerAndStorage2, 'F');
				}
			}
			break;
			
		case CheckSIM:
			Log.d("CJ", "CheckSIM");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineCheckSIM, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineCheckSIM, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBACheckSIM, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBACheckSIM, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					
				} else {
					
				}
			}
			break;
			
		case Camera:
			Log.d("CJ", "Camera");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineCamera, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineCamera, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBACamera, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBACamera, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineCamera2, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineCamera2, 'F');
				}
			}
			break;
			
		case Charge:
			Log.d("CJ", "Charge");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineCharge, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineCharge, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBACharge, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBACharge, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					
				} else {
					
				}
			}
			break;
			
		case Vibrate:
			Log.d("CJ", "Vibrate");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineVibrate, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineVibrate, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBAVibrate, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBAVibrate, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineVibrate2, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineVibrate2, 'F');
				}
			}
			break;
			
		case RecerverLoopback:
			Log.d("CJ", "RecerverLoopback");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineRecerverLoopback, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineRecerverLoopback, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBARecerverLoopback, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBARecerverLoopback, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineRecerverLoopback2, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineRecerverLoopback2, 'F');
				}
			}
			break;
			
		case HeadsetLoopback:
			Log.d("CJ", "HeadsetLoopback");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineHeadsetLoopback, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineHeadsetLoopback, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBAHeadsetLoopback, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBAHeadsetLoopback, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineHeadsetLoopback2, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineHeadsetLoopback2, 'F');
				}
			}
			break;
			
		case Keypad:
			Log.d("CJ", "Keypad");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineKeypad, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineKeypad, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBAKeypad, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBAKeypad, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineKeypad2, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineKeypad2, 'F');
				}
			}
			break;
			
		case MotionSensor:
			Log.d("CJ", "MotionSensor");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineMotionSensor, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineMotionSensor, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBAMotionSensor, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBAMotionSensor, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					
				} else {
					
				}
			}
			break;
			
		case Wifi:
			Log.d("CJ", "Wifi");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineWifi, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineWifi, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBAWifi, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBAWifi, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineWifi2, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineWifi2, 'F');
				}
			}
			break;
			
		case Bluetooth:
			Log.d("CJ", "Bluetooth");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineBluetooth, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineBluetooth, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBABluetooth, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBABluetooth, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					
				} else {
					
				}
			}
			break;
			
		case LCD:
			Log.d("CJ", "LCD");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineLCD, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineLCD, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					
				} else {

				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					
				} else {
					
				}
			}
			break;
			
		case Touch:
			Log.d("CJ", "Touch");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineTouch, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineTouch, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					
				} else {

				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineTouch2, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineTouch2, 'F');
				}
			}
			break;
			
		case Temperature:
			Log.d("CJ", "Temperature");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					
				} else {

				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					
				} else {

				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					
				} else {
					
				}
			}
			break;
			
		case GPSTest:
			Log.d("CJ", "GPSTest");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineGPSTest, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineGPSTest, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBAGPSTest, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBAGPSTest, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					
				} else {
					
				}
			}
			break;
			
		case FMRadio:
			Log.d("CJ", "FMRadio");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineFMRadio, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineFMRadio, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBAFMRadio, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBAFMRadio, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					
				} else {
					
				}
			}
			break;
			
		case LightSenor:
			Log.d("CJ", "LightSenor");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineLightSenor, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineLightSenor, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBALightSenor, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBALightSenor, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					
				} else {
					
				}
			}
			break;
			
		case ProximitySenor:
			Log.d("CJ", "ProximitySenor");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineProximitySenor, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineProximitySenor, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBAProximitySenor, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBAProximitySenor, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					
				} else {
					
				}
			}
			break;
			
		case Version:
			Log.d("CJ", "Version");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineVersion, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineVersion, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBAVersion, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBAVersion, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					
				} else {
					
				}
			}
			break;
			
		case Backlighttest:
			Log.d("CJ", "Backlighttest");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineBacklighttest, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineBacklighttest, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBABacklighttest, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBABacklighttest, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					
				} else {
					
				}
			}
			break;
			
		case Buttonlights:
			Log.d("CJ", "Buttonlights");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineButtonlights, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineButtonlights, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBAButtonlights, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBAButtonlights, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					
				} else {
					
				}
			}
			break;
			
		case TPProximitySenor:
			Log.d("CJ", "TPProximitySenor");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineTPProximitySenor, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineTPProximitySenor, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBATPProximitySenor, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBATPProximitySenor, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					
				} else {
					
				}
			}
			break;			
		case Flashlight:
			Log.d("CJ", "Flashlight");
			if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(MachineFlashlight, 'P');
				} else {
					rewriteOneByteOfNv2499(MachineFlashlight, 'F');
				}
			} else if (CITMain.CITtype == CITMain.PCBA_AUTO_TEST) {
				if (pass) {
					rewriteOneByteOfNv2499(PCBAFlashlight, 'P');
				} else {
					rewriteOneByteOfNv2499(PCBAFlashlight, 'F');
				}
			} else if (CITMain.CITtype == CITMain.MACHINE_AUTO_TEST_2) {
				if (pass) {
					
				} else {
					
				}
			}
			break;

		default:
			break;
		}
	}
	
	private void tpTest() {
		String s = getString(R.string.tp_test_loading);
		txt_descript.setText(s);
		ShowTestResult(test_idx);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				int result = CITOneByOne.readFile("/sys/class/msg-ito-test-m/device/ito_test")
						.equals("0") ? R.string.result_pass
						: R.string.result_fail;
				Message msg = Message.obtain(mTPTestHandler, 0);
				msg.arg1 = result;
				msg.arg2 = test_idx;
				mTPTestHandler.sendMessage(msg);
			}
		}).start();
	}
	
	private Handler mTPTestHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);	
			if (msg.arg2 == test_idx) {
				txt_descript.setText(msg.arg1);				
			}
		}
	};

}
