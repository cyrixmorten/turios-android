package com.turios.settings.global;

import android.os.Bundle;

import com.turios.R;
import com.turios.dagger.DaggerPreferenceFragment;

public class GeneralSettings extends DaggerPreferenceFragment {

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings_general);

	}
}