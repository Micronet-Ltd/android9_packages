package lovdream.android.cit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TouchProximitySenor extends Activity implements View.OnClickListener, Runnable{
	
	Thread mGetValues;
	private static final int REFRESH_TEXT = 1;
	private boolean bStop;
	private String mNowStatus = "";
	public static String FILE_URL = "/sys/devices/i2c-1/1-0024/";
    public static String PS_ENABLE = "ps_enable";
    public static String PS_STATUS = "ps_status";
    public static String PS_STATUS_ENABLE = "0x01";
    public static String PS_STATUS_DISENABLE = "0x00";
    public static final String FILE_URL_DEFAULT = "/sys/devices/i2c-1/1-0024/";
    public static final String PS_ENABLE_DEFAULT = "ps_enable";
    public static final String PS_STATUS_DEFAULT = "ps_status";
    public static final String FILE_URL_FT6206 = "/sys/devices/i2c-1/1-0038/ft6206/";
    public static final String PS_ENABLE_FT6206 = "ft6206_enable";
    public static final String PS_STATUS_FT6206 = "ft6206_status";
    public static final String FILE_URL_MSG2133A = "/sys/devices/virtual/input/input0/";
    public static final String FILE_URL_MSG2133A_2 = "/sys/devices/virtual/input/input1/";
    public static final String PS_ENABLE_MSG2133A = "proximity_sensor_enable";
    public static final String PS_STATUS_MSG2133A= "proximity_sensor_status";
    public static String [][] FILE_URL_LIST = {
		{FILE_URL_DEFAULT, PS_ENABLE_DEFAULT, PS_STATUS_DEFAULT, "0x01", "0x00"},
		{FILE_URL_FT6206, PS_ENABLE_FT6206, PS_STATUS_FT6206, "0xC0", "0xE0"},
		{FILE_URL_MSG2133A, PS_ENABLE_MSG2133A, PS_STATUS_MSG2133A, "0x01", "0x00", "4"},
		{FILE_URL_MSG2133A_2, PS_ENABLE_MSG2133A, PS_STATUS_MSG2133A, "0x01", "0x00", "4"},
    };
	
	private void initAllControl() {
		imageView = new ImageView[2];
		imageView[0] = (ImageView) findViewById(R.id.test_proximitysensor_gray);
		imageView[1] = (ImageView) findViewById(R.id.test_proximitysensor_green);
		strPromityinfo = getString(R.string.tp_proximitysenor_info);
		mtv_pro_info = (TextView) findViewById(R.id.tv_proximitysenor_info);
		madc_values = (TextView) findViewById(R.id.adc_values);
		bt_success = (Button)findViewById(R.id.btn_success);
		bt_success.setOnClickListener(this);
		bt_fail = (Button)findViewById(R.id.btn_fail);
		bt_fail.setOnClickListener(this);
	}
	
	Handler mRefreshHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case REFRESH_TEXT:
				madc_values.setText(msg.obj.toString());
				if (mNowStatus.contains(PS_STATUS_DISENABLE) || mNowStatus.contains("0x00")) {
					imageView[0].setVisibility(View.VISIBLE);
					imageView[1].setVisibility(View.GONE);
				} else {
					imageView[0].setVisibility(View.GONE);
					imageView[1].setVisibility(View.VISIBLE);					
				}
			}
		}
	};
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.test_proximitysenor);
		initAllControl();
		mtv_pro_info.setText(strPromityinfo);
		
		for (int i = 0; i < FILE_URL_LIST.length; i++) {
			File file = new File(FILE_URL_LIST[i][0], FILE_URL_LIST[i][2]);
			if (file.exists()) {
				FILE_URL = FILE_URL_LIST[i][0];
				PS_ENABLE = FILE_URL_LIST[i][1];
				PS_STATUS = FILE_URL_LIST[i][2];
				PS_STATUS_ENABLE = FILE_URL_LIST[i][3];
				PS_STATUS_DISENABLE = FILE_URL_LIST[i][4];
//				Log.d("CJ", "FILE_URL="+FILE_URL);
//				Log.d("CJ", "PS_ENABLE="+PS_ENABLE);
//				Log.d("CJ", "PS_STATUS="+PS_STATUS);
//				Log.d("CJ", "PS_STATUS_ENABLE="+PS_STATUS_ENABLE);
//				Log.d("CJ", "PS_STATUS_DISENABLE="+PS_STATUS_DISENABLE);
				break;
			}
		}
	}

	protected void onResume() {
		super.onResume();
		fileWrite("1");
		bStop = false;
		mGetValues = new Thread(this);
		mGetValues.start();
	}

	protected void onStop() {
		super.onStop();
		fileWrite("0");
		bStop = true;
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		Bundle b = new Bundle();
		Intent intent = new Intent();
		
		if(id == R.id.btn_success){
			b.putInt("test_result", 1);
		}else{
			b.putInt("test_result", 0);
		}
		
		intent.putExtras(b);
		setResult(RESULT_OK, intent);
		finish();
	}

	ImageView imageView[];
	TextView mtv_pro_info;
	TextView madc_values;
	String strPromityinfo;

	Button bt_success;
	Button bt_fail;
	
	private void fileRead() {
		 FileInputStream fis;
		 ByteArrayOutputStream baos;
		 byte buffer[] = new byte[1024];
		 byte data[] = new byte[1024];
		 try {
			 File f = new File(FILE_URL, PS_STATUS);
			 fis = new FileInputStream(f);
			 baos = new ByteArrayOutputStream();
			 int len;
			 while ((len = fis.read(buffer)) != -1) {
				 baos.write(buffer, 0, len);
			 }
			 data = baos.toByteArray();
			 fis.close();
			 baos.close();
        } catch (IOException e) {
        	Log.e("CJ", "CommunicateWithSensor > io error: " + e);
        }
        if (data == null || data.length == 0) {
            return;
        }
        String status = new String(data);
        mNowStatus = status;
        buffer = null;
        data = null;

	}
	
	private void fileWrite(String value) {
		try {
			FileOutputStream fos;
			fos = new FileOutputStream(FILE_URL+PS_ENABLE);
			fos.write(value.getBytes());
			fos.close();
		} catch (IOException e) {
		Log.e("CJ", "CommunicateWithSensor > io error: " + e);
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!bStop)
		{
			try {
	            fileRead();
	            mRefreshHandler.sendMessage(mRefreshHandler
						.obtainMessage(REFRESH_TEXT, mNowStatus));
	            Thread.sleep(500);
	            Log.d("CJ", "mNowStatus= "+mNowStatus);
				Log.d("CJ", "1212121");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
