package com.turios.activities;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.crashlytics.android.Crashlytics;
import com.parse.ParseException;
import com.turios.R;
import com.turios.activities.fragments.dialog.DialogFragments;
import com.turios.activities.fragments.dialog.GenericOkDialogFragment;
import com.turios.activities.fragments.dialog.GenericOkDialogFragment.GenericOkDialogInterface;
import com.turios.activities.fragments.dialog.ParseLoginDialogFragment;
import com.turios.activities.fragments.dialog.ParseLoginDialogFragment.ParseLoginDialogInterface;
import com.turios.activities.setup.SetupDone;
import com.turios.activities.setup.SetupDropbox;
import com.turios.activities.setup.SetupPDFViewer;
import com.turios.activities.setup.SetupProfiles;
import com.turios.activities.setup.SetupWelcome;
import com.turios.activities.setup.WizardHelper;
import com.turios.activities.util.Orientation;
import com.turios.dagger.DaggerActivity;
import com.turios.modules.Module;
import com.turios.modules.ModuleLoadCallback;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.core.ParseCoreModule.ParseLoginCallback;
import com.turios.modules.extend.AccessplansModule;
import com.turios.modules.extend.BasisModule;
import com.turios.modules.extend.BrowserModule;
import com.turios.modules.extend.DirectionsModule;
import com.turios.modules.extend.DropboxModule;
import com.turios.modules.extend.GoogleMapsModule;
import com.turios.modules.extend.HydrantsModule;
import com.turios.modules.extend.PicklistModule;
import com.turios.modules.preferences.ParseCoreModulePreferences;
import com.turios.persistence.Preferences;
import com.turios.util.Device;

public class SplashScreen extends DaggerActivity implements ModuleLoadCallback {

	private static final String TAG = "SplashScreen";

	private static final String TAG_DIALOG_LOGIN = "dialog_parselogin";

//	private DialogFragment loginDialog;

	@Nullable @Bind(R.id.progressBar1)
	ProgressBar progressBar;
	@Nullable @Bind(R.id.imgLogo)
	ImageView logo;

	@Inject
	Device device;
	@Inject
	Orientation orientation;
	@Inject
	Preferences preferences;

	@Inject
	FragmentManager fm;

	@Inject
	ParseCoreModule parse;

	@Inject
	AccessplansModule accessplansModule;
	@Inject
	BasisModule basisModule;
	@Inject
	BrowserModule browserModule;
	@Inject
	DirectionsModule directionsModule;
	@Inject
	DropboxModule dropboxModule;
	@Inject
	GoogleMapsModule googleMapsModule;
	@Inject
	HydrantsModule hydrantsModule;
	@Inject
	PicklistModule picklistModule;

	private ParseCoreModulePreferences parsePreferences;

	private List<Module> modules;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Crashlytics.start(this);
		Log.d(TAG, "onCreate");

		orientation.lockOrientation();


		modules = new ArrayList<Module>();
		modules.add(basisModule);
		modules.add(picklistModule);
		modules.add(accessplansModule);
		modules.add(browserModule);
		modules.add(dropboxModule);
		modules.add(googleMapsModule);
		modules.add(hydrantsModule);
		modules.add(directionsModule);

		parsePreferences = parse.getPreferences();

		if (TuriosApplication.getInstance().shouldShowSplashScreen()) {
			setContentView(R.layout.activity_splash);
			ButterKnife.bind(this);

			/** backwards compatibility changes */
			// EditText converted to SeekBarDialog
			preferences.convertStringToInt(R.string.key_hydrants_radius);
			/** backwards compatibility changes */

			logo.setVisibility(View.VISIBLE);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					progressBar.setVisibility(View.VISIBLE);

					if (parsePreferences.hasUsername()) {
						Log.d(TAG, "LOGIN auto");
						login();
					}
				}
			}, 1000);

			if (!parsePreferences.hasUsername()) {
				showLoginDialog();
			}

			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		} else {
			Log.d(TAG, "No splashscreen");
			startTurios();
		}
	}

	@Override
	protected void onPostResume() {
		Log.d(TAG, "onPostResume");

		super.onPostResume();
	}

	private void login() {
		Log.d(TAG, "LOGIN success");
		if (!device.isOnline()) {
			Toast.makeText(this, R.string.no_internet_connection,
					Toast.LENGTH_LONG).show();
			GenericOkDialogFragment.newInstance(new GenericOkDialogInterface() {
				
				@Override
				public void okClicked() {
					login();
				}
			}, R.string.login_failed, R.string.retry).show(fm, "retry_login");
//			showLoginDialog();
		} else {
			parse.login(parseLoginCallback);
		}
	}

	private void showLoginDialog() {
//		if (loginDialog == null) {
			ParseLoginDialogFragment.newInstance(
					new ParseLoginDialogInterface() {

						@Override
						public void okClicked(String username, String password) {
							Log.d(TAG, "LOGIN manual: " + username + " "
									+ password);

							parsePreferences.setUsername(username);
							parsePreferences.setPassword(password);

							login();

						}
					}, parsePreferences).show(fm, TAG_DIALOG_LOGIN);
//		}

//		if (!loginDialog.isAdded()) {
//			loginDialog.show(fm, TAG_DIALOG_LOGIN);
//		}
	}

	private ParseLoginCallback parseLoginCallback = new ParseLoginCallback() {

		@Override
		public void success() {
			Log.d(TAG, "LOGIN success");
			loadModules();
		}

		@Override
		public void failed(Exception e) {
			Log.d(TAG, "LOGIN failed " + e.getMessage());
			if (device.isOnline()) {
				// assume wrong login credentials
				parsePreferences.setPassword("");
				Toast.makeText(SplashScreen.this,
						getString(R.string.login_failed), Toast.LENGTH_SHORT)
						.show();
				showLoginDialog();
				Log.e(TAG, e.getMessage(), e);
			} else {
				DialogFragments.needInternetConnection(
						new GenericOkDialogInterface() {

							@Override
							public void okClicked() {
								login();
							}
						}).show(fm, "dialog_need_internet");

			}
		}
	};

	@Override
	public void startedLoadingModule(String module) {
		Log.d(TAG, "Loading " + module);
	}

	@Override
	public void doneLoadingModule(String module) {
		Log.d(TAG, "Loaded " + module + " " + progressBar.getProgress() + "/"
				+ modules.size());
		progressBar.setProgress(progressBar.getProgress() + 1);

		if (progressBar.getProgress() == modules.size()) {

			for (final Module amodule : modules) {
				amodule.setModuleLoadCallback(null);
			}

			// This method will be executed once the timer is over
			// Start your app main activity

			if (preferences.isFirstStartUp()) {
				WizardHelper.buildWizard().add(this, SetupWelcome.class)
						.add(this, SetupProfiles.class)
						.add(this, SetupPDFViewer.class)
						.add(this, SetupDropbox.class)
						.add(this, SetupDone.class)
						.add(this, SplashScreen.class);
				startActivity(WizardHelper.getNextScreen());
				finish();
				return;
			} else {
				startTurios();
			}

		}
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}

	private void startTurios() {
		orientation.releaseOrientation();
		Intent intent = new Intent(SplashScreen.this, Turios.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		TuriosApplication.getInstance().setShowSplashScreen(false);
		// close this activity
		finish();

	}

	private void loadModules() {

		if (progressBar != null) {
			progressBar.setMax(modules.size());
			progressBar.setProgress(0);
		} else {
			Log.e(TAG, "Where is my progressbar?!");
		}

		for (final Module module : modules) {
			module.setModuleLoadCallback(this);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {

					module.loadIfEnabled();
				}
			}, 50);
		}
	}

}
