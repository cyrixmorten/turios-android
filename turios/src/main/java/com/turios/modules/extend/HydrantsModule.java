package com.turios.modules.extend;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.GoogleMap.OnCameraChangeListener;
import com.androidmapsextensions.MarkerOptions;
import com.dropbox.sync.android.DbxPath.InvalidPathException;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.turios.R;
import com.turios.dagger.quialifiers.ForApplication;
import com.turios.modules.core.ExpirationCoreModule;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.core.PathsCoreModule;
import com.turios.modules.data.AddressHolder;
import com.turios.modules.data.HydrantHolder;
import com.turios.modules.extend.GoogleMapsModule.GoogleMapsCallback;
import com.turios.modules.preferences.HydrantsModulePreferences;
import com.turios.modules.utils.MapUtils;
import com.turios.persistence.Preferences;
import com.turios.settings.modules.status.CheckStrategy;
import com.turios.settings.modules.status.CheckStrategyHydrants;
import com.turios.util.Constants;
import com.turios.util.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HydrantsModule extends StandardModule {

	private final HydrantsModulePreferences modulePreferences;

	public HydrantsModulePreferences getPreferences() {
		return modulePreferences;
	}

	public interface HydrantOptionsChanged {
		public void enableAllHydrants();

		public void disableAllHydrants();
	}

	private static final String TAG = "HydrantsModule";

	private List<HydrantHolder> mHydrants;
	private Map<HydrantType, List<MarkerOptions>> mMarkers;

	private HydrantOptionsChanged mHydrantsOptionsChangedListener;
	private Set<HydrantType> mEnabledTypes;

	private final PathsCoreModule paths;
	// private final LocationsCoreModule location;

	private final GoogleMapsModule googlemaps;

	@Inject public HydrantsModule(@ForApplication Context context,
			Preferences preferences, ExpirationCoreModule expiration,
			ParseCoreModule parse, PathsCoreModule paths,
			GoogleMapsModule googlemaps) {
		super(context, preferences, expiration, parse, MODULES.HYDRANTS);

		Log.i(TAG, "CREATING MODULE " + TAG);

		this.paths = paths;
		// this.location = location;
		this.googlemaps = googlemaps;
		this.modulePreferences = new HydrantsModulePreferences(preferences);
        this.mMarkers = new HashMap<HydrantType, List<MarkerOptions>>();

		this.mEnabledTypes = new HashSet<HydrantsModule.HydrantType>();
	}

	public void load() {

		new ReadHydrantsFiles().execute();

	}

	@Override public CheckStrategy getCheckStrategy() {
		return new CheckStrategyHydrants(context, this);
	}

	public HydrantOptionsChanged getHydrantsOptionsChangedListener() {
		return mHydrantsOptionsChangedListener;
	}

	public void setHydrantsOptionsChangedListener(
			HydrantOptionsChanged mHydrantsOptionsChangedListener) {
		this.mHydrantsOptionsChangedListener = mHydrantsOptionsChangedListener;
	}

	public void enableAllHydrants() {
		mEnabledTypes = getHydrantTypes();
		if (mHydrantsOptionsChangedListener != null) {
			mHydrantsOptionsChangedListener.enableAllHydrants();
		}
	}

	public void disableAllHydrants() {
		mEnabledTypes.clear();

		GoogleMap map = googlemaps.getMap();
		if (map != null) {
			map.clear();
		}

		if (mHydrantsOptionsChangedListener != null) {
			mHydrantsOptionsChangedListener.disableAllHydrants();
		} else {
			Log.d(TAG, "mHydrantsOptionsChangedListener was null");
		}
	}

	public void enableHydrantType(HydrantType type) {
		mEnabledTypes.add(type);
	}

	public void disableHydrantType(HydrantType type) {
		mEnabledTypes.remove(type);
	}

	public boolean isHydrantTypeEnabled(HydrantType type) {
		return mEnabledTypes.contains(type);
	}

	public List<MarkerOptions> getEnabledHydrantMarkers() {
		List<MarkerOptions> enabledMarkers = new ArrayList<MarkerOptions>();
		Set<HydrantType> hydrantTypes = mMarkers.keySet();
		for (HydrantType hydrantType : hydrantTypes) {
			if (isHydrantTypeEnabled(hydrantType)) {
				List<MarkerOptions> markers = mMarkers.get(hydrantType);
				for (MarkerOptions marker : markers) {
					enabledMarkers.add(marker);
				}
			}
		}
		return enabledMarkers;
	}

	public boolean hasHydrants() {
		return mHydrants.size() > 0;
	}

	public void addAllHydrants() {

		enableAllHydrants();

		GoogleMap map = googlemaps.getMap();

		map.setTrafficEnabled(modulePreferences.showTraffic());
		map.setMapType(modulePreferences.getMapType());

		addHydrants(map);
	}

	public void addEnabledHydrants() {

		GoogleMap map = googlemaps.getMap();

		addHydrants(map);
	}

	public void addHydrantsNearAddress(final AddressHolder addressHolder,
			final boolean zoomToAddress) {

		final GoogleMap map = googlemaps.getMap();

		// // have to call this early to show the correct ToggleButtons in
		// // GoogleMapOptionsFragment
		// enableAllHydrants();

		final OnCameraChangeListener addHydrantsAfterZoom = new OnCameraChangeListener() {

			@Override public void onCameraChange(CameraPosition cameraPosition) {
				Log.d(TAG, cameraPosition.target.toString());
				Log.d(TAG, addressHolder.position.toString());

				final GoogleMap map = googlemaps.getMap();
				// if (Location.distanceBetween(cameraPosition.tar,
				// startLongitude, endLatitude, endLongitude, results)) {
				new Handler().postDelayed(new Runnable() {

					@Override public void run() {
						addAllHydrants();
					}
				}, 500);

				map.setOnCameraChangeListener(null); // unregister
				// }
			}
		};

		if (map == null) {
			// wait for the map to be ready before adding hydrants
			googlemaps.setGoogleMapsCallback(new GoogleMapsCallback() {

				@Override public void mapReady(GoogleMap map) {

					if (zoomToAddress) {
						map.setOnCameraChangeListener(addHydrantsAfterZoom);
						googlemaps.zoomTo(addressHolder);
					} else {
						addAllHydrants();
					}

					googlemaps.setGoogleMapsCallback(null); // unregister
				}
			});
		} else {

			if (zoomToAddress) {
				// only setOnCameraChangeListener if cammera needs to move
				LatLng cammeraPos = map.getCameraPosition().target;
				LatLng addressPos = addressHolder.position;

				float[] results = new float[1];
				Location.distanceBetween(cammeraPos.latitude,
						cammeraPos.longitude, addressPos.latitude,
						addressPos.longitude, results);

				// Log.d(TAG, "distance " + results[0]);

				if (results[0] > 1) {
					map.setOnCameraChangeListener(addHydrantsAfterZoom);
					googlemaps.zoomTo(addressHolder);
				} else {
					googlemaps.zoomTo(addressHolder);
					addAllHydrants();
				}

			}

		}

	}

	/**
	 * Assumes a non null parameters
	 * 
	 * @param map
	 */
	private void addHydrants(GoogleMap map) {

		Log.d(TAG, "addHydrants");

		if (mHydrants == null || mHydrants.isEmpty()) {
			Toast.makeText(context,
					context.getString(R.string.missing_hydrants),
					Toast.LENGTH_LONG).show();
			return;
		} else {
			for (MarkerOptions marker : getEnabledHydrantMarkers()) {
				map.addMarker(marker);
			}
		}

	}

	public List<HydrantHolder> getNearbyHydrants(
			final AddressHolder addressHolder) {
		List<HydrantHolder> nearHydrants = new ArrayList<HydrantHolder>();

		if (addressHolder == null || addressHolder.position == null)
			return nearHydrants;

		final double origin_lat = addressHolder.position.latitude;
		final double origin_lng = addressHolder.position.longitude;

		if (mHydrants != null) {
			for (HydrantHolder hydrant : mHydrants) {
				double hydrant_lat = hydrant.getLatitude();
				double hydrant_lng = hydrant.getLongitude();

				double dist = MapUtils.distBetween(origin_lat, origin_lng,
						hydrant_lat, hydrant_lng);

				if (dist < modulePreferences.getHydrantsLoadradius()) {
					nearHydrants.add(hydrant);
				}
			}
		}

		return nearHydrants;

	}

	private class ReadHydrantsFiles extends
			AsyncTask<Void, Integer, List<HydrantHolder>> {

		File[] hydrantsFiles = new File[0];

		// Before running code in separate thread
		@Override protected void onPreExecute() {

			loadStarted();

			String filepath = paths.PATH_LOCAL_HYDRANTS;

			File hydrantsPath = new File(filepath);
			hydrantsFiles = hydrantsPath.listFiles(new FilenameFilter() {

				@Override public boolean accept(File dir, String filename) {
					return filename.toLowerCase(Locale.ENGLISH).contains(
							Constants.FILETYPE_CSV);
				}
			});

			int lineCount = 0;

			if (hydrantsFiles == null || hydrantsFiles.length == 0) {
				if (!preferences.isFirstStartUp()) {
					// TODO notify user
				}
				Log.e(TAG, "Missing hydrants");
				if (moduleCallback != null) {
					moduleCallback.doneLoadingModule(toString());
				}
				this.cancel(false);
			} else {

				for (File file : hydrantsFiles) {
					// store linecount for visual progress update
					lineCount += Files.lineCount(file);

				}

			}

		}

		// The code to be executed in a background thread.
		@Override protected List<HydrantHolder> doInBackground(Void... args) {
			List<HydrantHolder> all_hydrants = new ArrayList<HydrantHolder>();
			// Directory path here
			int totalLineProgress = 0;

			// // required
			int indexLatitude = modulePreferences.indexLatitude();
			int indexLongitude = modulePreferences.indexLongitude();
			// optional
			int indexAddress = modulePreferences.indexAddress();
			int indexAddressNumber = modulePreferences.indexAddressNumber();
			int indexAddressRemark = modulePreferences.indexAddressRemark();
			int indexRemark = modulePreferences.indexRemark();
			// decimals
			int latitude_decimal = modulePreferences.latitude_decimal();
			int longitude_decimal = modulePreferences.longitude_decimal();

			if (indexLatitude <= 0 || indexLongitude <= 0)
				return all_hydrants;

			for (File file : hydrantsFiles) {
				BufferedReader in = null;
				try {

					String hydrantspath = paths.PATH_LOCAL_HYDRANTS;
					File hydrantsPath = new File(hydrantspath);

					// Read File Line By Line
					in = new BufferedReader(new InputStreamReader(
							new FileInputStream(file), "windows-1252"));
					String strLine;
					while ((strLine = in.readLine()) != null) {

						totalLineProgress++;

						String[] hydrantParts = strLine.split(";", -1);

						String errors = "";
						final String hydrantType = file.getName().replace(
								Constants.FILETYPE_CSV, "");

						File[] iconFiles = hydrantsPath
								.listFiles(new FilenameFilter() {

									@Override public boolean accept(File dir,
											String filename) {
										if (filename.contains(hydrantType)
												&& filename
														.contains(Constants.FILETYPE_PNG)) {
											return true;
										}
										return false;
									}
								});

						HydrantHolder.Builder hb = new HydrantHolder.Builder();
						if (hydrantParts.length >= 5) {

							hb.setHydrantType(hydrantType);
							if (iconFiles.length != 0) {
								hb.setIconPath(hydrantspath
										+ File.separatorChar
										+ iconFiles[0].getName());
							}
							hb.setLatitude(hydrantParts[indexLatitude],
									latitude_decimal);
							hb.setLongitude(hydrantParts[indexLongitude],
									longitude_decimal);

							if (indexAddress > 0)
								hb.setAddress(hydrantParts[indexAddress]);
							if (indexAddressNumber > 0)
								hb.setAddressNumber(hydrantParts[indexAddressNumber]);
							if (indexAddressRemark > 0)
								hb.setAddressRemark(hydrantParts[indexAddressRemark]);
							if (indexRemark > 0)
								hb.setRemark(hydrantParts[indexRemark]);

							if (hb.getErrors().isEmpty()) {
								HydrantHolder hydrant = hb.build();
								all_hydrants.add(hydrant);
							} else {
								// TODO write error file to Dropbox if possible,
								// otherwise locally
								Log.d(TAG, errors);
							}
						} else {
							errors = "Missing information";
						}

						publishProgress(totalLineProgress);
					}
				} catch (InvalidPathException e) {
					Log.e(TAG, e.getMessage(), e);
				} catch (IOException e) {
					Log.e(TAG, e.getMessage(), e);
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
						}
					}
				}
			}
			Log.d(TAG, "" + all_hydrants.size());
			return all_hydrants;
		}

		// Update the progress
		@Override protected void onProgressUpdate(Integer... values) {
			// set the current progress of the progress dialog
			// if (progressDialog != null && values != null && values.length >
			// 0) {
			// progressDialog.setProgress(values[0]);
			// }
		}

		@Override protected void onPostExecute(List<HydrantHolder> hydrants) {
			// Device.releaseOrientation((Activity) context);

			Log.d(TAG, "Saved " + hydrants.size() + " hydrants");
			mHydrants = hydrants;

			new PrepareMarkerOptionsTask(hydrants).execute();

			super.onPostExecute(hydrants);
		}

	}

	private class PrepareMarkerOptionsTask extends
			AsyncTask<Void, Integer, Map<HydrantType, List<MarkerOptions>>> {
		// Before running code in separate thread
		List<HydrantHolder> mHydrants;

		public PrepareMarkerOptionsTask(List<HydrantHolder> hydrants) {
			this.mHydrants = hydrants;
			mMarkers = new HashMap<HydrantsModule.HydrantType, List<MarkerOptions>>();
		}

		@Override protected void onPreExecute() {

		}

		// The code to be executed in a background thread.
		@Override protected Map<HydrantType, List<MarkerOptions>> doInBackground(
				Void... arg) {

			for (HydrantHolder hydrant : mHydrants) {

				final String hydrant_type = hydrant.getHydrantType();
				final String hydrant_icon_path = hydrant.getIconPath();
				double latitude = hydrant.getLatitude();
				double longitude = hydrant.getLongitude();

				final LatLng position = new LatLng(latitude, longitude);

				final String address = hydrant.getAddress();
				final String addressNumber = hydrant.getAddressNumber();
				final String addressremark = hydrant.getAddressRemark();
				final String remark = hydrant.getRemark();

				// Log.d(TAG, hydrant.toString());

				BitmapDescriptor icon = BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED);

				if (!hydrant_icon_path.isEmpty()) {
					File iconfile = new File(hydrant_icon_path);
					if (iconfile.exists()) {
						BitmapDescriptor loaded_icon = BitmapDescriptorFactory
								.fromPath(hydrant_icon_path);
						if (loaded_icon != null) {
							icon = loaded_icon;
						} else {
							Log.e(TAG, "loaded_icon was null");
						}
					} else {
						Log.e(TAG, "iconfile did not exist: "
								+ hydrant_icon_path);
					}
				} else {
					Log.e(TAG, "iconpath was empty on hydrant type: "
							+ hydrant_type);
				}

				StringBuffer snippet = new StringBuffer();
				if (!address.isEmpty())
					snippet.append("\n" + address + " " + addressNumber);
				if (addressremark.isEmpty())
					snippet.append("\n" + addressremark);
				if (!remark.isEmpty())
					snippet.append("\n" + remark);

				addHydrantMarker(
						hydrant_type,
						new MarkerOptions().position(position)
								.title(hydrant_type)
								.snippet(snippet.toString()).icon(icon));

				// publishProgress(markers.size());
			}
			return mMarkers;
		}

		// Update the progress
		@Override protected void onProgressUpdate(Integer... values) {
		}

		@Override protected void onCancelled() {
		}

		// after executing the code in the thread
		@Override protected void onPostExecute(
				Map<HydrantType, List<MarkerOptions>> markers) {

			Log.d(TAG, "Prepared " + markers.size() + " hydrantMarkerOptions");
			mMarkers = markers;

			loadEnded();

			super.onPostExecute(markers);

		}

	}

	public Set<HydrantType> getHydrantTypes() {
		return new HashSet<HydrantType>(mMarkers.keySet());
	}

	private void addHydrantMarker(String typeName, MarkerOptions marker) {
		HydrantType type = new HydrantType(typeName, marker.getIcon());
		if (mMarkers.containsKey(type)) {
			List<MarkerOptions> markers = mMarkers.get(type);
			markers.add(marker);
		} else {
			List<MarkerOptions> markers = new ArrayList<MarkerOptions>();
			markers.add(marker);
			mMarkers.put(type, markers);
			enableHydrantType(type);
		}
	}

	public class HydrantType {

		private final String name;
		private final BitmapDescriptor icon;

		public HydrantType(String name, BitmapDescriptor icon) {
			this.name = name;
			this.icon = icon;
		}

		public String getName() {
			return name;
		}

		public BitmapDescriptor getIcon() {
			return icon;
		}

		@Override public int hashCode() {
			return name.hashCode();
		}

		@Override public boolean equals(Object o) {
			if (o instanceof HydrantType) {
				if (((HydrantType) o).name.equals(name)) {
					return true;
				}
			}
			return false;
		}

	}

	private List<Marker> getSurroundingMarkers(List<Marker> markers,
			LatLng origin, int maxDistanceMeters) {
		List<Marker> surroundingMarkers = null;

		if (markers != null) {
			surroundingMarkers = new ArrayList<Marker>();
			for (Marker marker : markers) {

				double dist = MapUtils
						.distBetween(origin, marker.getPosition());

				if (dist < modulePreferences.getHydrantsLoadradius()) {
					surroundingMarkers.add(marker);
				}
			}
		}

		return surroundingMarkers;
	}

	private Marker getNearestMarker(List<Marker> markers, LatLng origin) {

		Marker nearestMarker = null;
		double lowestDistance = Double.MAX_VALUE;

		if (markers != null) {

			for (Marker marker : markers) {

				double dist = MapUtils
						.distBetween(origin, marker.getPosition());

				if (dist < lowestDistance) {
					nearestMarker = marker;
				}
			}
		}

		return nearestMarker;
	}

	private List<Marker> selectLowDistanceMarkers(List<Marker> markers,
			int maxDistanceMeters) {

		Map<Marker, Float> longestDist = new HashMap<Marker, Float>();

		for (Marker marker1 : markers) {

			// in this for loop we remember the max distance for each marker
			// think of a map with a flight company's routes from an airport
			// these lines is drawn for each airport
			// marker1 being the airport and marker2 destinations

			for (Marker marker2 : markers) {
				if (!marker1.equals(marker2)) {
					float distance = MapUtils.distBetween(
							marker1.getPosition(), marker2.getPosition());
					if (longestDist.containsKey(marker1)) {
						// possible we have a longer distance
						if (distance > longestDist.get(marker1))
							longestDist.put(marker1, distance);
					} else {
						// first distance
						longestDist.put(marker1, distance);
					}
				}
			}
		}

		List<Marker> acceptedMarkers = new ArrayList<Marker>();

		// examine the distances collected
		for (Marker marker : longestDist.keySet()) {
			if (longestDist.get(marker) <= maxDistanceMeters)
				acceptedMarkers.add(marker);
		}

		return acceptedMarkers;
	}

}
