package lovdream.android.cit;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.SystemProperties;

public class EightPinTest extends Activity {

	public final static String TAG = "EightPinTest";
	private Button mButton, successButton, failButton;
	public static final byte[] LIGHTE_ON = { '1' };
    public static final byte[] LIGHTE_OFF = { '0' };
    
    private boolean p800 = SystemProperties.get("ro.product.name").equals("msm8939_64_p800")
    		||SystemProperties.get("ro.product.name").equals("msm8939_64_p803") || SystemProperties.get("ro.product.name").equals("msm8939_64_p805");
  
	/*  
	 *  private final static String PATH_1 ="/sys/class/ext_dev/function/pin6_en";// "/sys/class/ext_dev/function/ext_dev_3v3_enable";
	    private final static String PATH_2 = "/sys/class/ext_dev/function/pin9_en";//"/sys/class/ext_dev/function/ext_dev_5v_enable";
	    private final static String PATH_3 = "/sys/class/ext_dev/function/power_en"; //"/sys/class/ext_dev/function/ext_dev_pin_3";

  		
  		private final static String PATH_1 = "/sys/class/ext_dev/function/ext_dev_3v3_enable";
 	    private final static String PATH_2 = "/sys/class/ext_dev/function/ext_dev_5v_enable";
 	    private final static String PATH_3 = "/sys/class/ext_dev/function/ext_dev_pin_3";
  	    private final static String PATH_4 = "/sys/class/ext_dev/function/ext_dev_pin_4";
  	    private final static String PATH_5 = "/sys/class/ext_dev/function/identity_enable";
	
  */
    
 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flash_test);
		TextView tv = (TextView) findViewById(R.id.textView1);
		if(p800){
			tv.setText(getString(R.string.test_fourteenpin_prompt));
			this.setTitle(getResources().getText(R.string.test_fourteenpin));
		}else{
			tv.setText(getString(R.string.test_eightpin_prompt));
		}
		mButton = (Button) findViewById(R.id.flash_test);
		mButton.setVisibility(View.GONE);
		successButton = (Button) findViewById(R.id.success_test);
		failButton = (Button) findViewById(R.id.fail_test);
		mButton.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mButton.getText() == getString(R.string.flash_test_on)) {
					setPinStatus(true);
					mButton.setText(R.string.flash_test_off);
				}else if (mButton.getText() == getString(R.string.flash_test_off)) {
					setPinStatus(false);
					mButton.setText(R.string.flash_test_on);
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
		setPinStatus(true);
		mButton.setText(R.string.flash_test_on);
	}
	
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		setPinStatus(false);
	}
	
	private void setPinStatus(boolean status) {
		
		if(p800)
		{
			 String PATH_1 ="/sys/class/ext_dev/function/pin6_en";// "/sys/class/ext_dev/function/ext_dev_3v3_enable";
			 String PATH_2 = "/sys/class/ext_dev/function/pin9_en";//"/sys/class/ext_dev/function/ext_dev_5v_enable";
			 String PATH_3 = "/sys/class/ext_dev/function/power_en";
			
			changePinStatus(status, PATH_1);
			changePinStatus(status, PATH_2);
			changePinStatus(status, PATH_3);
		}else {
			
	         String PATH_1 = "/sys/class/ext_dev/function/ext_dev_3v3_enable";
	 	     String PATH_2 = "/sys/class/ext_dev/function/ext_dev_5v_enable";
	 	     String PATH_3 = "/sys/class/ext_dev/function/ext_dev_pin_3";
	  	     String PATH_4 = "/sys/class/ext_dev/function/ext_dev_pin_4";
	  	     String PATH_5 = "/sys/class/ext_dev/function/identity_enable";
			
			changePinStatus(status, PATH_1);
			changePinStatus(status, PATH_2);
			changePinStatus(status, PATH_3);
			changePinStatus(status, PATH_4);
			changePinStatus(status, PATH_5);
		}
	
    }

    private void changePinStatus(boolean status, String node) {
        try {
            byte[] ledData = status ? LIGHTE_ON : LIGHTE_OFF;
            FileOutputStream brightness = new FileOutputStream(node);
            brightness.write(ledData);
            brightness.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
