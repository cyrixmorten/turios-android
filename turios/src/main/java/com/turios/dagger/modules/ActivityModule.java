/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.turios.dagger.modules;

import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuInflater;

import com.turios.activities.SettingsActivity;
import com.turios.activities.SplashScreen;
import com.turios.activities.Turios;
import com.turios.activities.fragments.BrowserFragment;
import com.turios.activities.fragments.DisplayFragment;
import com.turios.activities.fragments.GoogleMapFragment;
import com.turios.activities.fragments.GoogleMapOptionsFragment;
import com.turios.activities.listeners.TuriosUICallback;
import com.turios.activities.setup.SetupDone;
import com.turios.activities.setup.SetupDropbox;
import com.turios.activities.setup.SetupPDFViewer;
import com.turios.activities.setup.SetupProfiles;
import com.turios.activities.util.Orientation;
import com.turios.activities.util.WakeScreen;
import com.turios.dagger.DaggerActivity;
import com.turios.dagger.quialifiers.ForActivity;
import com.turios.dagger.quialifiers.ForApplication;
import com.turios.modules.extend.BasisModule;
import com.turios.modules.extend.DropboxModule;
import com.turios.modules.extend.DropboxModule.DropboxFileDownloaderResultReceiver;
import com.turios.util.Notifications;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * This module represents objects which exist only for the scope of a single
 * activity. We can safely create singletons using the activity instance because
 * ths entire object graph will only ever exist inside of that activity.
 */
@Module(addsTo = ApplicationModule.class, injects = { SplashScreen.class,
		Turios.class, DisplayFragment.class, GoogleMapFragment.class,
		GoogleMapOptionsFragment.class, BrowserFragment.class,
		SettingsActivity.class, SetupProfiles.class,
		SetupPDFViewer.class, SetupDropbox.class, SetupDone.class }, library = true)
public class ActivityModule {

	private static final String TAG = ActivityModule.class.getSimpleName();

	private final DaggerActivity activity;

	public ActivityModule(DaggerActivity activity) {
		this.activity = activity;
	}

	@Provides @Singleton WakeScreen provideWakeScreen(
			@ForApplication Context context, DaggerActivity activity,
			BasisModule basis, Notifications notifications) {
		return new WakeScreen(context, activity, basis, notifications);
	}

	@Provides @Singleton Orientation provideOrientation(DaggerActivity activity) {
		return new Orientation(activity);
	}

	@Provides @Singleton DaggerActivity provideDaggerActivity() {
		return activity;
	}

	@Provides @Singleton @ForActivity Context provideActivityContext() {
		return activity;
	}

	@Provides @Singleton TuriosUICallback provideTuriosUICallback() {
		if (activity instanceof TuriosUICallback) {
			return (TuriosUICallback) activity;
		}
		return null;
	}

	@Provides @Singleton ActionBar provideActionBar() {
		return activity.getActionBar();
	}

	@Provides @Singleton MenuInflater provideMenuInflater() {
		return activity.getMenuInflater();
	}

	@Provides @Singleton LayoutInflater provideLayoutInflater() {
		return activity.getLayoutInflater();
	}

	@Provides @Singleton FragmentManager provideFragmentManager() {
		return activity.getSupportFragmentManager();
	}

	@Provides @Singleton TabListener provideTabListener() {
		if (activity instanceof TabListener) {
			return (TabListener) activity;
		}
		return null;
	}

	@Provides @Singleton Handler provideHandler() {
		return new Handler(activity.getMainLooper());
	}

	@Provides @Singleton DropboxFileDownloaderResultReceiver provideDropboxFileDownloaderResultReceiver(
			DropboxModule dropbox, Handler handler) {
		return dropbox.new DropboxFileDownloaderResultReceiver(handler);
	}

}
