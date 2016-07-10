package com.turios.sms;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.turios.util.Constants;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

public class SMSSender extends IntentService {

	
	public SMSSender() {
		super("cyrix.turios.sms.SMSSender");
	}

	private final String TAG = "SendSMS";

	/**
	 * 
	 * Runtime unique Integer id
	 * 
	 * @author cyrix
	 * 
	 */
	private static class IDGenerator {

		private static final AtomicInteger counter = new AtomicInteger();

		public static int nextValue() {
			return counter.getAndIncrement();
		}
	}

	private void sendSMS(String message, String[] receivers) {

		SmsManager sm = SmsManager.getDefault();

		ArrayList<String> parts = sm.divideMessage(message);

		PendingIntent sentPI = null;
		PendingIntent deliveredPI = null;

		Intent sentIntent = new Intent(Constants.INTENT_MESSAGE_SENT);

		int sentID = IDGenerator.nextValue();
		sentPI = PendingIntent.getBroadcast(SMSSender.this, sentID, sentIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);


		Log.i(TAG, "sending SMS: parts: " + parts.size() + " message: "
				+ message);

		if (parts.size() > 1) {
			ArrayList<PendingIntent> sentIntents = null;
			ArrayList<PendingIntent> deliveredIntents = null;

			sentIntents = new ArrayList<PendingIntent>();
			deliveredIntents = new ArrayList<PendingIntent>();

			for (int i = 0; i < parts.size(); i++) {
				sentIntents.add(sentPI);
				deliveredIntents.add(deliveredPI);
			}

			for (String receiver : receivers) {
				try {
					sm.sendMultipartTextMessage(receiver, null, parts,
							sentIntents, deliveredIntents);
				} catch (IllegalArgumentException e) {
					Log.e(TAG, "illegal receiver: " + receiver);
				}

			}
		} else {
			for (String receiver : receivers) {
				try {
					sm.sendTextMessage(receiver, null, parts.get(0), sentPI,
							deliveredPI);
				} catch (IllegalArgumentException e) {
					Log.e(TAG, "illegal receiver: " + receiver);
				}
			}
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String message = intent.getStringExtra(Constants.EXTRA_MESSAGE);
		String[] receivers = intent.getStringArrayExtra(Constants.EXTRA_RECEIVERS);

		sendSMS(message, receivers);

	}
}