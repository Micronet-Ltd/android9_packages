package lovdream.android.cit;

import java.io.FileReader;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.hardware.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ProximitySenor extends Activity implements View.OnClickListener, Runnable{
	
	Thread mGetValues;
	private static final String ALS_ADC = "sys/devices/virtual/input/input0/als_adc";
	private static final String PS_ADC = "sys/devices/virtual/input/input1/ps_adc";
	private static final int REFRESH_TEXT = 1;
	private boolean bStop;
	
	private void initAllControl() {
		imageView = new ImageView[2];
		imageView[0] = (ImageView) findViewById(R.id.test_proximitysensor_gray);
		imageView[1] = (ImageView) findViewById(R.id.test_proximitysensor_green);
		strPromityinfo = getString(R.string.proximitysenor_info);
		mtv_pro_info = (TextView) findViewById(R.id.tv_proximitysenor_info);
		madc_values = (TextView) findViewById(R.id.adc_values);
		//madc_values.setVisibility(View.GONE);
		mProximityListener = new SenListener();

		bt_success = (Button)findViewById(R.id.btn_success);
		bt_success.setOnClickListener(this);
		bt_success.setEnabled(false);
		bt_fail = (Button)findViewById(R.id.btn_fail);
		bt_fail.setOnClickListener(this);
	}
	
	public void run() {
		while(!bStop)
		{
			try {
	            FileReader file_als = new FileReader(ALS_ADC);
	            FileReader file_ps = new FileReader(PS_ADC);
	            char[] buffer_als = new char[64];
	            char[] buffer_ps = new char[64];
	            int len_ls = file_als.read(buffer_als, 0, 64);
	            int len_ps = file_ps.read(buffer_ps, 0, 1);
	            String value_als = (new String(buffer_als, 0, len_ls)).trim();
	            String value_ps = (new String(buffer_ps, 0, len_ps)).trim();
	            mRefreshHandler.sendMessage(mRefreshHandler
						.obtainMessage(REFRESH_TEXT, value_als+"\n"+value_ps));
	            Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	Handler mRefreshHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case REFRESH_TEXT:
				madc_values.setText(msg.obj.toString());
				//madc_values.setVisibility(View.VISIBLE);
			}
		}
	};
	 private List<Sensor> mSensorList;
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.test_proximitysenor);
		initAllControl();
		mtv_pro_info.setText(strPromityinfo);
		mSensorManager = (SensorManager) getSystemService("sensor");
		//fix bug about mProimitySensor by zzj start
		mSensorList=mSensorManager.getSensorList(Sensor.TYPE_ALL);
		mProimitySensor = mSensorManager.getDefaultSensor(8);
/*		for(int i=0;i<mSensorList.size();i++){
		//	if(mSensorList.get(i).getName() !=null && mSensorList.get(i).getName().contains("EPL259x ALs")){
				mProimitySensor=mSensorList.get(i);
        // }
		//fix bug about mProimitySensor by zzj end
         }*/
		
	}

	protected void onResume() {
		super.onResume();
		boolean flag = mSensorManager.registerListener(mProximityListener,
				mProimitySensor, 3);
		String s = (new StringBuilder()).append("bSucceed is ").append(flag)
				.toString();
		Log.d("Prominity", s);
		bt_success.setEnabled(false);
		bStop = false;
		//fix bug about mProimitySensor by zzj start
	//	mGetValues = new Thread(this);
	//	mGetValues.start();
		//fix bug about mProimitySensor by zzj end
	}
   
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Log.d("CJ", "onBackPressed");
		Bundle bundle = new Bundle();
		Intent intent = new Intent();
		bundle.putInt("test_result", 2);
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		this.finish();
		super.onBackPressed();
	}
	
	protected void onStop() {
		super.onStop();
		SensorManager sensormanager = mSensorManager;
		SensorEventListener sensoreventlistener = mProximityListener;
		sensormanager.unregisterListener(sensoreventlistener);
		bStop = true;
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		Bundle b = new Bundle();
		Intent intent = new Intent();
		
		if(id == R.id.btn_success){
			b.putInt("test_result", 1);
		}else{
			b.putInt("test_result", 0);
		}
		
		intent.putExtras(b);
		setResult(RESULT_OK, intent);
		finish();
	}

	ImageView imageView[];
	Sensor mProimitySensor;
	private SensorEventListener mProximityListener;
	private SensorManager mSensorManager;
	TextView mtv_pro_info;
	TextView madc_values;
	String strPromityinfo;

	Button bt_success;
	Button bt_fail;

	private class SenListener implements SensorEventListener

	{

		public void onAccuracyChanged(Sensor sensor, int i) {
		}

		public void onSensorChanged(SensorEvent sensorevent) {
			StringBuilder stringbuilder;
			String s1;
			if (sensorevent.values[0] < 5F) {
				imageView[0].setVisibility(4);
				imageView[1].setVisibility(0);
				bt_success.setEnabled(true);
			} else {
				imageView[0].setVisibility(0);
				imageView[1].setVisibility(4);
				
			}
			stringbuilder = new StringBuilder();
			s1 = stringbuilder.append(strPromityinfo).append("\n")
					.append(sensorevent.values[0]).toString();
			mtv_pro_info.setText(s1);
			
			if(bt_success.isEnabled() && sensorevent.values[0] >=5F){
				bt_success.performClick();
			}
		}

	}
	
}
