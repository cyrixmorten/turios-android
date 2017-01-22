package com.turios.activities;

import android.content.Intent;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseSession;
import com.parse.ParseUser;
import com.turios.dagger.DaggerApplication;
import com.turios.services.Communicator;
import com.turios.wakefulservices.UpdateStateListener;

public class TuriosApplication extends DaggerApplication {

	private static final String TAG = "TuriosApplication";

	private boolean showSplashScreen;

	private static TuriosApplication instance;

	public static TuriosApplication getInstance() {
		return instance;
	}

	@Override public void onCreate() {

		instance = this;

		showSplashScreen = true;

		try {
			Class.forName("android.os.AsyncTask");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Parse.initialize(this, "VTvX9hidEEqijrvgihh4QK12Rqr1mlrzaq2OqcCT",
				"g8nSP32z5ij4lY7yRx62RiWjDAzyTeCsOKQSCzBr");

		ParseACL defaultACL = new ParseACL();
		defaultACL.setPublicWriteAccess(false);
		defaultACL.setPublicReadAccess(false);
		ParseACL.setDefaultACL(defaultACL, true);

		ParseUser.enableRevocableSessionInBackground();

		startService(new Intent(this, Communicator.class));

		WakefulIntentService.cancelAlarms(this);

		WakefulIntentService.scheduleAlarms(new UpdateStateListener(), this,
				true);

		super.onCreate();
	}

	public boolean shouldShowSplashScreen() {
		return showSplashScreen;
	}

	public void setShowSplashScreen(boolean showSplashScreen) {
		this.showSplashScreen = showSplashScreen;
	}

}
