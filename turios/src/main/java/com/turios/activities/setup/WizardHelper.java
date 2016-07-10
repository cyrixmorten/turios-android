package com.turios.activities.setup;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;

public class WizardHelper {

	private static List<Intent> wizardScreens;
	private static int currentScreen;

	private static WizardHelper instance;

	private WizardHelper() {
		wizardScreens = new ArrayList<Intent>();
		currentScreen = -1;
	}

	public static WizardHelper buildWizard() {
		return instance = new WizardHelper();
	}

	public WizardHelper add(Activity activity, Class<?> wizardClass) {
		wizardScreens.add(new Intent(activity, wizardClass)
				.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
		return instance;
	}

	public static Intent getNextScreen() {
		if (instance == null) {
			throw new IllegalStateException(
					"instance was null getNextScreen() WizardHelper");
		}
		currentScreen++;
		return wizardScreens.get(currentScreen);
	}

	public static Intent getPreviousScreen() {
		if (instance == null) {
			throw new IllegalStateException(
					"instance was null getPreviousScreen() WizardHelper");
		}
		currentScreen--;
		return wizardScreens.get(currentScreen);
	}

}
