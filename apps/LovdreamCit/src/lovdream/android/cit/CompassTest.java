/**
 * 
 */
package lovdream.android.cit;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class CompassTest extends Activity {
	final static int MENU_QUIT = 1;
	CompassView view;
	private Button successButton, failButton; 
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.main);
		
		//表面屏幕自动翻转导致的错位问题
		//效果与AndroidManifest.xml 设置android:screenOrientation 属性一样
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		initUI();	
		//LocationManager myLocationManager=null;
		//myLocationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		//Location l = myLocationManager.getLastKnownLocation("network");
		//String url = l.getLatitude() + "," + l.getLongitude(); 这里获取到了精度和维度
	}

	private void initUI(){
		setContentView(R.layout.compass_test);
		try{
			view = (CompassView) findViewById(R.id.compass_view);
			//fix bug about update button by zzj start
			view.setSensorChangeListener(new CompassView.SensorChangeListener() {
				@Override
				public void updateState(boolean state) {
					// TODO Auto-generated method stub
					successButton.setEnabled(state);
				}
			});
			//fix bug about update button by zzj end
		}catch(Exception e){
			Log.e("CompassTest","cannot be cast to CompassView "+e.toString());
		}
		successButton = (Button) findViewById(R.id.success_test);
		failButton = (Button) findViewById(R.id.fail_test);
		
		successButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle b = new Bundle();
				Intent intent = new Intent();
				b.putInt("test_result", 1);
				intent.putExtras(b);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		failButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle b = new Bundle();
				Intent intent = new Intent();
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
		//fix bug 11366 by wt start
		if(view != null){
			view.removeSensorListener();
		}
		//fix bug 11366 by wt end
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		initUI();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initUI();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		failButton.performClick();
	}

	
		
}
