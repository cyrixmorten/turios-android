package com.turios.activities.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;

import com.dropbox.sync.android.DbxPath;
import com.google.android.gms.maps.model.LatLng;
import com.turios.R;
import com.turios.activities.SettingsActivity;
import com.turios.activities.Turios;
import com.turios.activities.TuriosActionBarController;
import com.turios.activities.fragments.dialog.DialogFragments;
import com.turios.activities.fragments.dialog.GenericYesNoDialogFragment.GenericYesNoDialogInterface;
import com.turios.activities.util.WakeScreen;
import com.turios.dagger.DaggerFragment;
import com.turios.dagger.quialifiers.ForActivity;
import com.turios.modules.core.DatabaseCoreModule;
import com.turios.modules.core.DisplayCoreModule;
import com.turios.modules.core.ExpirationCoreModule;
import com.turios.modules.core.LocationsCoreModule;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.core.PathsCoreModule;
import com.turios.modules.data.AddressHolder;
import com.turios.modules.data.HydrantHolder;
import com.turios.modules.extend.AccessplansModule;
import com.turios.modules.extend.BasisModule;
import com.turios.modules.extend.BrowserModule;
import com.turios.modules.extend.DirectionsModule;
import com.turios.modules.extend.DropboxModule;
import com.turios.modules.extend.GoogleMapsModule;
import com.turios.modules.extend.DropboxModule.DropboxFileDownloaderResultReceiver;
import com.turios.modules.extend.GoogleMapsModule.FetchAddressCallback;
import com.turios.modules.extend.HydrantsModule;
import com.turios.modules.extend.PicklistModule;
import com.turios.modules.utils.MapUtils;
import com.turios.persistence.MessagesContentProvider;
import com.turios.util.Constants;
import com.turios.util.Device;

public class DisplayFragment extends DaggerFragment {

	protected static final String TAG = DisplayFragment.class.getSimpleName();

	public static int NAVIGATION_INDEX = 0;

	private ScrollView mScrollView;
	private ViewGroup mContainerView;
	private View mRootView;

	private View statusBar;
	private View visibleStatus;
	private View hiddenStatus;
	private View optionsBar;

	// visible
	private TextView tvReceived;
	private TextView tvAutomaticDeleteText;
	private TextView tvAutomaticDelete;

	private ImageButton hydrantsButton;
	private ImageButton routeButton;
	private ImageButton pdfButton;

	private ImageButton expandButton;
	
	private ProgressDialog progress;

	@Inject
	@ForActivity
	Context context;

	@Inject
	FragmentManager fm;

	@Inject
	TuriosActionBarController mTuriosActionBar;
	@Inject
	ActionBar mActionBar;
	@Inject
	MenuInflater mMenuInflater;
	@Inject
	LayoutInflater inflater;

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
	DatabaseCoreModule database;
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
	DropboxModule dropboxModule;
	@Inject
	GoogleMapsModule googleMapsModule;
	@Inject
	DirectionsModule directionsModule;
	@Inject
	HydrantsModule hydrantsModule;
	@Inject
	PicklistModule picklistModule;

	// invisible
	private ViewGroup mContainerhydrantsNearAddress;

	private long mPageid = -1;
	private List<String> mEntries;
	private AddressHolder mAddressHolder;

	public static DisplayFragment newInstance(int position) {
		DisplayFragment frag = new DisplayFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.EXTRA_MESSAGE, position);

		frag.setArguments(bundle);

		return frag;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	private ScheduledExecutorService exec;

	private void startDeleteCountdownExec() {
		shutDownExec();
		exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				updateAutomaticDeleteTime(true);
			}
		}, 0, 1, TimeUnit.MINUTES);
	}

	private void shutDownExec() {
		if (exec != null && !exec.isTerminated()) {
			exec.shutdown();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mEntries = new ArrayList<String>();
		super.onCreate(savedInstanceState);
	}

	private int hiddenStatusHeight;
	private int currentStatusBarHeight;

	private void getStatusBarHeight() {
		final ViewTreeObserver observer = hiddenStatus.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {

				hiddenStatus.measure(MeasureSpec.UNSPECIFIED,
						MeasureSpec.UNSPECIFIED);
				hiddenStatusHeight = hiddenStatus.getMeasuredHeight();

				currentStatusBarHeight = statusBar.getHeight();

				ViewTreeObserver obs = hiddenStatus.getViewTreeObserver();

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					obs.removeOnGlobalLayoutListener(this);
				} else {
					obs.removeGlobalOnLayoutListener(this);
				}
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		if (mPageid != -1
				&& mTuriosActionBar.getSelectedNavigationIndex() == DisplayFragment.NAVIGATION_INDEX
				|| mActionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_STANDARD) {

			mMenuInflater.inflate(R.menu.menu_display, menu);

			MenuItem menu_add_time = menu.findItem(R.id.button_add_time);

			if (!basisModule.getPreferences().isAutomaticDeleteEnabled()
					|| display.isPageDeletionDelayed(mPageid)) {
				menu_add_time.setVisible(false);
			}
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {

		case R.id.button_remove:
			DialogFragment dialog = (DialogFragment) fm
					.findFragmentByTag("fragment_confirm_delete");
			final int page = display.getCurrentPageSelected();
			if (dialog == null || !dialog.isVisible()) {
				DialogFragments.deletePage(new GenericYesNoDialogInterface() {

					@Override
					public void yesClicked() {
						display.deletePage(page);
					}

					@Override
					public void noClicked() {
					}
				}).show(fm, "fragment_confirm_delete");
			}
			return true;

		case R.id.button_add_time:
			display.addPageToDelayList(mPageid);
			updateAutomaticDeleteTime(false);
			Toast.makeText(context,
					context.getString(R.string.page_delete_delayed),
					Toast.LENGTH_SHORT).show();
			getActivity().invalidateOptionsMenu();
			return true;

		}

		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

//		Log.d(TAG, "onCreateView");

		this.mRootView = inflater.inflate(R.layout.fragment_display, container,
				false);

		ButterKnife.inject(this, mRootView);

		this.mContainerView = (ViewGroup) mRootView.findViewById(R.id.display);
		this.mScrollView = (ScrollView) mRootView.findViewById(R.id.scrollview);

		return mRootView;
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {

		Log.d(TAG, "onViewStateRestored");
		createStatusBar();
		createOptionsBar();

		// extract data from database
		Bundle bundle = getArguments();
		int position = bundle.getInt(Constants.EXTRA_MESSAGE);

		mPageid = display.getPageId(position);
		
		Log.d(TAG, "position " + position);
		Log.d(TAG, "pageId " + mPageid);

		Cursor messageCursor = database.getPageById(mPageid);

		if (messageCursor.getCount() == 0) {
			Log.d(TAG, "page not found in database");
			mRootView.findViewById(android.R.id.empty).setVisibility(
					View.VISIBLE);
			statusBar.setVisibility(View.GONE);
			optionsBar.setVisibility(View.GONE);
			statusBar.setVisibility(View.GONE);
			mEntries.clear();

		} else {
			// read database
			Log.d(TAG, "reading database");
			messageCursor.moveToNext();
			long received = messageCursor.getLong(messageCursor
					.getColumnIndexOrThrow(MessagesContentProvider.TIMESTAMP));

			setTimeReceived(received);

			String message = messageCursor.getString(messageCursor
					.getColumnIndexOrThrow(MessagesContentProvider.MESSAGE));

			mEntries = display.processEntriesToArray(message);

			for (String entry : mEntries) {
				addTextEntry(entry);
			}
		}

		messageCursor.close();
		
		super.onViewStateRestored(savedInstanceState);

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}

	private OnClickListener ExpandClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			boolean isExpanded = (Boolean) expandButton
					.getTag(R.id.TAG_EXPANDED);
			int originalHeight = (Integer) expandButton
					.getTag(R.id.TAG_ORIGINAL_HEIGHT);

			if (isExpanded) {
				expandButton.setTag(R.id.TAG_EXPANDED, false);
				expandButton.setImageResource(R.drawable.ic_action_down);
				// statusBar.setLayoutParams(new FrameLayout.LayoutParams(
				// LayoutParams.MATCH_PARENT, originalHeight));
				Log.d(TAG, "Collapsing to " + originalHeight);
				ValueAnimator va = ValueAnimator.ofInt(currentStatusBarHeight,
						originalHeight);
				va.setDuration(500);
				va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					public void onAnimationUpdate(ValueAnimator animation) {
						Integer value = (Integer) animation.getAnimatedValue();
						statusBar.getLayoutParams().height = value.intValue();
						statusBar.requestLayout();
					}
				});
				va.start();
			} else {
				expandButton.setTag(R.id.TAG_EXPANDED, true);
				expandButton.setImageResource(R.drawable.ic_action_collapse);
				currentStatusBarHeight = originalHeight + hiddenStatusHeight;
				// statusBar.setLayoutParams(new FrameLayout.LayoutParams(
				// LayoutParams.MATCH_PARENT, currentStatusBarHeight + 15));
				Log.d(TAG, "Expanding to " + originalHeight + "+"
						+ hiddenStatusHeight + "=" + currentStatusBarHeight);
				ValueAnimator va = ValueAnimator.ofInt(originalHeight,
						currentStatusBarHeight);
				va.setDuration(500);
				va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					public void onAnimationUpdate(ValueAnimator animation) {
						Integer value = (Integer) animation.getAnimatedValue();
						statusBar.getLayoutParams().height = value.intValue();
						statusBar.requestLayout();
					}
				});
				va.start();
			}

		}
	};

	private void createStatusBar() {
		this.tvReceived = (TextView) mRootView
				.findViewById(R.id.status_time_received);
		this.tvAutomaticDeleteText = (TextView) mRootView
				.findViewById(R.id.status_time_delete_relative_text);
		this.tvAutomaticDelete = (TextView) mRootView
				.findViewById(R.id.status_time_delete_relative);

		this.statusBar = mRootView.findViewById(R.id.displayStatusBar);
		this.visibleStatus = mRootView.findViewById(R.id.status_always_visible);
		this.mContainerhydrantsNearAddress = (ViewGroup) mRootView
				.findViewById(R.id.status_hydrants_near_address_container);
		this.hiddenStatus = mRootView.findViewById(R.id.status_hidden);

		getStatusBarHeight();

	}

	private void createOptionsBar() {
		this.optionsBar = mRootView.findViewById(R.id.optionsBar);

		optionsBar.setVisibility(View.INVISIBLE);

		createOptionsExpandButton();
		createHydrantsButton();
		createRouteButton();
		createPDFButton();
	}

	private void createOptionsExpandButton() {
		this.expandButton = (ImageButton) mRootView
				.findViewById(R.id.button_more);

		expandButton.setImageResource(R.drawable.ic_action_down);
		expandButton.setTag(R.id.TAG_EXPANDED, false);
		expandButton.setTag(R.id.TAG_ORIGINAL_HEIGHT,
				statusBar.getLayoutParams().height);
		expandButton.setOnClickListener(ExpandClickListener);

		setExpandOptionsReady(false);
	}

	private void createHydrantsButton() {
		this.hydrantsButton = (ImageButton) mRootView
				.findViewById(R.id.button_hydrants);

		hydrantsButton.setTag(false);

		setHydrantsReady((mAddressHolder != null && mAddressHolder.isHydrantsNearby()));
		hydrantsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Log.d(TAG, "hydrantsButton onClick " + device.isMultiPane());

				googleMapsModule.clearMap();

				if (!device.isMultiPane()) {
					// select the googlemaps tab
					mActionBar
							.setSelectedNavigationItem(GoogleMapFragment.NAVIGATION_INDEX);
				}
				hydrantsModule.addHydrantsNearAddress(mAddressHolder, true);
			}
		});
	}

	private void createRouteButton() {
		this.routeButton = (ImageButton) mRootView
				.findViewById(R.id.button_route);
		routeButton.setTag(false);
		setRouteReady(mAddressHolder != null);
		routeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Log.d(TAG, "routeButton onClick");

				googleMapsModule.clearMap();

				if (!device.isMultiPane()) {
					// select the googlemaps tab
					mActionBar
							.setSelectedNavigationItem(GoogleMapFragment.NAVIGATION_INDEX);
				}

				directionsModule.addDirectionsToMap(mAddressHolder);

			}
		});
	}

	private void createPDFButton() {
		this.pdfButton = (ImageButton) mRootView.findViewById(R.id.button_pdf);
		pdfButton.setTag("");
		setPdfReady(false, "");
		pdfButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				progress = new ProgressDialog(getActivity());
				progress.setMessage(getString(R.string.working_please_wait));
				progress.show();
				
				accessplansModule.viewAccessplan((String) pdfButton.getTag());
			}
		});
	}

	@Override
	public void onResume() {

		Log.d(TAG, "onResume");

		if (basisModule.getPreferences().isAutomaticDeleteEnabled()) {
			startDeleteCountdownExec();
		} else {
			tvAutomaticDelete.setVisibility(View.INVISIBLE);
			tvAutomaticDeleteText.setVisibility(View.INVISIBLE);
		}

		refreshScreen();

		super.onResume();
	}

	public void refreshScreen() {
		if (mEntries == null || mEntries.isEmpty()) {
			// do nothing
		} else {

			showStatusBar();
			showOptionsBar();
			runEntryCheck();

			if (mAddressHolder == null) {
				aquireAdress();
			}
			updateNearbyHydrantInformation();

		}
	}

	private void showOptionsBar() {

		expandButton.setVisibility(View.GONE);
		hydrantsButton.setVisibility(View.GONE);
		routeButton.setVisibility(View.GONE);
		pdfButton.setVisibility(View.GONE);

		boolean showOptionsBar = false;

		if (googleMapsModule.isActivated()) {
			if (hydrantsModule.isActivated()) {
				showOptionsBar = true;
				expandButton.setVisibility(View.VISIBLE);
				hydrantsButton.setVisibility(View.VISIBLE);
			}
			if (directionsModule.isActivated()) {
				showOptionsBar = true;
				routeButton.setVisibility(View.VISIBLE);
			}
		}
		if (accessplansModule.isActivated()) {
			showOptionsBar = true;
			pdfButton.setVisibility(View.VISIBLE);
		}

		if (showOptionsBar) {
			optionsBar.setVisibility(View.VISIBLE);
			hiddenStatus.setVisibility(View.VISIBLE);
		} else {
			optionsBar.setVisibility(View.GONE);
			hiddenStatus.setVisibility(View.GONE);
		}

	}

	private void showStatusBar() {
		if (statusBar != null) {
			if (googleMapsModule.isActivated()
					|| accessplansModule.isActivated()) {
				statusBar.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onPause() {
		Log.d(TAG, "onPause");
		shutDownExec();
		if (progress != null && progress.isShowing()) {
			progress.dismiss();
		}
		super.onPause();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		shutDownExec();
		super.onDestroy();
	}

	public long getPageId() {
		return mPageid;
	}

	private void setExpandOptionsReady(boolean value) {
		if (value) {
			expandButton.setSelected(true);
			setImageButtonReady(expandButton, true, -1);
		} else {
			expandButton.setSelected(false);
			setImageButtonReady(expandButton, false, -1);
		}
	}

	private void setHydrantsReady(boolean value) {
		if ((Boolean) hydrantsButton.getTag() == false) {
			if (value) {
				setExpandOptionsReady(true);
				hydrantsButton.setTag(true);
				setImageButtonReady(hydrantsButton, true, R.color.Red);
			} else {
				setImageButtonReady(hydrantsButton, false, R.color.DarkGrey);
			}
		}
	}

	private void setRouteReady(boolean value) {
		if ((Boolean) routeButton.getTag() == false) {
			if (value) {
				routeButton.setTag(true);
				setImageButtonReady(routeButton, true, R.color.Orange);
			} else {
				setImageButtonReady(routeButton, false, R.color.DarkGrey);
			}
		}
	}

	private void setPdfReady(boolean value, final String filename) {
		if (((String) pdfButton.getTag()).isEmpty()) {
			if (value && accessplansModule.isAccessplan(filename)) {
				pdfButton.setTag(filename);
				setImageButtonReady(pdfButton, true, R.color.Green);
			} else {
				setImageButtonReady(pdfButton, false, R.color.DarkGrey);
			}
		}
	}

	/**
	 * @param button
	 * @param value
	 * @param color
	 *            set to -1 to prevent color change
	 */
	private void setImageButtonReady(ImageButton button, boolean value,
			int color) {
		if (!isAdded())
			return;
		if (value) {
			button.setAlpha(1.0f);
			button.setClickable(true);
			button.setEnabled(true);
		} else {
			button.setAlpha(0.35f);
			button.setClickable(false);
			button.setEnabled(false);
		}
		if (color != -1) {
			button.setColorFilter(getResources().getColor(color));
		}
	}

	private void setTimeReceived(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
		tvReceived.setText(sdf.format(new Date(time)));
		tvReceived.setTag(R.id.TAG_TIMERECEIVED, time);
	}

	public void updateAutomaticDeleteTime(final boolean performDeletion) {
		handler.post(new Runnable() {

			@Override
			public void run() {

				tvAutomaticDelete.setVisibility(View.VISIBLE);
				tvAutomaticDeleteText.setVisibility(View.VISIBLE);

				long deleteTime = basisModule.getPreferences()
						.getAutomaticDeleteTime();

				long delay = display.getPageDeletionDelay(mPageid);

				deleteTime = deleteTime + delay;

				Long timeReceived = (Long) tvReceived
						.getTag(R.id.TAG_TIMERECEIVED);

				long now = System.currentTimeMillis();

				if (timeReceived == null) {
					tvAutomaticDelete.setText("N/A");
				} else {

					long timeToDelete = (timeReceived + deleteTime);

					CharSequence relativeTimeString = DateUtils
							.getRelativeTimeSpanString(timeToDelete, now,
									DateUtils.SECOND_IN_MILLIS);

					tvAutomaticDelete.setText(relativeTimeString);

					if (timeToDelete - now <= 0 && performDeletion) {
						display.deletePage(mPageid);
					}

				}
			}
		});

	}

	public void runEntryCheck() {
		int picklistEntry = picklistModule.getPreferences().getPicklistEntry();
		int accessPlanEntry = accessplansModule.getPreferences()
				.getAccessPlanEntry();

		for (int i = 0; i < mContainerView.getChildCount()
				&& mEntries.size() >= i; i++) {
			int entry = i + 1;
			String entryText = mEntries.get(i);

			View entryView = mContainerView.getChildAt(i);
			if (entryView instanceof TextView) {
				TextView entryTextView = (TextView) entryView;
				String currentText = entryTextView.getText().toString();

				// BASIS - PREFIX,SUFFIX,STANDARDVALUE
				String processedText = basisModule
						.addPrefixSuffixfixStandardToString(entryText, entry);

				if (!processedText.equals(currentText)) {
					Log.d(TAG, "BASIS " + currentText + "!=" + processedText);
					replaceTextEntry(i, processedText);
				}

				// PICKLIST
				if (picklistEntry == entry) {
					String pickListEntry = convertIfPicklist(entry, entryText);
					if (!currentText.equals(pickListEntry)) {
						Log.d(TAG, "PICKLIST " + currentText + "!="
								+ pickListEntry);
						replaceTextEntry(i, pickListEntry);
					}
				}

				// ACCESSPLAN
				if (accessPlanEntry == entry
						&& accessplansModule.isAccessplan(entryText)) {
					setPdfReady(true, entryText);
				}

			}
		}
	}

	private void replaceTextEntry(int index, String text) {
		mContainerView.removeViewAt(index);
		addTextEntryAt(text, index);
	}

	public void cancelScrollToBottom() {
		mScrollView.removeCallbacks(scrollToBottom);
	}

	public void scrollToBottomDelayed(int delay) {
		handler.postDelayed(scrollToBottom, delay);
	}

	private Runnable scrollToBottom = new Runnable() {
		@Override
		public void run() {
			mScrollView.fullScroll(View.FOCUS_DOWN);
		}
	};

	public boolean haveAddressPosition() {
		return mAddressHolder != null && mAddressHolder.position != null;
	}

	public AddressHolder getAddressHolder() {
		return mAddressHolder;
	}

	public void addTextEntry(String text) {
		int nextIndex = mContainerView.getChildCount();
		addTextEntryAt(text, nextIndex);
	}

	public void addEntry(TextView entry) {
		addEntryAt(entry, mContainerView.getChildCount());
	}

	public void addEntry(View entry) {
		mRootView.findViewById(android.R.id.empty).setVisibility(View.GONE);
		statusBar.setVisibility(View.VISIBLE);
		mContainerView.addView(entry);
	}

	public void addTextEntryAt(String text, int index) {
		// Log.d(TAG, "addTextEntryAt " + text + " " + index);
		TextView entry_tv = (TextView) inflater.inflate(R.layout.view_entry,
				null);
		text = convertIfPicklist(index + 1, text);
		entry_tv.setText(text);
		Typeface face = Typeface.createFromAsset(context.getAssets(),
				"fonts/DroidSans-Bold.ttf");
		entry_tv.setTypeface(face);
		addEntryAt(entry_tv, index);
	}

	public void addEntryAt(TextView entry, int index) {
		mRootView.findViewById(android.R.id.empty).setVisibility(View.GONE);
		statusBar.setVisibility(View.VISIBLE);
		mContainerView.addView(entry, index);
	}

	public void appendEntries(List<String> append_entries) {
		// int delay = 200;
		for (final String entry : append_entries) {
			// append to existing entries
			mEntries.add(entry);

			// new Handler().postDelayed(new Runnable() {
			//
			// @Override
			// public void run() {
			addTextEntry(entry);
			// }
			// }, delay);
			//
			// delay += 200;
		}

		scrollToBottomDelayed(2000);

	}

	private String convertIfPicklist(int entry, String text) {
		int picklistEntry = picklistModule.getPreferences().getPicklistEntry();
		if (entry == picklistEntry) {

			return picklistModule.convertKey(text);
		}
		return text;
	}

	private void aquireAdress() {
		// Log.d(TAG, "aquireAdress");

		GoogleMapsModule mapsModule = googleMapsModule;
		// only if we do not already have a valid address

		if (!mapsModule.isActivated())
			return;

		int addressEntry = mapsModule.getPreferences().getAddressEntry();
		int cityEntry = mapsModule.getPreferences().getCityEntry();

		if (mEntries.size() < addressEntry)
			return;
		if (mEntries.size() < cityEntry)
			return;

		if (addressEntry != 0 && cityEntry != 0) {
			final String addressString = mEntries.get(addressEntry - 1);
			final String cityString = mEntries.get(cityEntry - 1);

			mapsModule.lookupAddress(new FetchAddressCallback() {

				@Override
				public void addressLookupFailed() {
				}

				@Override
				public void addressFound(AddressHolder addressHolder) {
					LatLng deviceLocation = locationService
							.getLastLocationLatLng();

					// if (deviceLocation != null) {
					// MapUtils.distBetween(pos1, pos2)
					mAddressHolder = addressHolder;

					setRouteReady(true);
					updateNearbyHydrantInformation();
					// }
				}
			}, addressString, cityString);
		}
	}

	private void updateNearbyHydrantInformation() {
		if (haveAddressPosition()
				&& (!mAddressHolder.isHydrantsNearby() || mContainerhydrantsNearAddress.getChildCount() < 3)) {

			final LatLng addressPosition = mAddressHolder.position;
			final List<HydrantHolder> hydrants = hydrantsModule
					.getNearbyHydrants(mAddressHolder);

			if (hydrants.size() > 0) {
				mAddressHolder.setHydrantsNearby(true);
				handler.post(new Runnable() {

					@Override
					public void run() {
						SortedMap<Integer, HydrantHolder> distanceSortedHydrants = new TreeMap<Integer, HydrantHolder>();

						for (HydrantHolder hydrant : hydrants) {
							LatLng hydrantPosition = new LatLng(hydrant
									.getLatitude(), hydrant.getLongitude());
							float distanceToAddress = MapUtils.distBetween(
									addressPosition, hydrantPosition);
							distanceSortedHydrants.put((int) distanceToAddress,
									hydrant);
						}

						mContainerhydrantsNearAddress.removeAllViews();

						int counter = 0;
						for (Integer distance : distanceSortedHydrants.keySet()) {

							HydrantHolder hydrant = distanceSortedHydrants
									.get(distance);

							createHydrantView(hydrant, distance);

							counter++;
							if (counter == 3)
								break;
						}
						setHydrantsReady(true);

						// update height after views has been added
						getStatusBarHeight();
					}
				});

			}
		}
	}

	private void createHydrantView(HydrantHolder hydrant,
			float distanceToAddress) {

		Log.d(TAG, "createHydrantView return : " + !isAdded());
		if (!isAdded())
			return;

		View hydrantView = (View) inflater.inflate(R.layout.view_hydrant, null);

		TextView hydrantType = (TextView) hydrantView
				.findViewById(R.id.tv_hydrantType);
		TextView hydrantAddress = (TextView) hydrantView
				.findViewById(R.id.tv_hydrantAddress);
		TextView hydrantDistance = (TextView) hydrantView
				.findViewById(R.id.tv_hydrantDistance);

		hydrantType.setText(hydrant.getHydrantType());
		hydrantAddress.setText(hydrant.getAddress() + " "
				+ hydrant.getAddressNumber());
		hydrantDistance.setText(distanceToAddress + " "
				+ getString(R.string.meter));

		mContainerhydrantsNearAddress.addView(hydrantView);
		hydrantView.setSelected(true);
	}

}
