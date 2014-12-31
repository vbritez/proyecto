package com.tigo.cs.android.receiver;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.helper.domain.LocationEntity;
import com.tigo.cs.android.helper.domain.MessageEntity;
import com.tigo.cs.android.util.CSTigoNotificationID;
import com.tigo.cs.android.util.LocationResult;
import com.tigo.cs.android.util.LocationUtil;
import com.tigo.cs.android.util.MessageState;
import com.tigo.cs.android.util.Notifier;

public class LocatorReceiver extends BroadcastReceiver {

    private LocationManager locationManager;
    private boolean gpsEnabled = false;
    public static final Long ONE_MINUTE = 1 * 60 * 1000L;

    LocationResult locationResult = new LocationResult() {

        @Override
        public boolean isNotifyProviderDisabled() {
            return false;
        }

        @Override
        public void gotLocation(final Location location) {

        }

    };

    @Override
    public void onReceive(Context context, Intent intent) {

        /*
         * obtenemos el location manager
         */
        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        /*
         * verificamos que tenga habilitada la localizacion a traves de GPS
         */
        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {

            CsTigoApplication.showNotification(
                    CSTigoNotificationID.LOCATION,
                    CsTigoApplication.getContext().getString(
                            R.string.no_location_title),
                    CsTigoApplication.getContext().getString(
                            R.string.must_enable_gps),
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

            MessageEntity newMessageEntity = new MessageEntity();
            newMessageEntity.setEventDate(new Date());
            newMessageEntity.setMessage(CsTigoApplication.getContext().getString(
                    R.string.no_location_title)
                + " "
                + CsTigoApplication.getContext().getString(
                        R.string.must_enable_gps));
            newMessageEntity.setState(MessageState.RECEIVED.ordinal());
            CsTigoApplication.getMessageHelper().insert(newMessageEntity);

            Notifier.error(getClass(),
                    "Device doesn't have location through GPS enabled");
            // TODO:PLAY SOUND
        }

        class MyPhoneStateListener extends PhoneStateListener {

            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                CsTigoApplication.setSignalStrength(String.valueOf(signalStrength.getGsmSignalStrength()));
            }

        }

        MyPhoneStateListener mylistener = new MyPhoneStateListener();
        TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        tel.listen(mylistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        LocationEntity locationEntity = CsTigoApplication.getLocationHelper().findLast();
        if (locationEntity != null) {
            Long locationTime = locationEntity.getDateTime().getTime();
            Long nowTime = new Date().getTime();
            if (nowTime - locationTime > ONE_MINUTE) {

                LocationUtil locationUtil = new LocationUtil();
                locationUtil.getLocation(CsTigoApplication.getContext(),
                        locationResult);
            }
        } else {

            LocationUtil locationUtil = new LocationUtil();
            locationUtil.getLocation(CsTigoApplication.getContext(),
                    locationResult);
        }

    }

}