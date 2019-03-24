package lovdream.android.cit;



import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.KeyEvent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioSystem;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;

import com.lovdream.factorykit.libs.FmManager;

public class FMTest extends Activity implements Runnable{
	
	final static String FREQ_CHANGE = "com.thunderst.radio.fmchange";
	private boolean mHeadsetExist = false ;
	private TextView txtStatus ;
	private Button btnSucc ,btnFail ;
	Button searchButton;
	String now ;
	String kmz;

	AudioManager mAudioManager = null;
	FmManager mFmManager = null;

	boolean forceHeadset = false;
	boolean mRunning = false;

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				txtStatus.setText(new Float(mFmManager.getFrequency() / 1000f)
						.toString() + "MHZ");
				break;

			default:
				break;
			}
		};
	};

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.fmtest);
		now=getResources().getString(R.string.fm_freqnow);
		kmz=getResources().getString(R.string.fm_khz);
		txtStatus = (TextView) findViewById(R.id.txtStatus);
		btnSucc = (Button) findViewById(R.id.btnSuc); 
		btnFail = (Button) findViewById(R.id.btnFail);
		
		/*
		IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(FREQ_CHANGE);
        registerReceiver(mReceiver, filter);
		*/
        btnFail.setOnClickListener(onClick);
        btnSucc.setOnClickListener(onClick);

		searchButton = (Button) findViewById(R.id.fm_search);
		searchButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				if (mAudioManager.isWiredHeadsetOn()) {
					mFmManager.searchUP();
				} else {
                    txtStatus.setText(R.string.headset_plug_warning);
				}
			}
		});

		mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		mFmManager = new FmManager(this, mHandler);

		setAudio();

		if (!mRunning){
			mHandler.postDelayed(this,0);
		}
	}
	
	View.OnClickListener onClick = new View.OnClickListener() {
		public void onClick(View v) {
			Bundle b = new Bundle();
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.btnSuc:
				Log.i("FMTest", "check button start");
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
		/*
		if (mHeadsetExist) {
			Intent send = new Intent("com.thunderst.radio.service.trunon");
		send.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
			if(CITMain.CITtype == CITMain.MACHINE_AUTO_TEST){
				send.putExtra("freq",101.2);
			}else{
    		send.putExtra("freq",98.7);
			}
    		sendBroadcast(send);
		}else{
			txtStatus.setText(R.string.headset_plug_warning);
			btnSucc.setEnabled(false);
		}
		*/
	};
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
                if (intent.getIntExtra("state", 0) == 0) {
                	if (mHeadsetExist) {
                		sendBroadcast(new Intent("com.thunderst.radio.service.trunoff.and.ondestroyservice"));
					}
                    mHeadsetExist = false;
                    txtStatus.setText(R.string.headset_plug_warning);
                    btnSucc.setEnabled(false);
                    //fm_seek
                } else {
                    mHeadsetExist = true;
                    if(CITMain.CITtype == CITMain.MACHINE_AUTO_TEST){
                    	txtStatus.setText(now+"101.2"+kmz);
                    	btnSucc.setEnabled(true);
        			}else{
            		txtStatus.setText(now+"98.7"+kmz);
            		btnSucc.setEnabled(true);
        			}
            		Intent send = new Intent("com.thunderst.radio.service.trunon");
			send.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
			if(CITMain.CITtype == CITMain.MACHINE_AUTO_TEST){
				send.putExtra("freq",101.2);
			}else{
    		send.putExtra("freq",98.7);
			}
            		FMTest.this.sendBroadcast(send);
                }
			}else if(FREQ_CHANGE.equals(action)){
        		int fm = intent.getIntExtra("fm",0);
        		if (fm == 1) {
        			if(CITMain.CITtype == CITMain.MACHINE_AUTO_TEST){
        				txtStatus.setText(now+"101.2"+kmz);
        			}else{
        				txtStatus.setText(now+"98.7"+kmz);
        			}
				} else{
					txtStatus.setText(now +((double)fm)/1000+ kmz);
				} 
        		btnSucc.setEnabled(true);
            }
		}
	};
	@Override
	protected void onDestroy() {
		Log.i("FMTest", "onDestroy start");
		// TODO Auto-generated method stub
		super.onDestroy();
		/*
		Intent i=new Intent("com.thunderst.radio.service.trunoff.and.ondestroyservice");
		i.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
		sendBroadcast(i);
		unregisterReceiver(mReceiver);
		*/
		Log.i("FMTest", "onDestroy end");
		closeFM();
	}
	
	public boolean onKeyDown(int i, KeyEvent keyevent) {
		boolean flag;
		if (i == KeyEvent.KEYCODE_BACK ) {
			flag = true;
			Bundle bundle = new Bundle();
			Intent intent = new Intent();
			bundle.putInt("test_result", 2);
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			this.finish();
			
		}
		else {
			flag = false;
		}
		return flag;
	}

	public void closeFM() {

		mFmManager.closeFM();
		mRunning = false;
		if (forceHeadset) {
			AudioSystem.setDeviceConnectionState(
					AudioSystem.DEVICE_OUT_WIRED_HEADSET,
					AudioSystem.DEVICE_STATE_UNAVAILABLE, "","");
			AudioSystem.setForceUse(AudioSystem.FOR_MEDIA,
					AudioSystem.FORCE_NONE);
		}
	}

	public void setAudio() {

		mAudioManager.setMode(AudioManager.MODE_NORMAL);
		// Force headset's check
		if (forceHeadset) {
			AudioSystem.setDeviceConnectionState(
					AudioSystem.DEVICE_OUT_WIRED_HEADSET,
					AudioSystem.DEVICE_STATE_AVAILABLE, "","");
			AudioSystem.setForceUse(AudioSystem.FOR_MEDIA,
					AudioSystem.FORCE_WIRED_ACCESSORY);
			mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC,
					AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
		}
		float ratio = 0.8f;

		mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,
				(int) (ratio * mAudioManager
						.getStreamMaxVolume(AudioManager.STREAM_ALARM)), 0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				(int) (ratio * mAudioManager
						.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 0);
		mAudioManager
				.setStreamVolume(
						AudioManager.STREAM_VOICE_CALL,
						(int) (ratio * mAudioManager
								.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)),
						0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF,
				(int) (ratio * mAudioManager
						.getStreamMaxVolume(AudioManager.STREAM_DTMF)), 0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,
				(int) (ratio * mAudioManager
						.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)),
				0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_RING,
				(int) (ratio * mAudioManager
						.getStreamMaxVolume(AudioManager.STREAM_RING)), 0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,
				(int) (ratio * mAudioManager
						.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)), 0);
	}

	@Override
	public void run() {
		mRunning = true;
		mFmManager.openFM();
	};
}
