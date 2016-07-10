package com.turios.activities.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.turios.util.Constants;

public class GenericYesNoDialogFragment extends DialogFragment {

	public interface GenericYesNoDialogInterface {

		public void yesClicked();

		public void noClicked();

	}

	private static GenericYesNoDialogInterface mDialogInterface;

	public static GenericYesNoDialogFragment newInstance(
			GenericYesNoDialogInterface dialogInterface, int title) {
		return GenericYesNoDialogFragment.newInstance(dialogInterface, title,
				-1, -1);
	}

	public static GenericYesNoDialogFragment newInstance(
			GenericYesNoDialogInterface dialogInterface, int title, int message) {
		return GenericYesNoDialogFragment.newInstance(dialogInterface, title,
				message, -1);
	}

	public static GenericYesNoDialogFragment newInstance(
			GenericYesNoDialogInterface dialogInterface, int title,
			int message, int icon) {

		mDialogInterface = dialogInterface;

		GenericYesNoDialogFragment frag = new GenericYesNoDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.EXTRA_TITLE, title);
		bundle.putInt(Constants.EXTRA_MESSAGE, message);
		bundle.putInt(Constants.EXTRA_ICON, icon);

		frag.setCancelable(false);
		frag.setArguments(bundle);

		return frag;
	}

	public static GenericYesNoDialogFragment newInstance(
			GenericYesNoDialogInterface dialogInterface, int title,
			String messageString, int icon) {

		mDialogInterface = dialogInterface;

		GenericYesNoDialogFragment frag = new GenericYesNoDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.EXTRA_TITLE, title);
		bundle.putString(Constants.EXTRA_STRING_MESSAGE, messageString);

		bundle.putInt(Constants.EXTRA_ICON, icon);

		frag.setCancelable(false);
		frag.setArguments(bundle);

		return frag;
	}

	public GenericYesNoDialogFragment() {
		// Empty constructor required for DialogFragment
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		int title = getArguments().getInt(Constants.EXTRA_TITLE);
		int message = getArguments().getInt(Constants.EXTRA_MESSAGE);
		String messageString = getArguments().getString(
				Constants.EXTRA_STRING_MESSAGE);
		int icon = getArguments().getInt(Constants.EXTRA_ICON);

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
				getActivity())
				.setCancelable(true)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mDialogInterface.yesClicked();
							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mDialogInterface.noClicked();
							}
						});

		if (title != -1)
			alertBuilder.setTitle(title);
		if (message != 0) {
			alertBuilder.setMessage(message);
		} else if (messageString != null) {
			alertBuilder.setMessage(messageString);
		}
		if (icon != -1)
			alertBuilder.setIcon(icon);

		return alertBuilder.create();
	}
}
