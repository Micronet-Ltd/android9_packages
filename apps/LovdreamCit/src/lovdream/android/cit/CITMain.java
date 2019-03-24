package lovdream.android.cit;

import lovdream.android.cit.CameraHolder.CameraHardwareException;
import lovdream.android.cit.GPSTest.GpsInfo;

import android.bluetooth.BluetoothAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import android.net.wifi.WifiManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera.CameraInfo;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.SystemProperties;
import com.lovdream.util.SystemUtil;


public class CITMain extends ListActivity {

	private ArrayList<String> Items = new ArrayList<String>();

	public static final int MONO_TEST = 0;
	public static final int MACHINE_AUTO_TEST = 1;
	public static final int PCBA_AUTO_TEST = 2;
	public static final int MACHINE_AUTO_TEST_2 = 3;
	public static final int FASTMMI_TEST = 4;
	public static final int DEPUTY_BOARD_TEST = 5;
	public static int CITtype = -1;

	public static final String CIT_TYPE = "CIT type";
	public static final String CIT_ITEM_IDX = "CIT idex";
	public static final String CIT_ITEM_CNT = "CIT count";

	private String as[];
	private LocationManager m_mgr;
	private LocationListener locationListener;
	private PowerManager mPowerManager;
	private WakeLock mWakeLock;
	private WifiManager wifimanager;
	private static final String CAMERA_LAUNCHER_NAME = "com.android.camera.CameraLauncher";

	
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
	public static boolean NoFlashLight = SystemProperties.get("ro.product.noflashlight","0").equals("1");
	public static boolean HasButtonLight = SystemProperties.get("ro.product.hasbuttonlight","0").equals("1");
	public static boolean UseInter = SystemProperties.get("persist.sys.emmcsdcard.enabled","0").equals("1");
    private final static boolean mIsEvdo = SystemProperties.get("ro.product.plateform").equals("EVDO");
	private final static boolean mIsEvdoGsm = SystemProperties.get("ro.product.plateform").equals("EVDOGSM");
	private final static boolean mIsWcdmaGsm = SystemProperties.get("ro.product.plateform").equals("WCDMAGSM");
	public static boolean mSaveToNv2499 = false;//SystemProperties.get("ro.product.saveCitToNv2499","0").equals("1");

	public static final boolean mIsA500 = SystemProperties.get("ro.product.name").equals("msm8916_a500");
	public static final boolean mIsS500 = SystemProperties.get("ro.product.name").equals("msm8916_64_s500");
	public static final boolean mIsA470 = SystemProperties.get("ro.product.name").equals("msm8916_64_a470");
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		boolean cameraReady = true;
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		AudioManager audiomanager = (AudioManager) getSystemService(AUDIO_SERVICE);
		StringBuilder errorString = new StringBuilder();
		if (!HasSIMOrUIMCard()) {
			if (mIsEvdo) {
				errorString.append(getString(R.string.please_insert_uim_card)+"\n");
			}else if (mIsEvdoGsm) {
				errorString.append(getString(R.string.please_insert_uim_sim_card)+"\n");
			}else if (mIsWcdmaGsm) {
				errorString.append(getString(R.string.please_insert_sim_card)+"\n");
			}else {
				errorString.append(getString(R.string.please_insert_sim_card)+"\n");
			}
		}
		if (!getSDState().equals(Environment.MEDIA_MOUNTED)) {
			errorString.append(getString(R.string.please_insert_sdcard)+"\n");
		}
//		if (!audiomanager.isWiredHeadsetOn()) {
//			errorString.append(getString(R.string.please_insert_headset)+"\n");
//		}
//		CameraHolder cameraHolder= new CameraHolder();
//		if (cameraHolder.mNumberOfCameras == 0) {
//			errorString.append(getString(R.string.back_camear_is_out));
//		}
//		Log.d("CJ", "cameraHolder.mNumberOfCameras="+cameraHolder.mNumberOfCameras);
//		for (int i = 0; i < cameraHolder.mNumberOfCameras; i++) {
//			boolean openSucess = true;
//			try {
//				openSucess = true;
//				cameraHolder.open(i);
//			} catch (Exception e) {
//				// TODO: handle exception
//				cameraReady = false;
//				openSucess = false;
//				if (i == 0) {
//					errorString.append(getString(R.string.back_camear_is_out)+"\n");
//				}
//				if (i == 1) {
//					errorString.append(getString(R.string.front_camear_is_out)+"\n");
//				}
//			}
//			if (cameraHolder!=null && openSucess) {
//				cameraHolder.release();
//			}
//	    }
//		if (UseInter) {
//			errorString.append(getString(R.string.use_outer));
//		}
		
		if (!isCameraAvailable()) {
			errorString.append(getString(R.string.back_camear_is_out)+"\n");
		}
		
		switch (position) {
		case 0:
//			if ((!mIsA500 &&(!HasSIMOrUIMCard() || !getSDState().equals(Environment.MEDIA_MOUNTED)))
//					|| (mIsA500 && (!HasSIMOrUIMCard()))
//					/*|| !audiomanager.isWiredHeadsetOn()*/ || !cameraReady) {
//				b.setTitle(getText(R.string.lack_of_necessary_equipment))
//				.setMessage(errorString.toString())
//	            .setPositiveButton("OK", null)
//	            .show();
//			}else {
				CITtype = PCBA_AUTO_TEST;
				intent.setClass(this, PCBATest.class);
				startActivity(intent);	
//			}
			break;
		case 1:
//			if ((!mIsA500 &&(!HasSIMOrUIMCard() || !getSDState().equals(Environment.MEDIA_MOUNTED)))
//					|| (mIsA500 && (!HasSIMOrUIMCard()))
//					/*|| !audiomanager.isWiredHeadsetOn()*/ || !cameraReady) {
//				b.setTitle(getText(R.string.lack_of_necessary_equipment))
//				.setMessage(errorString.toString())
//	            .setPositiveButton("OK", null)
//	            .show();
//			}else {
				CITtype = MACHINE_AUTO_TEST;
				intent.setClass(this, CITAuto.class);
				startActivity(intent);
//			}
			break;
		case 2:
			CITtype = -1;
			intent.setClass(this, CITOneByOne.class);
			startActivity(intent);
			break;
		case 3:
			CITtype = DEPUTY_BOARD_TEST;
			intent.setClass(this, DeputyBoardTest.class);
			startActivity(intent);
			break;
		case 4:
			/*CITtype = -1;
			intent.setClass(this, TestResult.class);
			startActivity(intent);
			break;*/
			intent.setClass(this, TestFlag.class);
			startActivity(intent);
		}
		super.onListItemClick(l, v, position, id);
	}
	
	private boolean isCameraAvailable() {
		PackageManager pm = getPackageManager();
		ComponentName name = new ComponentName(this, CAMERA_LAUNCHER_NAME);
		int state = pm.getComponentEnabledSetting(name);
		return (state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) || (state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
	}
	
	private String getSDPath() {
        /*String sd = null;
        StorageManager mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        StorageVolume[] volumes = mStorageManager.getVolumeList();
        for (int i = 0; i < volumes.length; i++) {
            if (volumes[i].isRemovable() && volumes[i].allowMassStorage()) {
                sd = volumes[i].getPath();
            }
        }*/
        return "/storage/sdcard1";
    }

    private String getSDState() {
        StorageManager mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        return mStorageManager.getVolumeState(getSDPath());
    }

	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.main);
		Intent openBluetooth = new Intent("android.intent.CIT.openBluetooth");
		sendBroadcast(openBluetooth);
		wifimanager=(WifiManager)getSystemService(Context.WIFI_SERVICE);
		wifimanager.setWifiEnabled(true);
		wifimanager.startScan();
		getListItems();
		if (mIsMsm7627a_d7_e86) {
			if (!NoGps) {
				m_mgr = (LocationManager) getSystemService("location");
				locationListener = new LocaltionLis();
//				statusListener = new GpsStatusListner();
//				nmeaListener = new NmeaLis();
				android.provider.Settings.Secure.setLocationProviderEnabled(
						getContentResolver(), "gps", true);
				m_mgr.requestLocationUpdates("gps", 1000L, 1F, locationListener);
//				m_mgr.addGpsStatusListener(statusListener);
//				m_mgr.addNmeaListener(nmeaListener);
				
			}
		}
		mPowerManager = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
		mWakeLock = this.mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mIsMsm7627a_d7_e86) {
			if (!NoGps) {
				android.provider.Settings.Secure.setLocationProviderEnabled(
						getContentResolver(), "gps", false);
				m_mgr.removeUpdates(locationListener);
//				m_mgr.removeGpsStatusListener(statusListener);
//				m_mgr.removeNmeaListener(nmeaListener);
			}
		}
		wifimanager.setWifiEnabled(false);
		Intent closeBluetooth = new Intent("android.intent.CIT.closeBluetooth");
		sendBroadcast(closeBluetooth);
	}

	private void getListItems() {
		as = getResources().getStringArray(R.array.TestTypeStrings);
		int j = 0;
		do {
			if (j < as.length) {
				//if(!(!mIsS500 && j==3)){
					Items.add(as[j]);
				//}
				j++;
			} else {
				setListAdapter(new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, Items));
				getListView().setTextFilterEnabled(true);
				return;
			}
		} while (true);
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
	
	private boolean HasSIMOrUIMCard() {
		boolean flagSim1 = false;
		boolean flagSim2 = false;
		TelephonyManager mtm = TelephonyManager.getDefault();
		int stateSim1 = -1;
		int stateSim2 = -1;
		
		if (mtm.isMultiSimEnabled()) {
			stateSim1 = mtm.getSimState(0);
			stateSim2 = mtm.getSimState(1);
		}else {
			TelephonyManager tm2 = TelephonyManager.getDefault();
			stateSim1 = tm2.getSimState();
		}
		
		switch (stateSim1) {
//		case TelephonyManager.SIM_STATE_UNKNOWN:
//			flagSim1=true;
//			break;
		case TelephonyManager.SIM_STATE_ABSENT:
			flagSim1=true;
			break;
		default:
			break;
		}
		
		switch (stateSim2) {
//		case TelephonyManager.SIM_STATE_UNKNOWN:
//			flagSim2=true;
//			break;
		case TelephonyManager.SIM_STATE_ABSENT:
			flagSim2=true;
			break;
		default:
			break;
		}
		if (mtm.isMultiSimEnabled()) {
			if ((!mIsA500 && !flagSim2 && !flagSim1) || (mIsA500 && !flagSim1 && (!flagSim2 || getSDState().equals(Environment.MEDIA_MOUNTED)))) {
				return true;
			} else {
				return false;
			}
		} else {
			if (!flagSim1) {
				return true;
			}else {
				return false;
			}
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		wifimanager.setWifiEnabled(true);
		mWakeLock.acquire();
		try{
			byte[] bresult = SystemUtil.getNvFactoryData3IByte();
			bresult = SystemUtil.getNvFactoryData3IByte();
			if(bresult != null){
				for(int i = 0; i < bresult.length; i ++){
					Log.e("CITMain","bresult["+i+"]---------------------="+bresult[i]);
				}
			}else{
				Log.e("CITMain","bresult is null");
			}
		}catch(Exception e){
			Log.e("CITMain","getNvFactoryData3IByte exception "+e.toString());
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mWakeLock.release();
	}

}
