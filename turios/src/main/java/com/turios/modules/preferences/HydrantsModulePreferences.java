package com.turios.modules.preferences;

import com.androidmapsextensions.GoogleMap;
import com.turios.R;
import com.turios.modules.ModulePreferences;
import com.turios.persistence.Preferences;

public class HydrantsModulePreferences extends BaseModulePreferences implements ModulePreferences {

	public HydrantsModulePreferences(Preferences preferences) {
		super(preferences);
	}

	@Override public boolean isActivated() {
		return isActivated();
	}

	public boolean showTraffic() {
		return preferences.getBoolean(R.string.key_hydrants_traffic);
	}

	public int getMapType() {
		int maptype = preferences
				.getStringInt(R.string.key_hydrants_maptype);
		switch (maptype) {
		case 0:
			return GoogleMap.MAP_TYPE_NORMAL;
		case 1:
			return GoogleMap.MAP_TYPE_SATELLITE;
		case 2:
			return GoogleMap.MAP_TYPE_TERRAIN;
		case 3:
			return GoogleMap.MAP_TYPE_HYBRID;
		default:
			return 0;
		}
	}

	public int getHydrantsLoadradius() {
		return preferences.getStringInt(R.string.key_hydrants_radius);
	}

	public int indexLatitude() {
		return preferences
				.getInt(R.string.key_hydrants_setting_latitude, 0) - 1;
	}

	public int indexLongitude() {
		return preferences.getInt(R.string.key_hydrants_setting_longitude,
				0) - 1;
	}

	// optional
	public int indexAddress() {
		return preferences.getInt(R.string.key_hydrants_setting_address, 0) - 1;
	}

	public int indexAddressNumber() {
		return preferences.getInt(
				R.string.key_hydrants_setting_addressnumber, 0) - 1;
	}

	public int indexAddressRemark() {
		return preferences.getInt(
				R.string.key_hydrants_setting_addressremark, 0) - 1;
	}

	public int indexRemark() {
		return preferences.getInt(R.string.key_hydrants_setting_remark, 0) - 1;
	}

	// decimals
	public int latitude_decimal() {
		return preferences.getInt(
				R.string.key_hydrants_setting_lock_latitude_decimal, 0);
	}

	public int longitude_decimal() {
		return preferences.getInt(
				R.string.key_hydrants_setting_lock_longitude_decimal, 0);
	}

}
