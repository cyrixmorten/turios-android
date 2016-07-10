package com.turios.modules.preferences;

import com.turios.R;
import com.turios.modules.ModulePreferences;
import com.turios.persistence.Preferences;

public class ParseCoreModulePreferences extends BaseModulePreferences
		implements ModulePreferences {

	public ParseCoreModulePreferences(Preferences preferences) {
		super(preferences);
	}

	@Override
	public boolean isActivated() {
		return isActivated();
	}

	public boolean hasUsername() {
		return !preferences.getString(R.string.key_username).isEmpty();
	}

	public void setUsername(String username) {
		preferences.putString(R.string.key_username, username);
	}

	public String getUsername() {
		return preferences.getString(R.string.key_username);
	}

	public void setPassword(String password) {
		preferences.putString(R.string.key_password, password);
	}

	public String getPassword() {
		return preferences.getString(R.string.key_password);
	}

	public void setDevicename(String devicename) {
		preferences.putString(R.string.key_devicename, devicename);
	}

	public String getDevicename() {
		return preferences.getString(R.string.key_devicename);
	}

}
