package com.turios.settings.modules;

import javax.inject.Inject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.turios.R;
import com.turios.dagger.DaggerPreferenceFragment;
import com.turios.dagger.quialifiers.ForActivity;
import com.turios.modules.extend.DirectionsModule;
import com.turios.modules.extend.GoogleMapsModule;
import com.turios.persistence.Preferences;
import com.turios.settings.modules.status.StatusCheck;

public class DirectionsSettings extends DaggerPreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private static final String TAG = "DirectionsSettings";

	@Inject @ForActivity Context context;
	@Inject Preferences preferences;

	@Inject GoogleMapsModule googleMapsModule;
	@Inject DirectionsModule directionsModule;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.settings_module_directions);

	}

	@Override public void onStart() {
		updateAllSummaries();

		statusCheck = new StatusCheck(context, directionsModule, this);
		statusCheck.runCheck();

		super.onStart();
	}

	private StatusCheck statusCheck;

	@Override public void onResume() {
		PreferenceManager.getDefaultSharedPreferences(context)
				.registerOnSharedPreferenceChangeListener(this);

		super.onResume();
	}

	private void updateAllSummaries() {

		updateMaptypeSummary();
	}

	@Override public void onPause() {
		PreferenceManager.getDefaultSharedPreferences(context)
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	@Override public void onSharedPreferenceChanged(SharedPreferences arg0,
			String key) {

		if (key.equals(getString(R.string.key_module_activated_directions))) {
			statusCheck.runCheck();
		}

		updateAllSummaries();
	}

	private void updateMaptypeSummary() {
		Preference preference = (Preference) findPreference(getString(R.string.key_directions_maptype));
		if (preference != null) {
			int maptype = preferences
					.getStringInt(R.string.key_directions_maptype);
			preference.setSummary(googleMapsModule.mapType(maptype));
		}
	}

}