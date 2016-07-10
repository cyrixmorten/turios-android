package com.turios.activities.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.turios.util.Constants;

public class GenericEditTextDialogFragment extends DialogFragment {

	public interface GenericEditTextDialogInterface {

		public void okClicked(String editTextString);

	}

	private static GenericEditTextDialogInterface mDialogInterface;

	public static GenericEditTextDialogFragment newInstance(
			GenericEditTextDialogInterface dialogInterface, int title) {
		return GenericEditTextDialogFragment.newInstance(dialogInterface,
				title, -1, -1);
	}

	public static GenericEditTextDialogFragment newInstance(
			GenericEditTextDialogInterface dialogInterface, int title,
			int message) {
		return GenericEditTextDialogFragment.newInstance(dialogInterface,
				title, message, -1);
	}

	public static GenericEditTextDialogFragment newInstance(
			GenericEditTextDialogInterface dialogInterface, int title,
			int message, int icon) {

		mDialogInterface = dialogInterface;

		GenericEditTextDialogFragment frag = new GenericEditTextDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.EXTRA_TITLE, title);
		bundle.putInt(Constants.EXTRA_MESSAGE, message);
		bundle.putInt(Constants.EXTRA_ICON, icon);

		frag.setCancelable(false);
		frag.setArguments(bundle);

		return frag;
	}

	public GenericEditTextDialogFragment() {
		// Empty constructor required for DialogFragment
	}

	private EditText editText;

	@Override public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		editText = new EditText(getActivity());
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override public Dialog onCreateDialog(Bundle savedInstanceState) {

		int title = getArguments().getInt(Constants.EXTRA_TITLE);
		int message = getArguments().getInt(Constants.EXTRA_MESSAGE);
		int icon = getArguments().getInt(Constants.EXTRA_ICON);

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
				getActivity())
				.setCancelable(true)
				.setView(editText)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {
					@Override public void onClick(DialogInterface dialog,
							int which) {

						mDialogInterface.okClicked(editText.getText()
								.toString());
						editText.setText("");
					}
				})
				.setNegativeButton(android.R.string.cancel,
						new OnClickListener() {

							@Override public void onClick(
									DialogInterface dialog, int which) {
								editText.setText("");
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
