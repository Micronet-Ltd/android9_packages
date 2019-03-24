package com.android.lovdream;


import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
//import com.android.internal.telephony.Phone;
//import com.android.internal.telephony.PhoneFactory;


public class DeviceInfoShow extends PreferenceActivity {


    private static final String KEY_CONTAINER = "container";
    private static final String HARD_WARE_PROPERTY="ro.hardware.version";
    //private Phone mPhone = null;
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        //mPhone = PhoneFactory.getDefaultPhone();
        
        PreferenceGroup parentPreference = (PreferenceGroup) findPreference(KEY_CONTAINER);
        parentPreference = getPreferenceScreen();

        addPreferencesFromResource(R.xml.device_info_show);
       
        setStringSummary("device_model", Build.MODEL);
        setStringSummary("build_number", Build.DISPLAY);
        setStringProperty("hardware_version", HARD_WARE_PROPERTY);
        //if (mPhone.getPhoneName().equals("CDMA")) {
        //    setStringSummary("prl_version", mPhone.getCdmaPrlVersion());
        //    setStringSummary("meid_number", mPhone.getMeid().toUpperCase());
        //    setStringSummary("uim_id", mPhone.getEsn());
        //} else
	 {
        	getPreferenceScreen().removePreference(findPreference("prl_version"));
        	getPreferenceScreen().removePreference(findPreference("meid_number"));
                getPreferenceScreen().removePreference(findPreference("uim_id"));
        }
       
        
       
    }

  

    private void setStringSummary(String preference, String value) {
        try {
              if(value==null||value.equals(""))
               throw new RuntimeException();
            findPreference(preference).setSummary(value);
        } catch (RuntimeException e) {
            findPreference(preference).setSummary(
                getResources().getString(R.string.device_info_default));
        }
    }
     
    private void setStringProperty(String preference, String property){
    	String value=SystemProperties.get(property);
    	 try {
              if(value==null||value.equals(""))
               throw new RuntimeException();
             findPreference(preference).setSummary(value);
         } catch (RuntimeException e) {
             findPreference(preference).setSummary(
                 getResources().getString(R.string.device_info_default));
         }
    }
   

}
