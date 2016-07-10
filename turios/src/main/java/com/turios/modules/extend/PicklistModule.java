package com.turios.modules.extend;

import android.content.Context;
import android.util.Log;

import com.dropbox.sync.android.DbxPath.InvalidPathException;
import com.turios.dagger.quialifiers.ForApplication;
import com.turios.modules.core.ExpirationCoreModule;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.core.PathsCoreModule;
import com.turios.modules.preferences.PicklistModulePreferences;
import com.turios.persistence.Preferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PicklistModule extends StandardModule {

	private final PicklistModulePreferences modulePreferences;

	public PicklistModulePreferences getPreferences() {
		return modulePreferences;
	}

	private static final String TAG = "PicklistModule";

	private static final String PICKLIST_DELIMITER = ":";

	private Map<String, String> mPickListMap;

	private final PathsCoreModule paths;

	@Inject public PicklistModule(@ForApplication Context context,
			Preferences preferences, ExpirationCoreModule expiration,
			ParseCoreModule parse, PathsCoreModule paths) {
		super(context, preferences, expiration, parse, MODULES.PICKLISTS);

		this.paths = paths;
		this.modulePreferences = new PicklistModulePreferences(preferences);

		Log.i(TAG, "CREATING MODULE " + TAG);

		mPickListMap = new HashMap<String, String>();
	}

	@Override public void load() {
		loadStarted();
		createPicklistMapFromLocalFile();
		loadEnded();
	}

	public Map<String, String> getPickListMap() {
		return mPickListMap;
	}

	public String convertKey(String key) {
		if (!isActivated())
			return key;

		if (mPickListMap != null && mPickListMap.containsKey(key)) {
			return mPickListMap.get(key);
		}

		return key;
	}

	public void updatePicklistMap(String picklist) {
		Map<String, String> picklistMap = new HashMap<String, String>();

		String lines[] = picklist.split("#");
		for (String strLine : lines) {
			// int splitAt = strLine.replace("\t", " ").indexOf(" ");
			int splitAt = strLine.indexOf(PICKLIST_DELIMITER);
			if (splitAt != -1) {

				String key = strLine.substring(0, splitAt).trim();
				String value = strLine.substring(splitAt + 1, strLine.length())
						.trim();
				// Log.e(TAG, key + " - " + value);
				picklistMap.put(key, value);
			}
		}

		mPickListMap = picklistMap;
	}

	//

	public void createPicklistMapFromLocalFile() {

		File file = new File(paths.FILEPATH_LOCAL_PICKLIST);

		if (!file.exists()) {
		    return;
		}

		Map<String, String> picklistMap = new HashMap<String, String>();
		BufferedReader in = null;
		try {

			// Read File Line By Line
			in = new BufferedReader(new FileReader(file));
			String strLine;
			while ((strLine = in.readLine()) != null) {
				int splitAt = strLine.indexOf(PICKLIST_DELIMITER);
				if (splitAt != -1) {
					String key = strLine.substring(0, splitAt).trim();
					String value = strLine.substring(splitAt + 1,
							strLine.length()).trim();
					// Log.e(TAG, key + " - " + value);
					picklistMap.put(key, value);
				}
			}
		} catch (InvalidPathException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
				mPickListMap = picklistMap;
			}
		}

	}

}
