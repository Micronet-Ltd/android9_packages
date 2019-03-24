/**
 * 
 */
package lovdream.android.cit;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class BacklightTest extends Activity {
	
	private TextView backlightValue;
	private Button beginTest, exit, successButton, failButton; 
	int i = 19;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.backlight_test);
		myHandler.post(myRunnable);
		backlightValue = (TextView) findViewById(R.id.backlight_value);
		beginTest = (Button) findViewById(R.id.begin_test);
		beginTest.setVisibility(View.GONE);
		successButton = (Button) findViewById(R.id.success_test);
		failButton = (Button) findViewById(R.id.fail_test);
		successButton.setEnabled(false);
		
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
	
//    private void saveScreenBrightness(int paramInt){   
//        try{   
//          Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, paramInt);   
//        }   
//        catch (Exception localException){   
//        	localException.printStackTrace();   
//        }   
//    }   
//    
	 private void setScreenBrightness(int paramInt){   
        Window localWindow = getWindow();   
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();   
        float f = paramInt / 255.0F;   
        localLayoutParams.screenBrightness = f;   
        localWindow.setAttributes(localLayoutParams);   
	 }   
	 
	 Handler myHandler = new Handler();
	 Runnable myRunnable = new Runnable() {
		
		@Override		
		public void run() {
			// TODO Auto-generated method stub
			i = i+5;
			if (i >= 255) {
				successButton.setEnabled(true);
			}
			i = i>=255?20:i;
			setScreenBrightness(i);
			backlightValue.setText(String.valueOf(i));
			myHandler.postDelayed(myRunnable, 80);
		}
	};


}
