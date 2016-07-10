package com.turios.settings.modules.status;

import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.turios.R;
import com.turios.persistence.Preferences;

public class SummaryUpdater {

	public static void updateString(PreferenceFragment pref,
			Preferences preferences, int resource) {
		updateString(pref, preferences, resource, "");
	}

	public static void updateString(PreferenceFragment pref,
			Preferences preferences, int resource, String postfix) {
		Preference preference = (Preference) pref.findPreference(pref
				.getString(resource));
		if (preference != null) {
			String summaryString = preferences.getString(resource);
			summaryString = (postfix.isEmpty()) ? summaryString : summaryString
					+ " " + postfix;

			if (!summaryString.isEmpty()) {
				preference.setSummary(summaryString);
			}
		}
	}

	public static void updateEntry(PreferenceFragment pref,
			Preferences preferences, int resourcekey, int resourcedefault) {
		Preference preference = pref
				.findPreference(pref.getString(resourcekey));
		if (preference != null) {
			int currentEntry = Integer.parseInt(preferences
					.getString(resourcekey));

			if (currentEntry != 0) {
				preference.setSummary(pref.getString(R.string.entry) + ": "
						+ currentEntry);
			} else {
				preference.setSummary(pref.getString(resourcedefault));
			}
		}
	}

	public static void updateInt(PreferenceFragment pref,
			Preferences preferences, int resource) {
		updateInt(pref, preferences, resource, "");
	}

	public static void updateInt(PreferenceFragment pref,
			Preferences preferences, int resource, String postfix) {
		Preference preference = (Preference) pref.findPreference(pref
				.getString(resource));
		if (preference != null) {
			String summaryString = "" + preferences.getStringInt(resource);
			summaryString = (postfix.isEmpty()) ? summaryString : summaryString
					+ " " + postfix;
			preference.setSummary(summaryString);
		}
	}
}
