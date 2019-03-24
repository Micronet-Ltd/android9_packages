package lovdream.android.cit;

import java.io.File;
import java.io.FileOutputStream;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SideOtgTest extends Activity{
	private TextView mTextView;
	private Button mButton;
	private Button successButton;
	private Button failButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSideOtgStatus(1);
		setContentView(R.layout.flash_test);
		mTextView = (TextView) findViewById(R.id.textView1);
		mTextView.setText(getString(R.string.test_side_otg_prompt));
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
	protected void onDestroy() {
		super.onDestroy();
		setSideOtgStatus(0);
	}
	private void setSideOtgStatus(int status) {
		File file = new File("sys/class/ext_dev/function/sideswitch");
		FileOutputStream fos;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			fos.write(String.valueOf(status).getBytes());
			fos.close();
		} catch (Exception e) {
			Log.e("SideOtgTest", "error: " + e);
		}
	}

}
