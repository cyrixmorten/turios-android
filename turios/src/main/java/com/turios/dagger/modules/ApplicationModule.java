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

import static android.content.Context.LOCATION_SERVICE;

import javax.inject.Singleton;

import android.content.Context;
import android.location.LocationManager;

import com.google.android.gms.location.LocationRequest;
import com.turios.dagger.DaggerApplication;
import com.turios.dagger.quialifiers.ForApplication;
import com.turios.modules.core.DatabaseCoreModule;
import com.turios.modules.core.DisplayCoreModule;
import com.turios.modules.core.ExpirationCoreModule;
import com.turios.modules.core.LocationsCoreModule;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.core.PathsCoreModule;
import com.turios.modules.extend.AccessplansModule;
import com.turios.modules.extend.BasisModule;
import com.turios.modules.extend.BrowserModule;
import com.turios.modules.extend.DirectionsModule;
import com.turios.modules.extend.DropboxModule;
import com.turios.modules.extend.GoogleMapsModule;
import com.turios.modules.extend.HydrantsModule;
import com.turios.modules.extend.PicklistModule;
import com.turios.persistence.Preferences;
import com.turios.sms.ParseSMSReceiver;
import com.turios.sms.SMSReceiver;
import com.turios.sms.TuriosMessageReceiver;
import com.turios.util.Device;
import com.turios.wakefulservices.UpdateStateService;

import dagger.Module;
import dagger.Provides;

/**
 * A module for Android-specific dependencies which require a {@link Context} or
 * {@link android.app.Application} to create.
 */

// Injecting all modules to make them global singletons

@Module(injects = { Preferences.class, DatabaseCoreModule.class,
		DisplayCoreModule.class, ExpirationCoreModule.class,
		LocationsCoreModule.class, ParseCoreModule.class,
		PathsCoreModule.class, AccessplansModule.class, BasisModule.class,
		BrowserModule.class, DirectionsModule.class, DropboxModule.class,
		GoogleMapsModule.class, HydrantsModule.class, PicklistModule.class,
		ParseSMSReceiver.class, SMSReceiver.class, TuriosMessageReceiver.class, UpdateStateService.class }, library = true)
public class ApplicationModule {
	private final DaggerApplication application;

	public ApplicationModule(DaggerApplication application) {
		this.application = application;
	}

	/**
	 * Allow the application context to be injected but require that it be
	 * annotated with {@link ForApplication @ForApplication} to explicitly
	 * differentiate it from an activity context.
	 */
	@Provides @Singleton @ForApplication Context provideApplicationContext() {
		return application;
	}

	@Provides @Singleton Device provideDevice(@ForApplication Context context) {
		return new Device(context);
	}

	@Provides @Singleton LocationManager provideLocationManager() {
		return (LocationManager) application.getSystemService(LOCATION_SERVICE);
	}

	@Provides LocationRequest provideLocationRequest() {
		// Create the LocationRequest object
		LocationRequest mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval to 5 seconds
		mLocationRequest.setInterval(5000);
		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(1000);

		return mLocationRequest;
	}

}
