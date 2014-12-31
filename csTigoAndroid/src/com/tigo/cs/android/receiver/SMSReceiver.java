package com.tigo.cs.android.receiver;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Messenger;
import android.telephony.SmsMessage;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.util.Notifier;

public class SMSReceiver extends BroadcastReceiver {

    private String shortNumber;
    Messenger messenger = null;

    public String getShortNumber() throws Exception {
        if (shortNumber == null) {
            shortNumber = CsTigoApplication.getGlobalParameterHelper().getShortNumber();
        }
        return shortNumber;
    }

    public SMSReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String originAddress = null;

        Bundle pudsBundle = intent.getExtras();
        Object[] pdus = (Object[]) pudsBundle.get("pdus");
        String originatinAddress = null;

        SmsMessage[] msgs;
        Map<String, String> msg = null;
        if (pdus != null) {
            int nbrOfpdus = pdus.length;
            msg = new HashMap<String, String>(nbrOfpdus);
            msgs = new SmsMessage[nbrOfpdus];

            for (int i = 0; i < nbrOfpdus; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                originatinAddress = msgs[i].getOriginatingAddress();

                if (!msg.containsKey(originatinAddress)) {
                    msg.put(msgs[i].getOriginatingAddress(),
                            msgs[i].getMessageBody());

                } else {

                    String previousparts = msg.get(originatinAddress);
                    String msgString = previousparts + msgs[i].getMessageBody();
                    msg.put(originatinAddress, msgString);
                }
            }
        }

        if (originatinAddress.startsWith("+")) {
            originAddress = originatinAddress.substring(1);
        } else {
            originAddress = originatinAddress;
        }

        try {

            Notifier.info(getClass(), "originAddress: " + originAddress);
            Notifier.info(getClass(), "getShortNumber: " + getShortNumber());

            if (originAddress.compareTo(getShortNumber()) == 0) {

                String messageIn = msg.get(originatinAddress);

                if (CsTigoApplication.manage(messageIn)) {
                    abortBroadcast();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
