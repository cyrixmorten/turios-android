package com.turios.activities.setup;

import javax.inject.Inject;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.turios.R;
import com.turios.activities.SplashScreen;
import com.turios.activities.fragments.dialog.GenericOkDialogFragment;
import com.turios.activities.fragments.dialog.GenericOkDialogFragment.GenericOkDialogInterface;
import com.turios.dagger.DaggerActivity;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.core.ParseCoreModule.ParseLoginCallback;
import com.turios.modules.core.parse.ParseObjectHelper;

public class SetupWelcome extends DaggerActivity {

	@Inject FragmentManager fm;
	@Inject ParseCoreModule parse;
	@Inject ActionBar actionbar;

	@Bind(R.id.input_unit_name) EditText unitname;

	@OnClick(R.id.button_continue) public void onContinue(Button button) {
		String name = unitname.getText().toString();

		if (TextUtils.isEmpty(name.trim())) {
			GenericOkDialogFragment.newInstance(new GenericOkDialogInterface() {

				@Override public void okClicked() {

				}
			}, android.R.string.dialog_alert_title, R.string.missing_name,
					android.R.drawable.ic_dialog_alert).show(fm,
					"Dialog_name_missing");
		} else {
			ParseInstallation parseInstallation = parse.getParseInstallation();
			parseInstallation.put(ParseObjectHelper.ParseInstallation.name,
					name);
			parseInstallation.saveEventually();

			parse.getPreferences().setDevicename(name);

			startActivity(WizardHelper.getNextScreen());
			SetupWelcome.this.finish();
		}
	}

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ensure that we are logged in to parse
		parse.login(new ParseLoginCallback() {

			@Override public void success() {
				setContentView(R.layout.setup_welcome);

				actionbar.setTitle("Velkommen");

				ButterKnife.bind(SetupWelcome.this);

				unitname.setText(parse.getPreferences().getDevicename());

				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}

			@Override public void failed(Exception e) {
				Intent i = new Intent(SetupWelcome.this, SplashScreen.class)
						.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(i);
			}
		});

	}

	@Override public void onBackPressed() {
		// do nothing
	}
}
