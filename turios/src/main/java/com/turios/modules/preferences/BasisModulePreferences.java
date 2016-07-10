package com.turios.modules.preferences;

import com.turios.R;
import com.turios.modules.ModulePreferences;
import com.turios.persistence.Preferences;

public class BasisModulePreferences extends BaseModulePreferences implements ModulePreferences {

	public BasisModulePreferences(Preferences preferences) {
		super(preferences);
	}

	@Override public boolean isActivated() {
		return isActivated();
	}

	public boolean wakeScreenOnIncommingMessage() {
		return preferences.getBoolean(R.string.key_wakescreen);
	}

	public String notificationSoundUri() {
		return preferences.getString(R.string.key_notificationsound);
	}

	public String getStandardValue(int line) {
		return preferences.getStandardValue(line);
	}

	public String getPrefix(int line) {
		return preferences.getPrefix(line);
	}

	public String getPostfix(int line) {
		return preferences.getPostfix(line);
	}

	public long getAutomaticDeleteTime() {
		return Long.parseLong(preferences
				.getString(R.string.key_autodelete_values));
	}

	public boolean isAutomaticDeleteEnabled() {
		return preferences.getBoolean(R.string.key_autodelete);
	}
}
