package lovdream.android.cit;

import lovdream.android.cit.GPSTest.GpsInfo;


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.android.internal.telephony.PhoneConstants;
//import com.qualcomm.qcnvitems.QcNvItems;
import com.lovdream.util.SystemUtil;
import android.view.ViewConfiguration;
import java.lang.reflect.Field;
import android.widget.Button;

public class CITOneByOne extends ListActivity {
    private AudioManager mAudioManager;
	private ArrayList<String> Items = new ArrayList<String>();
	private String[] AcitItemStr = null;
	private final String configPath="//system//etc//usb_testmode.txt";

	public static final int MONO_TEST = 0;
	public static final int MACHINE_AUTO_TEST = 1;
	public static final int PCBA_AUTO_TEST = 2;
	public static int CITtype = -1;

	public static final String CIT_TYPE = "CIT type";
	public static final String CIT_ITEM_IDX = "CIT idex";
	public static final String CIT_ITEM_CNT = "CIT count";
	private SharedPreferences mSharedPreferences;
	private LayoutInflater mInflater;
	private Bitmap PASS_ICON;
	private Bitmap FAIL_ICON;
	private static int mTestPosition = 0;
	private static int [] mResultList = new int [40];
	static final private int MENU_CLEAN_STATE = Menu.FIRST;
	private static boolean mIsItemClick = false;
	private static final int REQUEST_ASK = 1;

	private LocationListener locationListener;
	private GpsStatus.Listener statusListener;
	private GpsStatus.NmeaListener nmeaListener;
	private MediaPlayer mp;
	private BroadcastReceiver mBatteryInfoReceiver;
	private Vibrator vibrator;
	private String as[];
	private AlertDialog mDialog = null;
	private LocationManager m_mgr;
	boolean m_bBatteryTesting;
	boolean m_bTemperature;
	boolean mBackChargingTest;
	//add wireless charge by zzj start
	boolean mＷirelessBatteryTesting;
	//add wireless charge by zzj end
	AudioManager am;
	String batteryStatus;
	int nCurrentMusicVolume;
	boolean P800 = true;//SystemProperties.get("ro.product.name").equals("msm8939_64_p800");
	private boolean mIsA420 =android.os. SystemProperties.get("ro.product.name").equals("msm8916_64_a420");
	//fix cit keytest by wt begin
	public static boolean mUse2015Style= SystemProperties.get("ro.product.systemui2015style").equals("1");
	//fix cit keytest by wt end
	public static boolean HasExternalAnte = SystemProperties.get("ro.product.externalante","0").equals("1");
	public static boolean NoTpTest = true;//SystemProperties.get("ro.product.noTpTest","0").equals("1");
	public static boolean HasSideOtg = SystemProperties.get("ro.product.sideotg","0").equals("1");
	public static boolean NoBt = SystemProperties.get("ro.product.nobt","0").equals("1"); 
	public static boolean NoFm = SystemProperties.get("ro.product.nofm","0").equals("1"); 
	public static boolean NoWifi = SystemProperties.get("ro.product.nowifi","0").equals("1"); 
	public static boolean NoGps = SystemProperties.get("ro.product.nogps","0").equals("1"); 
	public static boolean NoTemp = SystemProperties.get("ro.product.notemperature","0").equals("1"); 
	public static boolean NoLightsensor = SystemProperties.get("ro.product.nolightsensor","0").equals("1"); 
	public static boolean NoDistancesensor = SystemProperties.get("ro.product.nodistancesensor","0").equals("1"); 
	public static boolean NoButtonlights = SystemProperties.get("ro.product.nobuttonlights","0").equals("1"); 
	public static boolean HasTpinsteadsensor = SystemProperties.get("ro.product.tpinsteadsensor","0").equals("1"); 
	private boolean mIsMsm7627a_d7_e86 = SystemProperties.get("ro.product.name").equals("msm7627a_d7_e86");
	public static boolean NoGsensor = SystemProperties.get("ro.product.nogsensor","0").equals("1");
	public static boolean HasFlashLight = SystemProperties.get("ro.product.hasflashlight","0").equals("1");
	public static boolean NoFlashLight = SystemProperties.get("ro.product.noflashlight","0").equals("1");
	public static boolean HasButtonLight = SystemProperties.get("ro.product.hasbuttonlight","0").equals("1");
	public static boolean HasBreathingLight = SystemProperties.get("ro.product.breathing_light","0").equals("1");
	public static boolean HasGyroSensor = SystemProperties.get("ro.product.gyrosensor","0").equals("1");
	public static boolean HasNFC = SystemProperties.get("ro.product.nfc","0").equals("true");
	public static boolean mIsMsm8x25q_v5_w850 = SystemProperties.get("ro.product.name").equals("msm8x25q_v5_w850");
	public static boolean mIsMsm8x25q_d10s_e656 = SystemProperties.get("ro.product.name").equals("msm8x25q_d10s_e656");
	public static boolean mIsDoubleScreen = SystemProperties.get("ro.config.doublescreen","0").equals("1");
	public static boolean HasCompass = SystemProperties.get("ro.product.compass","0").equals("1");
	public static boolean HasBarometer = SystemProperties.get("ro.product.barometer","0").equals("1");
	public static boolean Has8Pin =  SystemProperties.get("ro.product.eightpin","0").equals("1");
	public static boolean Has5gwifi =  SystemProperties.get("ro.product.5gwifi","0").equals("1");
	public static boolean Has14Pin =  SystemProperties.get("ro.product.fourteenpin","0").equals("1");
	public static boolean HasResetKey = SystemProperties.get("ro.product.resetkey","0").equals("1");
	public static boolean HasHall = SystemProperties.get("ro.product.hall","0").equals("1");
	public static boolean HasOTG = SystemProperties.get("ro.product.otg","0").equals("1");
	public static boolean HasHeartBeat = SystemProperties.get("ro.product.heartbeat","0").equals("1");
	public static boolean HasFunsKey = SystemProperties.get("ro.product.funskey","0").equals("1");
	public static boolean HasHotTF = SystemProperties.get("ro.product.hottf","0").equals("1");
	public static boolean HasBackMic = SystemProperties.get("ro.product.backmic","0").equals("1");
	public static boolean HasRebootTest = SystemProperties.get("ro.product.reboottest","0").equals("1");
	public static boolean Has8PinOtg = SystemProperties.get("ro.product.eightpinotg","0").equals("1");
	public static boolean HasTwoColorLights = SystemProperties.get("ro.product.twocolorlights","0").equals("1");
	public static boolean HasBackCharging = SystemProperties.get("ro.product.backcharging","0").equals("1");
	public static boolean HasFrontFlashLight = SystemProperties.get("ro.build.product","").equals("msm8953_64_c551");
	public static boolean HasFingerPrint = SystemProperties.get("ro.build.product","").equals("msm8953_64_c551");

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();

		switch (position) {
//		case 0:
//			CITtype = PCBA_AUTO_TEST;
//			intent.setClass(this, CITAuto.class);
//			startActivity(intent);
//			break;
//		case 1:
//			CITtype = MACHINE_AUTO_TEST;
//			intent.setClass(this, CITAuto.class);
//			startActivity(intent);
//			break;
		default:
			mTestPosition = position;
				//Log.d("XJ","position==="+position+"str==="+Items.get(position)); 
			CommonTest(Items.get(position));

			break;
		}

		super.onListItemClick(l, v, position, id);
	}

	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.main);
		mInflater = LayoutInflater.from(this);
		mSharedPreferences = this.getSharedPreferences("one_by_one_data", Context.MODE_PRIVATE );
		if(mSharedPreferences == null){
			try {
				mSharedPreferences = this.createPackageContext("com.lovderam.cit",
						Context.CONTEXT_IGNORE_SECURITY).getSharedPreferences(
						"one_by_one_data", Context.MODE_PRIVATE); 
			} catch (Exception e) {
				Log.e("DeputyBoardTest", "createPackage failed!");
			}
		}
		PASS_ICON = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.test_pass);
		FAIL_ICON = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.test_fail);
		getListItems();
		Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.unloadSoundEffects();
//		if (mIsMsm7627a_d7_e86) {
//			if (!NoGps) {
//				m_mgr = (LocationManager) getSystemService("location");
//				locationListener = new LocaltionLis();
////				statusListener = new GpsStatusListner();
////				nmeaListener = new NmeaLis();
//				android.provider.Settings.Secure.setLocationProviderEnabled(
//						getContentResolver(), "gps", true);
//				m_mgr.requestLocationUpdates("gps", 1000L, 1F, locationListener);
////				m_mgr.addGpsStatusListener(statusListener);
////				m_mgr.addNmeaListener(nmeaListener);
//				
//			}
//		}
		
		m_bBatteryTesting = false;
		m_bTemperature = false;
		mBackChargingTest = false;
		batteryStatus = "";
		mBatteryInfoReceiver = new BatteryReceiver();
		am = (AudioManager) getSystemService("audio");
		int j = am.getStreamVolume(3);
		nCurrentMusicVolume = j;
		forceShowOverflowMenu();
	}
	
	private void forceShowOverflowMenu() {	
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		if (mIsMsm7627a_d7_e86) {
//			if (!NoGps) {
//				android.provider.Settings.Secure.setLocationProviderEnabled(
//						getContentResolver(), "gps", false);
//				m_mgr.removeUpdates(locationListener);
////				m_mgr.removeGpsStatusListener(statusListener);
////				m_mgr.removeNmeaListener(nmeaListener);
//			}
//		}
		Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 1);
		mAudioManager.loadSoundEffects();
		am.setStreamVolume(3, nCurrentMusicVolume, 0);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if(mp!=null){
			if(mp.isPlaying()){
				mp.stop();
				mp.release();
				mp=null;
			}
		}
		if(vibrator!=null)
		{
			vibrator.cancel();
		}
		if(mDialog!=null){
			mDialog.dismiss();
			mDialog=null;
		}
		super.onStop();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		am.setStreamVolume(3, nCurrentMusicVolume, 0);
		if(mIsItemClick){
			AlertDialog.Builder builder = (new AlertDialog.Builder(this)).setTitle(
					Items.get(mTestPosition)).setMessage(R.string.test_result_prompt);
			builder.setPositiveButton(R.string.success, new OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					saveValues(mTestPosition,1);
					mIsItemClick = false;
				}
				
			}).setNegativeButton(R.string.fail, new OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					saveValues(mTestPosition,0);
					mIsItemClick = false;
				}
				
			}).create().show();
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	private void getListItems() {
		as = getResources().getStringArray(R.array.OneByOneTypeStrings);
		int j = 0;
		do {
			if (j < as.length) {
				
				/*if (!((NoBt && j==11)
						|| (NoFm && j==7) || (NoDistancesensor && j==14)|| (NoGps && j==19) 
						|| (NoTemp && j==24) || (NoLightsensor && j==13)|| (NoWifi && j==10) 
						|| (!HasButtonLight && j==17)|| (NoGsensor && j==9)|| (!HasTpinsteadsensor && j==15)||(j==16)
						|| (NoFlashLight && j==18) || (!HasBreathingLight && j==25) || (!HasGyroSensor && j==26) 
						|| (!HasNFC && j==27) || (!HasNFC && j==28) || (NoTpTest && j==32) || (!HasHall && j == 33)
						|| (!mIsDoubleScreen && j == 34) || (!HasCompass && j == 35) || (!HasHeartBeat && j == 36)
						|| (!HasHotTF && j == 37) ||(!HasOTG && j == 38) ||  (!HasBackMic && j == 39) || (!HasRebootTest && j == 40)))*/
				if(mIsA420){
					Items.add(as[j]);
				}else{
				if (!((NoBt && j==11)
						|| (NoFm && j==7) || (NoDistancesensor && j==14)|| (NoGps && j==19) 
						|| (NoTemp && j==24) || (NoLightsensor && j==13)|| (NoWifi && j==10) 
						|| (!HasButtonLight && j==17)|| (NoGsensor && j==9)|| (!HasTpinsteadsensor && j==15)||(j==16)
						|| (NoFlashLight && j==18) || (!HasBreathingLight && j==25) || (!HasGyroSensor && j==26) 
						|| (!HasNFC && j==27)  || (NoTpTest && j==31) || (!HasHall && j == 32)
						|| (!mIsDoubleScreen && j == 33) || (!HasCompass && j == 34) || (!HasHeartBeat && j == 35)
						|| (!HasHotTF && j == 36) ||(!HasOTG && j == 37) ||  (!HasBackMic && j == 38) || (!HasRebootTest && j == 39)
						||(!HasBarometer && j == 40) ||(!Has8Pin && j == 46) ||(!HasResetKey && j == 41) ||(!Has8PinOtg && j == 44)
						||(!HasTwoColorLights && j == 42) || ( !Has5gwifi && j==43) || (!HasBackCharging && j == 45) || (!Has14Pin && j==47) || (!HasSideOtg && j==48 )
						|| (!HasExternalAnte && j == 49) || (!HasFingerPrint && j == 50)|| (!HasFrontFlashLight && j == 51)))
				{
					Items.add(as[j]);
				}}
				j++;
			} else {
//				setListAdapter(new ArrayAdapter<String>(this,
//						android.R.layout.simple_list_item_1, Items));
//				getListView().setTextFilterEnabled(true);
				getDefaultValues();
				setListAdapter(mBaseAdapter);
				getListView().setTextFilterEnabled(true);
				return;
			}
		} while (true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		SubMenu addMenu = menu.addSubMenu(0, MENU_CLEAN_STATE, Menu.NONE, R.string.clean_state);
		addMenu.setIcon(android.R.drawable.ic_menu_revert);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
        case (MENU_CLEAN_STATE):
            cleanTestState();
            break;
		}
		return super.onOptionsItemSelected(item);
	}
		
	private void cleanTestState(){
		for (int i = 0; i < Items.size(); i++) {
			saveValues(i,-1);
		}
	}

	private void saveValues(int key, int value){
		mResultList[key] = value;
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putInt(key+"", value);
		editor.commit();
		mBaseAdapter.notifyDataSetChanged();
	}
	
	private void getDefaultValues(){
		for (int i = 0; i < Items.size(); i++){
			mResultList [i] = mSharedPreferences.getInt(i+"", -1);
		}
	}
	
	private BaseAdapter mBaseAdapter = new BaseAdapter() {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Items.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return Items.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null){
				convertView = mInflater.inflate(R.layout.obo_list_item, null);
			}
			TextView text = (TextView) convertView.findViewById(R.id.text1);
			ImageView image = (ImageView) convertView.findViewById(R.id.icon_center);
			if(mResultList[position] == -1){
				image.setImageBitmap(null);
			}else if(mResultList[position] == 1){
				image.setImageBitmap(PASS_ICON);
			}else if(mResultList[position] == 0){
				image.setImageBitmap(FAIL_ICON);
			}
			text.setText(Items.get(position));
			return convertView;
		}
	};
	
	private void speakandStorageTest() {
		String pathMusic;
		int i = am.getStreamMaxVolume(3);
		int j = am.getStreamVolume(3);
		nCurrentMusicVolume = j;
		am.setStreamVolume(3, i, 0);
		if(mp!=null){
			if(mp.isPlaying()){
				mp.stop();
				mp.release();
				mp=null;
			}
		}

		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_REMOVED)) {
			mp = MediaPlayer.create(this, R.raw.test);
			android.app.AlertDialog.Builder builder = (new android.app.AlertDialog.Builder(
					this)).setTitle(R.string.test_item_speak);
			String s = getString(R.string.speak_test_hasnot_sdcard);
			android.app.AlertDialog.Builder builder1 = builder.setMessage(s);
			mDialog = builder1.setPositiveButton(R.string.alert_dialog_ok, new AudioLis())
					.create();
			mDialog.show();
		} else if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			pathMusic = Environment.getExternalStorageDirectory().toString()
					+ "/test.mp3";
			Log.d("CJ", "pathMusic="+pathMusic);
			File f = new File(pathMusic);
			Log.d("CJ", "f.exists()="+f.exists());
			if (!f.exists()) {
				mp = MediaPlayer.create(this, R.raw.test);
				android.app.AlertDialog.Builder builder = (new android.app.AlertDialog.Builder(
						this)).setTitle(R.string.test_item_speak);
				String s = getString(R.string.speak_test_hasnot_sdcard_file);
				android.app.AlertDialog.Builder builder1 = builder
						.setMessage(s);
				mDialog = builder1.setPositiveButton(R.string.alert_dialog_ok,
						new AudioLis()).create();
				mDialog.show();
			} else {
				Uri u = Uri.parse(pathMusic);
				mp = MediaPlayer.create(this, u);
				android.app.AlertDialog.Builder builder = (new android.app.AlertDialog.Builder(
						this)).setTitle(R.string.test_item_speak);
				String s = getString(R.string.speak_test_message);
				android.app.AlertDialog.Builder builder1 = builder
						.setMessage(s);
				mDialog = builder1.setPositiveButton(R.string.alert_dialog_ok,
						new AudioLis()).create();
				mDialog.show();
			}
		} else {
			return;
		}
		mp.setLooping(true);
		mp.start();
	}

	private class SucessListener implements android.content.DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialoginterface, int i) {
			saveValues(mTestPosition, 1);
		}
	}

	private class FailListener implements android.content.DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialoginterface, int i) {
			saveValues(mTestPosition, 0);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_ASK) {
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				int result = bundle.getInt("test_result");
				saveValues(mTestPosition,result);
			}
		}
	}
	
	private void SIMOrUIMCardTest() {
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
		Log.d("CJ", "card1Type="+card1Type);
		Log.d("CJ", "card2Type="+card2Type);
		
//		Log.d("CJ", "===============================");
//		Log.d("CJ", "tm.getCardType(0)="+tm.getCardType(0));
//		Log.d("CJ", "tm.getCardType(1)="+tm.getCardType(1));
//		Log.d("CJ", "===============================");
		
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
				
		String s1 = "";
		String s2 = "";
		switch (stateSim1) {
		case TelephonyManager.SIM_STATE_UNKNOWN:
			s1 = getString(R.string.test_sim_status_0);
			break;
		case TelephonyManager.SIM_STATE_ABSENT:
			s1 = getString(R.string.test_sim_status_1);
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
			break;
		case TelephonyManager.SIM_STATE_ABSENT:
			s2 = getString(R.string.test_sim_status_1_1);
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
		if (mtm.isMultiSimEnabled()) {
			AlertDialog.Builder builder = (new android.app.AlertDialog.Builder(this))
					.setTitle(R.string.test_item_simoruim).setMessage(s1+"\n"+s2);
			builder.setPositiveButton(R.string.success, new SucessListener())
					.setNegativeButton(R.string.fail, new FailListener())
					.create().show();
		} else {
			AlertDialog.Builder builder = (new android.app.AlertDialog.Builder(this))
					.setTitle(R.string.test_item_simoruim).setMessage(s1);
			builder.setPositiveButton(R.string.success, new SucessListener())
					.setNegativeButton(R.string.fail, new FailListener())
					.create().show();
		}
		
		return;
	}

	private void cameraTest() {
		try {
			Intent intent = new Intent(
					"android.media.action.STILL_IMAGE_CAMERA");
			intent.putExtra("FromCit", true);
			// intent.setFlags(0x4000000);
			startActivity(intent);
		} catch (ActivityNotFoundException exception) {
			Log.d("CIT", "the camera activity is not exist");
			String s = getString(R.string.device_not_exist);
			AlertDialog.Builder builder = (new android.app.AlertDialog.Builder(
					this)).setTitle(R.string.test_item_camera).setMessage(s);
			builder.setPositiveButton(R.string.alert_dialog_ok, null).create()
					.show();
		}
	}

	private void chargeTest() {
		m_bBatteryTesting = true;
		BroadcastReceiver broadcastreceiver = mBatteryInfoReceiver;
		IntentFilter intentfilter = new IntentFilter(
				"android.intent.action.BATTERY_CHANGED");
		registerReceiver(broadcastreceiver, intentfilter);
	}

	private void powerTest() {
		AlertDialog.Builder builder = (new AlertDialog.Builder(this)).setTitle(
				R.string.test_item_power).setMessage(R.string.power_message);
		builder.setPositiveButton(R.string.success, new SucessListener())
		   .setNegativeButton(R.string.fail, new FailListener())
		   .create().show();
	}
	
	private void resetTest() {
		AlertDialog.Builder builder = (new AlertDialog.Builder(this)).setTitle(
				R.string.test_item_reset).setMessage(R.string.reset_message);
		builder.setPositiveButton(R.string.success, new SucessListener())
		   .setNegativeButton(R.string.fail, new FailListener())
		   .create().show();
	}

	private void vibrationTest() {
		vibrator = (Vibrator) getSystemService("vibrator");
		long al[] = { 0,1000L,1000l, 1000l,1000l };
		vibrator.vibrate(al, 0);
		AlertDialog.Builder builder = (new AlertDialog.Builder(this)).setTitle(
				R.string.test_item_vibration).setMessage(
				R.string.vibration_message);
		mDialog = builder.setPositiveButton(R.string.success, new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				saveValues(mTestPosition,1);
				vibrator.cancel();
			}
			
		}).setNegativeButton(R.string.fail, new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				saveValues(mTestPosition,0);
				vibrator.cancel();
			}
		}).create();
		mDialog.show();
	}

	private void LoopbackTest(boolean flag) {
		int i;
		String s1;
		android.app.AlertDialog.Builder builder;
		if (flag) {
			i = R.string.test_item_reciveloopback;
			s1 = getString(R.string.reciveloopback_test_message);
			am.setParameters("loopback=receiver");
			builder = (new android.app.AlertDialog.Builder(this)).setTitle(i)
					.setMessage(s1);
			builder.setPositiveButton(R.string.alert_dialog_ok, new LoopLis())
					.create().show();
		} else {
			AudioManager audiomanager = (AudioManager) getSystemService(AUDIO_SERVICE);
			if (!audiomanager.isWiredHeadsetOn()) {
				Toast warning = Toast.makeText(this, getString(R.string.headset_plug_warning),
						Toast.LENGTH_LONG);
				warning.setGravity(Gravity.CENTER, 0, 0);
				warning.show();
			} else {
				i = R.string.test_item_heakloopback;
				s1 = getString(R.string.headloopback_test_message);
				am.setParameters("loopback=headset");
				builder = (new android.app.AlertDialog.Builder(this)).setTitle(
						i).setMessage(s1);
				builder.setPositiveButton(R.string.alert_dialog_ok,
						new LoopLis()).create().show();
			}
		}
	}

	private class BatteryReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (m_bBatteryTesting) {
				m_bBatteryTesting = false;
				showChargeTestInfo(intent, s);
			}
			if (m_bTemperature) {
				m_bTemperature = false;
				showTemperatureInfo(intent, s);
			}
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

	private void showChargeTestInfo(Intent intent, String s) {
		int i = 0, j = 0, k = 0, current = 0;
		if ("android.intent.action.BATTERY_CHANGED".equals(s)) {
			i = intent.getIntExtra("plugged", 0);
			j = intent.getIntExtra("status", 1);
			k = intent.getIntExtra("temperature", 0);
//			current = intent.getIntExtra("battery_current",0);
//
//			if (current < 0) {
//				current = -1 * current;
//			}else {
//				current = 0;
//			}
			//fix charge test too slow by wt start
			if(i > 0){
				current = readFileFile("/sys/class/power_supply/battery/current_now");
			}else{
				current = 0;
			}
			//fix charge test too slow by wt end
			//current = current / 1000;
			if (j == 2) {
				if (i > 0) {
					if (i == 1)
						batteryStatus = getString(R.string.battery_info_status_charging_ac);
					else
						batteryStatus = getString(R.string.battery_info_status_charging_usb);

				}
				if(mBackChargingTest){
					batteryStatus=getString(R.string.battery_info_status_back_charging);
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
		if (j == 4) {
			Toast warning = Toast.makeText(this,
					getString(R.string.battery_info_status_notcharging),
					Toast.LENGTH_LONG);
			warning.setGravity(Gravity.CENTER, 0, 0);
			warning.show();
			saveValues(mTestPosition,0);
		} else {
			String s1 = batteryStatus;
                     //modify back charging show by zzj start
                        if(!mBackChargingTest){
                       
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
				s1 += getString(R.string.fail);
			}else{
				s1 += getString(R.string.success);
			}}
                      //modify back charging show by zzj end
            //add wireless charge by zzj start
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setTitle(mBackChargingTest ? R.string.test_item_back_charge : mＷirelessBatteryTesting ? R.string.test_item_wireless_charge : R.string.test_item_charge);
			//add wireless charge by zzj end
			builder.setMessage(s1);
			builder.setPositiveButton(R.string.success, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					saveValues(mTestPosition, 1);
					unregisterBatteryReceiver();
				}

			}).setNegativeButton(R.string.fail, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					saveValues(mTestPosition, 0);
					unregisterBatteryReceiver();
				}

			});

			AlertDialog dialog= builder.create();
                        dialog.show();
                        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        if(current >50){
                                button.setEnabled(true);
                        }else{
                                button.setEnabled(false);
                        }
			mBackChargingTest = false;
			//add wireless charge by zzj start
			mＷirelessBatteryTesting =false;
			//add wireless charge by zzj end
		}
	}

	private void unregisterBatteryReceiver(){
		try{
			unregisterReceiver(mBatteryInfoReceiver);
		}catch(Exception e){
			Log.e("CITOneByOne","unregisterBatteryReceiver wrong e--------------="+e.toString());
		}
	}
	
	private void showTemperatureInfo(Intent intent, String s) {
		if ("android.intent.action.BATTERY_CHANGED".equals(s)) {
			int i = intent.getIntExtra("temperature", 0);
			i = i/10;
			String s1 = (new StringBuilder())
					.append("The phone temperature is ").append(i).toString();

			AlertDialog.Builder builder = (new AlertDialog.Builder(this))
					.setTitle(R.string.test_temperature_title);
			StringBuilder stringbuilder = new StringBuilder();
			String s2 = getString(R.string.test_temperature_info);
			String s3 = stringbuilder.append(s2).append(i).toString();
			builder.setMessage(s3)
					.setPositiveButton(R.string.success, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							saveValues(mTestPosition, 1);
							unregisterBatteryReceiver();
						}

					}).setNegativeButton(R.string.fail, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							saveValues(mTestPosition, 0);
							unregisterBatteryReceiver();
						}

					}).create().show();
		}
	}
	
	private void wifitest() {
//		Intent intent = new Intent().setClass(this, WifiTest.class);
//		startActivity(intent);
//		Intent intent = new Intent("android.intent.action.CIT_WIFI_TEST");
//		startActivity(intent);
		Intent intent = new Intent(this,WifiConnectTest.class);
		startActivityForResult(intent, REQUEST_ASK);
	}
	private void wifi5Gtest() {
		Intent intent = new Intent(this,Wifi5GConnectTest.class);
		startActivityForResult(intent, REQUEST_ASK);
	}
	private void bluetoothtest() {
//		Intent intent = new Intent().setClass(this, BluetoothTest.class);;
//		startActivity(intent);
//		Intent intent = new Intent("android.intent.action.CIT_BT_TEST");
//		startActivity(intent);
		Intent intent = new Intent(this,BluetoothTestNew.class);
		startActivityForResult(intent, REQUEST_ASK);
	}
	
	private void temperatureTest() {
		m_bTemperature = true;
		BroadcastReceiver broadcastreceiver = mBatteryInfoReceiver;
		IntentFilter intentfilter = new IntentFilter(
				"android.intent.action.BATTERY_CHANGED");
		registerReceiver(broadcastreceiver, intentfilter);
	}

	private class BatteryDilogListener implements
			android.content.DialogInterface.OnClickListener {

		public void onClick(DialogInterface dialoginterface, int i) {
			unregisterReceiver(mBatteryInfoReceiver);
		}
	}

	private class TempList implements
			android.content.DialogInterface.OnClickListener {

		public void onClick(DialogInterface dialoginterface, int i) {
			unregisterReceiver(mBatteryInfoReceiver);
		}
	}

	private class LoopLis implements
			android.content.DialogInterface.OnClickListener {

		public void onClick(DialogInterface dialoginterface, int i) {
			am.setParameters("loopback=off");
		}
	}

	private class VibratorListener implements
			android.content.DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialoginterface, int i) {
			vibrator.cancel();
		}
	}

	private class AudioLis implements
			android.content.DialogInterface.OnClickListener {

		public void onClick(DialogInterface dialoginterface, int i) {
			mp.stop();
			mp.release();
			mp = null;
			am.setStreamVolume(3, nCurrentMusicVolume, 0);
		}
	}

	  private boolean  storeToFile(int i){
		  try {
			FileOutputStream outStream=new FileOutputStream(configPath);
			 outStream.write((byte)i);
			 outStream.close();
			 return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	  }
	
	//private QcNvItems mNv;  
	
	public String getStr(int num, String str){
		StringBuffer sb = new StringBuffer("");
		for(int i=0;i<num;i++){
		   sb.append(str);
		}
		return sb.toString();
	}
	
	private String getNvFactory3() {
		String NvFactory3 = "111";
//		try {
//			NvFactory3 = mNv.getNvFactoryData3I();
//		} catch (IOException e) {
//			Log.d("CJ", "getNvFactoryData3I==== IOException---->" + e);
//		}
		return NvFactory3;
	}
	  
	private void setNvFactoryData3() {
//		try {
//			mNv.setNvFactoryData3I(getStr(128, "U"));
//		} catch (IOException e) {
//			Log.d("CJ", "setNvFactoryData3I==== IOException---->"+e);
//		}
	}
	
	private void CommonTest(String strAct/* , int type */) {
		int i = 0;
//		mNv = new QcNvItems(this);
		String ItemStr[] = getResources().getStringArray(
				R.array.CommonTestStrings);
		for (i = 0; i < ItemStr.length; i++) {
			if (strAct.equals(ItemStr[i])) {
				break;
			}
		}
		//Log.d("XJ","i==="+i);
		Intent intent = new Intent();
		switch (i) {
		case 0:
//			speakandStorageTest();
			mIsItemClick = true;
			int z = am.getStreamMaxVolume(3);
			int j = am.getStreamVolume(3);
			nCurrentMusicVolume = j;
			am.setStreamVolume(3, z, 0);
			intent = new Intent("android.intent.action.MUSIC_PLAYER");
			intent.putExtra("FromCit", true);
			startActivity(intent);
			break;
		case 1:
			setNvFactoryData3();
			SIMOrUIMCardTest();
			break;
		case 2:
			mIsItemClick = true;
			cameraTest();
			break;
		case 3:
			getNvFactory3();
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
			intent.setClassName("com.android.soundrecorder", "com.android.soundrecorder.Recerver");
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 7:
//			LoopbackTest(false);
			intent.setClassName("com.android.soundrecorder", "com.android.soundrecorder.Headset");
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
			startActivity(intent);
			break;*/
		case 10:
			wifitest();
			break;
		case 11:
			bluetoothtest();
			break;
		case 12:
			mIsItemClick = true;
			intent.setClass(this, LCDTest.class);
			startActivity(intent);
			break;
		case 13:
//			if (mIsMsm8x25q_v5_w850) {
//				intent.setClass(this, TouchTest2.class);
//			} else {
				intent.setClass(this, TouchTest3.class);
//			}
				startActivityForResult(intent, REQUEST_ASK);
			break;
		case 14:
			temperatureTest();
			break;
		case 15:
			intent.setClass(this, GPSTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 16:
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
			mIsItemClick = true;
			storeToFile(0); //turn off test mode
			intent.setAction("android.settings.BACKUP_AND_RESET_SETTINGS");
			//### 0015274 fix restore item  of CIT was pop up windows.by zj. 2017.6.15
			intent.setComponent(new  ComponentName("com.android.settings", "com.android.settings.Settings$PrivacySettingsActivity"));
			startActivity(intent);
			break;
		case 20:
			mIsItemClick = true;
			intent.setClass(this, Version.class);
			startActivity(intent);
			break;
//		case 21:
//			intent.setClass(this, RebootTest.class);
////			intent.putExtra("mmmisTest", true);
//			startActivity(intent);
//			break;
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
			mIsItemClick = true;
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
			mIsItemClick = true;
			intent.setClass(this, TouchTest2.class);
			startActivity(intent);
			break;
		case 31:
			mIsItemClick = true;
			intent.setClass(this, DeviceInfoCit.class);
			startActivity(intent);
			break;
		/*case 31:
			final String[] arrayString = new String[] { getString(R.string.experience_model), getString(R.string.network_mode), 
					getString(R.string.existing_network_mode), getString(R.string.rf_mode), getString(R.string.debug_mode)};
			final int idx = Integer.valueOf(SystemProperties.get("persist.yulong.defaultmode", "0"));
			selectedIndex = idx;
			new AlertDialog.Builder(this)
			.setSingleChoiceItems(arrayString, idx, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					selectedIndex = which;
				}
				
			})
            .setTitle(R.string.run_mode)
            .setIcon(android.R.drawable.ic_dialog_info)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
						Log.d("CJ", "selectedIndex="+selectedIndex);
						SystemProperties.set("persist.yulong.defaultmode", String.valueOf(selectedIndex));
				
				 }
			}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

			    public void onClick(DialogInterface dialog, int which) {
			      // TODO Auto-generated method stub

			    }
			}).show();
			break;
		case 32:
			intent.setComponent(new ComponentName("com.yulong.android.ota",
						"com.yulong.android.ota.ui.LoginActivity"));
			startActivity(intent);
			break;
		case 33:
			intent.setComponent(new ComponentName("com.yulong.android.ota",
							"com.yulong.android.ota.ui.TestActivity"));
			startActivity(intent);
			break;*/
			
		case 32:	
			mTpTestTextView = new TextView(this);
			mTpTestTextView.setTextColor(Color.WHITE);
			mTpTestTextView.setTextSize(20);
			mTpTestTextView.setText(R.string.tp_test_loading);
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					int result = readFile("/sys/class/msg-ito-test-m/device/ito_test")
							.equals("0") ? R.string.result_pass
							: R.string.result_fail;
					Message msg = Message.obtain(mHandler, 0);
					msg.arg1 = result;
					mHandler.sendMessage(msg);
				}
			}).start();
			new AlertDialog.Builder(this)
			.setPositiveButton(android.R.string.ok, null)
			.setView(mTpTestTextView)
			.setTitle(R.string.tp_test)
			.show();
			break;

		case 33:
			intent.setClass(this, HallTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		
		case 34:
			intent.setClass(this, AllKeyTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		
		case 35:
			mIsItemClick = true;
			intent.setClass(this, TestFlag.class);
			startActivity(intent);
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

		case 41:
			mIsItemClick = true;
			intent.setClass(this, RebootTest.class);
			startActivity(intent);
			break;
		case 42:
			intent.setClass(this, BarometerTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 43:
			intent.setClass(this, EightPinTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 44:
			resetTest();
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
			mBackChargingTest = true;
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
        // add side otg test by zzj end
        	//add wireless charge by zzj start
			case 54:
				mＷirelessBatteryTesting=true;
				chargeTest();
				break;
				//add wireless charge by zzj end
			case 55:
				   intent.setClass(this, ExternalAntennaTest.class);
		            startActivityForResult(intent, REQUEST_ASK);
				break;
			case 56:
				intent.setClass(this, FlashTestFront.class);
				startActivityForResult(intent, REQUEST_ASK);
				break;
		}
	}
	
	TextView mTpTestTextView;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);				
			if (mTpTestTextView != null) {
				mTpTestTextView.setText(msg.arg1);				
			}
		}
	};
	
	public static String readFile(String filePath){   
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
	
	private int selectedIndex = 0;
	private Toast toast = null;
	
	public void displayToast(String str) {
        if (toast == null) {
            toast = Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG);
        } else {
            toast.setText(str);
        }
        toast.show();
    }  
	
	private class LocaltionLis implements LocationListener {

		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String s) {
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String s) {
		}

		public void onStatusChanged(String s, int i, Bundle bundle) {
		}

	}
	
	private void updateWithNewLocation(Location location) {
	}
	
	private class GpsStatusListner implements GpsStatus.Listener {

		public void onGpsStatusChanged(int status) {
			
		}

	}
	
	private class NmeaLis implements GpsStatus.NmeaListener {

		public void onNmeaReceived(long timestamp, String nmea) {
			
		}

	}

}
