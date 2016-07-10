package com.turios.modules.extend;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.w3c.dom.Document;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.GoogleMap.OnCameraChangeListener;
import com.androidmapsextensions.PolylineOptions;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.turios.R;
import com.turios.dagger.quialifiers.ForApplication;
import com.turios.modules.ModulePreferences;
import com.turios.modules.core.ExpirationCoreModule;
import com.turios.modules.core.LocationsCoreModule;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.data.AddressHolder;
import com.turios.modules.extend.GoogleMapsModule.GoogleMapsCallback;
import com.turios.modules.preferences.DirectionsModulePreferences;
import com.turios.modules.utils.GMapV2Direction;
import com.turios.persistence.Preferences;

@Singleton
public class DirectionsModule extends StandardModule {

	private final DirectionsModulePreferences modulePreferences;

	public DirectionsModulePreferences getPreferences() {
		return modulePreferences;
	}

	private static final String TAG = "DirectionsModule";

	private final LocationsCoreModule location;
	private final GoogleMapsModule googlemaps;
	private final HydrantsModule hydrants;

	@Inject public DirectionsModule(@ForApplication Context context,
			Preferences preferences, ExpirationCoreModule expiration,
			ParseCoreModule parse, LocationsCoreModule location,
			GoogleMapsModule googlemaps, HydrantsModule hydrants) {
		super(context, preferences, expiration, parse, MODULES.DIRECTIONS);

		Log.i(TAG, "CREATING MODULE " + TAG);

		this.location = location;
		this.googlemaps = googlemaps;
		this.hydrants = hydrants;
		this.modulePreferences = new DirectionsModulePreferences(preferences);
	}

	public void updateDirections(AddressHolder addressHolder) {
		// only ever called from inside a map, so map will be non-null
		addDirectionsToMap(googlemaps.getMap(), addressHolder, false);
	}

	public void addDirectionsToMap(final AddressHolder addressHolder) {

		GoogleMap map = googlemaps.getMap();

		hydrants.disableAllHydrants();

		if (map == null) {
			// wait for the map to be ready before adding hydrants
			googlemaps.setGoogleMapsCallback(new GoogleMapsCallback() {

				@Override public void mapReady(GoogleMap map) {

					addDirectionsToMap(map, addressHolder, true);

					googlemaps.setGoogleMapsCallback(null); // unregister
				}
			});
		} else {

			googlemaps.clearMap();

			addDirectionsToMap(map, addressHolder, true);

		}

	}

	private void addDirectionsToMap(GoogleMap map, AddressHolder addressHolder,
			boolean zoomToBounds) {

		Log.i(TAG, "addDirectionsToMap");

		Location lastLocation = location.getLastLocation();

		if (lastLocation == null) {
			// TODO res/string
			Toast.makeText(context, "Kunne ikke bestemme enhedens position",
					Toast.LENGTH_LONG).show();
			return;
		}

		map.setTrafficEnabled(getPreferences().showTraffic());
		map.setMapType(getPreferences().getMapType());

		googlemaps.addLocationPin(lastLocation);
		googlemaps.addAddressPin(addressHolder);

		new GetDirectionsTask(zoomToBounds).execute(
				new LatLng(lastLocation.getLatitude(), lastLocation
						.getLongitude()), addressHolder.position);

	}

	private class GetDirectionsTask extends
			AsyncTask<LatLng, Integer, List<LatLng>> {

		private boolean zoomToBounds;

		public GetDirectionsTask(boolean zoomToBounds) {
			this.zoomToBounds = zoomToBounds;
		}

		// Before running code in separate thread
		@Override protected void onPreExecute() {
			// Create a new progress dialog.
			// progressDialog = new ProgressDialog(context);
			// progressDialog.setMessage("Henter rute...");
			// progressDialog.setCancelable(false);
			// progressDialog.setIndeterminate(true);
			// progressDialog.show();

		}

		// The code to be executed in a background thread.
		@Override protected List<LatLng> doInBackground(LatLng... position) {
			LatLng fromPosition = position[0];
			LatLng toPosition = position[1];

			GMapV2Direction md = new GMapV2Direction();

			List<LatLng> directionPoints = null;
			Document doc;
			try {
				doc = md.getDocument(fromPosition, toPosition,
						GMapV2Direction.MODE_DRIVING);

				if (doc != null) {
					directionPoints = md.getDirection(doc);
				} else {
					Log.d(TAG, "Directions document was null");
				}
			} catch (InterruptedException e) {
				Log.e(TAG, e.getMessage(), e);
			} catch (ExecutionException e) {
				Log.e(TAG, e.getMessage(), e);
			} catch (TimeoutException e) {
				Log.e(TAG, e.getMessage(), e);
			}
			return directionPoints;
		}

		// Update the progress
		@Override protected void onProgressUpdate(Integer... values) {
			// set the current progress of the progress dialog
			// progressDialog.setProgress(values[0]);
		}

		// after executing the code in the thread
		@Override protected void onPostExecute(List<LatLng> directionPoint) {
			// close the progress dialog
			// progressDialog.dismiss();
			final GoogleMap map = googlemaps.getMap();

			if (directionPoint == null || directionPoint.size() == 0)
				return;
			if (map == null)
				return;

			PolylineOptions rectLine = new PolylineOptions().width(3).color(
					Color.RED);

			LatLngBounds.Builder builder = new LatLngBounds.Builder();

			for (int i = 0; i < directionPoint.size(); i++) {
				rectLine.add(directionPoint.get(i));
				builder.include(directionPoint.get(i));
			}

			map.addPolyline(rectLine);

			final LatLngBounds bounds = builder.build();

			final int padding = 50; // offset from edges of the
			// map in pixels

			if (zoomToBounds) {
				try {
					googlemaps.zoomWithBounds(bounds, padding);
				} catch (Exception e) {

					// if map is just created we have to wait for it to be ready

					map.setOnCameraChangeListener(new OnCameraChangeListener() {

						@Override public void onCameraChange(CameraPosition arg0) {
							// Move camera.

							googlemaps.zoomWithBounds(bounds, padding);

							// Remove listener to prevent position reset on
							// camera move.
							map.setOnCameraChangeListener(null);

						}
					});
				}
			}

		}
	}

}
