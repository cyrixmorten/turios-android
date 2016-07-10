package com.turios.activities;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dropbox.sync.android.DbxPath;
import com.turios.R;
import com.turios.activities.fragments.BrowserFragment;
import com.turios.activities.fragments.DisplayFragment;
import com.turios.activities.fragments.GoogleMapFragment;
import com.turios.activities.fragments.dialog.DialogFragments;
import com.turios.activities.fragments.dialog.GenericYesNoDialogFragment.GenericYesNoDialogInterface;
import com.turios.activities.listeners.TuriosUICallback;
import com.turios.activities.util.WakeScreen;
import com.turios.dagger.DaggerActivity;
import com.turios.dagger.quialifiers.ForActivity;
import com.turios.modules.core.DisplayCoreModule;
import com.turios.modules.core.ExpirationCoreModule;
import com.turios.modules.core.LocationsCoreModule;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.core.PathsCoreModule;
import com.turios.modules.extend.BasisModule;
import com.turios.modules.extend.BrowserModule;
import com.turios.modules.extend.DropboxModule;
import com.turios.modules.extend.DropboxModule.DropboxFileDownloaderReceiver;
import com.turios.modules.extend.DropboxModule.DropboxFileDownloaderResultReceiver;
import com.turios.modules.extend.GoogleMapsModule;
import com.turios.modules.extend.HydrantsModule;
import com.turios.persistence.Preferences;
import com.turios.util.Constants;
import com.turios.util.Device;

@Singleton
public class TuriosActionBarController {

	private static final String TAG = "TuriosActionBar";

	// public static final String PREF_CURRENT_NAVIGATION =
	// "pref_current_navigation";
	//
	// public static int NAVIGATION_HOME = 0;
	// public static int NAVIGATION_MAP = 1;
	// public static int NAVIGATION_BROWSER = 2;

	// private WeakReference<Turios> mTurios;

	@Inject DaggerActivity mActivity;
	@Inject ActionBar mActionbar;
	@Inject MenuInflater mMenuInflater;

	@Inject Preferences preferences;
	@Inject WakeScreen wakeScreen;
	@Inject Device device;

	@Inject ParseCoreModule parse;
	@Inject ExpirationCoreModule expiration;
	@Inject PathsCoreModule paths;
	@Inject DisplayCoreModule display;
	@Inject LocationsCoreModule locationService;

	@Inject BasisModule basisModule;
	@Inject BrowserModule browserModule;
	@Inject DropboxModule dropboxModule;
	@Inject GoogleMapsModule googleMapsModule;
	@Inject HydrantsModule hydrantsModule;

	@Inject FragmentManager fm;
	@Inject Handler handler;

	@Inject @ForActivity Context context;

	private TuriosUICallback mTuriosUICallback;
	private TabListener mTabListener;

	@Inject public TuriosActionBarController(TabListener mTabListener,
			TuriosUICallback mTuriosUICallback) {
		this.mTabListener = mTabListener;
		this.mTuriosUICallback = mTuriosUICallback;
	}

	// private Turios context {
	// Turios activity = mTurios.get();
	// if (activity == null) {
	// throw new RuntimeException("TuriosActionBar mActivity is null");
	// }
	// return activity;
	// }

	public void recreateActionBar() {
		addActionBar(mActionbar.getSelectedNavigationIndex());
	}
	
	public void addActionBar(int selectedIndex) {

		// removes blue line underneath actionbar
		// mActionbar.setBackgroundDrawable(context.getResources().getDrawable(
		// android.R.drawable.screen_background_dark));

		// if (DisplayCoreModule().isSinglePane()) {
		mActionbar.setDisplayShowTitleEnabled(true);
		mActionbar.setDisplayShowHomeEnabled(false);
		// mActionbar
		// .setTitle(context.getResources().getString(R.string.app_name));
		// } else {
		// mActionbar.setDisplayShowTitleEnabled(false);
		// }

		mActionbar.removeAllTabs();

		List<String> tabValues = getActionbarTabValues();

		if (tabValues.size() == 1) {
			mActionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		} else {
			int index = 0;
			for (String tabName : tabValues) {
				Tab tab = addTab(mActionbar, tabName);
				if (index == selectedIndex) {
					mActionbar.selectTab(tab);
				}
				index++;
			}
			mActionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		}

		// // Specify a SpinnerAdapter to populate the dropdown list.
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(
		// actionbar.getThemedContext(),
		// android.R.layout.simple_spinner_item, android.R.id.text1,
		// tabValues.toArray(new String[] {}));
		//
		// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//
		// // Set up the dropdown list navigation in the action bar.
		// actionbar.setListNavigationCallbacks(adapter, this);
		mActionbar.show();
	}

	public int getSelectedNavigationIndex() {
		return (mActionbar != null) ? mActionbar.getSelectedNavigationIndex()
				: -1;
	}

	private Tab addTab(ActionBar bar, String name) {
		ActionBar.Tab tab = bar.newTab();
		tab.setText(name);
		tab.setTabListener(mTabListener);
		bar.addTab(tab);

		return tab;
	}

	private List<String> getActionbarTabValues() {
		int index = 0;
		List<String> tabValues = new ArrayList<String>();
		String turios = preferences.getString(R.string.key_basis_tabname);

		String browser = preferences.getString(R.string.key_browser_tabname);

		DisplayFragment.NAVIGATION_INDEX = Integer.MAX_VALUE;
		GoogleMapFragment.NAVIGATION_INDEX = Integer.MAX_VALUE;
		BrowserFragment.NAVIGATION_INDEX = Integer.MAX_VALUE;
		
		DisplayFragment.NAVIGATION_INDEX = index;
		index++;
		tabValues.add(turios);
		if (!device.isMultiPane()) {
			if (googleMapsModule.isActivated()) {
				GoogleMapFragment.NAVIGATION_INDEX = index;
				index++;
				tabValues.add(context.getString(R.string.map));

			}
		} else {
			GoogleMapFragment.NAVIGATION_INDEX = Integer.MAX_VALUE;
		}
		if (browserModule.isActivated()) {
			BrowserFragment.NAVIGATION_INDEX = index;
			index++;
			tabValues.add(browser);
		}
		
		Log.d(TAG, "-- Action bar tabs --");
		Log.d(TAG, "Display: " + DisplayFragment.NAVIGATION_INDEX);
		Log.d(TAG, "Map: " + GoogleMapFragment.NAVIGATION_INDEX);
		Log.d(TAG, "Browser: " + BrowserFragment.NAVIGATION_INDEX);
		Log.d(TAG, "-- Action bar tabs --");

		return tabValues;
	}

	public void setActionbarNavigationIndex(int index) {
		if (mActionbar.getNavigationMode() != ActionBar.NAVIGATION_MODE_STANDARD) {
			Log.d(TAG, "setActionbarNavigationIndex " + index);
			mActionbar.setSelectedNavigationItem(index);
		}
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem refresh = (MenuItem) menu.findItem(R.id.button_sync);
		if (refresh != null) {
			if (dropboxModule.isSynchronizing()) {
				refresh.setActionView(R.layout.actionbar_indeterminate_progress);
			} else {
				refresh.setActionView(null);
			}
		}
		return true;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
//		int selectedNavigationIndex = mActionbar.getSelectedNavigationIndex();

		mMenuInflater.inflate(R.menu.menu_main, menu);

//		MenuItem menu_add_time = menu.findItem(R.id.button_add_time);
//		MenuItem menu_remove = menu.findItem(R.id.button_remove);

//		if (selectedNavigationIndex == DisplayFragment.NAVIGATION_INDEX
//				|| mActionbar.getNavigationMode() == ActionBar.NAVIGATION_MODE_STANDARD) {
//
//			if (menu_add_time != null && menu_remove != null) {
//				if (!basisModule.getPreferences().isAutomaticDeleteEnabled()) {
//					menu_add_time.setVisible(false);
//				}
//
//				if (display.isPageDeletionDelayed(mTuriosUICallback.getSelectedDisplayFragment().getPageId())) {
//					menu_add_time.setVisible(false);
//				}
//				if (display.getPagesCount() == 0) {
//					menu_add_time.setVisible(false);
//					menu_remove.setVisible(false);
//				}
//			}
//		} else {
//			menu_add_time.setVisible(false);
//			menu_remove.setVisible(false);
//		}
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go home
			Intent intent = new Intent(context, Turios.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(intent);
			return true;
		case R.id.button_preferences:

			LayoutInflater factory = LayoutInflater.from(context);
			final View textEntryView = factory.inflate(R.layout.settings_lock,
					null);
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle(context.getString(R.string.prefs_setting_title_lock));
			alert.setView(textEntryView);

			alert.setPositiveButton(context.getString(android.R.string.ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							EditText mUserText = (EditText) textEntryView
									.findViewById(R.id.settings_lock_pw);
							String settingsPw = mUserText.getText().toString();
							if (settingsPw.equals(preferences
									.getString(R.string.key_lockpw))) {
								Intent prefsIntent = new Intent(context,
										SettingsActivity.class);
								context.startActivity(prefsIntent);
							} else {
								Toast.makeText(
										context,
										context.getText(R.string.wrong_password),
										Toast.LENGTH_SHORT).show();
							}
							return;
						}
					});

			alert.setNegativeButton(context.getString(android.R.string.cancel),
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

							return;
						}
					});
			alert.show();

			return true;

//		case R.id.button_remove:
//			if (display.getPagesCount() > 0) {
//				DialogFragments.deletePage(new GenericYesNoDialogInterface() {
//
//					@Override public void yesClicked() {
//						display.deletePage(display.getCurrentPageSelected());
//					}
//
//					@Override public void noClicked() {
//					}
//				}).show(fm, "fragment_confirm_delete");
//			}
//			return true;

//		case R.id.button_add_time:
//			DisplayFragment displayFragment = mTuriosUICallback
//					.getSelectedDisplayFragment();
//			long current_pageid = displayFragment.getPageId();
//			if (display.isPageDeletionDelayed(current_pageid)) {
//				Toast.makeText(
//						context,
//						context.getString(R.string.cannot_delay_page_delete_twice),
//						Toast.LENGTH_SHORT).show();
//			} else {
//				display.addPageToDelayList(current_pageid);
//				displayFragment.updateAutomaticDeleteTime(false);
//				Toast.makeText(context,
//						context.getString(R.string.page_delete_delayed),
//						Toast.LENGTH_SHORT).show();
//			}
//			mTuriosUICallback.invalidateOptionsMenu();
//			return true;

		case R.id.button_sync:

			if (dropboxModule.isEnabled()) {

				DropboxFileDownloaderResultReceiver dropboxFileDownloaderResultReceiver = dropboxModule.new DropboxFileDownloaderResultReceiver(
						new Handler());
				dropboxFileDownloaderResultReceiver
						.setReceiver(dropboxFileDownloaderReceiver);
				dropboxModule.synchronize(DbxPath.ROOT,
						dropboxFileDownloaderResultReceiver);

			}

			return true;
		}

		return false;
	}

	private DropboxFileDownloaderReceiver dropboxFileDownloaderReceiver = new DropboxFileDownloaderReceiver() {

		@Override public void onDropboxFileDownloadReceiveResult(
				int resultCode, Bundle resultData) {
			String suffixText = "";

			boolean showToast = true;

			switch (resultCode) {
			case DropboxFileDownloaderResultReceiver.RESULTCODE_ERROR:
				String error = resultData.getString(Constants.EXTRA_MESSAGE);
				Log.e(TAG, error);
				return;
			case DropboxFileDownloaderResultReceiver.RESULTCODE_CREATED:
				suffixText = context.getString(R.string.created);
				break;
			case DropboxFileDownloaderResultReceiver.RESULTCODE_UPDATED:
				suffixText = context.getString(R.string.updated);
				break;
			case DropboxFileDownloaderResultReceiver.RESULTCODE_UPTODATE:
				suffixText = context.getString(R.string.not_changed);
				showToast = false;
				break;
			default:
				break;
			}

			if (showToast) {
				String path = resultData.getString(Constants.EXTRA_FILEPATH);
				String message = path + " " + suffixText;
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
				Log.d(TAG, message);
			}

		}
	};



}
