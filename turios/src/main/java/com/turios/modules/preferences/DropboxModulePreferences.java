package com.turios.modules.preferences;

import com.turios.R;
import com.turios.modules.ModulePreferences;
import com.turios.persistence.Preferences;

public class DropboxModulePreferences extends BaseModulePreferences implements ModulePreferences {

	public DropboxModulePreferences(Preferences preferences) {
		super(preferences);
	}

	@Override public boolean isActivated() {
		return isActivated();
	}

	public boolean isDailySyncEnabled() {
		return preferences.getBoolean(R.string.key_dropbox_daily_sync);
	}

	public boolean deleteFilesDuringSync() {
		return preferences
				.getBoolean(R.string.key_dropbox_delete_during_sync);
	}

}
