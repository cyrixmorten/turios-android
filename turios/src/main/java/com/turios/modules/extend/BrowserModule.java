package com.turios.modules.extend;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.content.Context;
import android.util.Log;

import com.turios.R;
import com.turios.dagger.quialifiers.ForApplication;
import com.turios.modules.ModulePreferences;
import com.turios.modules.core.ExpirationCoreModule;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.preferences.BrowserModulePreferences;
import com.turios.persistence.Preferences;

@Singleton
public class BrowserModule extends StandardModule {

	private final BrowserModulePreferences modulePreferences;



	public BrowserModulePreferences getPreferences() {
		return modulePreferences;
	}

	private static final String TAG = "BrowserModule";

	@Inject public BrowserModule(@ForApplication Context context,
			Preferences preferences, ExpirationCoreModule expiration,
			ParseCoreModule parse) {
		super(context, preferences, expiration, parse, MODULES.BROWSER);
		Log.i(TAG, "CREATING MODULE " + TAG);
		this.modulePreferences = new BrowserModulePreferences(preferences);
	}

}
