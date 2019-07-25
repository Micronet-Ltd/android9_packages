package lovdream.android.cit;

import java.io.FileReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
//import com.android.internal.policy.impl.PhoneWindowManager;

public class HallTest extends Activity implements View.OnClickListener{
	
	ImageView imageView[];
	TextView mtv_pro_info;
	String strPromityinfo;

	Button bt_success;
	Button bt_fail;
	
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.test_proximitysenor);
		initAllControl();
		mtv_pro_info.setText(strPromityinfo);
	}

	protected void onResume() {
		super.onResume();
		imageView[0].setVisibility(View.VISIBLE);
		imageView[1].setVisibility(View.INVISIBLE);
		//bt_success.setEnabled(false);
		//PhoneWindowManager.sIgnoreHallGoToSleep = true;
		android.os.SystemProperties.set("sys.cit_keytest","true");
	}

	@Override
	protected void onPause() {
		super.onPause();
		//PhoneWindowManager.sIgnoreHallGoToSleep = false;
		android.os.SystemProperties.set("sys.cit_keytest","false");
	}
   
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();
		Intent intent = new Intent();
		bundle.putInt("test_result", 2);
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		this.finish();
		super.onBackPressed();
	}
	
	private void initAllControl() {
		imageView = new ImageView[2];
		imageView[0] = (ImageView) findViewById(R.id.test_proximitysensor_gray);
		imageView[1] = (ImageView) findViewById(R.id.test_proximitysensor_green);
		strPromityinfo = getString(R.string.test_hall);
		mtv_pro_info = (TextView) findViewById(R.id.tv_proximitysenor_info);

		bt_success = (Button)findViewById(R.id.btn_success);
		bt_success.setOnClickListener(this);
		bt_success.setEnabled(false);
		bt_fail = (Button)findViewById(R.id.btn_fail);
		bt_fail.setOnClickListener(this);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
	
		switch (keyCode) {
	
		//case KeyEvent.KEYCODE_HALL_OPEN:
		//case KeyEvent.KEYCODE_FOLDER_OPEN:
		case KeyEvent.KEYCODE_WAKEUP:
			imageView[0].setVisibility(View.VISIBLE);
			imageView[1].setVisibility(View.INVISIBLE);
			break;
		//case KeyEvent.KEYCODE_HALL_CLOSE:
		//case KeyEvent.KEYCODE_FOLDER_CLOSE:
		case KeyEvent.KEYCODE_SLEEP:
			imageView[0].setVisibility(View.INVISIBLE);
			imageView[1].setVisibility(View.VISIBLE);
			bt_success.setEnabled(true);
			if(CITMain.CITtype == CITMain.PCBA_AUTO_TEST 
					|| CITMain.CITtype == CITMain.MACHINE_AUTO_TEST){
				bt_success.performClick();
			}
			break;
		}
		
		return super.onKeyDown(keyCode, event);
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
}
