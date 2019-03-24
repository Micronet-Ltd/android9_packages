package lovdream.android.cit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.os.Build;
//import com.qualcomm.qcnvitems.QcNvItems;
import com.lovdream.util.SystemUtil;

public class Version extends PreferenceActivity {

	//private QcNvItems mNv;
	public static final String TP_INFO = "/sys/devices/virtual/assist/ctp/ic";
	public static final String TP_VER = "/sys/devices/virtual/assist/ctp/fw_ver";
        private static final boolean mHasUiModemS = !SystemProperties.get("ro.product.ui_model_s").equals("");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		mNv = new QcNvItems(this);
		addPreferencesFromResource(R.layout.test_version);

                if (mHasUiModemS){
        	   setStringSummary("sn_model", getSystemproString("ro.product.ui_model_s"));
                 } else {
		    setStringSummary("sn_model", getSystemproString("ro.product.ui_model"));
                 }

		setStringSummary("software_title", getSoftwareVersion());

		setStringSummary("baseband_version", SystemProperties.get("persist.sys.bandversion", "MPSS.TR.2.0-00633"));
		
		setStringSummary("tp_info", readFileSdcardFile(TP_INFO));
		
		setStringSummary("tp_ver", readFileSdcardFile(TP_VER));
		setStringSummary("inter_version", Build.INTER_VERSION);
		try {
			setStringSummary("sn_version", getSnVersion().substring(0, 20));
		} catch (Exception e) {
			setStringSummary("sn_version", getSnVersion().substring(0, 15));
		}
//		try {
//			setStringSummary("audio_parameters", getSnVersion().substring(96, 127));
//		} catch (Exception e) {
//			// TODO: handle exception
//			
//		}
		getPreferenceScreen().removePreference(findPreference("audio_parameters"));
	}
	
	public String readFileSdcardFile(String filePath){   
		String res="";   
		try{   
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
			String str=null;
			while((str=br.readLine())!=null){
				res+=str;
			}  
        }   
		catch(Exception e){   
         e.printStackTrace();   
        }   
        return res;   
	}   

	private static String getSystemproString(String property) {
		return SystemProperties.get(property, "unknown");
	}

	private String getSnVersion() {
		String strSN = "";
		strSN = SystemUtil.getSN();
//		try {
//			strSN = mNv.getSNNumber();
//		} catch (IOException e) {
//
//		}
		return strSN;
	}

	private String getSoftwareVersion() {
		String softwareTitle = getSystemproString("ro.build.display.id");
		String softwareTime = getSystemproString("ro.build.version.incremental");

//		if (softwareTime.length() > 18) {
//			int nPos = softwareTime.indexOf(".");
//			nPos = softwareTime.indexOf(".", nPos);
//			softwareTime = softwareTime.substring(nPos + 1);
//			nPos = softwareTime.indexOf(".");
//			nPos = softwareTime.indexOf(".", nPos);
//
//			softwareTime = softwareTime.substring(nPos + 1);
//
//			StringBuffer time = new StringBuffer(softwareTime);
//			time.insert(4, '-');
//			time.insert(7, '-');
//			time.replace(10, 11, " ");
//			time.insert(13, ':');
//			time.insert(16, ':');
//
//			softwareTitle += "\n" + time.toString();
//		}

		return softwareTitle;

	}

	private void setStringSummary(String preference, String value) {
		try {
			findPreference(preference).setSummary(value);
		} catch (RuntimeException e) {
			findPreference(preference).setSummary(
					getResources().getString(R.string.cit_info_default));
		}
	}

	private void setValueSummary(String preference, String property) {
		try {
			findPreference(preference).setSummary(
					SystemProperties
							.get(property,
									getResources().getString(
											R.string.cit_info_default)));
		} catch (RuntimeException e) {

		}
	}
}
