package lovdream.android.cit;

import android.app.Activity;
import android.content.Intent;
import android.hardware.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class LightSenor extends Activity implements View.OnClickListener{

	public LightSenor() {
		mLightSensorListener = new SensorListener();
	}

	private void initAllControl() {
		strLightinfo = getString(R.string.test_lightsensor_info);
		mtv_Light_info = (TextView) findViewById(R.id.tv_light_senor_info);

		bt_success = (Button)findViewById(R.id.btn_success);
		bt_success.setOnClickListener(this);
		bt_fail = (Button)findViewById(R.id.btn_fail);
		bt_fail.setOnClickListener(this);
	}

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.test_light_senor);
		initAllControl();
		TextView textview = mtv_Light_info;
		String s = strLightinfo;
		textview.setText(s);
		SensorManager sensormanager = (SensorManager) getSystemService("sensor");
		mSensorManager = sensormanager;
		Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		mLightSensor = sensor;
	}

	protected void onResume() {
		super.onResume();
		boolean flag = mSensorManager.registerListener(mLightSensorListener,
				mLightSensor, 3);
		String s = (new StringBuilder()).append("bSucceed is ").append(flag)
				.toString();
		Log.d("Prominity", s);
	}

	protected void onStop() {
		super.onStop();
		mSensorManager.unregisterListener(mLightSensorListener);
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

	Sensor mLightSensor;
	private final SensorEventListener mLightSensorListener;
	private SensorManager mSensorManager;
	TextView mtv_Light_info;
	String strLightinfo;
	Button bt_success;
	Button bt_fail;

	class SensorListener implements SensorEventListener
	{

		public void onAccuracyChanged(Sensor sensor, int i) {
		}

		public void onSensorChanged(SensorEvent sensorevent) {
			TextView textview = mtv_Light_info;
			StringBuilder stringbuilder = new StringBuilder();
			String s = strLightinfo;
			StringBuilder stringbuilder1 = stringbuilder.append(s).append("\n");
			float f = sensorevent.values[0];
			String s1 = stringbuilder1.append(f).toString();
			textview.setText(s1);
			if(f  > 1000){
				bt_success.performClick();
			}
		}
	}

}
