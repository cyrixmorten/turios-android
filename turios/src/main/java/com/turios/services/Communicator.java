package com.turios.services;

import java.util.List;

import com.turios.sms.SMSReceiver;
import com.turios.sms.TuriosMessageReceiver;
import com.turios.sms.ParseSMSReceiver;
import com.turios.util.Constants;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

public class Communicator extends Service
{
	
	private final String TAG = this.getClass().getSimpleName();
	
    private SMSReceiver mSMSReceiver;
    private ParseSMSReceiver mParseReceiver;
    private TuriosMessageReceiver mTuriosMessageReceiver;
    private IntentFilter mIntentFilter;

    @Override
    public void onCreate()
    {
        super.onCreate();

        
        Log.i(TAG, "Communicator started");
        //SMS event receiver
        mSMSReceiver = new SMSReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        mIntentFilter.setPriority(2147483647);
        registerReceiver(mSMSReceiver, mIntentFilter);
        
        // Parse message receiver
        mParseReceiver = new ParseSMSReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Constants.INTENT_INCOMMING_PARSESMS);
        registerReceiver(mParseReceiver, mIntentFilter);
        Log.i(TAG, "ParseSMSReceiver intialised!");
        
        
        mTuriosMessageReceiver = new TuriosMessageReceiver();
        mIntentFilter = new IntentFilter(Constants.INTENT_INCOMMING_MESSAGE);
        registerReceiver(mTuriosMessageReceiver, mIntentFilter);
        Log.i(TAG, "TuriosMessageReceiver intialised!");
        
//        Intent intent = new Intent("android.provider.Telephony.SMS_RECEIVED");
//        List<ResolveInfo> infos = getPackageManager().queryBroadcastReceivers(intent, 0);
//        for (ResolveInfo info : infos) {
//        	Log.i(TAG, "Receiver name:" + info.activityInfo.name + "; priority=" + info.priority);
//        }
    }
 
    @Override
    public void onDestroy()
    {
        super.onDestroy();

        Log.i(TAG, "Communicator stopped");
        
//        unregisterReceiver(mSMSReceiver);
        unregisterReceiver(mParseReceiver);
        unregisterReceiver(mTuriosMessageReceiver);
    }

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
//	private class SMSReceiver extends BroadcastReceiver
//	{
//	    private final String TAG = this.getClass().getSimpleName();
//
//	    @Override
//	    public void onReceive(Context context, Intent intent)
//	    {
//	        Bundle extras = intent.getExtras();
//
//	        
//
//	        if ( extras != null )
//	        {
//	            Object[] smsextras = (Object[]) extras.get( "pdus" );
//
//	            for ( int i = 0; i < smsextras.length; i++ )
//	            {
//	                SmsMessage smsmsg = SmsMessage.createFromPdu((byte[])smsextras[i]);
//
//	                final String strMsgBody = smsmsg.getMessageBody().toString();
//	                final String strMsgSrc = smsmsg.getOriginatingAddress();
//
////	                strMessage += "SMS from " + strMsgSrc + " : " + strMsgBody;                    
//
////	                Log.i(TAG, strMessage);
//	                
//	                if (strMsgBody.contains(Constants.DELIMITER)) {
//	                	
//	                	// send SMS to Turios
//	                	Intent msgIntent = new Intent(Constants.INTENT_INCOMMING_SMS);
//	                	msgIntent.putExtra(Constants.EXTRA_MESSAGE, strMsgBody);
//	                	msgIntent.putExtra(Constants.EXTRA_SENDER, strMsgSrc);
//	                	sendBroadcast(msgIntent);
//	                	
//	                		
//	                	this.abortBroadcast();
//	                }
//	            }
//	            
//	            
//
//	        }
//
//	    }
//
//	}
}