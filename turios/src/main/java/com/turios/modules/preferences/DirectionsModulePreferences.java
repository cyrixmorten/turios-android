package com.turios.modules.preferences;

import com.androidmapsextensions.GoogleMap;
import com.turios.R;
import com.turios.modules.ModulePreferences;
import com.turios.persistence.Preferences;

public class DirectionsModulePreferences extends BaseModulePreferences implements ModulePreferences {

	public DirectionsModulePreferences(Preferences preferences) {
		super(preferences);
	}

	@Override public boolean isActivated() {
		return isActivated();
	}

	public boolean showTraffic() {
		return preferences.getBoolean(R.string.key_directions_traffic);
	}

	public int getMapType() {
		int maptype = preferences
				.getStringInt(R.string.key_directions_maptype);
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

}
