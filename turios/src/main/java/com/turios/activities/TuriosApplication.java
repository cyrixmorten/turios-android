package com.turios.activities;

import android.content.Intent;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.crashlytics.android.Crashlytics;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;
import com.turios.BuildConfig;
import com.turios.dagger.DaggerApplication;
import com.turios.services.Communicator;
import com.turios.wakefulservices.UpdateStateListener;

import io.fabric.sdk.android.Fabric;

public class TuriosApplication extends DaggerApplication {

	private static final String TAG = "TuriosApplication";

	private boolean showSplashScreen;

	private static TuriosApplication instance;

	public static TuriosApplication getInstance() {
		return instance;
	}

	@Override public void onCreate() {

		Fabric.with(this, new Crashlytics());

		instance = this;

		showSplashScreen = true;

		try {
			Class.forName("android.os.AsyncTask");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);

		Parse.initialize(new Parse.Configuration.Builder(this)
				.applicationId("turios")
				.clientKey(null)
				.server(BuildConfig.PARSE_SERVER_URL)
				.build()
		);

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
