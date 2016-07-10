package com.turios.modules.preferences;

import com.turios.modules.ModulePreferences;
import com.turios.persistence.Preferences;

public class AccessplansModulePreferences extends BaseModulePreferences
		implements ModulePreferences {

	public AccessplansModulePreferences(Preferences preferences) {
		super(preferences);
	}

	@Override
	public boolean isActivated() {
		return isActivated();
	}

	public int getAccessPlanEntry() {
		return preferences
				.getStringInt(com.turios.R.string.key_accessplan_entry);
	}

}
