
package com.lovdream.factorykit;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.content.Context;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.StatusBarManager;
import android.app.ActivityManager;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.os.SystemProperties;

import com.lovdream.factorykit.Utils;
import com.swfp.utils.ProjectControlUtil;

import java.util.List;

import com.lovdream.factorykit.items.SystemVersionTest;
import android.preference.PreferenceScreen;

public class Main extends PreferenceActivity {

	public static final String TAG = "factorykit";

	Handler mHandler = new Handler();
	private StatusBarManager mSbManager;
	private boolean mIsLocationProviderEnabled;
	private boolean mIsWifiEnable;
	private boolean mIsBluetoothEnable;
	private boolean mIsNfcEnable;

	@Override
	protected void onCreate(Bundle bundle){
		super.onCreate(bundle);

		if (ActivityManager.isUserAMonkey()) {
			Log.e(TAG, "user is a monkey");
			finish();
			return;
		}

		try {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "e.getMessage()--->"+e.getMessage());
		}

		
		FactoryKitApplication app = (FactoryKitApplication) getApplication();
		if (app.getTestConfig().getTestItems().size() <= 0) {
			Toast.makeText(this, R.string.load_config_error, Toast.LENGTH_SHORT)
					.show();
			finish();
		}

		mSbManager = (StatusBarManager) getSystemService(Context.STATUS_BAR_SERVICE);

		addPreferencesFromResource(R.xml.main_list);

		if (!ProjectControlUtil.isC802) {
			PreferenceScreen mainScreen = (PreferenceScreen) findPreference("main");
			mainScreen.removePreference(findPreference("test_usb"));
		}
		if (SystemProperties.getBoolean(Utils.PROP_DEBUG_ABLE, false)) {
			Utils.createShortcut(this, Main.class);
			SystemProperties.set(Utils.PROP_DEBUG_ABLE, String.valueOf(false));
		}
		if (SystemProperties.getBoolean(CrashHandler.CRASH_PROP, false)) {
			Toast.makeText(this,
					getString(R.string.crash_mesg, CrashHandler.TRACE_FILE),
					Toast.LENGTH_LONG).show();
			SystemProperties
					.set(CrashHandler.CRASH_PROP, String.valueOf(false));
		}
		mIsLocationProviderEnabled = Utils.isLocationProviderEnabled(this);// bug
																			// 15688
		mIsBluetoothEnable = Utils.isBluetoothEnable(this);
		mIsNfcEnable = Utils.isNfcEnable(this);
		mIsWifiEnable = Utils.isWifiEnable(this);
		// To save test time, enable some devices first
		Utils.enableWifi(this, true);
		Utils.enableBluetooth(true);
		Utils.enableGps(this, true);
		Utils.enableNfc(this, true);
		// Utils.enableCharging(true);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (!mIsWifiEnable) Utils.enableWifi(this, false);
		if (!mIsBluetoothEnable) Utils.enableBluetooth(false);
		Utils.enableGps(this, mIsLocationProviderEnabled);//bug 15688 15689
		if (!mIsNfcEnable)Utils.enableNfc(this, false);
	}

	@Override
	public void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		Fragment intentHandler = getFragmentManager().findFragmentById(android.R.id.content);
		if(intentHandler instanceof FragmentNewIntentHandler){
			((FragmentNewIntentHandler)intentHandler).onNewIntent(intent);
		}
		String type = intent.getStringExtra("test_type");
		Fragment fragment = null;
		if("single".equals(type)){
			fragment = Fragment.instantiate(this,SingleTest.class.getName());
		}else if("auto".equals(type)){
			if(!Utils.isSdMounted(this) || !Utils.isSimReady()){
				showWarningDialog();
				return;
			}
			fragment = Fragment.instantiate(this,AutoTest.class.getName());
		}else if("pcba".equals(type)){
			fragment = Fragment.instantiate(this,PCBATest.class.getName());
		}else if("pcba2".equals(type)){
			fragment = Fragment.instantiate(this,BackTest.class.getName());
		}else if("small".equals(type)){
			fragment = Fragment.instantiate(this,SmallPCB.class.getName());
		}else if("result".equals(type)){
			fragment = Fragment.instantiate(this,TestResult.class.getName());
		}else if("usb".equals(type)){
			fragment = Fragment.instantiate(this,UsbTest.class.getName());
		}else if("version".equals(type)){
			fragment = Fragment.instantiate(this,SystemVersionTest.class.getName());
		}

		if(fragment == null){
			return;
		}
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(android.R.id.content,fragment,type);
		ft.addToBackStack(type);
		ft.commit();
	}

	private void showWarningDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.no_card);
		builder.setPositiveButton(android.R.string.ok,null);
		builder.show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event){
		Fragment fragment = getFragmentManager().findFragmentById(android.R.id.content);
		if((fragment instanceof FragmentKeyHandler) && ((FragmentKeyHandler)fragment).onKeyDown(keyCode,event)){
			return true;
		}
		return keyCode == KeyEvent.KEYCODE_MENU ? true : super.onKeyDown(keyCode,event);
	}

	@Override
	public boolean onKeyUp(int keyCode,KeyEvent event){
		Fragment fragment = getFragmentManager().findFragmentById(android.R.id.content);
		if((fragment instanceof FragmentKeyHandler) && ((FragmentKeyHandler)fragment).onKeyUp(keyCode,event)){
			return true;
		}
		return keyCode == KeyEvent.KEYCODE_MENU ? true : super.onKeyUp(keyCode,event);
	}

	
	//9.0之后此方法不可重写,否则导致界面崩溃,具体原因有待研究;
	/*@Override
		public void onAttachedToWindow() {
			try {
				getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
			} catch (Exception e) {
				e.printStackTrace();
				Log.d(TAG, "e.onAttachedToWindow()--->"+e.getMessage());
			}
			super.onAttachedToWindow();
		}*/

	@Override
	public void onResume(){
		super.onResume();
		mSbManager.disable(StatusBarManager.DISABLE_EXPAND);
	}

	@Override
	public void onPause(){
		super.onPause();
		mSbManager.disable(StatusBarManager.DISABLE_NONE);
	}

	private long exitTime;

	@Override
	public void onBackPressed(){

		if(getFragmentManager().popBackStackImmediate()){
			return;
		}
		if((System.currentTimeMillis() - exitTime) < 1000){
			super.onBackPressed();
		}else{
			exitTime = System.currentTimeMillis();
			Toast.makeText(this,R.string.double_back_msg,Toast.LENGTH_SHORT).show();
		}
	}
}
