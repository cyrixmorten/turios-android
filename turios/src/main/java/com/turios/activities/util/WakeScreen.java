package com.turios.activities.util;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.turios.dagger.quialifiers.ForApplication;
import com.turios.modules.extend.BasisModule;
import com.turios.util.Notifications;

@Singleton
public class WakeScreen {

	private static final String TAG = WakeScreen.class.getSimpleName();

	private PowerManager.WakeLock wl;
	private PowerManager pm;

	private final Context context;
	private final Activity activity;
	private final BasisModule basisModule;
	private final Notifications notifications;

	@Inject public WakeScreen(@ForApplication Context context,
			Activity activity, BasisModule basisModule,
			Notifications notifications) {
		this.context = context;
		this.activity = activity;
		this.basisModule = basisModule;
		this.notifications = notifications;
	}

	public void screenWakeup(boolean playSound) {

		if (basisModule.getPreferences().wakeScreenOnIncommingMessage()) {
			try {
				pm = (PowerManager) context
						.getSystemService(Activity.POWER_SERVICE);
				// if (!pm.isScreenOn()) {
				if (wl == null) {
					wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
							| PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Turios");
				}
				if (!wl.isHeld()) {
					wl.acquire();
				}

				final Window win = activity.getWindow();
				win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
				win.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				// }

			} catch (Exception ex) {
				Log.e(TAG, ex.getMessage());
			}
		} else {
			// just in case
			releaseWakeLock();
		}

		if (playSound)
			notifications.soundNotification(basisModule.getPreferences()
					.notificationSoundUri());

	}

	public void screenRelease() {

		if (releaseWakeLock()) {
			activity.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
							| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
							| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
							| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}

	}

	private boolean holdsWakeLock() {
		return wl != null && wl.isHeld();
	}

	private boolean releaseWakeLock() {
		try {
			if (holdsWakeLock()) {
				wl.release();
				return true;
			}

		} catch (Exception ex) {
			Log.e(TAG, ex.getMessage());
		}

		return false;
	}

}
