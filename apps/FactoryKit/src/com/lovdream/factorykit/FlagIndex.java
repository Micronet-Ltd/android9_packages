
package com.lovdream.factorykit;

import java.util.HashMap;

public final class FlagIndex{

	private static HashMap<String,Integer>indexMap;

	private static final String[] ALL_KEYS = {
		//usb测试18-19(只能控制在0-1);
		//背夹 70-75;(所有背夹的flag要控制在0-5以内)
		//副板测试 11-17(所有副板的flag要控制在0-6内)
		//pcba 20-69; (所有pcba的flag要控制在6-49以内)
		//单项 76-128; (所有单项的flag要控制在6-52内)
		"back_charge",                                                        //背夹充电
		"back_led",                                                       //背夹灯
		"back_headset",                                                      //背夹耳机
		"back_mic",                                                      //背夹MIC
		"handset_loopback",                                                      //话筒回路测试
		"button_light",                                                      //按键灯
		"test_flag",                                                      //Test Flag
        "charging_test",                                                      //充电测试
        "headset_test_nuno",                                                      //耳机测试
		"key_test",                                                      //按键测试
		"system_version",                                                      //版本
        "speaker_test",                                                      //扬声器
		"camera_test_back",                                                      //后置摄像头
		"camera_test_front",                                                      //前置摄像头
		"sim_test",                                                      //sim卡
		"speaker_storage_test",                                                      //扬声器和存储卡
		"vibrator_test",                                                      //震动测试
		"headset_test",                                                      //耳机测试
		"gsensor_test",                                                      //重力感应
		"wifi_test",                                                      //wifi
		"bt_test",                                                      //蓝牙
		"light_sensor",                                                      //光感应
		"distance_sensor",                                                      //距离感应
		"side_charging_test",                                                      //侧面充电测试
		"flash_light",                                                      //闪光灯
		"gps_test",                                                      //gps
		"lcd_test",                                                      //lcd
		"tp_test",                                                      //tp
		"temperature_test",                                                      //温度
		"nfc_test",                                                      //nfc
		"gyro_sensor",                                                      //陀螺仪
		"compass",                                                      //指南针
		"otg_test",                                                      //otg
		"noise_mic",                                                      //副MIC回路测试
		"led_test",                                                      //三色灯
		"wifi_5g_test",                                                      //5gwifi
		"memory_test",
		"back_clip_otg",                                                      //背夹otg测试
		"noise_mic_front",                                                      //正面副MIC测试
		"wake_up_test",                                                      //睡眠唤醒
		"hardware_info",                                                      //器件信息
		"master_clear",                                                      //恢复出厂设置
		"nmea_test",                                                      //明码测试
		"virtual_key_test",                                                      //TP虚拟按键测试
		"headset_key_test",                                                      //耳机按键测试
		"laser_test",                                                      //激光灯
		"ircut_test",                                                      //IR-CUT测试
		"infrared_test",                                                      //红外灯
		"media_mic",                                                      //Media MIC测试
		"sub_mic",                                                      //SUB MIC测试
		"tf_hot_plug",                                                      //T卡热插拔
		"hoare_test",                                                      //霍尔测试
		"barometer_test",                                                      //气压计
		"front_flash_light",                                                      //前置闪光灯测试
		"fingerprint_test",                                                      //指纹传感器测试
		"fm_test",                                                      //收音机
		"sarsensor_test",                                                      //sar感应
		"sub_pin_test",                                                      //14PIN 测试
		"tp_grid_test"
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
