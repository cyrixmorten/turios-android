package com.turios.sms;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseException;
import com.turios.R;
import com.turios.dagger.DaggerBroadcastReceiver;
import com.turios.modules.core.DisplayCoreModule;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.core.ParseCoreModule.ParseLoginCallback;
import com.turios.modules.extend.BasisModule;
import com.turios.util.Constants;
import com.turios.util.Notifications;

public class TuriosMessageReceiver extends DaggerBroadcastReceiver {

	private static final String TAG = TuriosMessageReceiver.class.getName();

	@Inject ParseCoreModule parse;
	@Inject DisplayCoreModule display;

	@Inject BasisModule basis;

	@Inject Notifications notifications;

	@Override public void onReceive(final Context context, Intent intent) {
		super.onReceive(context, intent);

		final String message = intent.getStringExtra(Constants.EXTRA_MESSAGE);
		final String sender = intent.getStringExtra(Constants.EXTRA_SENDER);

		parse.login(new ParseLoginCallback() {

			@Override public void success() {

				if (basis.isEnabled()) {

					parse.incrementTurnouts();
					display.newMessage(message);
					notifications.displayNotification(message);

				} else {
					Toast.makeText(
							context,
							context.getString(R.string.error_smsreceived_trial_expired),
							Toast.LENGTH_LONG).show();
				}
			}

			@Override public void failed(ParseException e) {
				Log.d(TAG, "JSONException: " + e.getMessage());
			}
		});

	}

}
