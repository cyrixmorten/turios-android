package com.turios.sms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.turios.dagger.DaggerBroadcastReceiver;
import com.turios.modules.core.DisplayCoreModule;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.core.ParseCoreModule.ParseLoginCallback;
import com.turios.modules.core.parse.ParseObjectHelper;
import com.turios.util.Constants;

public class ParseSMSReceiver extends DaggerBroadcastReceiver {

	private static final String TAG = ParseSMSReceiver.class.getName();

	@Inject DisplayCoreModule display;
	
	@Inject
	ParseCoreModule parse;

	@Override
	public void onReceive(final Context context, final Intent intent) {
		super.onReceive(context, intent);

		if (parse == null) {
			Log.e(TAG, "Parse is null!");
			return;
		}

		parse.login(new ParseLoginCallback() {

			@Override
			public void success() {
				parse.getChannelListenRelation(new FindCallback<ParseObject>() {

					List<String> listenToChannels = new ArrayList<String>();

					@Override
					public void done(List<ParseObject> channels,
							ParseException e) {

						if (e != null)
							return;

						for (ParseObject channel : channels) {
							String channelname = channel
									.getString(ParseObjectHelper.Channel.name);
							listenToChannels.add(channelname);
							Log.d(TAG, "listening on channel: " + channelname);
						}

						try {

							JSONObject json = new JSONObject(intent.getExtras()
									.getString("com.parse.Data"));

							String message = (String) json
									.get(Constants.EXTRA_MESSAGE);
//							String sender = (json.has(Constants.EXTRA_SENDER)) ? (String) json
//									.get(Constants.EXTRA_SENDER) : null;

							JSONArray channelsArray = json
									.getJSONArray(Constants.EXTRA_CHANNELS);

							for (int i = 0; i < channelsArray.length(); i++) {
								String channel = channelsArray.getString(i);
								Log.d(TAG, "Channel: " + channel);
								if (listenToChannels.contains(channel)) {
//									Iterator itr = json.keys();
//									while (itr.hasNext()) {
//										String key = (String) itr.next();
//										Log.d(TAG,
//												"..." + key + " => "
//														+ json.getString(key));
//									}

//									parse.incrementTurnouts();
//	            					display.newMessage(message);
	            					
									// send message to Turios
									Intent msgIntent = new Intent(
											Constants.INTENT_INCOMMING_MESSAGE);
									msgIntent.putExtra(Constants.EXTRA_MESSAGE,
											message);
									context.sendBroadcast(msgIntent);
								}
							}

						} catch (JSONException e1) {
							Log.d(TAG, "JSONException: " + e1.getMessage());
						}
					}
				});
			}

			@Override
			public void failed(ParseException e) {
				Log.d(TAG, "JSONException: " + e.getMessage());

			}
		});

	}
}
