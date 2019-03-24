/*
 * Copyright (c) 2011-2012 Qualcomm Technologies, Inc.  All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 */
package lovdream.android.cit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class Headset extends Activity {

    String mAudiofilePath;
    static String TAG = "Headset";
    MediaRecorder mMediaRecorder;
    boolean isRecording = false;
    Button recordButton = null;
    Button stopButton = null;
    AudioManager mAudioManager;
    Context mContext;
    boolean mFinished = false;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.headset);

        mMediaRecorder = new MediaRecorder();
        mContext = this;
        isRecording = false;

        getService();
        bindView();

        if (!mAudioManager.isWiredHeadsetOn())
            showWarningDialog(getString(R.string.please_insert_headset));

        setAudio();

    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	mAudioManager.setMode(AudioManager.MODE_NORMAL);
    	mFinished = true;
    	mHandler.removeMessages(0);
    	if (isRecording) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
    	}
    }

    @Override
    public void finish() {

        super.finish();
    }

    void record() throws IllegalStateException, IOException, InterruptedException {

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setOutputFile(this.getFilesDir().getAbsolutePath() + "/test.amr");
        mAudiofilePath = this.getFilesDir().getAbsolutePath() + "/test.amr";
        mMediaRecorder.prepare();
        mMediaRecorder.start();
    }

    void replay() throws IllegalArgumentException, IllegalStateException, IOException {
        final TextView mTextView = (TextView) findViewById(R.id.headset_hint);
        mTextView.setText(getString(R.string.headset_playing));
        // Replaying sound right now by record();
        mAudioManager.setMode(AudioManager.MODE_IN_CALL);
        stopButton.setClickable(false);
        File file = new File(mAudiofilePath);
        FileInputStream mFileInputStream = new FileInputStream(file);
        final MediaPlayer mMediaPlayer = new MediaPlayer();

        mMediaPlayer.reset();
        mMediaPlayer.setDataSource(mFileInputStream.getFD());
        mMediaPlayer.prepare();
        mMediaPlayer.start();

        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            public void onCompletion(MediaPlayer mPlayer) {

                mPlayer.stop();
                mPlayer.release();
                File file = new File(mAudiofilePath);
                file.delete();
                
                final TextView mTextView = (TextView) findViewById(R.id.headset_hint);
                mTextView.setText(getString(R.string.headset_replay_end));
                
                if (!mFinished) {
                    showConfirmDialog();
				}

            }
        });

    }
    
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();
		Intent intent = new Intent();
		bundle.putInt("test_result", 0);
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		super.onBackPressed();
	}

    void showWarningDialog(String title) {
        
        new AlertDialog.Builder(Headset.this).setTitle(title)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

    }

    void showConfirmDialog() {
    	final Bundle b = new Bundle();
		final Intent intent = new Intent();
        new AlertDialog.Builder(Headset.this)
        		.setCancelable(false)
        		.setTitle(getString(R.string.headset_confirm))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    
                    public void onClick(DialogInterface dialog, int which) {
                        
//                        pass();
                    	b.putInt("test_result", 1);
        				intent.putExtras(b);
        				setResult(RESULT_OK, intent);
        				finish();
                    }
                }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    
                    public void onClick(DialogInterface dialog, int which) {
                        
//                        fail(null);
                    	b.putInt("test_result", 0);
        				intent.putExtras(b);
        				setResult(RESULT_OK, intent);
        				finish();
                    }
                }).show();
    }

    public void setAudio() {

        mAudioManager.setMode(AudioManager.MODE_IN_CALL);
        float ratio = 0.6f;

        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_DTMF)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)), 0);
    }
    
    private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (isRecording) {
                mMediaRecorder.stop();
                mMediaRecorder.release();
                isRecording = false;
                try {
                    replay();
                } catch (Exception e) {
                    loge(e);
                }
            } else
                showWarningDialog(getString(R.string.headset_record_first));
        }
	};

    void bindView() {

        recordButton = (Button) findViewById(R.id.headset_record);
        stopButton = (Button) findViewById(R.id.headset_stop);
        stopButton.setVisibility(View.GONE);
        final TextView mTextView = (TextView) findViewById(R.id.headset_hint);
        mTextView.setText(getString(R.string.headset_to_record));

        recordButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                if (mAudioManager.isWiredHeadsetOn()) {

                    mTextView.setText(getString(R.string.headset_recording));
                    try {
                        recordButton.setClickable(false);
                        record();
                        isRecording = true;
                        mHandler.sendEmptyMessageDelayed(0, 2500);
                    } catch (Exception e) {
                        loge(e);
                    }
                } else
                    showWarningDialog(getString(R.string.please_insert_headset));
            }
        });

        stopButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                if (isRecording) {
                    mMediaRecorder.stop();
                    mMediaRecorder.release();

                    try {
                        replay();
                    } catch (Exception e) {
                        loge(e);
                    }
                } else
                    showWarningDialog(getString(R.string.headset_record_first));
            }
        });
    }

    void getService() {

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

//    void fail(Object msg) {
//
//        loge(msg);
//        toast(msg);
//        setResult(RESULT_CANCELED);
//        Utilities.writeCurMessage(this, TAG, "Failed");
//        finish();
//    }
//
//    void pass() {
//
//        setResult(RESULT_OK);
//        Utilities.writeCurMessage(this, TAG, "Pass");
//        finish();
//    }

    public void toast(Object s) {

        if (s == null)
            return;
        Toast.makeText(this, s + "", Toast.LENGTH_SHORT).show();
    }

    private void loge(Object e) {

        if (e == null)
            return;
        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();
        e = "[" + mMethodName + "] " + e;
        Log.e(TAG, e + "");
    }

    @SuppressWarnings("unused")
    private void logd(Object s) {

        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();

        s = "[" + mMethodName + "] " + s;
        Log.d(TAG, s + "");
    }

}
