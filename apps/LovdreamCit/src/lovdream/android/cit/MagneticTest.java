package lovdream.android.cit;

import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MagneticTest extends Activity implements View.OnClickListener{

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		SensorManager sensormanager = (SensorManager) getSystemService("sensor");
		mSensorManager = sensormanager;
		setContentView(R.layout.test_magnetic);
		mt_TextView = new TextView[3];
		mt_TextView[0] = (TextView) findViewById(R.id.mt_tv_x);
		mt_TextView[1] = (TextView) findViewById(R.id.mt_tv_y);
		mt_TextView[2] = (TextView) findViewById(R.id.mt_tv_z);
		bt_success = (Button)findViewById(R.id.btn_success);
		bt_success.setOnClickListener(this);
		bt_fail = (Button)findViewById(R.id.btn_fail);
		bt_fail.setOnClickListener(this);
		mListener = new senListerner();

	}

	protected void onResume() {
		super.onResume();
		SensorManager sensormanager = mSensorManager;
		SensorListener sensorlistener = mListener;
		sensormanager.registerListener(sensorlistener, 8);
	}

	protected void onStop() {
		super.onStop();
		SensorManager sensormanager = mSensorManager;
		SensorListener sensorlistener = mListener;
		sensormanager.unregisterListener(sensorlistener);

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

	private SensorListener mListener;
	private SensorManager mSensorManager;
	TextView mt_TextView[];
	Button bt_success;
	Button bt_fail;

	private class senListerner implements SensorListener {

		public void onAccuracyChanged(int i, int j) {
		}

		public void onSensorChanged(int i, float af[]) {
			StringBuilder stringbuilder = new StringBuilder();
			String s = stringbuilder.append("X = ").append(af[0]).toString();
			mt_TextView[0].setText(s);
			StringBuilder stringbuilder1 = (new StringBuilder()).append("Y = ");
			String s1 = stringbuilder1.append(af[1]).toString();
			mt_TextView[1].setText(s1);
			StringBuilder stringbuilder2 = (new StringBuilder()).append("Z = ");
			String s2 = stringbuilder2.append(af[2]).toString();
			mt_TextView[2].setText(s2);
		}

	}

}
