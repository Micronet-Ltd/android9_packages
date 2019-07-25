/**
 * 
 */
package lovdream.android.cit;

import java.io.IOException;

import android.content.Intent;
import android.R.integer;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//import com.android.server.pm.ShutdownThread;

public class RebootTest extends Activity {
	
	private Button rebootTestButton , stopTestButton;
	private TextView timesLeftTextView;
	private TextView timesTextView;
	private TextView timesintervalTextView;
	private EditText timesEditText;
	private EditText timesintervalEditText;
//	private TextView infoTextView;
	private TextView infotitleTextView;
	private static int count;
	private static int interval = 180;
	private static boolean isTest;
	private Handler mHandler = new Handler();
	private Runnable rebootRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(count <= 0){
				return;
			}
			count -= 1;
			if (count == 0) {
				isTest = false;
				saveIsTest();
			}
			save();
			Log.d("CJ", "CIT----rebootTest");
			reboot();	
		}
	};
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Log.d("CJ", "onCreate1");
//		Intent isTestIntent = getIntent();
//		boolean a = isTestIntent.getBooleanExtra("mmmisTest", false);
//		if ((loadIsTest()==false) && (a==false)) {
//			finish();
//		}
		setContentView(R.layout.reboot_test);
		count = load();
		String string = "The remaining number of reboot:" + count;
		timesLeftTextView = (TextView) findViewById(R.id.times_left);
		timesTextView = (TextView) findViewById(R.id.times_left);
//		infoTextView = (TextView) findViewById(R.id.info);
		infotitleTextView = (TextView) findViewById(R.id.info_title);
		timesintervalTextView = (TextView) findViewById(R.id.timesintervalTextView);
		timesEditText = (EditText) findViewById(R.id.timesLeftEditText);
		timesintervalEditText = (EditText) findViewById(R.id.timesintervalEditText);
		timesEditText.setHint("5000");
		timesintervalEditText.setHint("180");
		timesLeftTextView.setText(string);
		rebootTestButton = (Button) findViewById(R.id.rebootTest);
		rebootTestButton.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				if (timesEditText.getText().toString().equals("")) {
					count = 5000;
				}else {
					count = Integer.parseInt(timesEditText.getText().toString());
				}
				if (timesintervalEditText.getText().toString().equals("")) {
					interval = 180;
				}else {
					interval = Integer.parseInt(timesintervalEditText.getText().toString());
				}
				isTest = true;
				saveIsTest();
				saveInterval();
				String string = "The remaining number of reboot:" + count;
				timesLeftTextView.setText(string);
				save();
				reboot();	
 			}
		});
		stopTestButton = (Button) findViewById(R.id.stopTest);
		stopTestButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				count = 0;
				isTest = false;
				saveIsTest();
				timesLeftTextView.setText("The remaining number of reboot:0");
				save();
//				reboot();
			}
		});
		
		if (count > 0) {
			mHandler.postDelayed(rebootRunnable, loadInterval()*1000);
		}
	}
	
	public void save(){
		SharedPreferences rebootTimes = getSharedPreferences("rebootTimes", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = rebootTimes.edit();
		editor.putInt("rebootTimes", count);
		editor.commit();
		
	}
	
	public void saveIsTest() {
		SharedPreferences mIsTest = getSharedPreferences("mIsTest", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = mIsTest.edit();
		editor.putBoolean("mIsTest", isTest);
		editor.commit();
	}
	
	public boolean loadIsTest() {
		SharedPreferences mIsTest = getSharedPreferences("mIsTest", Context.MODE_PRIVATE);
		isTest = mIsTest.getBoolean("mIsTest", false);
		return isTest;
	}
	
	public void saveInterval() {
		SharedPreferences minterval = getSharedPreferences("minterval", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = minterval.edit();
		editor.putInt("minterval", interval);
		editor.commit();
	}
	
	public int loadInterval() {
		SharedPreferences minterval = getSharedPreferences("minterval", Context.MODE_PRIVATE);
		interval = minterval.getInt("minterval", 180);
		return interval;
	}
	
	public int load(){
		SharedPreferences rebootTimes = getSharedPreferences("rebootTimes", Context.MODE_PRIVATE);
		count = rebootTimes.getInt("rebootTimes", 0);
		return count;
	}
	
	public void reboot() {
		Intent i = new Intent(Intent.ACTION_REBOOT);
		i.putExtra("nowait", 1);
		i.putExtra("interval", 1);
		i.putExtra("window", 0);
		sendBroadcast(i);
//		ShutdownThread.reboot(RebootTest.this,"test",true);
/*		ShutdownThread.rebootOrShutdown(true, null);*/
//		String cmd = "su -c reboot";
//		try {
//		    Runtime.getRuntime().exec(cmd);
//		} catch (IOException e) {
//		   // TODO Auto-generated catch block
//		   
//		}
	}
	
}
