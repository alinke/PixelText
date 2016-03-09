package com.ledpixelart.pixel.scrolling.text.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
	
    private String TAG = SmsReceiver.class.getSimpleName();
    private ScrollingTextActivity scrollingtextactivity_;
 
    public SmsReceiver() {
	  super();  
    }
    
    public SmsReceiver(ScrollingTextActivity aScrollingTextActivity) { //thank god for the Internet! avoided having to make scrolltext in the main class static using this advice https://dzone.com/articles/why-static-bad-and-how-avoid
    	scrollingtextactivity_ = aScrollingTextActivity;
    }
 
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the data (SMS data) bound to intent
        Bundle bundle = intent.getExtras();
 
        SmsMessage[] msgs = null;
 
        String str = "";
 
        if (bundle != null) {
            // Retrieve the SMS Messages received
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
 
            // For every SMS message received
            for (int i=0; i < msgs.length; i++) {
                // Convert Object array
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                // Sender's phone number
                //str += "SMS from " + msgs[i].getOriginatingAddress() + " : ";
                // Fetch the text message
                str += msgs[i].getMessageBody().toString();
                // Newline <img src="http://codetheory.in/wp-includes/images/smilies/simple-smile.png" alt=":-)" class="wp-smiley" style="height: 1em; max-height: 1em;">
                str += "\n";
            }
 
            // Display the entire SMS Message
            Log.d(TAG, str);
            int duration = Toast.LENGTH_LONG;
            
            if (ScrollingTextActivity.debug_ == true) {
            	 Toast toast = Toast.makeText(context, 
                "senderNum: "+ str + ", message: " + str, duration);
   				toast.show();
            }
            
            if (scrollingtextactivity_.displayIncomingSMS_) {  //scroll the message if on
            	scrollingtextactivity_.textField.setText(str);
            }
            
         
            
        }
    }
}