/**
 * 
 */
package lovdream.android.cit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.os.SystemProperties;
import java.lang.InterruptedException;

public class PCBANfc extends Activity implements OnClickListener {
  

	private TextView mText;
	private Button successButton, failButton;
	private NfcAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_nfc);
		
		
		 mText = (TextView) findViewById(R.id.text);
	        mText.setText(R.string.nfc_test_loading);
			successButton = (Button) findViewById(R.id.success_test);
                        successButton.setEnabled(false);
			failButton = (Button) findViewById(R.id.fail_test);
			successButton.setOnClickListener(this);
			failButton.setOnClickListener(this);
			mAdapter = NfcAdapter.getDefaultAdapter(this);
	        
			if (mAdapter != null && mAdapter.isEnabled()) {
				mAdapter.disable();
			}
			
			new Thread(new Runnable() {

				@Override
				public void run() {
		 			// TODO Auto-generated method stub
				
				int result ;
				 try {
				synchronized(this){
						  wait(10000);	
				  SystemProperties.set("ctl.start", "nfc_check");

                  result = readFile("/persist/pn547_check")
 							.equals("0") ? R.string.result_pass
 							: R.string.result_fail;
                  Log.i("GSPNFC", "---------->"+result);
					Message msg = Message.obtain(mHandler, 0);
					msg.arg1 = result;
					mHandler.sendMessage(msg);
				  }
				 }catch(InterruptedException ex){                    
                       ex.printStackTrace();
		            }
				}
				
			}).start();
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);				
			if (mText != null) {
				mText.setText(msg.arg1);
				boolean success = false;
              if(msg.arg1 == R.string.result_pass){
            	  successButton.setEnabled(true);
            	  success = true;
              }	
              if(CITMain.CITtype == CITMain.PCBA_AUTO_TEST 
						|| CITMain.CITtype == CITMain.MACHINE_AUTO_TEST){
            	  if(success){
            		  successButton.performClick();
            	  }else{
            		  failButton.performClick();
            	  }
				}
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Bundle b = new Bundle();
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.success_test:
			b.putInt("test_result", 1);
			intent.putExtras(b);
			setResult(RESULT_OK, intent);
			finish();
			break;
			
		case R.id.fail_test:
			b.putInt("test_result", 0);
			intent.putExtras(b);
			setResult(RESULT_OK, intent);
			finish();
			break;

		default:
			
			break;
		}
	}

		public static String readFile(String filePath){   
			String res="";   
			try{   
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
				String str=null;
				while((str=br.readLine())!=null){
					res+=str;
				}  
	        }   
			catch(Exception e){   
	         e.printStackTrace();   
	        }   
	        return res;   
		}  
		
		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			
		}
		@Override
		protected void onDestroy() {
			super.onDestroy();
			
		}
		
		
	
}
