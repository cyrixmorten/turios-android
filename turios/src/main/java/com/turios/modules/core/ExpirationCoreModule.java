package com.turios.modules.core;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.util.Log;

@Singleton
public class ExpirationCoreModule {

	private static final String TAG = "ExpirationCoreModule";

	public interface ExpirationCoreModuleCallback {
		void expiresIn(int days);
	}

	private ExpirationCoreModuleCallback expirationCoreModuleCallback;

	public void setExpirationListener(
			ExpirationCoreModuleCallback expirationCoreModuleCallback) {
		this.expirationCoreModuleCallback = expirationCoreModuleCallback;
	}

	private static final int TRIAL_PERIOD_DAYS = 30;

	private final ParseCoreModule parse;

	@Inject
	public ExpirationCoreModule(ParseCoreModule parse) {
		this.parse = parse;
	}

	public void checkExpiration() {
		if (expirationCoreModuleCallback != null)
			expirationCoreModuleCallback.expiresIn(getDaysUntilExpiration());
	}

	private long getExpiresAndTodayDiff(Date installDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(installDate);
		cal.add(Calendar.DATE, TRIAL_PERIOD_DAYS);
		return cal.getTime().getTime() - System.currentTimeMillis();

	}

	public boolean isExpired() {
		return getDaysUntilExpiration() == 0;
	}

	private int getDaysUntilExpiration() {
		if (parse.isLoggedIn()) {
			Date installDate = parse.getParseInstallation().getCreatedAt();
			int days = (int) TimeUnit.DAYS.convert(
					getExpiresAndTodayDiff(installDate), TimeUnit.MILLISECONDS);
			return (days >= 0) ? days : 0;
		}
		return 0;
	}

}
