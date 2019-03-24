package lovdream.android.cit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.os.SystemProperties;

public class KeyTestDeputyBoard extends Activity {

	//private TextView camera;
	private TextView vUp;
	private TextView vDown;
	private TextView menu;
	private TextView search;
	private TextView back;
	private TextView home;
	private TextView currentView;
	private TextView headset;
	private TextView bluetooth;
	// private Button exit;
	private boolean flag;
	private boolean bAllPressed;
    boolean mIs_no_search_key;
    private int TEST_KEY_CNT;
    private int keyCnt[];
    private Button failTest, successTest;

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		this.setContentView(R.layout.keytest);
        mIs_no_search_key = true;//SystemProperties.get("ro.product.no_search_key").equals("true");      
        successTest = (Button) this.findViewById(R.id.success_test);
        failTest = (Button) this.findViewById(R.id.fail_test);
        successTest.setEnabled(false);
        successTest.setOnClickListener(new Button.OnClickListener() {
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
        failTest.setOnClickListener(new Button.OnClickListener() {
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
        //camera = (TextView) this.findViewById(R.id.camera);
		vUp = (TextView) this.findViewById(R.id.vup);
		vUp.setVisibility(View.GONE);
		vDown = (TextView) this.findViewById(R.id.vdown);
		vDown.setVisibility(View.GONE);
		menu = (TextView) this.findViewById(R.id.menu);
		headset = (TextView) this.findViewById(R.id.headset);
		headset.setVisibility(View.GONE);
		Log.d("CJ", "mIs_no_search_key="+mIs_no_search_key);
        if(!mIs_no_search_key)
    		search = (TextView) this.findViewById(R.id.search);
        else {
        	search = (TextView) this.findViewById(R.id.search);
			search.setVisibility(View.GONE);
		}
        	
		back = (TextView) this.findViewById(R.id.back);
		home = (TextView) this.findViewById(R.id.home);
		bluetooth = (TextView) this.findViewById(R.id.bluetooth);
		if(CITOneByOne.mIsDoubleScreen){
			bluetooth.setVisibility(View.VISIBLE);
		}else{
			bluetooth.setVisibility(View.GONE);
		}
		// exit = (Button) this.findViewById(R.id.exit);
        if(mIs_no_search_key && !CITOneByOne.mIsDoubleScreen)
        {
            TEST_KEY_CNT = 3;
            keyCnt = new int[TEST_KEY_CNT];   		    
            keyCnt[0] = keyCnt[1] = keyCnt[2] = 0;
        }        
        else
        {
            TEST_KEY_CNT = 4;
            keyCnt = new int[TEST_KEY_CNT]; 
		    keyCnt[0] = keyCnt[1] = keyCnt[2] = keyCnt[3] = 0;		
        }

        bAllPressed = false;
		flag = true;
		onAttachedToWindow();
		// exit.setOnClickListener(new OnClickListener() {

		// public void onClick(View v) {
		// if(flag)
		// {
		// getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG
		// );
		// flag = false;
		// }
		// finish();

		// }

		// });
	}

	public boolean onKeyDown(int i, KeyEvent keyevent) {
		return true;
	}

	public boolean onKeyUp(int keyCode, KeyEvent keyevent) {
		int i = 0;
		if (currentView != null)
			currentView.setBackgroundColor(-1);
		switch (keyCode) {
		/*case KeyEvent.KEYCODE_CAMERA:
			// camera.setBackgroundColor(R.drawable.green);
			camera.setTextColor(-1);
			keyCnt[0] = 1;
			currentView = camera;
			break;*/
		case KeyEvent.KEYCODE_MENU:
			menu.setTextColor(-1);
			keyCnt[0] = 1;
			currentView = menu;
			break;
		/*case KeyEvent.KEYCODE_VOLUME_UP:
			vUp.setTextColor(-1);
			keyCnt[1] = 1;
			currentView = vUp;
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			vDown.setTextColor(-1);
			keyCnt[2] = 1;
			currentView = vDown;
			break;*/
		case KeyEvent.KEYCODE_BACK:
			back.setTextColor(-1);
			keyCnt[1] = 1;
			currentView = back;
			break;
		case KeyEvent.KEYCODE_HOME:
			home.setTextColor(-1);
			keyCnt[2] = 1;
			currentView = home;
			break;
			
		/*case KeyEvent.KEYCODE_HEADSETHOOK:
			headset.setTextColor(-1);
			keyCnt[5] = 1;
			currentView = headset;
			break;
		
		case KeyEvent.KEYCODE_SEARCH:
			//search.setBackgroundColor(R.drawable.green); currentView = search;
			search.setTextColor(-1);
			keyCnt[6] = 1;
			currentView = search;
		 	break;*/
			
		/*case KeyEvent.KEYCODE_BLUETOOTH:
			bluetooth.setTextColor(-1);
			keyCnt[3] = 1;
			currentView = bluetooth;
			break;*/
		}

		for (i = 0; i < keyCnt.length; i++) {
			if (keyCnt[i] == 0) {
				break;
			}
		}
		if (i >= keyCnt.length) {
			if (flag) {
				getWindow().setType(
					WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG);
				flag = false;
			}
			successTest.setEnabled(true);
			if(CITMain.CITtype == CITMain.PCBA_AUTO_TEST 
					|| CITMain.CITtype == CITMain.MACHINE_AUTO_TEST){
				successTest.performClick();
			}
//			finish();
		}

		return true;
	}

	public void onAttachedToWindow() {
		if (flag) {
			getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		}
		super.onAttachedToWindow();
	}
}
