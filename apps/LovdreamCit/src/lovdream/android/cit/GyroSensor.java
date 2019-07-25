/**
 * 
 */
package lovdream.android.cit;

import java.io.FileInputStream;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.os.SystemProperties;
public class GyroSensor extends Activity implements OnClickListener {

	private final SensorEventListener mGyroSensorListener;
	private SensorManager mSensorManager;
	Sensor mGyroSensor;
	TextView mAxisX, mAxisY, mAxisZ;
	Button bt_success, bt_fail, bt_calibration;
	LinearLayout mCalibrationLayout;
	//private boolean bt_success_enalbe=false;
	private boolean mIsA406 = SystemProperties.get("ro.product.name").equals("msm8916_64_a406");
	private boolean mIsA406_d6 = SystemProperties.get("ro.product.name").equals("msm8916_a406_d6");
	private boolean mIsP800 = true;//SystemProperties.get("ro.product.name").equals("msm8939_64_p800");
	private boolean mShowBtn = mIsA406 || mIsA406_d6 || mIsP800;
	public GyroSensor() {
		mGyroSensorListener = new SensorListener();
	}
	
	class SensorListener implements SensorEventListener

	{

		public void onAccuracyChanged(Sensor sensor, int i) {
		}

		public void onSensorChanged(SensorEvent sensorevent) {
		     float axisX = sensorevent.values[0];
		     float axisY = sensorevent.values[1];
		     float axisZ = sensorevent.values[2];
		     
		     mAxisX.setText("X = " + axisX);
		     mAxisY.setText("Y = " + axisY);
		     mAxisZ.setText("Z = " + axisZ);
		     //bt_success_enalbe=true;
		     bt_success.setEnabled(true);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_gyro_sensor);
		
		mAxisX = (TextView)findViewById(R.id.tv_gyro_senor_info_x);
		mAxisY = (TextView)findViewById(R.id.tv_gyro_senor_info_y);
		mAxisZ = (TextView)findViewById(R.id.tv_gyro_senor_info_z);
		bt_success = (Button)findViewById(R.id.btn_success);
		bt_success.setEnabled(false);
		bt_success.setOnClickListener(this);
		bt_fail = (Button)findViewById(R.id.btn_fail);
		bt_fail.setOnClickListener(this);
		bt_calibration = (Button)findViewById(R.id.btn_calibration);
		bt_calibration.setOnClickListener(this);
		mCalibrationLayout = (LinearLayout)findViewById(R.id.calibration_LinearLayout);
		if(!mShowBtn){
			mCalibrationLayout.setVisibility(View.GONE);
		}
		
		SensorManager sensormanager = (SensorManager) getSystemService("sensor");
		mSensorManager = sensormanager;
		Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		mGyroSensor = sensor;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		Bundle b = new Bundle();
		Intent intent = new Intent();
		
		if(id == R.id.btn_success){
			b.putInt("test_result", 1);
			intent.putExtras(b);
			setResult(RESULT_OK, intent);
			finish();
		}else if(id == R.id.btn_fail){
			b.putInt("test_result", 0);
			intent.putExtras(b);
			setResult(RESULT_OK, intent);
			finish();
		}else if(id == R.id.btn_calibration){
			try{
				FileInputStream inStream=new FileInputStream("/sys/class/sensors/LS6DS0-gyro/self_test");
	            byte[] buffer= new byte[10];
	            int nu=inStream.read(buffer);
	            inStream.close();
	            android.util.Log.e("GyroSensor","buffer-------------------------="+new String(buffer));
	            if(nu > 0){
	            	Toast.makeText(this, R.string.str_calibration_success, Toast.LENGTH_SHORT).show();
	            }
			}catch(Exception e){
				android.util.Log.e("GyroSensor","Exception-------------------------="+e);
			}
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean flag = mSensorManager.registerListener(mGyroSensorListener,
				mGyroSensor, 3);
		String s = (new StringBuilder()).append("bSucceed is ").append(flag)
				.toString();
		Log.d("GyroSensor", s);
	}
	public boolean onKeyDown(int i, KeyEvent keyevent) {

		boolean flag;
		if (i == KeyEvent.KEYCODE_BACK ) {
			flag = true;
			Bundle bundle = new Bundle();
			Intent intent = new Intent();
			bundle.putInt("test_result", 2);
			Log.i("BACKPRESSED","------------------->GyroSensor");
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			this.finish( );
			
		}
		else {
			flag = false;
		}
		return flag;
	
	}
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mSensorManager.unregisterListener(mGyroSensorListener);
	}

}
