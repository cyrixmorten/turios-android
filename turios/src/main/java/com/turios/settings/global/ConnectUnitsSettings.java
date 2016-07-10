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
import com.turios.R;
import com.turios.dagger.DaggerPreferenceFragment;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.core.parse.ParseObjectHelper;

public class ConnectUnitsSettings extends DaggerPreferenceFragment {

	private static final String TAG = ConnectUnitsSettings.class
			.getSimpleName();

	private PreferenceCategory sendCat;
	private PreferenceCategory listenCat;

	private List<ParseObject> channels;

	private List<SwitchPreference> sendSwitchPrefs;
	private List<SwitchPreference> listenSwitchPrefs;

	private List<ParseObject> channelSendRelations;
	private List<ParseObject> channelListenRelations;

	@Inject ParseCoreModule parse;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings_connectunits);

		setHasOptionsMenu(true);

		sendCat = (PreferenceCategory) findPreference(getString(R.string.category1));
		listenCat = (PreferenceCategory) findPreference(getString(R.string.category2));

	}

	@Override public void onStart() {
		create();
		super.onStart();
	}

	private void create() {
		sendCat.removeAll();
		listenCat.removeAll();
		sendSwitchPrefs = new ArrayList<SwitchPreference>();
		listenSwitchPrefs = new ArrayList<SwitchPreference>();

		parse.getChannels(new FindCallback<ParseObject>() {

			@Override public void done(List<ParseObject> objects,
					ParseException e) {
				if (e == null) {
					channels = objects;
					for (ParseObject channel : channels) {
						addChannel(channel);
					}

					parse.getChannelSendRelation(new FindCallback<ParseObject>() {

						@Override public void done(List<ParseObject> objects,
								ParseException e) {
							if (e == null) {
								channelSendRelations = objects;
								Log.d(TAG, "channelSendRelation");
								for (ParseObject channelSendRelation : channelSendRelations) {
									Log.d(TAG, "" + channelSendRelation);
									selectSendChannel(channelSendRelation);
								}
							}

						}

					});

					parse.getChannelListenRelation(new FindCallback<ParseObject>() {

						@Override public void done(List<ParseObject> objects,
								ParseException e) {
							if (e == null) {
								channelListenRelations = objects;
								Log.d(TAG, "channelListenRelation");
								for (ParseObject channelListenRelation : channelListenRelations) {
									Log.d(TAG, "" + channelListenRelation);
									selectListenChannel(channelListenRelation);
								}
							}
						}

					});

				}

			}
		});
	}

	private int indexOfChannel(ParseObject channel) {
		for (int i = 0; i < channels.size(); i++) {
			if (channels.get(i).getObjectId().equals(channel.getObjectId())) {
				return i;
			}
		}
		return -1;
	}

	private void selectSendChannel(ParseObject channel) {
		sendSwitchPrefs.get(indexOfChannel(channel)).setChecked(true);
	}

	private void selectListenChannel(ParseObject channel) {
		listenSwitchPrefs.get(indexOfChannel(channel)).setChecked(true);
	}

	private void addChannel(ParseObject channel) {
		addSendChannel(channel);
		addListenChannel(channel);
	}

	private void addSendChannel(ParseObject channel) {
		SwitchPreference switchPref = new SwitchPreference(getActivity());
		switchPref.setTitle(channel.getString(ParseObjectHelper.Channel.name));
		switchPref.setOrder(sendSwitchPrefs.size());
		switchPref.setOnPreferenceChangeListener(new OnSendChanged());
		sendSwitchPrefs.add(switchPref);

		sendCat.addPreference(switchPref);
	}

	private void addListenChannel(ParseObject channel) {
		SwitchPreference switchPref = new SwitchPreference(getActivity());
		switchPref.setTitle(channel.getString(ParseObjectHelper.Channel.name));
		switchPref.setOrder(listenSwitchPrefs.size());
		switchPref.setOnPreferenceChangeListener(new OnListenChanged());
		listenSwitchPrefs.add(switchPref);

		listenCat.addPreference(switchPref);
	}

	private void deleteChannel(int index) {
		final ParseObject channel = channels.get(index);
		final SwitchPreference sendSwitchPref = sendSwitchPrefs.get(index);
		final SwitchPreference listenSwitchPref = listenSwitchPrefs.get(index);
		parse.deleteChannel(channel, new DeleteCallback() {

			@Override public void done(ParseException e) {
				if (e == null) {
					channels.remove(channel);
					channelListenRelations.remove(channel);
					channelSendRelations.remove(channel);
					sendSwitchPrefs.remove(sendSwitchPref);
					listenSwitchPrefs.remove(listenSwitchPref);
					sendCat.removePreference(sendSwitchPref);
					listenCat.removePreference(listenSwitchPref);
					Toast.makeText(getActivity(),
							getString(R.string.channel_deleted),
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(),
							getString(R.string.error_deleting),
							Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	private class OnSendChanged implements OnPreferenceChangeListener {

		@Override public boolean onPreferenceChange(Preference preference,
				Object newValue) {

			if (!sendSwitchPrefs.contains(preference))
				return false;
			Log.d(TAG, "OnSendChanged");

			ParseObject channel = channels.get(sendSwitchPrefs
					.indexOf(preference));
			final String channelname = channel
					.getString(ParseObjectHelper.Channel.name);
			if ((Boolean) newValue == true
					&& !channelSendRelations.contains(channel)) {
				// select
				Log.d(TAG, "Select channelSendRelation");
				parse.addChannelSendRelation(channel,
						new GetCallback<ParseObject>() {

							@Override public void done(ParseObject channel,
									ParseException e) {
								if (e == null) {
									// update local data
									channelSendRelations.add(channel);
//									Toast.makeText(
//											getActivity(),
//											getString(
//													R.string.channel_sending_to,
//													channelname),
//											Toast.LENGTH_SHORT).show();
								} else {
									deselectSendChannel(channel);
									android.util.Log.e(TAG, e.getMessage(), e);
									Toast.makeText(
											getActivity(),
											getString(R.string.missing_internet_connection),
											Toast.LENGTH_SHORT).show();
								}
							}
						});
			}
			if ((Boolean) newValue == false) {
				// deselect
				Log.d(TAG, "Deselect channelSendRelation");
				parse.removeChannelSendRelation(channel,
						new GetCallback<ParseObject>() {

							@Override public void done(ParseObject channel,
									ParseException e) {
								if (e == null) {
									// update local data
									channelSendRelations.remove(channel);
//									Toast.makeText(
//											getActivity(),
//											getString(
//													R.string.channel_not_sending_to,
//													channelname),
//											Toast.LENGTH_SHORT).show();
								} else {
									selectSendChannel(channel);
									android.util.Log.e(TAG, e.getMessage(), e);
									Toast.makeText(
											getActivity(),
											getString(R.string.missing_internet_connection),
											Toast.LENGTH_SHORT).show();
								}
							}
						});
			}
			return true;
		}

		private void deselectSendChannel(ParseObject channel) {
			sendSwitchPrefs.get(indexOfChannel(channel)).setChecked(false);
		}

	};

	private class OnListenChanged implements OnPreferenceChangeListener {

		@Override public boolean onPreferenceChange(Preference preference,
				Object newValue) {

			if (!listenSwitchPrefs.contains(preference))
				return false;

			ParseObject channel = channels.get(listenSwitchPrefs
					.indexOf(preference));
			final String channelname = channel
					.getString(ParseObjectHelper.Channel.name);
			if ((Boolean) newValue == true
					&& !channelListenRelations.contains(channel)) {
				// select
				parse.addChannelListenRelation(channel,
						new GetCallback<ParseObject>() {

							@Override public void done(ParseObject channel,
									ParseException e) {
								if (e == null) {
									// update local data
									channelListenRelations.add(channel);
//									Toast.makeText(
//											getActivity(),
//											getString(
//													R.string.channel_listening_to,
//													channelname),
//											Toast.LENGTH_SHORT).show();
								} else {
									deselectListenChannel(channel);
									android.util.Log.e(TAG, e.getMessage(), e);
									Toast.makeText(
											getActivity(),
											getString(R.string.missing_internet_connection),
											Toast.LENGTH_SHORT).show();
								}
							}
						});
			}
			if ((Boolean) newValue == false) {
				// deselect
				parse.removeChannelListenRelation(channel,
						new GetCallback<ParseObject>() {

							@Override public void done(ParseObject channel,
									ParseException e) {
								if (e == null) {
									// update local data
									channelListenRelations.remove(channel);
//									Toast.makeText(
//											getActivity(),
//											getString(
//													R.string.channel_not_listening_to,
//													channelname),
//											Toast.LENGTH_SHORT).show();
								} else {
									selectListenChannel(channel);
									android.util.Log.e(TAG, e.getMessage(), e);
									Toast.makeText(
											getActivity(),
											getString(R.string.missing_internet_connection),
											Toast.LENGTH_SHORT).show();
								}
							}
						});
			}
			return true;
		}

		private void deselectListenChannel(ParseObject channel) {
			listenSwitchPrefs.get(indexOfChannel(channel)).setChecked(false);
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
					.setTitle(R.string.channel_create)
					.setIcon(android.R.drawable.ic_menu_add)
					.setView(editText)
					.setPositiveButton(android.R.string.ok,
							new OnClickListener() {
								@Override public void onClick(
										DialogInterface dialog, int which) {

									final String editTextString = editText
											.getText().toString();
									if (!editTextString.isEmpty()) {

										parse.createChannel(editTextString,
												new GetCallback<ParseObject>() {

													@Override public void done(
															ParseObject channel,
															ParseException e) {
														if (e == null) {
															addChannel(channel);
															channels.add(channel);
//															Toast.makeText(
//																	getActivity(),
//																	getString(R.string.channel_created),
//																	Toast.LENGTH_SHORT)
//																	.show();
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

			final String[] channelnames = new String[channels.size()];
			int index = 0;
			for (SwitchPreference switchPref : sendSwitchPrefs) {
				channelnames[index] = switchPref.getTitle().toString();
				index++;
			}

			new AlertDialog.Builder(getActivity())
					.setTitle(R.string.profile_delete)
					.setIcon(android.R.drawable.ic_menu_delete)
					.setItems(channelnames, new OnClickListener() {

						@Override public void onClick(DialogInterface dialog,
								int which) {
							final int channelindex = which;
							new AlertDialog.Builder(getActivity())
									.setTitle(
											android.R.string.dialog_alert_title)
									.setIcon(android.R.drawable.ic_dialog_alert)
									.setMessage(
											getString(R.string.confirm_delete,
													channelnames[channelindex]))
									.setPositiveButton(android.R.string.ok,
											new OnClickListener() {

												@Override public void onClick(
														DialogInterface dialog,
														int which) {
													deleteChannel(channelindex);
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