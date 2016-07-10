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

import javax.inject.Singleton;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import com.turios.activities.SettingsActivity;
import com.turios.activities.util.InstallLauncher;
import com.turios.dagger.DaggerPreferenceActivity;
import com.turios.dagger.quialifiers.ForActivity;
import com.turios.settings.global.ConnectUnitsSettings;
import com.turios.settings.global.GeneralSettings;
import com.turios.settings.global.ProfilesSettings;
import com.turios.settings.global.UpdateSettings;
import com.turios.settings.modules.AccessplansSettings;
import com.turios.settings.modules.BasisEntrySettings;
import com.turios.settings.modules.BasisSettings;
import com.turios.settings.modules.BrowserSettings;
import com.turios.settings.modules.DirectionsSettings;
import com.turios.settings.modules.DropboxSettings;
import com.turios.settings.modules.GoogleMapsSettings;
import com.turios.settings.modules.HydrantsSettings;
import com.turios.settings.modules.PicklistSettings;

import dagger.Module;
import dagger.Provides;

@Module(addsTo = ApplicationModule.class, injects = { SettingsActivity.class,
		ConnectUnitsSettings.class, GeneralSettings.class,
		ProfilesSettings.class, UpdateSettings.class,
		AccessplansSettings.class, BasisEntrySettings.class,
		BasisSettings.class, BrowserSettings.class, DirectionsSettings.class,
		DropboxSettings.class, GoogleMapsSettings.class,
		HydrantsSettings.class, PicklistSettings.class }, library = true)
public class ActivityPreferenceModule {

	private final DaggerPreferenceActivity activity;

	public ActivityPreferenceModule(DaggerPreferenceActivity activity) {
		this.activity = activity;
	}

	@Provides @Singleton Activity provideActivity() {
		return activity;
	}

	@Provides @Singleton @ForActivity Context provideActivityContext() {
		return activity;
	}

	@Provides @Singleton FragmentManager provideFragmentManager() {
		return activity.getFragmentManager();
	}

	@Provides @Singleton InstallLauncher provideInstallLauncher(
			Activity activity) {
		return new InstallLauncher(activity);
	}

}
