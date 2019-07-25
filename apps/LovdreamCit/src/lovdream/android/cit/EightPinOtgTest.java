package lovdream.android.cit;
//add 8pin-otg by wt
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.SystemProperties;

public class EightPinOtgTest extends Activity {

	public final static String TAG = "EightPinOtgTest";
	private TextView mTextView;
	private Button mButton, successButton, failButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flash_test);
		mTextView = (TextView) findViewById(R.id.textView1);
		mTextView.setText(getString(R.string.test_eight_otg_prompt));
		mButton = (Button) findViewById(R.id.flash_test);
		mButton.setVisibility(View.GONE);
		successButton = (Button) findViewById(R.id.success_test);
		//start fix bug of 11204 add by chr
		//	successButton.setEnabled(false);
		//end 
		failButton = (Button) findViewById(R.id.fail_test);
		
		final Bundle b = new Bundle();
		final Intent intent = new Intent();
		successButton.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				b.putInt("test_result", 1);
				intent.putExtras(b);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		failButton.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				b.putInt("test_result", 0);
				intent.putExtras(b);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		/*
		switch(keyCode){
		case KeyEvent.KEYCODE_USB_OTG:
			mTextView.setText(getString(R.string.test_eight_otg_in));
			successButton.setEnabled(true);
			break;
		case KeyEvent.KEYCODE_BACK_TOUCH:
			mTextView.setText(getString(R.string.test_eight_otg_in));
			break;
		case KeyEvent.KEYCODE_BACK_RELEASE:
			mTextView.setText(getString(R.string.test_eight_otg_out));
			successButton.setEnabled(true);
			if(CITMain.CITtype == CITMain.PCBA_AUTO_TEST
					|| CITMain.CITtype == CITMain.MACHINE_AUTO_TEST){
				successButton.performClick();
			}
			break;
		}
		*/
		return super.onKeyDown(keyCode, event);
	}
}
