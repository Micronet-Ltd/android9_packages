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

public class TwoColorLights extends Activity implements OnClickListener {
	
	private Button mTestBtn,successButton, failButton; 
	private TextView mTextText;
	private static final String CONFIG_GREEN_PATH = "/sys/class/leds/green/brightness";
	private static final String CONFIG_RED_PATH = "/sys/class/leds/red/brightness";
	private static final String CONFIG_BLUE_PATH = "/sys/class/leds/blue/brightness";
	public int[] status = {0,0};
	private boolean mFinishThread = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.breathinglight_test);
		mTestBtn = (Button)findViewById(R.id.test_btn);
		successButton = (Button) findViewById(R.id.success_test);
		failButton = (Button) findViewById(R.id.fail_test);
		mTextText = (TextView)findViewById(R.id.test_text);
		mTextText.setText(R.string.two_color_pass);
		successButton.setEnabled(false);
		mTestBtn.setOnClickListener(this);
		successButton.setOnClickListener(this);
		failButton.setOnClickListener(this);
		mThread.start();
	}
	
	private Thread mThread = new Thread(new Runnable() {
		@Override
		public void run() {
			while( !isFinishing()){
				if(!mFinishThread){
			         try {

			             setBLNStatus(255,CONFIG_RED_PATH);
			             setBLNStatus(0,CONFIG_GREEN_PATH);
			        	 setBLNStatus(0,CONFIG_BLUE_PATH);
			             Thread.sleep(1000);
			        	 setBLNStatus(255,CONFIG_GREEN_PATH);
			        	 setBLNStatus(0,CONFIG_RED_PATH);
			        	 setBLNStatus(0,CONFIG_BLUE_PATH);
			             Thread.sleep(1000);
			        	 setBLNStatus(255,CONFIG_BLUE_PATH);
			             setBLNStatus(0,CONFIG_RED_PATH);
			             setBLNStatus(0,CONFIG_GREEN_PATH);
			             Thread.sleep(1000);
			             stopLed();
			         } catch(Exception e) {
			              Log.e("ThreeColorLight", "remote call for turn on led light failed.");
			         }
			    }
			}
		}
	});
	private void stopLed(){
		setBLNStatus(0,CONFIG_GREEN_PATH);
		setBLNStatus(0,CONFIG_RED_PATH);
		setBLNStatus(0,CONFIG_BLUE_PATH);
	}
	
	private void setBLNStatus(int status, String path) {
		File file = new File(path);;
		FileOutputStream fos;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			fos.write(String.valueOf(status).getBytes());
			fos.close();
		} catch (Exception e) {
			Log.e("TwoColorLights", "error: " + e);
		}
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mTestBtn.setText(R.string.flash_test_on);
		mFinishThread = true;
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
			
		case R.id.test_btn:
			if (mTestBtn.getText() == getString(R.string.flash_test_on)) {
				status[0]=1;
				mTestBtn.setText(R.string.flash_test_off);
				mFinishThread = false;
			}else if(mTestBtn.getText() == getString(R.string.flash_test_off)){
				status[1]=1;
				mTestBtn.setText(R.string.flash_test_on);
				mFinishThread = true;
			}
			if(status[0]==1 && status[1]==1){
				successButton.setEnabled(true);
			}
			break;
			
		default:
			break;
		}
	}
	
}
