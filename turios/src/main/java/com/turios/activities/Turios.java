package com.turios.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.GoogleMap;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ProgressCallback;
import com.parse.RefreshCallback;
import com.turios.BuildConfig;
import com.turios.R;
import com.turios.activities.display.DisplayPagerAdapter;
import com.turios.activities.fragments.BrowserFragment;
import com.turios.activities.fragments.DisplayFragment;
import com.turios.activities.fragments.GoogleMapFragment;
import com.turios.activities.fragments.GoogleMapOptionsFragment;
import com.turios.activities.fragments.dialog.DialogFragments;
import com.turios.activities.fragments.dialog.GenericOkDialogFragment.GenericOkDialogInterface;
import com.turios.activities.listeners.TuriosDisplayListener;
import com.turios.activities.listeners.TuriosDropboxSyncListener;
import com.turios.activities.listeners.TuriosExpirationListener;
import com.turios.activities.listeners.TuriosLocationListener;
import com.turios.activities.listeners.TuriosLocationsListener;
import com.turios.activities.util.WakeScreen;
import com.turios.dagger.DaggerActivity;
import com.turios.modules.core.DisplayCoreModule;
import com.turios.modules.core.ExpirationCoreModule;
import com.turios.modules.core.LocationsCoreModule;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.core.PathsCoreModule;
import com.turios.modules.data.AddressHolder;
import com.turios.modules.extend.AccessplansModule;
import com.turios.modules.extend.BasisModule;
import com.turios.modules.extend.BrowserModule;
import com.turios.modules.extend.DirectionsModule;
import com.turios.modules.extend.DropboxModule;
import com.turios.modules.extend.GoogleMapsModule;
import com.turios.modules.extend.GoogleMapsModule.FetchAddressCallback;
import com.turios.modules.extend.HydrantsModule;
import com.turios.modules.extend.PicklistModule;
import com.turios.persistence.Preferences;
import com.turios.util.Constants;
import com.turios.util.Device;
import com.turios.util.UpdateApp;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.android.gms.internal.a.R;

public class Turios extends DaggerActivity implements TabListener,
		GoogleMapFragment.OnGoogleMapFragmentListener,
        GoogleApiClient.OnConnectionFailedListener,
		com.turios.activities.listeners.TuriosUICallback, OnPageChangeListener {

	// tag filter :
	// ^(?!.*(dalvik)|(ButterKnife)|(libDropbox)|(com.dropbox)|(Communicator)).*$
	// message filter: ^(?!.*(nativeGetEnabledTags)|(GC_)).*$

	// AIzaSyD2xjaSWphzLaE75L4B1H5886kOoJCMqpE medion
	// AIzaSyCr6MttlKaWY_OlFCJ7TZkvji7QgNc-xmo samsung
	// AIzaSyA0UyCG5ShXiG1k3wBHl7gPUrmn4ss5Ca0 dist

	/*
	 * 2||BTPa||Carl Blochs Gade 50||Aarhus C
	 * 3|2|BLej||Vester Gade 3|Aarhus C|TKP
	 * 
	 * am broadcast -a cyrix.turios.INCOMMING_MESSAGE --es cyrix.turios.MESSAGE 2\|\|BTPa\|\|Carl\ Blochs\ Gade\ 50\|\|Aarhus\ C 
	 * am broadcast -a cyrix.turios.INCOMMING_MESSAGE --es cyrix.turios.MESSAGE 3\|2\|BLej\|\|Vester\ Gade\ 3\|\|Aarhus\ C\|ABA301
	 * am broadcast -a cyrix.turios.INCOMMING_SMS --es cyrix.turios.MESSAGE 3\|\|SBLe\|\|Lystrup\ Centervej\ 4\|Lystrup
	 * am broadcast -a
	 * cyrix.turios.INCOMMING_SMS --es cyrix.turios.MESSAGE 1\|Omraade\
	 * evakueret
	 * 
	 * am broadcast -a cyrix.turios.INCOMMING_SMS -e cyrix.turios.MESSAGE
	 * \#TMA\|1\|1\|1\|1\|1 -e cyrix.turios.SENDER 28718353
	 */

	private final static String TAG = "Turios";

	private static final int ANIMATION_TINTBACKGROUND = 750;

	public static final String FRAGMENT_DISPLAY_TAG = "fragment:display";
	public static final String FRAGMENT_MAP_TAG = "fragment:map";
	public static final String FRAGMENT_MAP_OPTIONS_TAG = "fragment:map:options";
	public static final String FRAGMENT_BROWSER_TAG = "fragment:browser";

	public static final String PENDING_DIALOGFRAGMENT_TAG = "fragment:trialexpired";

	private static final String STATE_TAB = "state_tab";

	private ViewPager mViewPager;
	private DisplayPagerAdapter mDisplayPagerAdapter;

	private BroadcastReceiver refreshModulesReceiver;

	private boolean mIsPaused;
	private DialogFragment mPendingDialog;

	private GoogleMapFragment mapsFragment;
	private GoogleMapOptionsFragment mapsOptionsFragment;
	private BrowserFragment browserFragment;

	// ActivityResult
	static final int REQUEST_LINK_TO_DBX = 0; // This value is up to you

	@Inject
	TuriosActionBarController mTuriosActionBar;

	@Inject
	FragmentManager fm;

	@Inject
	Preferences preferences;
	@Inject
	WakeScreen wakeScreen;
	@Inject
	Device device;
	@Inject
	Handler handler;

	@Inject
	ParseCoreModule parse;
	@Inject
	ExpirationCoreModule expiration;
	@Inject
	PathsCoreModule paths;
	@Inject
	DisplayCoreModule display;
	@Inject
	LocationsCoreModule locationService;

	@Inject
	AccessplansModule accessplansModule;
	@Inject
	BasisModule basisModule;
	@Inject
	BrowserModule browserModule;
	@Inject
	DirectionsModule directionsModule;
	@Inject
	DropboxModule dropboxModule;
	@Inject
	GoogleMapsModule googleMapsModule;
	@Inject
	HydrantsModule hydrantsModule;
	@Inject
	PicklistModule picklistModule;

	@Inject
	TuriosExpirationListener expirationListener;
	@Inject
	TuriosDisplayListener displayListener;
	@Inject
	TuriosLocationsListener locationsListener;
	@Inject
	TuriosDropboxSyncListener dropboxListener;
	@Inject
	TuriosLocationListener turiosLocationListenerImpl;

	@BindView(R.id.layout_new_update)
	LinearLayout layoutNewUpdate;

	@BindView(R.id.btn_download)
	Button downloadButton;

	// @Inject TuriosHydrantsListener hydrantsListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate");

		// getApplicationContext().setTheme(R.style.TuriosTheme);

		setContentView(R.layout.activity_turios);

		ButterKnife.bind(this);

		boolean isMultiPane = findViewById(R.id.master) != null;
		device.setMultiPaneEnabled(isMultiPane);
		Log.i(TAG, "setMultiPaneEnabled " + isMultiPane);



		if (savedInstanceState != null) {

			Log.i(TAG, "savedInstanceState not null");

			// restore fragments
			browserFragment = (BrowserFragment) fm
					.findFragmentByTag(FRAGMENT_BROWSER_TAG);
			mapsFragment = (GoogleMapFragment) fm
					.findFragmentByTag(FRAGMENT_MAP_TAG);

			mapsOptionsFragment = (GoogleMapOptionsFragment) fm
					.findFragmentByTag(FRAGMENT_MAP_OPTIONS_TAG);

			Log.i(TAG, "isMultiPane " + device.isMultiPane());
			Log.i(TAG, "isMultiPaneEnabled " + device.isMultiPaneEnabled());

			if (device.isMultiPane() || device.isMultiPaneEnabled()) {
				// remove maps tab
				mTuriosActionBar.addActionBar(DisplayFragment.NAVIGATION_INDEX);
				mTuriosActionBar
						.setActionbarNavigationIndex(DisplayFragment.NAVIGATION_INDEX);
			} else {
				// set to last tab
				int savedNavigationIndex = savedInstanceState.getInt(STATE_TAB,
						DisplayFragment.NAVIGATION_INDEX);
				mTuriosActionBar.addActionBar(savedNavigationIndex);
			}
		} else {
			// no saved instance
			Log.i(TAG, "savedInstanceState null");

			// reset map relevant data
			hydrantsModule.disableAllHydrants();
			googleMapsModule.destroyMap();

			mTuriosActionBar.addActionBar(DisplayFragment.NAVIGATION_INDEX);

		}

		FragmentTransaction ft = fm.beginTransaction();
		if (device.isMultiPane()) {
			addMapFragment(ft).commit();
		}

		mDisplayPagerAdapter = new DisplayPagerAdapter(this, fm, display);
		mViewPager = (ViewPager) findViewById(R.id.display_pager);
		mViewPager.setAdapter(mDisplayPagerAdapter);
		mViewPager.setOffscreenPageLimit(10);
		mViewPager.setOnPageChangeListener(this);

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (display.getPagesCount() > 0)
					mViewPager.setCurrentItem(display.getCurrentPageSelected());

			}
		}, 1000);

		
		registerBroadcastReceivers();
		registerListeners();

		handleIntent(getIntent());
	}

	private void registerBroadcastReceivers() {

		if (!preferences.isFirstStartUp()) {

			refreshModulesReceiver = new RefreshModules();
			registerReceiver(refreshModulesReceiver, new IntentFilter(
					Constants.INTENT_REFRESH_MODULES));
		}

	}

	@Override
	protected void onStart() {
		super.onStart();

		if (!BuildConfig.DEBUG) {
			Log.d(TAG, "Starting analytics");
			EasyTracker.getInstance(this).activityStart(this);
		} else {
			GoogleAnalytics googleAnalytics = GoogleAnalytics
					.getInstance(getApplicationContext());
			googleAnalytics.setAppOptOut(true);
		}

		locationService.start(turiosLocationListenerImpl, this);
		// Device.lockOrientation(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!BuildConfig.DEBUG) {
			Log.d(TAG, "Stopping analytics");
			EasyTracker.getInstance(this).activityStop(this);
		}

		locationService.stop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_TAB,
				mTuriosActionBar.getSelectedNavigationIndex());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!preferences.isFirstStartUp()) {
			Log.d(TAG, "onResume");

			mIsPaused = false;

			NotificationManager mNotificationManager = (NotificationManager) this
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancelAll();

			expiration.checkExpiration();

			if (!paths.hasExternalStorage()) {
				DialogFragments.missingSD(new GenericOkDialogInterface() {
					@Override
					public void okClicked() {
						finish();
					}
				}).show(fm, "dialog");
			}

			if (display.getPagesCount() > 0) {
				wakeScreen.screenWakeup(false);
				locationService.requestLocationUpdates();
			}

		}
	}

	@Override
	protected void onPostResume() {
		showPendingDialog();
		checkNewUpdate();
		super.onPostResume();
	}

	private void checkNewUpdate() {

		new ParseQuery<>("Update").addDescendingOrder("versionNumber").getFirstInBackground()
				.continueWith(new Continuation<ParseObject, Object>() {
					@Override
					public Object then(Task<ParseObject> task) throws Exception {
						if (task.getResult() != null) {
							ParseObject update = task.getResult();

							int currentVersion = device.getVersionCode();
							int nextVersion = update.getInt("versionNumber");

							if (currentVersion < nextVersion) {
								layoutNewUpdate.setVisibility(View.VISIBLE);
							}
						}

						return null;
					}
				});
	}

//	private void downloadAndInstall(Update update) {
//		ParseFile apkFile = update.getUpdateFile();
//		if (apkFile == null) {
//			ToastHelper.toast(this, getString(R.string.file_not_found));
//			return;
//		}
//
//		final MaterialDialog downloadDialog = new MaterialDialog.Builder(this)
//				.title(R.string.downloading_update)
//				.content(R.string.please_wait)
//				.progress(false, 100, false)
//				.show();
//
//		ProgressCallback progressCallback = new ProgressCallback() {
//			@Override
//			public void done(Integer percentDone) {
//				downloadDialog.setProgress(percentDone);
//			}
//		};
//
//		apkFile.getFileInBackground(new GetFileCallback() {
//			@Override
//			public void done(File file, ParseException e) {
//				if (e != null) {
//					return;
//				}
//
//				UpdateApp.fromFile(file);
//
//				downloadDialog.dismiss();
//
//			}
//		}, progressCallback);
//	}

	public void registerListeners() {

		expiration.setExpirationListener(expirationListener);
		display.setDisplayListener(displayListener);
		locationService.setLocationsListener(locationsListener);
		dropboxModule.setDropboxListener(dropboxListener);
		// hydrantsModule.setHydrantsListener(hydrantsListener);

	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");

		mIsPaused = true;

		wakeScreen.screenRelease();

		super.onPause();
	}

	private void unRegisterListeners() {
		expiration.setExpirationListener(null);
		display.setDisplayListener(null);
		locationService.setLocationsListener(null);
		dropboxModule.setDropboxListener(null);
		// hydrantsModule.setHydrantsListener(null);
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");

		unRegisterListeners();
		unRegisterReceivers();

		locationService.stopLoactionUpdates();

		super.onDestroy();
	}

	private void unRegisterReceivers() {
		if (!preferences.isFirstStartUp()) {
			unregisterReceiver(refreshModulesReceiver);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return mTuriosActionBar.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return mTuriosActionBar.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return mTuriosActionBar.onOptionsItemSelected(item);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
		super.onNewIntent(intent);
	}

	private void handleIntent(Intent intent) {

		String action = intent.getStringExtra(Constants.EXTRA_ACTION);
		Log.e(TAG, "onNewIntent");
		if (action != null) {
			if (action.equals("NOTIFICATION")) {

				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.cancelAll();

				if (mTuriosActionBar != null) {
					mTuriosActionBar
							.setActionbarNavigationIndex(DisplayFragment.NAVIGATION_INDEX);
				}
			}
		}

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			final String query = intent.getStringExtra(SearchManager.QUERY);
			// use the query to search your data somehow
			// Toast.makeText(this, query, Toast.LENGTH_SHORT).show();

			hydrantsModule.disableAllHydrants();

			googleMapsModule.lookupAddress(new FetchAddressCallback() {

				@Override
				public void addressLookupFailed() {
					// TODO res/string
					Toast.makeText(Turios.this, "Kunne ikke finde: " + query,
							Toast.LENGTH_LONG).show();
				}

				@Override
				public void addressFound(AddressHolder addressHolder) {
					googleMapsModule.zoomTo(addressHolder);
				}
			}, query);

		}
	}

	@Override
	public void onPageScrollStateChanged(int page) {
		DisplayFragment current_fragment = (DisplayFragment) mDisplayPagerAdapter
				.getRegisteredFragment(page);
		if (current_fragment != null) {
			current_fragment.cancelScrollToBottom();
		}

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int page) {

		display.setCurrentPageSelected(page);

		DisplayFragment current_fragment = (DisplayFragment) mDisplayPagerAdapter
				.getRegisteredFragment(page);
		if (current_fragment != null) {
			current_fragment.scrollToBottomDelayed(2000);
		}

	}

	/** PRIVATE HELPER METHODS START **/

	private AddressHolder getSelectedAddressHolder() {
		return getSelectedDisplayFragment().getAddressHolder();
	}

	/** PRIVATE HELPER METHODS END **/

	/** TAB SELECT CALLBACKS START **/

	private FragmentTransaction addMapFragment(FragmentTransaction ft) {
		if (mapsFragment == null) {
			AddressHolder address = (device.isMultiPane()) ? null
					: getSelectedAddressHolder();
			mapsFragment = GoogleMapFragment.newInstance(address);
			ft.add(R.id.details, mapsFragment, Turios.FRAGMENT_MAP_TAG);

			mapsOptionsFragment = new GoogleMapOptionsFragment();
			ft.add(R.id.details, mapsOptionsFragment, FRAGMENT_MAP_OPTIONS_TAG);
		} else {
			ft.attach(mapsFragment);
			ft.attach(mapsOptionsFragment);
		}
		return ft;
	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		int position = tab.getPosition();
		FragmentTransaction t = fm.beginTransaction();

		ft.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left);

		if (position == DisplayFragment.NAVIGATION_INDEX) {

			if (!device.isMultiPane() && mapsFragment != null
					&& mapsOptionsFragment != null) {
				t.detach(mapsFragment);
				t.detach(mapsOptionsFragment);
			}

			if (browserFragment != null) {
				t.detach(browserFragment);
			}

		}

		if (position == GoogleMapFragment.NAVIGATION_INDEX) {

			t = addMapFragment(t);

		}
		if (position == BrowserFragment.NAVIGATION_INDEX) {
			if (browserFragment == null) {
				browserFragment = new BrowserFragment();
				t.add(R.id.details, browserFragment,
						Turios.FRAGMENT_BROWSER_TAG);
			} else {
				t.attach(browserFragment);
			}
		}

		t.commitAllowingStateLoss();

	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		int position = tab.getPosition();
		FragmentTransaction t = fm.beginTransaction();

		if (position == GoogleMapFragment.NAVIGATION_INDEX) {
			if (mapsFragment.isVisible() && mapsOptionsFragment.isVisible()) {
				t.detach(mapsFragment);
				t.detach(mapsOptionsFragment);
			}
		}
		if (position == BrowserFragment.NAVIGATION_INDEX) {
			if (browserFragment.isVisible()) {
				t.detach(browserFragment);
			}
		}
		t.commitAllowingStateLoss();
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
	}

	/** TAB SELECT CALLBACKS END **/

	/** MAP START **/

	@Override
	public void onMapReady(GoogleMap map) {
		Log.i(TAG, "onMapReady1 : " + (map != null));
		if (map != null) {
			map = mapsFragment.getExtendedMap();
			map.setClustering(new ClusteringSettings().enabled(false)
					.addMarkersDynamically(true));
			googleMapsModule.setMap(map);
		}
	}

	/** MAP END **/

	@SuppressLint("NewApi")
	private void tintBackground() {
		ColorDrawable[] color = { new ColorDrawable(Color.RED),
				new ColorDrawable(Color.WHITE) };
		TransitionDrawable trans = new TransitionDrawable(color);
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			mViewPager.setBackgroundDrawable(trans);
		} else {
			mViewPager.setBackground(trans);
		}
		trans.startTransition(ANIMATION_TINTBACKGROUND);
	}

	/** TURIOSUICALLBACK START **/

	/* 
	 * Get the currently visible DisplayFragment in the ViewPager
	 * 
	 * @see com.turios.activities.listeners.TuriosUICallback#getSelectedDisplayFragment()
	 */
	@Override
	public DisplayFragment getSelectedDisplayFragment() {
		return (DisplayFragment) mDisplayPagerAdapter
				.getRegisteredFragment(mViewPager.getCurrentItem());
	}

	
	/* 
	 * Makes the current DisplayFragment refresh it's information
	 * 
	 * @see com.turios.activities.listeners.TuriosUICallback#refreshDisplayFragment()
	 */
	@Override
	public void refreshDisplayFragment() {
		DisplayFragment displayFragment = getSelectedDisplayFragment();
		if (displayFragment != null) {
			displayFragment.refreshScreen();
		}
	}

	/* 
	 * A page has been deleted in DisplayCoreModule
	 * Inform the Viewpager by calling notifySizeChangingDataSetChange
	 * 
	 * @see com.turios.activities.listeners.TuriosUICallback#pageDeleted(int)
	 */
	@Override
	public void pageDeleted(int page) {
		mDisplayPagerAdapter.notifySizeChangingDataSetChange();

		deferInvalidation();
		
		if (display.getPagesCount() == 0) {
			locationService.stopLoactionUpdates();
			wakeScreen.screenRelease();
		}

	}
	
	/* 
	 * A page has been added 
	 * Inform the Viewpager by calling notifySizeChangingDataSetChange
	 * 
	 * @see com.turios.activities.listeners.TuriosUICallback#addPage(int)
	 */
	@Override
	public void addPage(final int page) {
		tintBackground();
		
		mDisplayPagerAdapter.notifyDataSetChanged();
		
		if (mViewPager.getCurrentItem() == page)
			return;

		// http://stackoverflow.com/questions/13585396/viewpager-re-instantiates-items-out-of-order-after-screen-rotation
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mViewPager.setCurrentItem(page);
			}
		}, ANIMATION_TINTBACKGROUND);

	}

	/*
	 * New information has been added to a page
	 * Tint screen to catch attention and scroll to the newly updated DisplayFragment
	 *  
	 * @see com.turios.activities.listeners.TuriosUICallback#pageUpdated(int, java.util.List)
	 */
	@Override
	public void pageUpdated(final int page, final List<String> entries) {
		tintBackground();

		// http://stackoverflow.com/questions/13585396/viewpager-re-instantiates-items-out-of-order-after-screen-rotation
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!(mViewPager.getCurrentItem() == page)) {
					mViewPager.setCurrentItem(page);
				} else {
					invalidateOptionsMenu();
				}

				DisplayFragment displayFrag = (com.turios.activities.fragments.DisplayFragment) mDisplayPagerAdapter
						.getRegisteredFragment(page);
				if (displayFrag != null) {
					displayFrag.appendEntries(entries);
				}

			}
		}, ANIMATION_TINTBACKGROUND);

	}

	private void deferInvalidation() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				invalidateOptionsMenu();
			}
		});
	}
	

	/** TURIOSUICALLBACK END **/

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this, 0);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			GooglePlayServicesUtil.getErrorDialog(
					connectionResult.getErrorCode(), Turios.this, 1).show();

		}
	}

	/** BROADCASTS START **/

	public class RefreshModules extends BroadcastReceiver {
		private static final String TAG = "RefreshModules";

		// @Inject ParseCoreModule parse;
		// @Inject ExpirationCoreModule expiration;

		public RefreshModules() {
		}

		@Override
		public void onReceive(Context context, final Intent intent) {

			parse.refreshModulesPaid(new RefreshCallback() {

				@Override
				public void done(ParseObject object, ParseException e) {

					if (e == null) {
						parse.getParseProfile().reloadCurrentProfile(
								new RefreshCallback() {

									@Override
									public void done(ParseObject object,
											ParseException e) {

										refreshDisplayFragment();
										mTuriosActionBar.recreateActionBar();
										Toast.makeText(getApplicationContext(), getString(R.string.settings_loaded), Toast.LENGTH_SHORT).show();
									}
								});
						expiration.checkExpiration();
					}

				}
			});

		}
	}

	@Override
	public void addDialog(DialogFragment dialog) {
		mPendingDialog = dialog;
		if (!mIsPaused) {
			showPendingDialog();
		} 
	}
	
	private void showPendingDialog() {
		if (mPendingDialog != null && !mPendingDialog.isAdded()) {
			mPendingDialog.show(fm, "dialog_fragment");
			mPendingDialog = null;
		}
	}

	@Override
	public void showPage(int page) {
		mTuriosActionBar.setActionbarNavigationIndex(DisplayFragment.NAVIGATION_INDEX);
		mViewPager.setCurrentItem(page);
	}

	/** BROADCASTS END **/

}
