
package com.lovdream.factorykit.items;

import android.view.View;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.content.Context;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.os.SystemProperties;
import android.util.Log;
import android.os.Vibrator;

import java.util.ArrayList;

import com.lovdream.factorykit.R;
import com.lovdream.factorykit.TestItemBase;

public class KeyTest extends TestItemBase{

	public static final String TAG = "factorykit";

	ArrayList<Key> testKeys;
	private Vibrator mVibrator;

	public class Key{
		int keyCode;
		TextView textView;
		boolean isSupported;

		public Key(String keyName){
			keyCode = KeyEvent.keyCodeFromString(keyName);
			isSupported = keyCode > 0;

			if(isSupported){
				textView = new TextView(getActivity());
				textView.setTextSize(24);
				textView.setText(getKeyDisplayName(keyCode));
			}
		}
	}

	private String getKeyDisplayName(int keyCode){
		int resId = -1;
		switch(keyCode){
			case KeyEvent.KEYCODE_HOME:
				resId = R.string.key_name_home;
				break;
			case KeyEvent.KEYCODE_MENU:
				resId = R.string.key_name_menu;
				break;
			case KeyEvent.KEYCODE_BACK:
				resId = R.string.key_name_back;
				break;
			case KeyEvent.KEYCODE_VOLUME_UP:
				resId = R.string.key_name_volume_up;
				break;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				resId = R.string.key_name_volume_down;
				break;
			case KeyEvent.KEYCODE_CAMERA:
				resId = R.string.key_name_camera;
				break;
			//case KeyEvent.KEYCODE_SOS:
			case KeyEvent.KEYCODE_NAVIGATE_IN:
				resId = R.string.key_name_sos;
				break;
			//case KeyEvent.KEYCODE_PTT:
			case KeyEvent.KEYCODE_STEM_PRIMARY:
				resId = R.string.key_name_ptt;
				break;
			//case KeyEvent.KEYCODE_POLICE:
			case KeyEvent.KEYCODE_NAVIGATE_OUT:
				resId = R.string.key_name_police;
				break;
			case KeyEvent.KEYCODE_HEADSETHOOK:
				resId = R.string.key_name_headsethook;
				break;
			case KeyEvent.KEYCODE_FUNCTION:
				resId = R.string.key_name_fn;
				break;
			case KeyEvent.KEYCODE_APP_SWITCH:
				resId = R.string.key_name_app_switch;
				break;
			case KeyEvent.KEYCODE_F1:
				resId = R.string.key_name_f1;
				break;
			case KeyEvent.KEYCODE_F2:
				resId = R.string.key_name_f2;
				break;
			case KeyEvent.KEYCODE_F3:
				resId = R.string.key_name_f3;
				break;
			case KeyEvent.KEYCODE_MEDIA_RECORD:
				resId = R.string.key_name_media_record;
				break;
			case KeyEvent.KEYCODE_MEDIA_REWIND:
				resId = R.string.key_name_media_rewind;
				break;
			case KeyEvent.KEYCODE_DVR:
				resId = R.string.key_name_sound_record;
				break;
		}
		return resId > 0 ? getActivity().getString(resId) : KeyEvent.keyCodeToString(keyCode);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyevent) {
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent keyevent) {
		Log.d(TAG,"in key test,keyevent:" + keyevent);

		if(mVibrator != null){
			mVibrator.vibrate(200);
		}

		if((testKeys == null) || (testKeys.isEmpty())){
			return true;
		}

		for(Key key : testKeys){
			if(key.keyCode == keyCode){
				key.textView.setVisibility(View.INVISIBLE);
				testKeys.remove(key);
				break;
			}
		}

		if(testKeys.isEmpty()){
			postSuccess();
		}
		return true;
	}

	@Override
	public String getKey(){
		return "key_test";
	}

	@Override
	public String getTestMessage(){
		return getActivity().getString(R.string.key_test_mesg);
	}

	@Override
	public void onStartTest(){
		String keyNames[] = getParameter("keyCode");
		if(keyNames == null){
			Toast.makeText(getActivity(),R.string.load_config_error,Toast.LENGTH_SHORT);
			return;
		}

		testKeys = new ArrayList<Key>();

		for(String keyName : keyNames){
			Key key = new Key(keyName);
			if(key.isSupported){
				testKeys.add(key);
			}
		}

		mVibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		SystemProperties.set("sys.cit_keytest","true");
	}

	@Override
	public void onStopTest(){
		SystemProperties.set("sys.cit_keytest","false");
	}

	@Override
	public View getTestView(LayoutInflater inflater){
		Context context = getActivity();
		LinearLayout root = new LinearLayout(context);
		root.setOrientation(LinearLayout.VERTICAL);

		if((testKeys != null) && (!testKeys.isEmpty())){
			for(Key key : testKeys){
				root.addView(key.textView);
			}
		}

		return root;
	}
}
