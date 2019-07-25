package lovdream.android.cit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;



import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.SystemProperties;
import android.widget.Button;


public class DeputyBoardTest extends ListActivity {

	private String as[];
	private ArrayList<String> Items = new ArrayList<String>();
	AudioManager am;
	private Vibrator vibrator;
	private AlertDialog mDialog = null;
	private SharedPreferences mSharedPreferences;
	private LayoutInflater mInflater;
	private Bitmap PASS_ICON;
	private Bitmap FAIL_ICON;
	private static int mTestPosition = 0;
	private static int [] mResultList = new int [30];
	static final private int MENU_CLEAN_STATE = Menu.FIRST;
	private static boolean mIsItemClick = false;
	private static final int REQUEST_ASK = 1;
	boolean m_bBatteryTesting;
	private BroadcastReceiver mBatteryInfoReceiver;
	String batteryStatus;

	private boolean mIsA420 =android.os. SystemProperties.get("ro.product.name").equals("msm8916_64_a420");
	private boolean S550 = SystemProperties.get("ro.product.name").equals("msm8939_64_s550");
	
	private boolean A406 = SystemProperties.get("ro.product.name").contains("msm8916_64_a406")  ||
            SystemProperties.get("ro.product.name").contains("msm8916_a406")  ;
	boolean P800 = SystemProperties.get("ro.product.name").equals("msm8939_64_p800");
	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.main);
		mInflater = LayoutInflater.from(this);
	    mSharedPreferences = this.getSharedPreferences("deputy_board_data", Context.MODE_PRIVATE );
		if(mSharedPreferences == null){
			try {
				mSharedPreferences = this.createPackageContext("com.lovderam.cit",
						Context.CONTEXT_IGNORE_SECURITY).getSharedPreferences(
						"deputy_board_data", Context.MODE_PRIVATE); 
			} catch (Exception e) {
				Log.e("DeputyBoardTest", "createPackage failed!");
			}
		}
		PASS_ICON = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.test_pass);
		FAIL_ICON = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.test_fail);
		//add otg and headset test by zzj start
		if(P800){
			getP800ListItems();
		}else{
			getListItems();
		}
		//add otg and headset test by zzj end
		am = (AudioManager) getSystemService("audio");
		forceShowOverflowMenu();
		m_bBatteryTesting = false;
		batteryStatus = "";
		mBatteryInfoReceiver = new BatteryReceiver();
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
	
	//add otg and headset test by zzj start
    private void getP800ListItems() {
    	as = getResources().getStringArray(R.array.DeputyBoardTestStrings);
		int j = 0;
		do {
			if (j < as.length) {
				if (!((!CITOneByOne.HasButtonLight && j == 2) || (!CITOneByOne.HasBreathingLight && j == 3)) || j == 4 ) {
					Items.add(as[j]);
				}
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
  //add otg and headset test by zzj end
	private void getListItems() {
		as = getResources().getStringArray(R.array.DeputyBoardTestStrings);
		int j = 0;
		do {

			if (j < as.length) {
				if(mIsA420){
					Items.add(as[j]);
				}else{
				if (!((!CITOneByOne.HasButtonLight && j == 2) || (!CITOneByOne.HasBreathingLight && j == 3)) || j == 4 ) {
					if(!S550 && j==4)
					{
						j++;
						continue;
					}
					
					if(!A406 && j > 4)
					{
						j++;
						continue;
					}
					
					Log.v("Demo321", "S550  = " + S550 + "as[j] = " + as[j] + j + "  ====================");
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		mTestPosition = position;
		
		Log.v("Demo321","mTestPosition = " + mTestPosition);
		
		CommonTest(Items.get(position));
		super.onListItemClick(l, v, position, id);
	}

	private void CommonTest(String strAct/* , int type */) {
		int i = 0;

		String ItemStr[] = getResources().getStringArray(
				R.array.CommonTestStrings);
		for (i = 0; i < ItemStr.length; i++) {
			if (strAct.equals(ItemStr[i])) {
				break;
			}
		}
		
		if(strAct.equals(getResources().getString(R.string.camera_font)))
		{
			i = 2;
		}
		
		Intent intent = new Intent();
		android.util.Log.i("....zzj","-------i-----"+i);
		switch (i) {
		case 3:
			chargeTest();
			break;
		case 6:
			intent.setClassName("com.android.soundrecorder", "com.android.soundrecorder.Recerver");
			startActivityForResult(intent, REQUEST_ASK);
			break;
		//
		case 8:
			intent.setClass(this, KeyTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		//
		case 22:
			intent.setClass(this, ButtonLights.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 26:
			intent.setClass(this, BreathingLight.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 1:
			
			SIMOrUIMCardTest(false);
			break;
		
		case 18:
			intent.setClass(this, ProximitySenor.class);
			startActivityForResult(intent, REQUEST_ASK);
			
			break;
			
		case 2:
			mIsItemClick = true;
			cameraTest();
			break;
		case 7:
			intent.setClassName("com.android.soundrecorder", "com.android.soundrecorder.Headset");
			startActivityForResult(intent, REQUEST_ASK);
			break;
		case 39:
			intent.setClass(this, OtgTest.class);
			startActivityForResult(intent, REQUEST_ASK);
			break;
			
		}
	}
	
	
	private void cameraTest() {
		try {
			Intent intent = new Intent(
					"android.media.action.STILL_IMAGE_CAMERA");
			intent.putExtra("FromCit_font", true);
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
		
	
		
		if (mtm.isMultiSimEnabled()) {
			
			AlertDialog.Builder builder ;
		
			builder = (new android.app.AlertDialog.Builder(this))
						.setTitle(R.string.test_item_simoruim).setMessage(s1 +"\n"+s2 );
			
			
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
			//updateBtnState(true);
			
			saveValues(mTestPosition,1);
		}
	}
	
	private class FailListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialoginterface, int i) {
			//updateBtnState(false);
			saveValues(mTestPosition,0);
		}
	}
	
	private void chargeTest() {
		m_bBatteryTesting = true;
		BroadcastReceiver broadcastreceiver = mBatteryInfoReceiver;
		IntentFilter intentfilter = new IntentFilter(
				"android.intent.action.BATTERY_CHANGED");
		registerReceiver(broadcastreceiver, intentfilter);
	}
	
	private class BatteryReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (m_bBatteryTesting) {
				m_bBatteryTesting = false;
				showChargeTestInfo(intent, s);
			}
		}
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
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setTitle(R.string.test_item_charge);
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

	private void unregisterBatteryReceiver(){
		try{
			unregisterReceiver(mBatteryInfoReceiver);
		}catch(Exception e){
			Log.e("CITOneByOne","unregisterBatteryReceiver wrong e--------------="+e.toString());
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
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(mDialog!=null){
			mDialog.dismiss();
			mDialog=null;
		}
	}
}
