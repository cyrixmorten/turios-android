package com.turios.activities.setup;

import javax.inject.Inject;

import android.app.ActionBar;
import android.os.Bundle;
import android.widget.Button;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.turios.R;
import com.turios.dagger.DaggerActivity;
import com.turios.persistence.Preferences;

public class SetupDone extends DaggerActivity {

	@Inject
	ActionBar actionbar;
	@Inject
	Preferences preferences;

	@OnClick(R.id.button_continue)
	public void onContinue(Button button) {

		startActivity(WizardHelper.getNextScreen());
		SetupDone.this.finish();

	}

	@OnClick(R.id.button_back)
	public void onPrevious(Button button) {
		startActivity(WizardHelper.getPreviousScreen());
		SetupDone.this.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup_done);

		actionbar.setTitle(getString(R.string.app_name));

		ButterKnife.bind(this);

		this.setFinishOnTouchOutside(false);

		preferences.setisFirstStartUp(false);

		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	@Override
	public void onBackPressed() {
		// do nothing
	}
}
