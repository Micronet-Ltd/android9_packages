/**
 * 
 */
package lovdream.android.cit;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.os.SystemProperties;

public class BreathingLight extends Activity implements OnClickListener {
	
	private Button successButton, failButton; 
	private Button mStatusCharging, mStatusLowPower, mStatusUnread, mStatusNormal;
	private static final String CONFIG_FILE_PATH = "/data/breathing_light.config";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.breathinglight_test);
		
		successButton = (Button) findViewById(R.id.success_test);
		failButton = (Button) findViewById(R.id.fail_test);
		mStatusCharging = (Button) findViewById(R.id.status_charging);
		mStatusLowPower = (Button) findViewById(R.id.status_low_power);
		mStatusUnread = (Button) findViewById(R.id.status_unread);
		mStatusNormal = (Button) findViewById(R.id.status_normal);
		
		successButton.setOnClickListener(this);
		failButton.setOnClickListener(this);
		mStatusCharging.setOnClickListener(this);
		mStatusLowPower.setOnClickListener(this);
		mStatusUnread.setOnClickListener(this);
		mStatusNormal.setOnClickListener(this);
		
	}
	
	
	private void setBLNStatus(int status) {
		File file = new File(CONFIG_FILE_PATH);;
		FileOutputStream fos;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			fos.write(String.valueOf(status).getBytes());
			fos.close();
		} catch (Exception e) {
			Log.e("CJ", "error: " + e);
		}
		if (file.exists()) {
			SystemProperties.set("ctl.start", "br_light");
		}
	}
	
	enum AW2013_STATUS {
		AW2013_LED_STATUS_OFF, 
		AW2013_LED_STATUS_ON, 
		AW2013_LED_STATUS_CHARGING, 
		AW2013_LED_STATUS_LOW_POWER, 
		AW2013_LED_STATUS_UNREAD, 
		AW2013_LED_STATUS_NORMAL,
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		setBLNStatus((AW2013_STATUS.AW2013_LED_STATUS_OFF).ordinal());
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Bundle b = new Bundle();
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.success_test:
			b.putInt("test_result", 1);
			intent.putExtras(b);
			setResult(RESULT_OK, intent);
			finish();
			break;
			
		case R.id.fail_test:
			b.putInt("test_result", 0);
			intent.putExtras(b);
			setResult(RESULT_OK, intent);
			finish();
			break;
			
		case R.id.status_charging:
			setBLNStatus((AW2013_STATUS.AW2013_LED_STATUS_CHARGING).ordinal());
			break;
			
		case R.id.status_low_power:
			setBLNStatus((AW2013_STATUS.AW2013_LED_STATUS_LOW_POWER).ordinal());
			break;
			
		case R.id.status_unread:
			setBLNStatus((AW2013_STATUS.AW2013_LED_STATUS_UNREAD).ordinal());
			break;
			
		case R.id.status_normal:
			setBLNStatus((AW2013_STATUS.AW2013_LED_STATUS_NORMAL).ordinal());
			break;

		default:
			break;
		}
	}
	
}
