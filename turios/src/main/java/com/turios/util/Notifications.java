package com.turios.util;

import javax.inject.Inject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.turios.R;
import com.turios.activities.SplashScreen;
import com.turios.dagger.quialifiers.ForApplication;
import com.turios.modules.extend.BasisModule;

public class Notifications {

	private static final String TAG = Notifications.class.getSimpleName();

	private final Context context;
	private final BasisModule basis;

	@Inject public Notifications(@ForApplication Context context,
			BasisModule basis) {
		super();
		this.context = context;
		this.basis = basis;
	}

	private static final int NOTICIFATION_ID = 1;

	public void displayNotification(String message) {

		String notificationMessage = message.replaceAll("\\"
				+ Constants.DELIMITER, " ");

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher_fireicon)
				.setContentTitle(context.getString(R.string.app_name))
				.setContentText(notificationMessage).setAutoCancel(true);

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, SplashScreen.class);
		resultIntent.putExtra(Constants.EXTRA_ACTION, "NOTIFICATION");
		resultIntent.putExtra(Constants.EXTRA_MESSAGE, message);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(contentIntent);
		// mBuilder.setFullScreenIntent(contentIntent, true);
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(NOTICIFATION_ID, mBuilder.build());
	}

	public void soundNotification(String custom_uri) {

		try {
			Uri notification = null;
			if (custom_uri.isEmpty()) {
				notification = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			} else {
				notification = Uri.parse(custom_uri);
			}

			final Ringtone r = RingtoneManager.getRingtone(context,
					notification);

			new Handler().post(new Runnable() {

				@Override public void run() {
					r.play();
				}
			});

		} catch (Exception e) {
			Log.e(TAG, "", e);
		}
	}
}
