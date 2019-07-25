package com.android.lovdream;

import java.io.IOException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
//import com.qualcomm.qcnvitems.QcNvItems;
//import com.android.internal.telephony.Phone;
//import com.android.internal.telephony.PhoneConstants;
//import com.android.internal.telephony.PhoneFactory;
//import com.android.internal.telephony.ConfigResourceUtil;

import com.android.lovdream.R;
import com.lovdream.util.SystemUtil;

public class DeviceIdActivity extends Activity implements OnClickListener {

	//private QcNvItems mNv;

	private ListView info;
	private List<Map<String, Object>> data;
	private static final String SN = "ro.serialno";
	//private Phone mPhone = null;
	TelephonyManager tm;
	Button check;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_id_activity);
		tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
		info = (ListView) findViewById(R.id.device_id_info);
		check = (Button) findViewById(R.id.device_id_check);
		check.setOnClickListener(this);
		//mNv = new QcNvItems(this);
		data = getData();
		MyAdapter adapter = new MyAdapter(this);
		info.setAdapter(adapter);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		/*
		if (mNv != null) {
			Log.i("..pc", "mNv!= null");
			getImeiOrMeid();
		}
		*/
		for (int i = 1; i <= 4; i++) {
			map = new HashMap<String, Object>();
			if (i == 1) {
				map.put("name",
						getResources().getString(R.string.device_id_imei1));
				map.put("number", imei + "/ 02");
				map.put("icon", R.drawable.device_id_icon1);

			} else if (i == 2) {
				map.put("name",
						getResources().getString(R.string.device_id_imei2));
				map.put("number", imei2 + "/ 02");
				map.put("icon", R.drawable.device_id_icon2);
			} else if (i == 3) {
				map.put("name", getResources().getString(R.string.device_id_sn));
				map.put("number", getSnVersion().substring(0, 15));
				map.put("icon", R.drawable.device_id_icon3);
			} else {
				map.put("name",
						getResources().getString(R.string.device_id_meid));
				map.put("number", meid + "  ");
				map.put("icon", R.drawable.device_id_icon4);
			}
			list.add(map);
			map = null;
		}
		return list;
	}

	static class ViewHolder {
		TextView name;
		TextView number;
		ImageView icon;
	}

	private class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;

		private MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.device_id_item, null);
				holder.name = (TextView) convertView
						.findViewById(R.id.device_id_name);
				holder.number = (TextView) convertView
						.findViewById(R.id.device_id_number);
				holder.icon = (ImageView) convertView
						.findViewById(R.id.device_id_icon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String name = data.get(position).get("name").toString();
			String num = data.get(position).get("number").toString();
			int icon = (int) data.get(position).get("icon");
			Log.i("..pc", name + "    " + num + "    " + icon);
			if (!(name == null || num == null)) {
				holder.name.setText(name);
				holder.number.setText(num);
			}
			holder.icon.setImageResource(icon);
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public int getCount() {
			return data.size();
		}

	}

	static Method systemProperties_get = null;

	static String getAndroidOsSystemProperties(String key) {
		String ret;
		try {
			systemProperties_get = Class.forName("android.os.SystemProperties")
					.getMethod("get", String.class);
			if ((ret = (String) systemProperties_get.invoke(null, key)) != null)
				return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return "";
	}

	String imei = "1";
	String imei2 = "1";
	String meid = "1";

	/*
	public void getImeiOrMeid() {
		try {
			ConfigResourceUtil mConfigResUtil = new ConfigResourceUtil();
			List<String> deviceIds = new ArrayList<String>();
			for (int slot = 0; slot < tm.getPhoneCount(); slot++) {
				String deviceId = tm.getDeviceId(slot);
				boolean enable14DigitImei = false;
				try {
					enable14DigitImei = mConfigResUtil.getBooleanValue(this,
							"config_enable_display_14digit_imei");
				} catch (RuntimeException ex) {
					// do Nothing
					Log.i("..pc",
							"getImeiOrMeid   RuntimeException..."
									+ ex.toString());
				}
				if (enable14DigitImei && deviceId != null
						&& deviceId.length() > 14) {
					deviceId = deviceId.substring(0, 14);
				}
				deviceIds.add(deviceId);
				Log.i("..pc", "getImeiOrMeid   deviceId=..." + deviceId);
			}

			for (int i = 0; i < deviceIds.size(); i++) {
				if (i == 0) {
					imei = deviceIds.get(0);
				} else if (i == 1) {
					imei2 = deviceIds.get(1);
					meid = deviceIds.get(1);
				}
				if(!deviceIds.get(i).startsWith("867")){
					meid = deviceIds.get(i);
				}
			}

//			int numPhone = TelephonyManager.getDefault().getPhoneCount();
//			for (int i = 0; i < numPhone; i++) {
//				Phone phone = PhoneFactory.getPhone(i);
//				if (phone.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
//					mPhone = phone;
//					break;
//				}
//			}
//
//			Log.i("..pc", "getImeiOrMeid   Exception..." + mPhone.getMeid()
//					+ "    " + mPhone.getImei());
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("..pc", "getImeiOrMeid   Exception..." + e.toString());
		}
		 if (imei.equals("0")) {
			 imei = tm.getDeviceId();
		 }
		 if (imei2.equals("0")) {
//		 imei2 = "89736000000000";
		 }
		 if (meid.equals("0")) {
		 meid = tm.getDeviceId();
		 }
	}
	*/

	@Override
	public void onClick(View view) {
		if (view != null) {
			switch (view.getId()) {
			case R.id.device_id_check:
				Log.i("..pc", "device_id_check");
				finish();
				break;
			default:
				break;
			}
		}
	}
	
	private String getSnVersion() {
		String strSN = "";
		strSN = SystemUtil.getSN();
//		try {
//			strSN = mNv.getSNNumber();
//		} catch (IOException e) {
//
//		}
		return strSN;
	}

}
