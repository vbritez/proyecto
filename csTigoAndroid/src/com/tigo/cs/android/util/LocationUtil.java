package com.tigo.cs.android.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.helper.domain.LocationEntity;

public class LocationUtil {

    private Timer locationTimeoutTimer;
    private LocationManager locationManeger;
    private LocationResult locationResult;
    private boolean gpsEnabled = false;

    public LocationUtil() {

    }

    /**
     * obtenemos la localizacion del dispositivo, registrando un timer que
     * verificara luego de un determinado tiempo si se encontro una nueva
     * localizacion para el dispositivo y notifica del mismo a traves del
     * locationResult
     * 
     * 
     * @param context
     * @param locationResult
     * @return estado de registro de los listener y timer
     */
    public boolean getLocation(Context context, LocationResult locationResult) {

        this.locationResult = locationResult;

        /*
         * obtenemos el location manager
         */
        if (locationManeger == null) {
            locationManeger = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        /*
         * verificamos que tenga habilitada la localizacion a traves de GPS
         */
        try {
            gpsEnabled = locationManeger.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Notifier.error(getClass(),
                    "Device doesn't have location through GPS enabled");
            return false;
        }

        /*
         * finalmente registramos el listener con su respectivo handler para
         * tratar los nuevos puntos recibidos
         */
        Boolean allowMock = true;
        if (Settings.Secure.getString(
                CsTigoApplication.getContext().getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")) {
            allowMock = false;
        }

        Notifier.info(getClass(), "ALLOW_MOCK_LOCATION="
            + (allowMock ? "true" : "false"));

        if (gpsEnabled && !allowMock) {

            Notifier.info(getClass(),
                    "Device has GPS enabled and AllowMock is false.");

            if (Looper.myLooper() == null) {
                Looper.prepare();
            }

            Integer SECONDS = CsTigoApplication.getGlobalParameterHelper().getGpsTimeout();
            Integer METERS = CsTigoApplication.getGlobalParameterHelper().getGpsAccuracyRangeMeters();

            Notifier.info(getClass(), "Seconds to wait for GPS localization: "
                + SECONDS);
            Notifier.info(getClass(), "Gps Accuracy Meters: " + METERS);

            locationManeger.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, SECONDS, METERS,
                    locationListener);
            locationTimeoutTimer = new Timer();

            locationTimeoutTimer.schedule(new GetLastLocation(), SECONDS);
            return true;
        } else {
            locationResult.gotLocation(null);
        }

        return false;
    }

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

            if (location != null) {
                if (location.hasAccuracy()) {
                    if (location.getAccuracy() >= Float.valueOf(CsTigoApplication.getGlobalParameterHelper().getGpsAccuracyMeters())) {
                        return;
                    }
                }
            }

            /*
             * cancelamos el timer, notificamos de una nueva localizacion y
             * removemos el listener que actual del locationManager
             */

            TelephonyManager telephonyManager = (TelephonyManager) CsTigoApplication.getContext().getSystemService(
                    Context.TELEPHONY_SERVICE);
            GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();

            Notifier.info(getClass(), "Got the location.");

            LocationEntity locationEntity = new LocationEntity();
            locationEntity.setLatitude(location.getLatitude());
            locationEntity.setLongitude(location.getLongitude());
            locationEntity.setAccuracy(location.getAccuracy());
            locationEntity.setAltitude(location.getAltitude());
            locationEntity.setBearing(location.getBearing());
            locationEntity.setCellId(cellLocation.getCid());
            locationEntity.setDateTime(new Date(location.getTime()));
            if (cellLocation != null) {
                locationEntity.setCellId(cellLocation.getCid());
                locationEntity.setLac(cellLocation.getLac());
            }
            locationEntity.setProvider(location.getProvider());
            locationEntity.setSpeed(location.getSpeed());

            CsTigoApplication.getLocationHelper().insert(locationEntity);

            locationResult.gotLocation(location);
            locationTimeoutTimer.cancel();
            locationManeger.removeUpdates(this);
        }

        @Override
        public void onProviderDisabled(String provider) {
            if (locationResult.isNotifyProviderDisabled()) {
                Notifier.info(
                        getClass(),
                        CsTigoApplication.getContext().getString(
                                R.string.no_location_disabled_on_listening));

                CsTigoApplication.showNotification(
                        CSTigoNotificationID.LOCATION,
                        CsTigoApplication.getContext().getString(
                                R.string.no_location_title),
                        CsTigoApplication.getContext().getString(
                                R.string.no_location_disabled_on_listening),
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                locationTimeoutTimer.cancel();
                locationManeger.removeUpdates(this);
            } else {
                locationResult.gotLocation(null);
                locationTimeoutTimer.cancel();
                locationManeger.removeUpdates(this);
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    /**
     * 
     * Tarea que es ejecutada una vez que el timer llegue a su delayTime
     * 
     * @author Miguel Maciel
     * 
     */
    class GetLastLocation extends TimerTask {

        @Override
        public void run() {
            /*
             * una vez que llegue el delay time del timer removemos el listener
             * del location manager
             */

            Notifier.info(getClass(),
                    "Could not find GPS localization. Get the last known localization.");

            locationManeger.removeUpdates(locationListener);

            /*
             * obtenemos la ultima localizacion conocida para el provider y
             * notificamos al locationResult
             */
            Location deviceLocation = null;

            boolean gpsEnabled = false;
            LocationManager locationManager = null;

            if (locationManager == null) {
                locationManager = (LocationManager) CsTigoApplication.getContext().getSystemService(
                        Context.LOCATION_SERVICE);
            }

            try {
                gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
                gpsEnabled = false;
            }

            Notifier.info(getClass(), "gpsEnabled: "
                + (gpsEnabled ? "true" : "false"));

            if (gpsEnabled) {
                deviceLocation = locationManeger.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (deviceLocation != null) {

                Notifier.info(getClass(), "Last known location latitud: "
                    + deviceLocation.getLatitude() + " longitud: "
                    + deviceLocation.getLongitude());

                if (deviceLocation.hasAccuracy()
                    && deviceLocation.getAccuracy() <= CsTigoApplication.getGlobalParameterHelper().getGpsMaxAccuracyMeters()) {
                    Long now = new Date().getTime();
                    Long delta = now - deviceLocation.getTime();
                    if (delta <= CsTigoApplication.getGlobalParameterHelper().getTrackingTimeout()) {

                        Notifier.info(getClass(),
                                "Delta <= TrackingTimeOut. We send the location");
                        locationResult.gotLocation(deviceLocation);
                    } else {
                        Notifier.info(getClass(),
                                "Delta > TrackingTimeOut. We send a NULL value in Location.");
                        locationResult.gotLocation(null);
                    }

                } else {
                    Notifier.info(
                            getClass(),
                            "Device location doesn't have accuracy or accuracy of device > GpsAccuracyGlobalParameter.");
                    locationResult.gotLocation(null);
                }
                return;
            } else {
                Notifier.info(getClass(), "deviceLocation is null.");
                locationResult.gotLocation(null);
            }
        }

    }

}
