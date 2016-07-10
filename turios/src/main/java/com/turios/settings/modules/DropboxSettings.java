package com.turios.settings.modules;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import com.dropbox.sync.android.DbxAccountManager;
import com.turios.R;
import com.turios.activities.setup.SetupDropbox;
import com.turios.dagger.DaggerPreferenceFragment;
import com.turios.dagger.quialifiers.ForActivity;
import com.turios.modules.extend.DropboxModule;
import com.turios.settings.modules.status.StatusCheck;
import com.turios.util.Constants;

public class DropboxSettings extends DaggerPreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private static final String TAG = "DropboxSettings";

	private StatusCheck statusCheck;

	@Inject @ForActivity Context context;
	@Inject DropboxModule dropboxModule;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.settings_module_dropbox);

	}

	@Override public void onStart() {
		if (!dropboxModule.isActivated()) {
			((SwitchPreference) getPreferenceScreen().findPreference(
					getString(R.string.key_dropbox_link_unlink)))
					.setEnabled(false);
			((CheckBoxPreference) getPreferenceScreen().findPreference(
					getString(R.string.key_dropbox_daily_sync)))
					.setEnabled(false);
		}

		statusCheck = new StatusCheck(context, dropboxModule, this);
		statusCheck.runCheck();

		super.onStart();
	}

	@Override public void onResume() {
		PreferenceManager.getDefaultSharedPreferences(context)
				.registerOnSharedPreferenceChangeListener(this);

		super.onResume();
	}

	@Override public void onPause() {
		PreferenceManager.getDefaultSharedPreferences(context)
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	@Override public void onSharedPreferenceChanged(SharedPreferences prefs,
			String key) {

		if (key.equals(getString(R.string.key_module_activated_dropbox))) {
			statusCheck.runCheck();
		}

		if (key.equals(getString(R.string.key_dropbox_link_unlink))) {
			boolean value = prefs.getBoolean(key, true);
			if (value == false) {
				DbxAccountManager mDbxAcctMgr = DbxAccountManager.getInstance(
						context.getApplicationContext(),
						Constants.DROPBOX_APPKEY, Constants.DROPBOX_SECRETKEY);
				mDbxAcctMgr.unlink();
			} else {
				Intent intent = new Intent(context, SetupDropbox.class);
				intent.putExtra("settings", true);
				startActivity(intent);
			}
		}
	}
}