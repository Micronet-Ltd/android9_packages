package lovdream.android.cit;

import static android.provider.Settings.System.SCREEN_BRIGHTNESS;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;

public class ScreenTest extends Activity{
	
	private  IPowerManager mPower;
	private int mDefaultbrightnessMode;
	private int mDefaultbrightness;

	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		 int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
	                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
	                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
	                | WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES;
	    getWindow().addFlags(flags);
		setContentView(R.layout.screen_test_layout);
		mPower = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
		mDefaultbrightnessMode = Settings.System.getInt(getContentResolver(), SCREEN_BRIGHTNESS_MODE, SCREEN_BRIGHTNESS_MODE_MANUAL);
		mDefaultbrightness = Settings.System.getInt(getContentResolver(), SCREEN_BRIGHTNESS, 100);
	}
	
	private void setBrightness(int brightness) {
        try {
            mPower.setTemporaryScreenBrightnessSettingOverride(brightness);
        } catch (RemoteException ex) {
        }
    }
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Settings.System.putInt(getContentResolver(), SCREEN_BRIGHTNESS_MODE,mDefaultbrightnessMode);
		Settings.System.putInt(getContentResolver(), SCREEN_BRIGHTNESS,mDefaultbrightness);
		setBrightness(mDefaultbrightness);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Settings.System.putInt(getContentResolver(), SCREEN_BRIGHTNESS_MODE,SCREEN_BRIGHTNESS_MODE_MANUAL);
		Settings.System.putInt(getContentResolver(), SCREEN_BRIGHTNESS,255);
		setBrightness(255);
	}
}
