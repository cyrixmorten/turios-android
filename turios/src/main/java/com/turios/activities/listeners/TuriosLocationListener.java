package com.turios.activities.listeners;

import javax.inject.Inject;

import android.location.Location;

public class TuriosLocationListener implements
		com.google.android.gms.location.LocationListener {

	@Inject public TuriosLocationListener() {
	}

	@Override public void onLocationChanged(Location location) {

		// ModulesAggregator.getLocationsCoreModule().setLastLocation(location);
		//
		// AddressHolder addressHolder =
		// activity.get().getSelectedAddressHolder();
		//
		// final HydrantsModule hydrantsModule =
		// ModulesAggregator.getHydrantsModule();
		//
		// if (Device.isMultiPane(activity.get()) && addressHolder != null
		// && addressHolder.position != null
		// &&
		// !ModulesAggregator.getHydrantsModule().isNearbyHydrantsAdded()) {
		// int loadRadius = ModulesAggregator.getHydrantsModule()
		// .getHydrantsLoadradius();
		// float distanceToDistination = MapUtils.distBetween(new LatLng(
		// location.getLatitude(), location.getLongitude()),
		// addressHolder.position);
		// if (distanceToDistination <= loadRadius) {
		// final GoogleMapsModule googleMapsModule =
		// ModulesAggregator.getGoogleMapsModule();
		// final DisplayCoreModule displayCoreModule =
		// ModulesAggregator.getDisplayCoreModule();
		// final AddressHolder addressHolder =
		// displayCoreModule.getAddressHolder(mPageid);
		// final Location lastlocation =
		// ModulesAggregator.getLocationsCoreModule().getLastLocation();
		// hydrantsModule.addHydrantsNearAddress(googleMapsModule,
		// addressHolder, lastlocation, true,
		// activity.get().getSupportFragmentManager());
		// }
		// }
	}

}
