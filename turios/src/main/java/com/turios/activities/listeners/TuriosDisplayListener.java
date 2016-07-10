package com.turios.activities.listeners;

import java.util.List;

import javax.inject.Inject;

import android.app.ActionBar;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.turios.activities.fragments.DisplayFragment;
import com.turios.activities.fragments.dialog.DialogFragments;
import com.turios.activities.fragments.dialog.GenericOkDialogFragment.GenericOkDialogInterface;
import com.turios.activities.fragments.dialog.GenericYesNoDialogFragment.GenericYesNoDialogInterface;
import com.turios.activities.util.WakeScreen;
import com.turios.dagger.quialifiers.ForApplication;
import com.turios.modules.core.DisplayCoreModule;
import com.turios.modules.core.DisplayCoreModule.DisplayCoreModuleCallback;
import com.turios.modules.core.LocationsCoreModule;

public class TuriosDisplayListener implements DisplayCoreModuleCallback {

	private static final String TAG = "DisplayCoreModuleCallbackImpl";

	private final Context context;
	private final LocationsCoreModule mLocation;
	private final WakeScreen mDeviceUtil;
	private final ActionBar mActionBar;
	private final FragmentManager fm;
	private final DisplayCoreModule display;

	private final TuriosUICallback mTuriosUICallback;

	@Inject public TuriosDisplayListener(@ForApplication Context context, TuriosUICallback turiosUICallback, DisplayCoreModule display,
			LocationsCoreModule location, WakeScreen deviceUtil,
			ActionBar actionBar, FragmentManager fm) {
		this.context = context;
		this.mTuriosUICallback = turiosUICallback;
		this.display = display;
		this.mLocation = location;
		this.mDeviceUtil = deviceUtil;
		this.mActionBar = actionBar;
		this.fm = fm;
	}

	private boolean shouldShowDialog() {
		return mActionBar.getNavigationMode() != ActionBar.NAVIGATION_MODE_STANDARD
				&& mActionBar.getSelectedNavigationIndex() != DisplayFragment.NAVIGATION_INDEX;
	}

	@Override public void pageAdded(final int page, List<String> allEntries) {
		// if (TuriosApplication().isTuriosDestroyed()) {
		Log.d(TAG, "pageAdded " + page);
		mDeviceUtil.screenWakeup(true);

		mLocation.requestLocationUpdates();

		if (!shouldShowDialog()) {

			mTuriosUICallback.addPage(page);
			if (display.getPagesCount() == 1) mTuriosUICallback.invalidateOptionsMenu();

		} else {
			
			DialogFragment newTurnoutDialog = DialogFragments.newTurnout(new GenericYesNoDialogInterface() {
				
				@Override
				public void yesClicked() {
					mTuriosUICallback.showPage(page);
				}
				
				@Override
				public void noClicked() {
					// TODO Auto-generated method stub
					
				}
			}, allEntries, context);
			
			mTuriosUICallback.addDialog(newTurnoutDialog);
		}
		// }
	}

	@Override public void pageUpdated(final int page,
			final List<String> newEntries) {
		// if (TuriosApplication().isTuriosDestroyed()) {
		Log.d(TAG, "pageUpdated " + page);

		mDeviceUtil.screenWakeup(true);

		mTuriosUICallback.pageUpdated(page, newEntries);

		if (shouldShowDialog()) {
			DialogFragment turnoutAdditionDialog = DialogFragments.turnoutAddition(new GenericYesNoDialogInterface() {
				
				@Override
				public void yesClicked() {
					mTuriosUICallback.showPage(page);
				}
				
				@Override
				public void noClicked() {
					// TODO Auto-generated method stub
					
				}
			}, newEntries, context);
			
			mTuriosUICallback.addDialog(turnoutAdditionDialog);
		}
	}

	@Override public void pageDeleted(int page) {
		mTuriosUICallback.pageDeleted(page);
	}

}
