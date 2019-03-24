package com.lovdream.factorykit.items;


import android.os.Handler;
import android.util.Log;
import android.graphics.Color;

import com.lovdream.factorykit.TestItemBase;
import com.lovdream.factorykit.R;

import java.io.File;
import java.io.FileOutputStream;
import com.lovdream.LovdreamDeviceManager;
import android.content.Context;

public class LedTest extends TestItemBase{

    private static final String CONFIG_GREEN_PATH = "/sys/class/leds/green/brightness";
    private static final String CONFIG_RED_PATH = "/sys/class/leds/red/brightness";
    private static final String CONFIG_BLUE_PATH = "/sys/class/leds/blue/brightness";

    private Handler mHandler = new Handler();
    private boolean mIsInTest;
    private LovdreamDeviceManager ldm;
    private Context mContext;

	private int[] colors = {
		0xffff0000,
		0xff00ff00,
		0xff0000ff
	};

    @Override
    public String getKey() {
        return "led_test";
    }

    @Override
    public String getTestMessage() {
		String[] msg = getParameter("msg");
		if((msg != null) && (msg[0] != null)){
			return msg[0];
		}
        return getString(R.string.two_color_led_test_mesg);
    }

    @Override
    public void onStartTest() {
        mContext = getActivity();
        ldm = (LovdreamDeviceManager)mContext.getSystemService(Context.LOVDREAMDEVICES_SERVICE);
		String[] args = getParameter("colors");
		if(args != null){
			try{
				int[] newColors = new int[args.length];
				for(int i = 0;i < newColors.length;i++){
					newColors[i] = Color.parseColor(args[i]);
				}
				colors = newColors;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
        mIsInTest = true;
        //mThread.start();
        new Thread(mRunnable,"t1").start();
    }

    @Override
    public void onStopTest() {
        mIsInTest = false;
        //mThread.stop();
    }

    private Thread mThread = new Thread(new Runnable() {
        @Override
        public void run(){
            while(mIsInTest){
                for(int color : colors){
                    setColor(color);
                        try{
                        Thread.sleep(1000);
                        }catch(Exception e){
                        //ignore
                        }
                    }
                }
                disableLed();
        }
    });
        private Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                while(mIsInTest){
                    for(int color : colors){
                        setColor(color);
                            try{
                            Thread.sleep(1000);
                            }catch(Exception e){
                            //ignore
                            }
                        }
                    }
                disableLed();
            }
        };

	private void setColor(int color){

		int red = (color >> 16) & 0xff;
		int green = (color >> 8) & 0xff;
		int blue = color & 0xff;

		writeInt(CONFIG_RED_PATH,red);
		writeInt(CONFIG_GREEN_PATH,green);
		writeInt(CONFIG_BLUE_PATH,blue);
	}

    private void writeInt(String path,int value) {
        ldm.writeToFile(path, ""+value);
        /*File file = new File(path);;
        FileOutputStream fos;
        try {
            if (!file.exists()) {
				return;
            }
            fos = new FileOutputStream(file);
            fos.write(String.valueOf(value).getBytes());
            fos.close();
        } catch (Exception e) {
            Log.e("LedTest", "error: " + e);
        }*/
    }

    private void disableLed(){
        writeInt(CONFIG_GREEN_PATH,0);
        writeInt(CONFIG_RED_PATH,0);
        writeInt(CONFIG_BLUE_PATH,0);
    }
}
