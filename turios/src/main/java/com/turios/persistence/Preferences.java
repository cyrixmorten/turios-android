package com.turios.persistence;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.turios.R;
import com.turios.dagger.quialifiers.ForApplication;
import com.turios.modules.Module.MODULES;

@Singleton
public class Preferences {

	private final String TAG = Preferences.class.getName();

	private final SharedPreferences sharedPrefs;
	private final Context context;

	@Inject public Preferences(@ForApplication Context context) {
		this.context = context;
		this.sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		PreferenceManager.setDefaultValues(context,
				com.turios.R.xml.settings_general, true);
		PreferenceManager.setDefaultValues(context,
				com.turios.R.xml.settings_update, true);
		PreferenceManager.setDefaultValues(context,
				com.turios.R.xml.settings_module_basis, true);
		PreferenceManager.setDefaultValues(context,
				com.turios.R.xml.settings_module_picklist, true);
		PreferenceManager.setDefaultValues(context,
				com.turios.R.xml.settings_module_accessplans, true);
		PreferenceManager.setDefaultValues(context,
				com.turios.R.xml.settings_module_dropbox, true);
		PreferenceManager.setDefaultValues(context,
				com.turios.R.xml.settings_module_googlemaps, true);
		PreferenceManager.setDefaultValues(context,
				com.turios.R.xml.settings_module_hydrants, true);
		PreferenceManager.setDefaultValues(context,
				com.turios.R.xml.settings_module_directions, true);
		PreferenceManager.setDefaultValues(context,
				com.turios.R.xml.settings_module_browser, true);
		PreferenceManager.setDefaultValues(context,
				com.turios.R.xml.settings_entry_1, true);
		PreferenceManager.setDefaultValues(context,
				com.turios.R.xml.settings_entry_2, true);
		PreferenceManager.setDefaultValues(context,
				com.turios.R.xml.settings_entry_3, true);
		PreferenceManager.setDefaultValues(context,
				com.turios.R.xml.settings_entry_5, true);
		PreferenceManager.setDefaultValues(context,
				com.turios.R.xml.settings_entry_6, true);
		PreferenceManager.setDefaultValues(context,
				com.turios.R.xml.settings_entry_7, true);
		PreferenceManager.setDefaultValues(context,
				com.turios.R.xml.settings_entry_8, true);
		PreferenceManager.setDefaultValues(context,
				com.turios.R.xml.settings_entry_9, true);
		PreferenceManager.setDefaultValues(context,
				com.turios.R.xml.settings_entry_10, true);

	}

	public void convertStringToInt(int resource) {

		String key = context.getString(resource, "");

		if (!key.isEmpty()) {
			try {
				sharedPrefs.getInt(key, 0);
			} catch (ClassCastException e) {
				String value = sharedPrefs.getString(key, "");
				if (!value.isEmpty()) {
					try {
						int intValue = Integer.parseInt(value);
						sharedPrefs.edit().putInt(key, intValue).commit();
					} catch (NumberFormatException e2) {
						sharedPrefs.edit().putInt(key, 0).commit();
					}
				}
			}
		}
	}

	// public void convertIntToString( int resource) {
	// SharedPreferences sharedPrefs = PreferenceManager
	// .getDefaultSharedPreferences(context);
	//
	// String key = context.getString(resource, "");
	//
	// if (!key.isEmpty()) {
	// int intValue = sharedPrefs.getInt(key, 0);
	// sharedPrefs.edit().putString(key, "" + intValue).commit();
	// }
	// }

	public void setPrivateBoolean(String key, boolean value) {
		context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
				.edit().putBoolean(key, value).commit();
	}

	public boolean readPrivateBoolean(String key, boolean deafultValue) {
		return context.getSharedPreferences(key, Context.MODE_PRIVATE)
				.getBoolean(key, deafultValue);
	}

	public void setModuleAcitve(MODULES module, boolean activated) {

		int resource = moduleResource(module);
		String key = context.getString(resource);

		sharedPrefs.edit().putBoolean(key, activated).commit();

		Log.d(TAG, "setModuleAcitve: " + key + " " + activated);
	}

	public boolean isModuleAcitve(MODULES module) {

		int resource = moduleResource(module);

		return getBoolean(resource);

	}

	private int moduleResource(MODULES module) {
		switch (module) {
		case ACCESSPLANS:
			return R.string.key_module_activated_accessplan;
		case BASIS:
			return R.string.key_module_activated_basis;
		case BROWSER:
			return R.string.key_module_activated_browser;
		case DIRECTIONS:
			return R.string.key_module_activated_directions;
		case DROPBOX:
			return R.string.key_module_activated_dropbox;
		case GOOGLEMAPS:
			return R.string.key_module_activated_googlemaps;
		case HYDRANTS:
			return R.string.key_module_activated_hydrants;
		case PICKLISTS:
			return R.string.key_module_activated_picklist;
		default:
			Log.e(TAG, "Resource lookup failed for " + module.toString() + "!!");
			return -1;
		}
	}

	public Map<String, ?> getAll() {
		return sharedPrefs.getAll();
	}

	@SuppressWarnings("unchecked") public void loadPreferences(
			Map<String, ?> prefs) {
		for (String key : prefs.keySet()) {
			Object pref = prefs.get(key);
			if (pref instanceof Boolean) {
				sharedPrefs.edit().putBoolean(key, (Boolean) pref).commit();
			}
			if (pref instanceof Float) {
				sharedPrefs.edit().putFloat(key, (Float) pref).commit();
			}
			if (pref instanceof Integer) {
				sharedPrefs.edit().putInt(key, (Integer) pref).commit();
			}
			if (pref instanceof Long) {
				sharedPrefs.edit().putLong(key, (Long) pref).commit();
			}
			if (pref instanceof String) {
				sharedPrefs.edit().putString(key, (String) pref).commit();
			}
			if (pref instanceof Set<?>) {
				sharedPrefs.edit().putStringSet(key, (Set<String>) pref)
						.commit();
			}
		}

	}

	public void putInt(String key, int value) {
		sharedPrefs.edit().putInt(key, value).commit();
	}

	public boolean getBoolean(int resource) {
		String key = context.getString(resource);
		if (sharedPrefs != null && key != null) {
			return sharedPrefs.getBoolean(key, false);
		}
		return false;
	}

	public void putString(int resource, String value) {
		String key = context.getString(resource);
		boolean success = sharedPrefs.edit().putString(key, value).commit();
	}

	public String getString(int resource) {
		String key = context.getString(resource);
		if (sharedPrefs != null && context.getString(resource) != null) {
			return sharedPrefs.getString(key, "");
		}
		return "";
	}

	public int getStringInt(int resource) {

		String key = context.getString(resource);

		if (key != null && !key.isEmpty()) {

			// convertIntToString( resource);

			try {
				return sharedPrefs.getInt(key, 0);
			} catch (ClassCastException e) {
				return Integer.parseInt(sharedPrefs.getString(key, "0"));
			}

		}
		return 0;
	}

	public int getInt(int resource, int defaultValue) {

		return sharedPrefs.getInt(context.getString(resource), defaultValue);
	}

	public int getInt(String key, int defaultValue) {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		return sharedPrefs.getInt(key, defaultValue);
	}

	public boolean isFirstStartUp() {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sharedPrefs.getBoolean(
				context.getString(R.string.key_first_startup), true);
	}

	public void setisFirstStartUp(boolean value) {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		sharedPrefs
				.edit()
				.putBoolean(context.getString(R.string.key_first_startup),
						value).commit();
	}

	public boolean isAccessplan(int line) {
		return line == getStringInt(R.string.key_accessplan_entry);
	}

	public boolean isPicklist(int line) {
		return line == getStringInt(R.string.key_picklist_entry);
	}

	public boolean isAddress(int line) {
		return line == getStringInt(R.string.key_googlemaps_address_entry);
	}

	public boolean isCity(int line) {
		return line == getStringInt(R.string.key_googlemaps_city_entry);
	}

	public String getStandardValue(int line) {
		switch (line) {
		case 1:
			return getString(R.string.key_standardvalue1);
		case 2:
			return getString(R.string.key_standardvalue2);
		case 3:
			return getString(R.string.key_standardvalue3);
		case 4:
			return getString(R.string.key_standardvalue4);
		case 5:
			return getString(R.string.key_standardvalue5);
		case 6:
			return getString(R.string.key_standardvalue6);
		case 7:
			return getString(R.string.key_standardvalue7);
		case 8:
			return getString(R.string.key_standardvalue8);
		case 9:
			return getString(R.string.key_standardvalue9);
		case 10:
			return getString(R.string.key_standardvalue10);
		default:
			return "";
		}
	}

	public String getPrefix(int line) {

		switch (line) {
		case 1:
			return getString(R.string.key_prefix1);
		case 2:
			return getString(R.string.key_prefix2);
		case 3:
			return getString(R.string.key_prefix3);
		case 4:
			return getString(R.string.key_prefix4);
		case 5:
			return getString(R.string.key_prefix5);
		case 6:
			return getString(R.string.key_prefix6);
		case 7:
			return getString(R.string.key_prefix7);
		case 8:
			return getString(R.string.key_prefix8);
		case 9:
			return getString(R.string.key_prefix9);
		case 10:
			return getString(R.string.key_prefix10);
		default:
			return "";
		}
	}

	public String getPostfix(int line) {
		switch (line) {
		case 1:
			return getString(R.string.key_postfix1);
		case 2:
			return getString(R.string.key_postfix2);
		case 3:
			return getString(R.string.key_postfix3);
		case 4:
			return getString(R.string.key_postfix4);
		case 5:
			return getString(R.string.key_postfix5);
		case 6:
			return getString(R.string.key_postfix6);
		case 7:
			return getString(R.string.key_postfix7);
		case 8:
			return getString(R.string.key_postfix8);
		case 9:
			return getString(R.string.key_postfix9);
		case 10:
			return getString(R.string.key_postfix10);
		default:
			return "";
		}
	}

}
