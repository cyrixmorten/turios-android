package com.turios.modules.core;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.turios.dagger.quialifiers.ForApplication;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LocationsCoreModule implements
		GoogleApiClient.ConnectionCallbacks {

	private static final String TAG = "LocationsCoreModule";


    public interface LocationsCoreModuleCallback {
		public void locationClientConnected();
	}

	private LocationsCoreModuleCallback locationsCoreModuleCallback;

	private com.google.android.gms.location.LocationListener locationListener;
	
	private Location lastLocation;
    private GoogleApiClient googleApiClient;

	private final LocationRequest mLocationRequest;
	private final Context context;
	

	@Inject 
	public LocationsCoreModule(@ForApplication Context context, LocationRequest locationRequest) {
		this.context = context;
		this.mLocationRequest = locationRequest;
	}

	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}

	public void setLocationsListener(
			LocationsCoreModuleCallback locationsCoreModuleCallback) {
		this.locationsCoreModuleCallback = locationsCoreModuleCallback;
	}

	public void start(
			com.google.android.gms.location.LocationListener locationListener,
            GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
		this.locationListener = locationListener;
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(connectionFailedListener)
                .build();
        googleApiClient.connect();

	}

	public void stop() {
		if (googleApiClient != null) {
			// If the client is connected
			stopLoactionUpdates();

			// Disconnecting the client invalidates it.
            googleApiClient.disconnect();
		}
	}

//	public void setMockMode(boolean mockMode) {
//		if (googleApiClient.isConnected())
//            googleApiClient.setMockMode(mockMode);
//	}
//
//	public void setMockLocation(LatLng location) {
//		if (!mLocationClient.isConnected())
//			return;
//
//		mLocationClient.setMockMode(true);
//
//		Location loc = new Location("Test");
//		loc.setLatitude(location.latitude);
//		loc.setLongitude(location.longitude);
//		loc.setAltitude(0);
//		loc.setTime(System.currentTimeMillis());
//		loc.setAccuracy(3.0f);
//
//		mLocationClient.setMockLocation(loc);
//	}

	public boolean isConnected() {
		if (googleApiClient == null) return false;
		return googleApiClient.isConnected();
	}

	public Location getLastLocation() {

        return lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

	}
	
	public LatLng getLastLocationLatLng() {

        lastLocation = getLastLocation();

		if (lastLocation == null) {
			return null;
		}

		double lat = lastLocation.getLatitude();
		double lng = lastLocation.getLongitude();
		
		return new LatLng(lat, lng);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d(TAG, "GooglePlayServices connected!");

		Location lastLocation = getLastLocation();

		if (lastLocation == null)
			Log.e(TAG, "Lastlocation is null even after connected!!!!");

		if (locationsCoreModuleCallback != null) {
			locationsCoreModuleCallback.locationClientConnected();
			locationsCoreModuleCallback = null; // single shot
		}

	}

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void requestLocationUpdates() {
		if (googleApiClient != null && googleApiClient.isConnected()) {
			Log.d(TAG, "Requesting location updates");
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this.locationListener);
		}
	}

	public void stopLoactionUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
		}
	}


}
