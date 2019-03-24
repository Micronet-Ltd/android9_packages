package com.android.lovdream;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.util.Log;

public class TCardUpgrade extends Thread{
	//���·��
	private final String file_path="/sys/devices/platform/msm_hsusb/gadget/tflash_update"; 
	//����int[] data={1,2,3};
	private final byte[] data={1};

	public TCardUpgrade(){
          start();
	}
public void run(){
 try {
		 FileOutputStream outStream=new FileOutputStream(file_path);
		 outStream.write(data);
		 outStream.close();
		
	} catch (FileNotFoundException e) {
		Log.e("TCardUpgrade","TCardUpgrade -->FileNotFoundException :"+e.getMessage());
	} catch (IOException e) {
		Log.e("TCardUpgrade","TCardUpgrade -->IOException :"+e.getMessage());
	}
	
}

}
