package lovdream.android.cit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HotTFTest extends Activity implements OnClickListener{
	
	private TextView mTextView;
	private Button mBtnSucc, mBtnFail;
	private boolean mMounted = false;
	private boolean mHadTest = false;

	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.hottftest);
		mTextView = (TextView) findViewById(R.id.textView);
		mBtnSucc = (Button) findViewById(R.id.btnSuc);
		mBtnFail = (Button) findViewById(R.id.btnFail);
		mBtnSucc.setEnabled(false);
		
		mBtnSucc.setOnClickListener(this);
		mBtnFail.setOnClickListener(this);
		
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED); 
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED); 
        intentFilter.addDataScheme("file");  
        registerReceiver(mTFStateReceiver, intentFilter);
        
		
		if(Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_REMOVED)){
			mTextView.setText(R.string.insert_tf);
			mMounted = false;
		}else if(Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)){
			mTextView.setText(R.string.remove_tf);
			mMounted = true;
		}
		
        
	}
	
	private final BroadcastReceiver mTFStateReceiver = new BroadcastReceiver() {    
        @Override    
        public void onReceive(Context context, Intent intent) {    
			
            String action = intent.getAction();   
            if(action.equals(Intent.ACTION_MEDIA_MOUNTED)){
            	if(Environment.getExternalStorageState().equals(
        				android.os.Environment.MEDIA_MOUNTED)){
            		if(mHadTest == false){
	            			if(mMounted == false){
	            				mTextView.setText(R.string.remove_tf);
	            			}else{
	            					mTextView.setText(R.string.hot_tf_succes);
	        	            		mBtnSucc.setEnabled(true);
	        	            		mHadTest = true;
	            			}
            			}
            	}
            }else if(action.equals(Intent.ACTION_MEDIA_REMOVED) || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)){
            	if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_REMOVED) 
            			|| Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_BAD_REMOVAL)){
            		if(mHadTest == false){
	            		if(mMounted == false){
	        					mTextView.setText(R.string.hot_tf_succes);
	    	            		mBtnSucc.setEnabled(true);
	    	            		mHadTest = true;
	            		}else{
	            			mTextView.setText(R.string.insert_tf);
	            		}
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
		if(v == mBtnSucc){
			b.putInt("test_result", 1);
			intent.putExtras(b);
			setResult(RESULT_OK, intent);
			finish();
		}else if(v == mBtnFail){
			intent.putExtras(b);
			setResult(RESULT_OK, intent);
			finish();
		}
	}
}
