package com.turios.settings.modules;

import javax.inject.Inject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.turios.R;
import com.turios.dagger.DaggerPreferenceFragment;
import com.turios.dagger.quialifiers.ForActivity;
import com.turios.modules.extend.PicklistModule;
import com.turios.persistence.Preferences;
import com.turios.settings.modules.status.StatusCheck;
import com.turios.settings.modules.status.SummaryUpdater;

public class PicklistSettings extends DaggerPreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private static final String TAG = "PicklistSettings";

	@Inject Preferences preferences;

	@Inject @ForActivity Context context;
	@Inject PicklistModule picklistModule;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.settings_module_picklist);

	}

	private StatusCheck statusCheck;

	@Override public void onStart() {

		statusCheck = new StatusCheck(context, picklistModule, this);
		statusCheck.runCheck();

		super.onStart();
	}

	@Override public void onResume() {
		PreferenceManager.getDefaultSharedPreferences(context)
				.registerOnSharedPreferenceChangeListener(this);

		SummaryUpdater.updateEntry(this, preferences,
				R.string.key_picklist_entry,
				R.string.prefs_setting_summary_picklist);

		super.onResume();
	}

	@Override public void onPause() {
		PreferenceManager.getDefaultSharedPreferences(context)
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	@Override public void onSharedPreferenceChanged(SharedPreferences arg0,
			String key) {

		if (key.equals(getString(R.string.key_module_activated_picklist))) {
			statusCheck.runCheck();
		}

		SummaryUpdater.updateEntry(this, preferences,
				R.string.key_picklist_entry,
				R.string.prefs_setting_summary_picklist);
	}

}