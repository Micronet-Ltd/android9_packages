
package com.lovdream.factorykit;

import android.app.Application;

public class FactoryKitApplication extends Application{

	private Config mConfig;
	private TestItemFactory mTestItemFactory;

	@Override
	public void onCreate(){
		super.onCreate();
		
		Thread.setDefaultUncaughtExceptionHandler(CrashHandler.getInstance());

		mTestItemFactory = TestItemFactory.getInstance(this);
		mConfig = Config.getInstance(this);
		mConfig.loadConfig();
	}

	public TestItemFactory getTestItemFactory(){
		return mTestItemFactory;
	}

	public Config getTestConfig(){
		return mConfig;
	}
}
