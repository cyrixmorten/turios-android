package com.turios.wakefulservices;

import javax.inject.Inject;

import android.content.Intent;
import android.util.Log;

import com.dropbox.sync.android.DbxPath;
import com.parse.ParseException;
import com.turios.dagger.DaggerWakefulIntentService;
import com.turios.modules.core.ExpirationCoreModule;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.core.ParseCoreModule.ParseLoginCallback;
import com.turios.modules.extend.BasisModule;
import com.turios.modules.extend.DropboxModule;
import com.turios.util.Constants;

public class UpdateStateService extends DaggerWakefulIntentService {

	private static final String TAG = "UpdateStateService";

	public UpdateStateService() {
		super(TAG);
	}

	@Inject ParseCoreModule parse;

	@Inject ExpirationCoreModule expiration;

	@Inject BasisModule basisModule;
	@Inject DropboxModule dropboxModule;

	@Override protected void doWakefulWork(Intent intent) {

		Log.i(TAG, "Check state");

		parse.login(new ParseLoginCallback() {

			@Override public void success() {

				Log.i(TAG, "Parse loggin success");

				expiration.checkExpiration();

				dropboxModule.synchronize(DbxPath.ROOT);

				Intent i = new Intent(Constants.INTENT_REFRESH_MODULES);
				getApplicationContext().sendBroadcast(i);
			}

			@Override public void failed(Exception e) {
				Log.i(TAG, "Parse loggin failed " + e.getMessage());
				Log.e(TAG, e.getMessage(), e);
			}
		});

	}
}
