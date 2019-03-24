package lovdream.android.cit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.os.UserHandle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.util.Log;

import com.swfp.device.DeviceManager;
import com.swfp.device.DeviceManager.IFpCallBack;
import com.swfp.utils.Utils;
import com.swfp.utils.MessageType;



public class FingerPrintTest extends Activity{
	private FingerprintManager  mFingerprintManager;
	private Button failTest, successTest;
	private Vibrator mVibrator;

	DeviceManager mManager;

	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){

			Log.d("fingerprint","finger test handler,msg what:" + msg.what + " arg1:" + msg.arg1);

			if(msg.what != MessageType.FP_MSG_FINGER){
				return;
			}

			if(msg.arg1 == MessageType.FP_MSG_FINGER_WAIT_TOUCH){

			}else if(msg.arg1 == MessageType.FP_MSG_FINGER_TOUCH){
				vibrator();

				mManager.waitLeave();
			}else{
				if(msg.arg1 != MessageType.FP_MSG_IMG){
					return;
				}
				mManager.scanImage();
			}
		}
	};

	/*MyCallBack AuthenticationCallback;*/
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.fingerprint_test);
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
			mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
			mFingerprintManager = (FingerprintManager)getSystemService(FingerprintManager.class);
			/*
			 AuthenticationCallback = new MyCallBack();

            CancellationSignal cs = new CancellationSignal();
			mFingerprintManager.authenticate(null,cs,0,AuthenticationCallback,null,1000);
			*/
			
			if(!mFingerprintManager.isHardwareDetected()){
				failTest.performClick();
				return;
			}

			mManager = DeviceManager.getDeviceManager(getApplicationContext());
			mManager.registerFpCallBack(new IFpCallBack() {
				@Override
				public void sendMessageToClient(Message msg) {
					mHandler.sendMessage(msg);
				}
			});
		}

		@Override
		public void onResume(){
			super.onResume();

			mManager.checkConnectToServer();
			mManager.startImageMode();
		}

		@Override
		public void onPause(){
			super.onPause();

			mManager.stopImageMode();
			mManager.disConnect();
		}

		/*
		private class MyCallBack extends FingerprintManager.AuthenticationCallback{
			@Override
		    public void onAuthenticationError(int errMsgId, CharSequence errString) {
		    }
		    @Override
		    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
		    	vibrator();
		    }
		    @Override
		    public void onAuthenticationFailed() {
		    	vibrator();
		    }
		    @Override
		    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
		    	vibrator();
		    }
		}
		*/
		
		public void vibrator() {
			mVibrator.vibrate(100);
			successTest.setEnabled(true);
			successTest.performClick();
		}
}
