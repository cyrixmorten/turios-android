package com.turios.util;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.turios.dagger.quialifiers.ForApplication;

@Singleton
public class Device {

	final String TAG = "Device";

	// returns true if a multipane layout has been shown;
	private boolean multiPaneEnabled;

	private final Context context;

	@Inject public Device(@ForApplication Context context) {
		super();
		this.context = context;
	}

	public boolean isRunningOnEmulator() {
		return Build.FINGERPRINT.contains("generic");
	}

	public void setMultiPaneEnabled(boolean hasMasterView) {
		if (hasMasterView) {
			multiPaneEnabled = true;
		}
	}

	public boolean isMultiPaneEnabled() {
		return multiPaneEnabled;
	}

	public boolean isMultiPane() {
		boolean isMultiPane = context.getResources().getBoolean(
				com.turios.R.bool.has_two_panes);

		return isMultiPane;
	}

	public Boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isConnected())
			return true;

		// Toast.makeText(context,
		// context.getString(R.string.no_internet_connection),
		// Toast.LENGTH_SHORT).show();
		return false;
	}

	public int getVersionCode() {
		int v = 0;
		try {
			v = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			// Huh? Really?
		}
		return v;
	}

	public String getVersionName() {
		String v = "";
		try {
			v = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// Huh? Really?
		}
		return v;
	}

	// TODO check at startup!
	// public boolean servicesAvailable(FragmentActivity activity) {
	// // Check that Google Play services is available
	// int resultCode = GooglePlayServicesUtil
	// .isGooglePlayServicesAvailable(activity);
	// // If Google Play services is available
	// if (ConnectionResult.SUCCESS == resultCode) {
	// // In debug mode, log the status
	// // Log.d("Location Updates", "Google Play services is available.");
	// // Continue
	// return true;
	// // Google Play services was not available for some reason
	// } else if (resultCode == ConnectionResult.SERVICE_MISSING
	// || resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED
	// || resultCode == ConnectionResult.SERVICE_DISABLED) {
	// Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
	// activity, 1);
	// dialog.show();
	// }
	// return false;
	// }

	// public boolean hasGoogleAccount() {
	// AccountManager am = AccountManager.get(activity);
	// if (!Device.isRunningOnEmulator(activity)
	// && am.getAccountsByType("com.google").length == 0) {
	//
	// DialogFragments.missingGoogleAccount(
	// new GenericYesNoDialogInterface() {
	//
	// @Override public void yesClicked() {
	// AccountManager accountMgr = AccountManager
	// .get(activity);
	// accountMgr.addAccount("com.google", "ah", null,
	// new Bundle(), activity, null, null);
	// }
	//
	// @Override public void noClicked() {
	// // TODO Auto-generated method stub
	//
	// }
	// }).show(fm, "dialog_missing_google_account");
	//
	// return false;
	// }
	//
	// return true;
	// }

}
