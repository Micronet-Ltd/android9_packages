package lovdream.android.cit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import com.lovdream.util.SystemUtil;
//import com.android.server.pm.ShutdownThread;

public class bootReceiver extends BroadcastReceiver {
	static final String action_boot = "android.intent.action.BOOT_COMPLETED";
	private boolean isTest = false;
	private static final String CAMERA_BACK = "CAMERA_BACK";
	private static final String CAMERA_FRONT = "CAMERA_FRONT";
	private static final String BATTERY = "BATTERY";
	private static final String LCD = "LCD";
	private static final String KEYPADBACKLIGHT = "KEYPADBACKLIGHT";
	private static final String FLASHLIGHT = "FLASHLIGHT";
	private static final String LED_RED = "LED_RED";
	private static final String LED_GREEN = "LED_GREEN";
	private static final String GSENSOR = "GSENSOR";
	private static final String LSENSOR = "LSENSOR";
	private static final String MSENSOR = "MSENSOR";
	private static final String PSENSOR = "PSENSOR";
	private static final String SDCARD = "SDCARD";
	private static final String SIMCARD = "SIMCARD";
	private static final String TOUCH = "TOUCH";
	private static final String SOFT_KEYS = "SOFT_KEYS";
	private static final String HARDWARE_KEYS = "HARDWARE_KEYS";
	private static final String VIBRATOR = "VIBRATOR";
	private static final String AUDIO_HANDSET = "AUDIO_HANDSET";
	private static final String AUDIO_HEADSET = "AUDIO_HEADSET";
	private static final String AUDIO_LOUDSPEAKER = "AUDIO_LOUDSPEAKER";
	private static final String WIFI = "WIFI";
	private static final String FM = "FM";
	private static final String BLUETOOTH = "BLUETOOTH";
	private static final String GPS_GARDEN = "GPS_GARDEN";
	
	private static final int FASTMMI_CAMERA_BACK = 99;
	private static final int FASTMMI_CAMERA_FRONT = FASTMMI_CAMERA_BACK + 1;
	private static final int FASTMMI_BATTERY = FASTMMI_CAMERA_BACK + 2;
	private static final int FASTMMI_LCD = FASTMMI_CAMERA_BACK + 3;
	private static final int FASTMMI_KEYPADBACKLIGHT = FASTMMI_CAMERA_BACK + 4;
	private static final int FASTMMI_FLASHLIGHT = FASTMMI_CAMERA_BACK + 5;
	private static final int FASTMMI_LED_RED = FASTMMI_CAMERA_BACK + 6;
	private static final int FASTMMI_LED_GREEN = FASTMMI_CAMERA_BACK + 7;
	private static final int FASTMMI_GSENSOR = FASTMMI_CAMERA_BACK + 8;
	private static final int FASTMMI_LSENSOR = FASTMMI_CAMERA_BACK + 9;
	private static final int FASTMMI_MSENSOR = FASTMMI_CAMERA_BACK + 10;
	private static final int FASTMMI_PSENSOR = FASTMMI_CAMERA_BACK + 11;
	private static final int FASTMMI_SDCARD = FASTMMI_CAMERA_BACK + 12;
	private static final int FASTMMI_SIMCARD = FASTMMI_CAMERA_BACK + 13;
	private static final int FASTMMI_TOUCH = FASTMMI_CAMERA_BACK + 14;
	private static final int FASTMMI_SOFT_KEYS = FASTMMI_CAMERA_BACK + 15;
	private static final int FASTMMI_HARDWARE_KEYS = FASTMMI_CAMERA_BACK + 16;
	private static final int FASTMMI_VIBRATOR = FASTMMI_CAMERA_BACK + 17;
	private static final int FASTMMI_AUDIO_HANDSET = FASTMMI_CAMERA_BACK + 18;
	private static final int FASTMMI_AUDIO_HEADSET = FASTMMI_CAMERA_BACK + 19;
	private static final int FASTMMI_AUDIO_LOUDSPEAKER = FASTMMI_CAMERA_BACK + 20;
	private static final int FASTMMI_WIFI = FASTMMI_CAMERA_BACK + 21;
	private static final int FASTMMI_FM = FASTMMI_CAMERA_BACK + 22;
	private static final int FASTMMI_BLUETOOTH = FASTMMI_CAMERA_BACK + 23;
	private static final int FASTMMI_GPS_GARDEN = FASTMMI_CAMERA_BACK + 24;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		File file = new File("/persist/fastmmi_result");
		if (file.exists()) {
			initFastMMIResultInNv2499();
			Log.d("CJ", "1111SystemUtil.getNvFactoryData3I()="+SystemUtil.getNvFactoryData3I());
			String testResult = readFile("/persist/fastmmi_result");
			String[] resultWithKey = testResult.split(";");
			Log.d("CJ", "testResult="+testResult);
			Log.d("CJ", "resultWithKey.length="+resultWithKey.length);
			for (int i = 0; i < resultWithKey.length; i++) {
				String[] keyAndResult = resultWithKey[i].split(",");
				char[] result = keyAndResult[1].toCharArray();
				Log.d("CJ", "keyAndResult[0]="+keyAndResult[0]);
				Log.d("CJ", "result[0]="+result[0]);
				try {
					saveFastMMIResultToNv2499(keyAndResult[0], result[0]);
				} catch (Exception e) {
					e.printStackTrace();
					Log.d("CJ", "e=" + e);
				}
			}
			Log.d("CJ", "2222SystemUtil.getNvFactoryData3I()="+SystemUtil.getNvFactoryData3I());
			
			boolean result = file.delete();
			Log.d("CJ", "result="+result);
		}
		
		String action = intent.getAction();
		
		byte[] bresult = null;
		bresult = SystemUtil.getNvFactoryData3IByte();
		bresult = SystemUtil.getNvFactoryData3IByte();
		boolean showPCBATest = true;
	/*	if(bresult != null){
			char[] cChar = getChars(bresult);
			android.util.Log.e("...wt","cChar[4] -----------------------------"+cChar[4] );
			if(cChar[4] == 'P'){
				showPCBATest = false;
			}
		}*/
		File f = new File("storage/sdcard1/test.mp3");
		android.util.Log.e("...wt","f.exists() -----------------------------"+f.exists());
		if(!f.exists()){
			showPCBATest = false;
		}
		boolean rebootTest = loadIsTest(context);
		android.util.Log.e("...wt","showPCBATest -----------------------------"+showPCBATest);
		if (!rebootTest && action.equals(action_boot) && showPCBATest) {
        	Intent ootStartIntent=new Intent(context,PCBATest.class);  
        	ootStartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(ootStartIntent); 
        }
		
        if (rebootTest && action.equals(action_boot)) {
        	Intent ootStartIntent=new Intent(context,RebootTest.class);  
        	ootStartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
            context.startActivity(ootStartIntent); 
        }
        
        //Intent screenTestIntent=new Intent(context,ScreenTest.class);  
        //screenTestIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        //context.startActivity(screenTestIntent); 
	}
	
	private char[] getChars(byte[] bytes) {
		Charset cs = Charset.forName("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);
		return cb.array();
	}
	
	public boolean loadIsTest(Context context) {
		SharedPreferences mIsTest = context.getSharedPreferences("mIsTest", Context.MODE_PRIVATE);
		isTest  = mIsTest.getBoolean("mIsTest", false);
		return isTest;
	}
	
	private void initFastMMIResultInNv2499(){
		if (!CITMain.mSaveToNv2499) {
			return;
		}
		byte[] bresult = null;
		bresult = SystemUtil.getNvFactoryData3IByte();
		if (bresult == null) {
			bresult = new byte[128];
			for (int i = 0; i < bresult.length; i++) {
				bresult[i] = 'U';
			}
		} else {
			for (int i = FASTMMI_CAMERA_BACK; i < FASTMMI_GPS_GARDEN+1; i++) {
				if(i >= bresult.length){
					break;
				}
				bresult[i] = 'U';
			}
		}
		SystemUtil.setNvFactoryData3IByte(bresult);
	}
	
	private void rewriteOneByteOfNv2499(int idx, char testResult) {
		if (!CITMain.mSaveToNv2499) {
			return;
		}
		byte[] bresult = null;
		bresult = SystemUtil.getNvFactoryData3IByte();
		if (bresult == null) {
			bresult = new byte[128];
			for (int i = 0; i < bresult.length; i++) {
				bresult[i] = 'U';
			}
		}
		Log.d("CJ", "idx="+idx);
		if(bresult.length <= idx){
			Log.e("CIT","rewriteOneByteOfNv2499New(),getNvFactoryData3IByte length error");
			return;
		}
		bresult[idx] = (byte) testResult;
		SystemUtil.setNvFactoryData3IByte(bresult);
	}
	
	private void saveFastMMIResultToNv2499(String key, char result) {
		if (!CITMain.mSaveToNv2499) {
			return;
		}
		if (key.equals(CAMERA_BACK)) {
			rewriteOneByteOfNv2499(FASTMMI_CAMERA_BACK, result);
		} else if (key.equals(CAMERA_FRONT)) {
			rewriteOneByteOfNv2499(FASTMMI_CAMERA_FRONT, result);
		} else if (key.equals(BATTERY)) {
			rewriteOneByteOfNv2499(FASTMMI_BATTERY, result);
		} else if (key.equals(LCD)) {
			rewriteOneByteOfNv2499(FASTMMI_LCD, result);
		} else if (key.equals(KEYPADBACKLIGHT)) {
			rewriteOneByteOfNv2499(FASTMMI_KEYPADBACKLIGHT, result);
		} else if (key.equals(FLASHLIGHT)) {
			rewriteOneByteOfNv2499(FASTMMI_FLASHLIGHT, result);
		} else if (key.equals(LED_RED)) {
			rewriteOneByteOfNv2499(FASTMMI_LED_RED, result);
		} else if (key.equals(LED_GREEN)) {
			rewriteOneByteOfNv2499(FASTMMI_LED_GREEN, result);
		} else if (key.equals(GSENSOR)) {
			rewriteOneByteOfNv2499(FASTMMI_GSENSOR, result);
		} else if (key.equals(LSENSOR)) {
			rewriteOneByteOfNv2499(FASTMMI_LSENSOR, result);
		} else if (key.equals(MSENSOR)) {
			rewriteOneByteOfNv2499(FASTMMI_MSENSOR, result);
		} else if (key.equals(PSENSOR)) {
			rewriteOneByteOfNv2499(FASTMMI_PSENSOR, result);
		} else if (key.equals(SDCARD)) {
			rewriteOneByteOfNv2499(FASTMMI_SDCARD, result);
		} else if (key.equals(SIMCARD)) {
			rewriteOneByteOfNv2499(FASTMMI_SIMCARD, result);
		} else if (key.equals(TOUCH)) {
			rewriteOneByteOfNv2499(FASTMMI_TOUCH, result);
		} else if (key.equals(SOFT_KEYS)) {
			rewriteOneByteOfNv2499(FASTMMI_SOFT_KEYS, result);
		} else if (key.equals(HARDWARE_KEYS)) {
			rewriteOneByteOfNv2499(FASTMMI_HARDWARE_KEYS, result);
		} else if (key.equals(VIBRATOR)) {
			rewriteOneByteOfNv2499(FASTMMI_VIBRATOR, result);
		} else if (key.equals(AUDIO_HANDSET)) {
			rewriteOneByteOfNv2499(FASTMMI_AUDIO_HANDSET, result);
		} else if (key.equals(AUDIO_HEADSET)) {
			rewriteOneByteOfNv2499(FASTMMI_AUDIO_HEADSET, result);
		} else if (key.equals(AUDIO_LOUDSPEAKER)) {
			rewriteOneByteOfNv2499(FASTMMI_AUDIO_LOUDSPEAKER, result);
		} else if (key.equals(WIFI)) {
			rewriteOneByteOfNv2499(FASTMMI_WIFI, result);
		} else if (key.equals(FM)) {
			rewriteOneByteOfNv2499(FASTMMI_FM, result);
		} else if (key.equals(BLUETOOTH)) {
			rewriteOneByteOfNv2499(FASTMMI_BLUETOOTH, result);
		} else if (key.equals(GPS_GARDEN)) {
			rewriteOneByteOfNv2499(FASTMMI_GPS_GARDEN, result);
		}
	}	
	
	private String readFile(String filePath) {
		String res = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(filePath))));
			String str = null;
			while ((str = br.readLine()) != null) {
				res += str;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("CJ", "e=" + e);
		}
		return res;
	}
}
