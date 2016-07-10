package com.turios.activities.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.turios.R;
import com.turios.modules.preferences.ParseCoreModulePreferences;

public class ParseLoginDialogFragment extends DialogFragment {

	private static String USERNAME = "username";
	private static String PASSWORD = "password";

	public interface ParseLoginDialogInterface {

		public void okClicked(String username, String password);

	}

	private static ParseLoginDialogInterface mDialogInterface;

	public static ParseLoginDialogFragment newInstance(
			ParseLoginDialogInterface dialogInterface,
			ParseCoreModulePreferences parsePreferences) {

		mDialogInterface = dialogInterface;

		ParseLoginDialogFragment frag = new ParseLoginDialogFragment();
		Bundle bundle = new Bundle();

		bundle.putString(USERNAME, parsePreferences.getUsername());
		bundle.putString(PASSWORD, parsePreferences.getPassword());

		frag.setCancelable(false);
		frag.setArguments(bundle);

		return frag;
	}

	public ParseLoginDialogFragment() {
		// Empty constructor required for DialogFragment
	}

	private View view;
	private EditText input_username;
	private EditText input_password;

	@Override public Dialog onCreateDialog(Bundle savedInstanceState) {

		LayoutInflater inflater = getActivity().getLayoutInflater();

		view = inflater.inflate(R.layout.dialog_parselogin, null);

		input_username = (EditText) view.findViewById(R.id.username);
		input_password = (EditText) view.findViewById(R.id.password);

		String username = getArguments().getString(USERNAME);
		String password = getArguments().getString(PASSWORD);

		input_username.setText(username);
		input_password.setText(password);

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
				getActivity())
				.setCancelable(false)
				.setView(view)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override public void onClick(
									DialogInterface dialog, int which) {
								mDialogInterface.okClicked(input_username.getText()
										.toString(), input_password.getText()
										.toString());
							}
						});

		return alertBuilder.create();
	}
}
