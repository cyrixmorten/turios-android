package com.turios.modules.core.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.parse.CountCallback;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RefreshCallback;
import com.parse.SaveCallback;
import com.turios.R;
import com.turios.persistence.Preferences;

public class ParseProfile {

	protected static final String TAG = ParseProfile.class.getSimpleName();

	private ParseObject profile;

	private final Preferences preferences;
	private final ParseUser parseUser;
	private final ParseInstallation parseInstallation;
	private final Context context;

	public ParseProfile(Context context, Preferences preferences,
			ParseUser parseUser, ParseInstallation parseInstallation,
			RefreshCallback profileReady) {
		this.context = context;
		this.parseUser = parseUser;
		this.parseInstallation = parseInstallation;
		this.preferences = preferences;

		
		if (parseInstallation
				.has(ParseObjectHelper.Profile.objectname)) {
			profile = parseInstallation
					.getParseObject(ParseObjectHelper.Profile.objectname);
			if (profile != null) {
				Log.d(TAG, "found profile: "
						+ profile);
				profileReady.done(profile, null);
			} 
		} else {
			Log.d(TAG,
					"no profile selected");
		}
		
		if (profile == null) {
			loadProfile(parseUser, parseInstallation, profileReady);
		}
		
	}

	private void loadProfile(final ParseUser parseUser,
			final ParseInstallation parseInstallation,
			final RefreshCallback profileReady) {
		// #1 make sure that the Profiles object is setup
		ParseQuery<ParseObject> query = ParseQuery
				.getQuery(ParseObjectHelper.Profile.objectname);
		query.whereEqualTo(ParseObjectHelper.Profile.owner, parseUser);
		query.countInBackground(new CountCallback() {

			@Override public void done(int count, ParseException e) {
				if (e == null) {
					if (count == 0) {
						// create a default profile for this user
						Log.d(TAG, "create a default profile for this user");
						profile = new ParseObject(
								ParseObjectHelper.Profile.objectname);
						profile.put(ParseObjectHelper.Profile.owner, parseUser);
						profile.put(ParseObjectHelper.Profile.name,
								context.getString(R.string.Default));
						saveProfileToParseObject(profile, null);

						parseInstallation.put(
								ParseObjectHelper.Profile.objectname, profile);
						parseInstallation.saveEventually();

						profileReady.done(profile, e);
					} else {
						// locate currently selected profile
						Log.d(TAG, "locate currently selected profile");

						parseInstallation
								.refreshInBackground(new RefreshCallback() {

									@Override public void done(
											ParseObject object, ParseException e) {
										if (e == null) {
											ParseInstallation parseInstallation = (ParseInstallation) object;
											// ModulesAggregator.getParseCoreModule()
											// .setParseInstallation(
											// parseInstallation);
											if (parseInstallation
													.has(ParseObjectHelper.Profile.objectname)) {
												profile = parseInstallation
														.getParseObject(ParseObjectHelper.Profile.objectname);
												Log.d(TAG, "found profile: "
														+ profile);
											} else {
												Log.d(TAG,
														"no profile selected");
											}

											profileReady.done(profile, e);
										} else {
											Log.e(TAG, e.getMessage(), e);
										}
									}
								});

					}
				} else {
					Log.e(TAG, e.getMessage(), e);
				}
			}

		});
	}

	public void setProfile(ParseObject profile) {
		this.profile = profile;
	}

	public ParseObject getProfile() {
		return profile;
	}

	public void getProfiles(FindCallback<ParseObject> callback) {
		ParseQuery<ParseObject> query = ParseQuery
				.getQuery(ParseObjectHelper.Profile.objectname);
		query.whereEqualTo(ParseObjectHelper.Profile.owner, parseUser);
		query.findInBackground(callback);
	}

	public void reloadCurrentProfile(final RefreshCallback callback) {
		if (profile == null) {
			Log.w(TAG, "No profile selected");
			callback.done(profile, new ParseException(
					ParseException.OBJECT_NOT_FOUND, "profile is null"));
			return;
		}

		profile.refreshInBackground(new RefreshCallback() {

			@Override public void done(ParseObject object, ParseException e) {
				if (e == null) {
					profile = object;
					Map<String, Object> prefs = new HashMap<String, Object>();
					for (String key : profile.keySet()) {
						prefs.put(key, object.get(key));
					}
					preferences.loadPreferences(prefs);
					// Toast.makeText(
					// context,
					// context.getString(R.string.profile_settings_loaded)
					// + " "
					// + profile
					// .getString(ParseObjectHelper.Profile.name),
					// Toast.LENGTH_SHORT).show();
				}
				callback.done(object, e);
			}
		});
	}

	public void selectProfile(ParseObject profile, RefreshCallback callback) {
		this.profile = profile;
		parseInstallation.put(ParseObjectHelper.Profile.objectname, profile);
		parseInstallation.saveEventually();
		reloadCurrentProfile(callback);
	}

	/**
	 * @param callback
	 *            Notifies when the profile has been successfully saved
	 */
	public void saveCurrentProfile(final GetCallback<ParseObject> callback) {
		if (profile != null) {
			profile.fetchIfNeededInBackground(new GetCallback<ParseObject>() {

				@Override public void done(ParseObject object, ParseException e) {
					if (e == null) {
						saveProfileToParseObject(profile, callback);
					} else {
						Log.e(TAG, e.getMessage(), e);
					}
				}
			});
		} else {
			Log.e(TAG, "Profile was null");
		}
	}

	public void createProfile(final String profilename,
			final GetCallback<ParseObject> callback) {

		final ParseObject newProfile = new ParseObject(
				ParseObjectHelper.Profile.objectname);
		newProfile.put(ParseObjectHelper.Profile.name, profilename);
		newProfile.put(ParseObjectHelper.Profile.owner, parseUser);
		Map<String, ?> preferences = PreferenceManager
				.getDefaultSharedPreferences(context).getAll();
		for (String key : preferences.keySet()) {
			if (key.startsWith("key") && !key.contains("devicename")) {
				newProfile.put(key, preferences.get(key));
			}
		}
		newProfile.saveEventually(new SaveCallback() {

			@Override public void done(ParseException e) {
				callback.done(newProfile, e);
			}
		});
	}

	public void deleteProfile(final ParseObject profile,
			final DeleteCallback callback) {
		ParseQuery<ParseObject> query = ParseQuery
				.getQuery(ParseObjectHelper.Profile.objectname);
		query.getInBackground(profile.getObjectId(),
				new GetCallback<ParseObject>() {
					public void done(ParseObject profile, ParseException e) {
						if (e == null) {
							profile.deleteInBackground(new DeleteCallback() {

								@Override public void done(ParseException e) {
									callback.done(e);
								}
							});
						} else {
							callback.done(e);
						}
					}
				});
	}

	private void saveProfileToParseObject(final ParseObject object,
			final GetCallback<ParseObject> callback) {
		Map<String, ?> prefs = preferences.getAll();
		// boolean isUpdated = false;
		for (String key : prefs.keySet()) {
			if (key.startsWith("key")) {
				if (!object.containsKey(key)) {
					Log.d(TAG, key + " was not known");
					object.put(key, prefs.get(key));
					// isUpdated = true;
				} else if (profileKeyChanged(object, key, prefs.get(key))) {
					Log.d(TAG, key + " was updated");
					object.put(key, prefs.get(key));
					// isUpdated = true;
				} else {
					// nothing new to this preference
				}
			}
		}

		if (object.isDirty()) {
			Log.w(TAG, "Saving changes to preferences");
			if (callback == null) {
				object.saveEventually();
			} else {
				object.saveEventually(new SaveCallback() {

					@Override public void done(ParseException e) {
						callback.done(object, e);
					}
				});
			}
		}
	}

	private boolean profileKeyChanged(ParseObject object, String key,
			Object pref) {

		String actionMsg = "";
		boolean result = false;
		if (pref instanceof Boolean) {
			actionMsg = "comparing Boolean " + object.getBoolean(key)
					+ " with " + (Boolean) pref;
			result = object.getBoolean(key) == (Boolean) pref;
		}
		if (pref instanceof Float) {
			actionMsg = "comparing Float " + object.getInt(key) + " with "
					+ (Integer) pref;
			result = object.getInt(key) == (Integer) pref;
		}
		if (pref instanceof Integer) {
			actionMsg = "comparing Integer " + object.getInt(key) + " with "
					+ (Integer) pref;
			result = object.getInt(key) == (Integer) pref;
		}
		if (pref instanceof Long) {
			actionMsg = "comparing Long " + object.getLong(key) + " with "
					+ (Long) pref;
			result = object.getLong(key) == (Long) pref;
		}
		if (pref instanceof String) {
			actionMsg = "comparing String " + object.getString(key) + " with "
					+ (String) pref;
			result = object.getString(key).equals((String) pref);
		}
		if (pref instanceof Set<?>) {
			actionMsg = "comparing String Set<?>";
			result = false; // TODO handle this
		}
		if (!result) {
			Log.d(TAG, "profileKeyChanged " + key);
			Log.d(TAG, actionMsg);
		}
		return !result;
	}
}
