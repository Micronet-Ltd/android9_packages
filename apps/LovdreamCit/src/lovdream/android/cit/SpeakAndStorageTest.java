package lovdream.android.cit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.storage.VolumeInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SpeakAndStorageTest extends Activity {

	private Button successButton, failButton;
	AudioManager am;
	int nCurrentMusicVolume;
	private MediaPlayer mp;
	private TextView textView,textView2;
	private TextView textView3;
	private static final boolean mIsA406_f16 = android.os.SystemProperties.get("ro.product.name").equals("msm8916_a406_f16");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.speak_storage_test);
		successButton = (Button) findViewById(R.id.success_test);
		failButton = (Button) findViewById(R.id.fail_test);
		textView = (TextView) findViewById(R.id.textView1);
		//textView2 = (TextView) findViewById(R.id.textView2);
		textView3 = (TextView) findViewById(R.id.textView3);
		
		if(mIsA406_f16){
			textView3.setVisibility(View.GONE);
		}
		successButton.setEnabled(false);
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
	
	
	public String getPath(Context context){
		StorageManager sm =context.getSystemService(StorageManager.class);
		final List<VolumeInfo> vols = sm.getVolumes();
	    VolumeInfo   sdcardVolume = null ;
	    for (VolumeInfo volumeInfo : vols) {
	  	  if(volumeInfo.getDisk() != null && volumeInfo.getDisk().isSd())
    		  sdcardVolume = volumeInfo;
		}
	    if(sdcardVolume!= null) {
	    	   String sdcardPath = sdcardVolume.getPath()==null ? null :sdcardVolume.getPath().toString();
	    	   return sdcardPath;
	    	   }
		return null;
	}
	
	private void playMusic(Context context){
		boolean flag = false;
		int id = R.string.speak_test_message;
		am = (AudioManager) getSystemService("audio");
		String pathMusic;
		int i = am.getStreamMaxVolume(3);
		int j = am.getStreamVolume(3);
		nCurrentMusicVolume = j;
		am.setStreamVolume(3, i, 0);
		//未识别到T卡
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_REMOVED) || Environment.getExternalStorageState().equals(
						android.os.Environment.MEDIA_UNMOUNTED)) {
			mp = MediaPlayer.create(this, R.raw.test);
			id = R.string.speak_test_hasnot_sdcard;
			flag = true;
		} else if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			pathMusic = getPath(context)
					+ "/test.mp3";
			//File f = new File("storage/0403-0201/test.mp3");
			File f = new File(pathMusic);
			if (!f.exists()) {
				mp = MediaPlayer.create(this, R.raw.test);
				id = R.string.speak_test_hasnot_sdcard_file;
				successButton.setEnabled(true);
			} else {
				Uri u = Uri.parse(pathMusic);
				mp = MediaPlayer.create(this, u);
				id = R.string.speak_test_message;
				successButton.setEnabled(true);
			}
		} else {
			return;
		}
		mp.setLooping(true);
		mp.start();

		String s = getString(id);
		/*if (flag) {
			s += "\n\n";
			s += getString(R.string.fail);
		}
		else{
			s += "\n\n";
			s += getString(R.string.success);			
		}*/
		textView.setText(s);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		playMusic(this);
		am.setStreamVolume(3, nCurrentMusicVolume, 0);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(mp!=null){
			mp.stop();
			mp.release();
			mp=null;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		am.setStreamVolume(3, nCurrentMusicVolume, 0);
	}
}
