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

public class KeyTest_old extends Activity {

	private TextView camera;
	private TextView vUp;
	private TextView vDown;
	private TextView menu;
	private TextView search;
	private TextView back;
	private TextView home;
	private TextView currentView;
	private TextView headset;
	private TextView bluetooth;
	private TextView funskey;
	private TextView leftDown;
	
	
	
	private boolean flag;
	private boolean bAllPressed;
    boolean mIs_no_search_key;
    private int TEST_KEY_CNT;
    private int keyCnt[];
    private Button failTest, successTest;
    boolean mIsA538U = SystemProperties.get("ro.product.name").equals("msm8916_64_a538u");
    boolean mIsA538 = SystemProperties.get("ro.product.name").equals("msm8916_64_a538");
    boolean mS550 = true;//SystemProperties.get("ro.product.name").equals("msm8939_64_s550");
    boolean P805 = SystemProperties.get("ro.product.name").equals("msm8939_64_p805");
    boolean P803 = SystemProperties.get("ro.product.name").equals("msm8939_64_p803");
    boolean P800 = SystemProperties.get("ro.product.name").equals("msm8939_64_p800");
    boolean mIsA420 = SystemProperties.get("ro.product.name").equals("msm8916_64_a420");
    boolean mIsHasFKey=P803 || P800 || P805;
    boolean mIsHaveTwoLeftKey = mIsA538 || mIsA538U;
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
        
		camera = (TextView) this.findViewById(R.id.camera);
        	leftDown = (TextView) this.findViewById(R.id.left_down);
        if(mIsHaveTwoLeftKey){
        	camera.setVisibility(View.VISIBLE);
        	camera.setText(getString(R.string.key_left_up_text));
        	leftDown.setVisibility(View.VISIBLE);
        }else{
        	camera.setVisibility(View.GONE);
        	leftDown.setVisibility(View.GONE);
        }

		vUp = (TextView) this.findViewById(R.id.vup);
		vDown = (TextView) this.findViewById(R.id.vdown);
		menu = (TextView) this.findViewById(R.id.menu);
		//fix cit keytest by wt begin
		if(mIsA420){
			menu.setText(R.string.recent);
		}
		//fix cit keytest by wt end
		headset = (TextView) this.findViewById(R.id.headset);
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
		//if(CITOneByOne.mIsDoubleScreen){
		//	bluetooth.setVisibility(View.VISIBLE);
		//}else{
			bluetooth.setVisibility(View.GONE);
		//}
		funskey =  (TextView) this.findViewById(R.id.funs);
		if(CITOneByOne.HasFunsKey){
			funskey.setVisibility(View.VISIBLE);
		}else{
			funskey.setVisibility(View.GONE);
		}
		
		

		
		if(mIsHasFKey)
		{
		//	f1View.setVisibility(View.VISIBLE);
			
		//	f2View.setVisibility(View.VISIBLE);
			
			leftDown.setVisibility(View.VISIBLE);
        	leftDown.setText("F1");
        	bluetooth.setVisibility(View.VISIBLE);
        	bluetooth.setText("F2");
		}
		
		if(mS550){
			camera.setVisibility(View.VISIBLE);
        	leftDown.setVisibility(View.VISIBLE);
        	leftDown.setText("SOS");
        	bluetooth.setVisibility(View.VISIBLE);
        	bluetooth.setText("PTT");
        	funskey.setVisibility(View.VISIBLE);
        	funskey.setText("POLICE");
		}
		
        if(mIs_no_search_key || CITOneByOne.mIsDoubleScreen)
        {
            TEST_KEY_CNT = 6;
            keyCnt = new int[TEST_KEY_CNT];   		    
            keyCnt[0] = keyCnt[1] = keyCnt[2] = keyCnt[3] = keyCnt[4] = keyCnt[5] = 0;
        }        
        if(CITOneByOne.HasFunsKey)
        {
            TEST_KEY_CNT = 7;
            keyCnt = new int[TEST_KEY_CNT]; 
		    keyCnt[0] = keyCnt[1] = keyCnt[2] = keyCnt[3] = keyCnt[4] = keyCnt[5] = keyCnt[6] = 0;		
        }
	 if(mIsHaveTwoLeftKey){
        	TEST_KEY_CNT = 8;
            keyCnt = new int[TEST_KEY_CNT]; 
		    keyCnt[0] = keyCnt[1] = keyCnt[2] = keyCnt[3] = keyCnt[4] = keyCnt[5] = keyCnt[6] = keyCnt[7] = 0;
        }

	 if(mS550){
		 TEST_KEY_CNT = 10;
         keyCnt = new int[TEST_KEY_CNT]; 
		 keyCnt[0] = keyCnt[1] = keyCnt[2] = keyCnt[3] = keyCnt[4] = keyCnt[5] = keyCnt[6] = keyCnt[7] = keyCnt[8] = keyCnt[9] = 0;
	 }
	 
	 if(mIsHasFKey)
	 {
		 TEST_KEY_CNT = 9;
         keyCnt = new int[TEST_KEY_CNT]; 
		 keyCnt[0] = keyCnt[1] = keyCnt[2] = keyCnt[3] = keyCnt[4] = keyCnt[5] =  keyCnt[7] = keyCnt[8] = 0;
		 
		 keyCnt[6] = 1;
	 }
	 
        bAllPressed = false;
		flag = true;
	//	onAttachedToWindow();
	}

	public boolean onKeyDown(int i, KeyEvent keyevent) {
		return true;
	}

	public boolean onKeyUp(int keyCode, KeyEvent keyevent) {
		int i = 0;
		android.util.Log.i("....zzj","---------keyCode-----"+keyCode);
		switch (keyCode) {
		case KeyEvent.KEYCODE_CAMERA:
			camera.setVisibility(View.INVISIBLE);
			keyCnt[6] = 1;
			currentView = camera;
			break;
		case KeyEvent.KEYCODE_FOCUS:
			leftDown.setVisibility(View.INVISIBLE);
			keyCnt[7] = 1;
			currentView = leftDown;
			break;
		case KeyEvent.KEYCODE_MENU:
			menu.setVisibility(View.INVISIBLE);
			keyCnt[0] = 1;
			currentView = menu;
			break;
		//fix cit keytest by wt begin
		
		//case KeyEvent.KEYCODE_RECENT:
		case KeyEvent.KEYCODE_APP_SWITCH:
			menu.setVisibility(View.INVISIBLE);
			keyCnt[0] = 1;
			currentView = menu;
			break;
			//fix cit keytest by wt end
		case KeyEvent.KEYCODE_VOLUME_UP:
			vUp.setVisibility(View.INVISIBLE);
			keyCnt[1] = 1;
			currentView = vUp;
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			vDown.setVisibility(View.INVISIBLE);
			keyCnt[2] = 1;
			currentView = vDown;
			break;
		case KeyEvent.KEYCODE_BACK:
			back.setVisibility(View.INVISIBLE);
			keyCnt[3] = 1;
			currentView = back;
			break;
		case KeyEvent.KEYCODE_HOME:
			home.setVisibility(View.INVISIBLE);
			keyCnt[4] = 1;
			currentView = home;
			break;
			
		case KeyEvent.KEYCODE_HEADSETHOOK:
			headset.setVisibility(View.INVISIBLE);
			keyCnt[5] = 1;
			currentView = headset;
			break;
               //add f1f2 key by zzj start
	         case KeyEvent.KEYCODE_F1:
                        leftDown.setVisibility(View.INVISIBLE);
                        keyCnt[7] = 1;
                        currentView = leftDown;
                        break;
               //add f1f2 key by zzj end 
                case KeyEvent.KEYCODE_F2:
                        bluetooth.setVisibility(View.INVISIBLE);
                        keyCnt[8] = 1;
                        currentView = bluetooth;
                        break;	 	
			
		case KeyEvent.KEYCODE_SOS:
			leftDown.setVisibility(View.INVISIBLE);
			keyCnt[7] = 1;
			currentView = leftDown;
			break;
		
		case KeyEvent.KEYCODE_PTT:
			bluetooth.setVisibility(View.INVISIBLE);
			keyCnt[8] = 1;
			currentView = bluetooth;
			break;
			
		case KeyEvent.KEYCODE_POLICE:
			funskey.setVisibility(View.INVISIBLE);
			keyCnt[9] = 1;
			currentView = funskey;
			break;
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
		}

		return true;
	}

	public void onAttachedToWindow() {
		if (flag) {
			getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		}
	/*	add for cit key test by pc,start
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.flags &= (~WindowManager.LayoutParams.FLAG_CIT_HOME_TEST);
		getWindow().setAttributes(attrs);
		int i=WindowManager.LayoutParams.FLAG_CIT_HOME_TEST;
		android.util.Log.i("....zzj","------i--------"+i);
		android.util.Log.i("....zzj","-----attrsi--------"+attrs);
		add for cit key test by pc,end*/
		super.onAttachedToWindow();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	SystemProperties.set("sys.cit_keytest","false");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SystemProperties.set("sys.cit_keytest","true");
	}
}
