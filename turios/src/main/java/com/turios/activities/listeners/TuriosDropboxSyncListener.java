package com.turios.activities.listeners;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.turios.R;
import com.turios.dagger.quialifiers.ForApplication;
import com.turios.modules.extend.DropboxModule.DropboxCallback;
import com.turios.util.Constants;

public class TuriosDropboxSyncListener implements DropboxCallback {

	private final Context context;
	private final TuriosUICallback mTuriosUICallback;

	@Inject public TuriosDropboxSyncListener(@ForApplication Context context,
			TuriosUICallback turiosUICallback) {
		this.mTuriosUICallback = turiosUICallback;
		this.context = context;
	}

	@Override public void syncStarted(int filescount) {
		Toast.makeText(context, context.getString(R.string.sync_started),
				Toast.LENGTH_SHORT).show();

		mTuriosUICallback.invalidateOptionsMenu();
	}

	@Override public void syncEnded() {
		Toast.makeText(context, context.getString(R.string.sync_complete),
				Toast.LENGTH_LONG).show();
		mTuriosUICallback.invalidateOptionsMenu();
		context.sendBroadcast(new Intent(Constants.INTENT_REFRESH_MODULES));
	}

	@Override public void syncCanceled(String cause) {
		Toast.makeText(context, cause, Toast.LENGTH_LONG).show();
	}

	@Override public void syncDownloaded(int filecount) {
		// TODO update a progress notification
	}

}
