package com.turios.sms;

import com.turios.services.Communicator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootCommunicatorReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent smsService = new Intent(context, Communicator.class);
		context.startService(smsService);
	}

}
