package com.turios.wakefulservices;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class UpdateStateListener implements WakefulIntentService.AlarmListener {

	private static final String TAG = UpdateStateListener.class.getSimpleName();

	public void scheduleAlarms(AlarmManager mgr, PendingIntent pi, Context ctxt) {
		Log.i(TAG, "scheduleAlarms");
		mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY,
				AlarmManager.INTERVAL_DAY, pi);
		// mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
		// SystemClock.elapsedRealtime()
		// + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
		// AlarmManager.INTERVAL_FIFTEEN_MINUTES, pi);
	}

	public void sendWakefulWork(Context ctxt) {
		WakefulIntentService.sendWakefulWork(ctxt, UpdateStateService.class);
	}

	public long getMaxAge() {
		return AlarmManager.INTERVAL_DAY * 2;
		// return AlarmManager.INTERVAL_FIFTEEN_MINUTES * 2;
	}

}
