package com.turios.settings.global;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.inject.Inject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.turios.R;
import com.turios.activities.util.InstallLauncher;
import com.turios.dagger.DaggerPreferenceFragment;
import com.turios.dagger.quialifiers.ForActivity;
import com.turios.util.Constants;
import com.turios.util.Device;

public class UpdateSettings extends DaggerPreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private static final String TAG = "PicklistSettings";

	private CheckBoxPreference updateCheckbox;
	private EditTextPreference updateInfoEdit;

	@Inject Device device;
	@Inject InstallLauncher installer;

	@Inject @ForActivity Context context;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.settings_update);
	}

	@Override public void onStart() {
		updateInfoEdit = (EditTextPreference) getPreferenceScreen()
				.findPreference(getString(R.string.key_update_info));
		updateCheckbox = (CheckBoxPreference) getPreferenceScreen()
				.findPreference(getString(R.string.key_update));

		updateInfoEdit.setTitle(context.getString(R.string.current_version)
				+ ": " + device.getVersionName());
		updateInfoEdit.setSummary("");
		updateInfoEdit.setEnabled(false);
		updateCheckbox.setChecked(false);
		super.onStart();
	}

	@Override public void onResume() {
		PreferenceManager.getDefaultSharedPreferences(context)
				.registerOnSharedPreferenceChangeListener(this);

		super.onResume();
	}

	@Override public void onPause() {
		PreferenceManager.getDefaultSharedPreferences(context)
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	@Override public void onSharedPreferenceChanged(SharedPreferences prefs,
			String key) {
		if (key.equals(getString(R.string.key_update))) {
			boolean value = prefs.getBoolean(key, true);

			if (value == true) {
				new FetchLatestVersion().execute();
			}
		}
	}

	private void makeToast(final String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	private class FetchLatestVersion extends AsyncTask<Void, Void, Integer> {

		private ProgressDialog working_dialog;

		private String errorMsg = "";

		@Override protected void onPreExecute() {
			working_dialog = ProgressDialog.show(context, "",
					getString(R.string.working_please_wait), true);
		}

		@Override protected Integer doInBackground(Void... param) {

			String inputLine = "-1";
			BufferedReader in = null;
			try {
				URL turios_update = new URL(Constants.UPDATE_VERSIONCHECK_URL);
				in = new BufferedReader(new InputStreamReader(
						turios_update.openStream()));

				while ((inputLine = in.readLine()) != null) {
					return Integer.parseInt(inputLine);
				}

			} catch (IOException e) {
				errorMsg = getString(R.string.unable_to_contact_server);
				Log.e(TAG, inputLine, e);
				removeWorkingDialog();
			} catch (NumberFormatException e) {
				Log.e(TAG, inputLine, e);
				errorMsg = getString(R.string.invalid_data);
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException e) {

				}
			}
			return -1;

		}

		@Override protected void onPostExecute(Integer result) {

			if (!errorMsg.isEmpty()) {
				makeToast(errorMsg);
			} else {
				if (result != -1) {
					if (result > device.getVersionCode()) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								context);
						builder.setTitle(getString(R.string.update_found));
						builder.setMessage(getString(R.string.download_and_install_now));
						// Add the buttons
						builder.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										new DownloadUpdate().execute();
										dialog.dismiss();
									}
								});
						builder.setNegativeButton(android.R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// User cancelled the dialog
										dialog.dismiss();
									}
								});

						// Create the AlertDialog
						builder.show();
					} else {
						makeToast(getString(R.string.turios_is_up_to_date));
					}
				}
			}

			updateCheckbox.setChecked(false);
			removeWorkingDialog();
		}

		private void removeWorkingDialog() {
			if (working_dialog != null) {
				working_dialog.dismiss();
				working_dialog = null;
			}
		}
	}

	private class DownloadUpdate extends AsyncTask<Void, Void, File> {

		private ProgressDialog working_dialog;

		@Override protected void onPreExecute() {
			working_dialog = ProgressDialog.show(context, "",
					getString(R.string.working_please_wait), true);
		}

		@Override protected File doInBackground(Void... param) {

			String filename = "turios.apk";

			HttpURLConnection c = null;
			try {
				URL url = new URL(Constants.UPDATE_DOWNLOAD_URL);
				c = (HttpURLConnection) url.openConnection();
				c.setRequestMethod("GET");
				c.setDoOutput(true);
				c.connect();
			} catch (IOException e1) {
				// TODO handle
			}

			File myFilesDir = new File(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/Download");

			File file = new File(myFilesDir, filename);

			if (file.exists()) {
				file.delete();
			}

			if ((myFilesDir.mkdirs() || myFilesDir.isDirectory())) {
				try {
					InputStream is = c.getInputStream();
					FileOutputStream fos = new FileOutputStream(myFilesDir
							+ "/" + filename);

					byte[] buffer = new byte[1024];
					int len1 = 0;
					while ((len1 = is.read(buffer)) != -1) {
						fos.write(buffer, 0, len1);
					}
					fos.close();
					is.close();

				} catch (Exception e) {
					// TODO handle
				}

			}

			return file;
		}

		@Override protected void onPostExecute(File file) {

			String toastMsg = "";
			if (file == null) {
				toastMsg = getString(R.string.could_not_create_or_access_downloadfolder);
				;
			} else if (file.exists()) {
				installer.launchInstaller(file);
				toastMsg = getString(R.string.file_successfully_downloaded);
			} else {
				toastMsg = getString(R.string.could_not_locate_file);
			}

			if (!toastMsg.isEmpty()) {
				makeToast(toastMsg);
			}

			removeWorkingDialog();
		}

		private void removeWorkingDialog() {
			if (working_dialog != null) {
				working_dialog.dismiss();
				working_dialog = null;
			}
		}
	}

}