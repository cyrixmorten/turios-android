//package com.turios.activities.fragments;
//
//import java.lang.reflect.Field;
//import java.util.List;
//
//import javax.inject.Inject;
//
//import android.annotation.SuppressLint;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.graphics.drawable.TransitionDrawable;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.ViewPager;
//import android.support.v4.view.ViewPager.OnPageChangeListener;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import butterknife.ButterKnife;
//
//import com.google.analytics.tracking.android.Fields;
//import com.google.analytics.tracking.android.GoogleAnalytics;
//import com.google.analytics.tracking.android.MapBuilder;
//import com.google.analytics.tracking.android.Tracker;
//import com.turios.BuildConfig;
//import com.turios.R;
//import com.turios.activities.display.DisplayPagerAdapter;
//import com.turios.activities.listeners.TuriosUICallback;
//import com.turios.activities.util.WakeScreen;
//import com.turios.dagger.DaggerFragment;
//import com.turios.modules.core.DisplayCoreModule;
//import com.turios.modules.core.LocationsCoreModule;
//import com.turios.util.Constants;
//
//public class DisplayContainerFragment extends DaggerFragment implements
//		TuriosUICallback, OnPageChangeListener {
//
//	private static final String TAG = DisplayContainerFragment.class
//			.getSimpleName();
//
//	// http://stackoverflow.com/questions/15207305/getting-the-error-java-lang-illegalstateexception-activity-has-been-destroyed
//	@Override public void onDetach() {
//		super.onDetach();
//
//		try {
//			Field childFragmentManager = Fragment.class
//					.getDeclaredField("mChildFragmentManager");
//			childFragmentManager.setAccessible(true);
//			childFragmentManager.set(this, null);
//
//		} catch (NoSuchFieldException e) {
//			throw new RuntimeException(e);
//		} catch (IllegalAccessException e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	public static int NAVIGATION_INDEX = 0;
//
//	private static final int ANIMATION_TINTBACKGROUND = 750;
//
//	private ViewPager mViewPager;
//	private DisplayPagerAdapter mDisplayPagerAdapter;
//
//	@Inject WakeScreen wakeScreen;
//	@Inject LocationsCoreModule locationService;
//	@Inject DisplayCoreModule display;
//
//	@Override public void onStart() {
//		super.onStart();
//		if (!BuildConfig.DEBUG) {
//
//			Tracker tracker = GoogleAnalytics.getInstance(getActivity())
//					.getTracker(Constants.GATracker);
//
//			tracker.send(MapBuilder.createAppView()
//					.set(Fields.SCREEN_NAME, "DisplayContainer View").build());
//
//		}
//	}
//
//	@Override public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//		// setRetainInstance(true);
//		setHasOptionsMenu(true);
//	}
//
//	public static DisplayContainerFragment newInstance() {
//		Bundle arguments = new Bundle();
//
//		DisplayContainerFragment fragment = new DisplayContainerFragment();
//		fragment.setArguments(arguments);
//		return fragment;
//	}
//
//	@Override public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//	}
//
//	@Override public void onResume() {
//		Log.d(TAG, "onResume");
//		Log.d(TAG, "onResume viewpager " + (mViewPager == null));
//		Log.d(TAG, "onResume adapter " + (mDisplayPagerAdapter == null));
//
//		mDisplayPagerAdapter = new DisplayPagerAdapter(getActivity(),
//				getChildFragmentManager(), display);
//
//		new SetAdapterTask().execute();
//
//		super.onResume();
//	}
//
//	@Override public void onPause() {
//		super.onPause();
//	}
//
//	View root;
//
//	@Override public View onCreateView(LayoutInflater inflater,
//			ViewGroup container, Bundle savedInstanceState) {
//		Log.d(TAG, "onCreateView");
//		root = inflater.inflate(R.layout.fragment_displaycontainer, container,
//				false);
//		ButterKnife.inject(this, root);
//
//		mViewPager = (ViewPager) root.findViewById(R.id.display_pager);
//		mViewPager.setOffscreenPageLimit(10);
//		mViewPager.setOnPageChangeListener(this);
//
//		return root;
//	}
//
//	private class SetAdapterTask extends AsyncTask<Void, Void, Void> {
//		protected Void doInBackground(Void... params) {
//			return null;
//		}
//
//		@Override protected void onPostExecute(Void result) {
//
//			if (mDisplayPagerAdapter != null && mViewPager.getAdapter() == null) {
//				Log.d(TAG, "SetAdapterTask viewpager " + (mViewPager == null));
//				Log.d(TAG, "SetAdapterTask adapter "
//						+ (mDisplayPagerAdapter == null));
//				mViewPager = (ViewPager) root.findViewById(R.id.display_pager);
//				mViewPager.setAdapter(mDisplayPagerAdapter);
//				Log.d(TAG, "SETTING ADAPTER");
//			} else {
//				Log.d(TAG, "SETTING FAILED");
//			}
//
//		}
//	}
//
//	@Override public void onViewCreated(View view, Bundle savedInstanceState) {
//		super.onViewCreated(view, savedInstanceState);
//	}
//
//	@Override public void onDestroyView() {
//		super.onDestroyView();
//		ButterKnife.reset(this);
//	}
//
//	public ViewPager getViewPager() {
//		return mViewPager;
//	}
//
//	public DisplayPagerAdapter getDisplayPagerAdapter() {
//		return mDisplayPagerAdapter;
//	}
//
//	/** VIEWPAGER START **/
//
//	@Override public void onPageScrollStateChanged(int arg0) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override public void onPageScrolled(int arg0, float arg1, int arg2) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override public void onPageSelected(int page) {
//
//		display.setCurrentPageSelected(page);
//
//		DisplayFragment current_fragment = (DisplayFragment) mDisplayPagerAdapter
//				.getRegisteredFragment(page);
//		if (current_fragment != null) {
//			current_fragment.scrollToBottomDelayed(2000);
//		}
//
//		invalidateOptionsMenu();
//	}
//
//	/** VIEWPAGER END **/
//
//	@SuppressLint("NewApi") private void tintBackground() {
//		ColorDrawable[] color = { new ColorDrawable(Color.RED),
//				new ColorDrawable(Color.WHITE) };
//		TransitionDrawable trans = new TransitionDrawable(color);
//		int sdk = android.os.Build.VERSION.SDK_INT;
//		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//			mViewPager.setBackgroundDrawable(trans);
//		} else {
//			mViewPager.setBackground(trans);
//		}
//		trans.startTransition(ANIMATION_TINTBACKGROUND);
//	}
//
//	/** TURIOSUICALLBACK START **/
//
//	@Override public DisplayFragment getSelectedDisplayFragment() {
//		return (DisplayFragment) mDisplayPagerAdapter
//				.getRegisteredFragment(mViewPager.getCurrentItem());
//	}
//
//	@Override public void refreshDisplayFragment() {
//		// SparseArray<DisplayFragment> fragments = mDisplayPagerAdapter
//		// .getRegisteredFragments();
//		// for (int i = 0; i < fragments.size(); i++) {
//		// DisplayFragment displayFragment = fragments.get(i);
//		DisplayFragment displayFragment = getSelectedDisplayFragment();
//		if (displayFragment != null) {
//			displayFragment.refreshScreen();
//		}
//		// }
//	}
//
//	@Override public void notifyDataSetChanged() {
//		mDisplayPagerAdapter.notifyDataSetChanged();
//
//		if (display.getPagesCount() == 0) {
//			locationService.stopLoactionUpdates();
//			wakeScreen.screenRelease();
//		}
//
//		invalidateOptionsMenu();
//	}
//
//	@Override public void selectPage(final int page) {
//		tintBackground();
//		// http://stackoverflow.com/questions/13585396/viewpager-re-instantiates-items-out-of-order-after-screen-rotation
//		new Handler().postDelayed(new Runnable() {
//			@Override public void run() {
//				mViewPager.setCurrentItem(page);
//			}
//		}, ANIMATION_TINTBACKGROUND);
//
//	}
//
//	@Override public void selectPageAndAppend(final int page,
//			final List<String> entries) {
//		tintBackground();
//		// http://stackoverflow.com/questions/13585396/viewpager-re-instantiates-items-out-of-order-after-screen-rotation
//		new Handler().postDelayed(new Runnable() {
//			@Override public void run() {
//				selectPage(page);
//				getSelectedDisplayFragment().appendEntries(entries);
//			}
//		}, ANIMATION_TINTBACKGROUND);
//
//	}
//
//	@Override public void invalidateOptionsMenu() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override public void showExpiredDialog() {
//		// TODO Auto-generated method stub
//
//	}
//
//	/** TURIOSUICALLBACK END **/
//
// }
