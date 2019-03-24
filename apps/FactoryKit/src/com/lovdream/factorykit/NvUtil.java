package com.lovdream.factorykit;

import android.util.Log;

import com.lovdream.util.SystemUtil;

public class NvUtil {

	
	private static final String TAG = "NvUtil";

	public static byte[] getNvFactoryData3IByte(){
		byte [] mTestFlag = SystemUtil.getNvFactoryData3IByte();
		if((mTestFlag == null) || (mTestFlag.length < Config.TEST_FLAG_MAX)){
			Log.e(TAG,"can not get test falg,or invalid length");
			byte[] tmp = new byte[Config.TEST_FLAG_MAX];

			if(mTestFlag != null){
				for(int i = 0;i < mTestFlag.length;i++){
					tmp[i] = mTestFlag[i];
				}
			}
			mTestFlag = tmp;
		}
		
		return mTestFlag;
	}
}
