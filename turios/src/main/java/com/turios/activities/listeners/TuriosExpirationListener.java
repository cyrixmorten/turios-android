package com.turios.activities.listeners;

import javax.inject.Inject;

import android.support.v4.app.DialogFragment;

import com.turios.activities.fragments.dialog.DialogFragments;
import com.turios.activities.fragments.dialog.GenericOkDialogFragment.GenericOkDialogInterface;
import com.turios.modules.core.ExpirationCoreModule.ExpirationCoreModuleCallback;
import com.turios.modules.extend.BasisModule;

public class TuriosExpirationListener implements
		ExpirationCoreModuleCallback {

	private final TuriosUICallback mTuriosUICallback;
	private final BasisModule mBasisModule;

	@Inject public TuriosExpirationListener(
			TuriosUICallback mTuriosUICallback, BasisModule mBasisModule) {
		super();
		this.mTuriosUICallback = mTuriosUICallback;
		this.mBasisModule = mBasisModule;
	}

	@Override public void expiresIn(int days) {

		if (days == 0 && !mBasisModule.isPaid()) {
			DialogFragment expiredDialog = DialogFragments.trialExpired(new GenericOkDialogInterface() {

				@Override
				public void okClicked() {

				}
			});
			mTuriosUICallback.addDialog(expiredDialog);
		}

	}
}
