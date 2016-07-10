package com.turios.settings.modules;

import java.util.concurrent.TimeUnit;

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
import com.turios.modules.extend.BasisModule;
import com.turios.persistence.Preferences;
import com.turios.settings.modules.status.StatusCheck;
import com.turios.settings.modules.status.SummaryUpdater;

public class BasisSettings extends DaggerPreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private static final String TAG = "BasisSettings";

	private StatusCheck statusCheck;

	@Inject @ForActivity Context context;
	@Inject Preferences preferences;
	@Inject BasisModule basisModule;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.settings_module_basis);
	}

	@Override public void onStart() {
		updateSummaries();

		statusCheck = new StatusCheck(context, basisModule, this);
		statusCheck.runCheck();

		super.onStart();
	}

	private void updateSummaries() {
		updateAutoDeleteSummary();
		SummaryUpdater.updateString(this, preferences,
				R.string.key_basis_tabname);
	}

	@Override public void onResume() {
		PreferenceManager.getDefaultSharedPreferences(context)
				.registerOnSharedPreferenceChangeListener(this);

		super.onResume();
	}

	@Override public void onSharedPreferenceChanged(SharedPreferences prefs,
			String key) {

		if (key.equals(getString(R.string.key_module_activated_basis))) {
			statusCheck.runCheck();
		}

		updateSummaries();
	}

	@Override public void onPause() {
		PreferenceManager.getDefaultSharedPreferences(context)
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	private void updateAutoDeleteSummary() {
		ListPreference autodelete = (ListPreference) findPreference(getString(R.string.key_autodelete_values));
		if (autodelete != null) {
			long value = basisModule.getPreferences().getAutomaticDeleteTime();
			autodelete.setSummary(String.valueOf(TimeUnit.MINUTES.convert(
					value, TimeUnit.MILLISECONDS))
					+ " "
					+ getString(R.string.minutes));
		}
	}

}