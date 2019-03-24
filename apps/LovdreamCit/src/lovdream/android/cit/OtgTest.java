package lovdream.android.cit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageEventListener;
import android.os.storage.VolumeInfo;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class OtgTest extends Activity implements View.OnHoverListener{
	
	private TextView txtStatus ;
	private Button btnSucc ,btnFail ;
	private StorageManager mStorageManager;
	private boolean otgTest = false;

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.fmtest);
		
		TextView tv = (TextView) findViewById(R.id.test_name);
		tv.setText(getString(R.string.test_otg));
		txtStatus = (TextView) findViewById(R.id.txtStatus);
		txtStatus.setText(getString(R.string.test_otg_prompt));
		btnSucc = (Button) findViewById(R.id.btnSuc); 
		btnFail = (Button) findViewById(R.id.btnFail);

/*		IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        filter.addDataScheme("file");
        registerReceiver(mReceiver, filter);*/

		// add this for otg not recognized by zj.0013542 start
//		(findViewById(R.layout.fmtest)).setOnHoverListener(this);
		btnSucc.setOnHoverListener(this);
		btnFail.setOnHoverListener(this);
		btnSucc.setClickable(false);
		btnSucc.getBackground().setAlpha(100);
		mStorageManager = getSystemService(StorageManager.class);
        mStorageManager.registerListener(mListener);
		// add this for otg not recognized by zj.0013542 end

        btnFail.setOnClickListener(onClick);
        btnSucc.setOnClickListener(onClick);
	}
	
	View.OnClickListener onClick = new View.OnClickListener() {
		public void onClick(View v) {
			Bundle b = new Bundle();
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.btnSuc:
				b.putInt("test_result", 1);
				intent.putExtras(b);
				setResult(RESULT_OK, intent);
				finish();
				break;
			case R.id.btnFail:
				b.putInt("test_result", 0);
				intent.putExtras(b);
				setResult(RESULT_OK, intent);
				finish();
				break;
			default:
				break;
			}
		}
	};
	
	protected void onResume() {
		super.onResume();
		updateUI();
	};

	// add this for otg not recognized by zj.0013542 start
	private final StorageEventListener mListener = new StorageEventListener() {

		@Override
		public void onVolumeStateChanged(VolumeInfo vol, int oldState, int newState) {
			if(vol.getType() == VolumeInfo.TYPE_PUBLIC) {
				if (vol.getPath() == null) return;
				if(vol.getDisk() != null && vol.getDisk().isUsb()){
					OtgStorageExist(vol);
				}
			}
		}
	};
	// add this for otg not recognized by zj.0013542 end

/*	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			updateUI();
		}
	};*/
	
	private void updateUI(){
		if(/*externalOtgStorageExist()*/otgTest){
			btnSucc.setClickable(true);
			btnSucc.getBackground().setAlpha(255);
			txtStatus.setText(getString(R.string.test_otg_pass));
		}else{
			btnSucc.setClickable(false);
			btnSucc.getBackground().setAlpha(100);
			txtStatus.setText(getString(R.string.test_otg_prompt));
		}
	}
	
/*	public boolean externalOtgStorageExist() {
        boolean ret = false;

        StorageManager mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        try {
	        if (mStorageManager.getVolumeState("/storage/usbotg").equals(
	                android.os.Environment.MEDIA_MOUNTED)) {
	            ret = true;
	        }
        } catch (Exception e) {
        	
        }
        return ret;
    }*/

	// add this for otg not recognized by zj.0013542 start
	private void OtgStorageExist(VolumeInfo vol) {

        switch (vol.getState()) {
		case VolumeInfo.STATE_UNMOUNTED:
		case VolumeInfo.STATE_REMOVED:
			otgTest = false;
			updateUI();
			break;
        case VolumeInfo.STATE_MOUNTED:
        case VolumeInfo.STATE_MOUNTED_READ_ONLY:
            otgTest = true;
            updateUI();
            break;

		default:
			break;
		}

	}
	// add this for otg not recognized by zj.0013542 end

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
        if(mListener != null){
           mStorageManager.unregisterListener(mListener);
		}

	}

	@Override
	public boolean onHover(View v, MotionEvent event) {
		if(MotionEvent.ACTION_HOVER_MOVE == event.getAction()) {
			btnSucc.setClickable(true);
			btnSucc.getBackground().setAlpha(255);
			txtStatus.setText(getString(R.string.test_otg_pass));
		}
		return true;
	}
}
