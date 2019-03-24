
package com.lovdream.factorykit.items;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.LayoutInflater;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.AudioManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.content.Context;
import java.io.FileInputStream;

import com.lovdream.factorykit.R;
import com.lovdream.factorykit.TestItemBase;
import com.lovdream.LovdreamDeviceManager;

public class NoiseMicTest extends TestItemBase implements Runnable{

	private static final String TAG = "noise_mic";

	private static final int MSG_PLAY = 0x11;
	private static final int MSG_RECORD = 0x12;
	private static final int STATE_RECORDING = 1;
	private static final int STATE_PLAYING = 2;

	private static final String RECORD_FILE = "/data/vendor/ftm_pcm_record.wav";

	private static final String AUDIO_FTM = "mm-audio-ftm";
	private static final String defaultFTMConfig = "/vendor/etc/ftm_test_config";
	private static final String defaultVolume = "100";
	private static final String defaultTimeout = "3";

	private static String defaultTunnel = "18";

	private static final int SAMPLE_RATE = 48000;
	private static final int BUFFER_SIZE = 1024;

	private TextView mStatusText;
	private ImageView mStatusIcon;

	private Process mTestProcess;
	private boolean running = false;


	private byte[] mBuffer = new byte[BUFFER_SIZE];
	private AudioTrack mTrack;

	private String buildCmds(){
		//cmdline eg: mm-audio-ftm -tc 252 -c /vendor/etc/ftm_test_config -d 120 -v 60
		String[] cmds = new String[9];
		cmds[0] = AUDIO_FTM;
		cmds[1] = "-tc";
		cmds[2] = defaultTunnel;
		cmds[3] = "-c";
		cmds[4] = defaultFTMConfig;
		cmds[5] = "-d";
		cmds[6] = defaultTimeout;
		cmds[7] = "-v";
		cmds[8] = defaultVolume;
                String a = cmds[0]+"*"+cmds[1]+"*"+cmds[2]+"*"+cmds[3]+"*"+cmds[4]+"*"+cmds[5]+"*"+cmds[6]+"*"+cmds[7]+"*"+cmds[8];
		return a;
		//return cmds;
	}

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
				case MSG_PLAY:
					updateUI(STATE_PLAYING);
					break;
				case MSG_RECORD:
					updateUI(STATE_RECORDING);
					break;
			}
		}
	};

	@Override
	public void run(){
		try{
			while(running){
				/* mm-audio-ftm just works fine about 5 second,i have no idea,just run again and again */
				mHandler.sendEmptyMessage(MSG_RECORD);
				Log.d(TAG,"process start");
				String cmds = buildCmds();
				/*for(String s : cmds){
					Log.d(TAG,s);
				}*/
                                      ldm.runProcess(cmds);
				Log.d(TAG,"process exit");
				
				mHandler.sendEmptyMessage(MSG_PLAY);
				FileInputStream fin = new FileInputStream(RECORD_FILE);
				int readSize = 0;
				while(((readSize = fin.read(mBuffer)) != -1) && running){
					mTrack.write(mBuffer,0,readSize);
				}
				fin.close();
			}
		}catch(Exception e){
			e.printStackTrace();
			postFail();
		}
	}


	@Override
	public String getKey(){
		return "noise_mic";
	}

	@Override
	public String getTestMessage(){
		return getActivity().getString(R.string.noise_mic_test_mesg);
	}
    private LovdreamDeviceManager ldm;
    private Context mContext;

	@Override
	public void onStartTest(){
                    mContext = getActivity();
                    ldm = (LovdreamDeviceManager)mContext.getSystemService(Context.LOVDREAMDEVICES_SERVICE);

		String arg[] = getParameter("tc");
		if((arg != null) && (arg[0] != null)){
			defaultTunnel = arg[0];
		}

		running = true;
		new Thread(this).start();

		mTrack = new AudioTrack(AudioManager.STREAM_MUSIC,SAMPLE_RATE,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT,BUFFER_SIZE,AudioTrack.MODE_STREAM);
		mTrack.setPlaybackRate(SAMPLE_RATE);
		mTrack.play();
	}

	@Override
	public void onStopTest(){
		running = false;
		if(mTestProcess != null){
			mTestProcess.destroy();
		}
		mTrack.stop();
		mTrack.release();
		mTrack = null;
	}

	@Override
	public View getTestView(LayoutInflater inflater){
		View v = inflater.inflate(R.layout.noise_mic_test,null);
		mStatusText = (TextView) v.findViewById(R.id.stateMessage);
		mStatusIcon = (ImageView) v.findViewById(R.id.stateIcon);
		Animation anim = AnimationUtils.loadAnimation(getActivity(),R.anim.twinkle);
		mStatusIcon.startAnimation(anim);
		return v;
	}

	private void updateUI(int state){
		if(mStatusText != null){
			mStatusText.setText(state == STATE_RECORDING ? R.string.recording : R.string.playing);
		}
		if(mStatusIcon != null){
			mStatusIcon.setImageResource(state == STATE_RECORDING ? R.drawable.speak : R.drawable.listen);
		}
	}
}
