package com.turios.modules.preferences;

import com.turios.R;
import com.turios.modules.ModulePreferences;
import com.turios.persistence.Preferences;

public class GoogleMapsModulePreferences extends BaseModulePreferences implements ModulePreferences {

	public GoogleMapsModulePreferences(Preferences preferences) {
		super(preferences);
	}

	@Override public boolean isActivated() {
		return isActivated();
	}

	public int getAddressEntry() {
		return preferences
				.getStringInt(R.string.key_googlemaps_address_entry);
	}

	public int getCityEntry() {
		return preferences.getStringInt(R.string.key_googlemaps_city_entry);
	}

}
