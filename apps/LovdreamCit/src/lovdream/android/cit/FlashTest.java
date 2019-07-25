/**
 * 
 */
package lovdream.android.cit;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.os.SystemProperties;
import android.hardware.camera2.CameraManager;
public class FlashTest extends Activity {
	private CameraManager cameraManager;
	private Button mButton, successButton, failButton;
	public static final byte[] LIGHTE_ON = { '1' };
    public static final byte[] LIGHTE_OFF = { '0' };
    public static boolean mIsMsm8x25q_v5l_f1 = SystemProperties.get("ro.product.name").equals("msm8x25q_v5l_f1");
    public static final byte[] F1_LIGHTE_ON = { '2','5','5'};
    public static final byte[] F1_LIGHTE_OFF = { '0' };
	public int[] status = {0,0};

    //add for Flash test by zj start
 //   private Camera mCamera = null;
//    private Parameters mParameters = null;
    //add for Flash test by zj end
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flash_test);
	    cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
		mButton = (Button) findViewById(R.id.flash_test);
		successButton = (Button) findViewById(R.id.success_test);
		successButton.setEnabled(false);
		failButton = (Button) findViewById(R.id.fail_test);
		mButton.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mButton.getText() == getString(R.string.flash_test_on)) {
					//enableFlash(true);
					setLEDStatus(true);
					mButton.setText(R.string.flash_test_off);
					status[0]=1;
				}else if (mButton.getText() == getString(R.string.flash_test_off)) {
					//enableFlash(false);
					setLEDStatus(false);
					mButton.setText(R.string.flash_test_on);
					status[1]=1;
				}
				if(status[0]==1 && status[1]==1){
					successButton.setEnabled(true);
				}
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
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//enableFlash(false);
		setLEDStatus(false);
		mButton.setText(R.string.flash_test_on);
	}
	
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//enableFlash(false);
		setLEDStatus(false);
	}
	
	private void enableFlash(boolean state){
		try {
			 cameraManager.setTorchMode("0", state);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void setLEDStatus(boolean status) {
	File file = new File ("sys/class/leds/led:torch_1/brightness");
        FileOutputStream red;
        try {
        	byte[] ledData = LIGHTE_OFF;
	        ledData = status ? LIGHTE_ON : LIGHTE_OFF;
	        red = new FileOutputStream(file);
            red.write(ledData);
            red.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
      }


	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	//	mCamera.release();//add for Flash test by zj
	}

}
