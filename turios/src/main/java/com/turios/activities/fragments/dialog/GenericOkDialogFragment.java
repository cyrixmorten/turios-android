package com.turios.activities.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.turios.util.Constants;

public class GenericOkDialogFragment extends DialogFragment {

	public interface GenericOkDialogInterface {

		public void okClicked();

	}

	private static GenericOkDialogInterface mDialogInterface;

	public static GenericOkDialogFragment newInstance(
			GenericOkDialogInterface dialogInterface, int title) {
		return GenericOkDialogFragment.newInstance(dialogInterface, title, -1,
				-1);
	}

	public static GenericOkDialogFragment newInstance(
			GenericOkDialogInterface dialogInterface, int title, int message) {
		return GenericOkDialogFragment.newInstance(dialogInterface, title,
				message, -1);
	}

	public static GenericOkDialogFragment newInstance(
			GenericOkDialogInterface dialogInterface, int title, int message,
			int icon) {

		mDialogInterface = dialogInterface;

		GenericOkDialogFragment frag = new GenericOkDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.EXTRA_TITLE, title);
		bundle.putInt(Constants.EXTRA_MESSAGE, message);
		bundle.putInt(Constants.EXTRA_ICON, icon);

		frag.setCancelable(false);
		frag.setArguments(bundle);

		return frag;
	}

	public GenericOkDialogFragment() {
		// Empty constructor required for DialogFragment
	}

	@Override public Dialog onCreateDialog(Bundle savedInstanceState) {

		int title = getArguments().getInt(Constants.EXTRA_TITLE);
		int message = getArguments().getInt(Constants.EXTRA_MESSAGE);
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
		if (message != -1)
			alertBuilder.setMessage(message);
		if (icon != -1)
			alertBuilder.setIcon(icon);

		return alertBuilder.create();
	}
}
