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
import com.turios.modules.extend.GoogleMapsModule;
import com.turios.modules.extend.HydrantsModule;
import com.turios.persistence.Preferences;
import com.turios.settings.modules.status.StatusCheck;
import com.turios.settings.modules.status.SummaryUpdater;

public class HydrantsSettings extends DaggerPreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private static final String TAG = "HydrantsSetings";

	private StatusCheck statusCheck;

	@Inject @ForActivity Context context;

	@Inject Preferences preferences;
	@Inject GoogleMapsModule googleMapsModule;

	@Inject HydrantsModule hydrantsModule;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.settings_module_hydrants);

	}

	@Override public void onStart() {
		updateSummaries();
		updateMaptypeSummary();

		statusCheck = new StatusCheck(context, hydrantsModule, this);
		statusCheck.runCheck();

		super.onStart();
	}

	@Override public void onResume() {
		PreferenceManager.getDefaultSharedPreferences(context)
				.registerOnSharedPreferenceChangeListener(this);

		super.onResume();
	}

	@Override public void onPause() {
		PreferenceManager.getDefaultSharedPreferences(context)
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	@Override public void onSharedPreferenceChanged(SharedPreferences arg0,
			String key) {

		if (key.equals(getString(R.string.key_module_activated_hydrants))) {
			statusCheck.runCheck();
		}

		updateMaptypeSummary();
		updateSummaries();
	}

	private void updateSummaries() {
		SummaryUpdater.updateInt(this, preferences,
				R.string.key_hydrants_radius, getString(R.string.meter));
		SummaryUpdater.updateInt(this, preferences,
				R.string.key_hydrants_setting_latitude);
		SummaryUpdater.updateInt(this, preferences,
				R.string.key_hydrants_setting_longitude);
		SummaryUpdater.updateInt(this, preferences,
				R.string.key_hydrants_setting_lock_latitude_decimal);
		SummaryUpdater.updateInt(this, preferences,
				R.string.key_hydrants_setting_lock_longitude_decimal);
		SummaryUpdater.updateInt(this, preferences,
				R.string.key_hydrants_setting_address);
		SummaryUpdater.updateInt(this, preferences,
				R.string.key_hydrants_setting_addressremark);
		SummaryUpdater.updateInt(this, preferences,
				R.string.key_hydrants_setting_addressnumber);
		SummaryUpdater.updateInt(this, preferences,
				R.string.key_hydrants_setting_remark);
	}

	private void updateMaptypeSummary() {
		Preference preference = (Preference) findPreference(getString(R.string.key_hydrants_maptype));
		if (preference != null) {
			int maptype = preferences
					.getStringInt(R.string.key_hydrants_maptype);
			preference.setSummary(googleMapsModule.mapType(maptype));
		}
	}

}