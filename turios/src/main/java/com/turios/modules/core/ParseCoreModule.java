package com.turios.modules.core;

import android.content.Context;
import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.RefreshCallback;
import com.parse.SaveCallback;
import com.turios.dagger.quialifiers.ForApplication;
import com.turios.modules.Module;
import com.turios.modules.Module.MODULES;
import com.turios.modules.core.parse.ParseObjectHelper;
import com.turios.modules.core.parse.ParseProfile;
import com.turios.modules.preferences.ParseCoreModulePreferences;
import com.turios.persistence.Preferences;
import com.turios.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ParseCoreModule {

	private final Preferences preferences;
	private ParseCoreModulePreferences prefs;

	public ParseCoreModulePreferences getPreferences() {
		return prefs;
	}

	private static final String TAG = "ParseCoreModule";

	public interface ParseLoginCallback {
		public void success();

		public void failed(Exception e);
	}

	private ParseUser parseUser;
	private ParseInstallation parseInstallation;
	private ParseProfile parseProfile;

	private final Context context;

	@Inject public ParseCoreModule(@ForApplication Context context, Preferences preferences) {
		this.context = context;
		this.preferences = preferences;
		this.prefs = new ParseCoreModulePreferences(preferences);
	}

	public void pushRefreshToProfile() {
		if (parseProfile != null && parseProfile.getProfile() != null) {
			ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
			query.whereEqualTo(ParseObjectHelper.ParseInstallation.profile,
					parseProfile.getProfile());
			query.whereEqualTo(ParseObjectHelper.ParseInstallation.owner,
					parseUser);
			query.whereNotEqualTo(ParseObjectHelper.objectId,
					parseInstallation.getObjectId());

			long fiveminutes = 60 * 5;
			try {
				JSONObject data = new JSONObject();
				data.put("action", Constants.INTENT_REFRESH_MODULES);

				ParsePush androidPush = new ParsePush();
				androidPush.setQuery(query);
				androidPush.setData(data);
				androidPush.setExpirationTimeInterval(fiveminutes);
				androidPush.sendInBackground();

			} catch (JSONException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}

	public ParseUser getParseUser() {
		return parseUser;
	}

	public ParseInstallation getParseInstallation() {
		Log.d(TAG, "getParseInstallation " + parseInstallation);
		return parseInstallation;
	}

	public boolean isModulePaid(Module.MODULES module) {
		if (parseInstallation == null) {
			return true;
		} else {
			ParseObject modules = parseInstallation
					.getParseObject(ParseObjectHelper.ModulesPaid.objectname);
			if (modules != null) {
				try {
					modules.fetchIfNeeded();
					return modules.getBoolean(module.toString());
				} catch (ParseException e) {
					Log.e(TAG, e.getMessage(), e);
					return true;
				}

			} else {
				Log.e(TAG, "modules is null");
				return true;
			}
		}
	}

	public void refreshModulesPaid(final RefreshCallback callback) {

		parseInstallation.getParseObject(
				ParseObjectHelper.ModulesPaid.objectname).refreshInBackground(
				new RefreshCallback() {

					@Override public void done(ParseObject object,
							ParseException e) {
						if (e == null) {
							callback.done(object, e);
						}
					}
				});
	}

	public void newTurnout(long id, String message) {
		parseInstallation
				.increment(ParseObjectHelper.ParseInstallation.turnouts);
		parseInstallation.saveEventually();
	}

	public void updateTurnout(long id, String message) {

	}

	private void completeLogin(final ParseLoginCallback loginCallback) {
		if (parseProfile == null) {
			parseProfile = new ParseProfile(context, preferences, parseUser,
					parseInstallation, new RefreshCallback() {

						@Override public void done(ParseObject object,
								ParseException e) {
							if (e == null) {
								if (loginCallback != null) {
									loginCallback.success();
								}
							} else {
								if (loginCallback != null) {
									loginCallback.failed(e);
								}
							}
						}
					});
		} else {
			loginCallback.success();
		}
	}

	public void setParseInstallation(ParseInstallation parseInstallation) {
		this.parseInstallation = parseInstallation;
	}

	public void getChannels(final FindCallback<ParseObject> callback) {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				ParseObjectHelper.Channel.objectname);
		query.whereEqualTo(ParseObjectHelper.Channel.owner, parseUser);
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override public void done(List<ParseObject> objects,
					ParseException e) {
				if (e == null) {
					Log.d(TAG, "getChannels found: " + objects.size()
							+ " channels");
				} else {
					Log.e(TAG, e.getMessage(), e);
				}
				callback.done(objects, e);
			}
		});
	}

	public void createChannel(final String channelname,
			final GetCallback<ParseObject> callback) {

		final ParseObject newChannel = new ParseObject(
				ParseObjectHelper.Channel.objectname);
		newChannel.put(ParseObjectHelper.Channel.name, channelname);
		newChannel.put(ParseObjectHelper.Channel.owner, parseUser);

		newChannel.saveEventually(new SaveCallback() {

			@Override public void done(ParseException e) {
				callback.done(newChannel, e);
			}
		});
	}

	public void deleteChannel(final ParseObject channel,
			final DeleteCallback callback) {
		ParseQuery<ParseObject> query = ParseQuery
				.getQuery(ParseObjectHelper.Channel.objectname);
		query.getInBackground(channel.getObjectId(),
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

	public void addChannelSendRelation(final ParseObject channel,
			final GetCallback<ParseObject> callback) {

		ParseRelation<ParseObject> channelSendRelation = parseInstallation
				.getRelation(ParseObjectHelper.ParseInstallation.sendChannelRelation);

		channelSendRelation.add(channel);
		parseInstallation.saveInBackground(new SaveCallback() {

			@Override public void done(ParseException e) {
				callback.done(channel, e);
			}
		});
	}

	public void removeChannelSendRelation(final ParseObject channel,
			final GetCallback<ParseObject> callback) {

		ParseRelation<ParseObject> channelSendRelation = parseInstallation
				.getRelation(ParseObjectHelper.ParseInstallation.sendChannelRelation);
		channelSendRelation.remove(channel);
		parseInstallation.saveInBackground(new SaveCallback() {

			@Override public void done(ParseException e) {
				callback.done(channel, e);
			}
		});
	}

	public void addChannelListenRelation(final ParseObject channel,
			final GetCallback<ParseObject> callback) {

		ParseRelation<ParseObject> channelListenRelation = parseInstallation
				.getRelation(ParseObjectHelper.ParseInstallation.listenChannelRelation);
		channelListenRelation.add(channel);
		parseInstallation.saveInBackground(new SaveCallback() {

			@Override public void done(ParseException e) {
				callback.done(channel, e);
			}
		});
	}

	public void removeChannelListenRelation(final ParseObject channel,
			final GetCallback<ParseObject> callback) {

		ParseRelation<ParseObject> channelListenRelation = parseInstallation
				.getRelation(ParseObjectHelper.ParseInstallation.listenChannelRelation);
		channelListenRelation.remove(channel);
		parseInstallation.saveInBackground(new SaveCallback() {

			@Override public void done(ParseException e) {
				callback.done(channel, e);
			}
		});
	}

	public void getChannelSendRelation(final FindCallback<ParseObject> callback) {

		ParseRelation<ParseObject> channelSendRelation = parseInstallation
				.getRelation(ParseObjectHelper.ParseInstallation.sendChannelRelation);
		channelSendRelation.getQuery().findInBackground(
				new FindCallback<ParseObject>() {
					public void done(List<ParseObject> results, ParseException e) {
						callback.done(results, e);
					}
				});
	}

	public void getChannelListenRelation(
			final FindCallback<ParseObject> callback) {

		ParseRelation<ParseObject> channelSendRelation = parseInstallation
				.getRelation(ParseObjectHelper.ParseInstallation.listenChannelRelation);
		channelSendRelation.getQuery().findInBackground(
				new FindCallback<ParseObject>() {
					public void done(List<ParseObject> results, ParseException e) {
						callback.done(results, e);
					}
				});
	}

	public void login(final ParseLoginCallback parseLoginCallback) {
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {
			// do stuff with the user
			loginSuccess(currentUser, parseLoginCallback);
		} else {
			ParseUser.logInInBackground(prefs.getUsername(),
					prefs.getPassword(), new LogInCallback() {
						public void done(ParseUser user, ParseException e) {
							if (user != null) {
								loginSuccess(user, parseLoginCallback);
							} else {
								parseUser = null;
								if (parseLoginCallback != null) {
									parseLoginCallback.failed(e);
								}
							}
						}
					});
		}
	}

	private void loginSuccess(ParseUser user,
			final ParseLoginCallback loginCallback) {
		parseUser = user;

		if (user.isDirty()) {
			user.saveEventually();
		}

		parseInstallation = ParseInstallation.getCurrentInstallation();

		if (parseInstallation != null) {
			Log.d(TAG, "Parse logged in!");
			addDataToParseInstallation(loginCallback);
		} else {
			ParseInstallation.getCurrentInstallation().saveInBackground(
					new SaveCallback() {

						@Override public void done(ParseException e) {
							Log.d(TAG, "saveInstallation done");
							if (e == null) {
								addDataToParseInstallation(loginCallback);
							} else {
								loginCallback.failed(e);
							}
						}

					});
		}
	}

	private void addDataToParseInstallation(
			final ParseLoginCallback loginCallback) {
		parseInstallation = ParseInstallation.getCurrentInstallation();
		if (parseInstallation != null) {

			addModulesPaidToParseInstallation(parseInstallation);
			addOwnerToParseInstallation(parseInstallation);
			addTurnoutsToParseInstallation(parseInstallation);

			if (parseInstallation.isDirty()) {
				Log.d(TAG, "parseinstallation is dirty - saving");
				parseInstallation.saveInBackground(new SaveCallback() {

					@Override public void done(ParseException e) {
						if (e == null) {
							completeLogin(loginCallback);
						} else {
							loginCallback.failed(e);
						}
					}
				});
			} else {
				Log.d(TAG, "parseinstallation is ready");
				completeLogin(loginCallback);
			}

		} else {
			loginCallback.failed(new Exception("parseInstallation was null"));
		}

	}

	private void addTurnoutsToParseInstallation(
			ParseInstallation parseInstallation2) {
		if (!parseInstallation
				.has(ParseObjectHelper.ParseInstallation.turnouts)) {
			parseInstallation.put(ParseObjectHelper.ParseInstallation.turnouts,
					0);
		}
	}

	private void addOwnerToParseInstallation(
			ParseInstallation parseInstallation2) {
		ParseUser installationUser = (ParseUser) parseInstallation.get("owner");

		if (installationUser == null
				|| !installationUser.getObjectId().equals(
						parseUser.getObjectId())) {
			parseInstallation.put(ParseObjectHelper.ParseInstallation.owner,
					parseUser);

			Log.d(TAG, "associating user with installation");
		}
	}

	private void addModulesPaidToParseInstallation(
			ParseInstallation parseInstallation) {
		ParseObject modules = parseInstallation
				.getParseObject(ParseObjectHelper.ModulesPaid.objectname);
		if (modules == null) {
			Log.d(TAG, "ModulesPaid is null - creating");
			modules = ParseObject
					.create(ParseObjectHelper.ModulesPaid.objectname);
			parseInstallation.add(ParseObjectHelper.ModulesPaid.objectname,
					modules);
		}
		try {
			modules.fetchIfNeeded();
			for (MODULES module : MODULES.values()) {
				if (!modules.has(module.toString())) {
					Log.d(TAG,
							"ModulesPaid did not have - " + module.toString());
					modules.put(module.toString(), true);
				}
			}
			if (modules.isDirty()) {
				Log.d(TAG, "ModulesPaid is dirty - saving");
				parseInstallation.put(ParseObjectHelper.ModulesPaid.objectname,
						modules);
			}
		} catch (ParseException e1) {
			Log.e(TAG, e1.getMessage(), e1);
		}
	}

	public boolean isLoggedIn() {
		return parseUser != null;
	}

	public void incrementTurnouts() {
		parseInstallation
				.increment(ParseObjectHelper.ParseInstallation.turnouts);
		parseInstallation.saveEventually();
	}

	public ParseProfile getParseProfile() {
		return parseProfile;
	}

	public void setParseProfile(ParseProfile parseProfile) {
		this.parseProfile = parseProfile;
	}

}
