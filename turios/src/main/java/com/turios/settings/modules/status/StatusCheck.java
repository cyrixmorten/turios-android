package com.turios.settings.modules.status;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.turios.R;
import com.turios.modules.ModuleLoadCallback;
import com.turios.modules.extend.StandardModule;

public class StatusCheck {

	private static final String TAG = "StatusCheck";

	public static final int STATUS_READY = 1;
	public static final int STATUS_CHECKING = 2;
	public static final int STATUS_ERROR = 3;

	private List<Preference> mStatusPrefs;
	private List<Preference> mWarningPrefs;
	private List<Preference> mFatalErrorPrefs;

	private StandardModule module;
	private CheckStrategy strategy;

	private PreferenceCategory statusCat;
	private CheckBoxPreference statusCheckbox;

	private final Context context;

	public StatusCheck(Context context, StandardModule module,
			PreferenceFragment prefsFrag) {
		this.context = context;
		this.module = module;
		this.strategy = module.getCheckStrategy();
		this.statusCat = (PreferenceCategory) prefsFrag.findPreference(context
				.getString(R.string.status_category));
		this.statusCheckbox = (CheckBoxPreference) prefsFrag
				.findPreference(context.getString(R.string.status_checkbox));

		this.mFatalErrorPrefs = new ArrayList<Preference>();
		this.mWarningPrefs = new ArrayList<Preference>();
		this.mStatusPrefs = new ArrayList<Preference>();

	}

	public boolean runCheck() {

		statusCat.setOrderingAsAdded(true);

		updateStatus(statusCheckbox, STATUS_CHECKING);

		checkErrors();
		if (mFatalErrorPrefs.isEmpty()) {
			checkWarnings();
			checkStatus();
		}
		return finish();
	}

	public void updateStatus(final CheckBoxPreference statusPref, int status) {
		switch (status) {
		case STATUS_READY:
			statusPref.setEnabled(true);
			statusPref.setChecked(true);
			module.setModuleLoadCallback(new ModuleLoadCallback() {

				@Override public void startedLoadingModule(String moduleName) {
					statusPref.setTitle(context.getString(R.string.loading));
					statusPref.setSummary("");
				}

				@Override public void doneLoadingModule(String moduleName) {
					statusPref.setTitle(context.getString(R.string.ready));
					statusPref.setSummary(context
							.getString(R.string.module_is_ready));
					module.setModuleLoadCallback(null);
				}
			});
			module.loadIfEnabled();
			break;
		case STATUS_CHECKING:
			statusPref.setEnabled(false);
			statusPref.setChecked(false);
			statusPref.setTitle(context.getString(R.string.checking));
			statusPref.setSummary(context.getString(R.string.please_wait));
			break;
		case STATUS_ERROR:
			statusPref.setTitle(context.getString(R.string.error));
			statusPref.setEnabled(false);
			statusPref
					.setSummary(context.getString(R.string.see_details_below));
			break;

		default:
			break;
		}
	}

	public void clear() {
		// remove previous notifications
		for (int prefIndex = (statusCat.getPreferenceCount() - 1); prefIndex > 1; prefIndex--) {
			statusCat.removePreference(statusCat.getPreference(prefIndex));
		}
	}

	private void checkStatus() {
		if (mStatusPrefs != null)
			mStatusPrefs.clear();
		List<Preference> status = strategy.checkStatus();

		if (status != null && !status.isEmpty()) {
			Log.d(TAG, "Status: " + status.size());
			PreferenceCategory statusHeader = new PreferenceCategory(context);
			statusHeader.setTitle("Status rapport");
			// status.add(statusHeader);
			mStatusPrefs.addAll(status);
		}

	}

	private void checkWarnings() {
		if (mWarningPrefs != null)
			mWarningPrefs.clear();
		List<Preference> warnings = strategy.checkWarnings();
		if (warnings != null && !warnings.isEmpty()) {
			Log.d(TAG, "Warnings: " + warnings.size());
			PreferenceCategory statusHeader = new PreferenceCategory(context);
			statusHeader.setTitle("Advarsels rapport");
			// warnings.add(statusHeader);
			mWarningPrefs.addAll(warnings);
		}

	}

	private void checkErrors() {
		if (mFatalErrorPrefs != null)
			mFatalErrorPrefs.clear();
		List<Preference> errors = strategy.checkFatalErrors();
		if (errors != null && !errors.isEmpty()) {
			Log.d(TAG, "Errors: " + errors.size());
			PreferenceCategory statusHeader = new PreferenceCategory(context);
			statusHeader.setTitle("Fejl rapport");
			// errors.add(statusHeader);
			mFatalErrorPrefs.addAll(errors);
		}
	}

	private boolean finish() {
		clear();

		if (mFatalErrorPrefs.isEmpty()) {
			// no errors so we can use some space on module status information
			addPreferences(mStatusPrefs);
			addPreferences(mWarningPrefs);
			updateStatus(statusCheckbox, STATUS_READY);
			return true;
		} else {
			addPreferences(mFatalErrorPrefs);
			updateStatus(statusCheckbox, STATUS_ERROR);
			return false;
		}
	}

	private void addPreferences(List<Preference> prefs) {
		for (Preference pref : prefs) {
			if (pref != null) {
				statusCat.addPreference(pref);
			}
		}
	}
}
