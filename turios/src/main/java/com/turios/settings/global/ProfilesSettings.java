package com.turios.settings.global;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.RefreshCallback;
import com.turios.R;
import com.turios.dagger.DaggerPreferenceFragment;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.core.parse.ParseObjectHelper;

public class ProfilesSettings extends DaggerPreferenceFragment {

	private static final String TAG = ProfilesSettings.class.getSimpleName();

	private ParseObject currentProfile;
	private List<ParseObject> profiles;
	private List<SwitchPreference> switchPrefs;
	private PreferenceCategory cat;

	@Inject ParseCoreModule parse;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings_profiles);

		setHasOptionsMenu(true);
		cat = (PreferenceCategory) findPreference(getString(R.string.category1));

	}

	@Override public void onStart() {
		create();
		super.onStart();
	}

	private void create() {
		cat.removeAll();
		switchPrefs = new ArrayList<SwitchPreference>();

		currentProfile = parse.getParseProfile().getProfile();

		parse.getParseProfile().getProfiles(new FindCallback<ParseObject>() {

			@Override public void done(List<ParseObject> objects,
					ParseException e) {
				if (e == null) {
					profiles = objects;
					for (ParseObject profile : profiles) {
						addProfile(profile);
					}
				}
			}
		});
	}

	@Override public void onResume() {
		super.onResume();
	}

	private void addProfile(ParseObject profile) {
		if (this.isDetached()) {
			return;
		}
		
		SwitchPreference switchPref = new SwitchPreference(getActivity());
		switchPref.setTitle(profile.getString(ParseObjectHelper.Profile.name));
		switchPref.setOrder(switchPrefs.size());
		// switchPref.setKey(String.valueOf(profiles.size()));
		if (currentProfile != null
				&& profile.getObjectId().equals(currentProfile.getObjectId())) {
			switchPref.setChecked(true);
		} else {
			switchPref.setChecked(false);
		}

		switchPref.setOnPreferenceChangeListener(new OnProfileChanged());

		cat.addPreference(switchPref);
		switchPrefs.add(switchPref);

		if (!profiles.contains(profile)) {
			profiles.add(profile);
		}
	}

	private void deleteProfile(int index) {
		final ParseObject profile = profiles.get(index);
		final SwitchPreference switchPref = switchPrefs.get(index);
		parse.getParseProfile().deleteProfile(profile, new DeleteCallback() {

			@Override public void done(ParseException e) {
				if (e == null) {
					profiles.remove(profile);
					switchPrefs.remove(switchPref);
					cat.removePreference(switchPref);
					Toast.makeText(getActivity(),
							getString(R.string.profile_deleted),
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(),
							getString(R.string.error_deleting),
							Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	private class OnProfileChanged implements OnPreferenceChangeListener {

		@Override public boolean onPreferenceChange(Preference preference,
				Object newValue) {
			SwitchPreference switchPreference = (SwitchPreference) preference;
			if (!(Boolean) newValue) {
				// cannot turn off a profile
				Log.d(TAG, "cannot turn off a profile");
				return false;
			} else {
				// turn all other profiles off
				for (SwitchPreference switchPref : switchPrefs) {
					if (!switchPref.equals(switchPreference)) {
						switchPref.setChecked(false);
					} else {
						// and select the new profile
						int profileIndex = switchPreference.getOrder();
						final ParseObject profile = profiles.get(profileIndex);

						// already selected profile
						if (currentProfile != null
								&& profile.getObjectId().equals(
										currentProfile.getObjectId()))
							return true;

						profile.fetchIfNeededInBackground(new GetCallback<ParseObject>() {

							@Override public void done(ParseObject object,
									ParseException e) {
								if (e == null) {
									parse.getParseProfile().selectProfile(
											profile, new RefreshCallback() {

												@Override public void done(
														ParseObject object,
														ParseException e) {
													if (e == null) {
														currentProfile = profile;
														Log.d(TAG,
																"selected profile "
																		+ profile
																				.getString(ParseObjectHelper.Profile.name));
													} else {
														fallback();
														Log.e(TAG,
																e.getMessage(),
																e);
													}
												}
											});

								} else {
									fallback();
									Log.e(TAG, e.getMessage(), e);
								}
							}
						});

					}
				}

			}
			return true;
		}

		private void fallback() {
			Toast.makeText(getActivity(),
					getString(R.string.unable_to_contact_server),
					Toast.LENGTH_SHORT).show();
			if (currentProfile != null) {
				switchPrefs.get(profiles.indexOf(currentProfile)).setChecked(
						true);
			}
		}

	};

	@Override public void onCreateOptionsMenu(android.view.Menu menu,
			android.view.MenuInflater inflater) {
		inflater.inflate(R.menu.menu_add_delete_refresh, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_btn_add:

			final EditText editText = new EditText(getActivity());
			new AlertDialog.Builder(getActivity())
					.setCancelable(true)
					.setTitle(R.string.profile_create)
					.setIcon(android.R.drawable.ic_menu_add)
					.setView(editText)
					.setPositiveButton(android.R.string.ok,
							new OnClickListener() {
								@Override public void onClick(
										DialogInterface dialog, int which) {

									final String editTextString = editText
											.getText().toString();
									if (!editTextString.isEmpty()) {

										for (SwitchPreference switchPref : switchPrefs) {
											if (switchPref.getTitle().equals(
													editTextString)) {
												Toast.makeText(
														getActivity(),
														getString(R.string.profile_already_exists),
														Toast.LENGTH_SHORT)
														.show();
											}
										}

										parse.getParseProfile().createProfile(
												editTextString,
												new GetCallback<ParseObject>() {

													@Override public void done(
															ParseObject profile,
															ParseException e) {
														if (e == null) {
															addProfile(profile);

															Toast.makeText(
																	getActivity(),
																	getString(R.string.profile_created),
																	Toast.LENGTH_SHORT)
																	.show();
														} else {
															Toast.makeText(
																	getActivity(),
																	getString(R.string.error_creating),
																	Toast.LENGTH_SHORT)
																	.show();
														}
													}
												});

									}
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new OnClickListener() {

								@Override public void onClick(
										DialogInterface dialog, int which) {

								}
							}).create().show();

			break;

		case R.id.menu_btn_delete:

			final String[] profilenames = new String[profiles.size()];
			int index = 0;
			for (SwitchPreference switchPref : switchPrefs) {
				profilenames[index] = switchPref.getTitle().toString();
				index++;
			}

			new AlertDialog.Builder(getActivity())
					.setTitle(R.string.profile_delete)
					.setIcon(android.R.drawable.ic_menu_delete)
					.setItems(profilenames, new OnClickListener() {

						@Override public void onClick(DialogInterface dialog,
								int which) {
							final int profileindex = which;
							new AlertDialog.Builder(getActivity())
									.setTitle(
											android.R.string.dialog_alert_title)
									.setIcon(android.R.drawable.ic_dialog_alert)
									.setMessage(
											getString(R.string.confirm_delete,
													profilenames[profileindex]))
									.setPositiveButton(android.R.string.ok,
											new OnClickListener() {

												@Override public void onClick(
														DialogInterface dialog,
														int which) {
													deleteProfile(profileindex);
												}
											})
									.setNegativeButton(android.R.string.cancel,
											null).create().show();
						}
					})
					.setNegativeButton(android.R.string.cancel,
							new OnClickListener() {

								@Override public void onClick(
										DialogInterface dialog, int which) {

								}
							}).create().show();
			break;
		case R.id.menu_btn_refresh:
			create();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}