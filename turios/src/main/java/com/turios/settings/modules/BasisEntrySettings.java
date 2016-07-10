package com.turios.settings.modules;

import javax.inject.Inject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;

import com.turios.R;
import com.turios.dagger.DaggerPreferenceFragment;
import com.turios.dagger.quialifiers.ForActivity;
import com.turios.modules.extend.BasisModule;
import com.turios.modules.preferences.BasisModulePreferences;

public class BasisEntrySettings extends DaggerPreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private static final String TAG = "BasisEntrySettings";

	private EditTextPreference standardValue;
	private EditTextPreference prefix;
	private EditTextPreference postfix;

	private BasisModulePreferences basisPreferences;

	@Inject BasisModule basis;
	@Inject @ForActivity Context context;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override public void onStart() {
		Bundle arguments = getArguments();
		if (arguments != null) {
			int line = getArguments().getInt(getString(R.string.key_entry));

			basisPreferences = basis.getPreferences();

			switch (line) {
			case 1:
				addPreferencesFromResource(R.xml.settings_entry_1);
				standardValue = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_standardvalue1));
				prefix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_prefix1));
				postfix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_postfix1));
				break;
			case 2:
				addPreferencesFromResource(R.xml.settings_entry_2);
				standardValue = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_standardvalue2));
				prefix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_prefix2));
				postfix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_postfix2));
				break;
			case 3:
				addPreferencesFromResource(R.xml.settings_entry_3);
				standardValue = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_standardvalue3));
				prefix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_prefix3));
				postfix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_postfix3));
				break;
			case 4:
				addPreferencesFromResource(R.xml.settings_entry_4);
				standardValue = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_standardvalue4));
				prefix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_prefix4));
				postfix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_postfix4));
				break;
			case 5:
				addPreferencesFromResource(R.xml.settings_entry_5);
				standardValue = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_standardvalue5));
				prefix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_prefix5));
				postfix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_postfix5));
				break;
			case 6:
				addPreferencesFromResource(R.xml.settings_entry_6);
				standardValue = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_standardvalue6));
				prefix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_prefix6));
				postfix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_postfix6));

				break;
			case 7:
				addPreferencesFromResource(R.xml.settings_entry_7);
				standardValue = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_standardvalue7));
				prefix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_prefix7));
				postfix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_postfix7));

				break;
			case 8:
				addPreferencesFromResource(R.xml.settings_entry_8);
				standardValue = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_standardvalue8));
				prefix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_prefix8));
				postfix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_postfix8));

				break;
			case 9:
				addPreferencesFromResource(R.xml.settings_entry_9);
				standardValue = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_standardvalue9));
				prefix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_prefix9));
				postfix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_postfix9));
				break;
			case 10:
				addPreferencesFromResource(R.xml.settings_entry_10);
				standardValue = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_standardvalue10));
				prefix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_prefix10));
				postfix = (EditTextPreference) getPreferenceScreen()
						.findPreference(getString(R.string.key_postfix10));
				break;

			default:
				break;
			}

			if (standardValue != null && prefix != null && postfix != null) {
				setSummaryStandardvalue(basisPreferences.getStandardValue(line));
				setSummaryPrefix(basisPreferences.getPrefix(line));
				setSummaryPostfix(basisPreferences.getPostfix(line));
			}
		}
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

		if (key.contains("standardvalue")) {
			String value = prefs.getString(key, "");
			setSummaryStandardvalue(value);
		}
		if (key.contains("prefix")) {
			String value = prefs.getString(key, "");
			setSummaryPrefix(value);
		}
		if (key.contains("postfix")) {
			String value = prefs.getString(key, "");
			setSummaryPostfix(value);
		}
	}

	private void setSummaryStandardvalue(String value) {
		value = (value == null || value.isEmpty()) ? context
				.getString(R.string.prefs_setting_summary_standardvalue)
				: value;
		standardValue.setSummary(value);
	}

	private void setSummaryPrefix(String value) {
		value = (value == null || value.isEmpty()) ? context
				.getString(R.string.prefs_setting_summary_prefix) : value;
		prefix.setSummary(value);
	}

	private void setSummaryPostfix(String value) {
		value = (value == null || value.isEmpty()) ? context
				.getString(R.string.prefs_setting_summary_suffix) : value;
		postfix.setSummary(value);
	}

}