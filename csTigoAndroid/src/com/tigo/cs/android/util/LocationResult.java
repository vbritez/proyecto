package com.tigo.cs.android.util;

import android.location.Location;

import com.tigo.cs.android.service.LocationService.ResultManager;

/**
 * 
 * Tarea que es ejecutada una vez que el timer llegue a su delayTime
 * 
 * @author Miguel Maciel
 * 
 */
public abstract class LocationResult {

    protected ResultManager resultManager;

    protected boolean notifyProviderDisabled = true;

    public void cancelNotify() {
        notifyProviderDisabled = false;
    }

    public boolean isNotifyProviderDisabled() {
        return notifyProviderDisabled;
    }

    public void setNotifyProviderDisabled(boolean notifyProviderDisabled) {
        this.notifyProviderDisabled = notifyProviderDisabled;
    }

    public abstract void gotLocation(Location location);

    public ResultManager getResultManager() {
        return resultManager;
    }

    public void setResultManager(ResultManager resultManager) {
        this.resultManager = resultManager;
    }

}
