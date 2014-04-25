package com.paydayr.moneymanager;

import android.app.Application;
import android.content.Context;

public class MoneyManager extends Application {
	
	private static Context context;

	public void onCreate() {
		super.onCreate();
		MoneyManager.context = getApplicationContext();
	}

	public static Context getAppContext() {
		return MoneyManager.context;
	}
}
