package com.turios.settings.modules;

import javax.inject.Inject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;

import com.turios.R;
import com.turios.dagger.DaggerPreferenceFragment;
import com.turios.dagger.quialifiers.ForActivity;
import com.turios.modules.extend.GoogleMapsModule;
import com.turios.persistence.Preferences;
import com.turios.settings.modules.status.StatusCheck;
import com.turios.settings.modules.status.SummaryUpdater;

public class GoogleMapsSettings extends DaggerPreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private static final String TAG = "GoogleMapsSetings";

	private StatusCheck statusCheck;

	private ListPreference address_entry;
	private ListPreference city_entry;

	@Inject @ForActivity Context context;
	@Inject Preferences preferences;
	@Inject GoogleMapsModule googleMapsModule;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.settings_module_googlemaps);

		address_entry = (ListPreference) getPreferenceScreen().findPreference(
				getString(R.string.key_googlemaps_address_entry));

		city_entry = (ListPreference) getPreferenceScreen().findPreference(
				getString(R.string.key_googlemaps_city_entry));

	}

	@Override public void onStart() {
		updateAllSummaries();

		statusCheck = new StatusCheck(context, googleMapsModule, this);
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

	@Override public void onSharedPreferenceChanged(SharedPreferences prefs,
			String key) {

		if (key.equals(getString(R.string.key_module_activated_googlemaps))) {
			statusCheck.runCheck();
		}

		updateAllSummaries();
	}

	private void updateAllSummaries() {
		SummaryUpdater.updateEntry(this, preferences,
				R.string.key_googlemaps_address_entry,
				R.string.prefs_setting_summary_address);
		SummaryUpdater.updateEntry(this, preferences,
				R.string.key_googlemaps_city_entry,
				R.string.prefs_setting_summary_city);
		// updateAddressSummary();
		// updateCitySummary();
	}

	// private void updateAddressSummary() {
	// if (address_entry != null) {
	// int currentEntry = googleMapsModule.getPreferences().getAddressEntry();
	//
	// if (currentEntry != 0) {
	// address_entry.setSummary(context.getString(R.string.entry)
	// + ": " + currentEntry);
	// } else {
	// address_entry.setSummary(context
	// .getString(R.string.prefs_setting_summary_address));
	// }
	// }
	// }
	//
	// private void updateCitySummary() {
	// if (city_entry != null) {
	// int currentEntry = Integer.parseInt(Preferences.getString(
	// context, R.string.key_googlemaps_city_entry));
	//
	// if (currentEntry != 0) {
	// city_entry.setSummary(context.getString(R.string.entry) + ": "
	// + currentEntry);
	// } else {
	// city_entry.setSummary(context
	// .getString(R.string.prefs_setting_summary_city));
	// }
	// }
	// }

}