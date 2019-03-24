
package com.lovdream.factorykit;

import java.util.HashMap;

public final class FlagIndex{

	private static HashMap<String,Integer>indexMap;

	private static final String[] ALL_KEYS = {
		/*if some item is in small pcb test,index do not big than 10*/
		"back_charge",
		"back_led",
		"back_headset",
		"back_mic",
        "charging_test",
        "headset_test_nuno",
        "handset_loopback",
		"key_test",
		"system_version",
                "speaker_test",
		"button_light",
		"camera_test_back",
		"camera_test_front",
		"sim_test",
		"speaker_storage_test",
		"vibrator_test",
		"headset_test",
		"gsensor_test",
		"sarsensor_test",
		"wifi_test",
		"bt_test",
		"light_sensor",
		"distance_sensor",
		"side_charging_test",
		"flash_light",
		"front_flash_light",
		"gps_test",
		"lcd_test",
		"tp_test",
		"temperature_test",
		"nfc_test",
		"gyro_sensor",
		"compass",
		"otg_test",
		"noise_mic",
		"led_test",
		"wifi_5g_test",
		"back_clip_otg",
		"fingerprint_test",
		"hoare_test",
		"noise_mic_front",
		"barometer_test",
		"wake_up_test",
		"nmea_test",
		"fm_test",
		"tp_grid_test",
		"tf_hot_plug",
		"virtual_key_test",
		"headset_key_test",
		"laser_test",
		"ircut_test",
		"infrared_test",
		"media_mic",
		"sub_mic",
		"hardware_info",
		"test_flag",
		"master_clear",
		"sub_pin_test"
	};

	private static void load(){
		indexMap = new HashMap<String,Integer>();
		for(int i = 0;i < ALL_KEYS.length;i++){
			indexMap.put(ALL_KEYS[i],i);
		}
	}

	public static int getIndex(String key){
		if(indexMap == null){
			load();
		}
		if(indexMap.get(key) == null){
			throw new RuntimeException("Invalid key,you should add the key in FlagIndex.ALL_KEYS if you implement any test items");
		}
		return indexMap.get(key);
	}

}
