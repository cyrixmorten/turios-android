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
import com.turios.modules.preferences.BasisModulePreferences;
import com.turios.persistence.Preferences;

@Singleton
public class BasisModule extends StandardModule {

	private final BasisModulePreferences modulePreferences;



	public BasisModulePreferences getPreferences() {
		return modulePreferences;
	}

	private static final String TAG = "BasisModule";


	@Inject public BasisModule(@ForApplication Context context,
			Preferences preferences, ExpirationCoreModule expiration,
			ParseCoreModule parse) {
		super(context, preferences, expiration, parse, MODULES.BASIS);
		Log.i(TAG, "CREATING MODULE " + TAG);
		this.modulePreferences = new BasisModulePreferences(preferences);
	}

	public String addPrefixSuffixfixStandardToString(String string, int line) {
		return modulePreferences.getPrefix(line)
				+ ((!string.isEmpty()) ? string : modulePreferences
						.getStandardValue(line))
				+ modulePreferences.getPostfix(line);
	}

}
