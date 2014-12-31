package com.tigo.cs.android.activity;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.messagehistory.MessageHistoryActivity;
import com.tigo.cs.android.helper.domain.LocationEntity;
import com.tigo.cs.android.helper.domain.MessageEntity;
import com.tigo.cs.android.helper.domain.ServiceEntity;
import com.tigo.cs.android.helper.domain.ServiceEventEntity;
import com.tigo.cs.android.service.LocationService;
import com.tigo.cs.android.util.CSTigoNotificationID;
import com.tigo.cs.android.util.LocationResult;
import com.tigo.cs.android.util.LocationUtil;
import com.tigo.cs.android.util.MessageState;
import com.tigo.cs.android.util.Notifier;
import com.tigo.cs.api.entities.BaseServiceBean;

public abstract class AbstractFragmentActivity extends FragmentActivity {

    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";

    protected ServiceEntity service;
    protected ServiceEventEntity serviceEvent;
    protected LocationManager locationManager;
    protected TelephonyManager telephonyManager;
    protected Messenger messenger;
    protected boolean mBounded;

    public static final Long ONE_MINUTE = 1 * 60 * 1000L;
    protected BaseServiceBean entity;
    private static boolean searching = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        if (telephonyManager == null) {
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        }

        if (service == null && getServicecod() != null) {
            service = CsTigoApplication.getServiceHelper().findByServiceCod(
                    getServicecod());
        }
        if (serviceEvent == null && getServicecod() != null
            && getServiceEventCod() != null) {
            serviceEvent = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                    getServicecod(), getServiceEventCod());
        }
        verifyLocation();

    }

    /**
     * Metodo generico para el envio de mensajes al numero corto configurado
     * para la plataforma
     * 
     * @param message
     */

    protected void goToMain() {
        Intent goToMainActivity = new Intent(this, MainActivity.class);
        startActivity(goToMainActivity);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    protected void onSetContentViewFinish() {

        ImageView tigo = (ImageView) findViewById(R.id.tigoHeaderImage);
        tigo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (verifyDeviceEnabled()) {
                    startActivity(new Intent(AbstractFragmentActivity.this, MainActivity.class));
                    CsTigoApplication.vibrate(false);
                }
            }
        });

        TextView tigoTitle = (TextView) findViewById(R.id.tigoHeaderTitle);
        tigoTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (verifyDeviceEnabled()) {
                    startActivity(new Intent(AbstractFragmentActivity.this, MainActivity.class));
                    CsTigoApplication.vibrate(false);
                }
            }
        });

        ImageView consultas = (ImageView) findViewById(R.id.im_metadata_query);
        consultas.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (verifyDeviceEnabled()) {
                    startActivity(new Intent(AbstractFragmentActivity.this, UserDataActivity.class));
                    CsTigoApplication.vibrate(false);
                }
            }
        });

        ImageView mensajes = (ImageView) findViewById(R.id.im_message);
        mensajes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (verifyDeviceEnabled()) {
                    startActivity(new Intent(AbstractFragmentActivity.this, MessageHistoryActivity.class));
                    CsTigoApplication.vibrate(false);
                }
            }
        });
        ImageView opciones = (ImageView) findViewById(R.id.im_options);
        opciones.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(AbstractFragmentActivity.this, PlatformUpdateActivity.class));
                CsTigoApplication.vibrate(false);
            }
        });
    }

    private boolean verifyDeviceEnabled() {
        /*
         * verificamos si el dispositivo se encuentra habilitado antes de
         * solicitar la actualizacion de servicios
         */

        if (!CsTigoApplication.getGlobalParameterHelper().getDeviceEnabled()) {

            /*
             * notificamos que el dispositivo no esta habilitado
             */
            Toast.makeText(
                    AbstractFragmentActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.no_enabled_device), Toast.LENGTH_LONG).show();
            return false;

        }
        return true;
    }

    protected boolean startUserMark() {
        Locale.setDefault(Locale.US);

        entity.setRequiresLocation(serviceEvent.getRequiresLocation());
        if (Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("1")) {
            Toast.makeText(
                    AbstractFragmentActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.no_location), Toast.LENGTH_LONG).show();
            return false;
        }

        if (!locationManager.isProviderEnabled((LocationManager.GPS_PROVIDER))) {

            CsTigoApplication.showNotification(
                    CSTigoNotificationID.LOCATION,
                    CsTigoApplication.getContext().getString(
                            R.string.no_location_title),
                    CsTigoApplication.getContext().getString(
                            R.string.no_location_notif),
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

            return false;

        }
        if (!validateUserInput()) {
            return false;
        }
        return true;

    }

    protected Long persistMessage(String msg, GsmCellLocation cellLocation) {
        MessageEntity message = new MessageEntity();
        message.setEventDate(new Date());
        message.setMessage(msg);
        message.setService(service);
        message.setState(MessageState.PREPARED_TO_SEND.ordinal());
        return CsTigoApplication.getMessageHelper().insert(message);
    }

    protected void showServiceMarkNotification() {
        CsTigoApplication.showNotification(
                CSTigoNotificationID.SERVICE_MESSAGE,
                CsTigoApplication.getContext().getString(
                        R.string.processing_detail),
                CsTigoApplication.getContext().getString(
                        R.string.processing_detail), MainActivity.class);
    }

    protected void endUserMark(String message, GsmCellLocation cellLocation) {

        Notifier.info(getClass(),
                "Se persistira el mensaje antes en la base de datos interna.");
        Long messageId = persistMessage(message, cellLocation);

        Notifier.info(getClass(), "Mensaje persistido en la base de datos");

        Message msg = Message.obtain(null, 0, new ClientHandle());

        Notifier.info(getClass(),
                "Se obtiene mensajero para el envio de mensajes al servicio.");
        try {

            entity.setMessageId(messageId);

            msg.obj = entity;
            msg.what = LocationService.LOCATE_AND_SEND;

            Notifier.info(getClass(),
                    "Seteados los parametros, se enviara el mensaje al servicio.");
            messenger.send(msg);

            Notifier.info(getClass(), "Mensaje enviado al servicio.");

            Toast.makeText(CsTigoApplication.getContext(),
                    R.string.sending_message, Toast.LENGTH_LONG).show();

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        goToMain();
    }

    protected boolean validateUserInput() {
        return false;
    }

    public Integer getServicecod() {
        return null;
    }

    public String getServiceEventCod() {
        return null;
    }

    protected void startEventActivity(Class<? extends Activity> activityClass) {
        startEventActivity(activityClass, null);
    }

    public void startEventActivity(Class<? extends Activity> activityClass, HashMap<String, String> extras) {
        Intent i = new Intent(this, activityClass);
        if (extras != null) {
            for (String value : extras.keySet()) {
                i.putExtra(value, extras.get(value));
            }
        }
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyLocation();
        bindService(new Intent(this, LocationService.class), mConnection,
                BIND_AUTO_CREATE);
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        verifyLocation();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        verifyLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBounded) {
            unbindService(mConnection);
        }
    }

    public class ClientHandle extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            messenger = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = new Messenger(service);
            mBounded = true;

            Message msg = Message.obtain(null, 0, new ClientHandle());
            try {
                msg.what = LocationService.ALARM;
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

    };

    LocationResult locationResult = new LocationResult() {

        @Override
        public boolean isNotifyProviderDisabled() {
            return false;
        }

        @Override
        public void gotLocation(final Location location) {
            searching = false;
        }

    };

    protected void verifyLocation() {
        if (CsTigoApplication.getGlobalParameterHelper().getDeviceEnabled()) {

            if (!searching) {
                /*
                 * antes de invocar al WS verificamos la ultima localizacion GPS
                 * del movil si esta dentro de los ultimos 2 minutos
                 */

                LocationEntity locationEntity = CsTigoApplication.getLocationHelper().findLast();
                if (locationEntity != null) {
                    Long locationTime = locationEntity.getDateTime().getTime();
                    Long nowTime = new Date().getTime();
                    if (nowTime - locationTime > ONE_MINUTE) {
                        searching = true;
                        LocationUtil locationUtil = new LocationUtil();
                        locationUtil.getLocation(
                                CsTigoApplication.getContext(), locationResult);
                    }
                } else {
                    searching = true;
                    LocationUtil locationUtil = new LocationUtil();
                    locationUtil.getLocation(CsTigoApplication.getContext(),
                            locationResult);
                }
            }
        }
    }

}
