package com.turios.activities.fragments.dialog;

import java.util.List;

import android.content.Context;

import com.turios.R;
import com.turios.activities.fragments.dialog.GenericEditTextDialogFragment.GenericEditTextDialogInterface;
import com.turios.activities.fragments.dialog.GenericOkDialogFragment.GenericOkDialogInterface;
import com.turios.activities.fragments.dialog.GenericYesNoDialogFragment.GenericYesNoDialogInterface;

public class DialogFragments {

	public static GenericEditTextDialogFragment createProfile(GenericEditTextDialogInterface dialogInterface) {
		return GenericEditTextDialogFragment.newInstance(dialogInterface, R.string.profile_created, -1, android.R.drawable.ic_menu_add);
	}
	
	/** Simple OK dialogs with a message **/
	public static GenericOkDialogFragment needInternetConnection(
			GenericOkDialogInterface dialogInterface) {
		return GenericOkDialogFragment.newInstance(dialogInterface,R.string.missing_internet_connection, R.string.no_internet_connection_try_again, android.R.drawable.ic_dialog_alert);
	}
	
	/** Simple OK dialogs **/
	public static GenericOkDialogFragment trialExpired(
			GenericOkDialogInterface dialogInterface) {
		return GenericOkDialogFragment.newInstance(dialogInterface,
				R.string.trial_expired, -1, android.R.drawable.ic_lock_lock);
	}

	public static GenericOkDialogFragment missingSD(
			GenericOkDialogInterface dialogInterface) {
		return GenericOkDialogFragment.newInstance(dialogInterface,
				R.string.missingsd, R.string.no_support_for_external_sd,
				android.R.drawable.ic_dialog_alert);
	}


	/** Yes/No dialogs **/
	public static GenericYesNoDialogFragment deletePage(
			GenericYesNoDialogInterface dialogInterface) {
		return GenericYesNoDialogFragment.newInstance(dialogInterface,
				R.string.delete_page, R.string.confirm_delete_page,
				android.R.drawable.ic_menu_delete);
	}

	public static GenericYesNoDialogFragment loadDropboxSettings(
			GenericYesNoDialogInterface dialogInterface) {
		return GenericYesNoDialogFragment.newInstance(dialogInterface,
				R.string.preferences_file_found,
				R.string.preferences_file_found_dropbox,
				android.R.drawable.ic_menu_preferences);
	}

	public static GenericYesNoDialogFragment missingGoogleAccount(
			GenericYesNoDialogInterface dialogInterface) {
		return GenericYesNoDialogFragment.newInstance(dialogInterface,
				R.string.google_account,
				R.string.google_maps_missing_google_account,
				android.R.drawable.ic_dialog_alert);
	}

	public static GenericYesNoDialogFragment newTurnout(
			GenericYesNoDialogInterface dialogInterface, List<String> allEntries, Context context) {
		return GenericYesNoDialogFragment.newInstance(
				dialogInterface, R.string.new_turnout,
				flattenEntries(allEntries) + "\n" + context.getString(R.string.go_to_information), android.R.drawable.ic_dialog_alert);
	}

	public static GenericYesNoDialogFragment turnoutAddition(
			GenericYesNoDialogInterface dialogInterface, List<String> allEntries, Context context) {
		return GenericYesNoDialogFragment.newInstance(
				dialogInterface, R.string.turnout_addition,
				flattenEntries(allEntries) + "\n" + context.getString(R.string.go_to_information), android.R.drawable.ic_dialog_alert);
	}

	private static String flattenEntries(List<String> entries) {
		StringBuffer b = new StringBuffer();
		for (String entry : entries) {
			b.append(entry);
			b.append("\n");
		}
		return b.toString();
	}
}
