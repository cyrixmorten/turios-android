package com.turios.activities.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.turios.activities.fragments.dialog.GenericOkDialogFragment.GenericOkDialogInterface;
import com.turios.util.Constants;

public class GenericOkStringMessageDialogFragment extends DialogFragment {

	private static GenericOkDialogInterface mDialogInterface;

	public static GenericOkStringMessageDialogFragment newInstance(
			GenericOkDialogInterface dialogInterface, int title) {
		return GenericOkStringMessageDialogFragment.newInstance(
				dialogInterface, title, "", -1);
	}

	public static GenericOkStringMessageDialogFragment newInstance(
			GenericOkDialogInterface dialogInterface, int title, String message) {
		return GenericOkStringMessageDialogFragment.newInstance(
				dialogInterface, title, message, -1);
	}

	public static GenericOkStringMessageDialogFragment newInstance(
			GenericOkDialogInterface dialogInterface, int title,
			String message, int icon) {

		mDialogInterface = dialogInterface;

		GenericOkStringMessageDialogFragment frag = new GenericOkStringMessageDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.EXTRA_TITLE, title);
		bundle.putString(Constants.EXTRA_MESSAGE, message);
		bundle.putInt(Constants.EXTRA_ICON, icon);

		frag.setCancelable(false);
		frag.setArguments(bundle);

		return frag;
	}

	public GenericOkStringMessageDialogFragment() {
		// Empty constructor required for DialogFragment
	}

	@Override public Dialog onCreateDialog(Bundle savedInstanceState) {

		int title = getArguments().getInt(Constants.EXTRA_TITLE);
		String message = getArguments().getString(Constants.EXTRA_MESSAGE);
		int icon = getArguments().getInt(Constants.EXTRA_ICON);

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
				getActivity()).setCancelable(true).setPositiveButton(
				android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog,
							int which) {
						mDialogInterface.okClicked();
					}
				});

		if (title != -1)
			alertBuilder.setTitle(title);
		alertBuilder.setMessage(message);
		if (icon != -1)
			alertBuilder.setIcon(icon);

		return alertBuilder.create();
	}
}
