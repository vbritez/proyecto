package com.tigo.cs.android.service;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.tigo.cs.android.CsTigoApplication;

public class LocationServiceUtil {

    private static Context context;

    /*
     * atributos utilizados para la localizacion
     */
    private static int SECONDS;
    private static int METERS;

    /*
     * atributos utilizados para manejar la localizacion del dispositivo a
     * traves de la red o por GPS
     */
    private static LocationManager locationManager;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        LocationServiceUtil.context = context;
    }

    public static LocationManager getLocationManager() {
        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        return locationManager;
    }

    public static void removeUpdatesFromProvider(CSTigoLocationListener listener) {
        getLocationManager().removeUpdates(listener);
    }

    public static Location requestUpdatesFromProvider(CSTigoLocationListener listener) {

        SECONDS = CsTigoApplication.getGlobalParameterHelper().getGpsTimeout();
        METERS = CsTigoApplication.getGlobalParameterHelper().getGpsAccuracyMeters();

        Location location = null;
        String provider = LocationManager.GPS_PROVIDER;
        if (getLocationManager().isProviderEnabled(provider)) {
            getLocationManager().requestLocationUpdates(provider, SECONDS,
                    METERS, listener);
            location = getLocationManager().getLastKnownLocation(provider);
        }
        return location;
    }

}
