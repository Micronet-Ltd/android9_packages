package lovdream.android.cit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import junit.framework.Test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
//import android.os.IHardwareService;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class ButtonLights extends Activity {
	private String TAG = "ButtonLights";
	private Button turnOn ,turnOff, successButton, failButton;
	private boolean lightON = true;
	PowerManager pm;
	private static boolean exit = false;
//	IHardwareService mLight;

//the follow Field add by zj
	private File mFile;
	private String buttonLights;

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		exit = true;
//		pm.turnOnKeyboardLight();
//		try {
//            mLight.setButtonLightEnabled(false);
//        } catch(RemoteException e) {
//            Log.e(TAG, "remote call for turn off button light failed.");
//        }
		setBrightness(false);//add by zj
	}
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (exit == true) {
					return;
				}
				if (lightON) {
//					pm.turnOffKeyboardLight();
//					try {
//			            mLight.setButtonLightEnabled(false);
//			        } catch(RemoteException e) {
//			            Log.e(TAG, "remote call for turn off button light failed.");
//			        }
					setBrightness(false);//add by zj
				}else {
//					pm.turnOnKeyboardLight();
//					try {
//			            mLight.setButtonLightEnabled(true);
//			        } catch(RemoteException e) {
//			            Log.e(TAG, "remote call for turn on button light failed.");
//			        }
					setBrightness(true);//add by zj
				}
				lightON = !lightON;
				mHandler.sendEmptyMessageDelayed(0, 500);
				break;

			default:
				break;
			}
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.button_lights);
		turnOn = (Button) findViewById(R.id.turn_on);
		turnOff = (Button) findViewById(R.id.turn_off);
		successButton = (Button) findViewById(R.id.success_test);
		failButton = (Button) findViewById(R.id.fail_test);
		turnOff.setVisibility(View.GONE);
		turnOn.setVisibility(View.GONE);
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		mLight = IHardwareService.Stub.asInterface(ServiceManager.getService("hardware"));
		setBrightness(false);//add by zj

		turnOn.setOnClickListener(new Button.OnClickListener() {	
			@Override
			public void onClick(View v) {
//				try {
//		            mLight.setButtonLightEnabled(true);
//		        } catch(RemoteException e) {
//		            Log.e(TAG, "remote call for turn on button light failed.");
//		        }
				setBrightness(true);//add by zj
			}
		});
		turnOff.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				try {
//		            mLight.setButtonLightEnabled(false);
//		        } catch(RemoteException e) {
//		            Log.e(TAG, "remote call for turn off button light failed.");
//		        }
				setBrightness(false);//add by zj
			}
		});
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
		exit = false;
		mHandler.sendEmptyMessage(0);
	}

/**
 * add by zj
 * @param flag [true] open buttonLight,[false] close buttonLight;
 */
	private void setBrightness(boolean flag){
		try {
			if (mFile == null)
			mFile = new File("/sys/class/leds/button-backlight/brightness");

			FileOutputStream fos = new FileOutputStream(mFile);
			buttonLights = flag ? "255": "0";
			fos.write(buttonLights.getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			Log.i(TAG, "open Button Lights error  : "+e.getMessage());
		}
	}

//	private void setDimButtons(boolean dimButtons) {
//        Window window = getWindow();
//        WindowManager.LayoutParams layoutParams = window.getAttributes();
//        float val = dimButtons ? 0 : -1;
//        try {
//            Field buttonBrightness = layoutParams.getClass().getField(
//                    "buttonBrightness");
//            Log.d("CJ", "buttonBrightness="+buttonBrightness);
//            buttonBrightness.set(layoutParams, val);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
////        layoutParams.buttonBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF;
//        window.setAttributes(layoutParams);
//    }
	
}
