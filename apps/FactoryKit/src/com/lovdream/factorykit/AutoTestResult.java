package com.lovdream.factorykit;

import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.View;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Toast;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

import java.util.ArrayList;

import com.lovdream.factorykit.Config.TestItem;

public class AutoTestResult extends Fragment{

	private static final String TAG = Main.TAG;

	private String buildTestResult(){

		FactoryKitApplication app = (FactoryKitApplication)getActivity().getApplication();
		Config config = app.getTestConfig();
		ArrayList<TestItem> mItems = config.getTestItems();

		String failItems = "";
		for(TestItem item : mItems){
			if(item.inAutoTest && (config.getTestFlag(item.flagIndex) == Config.TEST_FLAG_FAIL)){
				failItems += item.displayName + "\n";
			}
		}

		if("".equals(failItems)){
			config.setAutoTestFt(true);
			return "整机自动测试\n所有测试通过!";
		}else{
			config.setAutoTestFt(false);
			return "整机自动测试\n部分测试项未通过：\n\n" + failItems;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		TextView tv = new TextView(getActivity());
		tv.setTextSize(24);
		tv.setText(buildTestResult());
		tv.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		return tv;
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		activity.setTitle(R.string.test_result);
	}

	@Override
	public void onDetach(){
		super.onDetach();
		getActivity().setTitle(R.string.app_name);
	}
}
