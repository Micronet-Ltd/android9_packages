/**
 * 
 */
package lovdream.android.cit;

import lovdream.android.cit.LightSenor.SensorListener;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author wt
 *
 */
public class BarometerTest extends Activity implements OnClickListener {

	private final SensorEventListener mBarometerSensorListener;
	private SensorManager mSensorManager;
	Sensor mBarometerSensor;
	TextView mAxisX;
	Button bt_success, bt_fail;
	
	public BarometerTest() {
		mBarometerSensorListener = new SensorListener();
	}
	
	class SensorListener implements SensorEventListener

	{

		public void onAccuracyChanged(Sensor sensor, int i) {
		}

		public void onSensorChanged(SensorEvent sensorevent) {
		     float axisX = sensorevent.values[0];
		     //Log.d("GyroSensor","SensorEventListener"+axisX);
		     mAxisX.setText("P = " + axisX+"");
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_baromter);
		
		mAxisX = (TextView)findViewById(R.id.tv_info_x);
		bt_success = (Button)findViewById(R.id.btn_success);
		bt_success.setOnClickListener(this);
		bt_fail = (Button)findViewById(R.id.btn_fail);
		bt_fail.setOnClickListener(this);
		
		SensorManager sensormanager = (SensorManager) getSystemService("sensor");
		mSensorManager = sensormanager;
		Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		mBarometerSensor = sensor;
	}
	@Override
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
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean flag = mSensorManager.registerListener(mBarometerSensorListener,
				mBarometerSensor, 3);
		String s = (new StringBuilder()).append("bSucceed is ").append(flag)
				.toString();
		//Log.d("GyroSensor", s);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mSensorManager.unregisterListener(mBarometerSensorListener);
	}

}
