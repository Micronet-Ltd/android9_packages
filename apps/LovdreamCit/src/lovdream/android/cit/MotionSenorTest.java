package lovdream.android.cit;

import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.KeyEvent;

public class MotionSenorTest extends Activity implements View.OnClickListener{
	public MotionSenorTest() {
		m_rotation = 0;
		mListener = new SensListener();
	}
	
	public boolean onKeyDown(int i, KeyEvent keyevent) {
		boolean flag;
		if (i == KeyEvent.KEYCODE_BACK ) {
			flag = true;
			Bundle bundle = new Bundle();
			Intent intent = new Intent();
			bundle.putInt("test_result", 2);
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			this.finish( );
			
		}
		else {
			flag = false;
		}
		return flag;
	}
	 
	
	private int GetDirection(int i, int j) {
		int i1;
		int k = Math.abs(i);
		int l = Math.abs(j);
		
		if (k > l) {
			if (i > 0)
				i1 = 2;
			else
				i1 = 4;
		} else {
			if (j > 0)
				i1 = 1;
			else
				i1 = 3;
		}
		
		if (m_rotation == 90) // goto _L2; else goto _L1
			i1 = (i1 + 1) % 4;
		else if (m_rotation == 270)
			i1 = (i1 + 3) % 4;

		if (i1 == 0)
			i1 = 4;
		
		return i1;

	}

	private void initAllControl() {
		imageView[0] = (ImageView) findViewById(R.id.ms_arrow_up);
		imageView[1] = (ImageView) findViewById(R.id.ms_arrow_right);
		imageView[3] = (ImageView) findViewById(R.id.ms_arrow_left);
		imageView[2] = (ImageView) findViewById(R.id.ms_arrow_down);
		
		ms_tv_XYZ[0] = (TextView) findViewById(R.id.ms_tv_x);
		ms_tv_XYZ[1] = (TextView) findViewById(R.id.ms_tv_y);
		ms_tv_XYZ[2] = (TextView) findViewById(R.id.ms_tv_z);
		bt_success = (Button)findViewById(R.id.btn_success);
		bt_success.setOnClickListener(this);
		bt_fail = (Button)findViewById(R.id.btn_fail);
		bt_fail.setOnClickListener(this);
		bt_success.setEnabled(false);
//		bt_fail.setEnabled(false);
	}

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		mSensorManager = (SensorManager) getSystemService("sensor");
		imageView = new ImageView[4];
		ms_tv_XYZ = new TextView[3];
		setContentView(R.layout.test_motionsenor);
		initAllControl();
	}

	protected void onResume() {
		super.onResume();
		SensorManager sensormanager = mSensorManager;
		SensorListener sensorlistener = mListener;
		sensormanager.registerListener(sensorlistener, 2);
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



	ImageView imageView[];
	int[] isTrue={0,0,0,0};
	private final SensorListener mListener;
	private SensorManager mSensorManager;
	private float mValues[];
	int m_nCurArrow;
	int m_rotation;
	TextView ms_tv_XYZ[];
	Button bt_success;
	Button bt_fail;
	
	boolean x = false;
	boolean y = false;
	boolean z = false;

	/*
	 * static float[] access$002(MotionSenorTest motionsenortest, float af[]) {
	 * motionsenortest.mValues = af; return af; }
	 */

	private class SensListener implements SensorListener {

		public void onAccuracyChanged(int i, int j) {
		}

		public void onSensorChanged(int i, float af[]) {
			/*if (x && y && z) {
				bt_success.setEnabled(true);
			}*/
			MotionSenorTest motionsenortest;
			int j;
			int k;
			mValues = af;
			TextView textview = ms_tv_XYZ[0];
			StringBuilder stringbuilder = (new StringBuilder()).append("X = ");
			float f = mValues[0];
			if (f != -0.0) {
				x = true;
			}
			String s = stringbuilder.append(f).toString();
			textview.setText(s);
			TextView textview1 = ms_tv_XYZ[1];
			StringBuilder stringbuilder1 = (new StringBuilder()).append("Y = ");
			float f1 = mValues[1];
			if (f1 != -0.0) {
				y = true;
			}
			String s1 = stringbuilder1.append(f1).toString();
			textview1.setText(s1);
			TextView textview2 = ms_tv_XYZ[2];
			StringBuilder stringbuilder2 = (new StringBuilder()).append("Z = ");
			float f2 = mValues[2];
			if (f2 != -0.0) {
				z = true;
			}
			String s2 = stringbuilder2.append(f2).toString();
			textview2.setText(s2);
			motionsenortest = MotionSenorTest.this;
			j = (int) mValues[0];
			k = (int) mValues[1];

			int l = 1;
			switch (motionsenortest.GetDirection(j, k)) {
			case 1:
				l = 1;	
				break;
			case 2:
				l = 2;
				break;
			case 3:
				l = 3;
				break;
			case 4:
				l = 4;
				break;
			default:
				l = 1;
				break;
			}
			l--;

			if (l != m_nCurArrow) {
				imageView[m_nCurArrow].setVisibility(4);
				m_nCurArrow = l;
				imageView[l].setVisibility(0);
				if(isTrue[l]!=1){
					isTrue[l]=1;
				}
			} else {
				imageView[m_nCurArrow].setVisibility(0);
				if(isTrue[m_nCurArrow]!=1){
					isTrue[m_nCurArrow]=1;
				}
			}
				if(isTrue[0]==1&&isTrue[1]==1&&isTrue[2]==1&&isTrue[3]==1){
				bt_success.setEnabled(true);
			}
			return;
		}

	}

}
