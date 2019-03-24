package lovdream.android.cit;
import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
public class ExternalAntennaTest extends Activity{

	private TextView mTextView;
	private Button mButton;
	private Button successButton;
	private Button failButton;
        boolean in = false;
	boolean out = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flash_test);
		mTextView = (TextView) findViewById(R.id.textView1);
		mTextView.setText(getString(R.string.test_external_antenna_prompt));
		mButton = (Button) findViewById(R.id.flash_test);
		mButton.setVisibility(View.GONE);
		successButton = (Button) findViewById(R.id.success_test);
		//start fix bug of 11204 add by chr
		successButton.setEnabled(false);
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
		switch (keyCode) {
		case  KeyEvent.KEYCODE_F3:
			in = true;
			mTextView.setText(getString(R.string.test_external_antenna_inplug));
			successButton.setEnabled(in && out);
			break;
		case  KeyEvent.KEYCODE_F4:
			mTextView.setText(getString(R.string.test_external_antenna_outplug));
			out = true;
			successButton.setEnabled(in && out);
			break;
		default:
			break;
		}
		return true;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		in = false;
		out = false;
	}
	
}
