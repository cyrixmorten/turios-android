package com.turios.modules.preferences;

import com.turios.R;
import com.turios.modules.ModulePreferences;
import com.turios.persistence.Preferences;

public class BrowserModulePreferences extends BaseModulePreferences implements ModulePreferences {

	public static final String DEFAULT_URL = "www.turios.dk/category/blog/";
	
	public BrowserModulePreferences(Preferences preferences) {
		super(preferences);
	}

	@Override public boolean isActivated() {
		return isActivated();
	}

	public String url() {
		String setUrl = preferences.getString(R.string.key_browser_url);
		if (setUrl.isEmpty()) {
			return DEFAULT_URL;
		}
		return "http://" + setUrl;
	}

	public boolean autoSubmitEnabled() {
		return preferences.getBoolean(R.string.key_browser_autosubmit);
	}

	public String inputName1() {
		return preferences.getString(R.string.key_browser_input_name_1);
	}

	public String inputName2() {
		return preferences.getString(R.string.key_browser_input_name_2);
	}

	public String inputValue1() {
		return preferences.getString(R.string.key_browser_input_value_1);
	}

	public String inputValue2() {
		return preferences.getString(R.string.key_browser_input_value_2);
	}

	public boolean supportZoom() {

		return preferences.getBoolean(R.string.key_browser_supportzoom);
	}

	public boolean builtInZoomControls() {

		return preferences.getBoolean(R.string.key_browser_builtinzoom);
	}

	public boolean displayZoomControls() {
		return preferences
				.getBoolean(R.string.key_browser_displayzoomcontrol);
	}

	public boolean javaScriptEnabled() {
		return preferences
				.getBoolean(R.string.key_browser_javascriptenabled);
	}

	public boolean javaScriptCanOpenWindowsAutomatically() {
		return preferences
				.getBoolean(R.string.key_browser_javascriptcanopenwindowsautomatically);
	}

	public boolean allowFileAccess() {
		return preferences.getBoolean(R.string.key_browser_allowfileaccess);
	}

	public boolean domStorageEnabled() {
		return preferences
				.getBoolean(R.string.key_browser_domstorageenabled);
	}

}
