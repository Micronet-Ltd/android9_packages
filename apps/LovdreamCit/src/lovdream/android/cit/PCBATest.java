package lovdream.android.cit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import com.lovdream.util.SystemUtil;

import android.os.SystemProperties;
import android.provider.Settings;

public class PCBATest extends Activity implements OnClickListener {
	
	private Button mTestItemBtns[];
	private String mCitItemStrs[];
	private String mPCBAAllItemStrs[];
	private int mCitItemsCount;
	private int mTestResults[];
	private static final int REQUEST_ASK = 1;
	private int mClickId = 0;
	private boolean mIsShowConfirm = false;
	boolean mBatteryTesting;
	boolean mBackChargingTest;
	boolean mAutoBatteryTesting;
	//add wireless charge by zzj start
	boolean mＷirelessBatteryTesting;
	//add wireless charge by zzj end
	private Vibrator mVibrator;
	private AlertDialog mDialog = null;
	private Button mSuccessBtn;
	private SharedPreferences mSharedPreferences;
	static final private int MENU_CLEAN_STATE = Menu.FIRST;
	private boolean mHallOpen = false;
	private boolean mChartBtnClick = false;
	private boolean mIsA420 =android.os. SystemProperties.get("ro.product.name").equals("msm8916_64_a420");
	//add 4g test by zzj start
	private char[] getChars(byte[] bytes) {
		Charset cs = Charset.forName("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);
		return cb.array();
	} 
	private int get4GIndext(){
		for(int i=0;i<mTestItemBtns.length;i++){
			CharSequence str=mTestItemBtns[i].getText();
			
			if(str.toString().equals("4G FT")){
				return i;
			}
		}
		return -1;
	}
	boolean is4Gpass=false;
      private void update4Gtest(){
    		byte[] bresult = null;
    		int i=get4GIndext();
    		mTestItemBtns[i].setClickable(false);
    		if(i==-1 ){
    			return;
    		}
    		bresult = SystemUtil.getNvFactoryData3IByte();
    		if (bresult == null) {
    			mTestItemBtns[i].setTextColor(android.graphics.Color.YELLOW);
    			mTestItemBtns[i].setText(R.string.no_init_prompt_4g);
    		}else{
    			if(i!=-1){
    	  			char[] cChar = getChars(bresult);
					char tmp;
					if("msm8953_64_c551".equals(SystemProperties.get("ro.build.product"))){
						tmp = cChar[1];
					}else{
						tmp = cChar[2];
					}
        			if('P'== tmp){
        				mTestItemBtns[i].setTextColor(android.graphics.Color.GREEN);
        				is4Gpass=true;
        			}else if('F'== tmp){
        				mTestItemBtns[i].setTextColor(android.graphics.Color.RED);
        				is4Gpass=false;
        			}else{
        				mTestItemBtns[i].setTextColor(android.graphics.Color.WHITE);
        				mTestItemBtns[i].setText(R.string.frist_4g);
        				is4Gpass=false;
        			}
    			}
    		}
    	  
      }
	//add 4g test by zzj end
	private boolean mIsa406 = SystemProperties.get("ro.product.name").contains("msm8916_64_a406")
                                                || SystemProperties.get("ro.product.name").contains("msm8916_a406") ;
	private boolean mIsC550 = SystemProperties.get("ro.product.name").contains("msm8953_64_c550");	
	private boolean P800 = SystemProperties.get("ro.product.name").contains("msm8939_64_p800");
	private boolean mIsS550 = SystemProperties.get("ro.product.name").contains("msm8939_64_s550");
        private boolean mIsBDGps = mIsC550;
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		// TODO Auto-generated method stub
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		setContentView(R.layout.pcba_test);
		sendBroadcast(new Intent("com.lovdream.hide_keyguard"));
		CITMain.CITtype = CITMain.PCBA_AUTO_TEST;
		initBtn();
		initArrays();
	        update4Gtest();
		initData();
		if(!mIsA420){
		 autoTest();
		}
		//start fix bug of 12571
		if(P800 || mIsS550)
		{
			Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);
		}
		//end 
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(mIsShowConfirm){
			AlertDialog.Builder builder = (new android.app.AlertDialog.Builder(this)).setTitle(
					mCitItemStrs[mClickId]).setMessage(R.string.test_result_prompt);
			builder.setPositiveButton(R.string.success, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					updateBtnState(true);
				}
			}).setNegativeButton(R.string.fail, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					updateBtnState(false);
				}
			}).create().show();
			mIsShowConfirm = false;
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(mVibrator != null){
			mVibrator.cancel();
		}
		if(mDialog != null){
			mDialog.dismiss();
			mDialog=null;
		}
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

	private void initBtn(){
		mTestItemBtns = new Button[39];
		mTestItemBtns[0] = (Button) findViewById(R.id.btn_0);
		mTestItemBtns[1] = (Button) findViewById(R.id.btn_1);
		mTestItemBtns[2] = (Button) findViewById(R.id.btn_2);
		mTestItemBtns[3] = (Button) findViewById(R.id.btn_3);
		mTestItemBtns[4] = (Button) findViewById(R.id.btn_4);
		mTestItemBtns[5] = (Button) findViewById(R.id.btn_5);
		mTestItemBtns[6] = (Button) findViewById(R.id.btn_6);
		mTestItemBtns[7] = (Button) findViewById(R.id.btn_7);
		mTestItemBtns[8] = (Button) findViewById(R.id.btn_8);
		mTestItemBtns[9] = (Button) findViewById(R.id.btn_9);
		mTestItemBtns[10] = (Button) findViewById(R.id.btn_10);
		mTestItemBtns[11] = (Button) findViewById(R.id.btn_11);
		mTestItemBtns[12] = (Button) findViewById(R.id.btn_12);
		mTestItemBtns[13] = (Button) findViewById(R.id.btn_13);
		mTestItemBtns[14] = (Button) findViewById(R.id.btn_14);
		mTestItemBtns[15] = (Button) findViewById(R.id.btn_15);
		mTestItemBtns[16] = (Button) findViewById(R.id.btn_16);
		mTestItemBtns[17] = (Button) findViewById(R.id.btn_17);
		mTestItemBtns[18] = (Button) findViewById(R.id.btn_18);
		mTestItemBtns[19] = (Button) findViewById(R.id.btn_19);
		mTestItemBtns[20] = (Button) findViewById(R.id.btn_20);
		mTestItemBtns[21] = (Button) findViewById(R.id.btn_21);
		mTestItemBtns[22] = (Button) findViewById(R.id.btn_22);
		mTestItemBtns[23] = (Button) findViewById(R.id.btn_23);
		mTestItemBtns[24] = (Button) findViewById(R.id.btn_24);
		mTestItemBtns[25] = (Button) findViewById(R.id.btn_25);
		mTestItemBtns[26] = (Button) findViewById(R.id.btn_26);
		mTestItemBtns[27] = (Button) findViewById(R.id.btn_27);
		mTestItemBtns[28] = (Button) findViewById(R.id.btn_28);
		mTestItemBtns[29] = (Button) findViewById(R.id.btn_29);
		mTestItemBtns[30] = (Button) findViewById(R.id.btn_30);
		mTestItemBtns[31] = (Button) findViewById(R.id.btn_31);
		mTestItemBtns[32] = (Button) findViewById(R.id.btn_32);
		mTestItemBtns[33] = (Button) findViewById(R.id.btn_33);
		mTestItemBtns[34] = (Button) findViewById(R.id.btn_34);
		mTestItemBtns[35] = (Button) findViewById(R.id.btn_35);
		mTestItemBtns[36] = (Button) findViewById(R.id.btn_36);
		mTestItemBtns[37] = (Button) findViewById(R.id.btn_37);
		mTestItemBtns[38] = (Button) findViewById(R.id.btn_38);
		for(int i = 0; i < mTestItemBtns.length; i ++){
			mTestItemBtns[i].setId(i);
		}
		mSuccessBtn = (Button) findViewById(R.id.success_btn);
		mSuccessBtn.setOnClickListener(this);
		mSuccessBtn.setEnabled(false);
	}
	private void initArrays(){
    
		if(mCitItemStrs == null){
			mPCBAAllItemStrs = getResources().getStringArray(R.array.PCBAAutoTestStrings);
			ArrayList<String> d = new ArrayList<String>();
			int len = mPCBAAllItemStrs.length;
			for(int i=0;i<len;i++)
			{	
				if(mIsA420){
					d.add(mPCBAAllItemStrs[i]);
				}else{
				 if (!((!CITOneByOne.HasBackMic && i == 4) || (CITOneByOne.NoFm && i==7) || (CITOneByOne.NoGsensor && i==9) 
						|| (!CITOneByOne.HasButtonLight && i==10) || (CITOneByOne.NoFlashLight && i==11) || (!CITOneByOne.HasBreathingLight && i == 13) 
						|| (!CITOneByOne.HasGyroSensor && i == 14) || ((CITMain.mIsA500 || !CITOneByOne.HasNFC) && i==15) || (!CITOneByOne.HasHall && i==16) 
						|| (!CITOneByOne.HasCompass && i == 17) || (!CITOneByOne.HasOTG && i == 18) || (!CITOneByOne.Has8Pin && i == 19) || (!CITOneByOne.Has8PinOtg && i == 20) 
						|| (CITOneByOne.NoWifi && i==22) || (CITOneByOne.NoBt && i==23) || (CITOneByOne.NoLightsensor && i==25) 
						|| (CITOneByOne.NoDistancesensor && i==26) || (CITOneByOne.NoGps && i==27) || (!CITOneByOne.HasTwoColorLights && i==28)
						|| (!CITOneByOne.mIsDoubleScreen && i == 29) || (!CITOneByOne.HasBackCharging && i == 30) || (!mIsBDGps && i==32) || (!CITOneByOne.Has5gwifi && i==33) 
						|| (!CITOneByOne.Has14Pin && i==34) || (!CITOneByOne.HasSideOtg && i==35) || (!CITOneByOne.HasExternalAnte && i==36) || (!CITOneByOne.HasFingerPrint && i==37) || (!CITOneByOne.HasFrontFlashLight && i==38))){
					d.add(mPCBAAllItemStrs[i]);
			    	}
				}
			}
			mCitItemsCount = d.size();
			mCitItemStrs = new String[mCitItemsCount];
			mTestResults = new int[mCitItemsCount];
			mCitItemStrs = d.toArray(mCitItemStrs);
			d.clear();
			d = null;
		}
		
		for(int i = 0; i <  mCitItemsCount; i++){
			mTestItemBtns[i].setText(mCitItemStrs[i]);
			mTestItemBtns[i].setOnClickListener(this);
		}
		for(int i = mCitItemsCount; i <  mTestItemBtns.length; i++){
			mTestItemBtns[i].setVisibility(View.GONE);
		}
	}
	
	private void autoTest(){
		if(!CITOneByOne.NoGps && !(mTestResults [getTestId(mPCBAAllItemStrs[27])] == 1)){
			gpsTest(true);
		}
		if(!(mTestResults [getTestId(mPCBAAllItemStrs[21])] == 1)){
			SIMOrUIMCardTest(true);
		}
		if(!(mTestResults [getTestId(mPCBAAllItemStrs[24])] == 1)){
			mAutoBatteryTesting = true;
			chargeTest();
		}
		if(!CITOneByOne.NoWifi && !(mTestResults [getTestId(mPCBAAllItemStrs[22])] == 1)){
			wifiTest(true);
		}
		if(!CITOneByOne.NoBt && !(mTestResults [getTestId(mPCBAAllItemStrs[23])] == 1)){
			bluetoothTest(true);
		}
		if(!CITOneByOne.NoLightsensor && !(mTestResults [getTestId(mPCBAAllItemStrs[25])] == 1)){
			lightSensorTest(true);
		}
		if(!CITOneByOne.NoDistancesensor && !(mTestResults [getTestId(mPCBAAllItemStrs[26])] == 1)){
			proximitySensorTest(true);
		}
		if(CITOneByOne.HasCompass && !(mTestResults [getTestId(mPCBAAllItemStrs[17])] == 1)){
			compassTest(true);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		switch (keyCode) {
//		case KeyEvent.KEYCODE_HALL_OPEN:
//			mHallOpen = true;
//			break;
//		case KeyEvent.KEYCODE_HALL_CLOSE:
//			if(mHallOpen){
//				mClickId = getTestId(mPCBAAllItemStrs[16]);
//				updateBtnState(true);
//				mHallOpen = false;
//			}
//			break;
//		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void initData(){
		mSharedPreferences = this.getSharedPreferences("pcba_test_data", Context.MODE_PRIVATE); 
		//		+ Context.MODE_WORLD_WRITEABLE + Context.MODE_MULTI_PROCESS);
		if(mSharedPreferences == null){
			try {
				mSharedPreferences = this.createPackageContext("com.lovderam.cit",
						Context.CONTEXT_IGNORE_SECURITY).getSharedPreferences(
						"pcba_test_data", Context.MODE_PRIVATE); 
    					//+ Context.MODE_WORLD_WRITEABLE + Context.MODE_MULTI_PROCESS);
			} catch (Exception e) {
				Log.e("PCBATest", "createPackage failed!");
			}
		}
		for (int i = 0; i < mCitItemsCount; i++){
			mTestResults [i] = mSharedPreferences.getInt(i+"", -1);
			if(mTestResults [i] == 1){
				mTestItemBtns[i].setTextColor(android.graphics.Color.GREEN);
			}else if(mTestResults [i] == 0){
				mTestItemBtns[i].setTextColor(android.graphics.Color.RED);
			}
		}
		
		try{
			if(is4Gpass){
				mTestResults[get4GIndext()]=1;
			}else{
				mTestResults[get4GIndext()]=0;
			}
		 }catch(Exception e){
		 }

		int j = 0;
		for( ; j < mCitItemsCount;j ++){
			if(mTestResults[j] != 1){
				break;
			}
		}
		if(j == mCitItemsCount){
			mSuccessBtn.setEnabled(true);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == mSuccessBtn){
			rewriteOneByteOfNv2499(4,'P');
			Intent newIntent = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);
			newIntent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
			newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(newIntent);
		}else{
			mClickId = v.getId();
			CommonTest(((Button)v).getText().toString());
		}
	}
	
	private void rewriteOneByteOfNv2499(int idx, char testResult) {
		byte[] bresult = null;
		bresult = SystemUtil.getNvFactoryData3IByte();
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
	
	private void CommonTest(String strAct) {
		int i = 0;
		String ItemStr[] = getResources().getStringArray(R.array.CommonTestStrings);
		for (i = 0; i < ItemStr.length; i++) {
			if (strAct.equals(ItemStr[i])) {
				break;
			}
		}
		Intent intent = new Intent();
		android.util.Log.i("....zzj","-------i---------"+i);
		switch (i) {
		case 0:
			intent.setClass(this, SpeakAndStorageTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 1:
			mIsShowConfirm = true;
			SIMOrUIMCardTest(false);
			break;
		case 2:
			cameraTest();
			break;
		case 3:
			mAutoBatteryTesting = false;
			mChartBtnClick = true;
			chargeTest();
			break;
		case 4:
			powerTest();
			break;
		case 5:
			vibrationTest();
			break;
		case 6:
			intent.setClassName("com.android.soundrecorder", "com.android.soundrecorder.Recerver");
			intent.putExtra("com.android.soundrecorder.CIT","lovdream.android.cit.testsoundrecorderRecerver");
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 7:
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
		case 10:
			wifiTest(false);
			break;
		case 11:
			bluetoothTest(false);
			break;
//		case 12:
//			intent.setClass(this, LCDTest.class);
//			startActivity(intent);
//			break;
//		case 13:
//			intent.setClass(this, TouchTest3.class);
//			startActivityForResult(intent, REQUEST_ASK);
//			break;
//		case 14:
//			temperatureTest();
//			break;
		case 15:
			gpsTest(false);
			break;
		case 16:
			intent.setClass(this, FMTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 17:
			lightSensorTest(false);
			break;
		case 18:
			proximitySensorTest(false);
			break;
//		case 19:
//			intent.setClassName("com.android.settings","com.android.settings.MasterClear");
//			startActivity(intent);
//			break;
		case 20:
			mIsShowConfirm = true;
			intent.setClass(this, Version.class);
			startActivity(intent);
			break;
//		case 21:
//			intent.setClass(this, BacklightTest.class);
//			startActivityForResult(intent, REQUEST_ASK);
//			break;
		case 22:
			intent.setClass(this, ButtonLights.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
//		case 23:
//			intent.setClass(this, TouchProximitySenor.class);
//			startActivityForResult(intent, REQUEST_ASK);
//			break;
		case 24:
			intent.setClass(this, FlashTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
//		case 25:
//			intent.setClass(this, RebootTest.class);
//			startActivity(intent);
//			break;
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
//		case 29:
//			intent.setClass(this,PCBANfc.class);
//			startActivityForResult(intent, REQUEST_ASK);
//			break;
//		case 30:
//			intent.setClass(this, TouchTest2.class);
//			startActivity(intent);
//			break;
//		case 31:
//			intent.setClass(this, DeviceInfoCit.class);
//			startActivity(intent);
//			break;
//		case 32:
//			tpTest();
//			break;
		case 33:
			intent.setClass(this, HallTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 34:
			intent.setClass(this, AllKeyTestPCBA.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
//		case 35:
//			intent.setClass(this, TestFlag.class);
//			startActivity(intent);
//			break;
		case 36:
			compassTest(false);
			break;
		case 37:
			intent.setClass(this, HeartBeatTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
//		case 38:
//			intent.setClass(this, HotTFTest.class);
//			startActivityForResult(intent, REQUEST_ASK);
//			break;
		case 39:
			intent.setClass(this, OtgTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 40:
			intent.setClassName("com.android.soundrecorder", "com.android.soundrecorder.BackRecerver");
			startActivityForResult(intent, REQUEST_ASK);
			break;
//		case 41:
//			intent.setClass(this, RebootTest.class);
//			startActivity(intent);
//			break;
		case 42:
			intent.setClass(this, BarometerTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 43:
			intent.setClass(this, EightPinTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
//		case 44:
//			resetTest();
//			break;
		case 45:
			intent.setClass(this, EightPinOtgTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 46:
			intent.setClass(this, TwoColorLights.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 47:
			mChartBtnClick =  true;
			mBackChargingTest = true;
			chargeTest();
			break;
		case 49:
				wifi5Gtest();
			break;	
			// add code test by zzj start
	     case 50:
				intent.setClass(this, GPSTest.class);
				intent.putExtra("code", true);
				startActivityForResult(intent, REQUEST_ASK);
				break;
		 // add code test by zzj end
		case 51:
                        intent.setClass(this, EightPinTest.class);
                        startActivityForResult(intent, REQUEST_ASK);
                        break;
        // add side otg test by zzj start
		case 52:
            intent.setClass(this, SideOtgTest.class);
            startActivityForResult(intent, REQUEST_ASK);
            break;
	  // add side otg test by zzj end
		 case 53:
			   intent.setClass(this, FingerPrintTest.class);
	            startActivityForResult(intent, REQUEST_ASK);
			 break;
        	//add wireless charge by zzj start
		case 54:
			mAutoBatteryTesting = false;
			mＷirelessBatteryTesting=true;
			mChartBtnClick = true;
			chargeTest();
			break;
		case 55:
			   intent.setClass(this, ExternalAntennaTest.class);
	            startActivityForResult(intent, REQUEST_ASK);
			break;
		case 56:
			intent.setClass(this, FlashTestFront.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
			//add wireless charge by zzj end
		}
	}
	private void wifi5Gtest() {
		Intent intent = new Intent(this,Wifi5GConnectTest.class);
		startActivityForResult(intent, REQUEST_ASK);
		isWifiAutoTest = false;
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
				if(result == 0){
					updateBtnState(false);
				}else if(result == 1){
					updateBtnState(true);
				}
			}
		}
	}
	
	private void SIMOrUIMCardTest(boolean auto) {
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
		if(!mIsa406)
		{
			switch (stateSim2) {
				case TelephonyManager.SIM_STATE_UNKNOWN:
					s2 = getString(R.string.test_sim_status_0);
					flagSim2=true;
					break;
				case TelephonyManager.SIM_STATE_ABSENT:
					s2 = getString(R.string.test_sim_status_1_1);
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
		}
		
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
		
		if(auto){
			mClickId = getTestId(mPCBAAllItemStrs[21]);
			updateBtnState(success);
			return;
		}
		
		if (mtm.isMultiSimEnabled()) {
			
			AlertDialog.Builder builder ;
			if(mIsa406)
			{
				 builder = (new android.app.AlertDialog.Builder(this))
						.setTitle(R.string.test_item_simoruim).setMessage(s1 /*+"\n"+s2 */);
			}else {
				 builder = (new android.app.AlertDialog.Builder(this))
						.setTitle(R.string.test_item_simoruim).setMessage(s1 +"\n"+s2 );
			}
			
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
	}
	
	private class SucessListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialoginterface, int i) {
			updateBtnState(true);
		}
	}
	
	private class FailListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialoginterface, int i) {
			updateBtnState(false);
		}
	}
	
	private void cameraTest() {
		try {
			Intent intent = new Intent(
					"android.media.action.STILL_IMAGE_CAMERA");
			intent.putExtra("FromCit", true);
			startActivity(intent);
			this.registerReceiver(mCameraReceiver, new IntentFilter("lovdream.android.cit.cameratest"));
		} catch (ActivityNotFoundException exception) {
			Log.d("CIT", "the camera activity is not exist");
		}
	}
	
	private BroadcastReceiver mCameraReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			updateBtnState(true);
			unregisterCameraReceiver();
		}
	};
	
	private void unregisterCameraReceiver(){
		try{
			if(mCameraReceiver != null){
				unregisterReceiver(mCameraReceiver);
			}
		}catch(Exception e){
			Log.e("PCBATest","unregisterCameraReceiver wrong e--------------="+e.toString());
		}
	}
	
	private void chargeTest() {
		mBatteryTesting = true;
		IntentFilter filter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
		registerReceiver(mBatteryInfoReceiver, filter);
	}
	
	private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (mBatteryTesting) {
				showChargeTestInfo(intent, s);
			}
		}
	};
	
	private void showChargeTestInfo(Intent intent, String s) {
		String batteryStatus = "";;
		int i = 0, j = 0, k = 0, current = 0;
		if ("android.intent.action.BATTERY_CHANGED".equals(s)) {
			i = intent.getIntExtra("plugged", 0);
			j = intent.getIntExtra("status", 1);
			k = intent.getIntExtra("temperature", 0);
			if(i > 0){
				current = readFileFile("/sys/class/power_supply/battery/current_now");
			}else{
				current = 0;
			}
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
			Toast warning = Toast.makeText(this,getString(R.string.battery_info_status_notcharging),Toast.LENGTH_LONG);
			warning.setGravity(Gravity.CENTER, 0, 0);
			warning.show();
		} else {
			String s1 = batteryStatus;
                        //modify backCharging show by zzj start
                      if(!mBackChargingTest){
			s1 += "\n";
			s1 += getString(R.string.charge_current) + current;
			if(current <= 50){
				s1 += "("+getString(R.string.abnormal_value)+")";
			}		
			s1 += "\n\n";
			if (current <= 50) {
				s1 += getString(R.string.fail);
			}else{
				s1 += getString(R.string.success);
			}
                      }
                        //modify backCharging show by zzj end
			if(mAutoBatteryTesting && !mBackChargingTest){
				if(j == 2){
					mClickId = getTestId(mPCBAAllItemStrs[24]);
					updateBtnState(true);
					unregisterBatteryReceiver();
					mAutoBatteryTesting = false;
				}
				return;
			}
			
			if(mChartBtnClick == false){
				return;
			}
			//add wireless charge by zzj start
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setTitle(mBackChargingTest ? R.string.test_item_back_charge : mＷirelessBatteryTesting ? R.string.test_item_wireless_charge : R.string.test_item_charge);
			//add wireless charge by zzj end
			builder.setMessage(s1);
			builder.setPositiveButton(R.string.success, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					updateBtnState(true);
					unregisterBatteryReceiver();
				}

			}).setNegativeButton(R.string.fail, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					updateBtnState(false);
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
			mChartBtnClick =  false;
		}
	}
	
	private void unregisterBatteryReceiver(){
		try{
			unregisterReceiver(mBatteryInfoReceiver);
		}catch(Exception e){
			Log.e("PCBATest","unregisterBatteryReceiver wrong e--------------="+e.toString());
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
        return ret>0?ret:-ret;
	}  
	
	private void powerTest() {
		AlertDialog.Builder builder = (new AlertDialog.Builder(this)).setTitle(
				R.string.test_item_power).setMessage(R.string.power_message);
		builder.setPositiveButton(R.string.success, new SucessListener())
				.setNegativeButton(R.string.fail, new FailListener()).create().show();
	}
	
	private void vibrationTest() {
		mVibrator = (Vibrator) getSystemService("vibrator");
		long al[] = { 0,1000L,1000l, 1000l,1000l };
		mVibrator.vibrate(al, 0);
		AlertDialog.Builder builder = (new AlertDialog.Builder(this)).setTitle(
				R.string.test_item_vibration).setMessage(
				R.string.vibration_message);
		mDialog = builder.setPositiveButton(R.string.success, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mVibrator.cancel();
				updateBtnState(true);
			}
			
		}).setNegativeButton(R.string.fail, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mVibrator.cancel();
				updateBtnState(false);
			}
		}).create();
		mDialog.show();
	}
	
	private WifiManager mWifiManager; 
    private WifiInfo mWifiInfo; 
    private List<ScanResult> mWifiList; 
    private String mSSID1 = "lovdream";
    private String mPassWrod1 = "";
    private int mNetworkId = -1;
    private WifiManager.ActionListener mForgetListener;
	boolean isWifiAutoTest = false;
	
	private void wifiTest(boolean auto) {
		if(auto){
			isWifiAutoTest = true;
			initWifi();
		}else{
			Intent intent = new Intent(this,WifiConnectTest.class);
			startActivityForResult(intent, REQUEST_ASK);
		}
	}
	
	private void initWifi(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerReceiver(mWifiConnectReceiver, filter);
		mForgetListener = new WifiManager.ActionListener() {
	         @Override
	         public void onSuccess() {
	         }
	         @Override
	         public void onFailure(int reason) {
	        	 android.util.Log.e("PCBATest","fail to forget network -- "+reason);
	         }
	     };
       mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE); 
       if(!mWifiManager.isWifiEnabled()){
    	   mWifiManager.setWifiEnabled(true);
       }
       doAfterScanDone();
	}
	
	private void doAfterScanDone(){
		mWifiInfo = mWifiManager.getConnectionInfo();
	       if(mWifiInfo.getSSID().contains(mSSID1)){
	    	   mNetworkId = mWifiInfo.getNetworkId();
	    	   if(mNetworkId != -1){
	    		   return;
	    	   }
	       }
		mWifiList = mWifiManager.getScanResults(); 
		int i, count = mWifiList.size();
		if(count == 0){
			mWifiManager.startScan();
			return;
		}
		for (i = 0; i < count; i++) {
			if (mWifiList.get(i).SSID.equals(mSSID1)) {
				addNetwork(CreateWifiInfo(mSSID1, mPassWrod1, 1));
				break;
			}
		}
		if ( count != 0 && i == count) {
//			mClickId = getTestId(mPCBAAllItemStrs[22]);
//			updateBtnState(false);
		}
	}
	
	private BroadcastReceiver mWifiConnectReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
	            Parcelable parcelableExtra = intent
	                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
	            if (null != parcelableExtra) {
	                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
	                State state = networkInfo.getState();
	                boolean isConnected = state == State.CONNECTED;
	                if (isConnected) {
						if(isWifiAutoTest){
	                		mClickId = getTestId(mPCBAAllItemStrs[22]);
							isWifiAutoTest = false;
						}
	        			updateBtnState(true);
	        			if(mNetworkId != -1){
	        				mWifiManager.forget(mNetworkId, mForgetListener);
	        			}
	        			if(mWifiConnectReceiver != null){
	        				unregisterReceiver(mWifiConnectReceiver);
	        			}
	        			if(mWifiManager.isWifiEnabled()){
	        		    	   mWifiManager.setWifiEnabled(false);
	        		       }
	                } 
	            }
	        }else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
	        	doAfterScanDone();
	        }
		}
	};
	
   public void addNetwork(WifiConfiguration wcg) { 
	     mNetworkId = mWifiManager.addNetwork(wcg); 
	     boolean enable =  mWifiManager.enableNetwork(mNetworkId, true); 
	     if(!enable){
//	    	 mClickId = getTestId(mPCBAAllItemStrs[22]);
// 			updateBtnState(false);
	     }
    } 
 
	public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) 
    { 
          WifiConfiguration config = new WifiConfiguration();   
           config.allowedAuthAlgorithms.clear(); 
           config.allowedGroupCiphers.clear(); 
           config.allowedKeyManagement.clear(); 
           config.allowedPairwiseCiphers.clear(); 
           config.allowedProtocols.clear(); 
          config.SSID = "\"" + SSID + "\"";   
          
          if(Type == 1) //WIFICIPHER_NOPASS
          { 
               config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); 
          } 
          if(Type == 2) //WIFICIPHER_WEP
          { 
              config.hiddenSSID = true;
              config.wepKeys[0]= "\""+Password+"\""; 
              config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED); 
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP); 
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP); 
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40); 
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104); 
              config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); 
              config.wepTxKeyIndex = 0; 
          } 
          if(Type == 3) //WIFICIPHER_WPA
          { 
          config.preSharedKey = "\""+Password+"\""; 
          config.hiddenSSID = true;   
          config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);   
          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);                         
          config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);                         
          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);                    
          //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);  
          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
          config.status = WifiConfiguration.Status.ENABLED;   
          }
           return config; 
    }
	
	private void bluetoothTest(boolean auto) {
		if(auto){
			initBt();
		}else{
			Intent intent = new Intent(this,BluetoothTestNew.class);
			startActivityForResult(intent, REQUEST_ASK);
		}
	}
	
	BluetoothAdapter mBluetoothAdapter = null;
	List<DeviceInfo> mDeviceList = new ArrayList<DeviceInfo>();
	Set<BluetoothDevice> bondedDevices;
	 IntentFilter filter = null;
	 private final static int MIN_COUNT = 1;
	private void initBt(){
		BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager == null) {
//        	mClickId = getTestId(mPCBAAllItemStrs[23]);
// 			updateBtnState(false);
 			return;
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
//        	mClickId = getTestId(mPCBAAllItemStrs[23]);
// 			updateBtnState(false);
 			return;
        }
        startScanAndUpdateAdapter();
        if(mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON){
			scanDevices();
		} else{
			if(mBluetoothAdapter.getState() != BluetoothAdapter.STATE_TURNING_ON){
				mBluetoothAdapter.enable();
			}
		}

		filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(mReceiver,filter);
	}
	
	private void startScanAndUpdateAdapter(){
		mDeviceList.clear();
		bondedDevices = mBluetoothAdapter.getBondedDevices();
		for(BluetoothDevice device : bondedDevices){
			DeviceInfo deviceInfo = new DeviceInfo(device.getName(),device.getAddress());
			mDeviceList.add(deviceInfo);
		}
	}
	
	private void scanDevices(){
		if(mBluetoothAdapter.isDiscovering()){
			mBluetoothAdapter.cancelDiscovery();
		}
		mBluetoothAdapter.startDiscovery();
	}
	
	BroadcastReceiver mReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent){
			String action = intent.getAction();
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if(BluetoothDevice.ACTION_FOUND.equals(action)){
				mDeviceList.add(new DeviceInfo(device.getName(), device.getAddress()));
				if(mDeviceList.size() >= MIN_COUNT){
					mClickId = getTestId(mPCBAAllItemStrs[23]);
		 			updateBtnState(true);
		 			if(mReceiver != null){
		 				unregisterReceiver(mReceiver);
		 			}
		 			if (mBluetoothAdapter != null){
		 				mBluetoothAdapter.disable();
		 			}
				}
			} else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
				setProgressBarIndeterminateVisibility(false);
				if(mDeviceList.size() >= MIN_COUNT){
					mClickId = getTestId(mPCBAAllItemStrs[23]);
		 			updateBtnState(true);
		 			if(mReceiver != null){
		 				unregisterReceiver(mReceiver);
		 			}
		 			if (mBluetoothAdapter != null){
		 				mBluetoothAdapter.disable();
		 			}
				}
			} else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
				startScanAndUpdateAdapter();
			} else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
				if(BluetoothAdapter.STATE_ON == intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)){
					scanDevices();
					if(BluetoothAdapter.STATE_OFF == intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)){
						mBluetoothAdapter.enable();
					}
				}
			}
		}
	};
	
	class DeviceInfo{
		private String name="";
		private String address="";

		public DeviceInfo(String name, String address){
			super();
			this.name = name;
			this.address = address;
		}
		
		public String getName(){
			return name;
		}

		public void setName(String name){
			this.name = name;
		}

		public String getAddress(){
			return address;
		}

		public void setAddress(String address){
			this.address = address;
		}
	}
	
	private void gpsTest(boolean auto){
		if(auto){
			initGps();
		}else{
			Intent intent = new Intent(this,GPSTest.class);
			startActivityForResult(intent, REQUEST_ASK);
		}
	}
	
	private LocationManager mLocationManager;
	private GpsStatus.Listener mStatusListener = new GpsStatusListner();
	private final LocationListener mLocationListener = new LocaltionLis();
	private void initGps(){
		mLocationManager = (LocationManager) getSystemService("location");
		if (!mLocationManager.isProviderEnabled("gps")) {
			openOrCloseGps(true);
		}
		mLocationManager.requestLocationUpdates("gps", 1000L, 1F, mLocationListener);
		mLocationManager.addGpsStatusListener(mStatusListener);
	}
	
	private void openOrCloseGps(boolean state) {
		android.provider.Settings.Secure.setLocationProviderEnabled(getContentResolver(), "gps", state);
	}
	
	private class LocaltionLis implements LocationListener {
		public void onLocationChanged(Location location) {
		}
		public void onProviderDisabled(String s) {
		}
		public void onProviderEnabled(String s) {
		}
		public void onStatusChanged(String s, int i, Bundle bundle) {
		}
	}
	
	private class GpsStatusListner implements GpsStatus.Listener {
		@Override
		public void onGpsStatusChanged(int status) {
			// TODO Auto-generated method stub
			GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
			switch (status) {
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				mClickId = getTestId(mPCBAAllItemStrs[27]);
	 			updateBtnState(true);
	 			openOrCloseGps(false);
	 			mLocationManager.removeUpdates(mLocationListener);
	 			mLocationManager.removeGpsStatusListener(mStatusListener);
				break;
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				Iterator iterator = gpsStatus.getSatellites().iterator();
				int k = 0;
				for (;iterator.hasNext();) {
					k++;
					if(k > 3){
						mClickId = getTestId(mPCBAAllItemStrs[27]);
			 			updateBtnState(true);
			 			mLocationManager.removeUpdates(mLocationListener);
			 			mLocationManager.removeGpsStatusListener(mStatusListener);
			 			openOrCloseGps(false);
			 			break;
					}
				}
				break;
			}
		}
	}
	
	private void lightSensorTest(boolean auto){
		if(auto){
			initLightSensor();
		}else{
			Intent intent = new Intent(this,LightSenor.class);
			startActivityForResult(intent, REQUEST_ASK);
		}
	}
	
	private SensorManager mSensorManager;
	private Sensor mLightSensor;
	private final SensorEventListener mLightSensorListener = new SensorListener();
	private void initLightSensor(){
		mSensorManager = (SensorManager) getSystemService("sensor");
		mLightSensor = mSensorManager.getDefaultSensor(5);
		mSensorManager.registerListener(mLightSensorListener,mLightSensor, 500000);
	}
	
	class SensorListener implements SensorEventListener{
		public void onAccuracyChanged(Sensor sensor, int i) {
		}
		public void onSensorChanged(SensorEvent sensorevent) {
			float f = sensorevent.values[0];
			if(f  > 1000){
				mClickId = getTestId(mPCBAAllItemStrs[25]);
	 			updateBtnState(true);
	 			mSensorManager.unregisterListener(mLightSensorListener);
			}
		}
	}
	
	private void proximitySensorTest(boolean auto){
		if(auto){
			initProximitySenor();
		}else{
			Intent intent = new Intent(this,ProximitySenor.class);
			startActivityForResult(intent, REQUEST_ASK);
		}
	}
	
	private SensorEventListener mProximityListener = new SenListener();
	private Sensor mProimitySensor;
	private void initProximitySenor(){
		if(mSensorManager == null){
			mSensorManager = (SensorManager) getSystemService("sensor");
		}
		mProimitySensor = mSensorManager.getDefaultSensor(8);
		mSensorManager.registerListener(mProximityListener, mProimitySensor, 100000);
	}
	
	private class SenListener implements SensorEventListener{
		public void onAccuracyChanged(Sensor sensor, int i) {
		}
		public void onSensorChanged(SensorEvent sensorevent) {
			if (sensorevent.values[0] < 5F) {
				mClickId = getTestId(mPCBAAllItemStrs[26]);
	 			updateBtnState(true);
	 			mSensorManager.unregisterListener(mProximityListener);
			}
		}
	}
	
	private void compassTest(boolean auto){
		if(auto){
			initCompass();
		}else{
			Intent intent = new Intent(this,CompassTest.class);
			startActivityForResult(intent, REQUEST_ASK);
		}
	}
	
	private Sensor mOrieSensor;
	private SensorEventListener mOrieSensorListener = new OrieSensorListener();
	private void initCompass(){
		if(mSensorManager == null){
			mSensorManager = (SensorManager) getSystemService("sensor");
		}
		mOrieSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		mSensorManager.registerListener(mOrieSensorListener, mOrieSensor,SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	private class OrieSensorListener implements SensorEventListener{

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			if (Math.abs(event.values[0]) > 1) {
				mClickId = getTestId(mPCBAAllItemStrs[17]);
	 			updateBtnState(true);
	 			mSensorManager.unregisterListener(mOrieSensorListener);
			}
		}
	}
	
	private int getTestId(String item){
		int id = 0;
		for(int i = 0; i < mCitItemsCount; i ++){
			if(mCitItemStrs[i].equals(item)){
				id = i;
				break;
			}
		}
		return id;
	}
	
	private void updateBtnState(boolean success){
		android.util.Log.e("...wt","mCitItemStrs["+mClickId+"]--------------2-------------="+mCitItemStrs[mClickId]);
		android.util.Log.e("...wt","success---------------------------="+success);
		if(success){
			mTestItemBtns[mClickId].setTextColor(android.graphics.Color.GREEN);
		}else{
			mTestItemBtns[mClickId].setTextColor(android.graphics.Color.RED);
		}
		mTestResults[mClickId] = success ? 1 : 0;
		try{
			if(is4Gpass){
				mTestResults[get4GIndext()]=1;
			}else{
				mTestResults[get4GIndext()]=0;
			}
		 }catch(Exception e){
		 }

		saveDate(mClickId, success);
		int i = 0;
		for( ; i < mCitItemsCount; i ++){
			if(mTestResults[i] != 1){
				break;
			}
		}
		if(i == mCitItemsCount){
			mSuccessBtn.setEnabled(true);
		}
	}
	
	private void saveDate(int key,boolean value){
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putInt(key+"", value ? 1 : 0);
		editor.commit();
	}
	
	private void cleanTestState(){
		for(int i = 0; i < mCitItemsCount; i ++){
			SharedPreferences.Editor editor = mSharedPreferences.edit();
			editor.putInt(i+"", -1);
			editor.commit();
			mTestItemBtns[i].setTextColor(android.graphics.Color.BLACK);
		}
		//rewriteOneByteOfNv2499(4,'U');
		mSuccessBtn.setEnabled(false);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(P800 || mIsS550)
		{
			Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 1);
		}
	}
}
