package lovdream.android.cit;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.IBluetooth;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ServiceManager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothTestNew extends Activity{

    ListView mListView = null;
    Button cancelButton = null;
    Button scanButton = null;

    LayoutInflater mInflater = null;
    IntentFilter filter = null;
    BluetoothAdapter mBluetoothAdapter = null;
    List<DeviceInfo> mDeviceList = new ArrayList<DeviceInfo>();
    Time time = new Time();
    Set<BluetoothDevice> bondedDevices;
    long startTime;
    long endTime;
    boolean recordTime = false;
    boolean isUserCanncel = false;
    private static IBluetooth btService;
    private final static int MIN_COUNT = 1;
    private static final boolean BLUETOOTH_SCAN_TO_SUCESS = true;

	BroadcastReceiver mReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent){
			String action = intent.getAction();
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if(BluetoothDevice.ACTION_FOUND.equals(action)){
				mDeviceList.add(new DeviceInfo(device.getName(), device.getAddress()));
				updateAdapter();
				if(mDeviceList.size() >= MIN_COUNT){
					finishTest(true,getString(R.string.bluetooth_test_success));
				}
			} else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
				setProgressBarIndeterminateVisibility(false);
				if(mDeviceList.size() >= MIN_COUNT){
					finishTest(true,getString(R.string.bluetooth_test_success));
				} else if(!isUserCanncel){
					Toast.makeText(BluetoothTestNew.this,R.string.bluetooth_test_scan_null,Toast.LENGTH_SHORT).show();
				}
			} else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
				startScanAndUpdateAdapter();
			} else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
				if(BluetoothAdapter.STATE_ON == intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)){
					if(BLUETOOTH_SCAN_TO_SUCESS){
						scanDevices();
					} else{
						finishTest(true,getString(R.string.bluetooth_test_success));
					}
					if(recordTime){
						time.setToNow();
						endTime = time.toMillis(true);
						recordTime = false;
						Toast.makeText(BluetoothTestNew.this,getString(R.string.bluetooth_test_turn_on_bluetooth_cost) 
								+ (endTime - startTime) / 1000 + "S",Toast.LENGTH_SHORT).show();
					} else if(BluetoothAdapter.STATE_OFF == intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)){
						mBluetoothAdapter.enable();
					}
				} else if(BluetoothAdapter.STATE_TURNING_ON == intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)){
					Toast.makeText(BluetoothTestNew.this,R.string.bluetooth_test_turning_on,Toast.LENGTH_SHORT).show();
					setProgressBarIndeterminateVisibility(true);
				}
			}
		}
	};

	BaseAdapter mAdapter = new BaseAdapter(){
		@Override
		public View getView(int index,View convertView,ViewGroup parent){
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.bluetooth_item,null);
			}
			ImageView image = (ImageView) convertView.findViewById(R.id.bluetooth_image);
			TextView text = (TextView) convertView.findViewById(R.id.bluetooth_text);
			text.setText(mDeviceList.get(index).getName() + "\n" + mDeviceList.get(index).getAddress());
			image.setImageResource(android.R.drawable.stat_sys_data_bluetooth);
			return convertView;
		}
		@Override
		public int getCount(){
			return mDeviceList != null ? mDeviceList.size() : 0;
		}
		@Override
		public long getItemId(int arg){
			return 0;
		}
		@Override
		public Object getItem(int arg){
			return null;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.bluetooth_test_new);

		mInflater = LayoutInflater.from(this);
//		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager == null) {
            Log.e("cit_bluetooth_test", "Unable to initialize BluetoothManager.");
            finishTest(false,getString(R.string.bluetooth_test_get_service_fail));
            return;
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e("cit_bluetooth_test", "Unable to obtain a BluetoothAdapter.");
            finishTest(false,getString(R.string.bluetooth_test_get_service_fail));
            return;
        }

//		IBinder binder = ServiceManager.getService("bluetooth");
//		if(binder == null){
//			// what can we do?
//			Log.e("cit_bluetooth_test","can not get bluetooth service");
//			finishTest(false,getString(R.string.bluetooth_test_get_service_fail));
//		}
//		btService = IBluetooth.Stub.asInterface(binder);

		mListView = (ListView) findViewById(R.id.devices_list);
		mListView.setAdapter(mAdapter);
		scanButton = (Button) findViewById(R.id.bluetooth_scan);
		scanButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				scanDevices();
			}
		});
		cancelButton = (Button) findViewById(R.id.bluetooth_cancel);
		cancelButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				isUserCanncel = true;
				finishTest(false,getString(R.string.bluetooth_test_fail));
			}
		});
		
		startScanAndUpdateAdapter();

		if(mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON){
			if(BLUETOOTH_SCAN_TO_SUCESS){
				scanDevices();
			} else{
				finishTest(true,getString(R.string.bluetooth_test_success));
			}
		} else{
			if(mBluetoothAdapter.getState() != BluetoothAdapter.STATE_TURNING_ON){
				time.setToNow();
				startTime = time.toMillis(true);
				recordTime = true;
				mBluetoothAdapter.enable();
			}
		}

		filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
	}

	@Override
	protected void onResume(){
		registerReceiver(mReceiver,filter);
		updateAdapter();
		super.onResume();
	}

	@Override
	protected void onPause(){
		unregisterReceiver(mReceiver);
		super.onPause();
	}

	@Override
	public void finish(){
		cancelScan();
		/*manufactory's requirement,close BT after test unconditional,for save charge?*/
		if (mBluetoothAdapter != null)
			mBluetoothAdapter.disable();
		super.finish();
	}

	private void startScanAndUpdateAdapter(){
		mDeviceList.clear();
		bondedDevices = mBluetoothAdapter.getBondedDevices();
		for(BluetoothDevice device : bondedDevices){
			DeviceInfo deviceInfo = new DeviceInfo(device.getName(),device.getAddress());
			mDeviceList.add(deviceInfo);
		}
		updateAdapter();
	}

	private void updateAdapter(){
		mAdapter.notifyDataSetChanged();
	}

	private void scanDevices(){
		Toast.makeText(this,R.string.bluetooth_test_scan_start,Toast.LENGTH_SHORT).show();
		setProgressBarIndeterminateVisibility(true);
		if(mBluetoothAdapter.isDiscovering()){
			mBluetoothAdapter.cancelDiscovery();
		}
		mBluetoothAdapter.startDiscovery();
	}

	private void cancelScan(){
		setProgressBarIndeterminateVisibility(false);
		if((mBluetoothAdapter != null) && mBluetoothAdapter.isDiscovering()){
			mBluetoothAdapter.cancelDiscovery();
		}
	}

	private void finishTest(boolean passed,String msg){
		if(msg != null){
			Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
		}
		Bundle bundle = new Bundle();
		bundle.putInt("test_result",passed ? 1 : 0);
		setResult(RESULT_OK,new Intent().putExtras(bundle));
		finish();
	}
	
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
}