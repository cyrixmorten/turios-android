package com.turios.activities.listeners;

import javax.inject.Inject;

import com.turios.modules.core.DisplayCoreModule;
import com.turios.modules.core.LocationsCoreModule;
import com.turios.modules.core.LocationsCoreModule.LocationsCoreModuleCallback;

public class TuriosLocationsListener implements
		LocationsCoreModuleCallback {

	private static final String TAG = "LocationsCoreModuleCallbackImpl";

	private final LocationsCoreModule locationService;
	private final DisplayCoreModule display;

	@Inject public TuriosLocationsListener(
			LocationsCoreModule locationService, DisplayCoreModule display) {
		this.locationService = locationService;
		this.display = display;
	}

	@Override public void locationClientConnected() {

		if (display.getPagesCount() > 0) {
			locationService.requestLocationUpdates();
		}

	}

}
