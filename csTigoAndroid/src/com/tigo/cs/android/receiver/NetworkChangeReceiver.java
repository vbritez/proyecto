package com.tigo.cs.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tigo.cs.android.service.LocationService;
import com.tigo.cs.android.util.Notifier;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            context.startService(new Intent(context, LocationService.class));
        } catch (Exception e) {
            Notifier.info(getClass(), "Error on initialization");
        }
    }

}
