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

public class AllKeyTestPCBA extends Activity {

	private TextView mMenuView, mUpView, mBackView, mLeftView, mCenterView,
			mRightView, mGsmView, mDownView, mSimView, mCdmaView, mCView,
			mPhoneOffView, mOneView, mTwoView, mThreeView, mFourView,
			mFiveView, mSixView, mSevenView, mEightView, mNineView, mStarView,
			mZeroView, mJingView;
	private TextView successButton, failButton;
	private static int mColorNormal;
	private static int mColorPress;
	private boolean mIsA406 = SystemProperties.get("ro.product.name").startsWith("msm8916_64_a406");
	private boolean mIsW2014B = SystemProperties.get("ro.product.name").equals("msm7627a_l352_w2014b");
	private boolean mIsSamsungStyle = mIsA406 || mIsW2014B;
	
	private boolean mIsUpPress = false;
	private boolean mIsDownPress = false;
	private boolean mIsLeftPress = false;
	private boolean mIsRightPress = false;
	private boolean mIsCenterPress = false;
	private boolean mIsMenuPress = false;
	private boolean mIsBackPress = false;
	private boolean mIsHomePress = false;
	private boolean mIsSimPress = false;
	private boolean mIsPhonePress = false;
	private boolean mIsCPress = false;
	private boolean mIsPhoneOffPress = false;
	private boolean mIsOnePress = false;
	private boolean mIsTwoPress = false;
	private boolean mIsThreePress = false;
	private boolean mIsFourPress = false;
	private boolean mIsFivePress = false;
	private boolean mIsSixPress = false;
	private boolean mIsSevenPress = false;
	private boolean mIsEightPress = false;
	private boolean mIsNinePress = false;
	private boolean mIsStarPress = false;
	private boolean mIsZeroPress = false;
	private boolean mIsJingPress = false;

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		this.setContentView(R.layout.allkeytest);
		mMenuView = (TextView)findViewById(R.id.key_menu);
		//fix cit keytest by wt begin
		if(CITOneByOne.mUse2015Style){
			mMenuView.setText(R.string.key_recent_text);
		}
		//fix cit keytest by wt end
		mUpView = (TextView)findViewById(R.id.key_up);
		mBackView = (TextView)findViewById(R.id.key_back);
		mLeftView = (TextView)findViewById(R.id.key_left);
		mCenterView = (TextView)findViewById(R.id.key_center);
		mRightView = (TextView)findViewById(R.id.key_right);
		mGsmView = (TextView)findViewById(R.id.key_gsm);
		if(mIsSamsungStyle){
			mGsmView.setText(R.string.key_home_text);
		}
		mDownView = (TextView)findViewById(R.id.key_down);
		mSimView = (TextView)findViewById(R.id.key_sim);
		mCdmaView = (TextView)findViewById(R.id.key_cdma);
		if(mIsSamsungStyle){
			mCdmaView.setText(R.string.key_phone_text);
		}
		mCView = (TextView)findViewById(R.id.key_c);
		mPhoneOffView = (TextView)findViewById(R.id.key_phone_off);
		mOneView = (TextView)findViewById(R.id.key_one);
		mTwoView = (TextView)findViewById(R.id.key_two);
		mThreeView = (TextView)findViewById(R.id.key_three);
		mFourView = (TextView)findViewById(R.id.key_four);
		mFiveView = (TextView)findViewById(R.id.key_five);
		mSixView = (TextView)findViewById(R.id.key_six);
		mSevenView = (TextView)findViewById(R.id.key_seven);
		mEightView = (TextView)findViewById(R.id.key_eight);
		mNineView = (TextView)findViewById(R.id.key_nine);
		mStarView = (TextView)findViewById(R.id.key_start);
		mZeroView = (TextView)findViewById(R.id.key_zero);
		mJingView = (TextView)findViewById(R.id.key_jing);
		successButton = (TextView) findViewById(R.id.success_test);
		failButton = (TextView) findViewById(R.id.fail_test);
		
		mColorNormal = getResources().getColor(R.color.all_key_test_normal);
		mColorPress = getResources().getColor(R.color.all_key_test_press);
		
		mMenuView.setBackgroundColor(mColorNormal);
		mUpView.setBackgroundColor(mColorNormal);
		mBackView.setBackgroundColor(mColorNormal);
		mLeftView.setBackgroundColor(mColorNormal);
		mCenterView.setBackgroundColor(mColorNormal);
		mRightView.setBackgroundColor(mColorNormal);
		mGsmView.setBackgroundColor(mColorNormal);
		mDownView.setBackgroundColor(mColorNormal);
		mSimView.setBackgroundColor(mColorNormal);
		mCdmaView.setBackgroundColor(mColorNormal);
		mCView.setBackgroundColor(mColorNormal);
		mPhoneOffView.setBackgroundColor(mColorNormal);
		mOneView.setBackgroundColor(mColorNormal);
		mTwoView.setBackgroundColor(mColorNormal);
		mThreeView.setBackgroundColor(mColorNormal);
		mFourView.setBackgroundColor(mColorNormal);
		mFiveView.setBackgroundColor(mColorNormal);
		mSixView.setBackgroundColor(mColorNormal);
		mSevenView.setBackgroundColor(mColorNormal);
		mEightView.setBackgroundColor(mColorNormal);
		mNineView.setBackgroundColor(mColorNormal);
		mStarView.setBackgroundColor(mColorNormal);
		mZeroView.setBackgroundColor(mColorNormal);
		mJingView.setBackgroundColor(mColorNormal);
		successButton = (TextView) findViewById(R.id.success_test);
		failButton = (TextView) findViewById(R.id.fail_test);
		successButton.setEnabled(false);
		
		mMenuView.setVisibility(View.INVISIBLE);
		mBackView.setVisibility(View.INVISIBLE);
		mLeftView.setVisibility(View.INVISIBLE);
		mRightView.setVisibility(View.INVISIBLE);
		mGsmView.setVisibility(View.INVISIBLE);
		mSimView.setVisibility(View.INVISIBLE);
		mCdmaView.setVisibility(View.INVISIBLE);
		mTwoView.setVisibility(View.INVISIBLE);
		mThreeView.setVisibility(View.INVISIBLE);
		mFourView.setVisibility(View.INVISIBLE);
		mFiveView.setVisibility(View.INVISIBLE);
		mSixView.setVisibility(View.INVISIBLE);
		mSevenView.setVisibility(View.INVISIBLE);
		mEightView.setVisibility(View.INVISIBLE);
		mNineView.setVisibility(View.INVISIBLE);
		mZeroView.setVisibility(View.INVISIBLE);
		mJingView.setVisibility(View.INVISIBLE);
		
		mIsMenuPress = true;
		mIsBackPress = true;
		mIsLeftPress = true;
		mIsRightPress = true;
		mIsHomePress = true;
		mIsSimPress = true;
		mIsPhonePress = true;
		mIsTwoPress = true;
		mIsThreePress = true;
		mIsFourPress = true;
		mIsFivePress = true;
		mIsSixPress = true;
		mIsSevenPress = true;
		mIsEightPress = true;
		mIsNinePress = true;
		mIsZeroPress = true;
		mIsJingPress = true;
		
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
		onAttachedToWindow();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mIsUpPress = false;
		mIsDownPress = false;
		mIsLeftPress = false;
		mIsRightPress = false;
		mIsCenterPress = false;
	}

	@Override
	public boolean onKeyDown(int i, KeyEvent keyevent) {
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		//-------------1-------------
		case KeyEvent.KEYCODE_MENU:
			mMenuView.setBackgroundColor(mColorPress);
			mIsMenuPress = true;
			break;
			//fix cit keytest by wt begin
			/*
		case KeyEvent.KEYCODE_RECENT:
			mMenuView.setBackgroundColor(mColorPress);
			mIsMenuPress = true;
			break;
			*/
			//fix cit keytest by wt end
		case KeyEvent.KEYCODE_DPAD_UP:
			mUpView.setBackgroundColor(mColorPress);
			if(mIsUpPress){
				return false;
			}else{
				mIsUpPress = true;
			}
			break;
		case KeyEvent.KEYCODE_BACK:
			mBackView.setBackgroundColor(mColorPress);
			mIsBackPress = true;
			break;
			
		//-------------2-------------
		case KeyEvent.KEYCODE_DPAD_LEFT:
			mLeftView.setBackgroundColor(mColorPress);
			if(mIsLeftPress){
				return false;
			}else{
				mIsLeftPress = true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			mCenterView.setBackgroundColor(mColorPress);
			if(mIsCenterPress){
				return false;
			}else{
				mIsCenterPress = true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			mRightView.setBackgroundColor(mColorPress);
			if(mIsRightPress){
				return false;
			}else{
				mIsRightPress = true;
			}
			break;
			
		//-------------3-------------
		/*case KeyEvent.KEYCODE_CALL_GSM:
			mGsmView.setBackgroundColor(mColorPress);
			mIsHomePress = true;
			break;*/
		case KeyEvent.KEYCODE_HOME:
			mGsmView.setBackgroundColor(mColorPress);
			mIsHomePress = true;
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			mDownView.setBackgroundColor(mColorPress);
			if(mIsDownPress){
				return false;
			}else{
				mIsDownPress = true;
			}
			break;
			/*
		case KeyEvent.KEYCODE_SWITCH_SIM:
			mSimView.setBackgroundColor(mColorPress);
			mIsSimPress = true;
			break;
			*/
			
		//-------------4-------------
		/*case KeyEvent.KEYCODE_CALL_CDMA:
			mCdmaView.setBackgroundColor(mColorPress);
			mIsPhonePress = true;
			break;*/
		case KeyEvent.KEYCODE_CALL:
			mCdmaView.setBackgroundColor(mColorPress);
			mIsPhonePress = true;
			break;
		case KeyEvent.KEYCODE_DEL:
			mCView.setBackgroundColor(mColorPress);
			mIsCPress = true;
			break;
		case KeyEvent.KEYCODE_ENDCALL:
			mPhoneOffView.setBackgroundColor(mColorPress);
			mIsPhoneOffPress = true;
			break;
			
		//-------------5-------------
		case KeyEvent.KEYCODE_1:
			mOneView.setBackgroundColor(mColorPress);
			mIsOnePress = true;
			break;
		case KeyEvent.KEYCODE_2:
			mTwoView.setBackgroundColor(mColorPress);
			mIsTwoPress = true;
			break;
		case KeyEvent.KEYCODE_3:
			mThreeView.setBackgroundColor(mColorPress);
			mIsThreePress = true;
			break;
			
		//-------------6-------------
		case KeyEvent.KEYCODE_4:
			mFourView.setBackgroundColor(mColorPress);
			mIsFourPress = true;
			break;
		case KeyEvent.KEYCODE_5:
			mFiveView.setBackgroundColor(mColorPress);
			mIsFivePress = true;
			break;
		case KeyEvent.KEYCODE_6:
			mSixView.setBackgroundColor(mColorPress);
			mIsSixPress = true;
			break;
		
		//-------------7-------------
		case KeyEvent.KEYCODE_7:
			mSevenView.setBackgroundColor(mColorPress);
			mIsSevenPress = true;
			break;
		case KeyEvent.KEYCODE_8:
			mEightView.setBackgroundColor(mColorPress);
			mIsEightPress = true;
			break;
		case KeyEvent.KEYCODE_9:
			mNineView.setBackgroundColor(mColorPress);
			mIsNinePress = true;
			break;
			
		//-------------8-------------
		case KeyEvent.KEYCODE_STAR:
			mStarView.setBackgroundColor(mColorPress);
			mIsStarPress = true;
			break;
		case KeyEvent.KEYCODE_0:
			mZeroView.setBackgroundColor(mColorPress);
			mIsZeroPress = true;
			break;
		case KeyEvent.KEYCODE_POUND:
			mJingView.setBackgroundColor(mColorPress);
			mIsJingPress = true;
			break;
		}
		
		if(mIsUpPress && mIsDownPress && mIsLeftPress && mIsRightPress && mIsCenterPress
		   && mIsMenuPress && mIsBackPress && mIsHomePress && mIsSimPress && mIsPhonePress
		   && mIsCPress && mIsPhoneOffPress && mIsOnePress && mIsTwoPress && mIsThreePress && mIsFourPress 
		   && mIsFivePress && mIsSixPress && mIsSevenPress && mIsEightPress
		   && mIsNinePress && mIsStarPress && mIsZeroPress && mIsJingPress){
				successButton.setEnabled(true);
				successButton.setFocusable(true);
				failButton.setFocusable(true);
				if(CITMain.CITtype == CITMain.PCBA_AUTO_TEST 
						|| CITMain.CITtype == CITMain.MACHINE_AUTO_TEST){
					successButton.performClick();
				}
		}
		
		return true;
	}

	public void onAttachedToWindow() {
		getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
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
