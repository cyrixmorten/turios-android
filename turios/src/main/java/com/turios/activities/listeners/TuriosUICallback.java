package com.turios.activities.listeners;

import java.util.List;

import android.support.v4.app.DialogFragment;

import com.turios.activities.fragments.DisplayFragment;

public interface TuriosUICallback {

	public void invalidateOptionsMenu();
	
	/**
	 * 
	 * Shows a dialog fragment, if the activity is paused, then the fragment is pending till resumed
	 * There can only be one pending dialog at a time
	 * 
	 * @param dialog
	 */
	public void addDialog(DialogFragment dialog);

	public DisplayFragment getSelectedDisplayFragment();

	public void showPage(int page);
	
	public void addPage(int page);

	public void pageUpdated(int page, List<String> entries);

	public void refreshDisplayFragment();

	public void pageDeleted(int page);

//	public void showExpiredDialog();

}