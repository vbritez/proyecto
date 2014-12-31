package com.tigo.cs.android.service;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class CSTigoLocationListener implements LocationListener {

    private Handler handler;

    public CSTigoLocationListener() {
    }

    public CSTigoLocationListener(Handler handler) {
        this.handler = handler;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onLocationChanged(Location location) {
        Message.obtain(handler, 0, location).sendToTarget();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}
