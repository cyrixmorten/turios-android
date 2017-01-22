package com.turios.activities.fragments;

import javax.inject.Inject;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import butterknife.ButterKnife;

import com.androidmapsextensions.GoogleMap;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gms.maps.GoogleMapOptions;
import com.turios.BuildConfig;
import com.turios.R;
import com.turios.dagger.DaggerActivity;
import com.turios.dagger.DaggerMapFragment;
import com.turios.dagger.quialifiers.ForApplication;
import com.turios.modules.data.AddressHolder;
import com.turios.modules.extend.DirectionsModule;
import com.turios.modules.extend.GoogleMapsModule;
import com.turios.modules.extend.HydrantsModule;
import com.turios.util.Constants;

public class GoogleMapFragment extends DaggerMapFragment implements
		OnQueryTextListener {

	private static final String SUPPORT_MAP_BUNDLE_KEY = "MapOptions";
	private static final String TAG = GoogleMapFragment.class.getSimpleName();

	public static final String FRAGMENT_MAPOPTIONS_TAG = "childfragment:map:options";

	public static int NAVIGATION_INDEX = 0;

	public GoogleMapFragment() {
		mCallback = null;
	}

	public static interface OnGoogleMapFragmentListener {
		void onMapReady(GoogleMap map);
	}

	private AddressHolder mAddressHolder;

	@Inject DaggerActivity activity;
	@Inject @ForApplication Context context;
	@Inject HydrantsModule hydrantsModule;
	@Inject DirectionsModule directionsModule;
	@Inject GoogleMapsModule googlemaps;

	@Override public void onStart() {
		super.onStart();
		if (!BuildConfig.DEBUG) {

			Tracker tracker = GoogleAnalytics.getInstance(getActivity())
					.getTracker(Constants.GATracker);

			tracker.send(MapBuilder.createAppView()
					.set(Fields.SCREEN_NAME, "Hydrants View").build());

		}
	}

	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	public static GoogleMapFragment newInstance(AddressHolder addressHolder) {
		GoogleMapFragment mapFragment = new GoogleMapFragment();
		mapFragment.mAddressHolder = addressHolder;
		return mapFragment;
	}

	public static GoogleMapFragment newInstance(GoogleMapOptions options) {
		Bundle arguments = new Bundle();
		arguments.putParcelable(SUPPORT_MAP_BUNDLE_KEY, options);

		GoogleMapFragment fragment = new GoogleMapFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	public void setCurrentAddress(AddressHolder addressHolder) {
		mAddressHolder = addressHolder;
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override public void onResume() {
		super.onResume();
	}

	@Override public void onPause() {
		super.onPause();
	}

	private SearchView mSearchView;

	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		Log.d(TAG, "onCreateOptionsMenu");
		inflater.inflate(R.menu.menu_googlemaps, menu);
		if (mAddressHolder == null) {
			MenuItem menu_directions = menu
					.findItem(R.id.menu_update_directions);
			menu_directions.setVisible(false);
		}

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) activity
				.getSystemService(Context.SEARCH_SERVICE);

		mSearchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
//		Log.d(TAG, "mSearchView null " + (mSearchView == null));
		mSearchView.setSearchableInfo(searchManager.getSearchableInfo(activity
				.getComponentName()));

		mSearchView.setOnQueryTextListener(this);
		mSearchView.setIconifiedByDefault(false);
		mSearchView.setFocusable(false);
		mSearchView.setFocusableInTouchMode(false);
		// mSearchView.clearFocus();
		// getView().requestFocus();

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override public boolean onQueryTextSubmit(String query) {
		// Hide keyboard
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(FragmentActivity.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
		mSearchView.setFocusable(false);
		mSearchView.setFocusableInTouchMode(false);

		return false;
	}

	@Override public boolean onQueryTextChange(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// case R.id.button_get_directions:
		// AddressHolder addressHolder = ((Turios) getActivity())
		// .getSelectedDisplayFragment().getAddressHolder();
		// Location currentLocation = LocationsCoreModule.getInstance()
		// .getLastLocation();
		// String url = "http://maps.google.com/maps?saddr="
		// + currentLocation.getLatitude() + ","
		// + currentLocation.getLongitude() + "&daddr="
		// + addressHolder.position.latitude + ","
		// + addressHolder.position.longitude + "&mode=driving";
		// Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
		// Uri.parse(url));
		// intent.setClassName("com.google.android.apps.maps",
		// "com.google.android.maps.MapsActivity");
		// startActivity(intent);
		// return true;

		case R.id.menu_update_directions:
			directionsModule.updateDirections(mAddressHolder);
			return true;
		case R.id.menu_map_normal:
			googlemaps.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			return true;
		case R.id.menu_map_satelite:
			googlemaps.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			return true;
		case R.id.menu_map_terrain:
			googlemaps.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			return true;
		case R.id.menu_map_hybrid:
			googlemaps.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			return true;
		}

		return false;
	}
	
		
		private OnGoogleMapFragmentListener mCallback;
	
		
		@Override public void onAttach(Activity activity) {
			super.onAttach(activity);
			try {
				mCallback = (OnGoogleMapFragmentListener) activity;
			} catch (ClassCastException e) {
				throw new ClassCastException(getActivity().getClass().getName()
						+ " must implement OnGoogleMapFragmentListener");
			}
	
		}
		
		@Override public View onCreateView(LayoutInflater inflater,
				ViewGroup container, Bundle savedInstanceState) {
			View view = super.onCreateView(inflater, container, savedInstanceState);
			ButterKnife.bind(this, view);
			if (mCallback != null) {
				mCallback.onMapReady(getExtendedMap());
			}
	
			return view;
		}
		
	@Override public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}


	@Override public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	

}
