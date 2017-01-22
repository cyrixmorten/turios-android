package com.turios.sms;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseException;
import com.turios.R;
import com.turios.dagger.DaggerBroadcastReceiver;
import com.turios.modules.core.DisplayCoreModule;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.core.ParseCoreModule.ParseLoginCallback;
import com.turios.modules.extend.BasisModule;
import com.turios.util.Constants;
import com.turios.util.Notifications;

public class SMSReceiver extends DaggerBroadcastReceiver {

	private static final String TAG = SMSReceiver.class.getName();

	@Inject
	ParseCoreModule parse;
	@Inject
	DisplayCoreModule display;

	@Inject
	BasisModule basis;

	@Override
	public void onReceive(final Context context, Intent intent) {
		super.onReceive(context, intent);

		Log.d(TAG, "Received SMS!");
		
		Bundle extras = intent.getExtras();

		if (extras != null) {
			Object[] smsextras = (Object[]) extras.get("pdus");

			for (int i = 0; i < smsextras.length; i++) {
				SmsMessage smsmsg = SmsMessage
						.createFromPdu((byte[]) smsextras[i]);

				final String message = smsmsg.getMessageBody().toString();
				final String sender = smsmsg.getOriginatingAddress();

				// strMessage += "SMS from " + strMsgSrc + " : " + strMsgBody;

				// Log.i(TAG, strMessage);

				if (message.contains(Constants.DELIMITER)) {

					parse.login(new ParseLoginCallback() {

						@Override
						public void success() {

							if (basis.isEnabled()) {

								// send Push notification to all listeners via
								// Parse
								parse.pushSMSToSendRelations(sender, message);
								
								Intent intent = new Intent();
//								parse.incrementTurnouts();
//								display.newMessage(message);
								
								// send message to Turios
								Intent msgIntent = new Intent(
										Constants.INTENT_INCOMMING_MESSAGE);
								msgIntent.putExtra(Constants.EXTRA_MESSAGE,
										message);
								context.sendBroadcast(msgIntent);

							} else {
								Toast.makeText(
										context,
										context.getString(R.string.error_smsreceived_trial_expired),
										Toast.LENGTH_LONG).show();
							}
						}

						@Override
						public void failed(Exception e) {
							Log.d(TAG, "JSONException: " + e.getMessage());
						}
					});

					this.abortBroadcast();
				}
			}

		}

	}
}
