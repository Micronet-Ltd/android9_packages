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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.LayoutInflater;
import android.widget.Toast;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

import java.util.ArrayList;

import com.lovdream.factorykit.Config.TestItem;

public class AutoTest extends Fragment implements TestItemBase.TestCallback{

	private static final String TAG = Main.TAG;

	private TestItemFactory mFactory;
	private ArrayList<TestItem> mItems;
	private int mCurrentIndex;
	
	private final int AUTO_TEST_TIME_INTERVAL = 300;
	private Handler mHandler = new Handler();
	private boolean quitTest;

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);

		FactoryKitApplication app = (FactoryKitApplication)getActivity().getApplication();
		mItems = new ArrayList<TestItem>();
		ArrayList<TestItem> allItems = app.getTestConfig().getTestItems();
		for(TestItem item : allItems){
			if(item.inAutoTest){
				mItems.add(item);
			}
		}
		mFactory = TestItemFactory.getInstance(getActivity());

		mCurrentIndex = 0;
		mHandler.postDelayed(mAutoTestRunnale, AUTO_TEST_TIME_INTERVAL);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		TextView tv = new TextView(getActivity());
		tv.setTextSize(24);
		tv.setText(R.string.auto_test_msg);
		tv.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		return tv;
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		activity.setTitle(R.string.auto_test);
		setHasOptionsMenu(true);
		quitTest = false;
		Log.d(TAG,"AutoTest onAttach");
	}

	@Override
	public void onDetach(){
		super.onDetach();
		getActivity().setTitle(R.string.app_name);
		Log.d(TAG,"AutoTest onDetach");
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
		menu.add(R.string.quit_auto_test);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		mHandler.removeCallbacks(mAutoTestRunnale);

		FragmentManager fm = getActivity().getFragmentManager();
		fm.popBackStack();

		quitTest = true;

		return true;
	}

	@Override
	public void onTestFinish(TestItemBase item){
		if(quitTest){
			FragmentManager fm = getActivity().getFragmentManager();
			fm.popBackStack();
			return;
		}
		mHandler.postDelayed(mAutoTestRunnale, AUTO_TEST_TIME_INTERVAL);
	}

	private Runnable mAutoTestRunnale = new Runnable(){
		@Override
		public void run(){
			if((mCurrentIndex >= 0) && (mCurrentIndex < mItems.size())){
				
				Log.d(TAG,"mAutoTestRunnale,mCurrentIndex:" + mCurrentIndex);
				TestItem item = (TestItem)mItems.get(mCurrentIndex++);
				if(!item.inAutoTest){
					onTestFinish(null);
					return;
				}
				TestItemBase fragment = mFactory.createTestItem(getActivity(),item);

				if(fragment == null){
					Toast.makeText(getActivity(),R.string.no_item,Toast.LENGTH_SHORT).show();
					onTestFinish(fragment);
					return;
				}

				if(fragment.isAdded()){
					onTestFinish(fragment);
					return;
				}

				fragment.setTestCallback(AutoTest.this);
				fragment.setAutoTest(true);
				FragmentManager fm = getFragmentManager();
				if(fm == null){
					Log.e(TAG,"in mAutoTestRunnale,fm == null");
					return;
				}
				FragmentTransaction ft = fm.beginTransaction();
				ft.add(android.R.id.content,fragment,item.key);
				ft.addToBackStack(item.key);
				ft.commit();
				//getActivity().setTitle(item.displayName);
			}else{
				finishAndShowResult();
			}
		}
	};

	private void finishAndShowResult(){
		FragmentManager fm = getActivity().getFragmentManager();
		fm.popBackStack();

		Fragment fragment = Fragment.instantiate(getActivity(),AutoTestResult.class.getName());
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(android.R.id.content,fragment);
		ft.addToBackStack("result");
		ft.commit();
	}
}
