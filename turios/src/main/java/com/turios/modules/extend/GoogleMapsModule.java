package com.turios.modules.extend;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.turios.dagger.quialifiers.ForApplication;
import com.turios.modules.core.ExpirationCoreModule;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.data.AddressHolder;
import com.turios.modules.preferences.GoogleMapsModulePreferences;
import com.turios.persistence.Preferences;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GoogleMapsModule extends StandardModule {

	private final GoogleMapsModulePreferences modulePreferences;

	public GoogleMapsModulePreferences getPreferences() {
		return modulePreferences;
	}

	public interface GoogleMapsCallback {
		public void mapReady(GoogleMap map);
	}

	public interface FetchAddressCallback {
		public void addressFound(AddressHolder addressHolder);

		public void addressLookupFailed();
	}

	private static final String TAG = "GoogleMapsModule";

	public static final int MAPTYPE_NORMAL = 0;
	public static final int MAPTYPE_SATELITE = 1;
	public static final int MAPTYPE_TERRAIN = 2;
	public static final int MAPTYPE_HYBRID = 3;

	private AddressHolder addressPin;

	private GoogleMap mGoogleMap;
	private Geocoder geocoder;

	private GoogleMapsCallback googleMapsCallback;

	@Inject public GoogleMapsModule(@ForApplication Context context,
			Preferences preferences, ExpirationCoreModule expiration,
			ParseCoreModule parse) {
		super(context, preferences, expiration, parse, MODULES.GOOGLEMAPS);

		this.modulePreferences = new GoogleMapsModulePreferences(preferences);

		Log.i(TAG, "CREATING MODULE " + TAG);

		geocoder = new Geocoder(context, new Locale("da_DK"));
	}

	@Override public void load() {

		MapsInitializer.initialize(context);
		loadEnded();

	}

	public void setGoogleMapsCallback(GoogleMapsCallback mapReadyCallback) {
		this.googleMapsCallback = mapReadyCallback;
	}

	public void destroyMap() {
		mGoogleMap = null;
	};

	public void clearMap() {
		clearMap(false);
	}

	public void clearMap(boolean keepAddressPin) {
		this.googleMapsCallback = null;

		if (mGoogleMap != null) {
			mGoogleMap.clear();

			if (keepAddressPin && addressPin != null) {
				addAddressPin(addressPin);
			}
		}
	}

	public void setMap(GoogleMap map) {
		Log.d(TAG, "setMap: " + (map != null));
		if (mGoogleMap == null) {
			mGoogleMap = map;
			mGoogleMap.setMyLocationEnabled(true);
			mGoogleMap.setTrafficEnabled(true);
			mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}

		Log.d(TAG, "googleMapsCallback: " + (googleMapsCallback != null));
		if (googleMapsCallback != null) {
			googleMapsCallback.mapReady(mGoogleMap);
		}
	}

	public GoogleMap getMap() {
		return mGoogleMap;
	}

	public void setMapType(int maptype) {
		if (mGoogleMap != null) {
			mGoogleMap.setMapType(maptype);
		}
	}

	public String mapType(int maptype) {
		switch (maptype) {
		case MAPTYPE_NORMAL:

			return "Normal";
		case MAPTYPE_SATELITE:

			return "Satelite";
		case MAPTYPE_TERRAIN:

			return "Terrain";
		case MAPTYPE_HYBRID:

			return "Hybrid";

		default:
			return "Normal";
		}
	}

	public void zoomWithBounds(LatLngBounds bounds, int padding)
			throws IllegalStateException {
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

		getMap().animateCamera(cu);

	}

	public void addLocationPin(Location location) {
		mGoogleMap.addMarker(new MarkerOptions().position(
				new LatLng(location.getLatitude(), location.getLongitude()))
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
	}

	public void addAddressPin(AddressHolder addressHolder) {
		if (addressHolder.position != null) {

			String city = addressHolder.city;
			String address = addressHolder.address;

			// address or city or empty
			String title = (address != null && !address.isEmpty()) ? address
					: ((city != null) ? city : "");

			MarkerOptions markerOptions = new MarkerOptions()
					.position(addressHolder.position)
					.title(title)
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

			Marker destMarker = mGoogleMap.addMarker(markerOptions);

			destMarker.showInfoWindow();

			addressPin = addressHolder;

		}
	}

	public void moveTo(AddressHolder addressHolder) {

		GoogleMap map = getMap();

		CameraUpdate center = CameraUpdateFactory
				.newLatLng(addressHolder.position);

		CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

		map.moveCamera(center);
		map.animateCamera(zoom);
	}

	public void zoomTo(Location location) {
		if (location == null) {
			return;
		}

		zoomTo(new LatLng(location.getLatitude(), location.getLongitude()));
	}

	public void zoomTo(AddressHolder addressHolder) {
		if (getMap() == null || addressHolder.position == null) {
			Log.e(TAG, "zoomTo map or address position was null: map "
					+ (getMap() == null) + " address position "
					+ (addressHolder.position == null));
			return;
		}

		addAddressPin(addressHolder);

		// Zoom in, animating the camera.
		zoomTo(addressHolder.position);

	}

	private void zoomTo(LatLng latlng) {

		GoogleMap map = getMap();

		if (getMap() == null || latlng == null) {
			return;
		}

		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16));
	}

	public void lookupAddress(final FetchAddressCallback fetchAddressCallback,
			String address, String city) {

		new GetAddressPositionTask(fetchAddressCallback, address, city)
				.execute();
	}

	public void lookupAddress(final FetchAddressCallback fetchAddressCallback,
			String searchString) {

		new GetAddressPositionTask(fetchAddressCallback, searchString)
				.execute();
	}

	private class GetAddressPositionTask extends
			AsyncTask<String, Integer, AddressHolder> {

		private FetchAddressCallback fetchAddressPositionCallback;

		private String searchString;

		private String city = "";
		private String address = "";

		public GetAddressPositionTask(
				FetchAddressCallback fetchAddressPositionCallback,
				String address, String city) {
			this.fetchAddressPositionCallback = fetchAddressPositionCallback;

			this.city = city;
			this.address = address;
			this.searchString = address + ", " + city;
		}

		public GetAddressPositionTask(
				FetchAddressCallback fetchAddressPositionCallback,
				String searchString) {
			this.fetchAddressPositionCallback = fetchAddressPositionCallback;

			this.searchString = searchString;
		}

		@Override protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override protected AddressHolder doInBackground(String... params) {

			final String lookupStringUriencoded = Uri.encode(searchString);

			LatLng position = null;

			try {
				if (geocoder != null) {
					List<Address> addresses = geocoder.getFromLocationName(
							searchString, 1);
					if (addresses != null && !addresses.isEmpty()) {
						Address first_address = addresses.get(0);

						String foundCity = first_address.getLocality();

						if (foundCity != null) {
							city = (city.isEmpty()) ? foundCity : city;
						}

						String addressName = first_address.getThoroughfare();
						String streetNumber = first_address
								.getSubThoroughfare();

						// if (addressName != null && address.isEmpty()) {
						address = (streetNumber != null) ? addressName + " "
								+ streetNumber : addressName;
						// }
						position = new LatLng(first_address.getLatitude(),
								first_address.getLongitude());
						Log.d(TAG, "geocoder was found " + position);
					}
				} else {
					Log.e(TAG, "geocoder was null, is the module loaded?");
				}

			} catch (IOException e) {
				// Log.e(TAG, "geocoder failed, moving on to HTTP");
			}
			// try HTTP lookup to the maps API
			if (position == null) {
				HttpGet httpGet = new HttpGet(
						"http://maps.google.com/maps/api/geocode/json?address="
								+ lookupStringUriencoded + "&sensor=true");
				HttpClient client = new DefaultHttpClient();
				HttpResponse response;
				StringBuilder stringBuilder = new StringBuilder();

				try {
					response = client.execute(httpGet);
					HttpEntity entity = response.getEntity();
					InputStream stream = entity.getContent();
					int b;
					while ((b = stream.read()) != -1) {
						stringBuilder.append((char) b);
					}
				} catch (ClientProtocolException e) {
					Log.e(TAG, e.getMessage(), e);
				} catch (IOException e) {
					Log.e(TAG, e.getMessage(), e);
				}

				JSONObject jsonObject = new JSONObject();
				try {
					// Log.d("MAPSAPI", stringBuilder.toString());

					jsonObject = new JSONObject(stringBuilder.toString());
					if (jsonObject.getString("status").equals("OK")) {
						jsonObject = jsonObject.getJSONArray("results")
								.getJSONObject(0);

						JSONArray address_components = jsonObject
								.getJSONArray("address_components");

						String jsonCity = "";
						String jsonAddress = "";
						String jsonStreetNumber = "";

						// extract looked up address information
						for (int i = 0; i < address_components.length(); i++) {
							JSONObject address_component = address_components
									.getJSONObject(i);
							String type = address_component.getJSONArray(
									"types").getString(0);

							String value = address_component
									.getString("long_name");

							if (type.equals("locality")) {
								jsonCity = value;
							}

							if (type.equals("route")) {
								jsonAddress = value;
							}

							if (type.equals("street_number")) {
								jsonStreetNumber = value;
							}

						}

						Log.d("MAPSAPI", jsonCity + "," + jsonAddress + " "
								+ jsonStreetNumber);

						city = (city.isEmpty()) ? jsonCity : city;

						address = (address.isEmpty()) ? (jsonAddress + " " + jsonStreetNumber)
								.trim() : address;

						// extract position
						jsonObject = jsonObject.getJSONObject("geometry");
						jsonObject = jsonObject.getJSONObject("location");
						String lat = jsonObject.getString("lat");
						String lng = jsonObject.getString("lng");

						Log.d("MAPSAPI", "latlng " + lat + ", " + lng);

						position = new LatLng(Double.valueOf(lat),
								Double.valueOf(lng));
					}

				} catch (JSONException e) {
					Log.e(TAG, e.getMessage(), e);
				}

			}
			return new AddressHolder(address, city, position);
		}

		@Override protected void onPostExecute(final AddressHolder result) {
			Log.d(TAG, "GetAddressPositionTask " + result);
			if (result.position != null) {
				fetchAddressPositionCallback.addressFound(result);
			} else {
				fetchAddressPositionCallback.addressLookupFailed();
			}
			// ensure no more callbacks to this
			fetchAddressPositionCallback = null;
			super.onPostExecute(result);
		}

	}

}
