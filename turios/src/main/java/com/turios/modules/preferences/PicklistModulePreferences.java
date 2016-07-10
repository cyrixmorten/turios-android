package com.turios.modules.preferences;

import com.turios.modules.ModulePreferences;
import com.turios.persistence.Preferences;

public class PicklistModulePreferences extends BaseModulePreferences implements ModulePreferences {

	public PicklistModulePreferences(Preferences preferences) {
		super(preferences);
	}

	@Override public boolean isActivated() {
		return isActivated();
	}

	public int getPicklistEntry() {
		return preferences
				.getStringInt(com.turios.R.string.key_picklist_entry);
	}

}
