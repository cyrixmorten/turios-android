package com.turios.settings.modules;

import javax.inject.Inject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import com.turios.R;
import com.turios.dagger.DaggerPreferenceFragment;
import com.turios.dagger.quialifiers.ForActivity;
import com.turios.modules.extend.BrowserModule;
import com.turios.persistence.Preferences;
import com.turios.settings.modules.status.StatusCheck;
import com.turios.settings.modules.status.SummaryUpdater;

public class BrowserSettings extends DaggerPreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private static final String TAG = "BrowserSettings";

	@Inject @ForActivity Context context;
	@Inject Preferences preferences;
	@Inject BrowserModule browserModule;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.settings_module_browser);

	}

	@Override public void onStart() {

		updateAllSummaries();

		SwitchPreference advanced = (SwitchPreference) findPreference(getString(R.string.key_browser_advanced));
		advanced.setChecked(false);

		statusCheck = new StatusCheck(context, browserModule, this);
		statusCheck.runCheck();

		super.onStart();
	}

	private StatusCheck statusCheck;

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

		if (key.equals(getString(R.string.key_module_activated_browser))) {
			statusCheck.runCheck();
		}

		updateAllSummaries();
	}

	private void updateAllSummaries() {
		SummaryUpdater.updateString(this, preferences,
				R.string.key_browser_tabname);
		SummaryUpdater
				.updateString(this, preferences, R.string.key_browser_url);
		SummaryUpdater.updateString(this, preferences,
				R.string.key_browser_input_name_1);
		SummaryUpdater.updateString(this, preferences,
				R.string.key_browser_input_value_1);
		SummaryUpdater.updateString(this, preferences,
				R.string.key_browser_input_name_2);
		SummaryUpdater.updateString(this, preferences,
				R.string.key_browser_input_value_2);
	}

}