package com.turios.activities.setup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.RefreshCallback;
import com.turios.R;
import com.turios.dagger.DaggerActivity;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.core.parse.ParseObjectHelper;

public class SetupProfiles extends DaggerActivity {

	@Inject ParseCoreModule parse;
	@Inject LayoutInflater inflater;
	@Inject ActionBar actionbar;

	@InjectView(R.id.setup_content) LinearLayout content;
	@InjectView(R.id.progress_loading) ProgressBar loading;
	@InjectView(R.id.setup_info_no_profile) TextView noProfileInfo;

	private List<Switch> switches;

	@OnClick(R.id.button_continue) public void onContinue(Button button) {

		startActivity(WizardHelper.getNextScreen());
		SetupProfiles.this.finish();

	}

	@OnClick(R.id.button_back) public void onPrevious(Button button) {
		startActivity(WizardHelper.getPreviousScreen());
		SetupProfiles.this.finish();
	}

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.setup_profiles);
		actionbar.setTitle("Profil");

		switches = new ArrayList<Switch>();

		ButterKnife.inject(SetupProfiles.this);

		final ParseObject currentProfile = parse.getParseProfile().getProfile();
		parse.getParseProfile().getProfiles(new FindCallback<ParseObject>() {

			@Override public void done(List<ParseObject> profiles,
					ParseException e) {
				loading.setVisibility(View.GONE);
				if (e == null && profiles.size() > 0) {
					noProfileInfo.setVisibility(View.GONE);
					for (ParseObject profile : profiles) {
						String name = profile
								.getString(ParseObjectHelper.Profile.name);

						LinearLayout profileselect = (LinearLayout) inflater
								.inflate(R.layout.view_profileselect, null);
						TextView nametv = ButterKnife.findById(profileselect,
								R.id.profileselect_name);
						Switch profileswitch = ButterKnife.findById(
								profileselect, R.id.profileselect_switch);

						nametv.setText(name);
						profileswitch.setTag(profile);
						if (currentProfile != null && currentProfile.hasSameId(profile)) {
							profileswitch.setChecked(true);
						}
						profileswitch
								.setOnCheckedChangeListener(new ProfileSelect());

						content.addView(profileselect);
						switches.add(profileswitch);
					}
				} else {
					noProfileInfo.setVisibility(View.VISIBLE);
				}
			}
		});

		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

	}

	private class ProfileSelect implements OnCheckedChangeListener {

		@Override public void onCheckedChanged(final CompoundButton buttonView,
				boolean isChecked) {
			uncheckall();
			ParseObject profile = (ParseObject) buttonView.getTag();
			if (isChecked) {
				buttonView.setChecked(true);
				parse.getParseProfile().selectProfile(profile,
						new RefreshCallback() {

							@Override public void done(ParseObject arg0,
									ParseException e) {
								if (e != null) {
									Toast.makeText(getApplicationContext(),
											e.getMessage(), Toast.LENGTH_SHORT)
											.show();
									buttonView.setChecked(false);
								}
							}
						});

			} else {
				// Toast.makeText(getApplicationContext(), name + " deselected",
				// Toast.LENGTH_SHORT).show();
			}
		}

	}

	private void uncheckall() {
		for (Switch aswitch : switches) {
			aswitch.setChecked(false);
		}
	}

	@Override public void onBackPressed() {
		// do nothing
	}
}
