package com.turios.activities.setup;

import javax.inject.Inject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxPath;
import com.turios.R;
import com.turios.dagger.DaggerActivity;
import com.turios.modules.core.PathsCoreModule;
import com.turios.modules.extend.DropboxModule;
import com.turios.modules.extend.DropboxModule.DropboxCallback;
import com.turios.util.Constants;

public class SetupDropbox extends DaggerActivity implements DropboxCallback {

	private static final String TAG = "SetupDropbox";
	// Dropbox
	private DbxAccountManager mDbxAcctMgr;



	@Bind(R.id.setup_progress) ProgressBar progress;
	@Bind(R.id.setup_info) TextView info;
	@Bind(R.id.setup_dropbox_connect) Button connect_button;
	@Bind(R.id.button_continue) Button continue_button;
	@Bind(R.id.button_back) Button back_button;


	@Bind(R.id.progress_loading) ProgressBar loading;
	@Inject ActionBar actionbar;
	
	@Inject PathsCoreModule paths;
	@Inject DropboxModule dropboxModule;
	
	private boolean settings = false;

	// ActivityResult
	static final int REQUEST_LINK_TO_DBX = 0; // This value is up to you

	@OnClick(R.id.setup_dropbox_connect) public void onConnect(Button button) {
		if (isLinkedToDropbox()) {
			mDbxAcctMgr.unlink();
			stateLogin();
		} else {
			connect_button.setEnabled(false);
			mDbxAcctMgr.startLink((Activity) SetupDropbox.this,
					REQUEST_LINK_TO_DBX);
		}
	}

	@OnClick(R.id.button_continue) public void onContinue(Button button) {
		startActivity(WizardHelper.getNextScreen());
		SetupDropbox.this.finish();
	}

	@OnClick(R.id.button_back) public void onPrevious(Button button) {
		startActivity(WizardHelper.getPreviousScreen());
		SetupDropbox.this.finish();
	}

	@Override public void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			settings = extras.getBoolean("settings");
		}

	}

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.setup_dropbox);

		actionbar.setTitle(R.string.module_dropbox);

		ButterKnife.bind(this);

		handleIntent(getIntent());

		dropboxModule.setDropboxListener(this);

		mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
				Constants.DROPBOX_APPKEY, Constants.DROPBOX_SECRETKEY);

		this.setFinishOnTouchOutside(false);

		if (settings) {
			continue_button.setVisibility(View.GONE);
			back_button.setVisibility(View.GONE);
			actionbar.setDisplayHomeAsUpEnabled(true);
		}

		if (isLinkedToDropbox()) {
			stateLogout();
		} else {
			stateLogin();
		}

		// restore state
		if (savedInstanceState != null) {
			connect_button
					.setText(savedInstanceState.getString(
							"connect_button_text", connect_button.getText()
									.toString()));
			connect_button.setEnabled(savedInstanceState.getBoolean(
					"connect_button_enabled", connect_button.isEnabled()));

			progress.setMax(savedInstanceState.getInt("progressMax"));
			progress.setProgress(savedInstanceState.getInt("progressCurrent"));
		}

		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	private void stateLogin() {
		connect_button.setEnabled(true);
		connect_button.setText(getString(R.string.dropbox_login));
	}

	private void stateLogout() {
		connect_button.setEnabled(true);
		connect_button.setText(getString(R.string.dropbox_logout));
	}

	private boolean isLinkedToDropbox() {
		return mDbxAcctMgr.hasLinkedAccount()
				&& mDbxAcctMgr.getLinkedAccount().isLinked();
	}

	@Override protected void onResume() {
		super.onResume();

	}

	@Override protected void onSaveInstanceState(Bundle outState) {
		outState.putString("connect_button_text", connect_button.getText()
				.toString());
		outState.putBoolean("connect_button_enabled",
				connect_button.isEnabled());
		outState.putInt("progressMax", progress.getMax());
		outState.putInt("progressCurrent", progress.getProgress());
		super.onSaveInstanceState(outState);
	}

	@Override protected void onDestroy() {
		super.onDestroy();
	}

	@Override public void onBackPressed() {
		// do nothing
		if (settings) {
			this.finish();
		}

	}

	@Override public void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		Log.d(TAG, "onActivityResult " + requestCode + " " + resultCode);
		if (requestCode == REQUEST_LINK_TO_DBX) {
			if (resultCode == Activity.RESULT_OK) {
				// ... Start using Dropbox files.
				Toast.makeText(this, getString(R.string.dropbox_link_success),
						Toast.LENGTH_SHORT).show();

				loading.setVisibility(View.VISIBLE);
				dropboxModule.loadIfEnabled();
				dropboxModule.synchronize(DbxPath.ROOT);

			} else {
				// ... Link failed or was cancelled by the user.
				stateLogin();
				Toast.makeText(this, getString(R.string.dropbox_link_failure),
						Toast.LENGTH_SHORT).show();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override public void syncStarted(int totalfiles) {
		downloading();
		loading.setVisibility(View.INVISIBLE);
		progress.setMax(totalfiles);
	}
	
	@Override public void syncDownloaded(int filecount) {
		
		Log.d(TAG, "downloaded " + filecount + " of " + progress.getMax());
		
		downloading();

		
		progress.setProgress(filecount);
	}

	@Override public void syncEnded() {
		stateLogout();
	}

	@Override public void syncCanceled(String cause) {
		Log.e(TAG, "syncCanceled " + cause);
		progress.setVisibility(View.INVISIBLE);
		stateLogout();
	}

	private void downloading() {
		connect_button.setText(getString(R.string.connected));
		connect_button.setEnabled(false);
		progress.setVisibility(View.VISIBLE);
		if (info.getVisibility() == View.INVISIBLE) {
			new Handler().postDelayed(new Runnable() {

				@Override public void run() {
					info.setVisibility(View.VISIBLE);
				}
			}, 1000);
		}
	}


}
