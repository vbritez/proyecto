package com.tigo.cs.android.service;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.messagehistory.MessageHistoryActivity;
import com.tigo.cs.android.exception.FlyModeException;
import com.tigo.cs.android.exception.RetryException;
import com.tigo.cs.android.helper.domain.LocationEntity;
import com.tigo.cs.android.helper.domain.MessageEntity;
import com.tigo.cs.android.helper.domain.OperationEntity;
import com.tigo.cs.android.helper.domain.ServiceEventEntity;
import com.tigo.cs.android.receiver.AlarmReceiver;
import com.tigo.cs.android.receiver.LocatorReceiver;
import com.tigo.cs.android.receiver.ResendSmsReceiver;
import com.tigo.cs.android.util.CSTigoNotificationID;
import com.tigo.cs.android.util.Cypher;
import com.tigo.cs.android.util.LocationResult;
import com.tigo.cs.android.util.MessageState;
import com.tigo.cs.android.util.Notifier;
import com.tigo.cs.android.util.PostRESTRequestData;
import com.tigo.cs.android.util.WebService;
import com.tigo.cs.api.entities.BaseServiceBean;

public class LocationService extends Service {

    public static final int LOCATE_AND_SEND = 1;
    public static final int SEND = 2;
    public static final int ALARM = 3;
    public static final int UPDATE_CONFIGURATION = 4;
    public static final int RESEND_SMS = 5;
    public static final String SENT = "SMS_SENT";
    public static final String DELIVERED = "SMS_DELIVERED";
    public static final Long TWO_MINUTE = 2 * 60 * 1000L;

    /*
     * mensajeros utilizados desde la actividad cliente para enlazarse a este
     * servicio
     */
    Messenger messenger = new Messenger(new LocalHandler());
    Messenger clientMessenger;
    private Gson gson;

    /*
     * manejador del servicio de localizacion
     */
    private LocalHandler mServiceHandler;

    @Override
    public void onCreate() {
        /*
         * En el momento de creación del servicio instanciamos el manejador
         * mismo
         * 
         * Este es el responsable de realizar las acciones en cada invocacion al
         * servicio
         */
        mServiceHandler = new LocalHandler();

        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /*
         * obtenemos un mensaje del manejador y realizamos la invocacion con el
         * mensaje de finalizacion del servicio como parametro del mensaje
         */

        if (intent == null) {
            return START_STICKY;
        }

        Message msg = mServiceHandler.obtainMessage();

        if (msg == null) {
            return START_STICKY;
        }

        msg.what = ALARM;
        if (intent.getExtras() != null) {
            Serializable serializable = intent.getExtras().getSerializable(
                    "service");
            if (serializable instanceof BaseServiceBean) {
                BaseServiceBean entity = (BaseServiceBean) serializable;
                msg.obj = entity;
                MessageEntity messageEntity = CsTigoApplication.getMessageHelper().find(
                        entity.getMessageId());
                if (messageEntity.getState().compareTo(
                        MessageState.PREPARED_TO_SEND.ordinal()) == 0) {
                    msg.what = LOCATE_AND_SEND;
                } else {
                    msg.what = SEND;
                }
                msg.arg1 = 1;
            }
        }
        msg.arg1 = 1;
        mServiceHandler.sendMessage(msg);

        /*
         * retornamos esta variable para que el servicio siga ejecutandose,que
         * no muera ya que en el hilo asignado al servicio estan corriendo los
         * listener del locationManager
         */
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    enum Channel {
        REST, SMS;
    }

    public static abstract class ResultManager {

        private Channel channel = Channel.REST;

        public Channel getChannel() {
            return channel;
        }

        public void setChannel(Channel channel) {
            this.channel = channel;
        }

        public abstract void gotResponse(ResultManagerData response);
    }

    enum ResultManagerState {
        OK, ERROR;
    }

    class ResultManagerData {
        private String data;
        private ResultManagerState resultManagerState;

        public ResultManagerData(String data,
                ResultManagerState resultManagerState) {
            this.data = data;
            this.resultManagerState = resultManagerState;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public ResultManagerState getResultManagerState() {
            return resultManagerState;
        }

        public void setResultManagerState(ResultManagerState resultManagerState) {
            this.resultManagerState = resultManagerState;
        }

    }

    public class WSAsyncTask extends AsyncTask<String, Void, ResultManagerData> {

        protected String serviceName;
        protected String json;
        protected OperationEntity operationEntity;
        protected ResultManager resultManager;

        public WSAsyncTask(String serviceName, String json,
                ResultManager resultManager, OperationEntity operationEntity) {
            this.serviceName = serviceName;
            this.json = json;
            this.resultManager = resultManager;
            this.operationEntity = operationEntity;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ResultManagerData doInBackground(String... urls) {
            try {
                PostRESTRequestData requestData = new PostRESTRequestData();
                requestData.setData(json);
                requestData.setVerifyURL(operationEntity.getRestWsLocation());
                requestData.setWebMethodName(serviceName);
                requestData.setWebServiceURL(operationEntity.getRestWsLocation());
                requestData.setOperationEntity(operationEntity);

                WebService ws = new WebService(requestData);
                String result = ws.webInvoke();

                ResultManagerData resultManagerData = null;
                ResultManagerState resultManagerState = ResultManagerState.ERROR;
                if (result != null) {
                    resultManagerState = ResultManagerState.OK;
                }
                resultManagerData = new ResultManagerData(result, resultManagerState);

                return resultManagerData;

            } catch (RetryException e) {
                ResultManagerData resultManagerData = null;
                ResultManagerState resultManagerState = ResultManagerState.ERROR;
                resultManagerData = new ResultManagerData(e.getMessage(), resultManagerState);
                return resultManagerData;
            } catch (Exception e) {
                ResultManagerData resultManagerData = null;
                ResultManagerState resultManagerState = ResultManagerState.ERROR;
                resultManagerData = new ResultManagerData(e.getMessage(), resultManagerState);
                return resultManagerData;
            }
        }

        @Override
        protected void onPostExecute(ResultManagerData result) {
            super.onPostExecute(result);
            resultManager.gotResponse(result);
        }

    }

    public boolean checkOnlineState() {
        ConnectivityManager CManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo NInfo = CManager.getActiveNetworkInfo();
        if (NInfo != null && NInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    /**
     * 
     * Manejador mensajes locales del servicio. Cuando una actividad u otro
     * componente este enlazado a este servicio, los mensajes al mismo son
     * procesados en esta clase.
     * 
     * El principal objetivo de esta clase es recibir mensajes de los servicio
     * 
     * @author Miguel Maciel
     * 
     */
    public class LocalHandler extends Handler {

        /**
         * metodo manejador de los mensajes que son enviados desde los
         * componentes enlazados a este servicio
         */
        @Override
        public void handleMessage(Message msg) {
            Locale.setDefault(Locale.US);
            final BaseServiceBean entity = msg.obj != null
                && msg.obj instanceof BaseServiceBean ? (BaseServiceBean) msg.obj : null;
            final MessageEntity messageEntity = entity != null ? CsTigoApplication.getMessageHelper().find(
                    entity.getMessageId()) : null;
            final ServiceEventEntity serviceEvent = entity != null ? CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                    entity.getServiceCod(), entity.getEvent()) : null;

            final ServiceEventEntity locationServiceEventEntity = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                    0, "LOCATION");
            OperationEntity operationEntity = null;
            try {
                operationEntity = CsTigoApplication.getOperationHelper().findOperationData();

                if (operationEntity == null) {

                    /*
                     * se obtiene siempre la informacion directamente del movil,
                     * para el caso en que el usuario cambie su simcard.
                     * 
                     * con esto forzamos al usuario a que utilice siempre su
                     * simcard del operador habilitado.
                     */

                    mServiceHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            Toast toast = Toast.makeText(LocationService.this,
                                    R.string.no_valid_simcard,
                                    Toast.LENGTH_LONG);
                            toast.show();

                        }
                    });
                    return;

                }

            } catch (FlyModeException e) {

                Toast toast = Toast.makeText(LocationService.this,
                        R.string.fly_mode, Toast.LENGTH_LONG);
                toast.show();
                return;

            }

            switch (msg.what) {
            /*
             * o segun sea el caso se necesita localizar el dispositivo antes de
             * enviar en esta seccion se determinan todas las condiciones y
             * validaciones de obtencion de datos gps
             */
            case LOCATE_AND_SEND:

                /*
                 * una vez obtenida la localizacion obetenemos el mensaje que
                 * sera enviado y enviamos con el punto gps o en el caso de no
                 * tener esta informacion con los datos de la celda
                 */

                Notifier.info(
                        getClass(),
                        "Se crea clase que manejara las acciones a realizar una vez encontrada la localizacion.");

                /*
                 * esta es la clase encargada de procesar en envio de la
                 * peticion al BMA, ya sea por sms o via red de datos
                 */
                LocationResult locationResult = new LocationResult() {

                    private TelephonyManager telephonyManager;

                    /**
                     * Metodo ejecutado al momento de obtener una localizacion,
                     * ya se por celda o gps. Puede ser una localizacion nula
                     */
                    @Override
                    public void gotLocation(final Location location) {
                        try {
                            boolean gpsEnabled = false;
                            LocationManager locationManager = null;
                            String locationSection = null;

                            String versionName = "";
                            Double latitude = 0.0;
                            Double longitude = 0.0;
                            Integer cellId = 0;
                            Integer lac = 0;
                            String deviceTimestamp = "";
                            String gpsEnabledString = "0";
                            Float accuracy = -1.0f;

                            if (locationManager == null) {
                                locationManager = (LocationManager) CsTigoApplication.getContext().getSystemService(
                                        Context.LOCATION_SERVICE);
                            }

                            /*
                             * se vuelve a hacer una re-verificacion de los
                             * estados del gps, en el caso que el usuario apago
                             * intencionalmente el gps
                             */
                            try {
                                gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                            } catch (Exception ex) {
                                gpsEnabled = false;
                            }

                            /*
                             * hay eventos, como el rastreo persistente, donde
                             * el evento de apagado intencional del gps no debe
                             * ser mostrado al usuario pero igual debe enviar la
                             * localizacion del usuario. Se valida este caso.
                             */
                            if (!serviceEvent.getIgnoreGpsDisabled()) {
                                /*
                                 * Para los demas casos, un apagado intencional
                                 * del gps debe notificar del no envio de la
                                 * peticion al bma
                                 */
                                if (entity.getRequiresLocation()) {
                                    /*
                                     * esta notificacion solo se hace en el caso
                                     * que el evento este seleccionado como que
                                     * este requiera enviar la localizacion
                                     */
                                    if (!gpsEnabled) {

                                        /*
                                         * solo notificamos en el caso que el
                                         * gps este apado
                                         */
                                        Notifier.warning(
                                                getClass(),
                                                CsTigoApplication.getContext().getString(
                                                        R.string.no_location));

                                        CsTigoApplication.showNotification(
                                                CSTigoNotificationID.LOCATION,
                                                CsTigoApplication.getContext().getString(
                                                        R.string.no_location_title),
                                                CsTigoApplication.getContext().getString(
                                                        R.string.no_location),
                                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        CsTigoApplication.vibrate(false);
                                        return;
                                    } else {

                                        /*
                                         * hay situaciones donde una
                                         * localizacion a traves de celda no es
                                         * suficiente, y necesariamente
                                         * necesitamos una localizacion vis gps
                                         */

                                        if (serviceEvent.getForceGps()
                                            && location == null) {
                                            /*
                                             * si es el caso comentado, se
                                             * notifica al usuario que no se
                                             * pudo enviar la peticion por no
                                             * obtener un punto gps valido
                                             */
                                            Notifier.warning(
                                                    getClass(),
                                                    CsTigoApplication.getContext().getString(
                                                            R.string.no_location));

                                            CsTigoApplication.showNotification(
                                                    CSTigoNotificationID.LOCATION,
                                                    CsTigoApplication.getContext().getString(
                                                            R.string.could_not_get_location_title),
                                                    CsTigoApplication.getContext().getString(
                                                            R.string.could_not_get_location_message),
                                                    MessageHistoryActivity.class);
                                            CsTigoApplication.vibrate(false);

                                            /*
                                             * se guarda el mensaje, para que se
                                             * muestre al usuario al momento de
                                             * abrir la notificacion
                                             */
                                            MessageEntity newMessageEntity = new MessageEntity();
                                            newMessageEntity.setEventDate(new Date());
                                            newMessageEntity.setMessage(CsTigoApplication.getContext().getString(
                                                    R.string.could_not_get_location_message)
                                                + CsTigoApplication.getContext().getString(
                                                        R.string.could_not_send_message_without_gps_localization));
                                            newMessageEntity.setService(serviceEvent.getService());
                                            newMessageEntity.setState(MessageState.RECEIVED.ordinal());
                                            CsTigoApplication.getMessageHelper().insert(
                                                    newMessageEntity);

                                            /*
                                             * TODO: analizar si esta porcion de
                                             * codigo esta correcta, ya que se
                                             * notifica dos veces antes al
                                             * usuario, y esta validacion ya
                                             * estaria de mas
                                             */
                                            mServiceHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (serviceEvent.getNotifyMessage()) {
                                                        Toast toast = Toast.makeText(
                                                                LocationService.this,
                                                                R.string.could_not_send_message_without_gps_localization,
                                                                Toast.LENGTH_LONG);
                                                        toast.show();
                                                    }
                                                }
                                            });

                                            return;
                                        }
                                    }
                                }
                            }

                            /*
                             * una vez terminadas las validaciones referentes al
                             * gps, agregamos la informacion de localizacion a
                             * los datos a enviar
                             */

                            if (telephonyManager == null) {
                                telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(
                                        Context.TELEPHONY_SERVICE);
                            }

                            /*
                             * obtenemos la localizacion basada en celdas para
                             * enviar al usuario
                             */
                            GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();

                            /*
                             * obtenemos el patron de localizacion para
                             * concatenar al mensaje
                             */

                            String locationPattern = locationServiceEventEntity.getMessagePattern();

                            /*
                             * definimos el valor del campo cellid
                             */
                            try {
                                cellId = getCID(cellLocation.getCid());
                                entity.setCellId(cellId);

                                lac = cellLocation.getLac();
                                entity.setLac(lac);

                                Date currentDate = new Date();
                                deviceTimestamp = Long.toString(currentDate.getTime());
                                entity.setTimestamp(currentDate.getTime());
                                entity.setDeviceHour(deviceTimestamp);
                                entity.setGeneredDate(currentDate.getTime());

                                gpsEnabledString = gpsEnabled ? "1" : "0";
                                entity.setGpsEnabled(gpsEnabled);

                                versionName = CsTigoApplication.getContext().getPackageManager().getPackageInfo(
                                        CsTigoApplication.getContext().getPackageName(),
                                        0).versionName;

                                entity.setVersionName(versionName);

                                accuracy = location != null ? location.getAccuracy() : -1;
                                entity.setGpsAccuracy(accuracy);

                                latitude = location != null ? location.getLatitude() : 0.0;
                                entity.setLatitude(latitude);

                                longitude = location != null ? location.getLongitude() : 0.0;
                                entity.setLongitude(longitude);

                                /*
                                 * una vez preparada la seccion de localizacion,
                                 * recuperamos el mensaje de la base de datos
                                 * del dispositivo
                                 */

                                entity.setImsi(telephonyManager.getSubscriberId());
                                entity.setImei(telephonyManager.getDeviceId());

                            } catch (Exception e) {
                                Notifier.error(getClass(),
                                        "No se obtuvo la longitud del punto GPS.");
                            }

                            locationSection = MessageFormat.format(
                                    locationPattern, latitude, longitude,
                                    cellId, lac, deviceTimestamp,
                                    gpsEnabledString, accuracy, versionName);

                            /*
                             * una vez preparada la seccion de localizacion,
                             * recuperamos el mensaje de la base de datos del
                             * dispositivo
                             */

                            String finalMessageOut = messageEntity.getMessage();

                            if (finalMessageOut == null) {
                                finalMessageOut = "";
                            }
                            finalMessageOut = locationSection.concat(finalMessageOut);

                            entity.setMsisdn(CsTigoApplication.getGlobalParameterHelper().getDeviceCellphoneNum());

                            entity.setAndroid(true);
                            entity.setSendSMS(true);

                            entity.setHash(Cypher.encrypt(entity.getMsisdn(),
                                    entity.getMsisdn() + entity.getTimestamp()));

                            String gsonRequest = entity.toStringWithHash();
                            messageEntity.setJsonData(gsonRequest);
                            messageEntity.setEntityClass(entity.getClass());

                            try {
                                if (serviceEvent.getEncryptMessage()) {
                                    finalMessageOut = Cypher.encrypt(
                                            CsTigoApplication.getGlobalParameterHelper().getDeviceCellphoneNum().trim(),
                                            finalMessageOut.trim());
                                    finalMessageOut = "?#".concat(finalMessageOut);
                                } else {
                                    finalMessageOut = finalMessageOut.trim();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            messageEntity.setMessage(finalMessageOut);

                            /*
                             * una vez agregada toda la informacion necesaria al
                             * objeto, lo persistimos y almacenamos para que en
                             * caso que tengamos que reenviar la peticion por
                             * alguna falla no obtengamos datos de localizacion
                             * erroneos
                             */
                            CsTigoApplication.getMessageHelper().update(
                                    messageEntity);
                        }
                        /*
                         * ante cualquier error durante la recuperacion de datos
                         * de localizacion o procesamiento del mensaje se atrapa
                         * la excepcion para no mostrar
                         */
                        catch (Exception e) {
                            Notifier.error(
                                    getClass(),
                                    CsTigoApplication.getContext().getString(
                                            R.string.no_location_disabled_on_listening));

                        }
                    }

                };

                /*
                 * antes de realizar el encendido del GPS y se verifican
                 * configuraciones de la entidad y la posibilidad de enviar
                 * peticiones con datos de localizacion
                 */
                // LocationUtil locationUtil = new LocationUtil();

                /*
                 * esta consfiguracion se agrega para ocasiones donde el evento
                 * deba ser enviado aunque el GPS del dispositivo sea apagado
                 * intencionalmente durante el proceso de ubicacion del mismo
                 */
                locationResult.setNotifyProviderDisabled(!serviceEvent.getIgnoreGpsDisabled());

                /*
                 * luego de cada localizacion de un punto GPS, este es
                 * almacenado en la base de datos interna del dispositivo. Esta
                 * localizacion es verificada en cada marcacion y si la
                 * localizacion es lo suficientemente actual se envia esta
                 * localizacion en lugar de obtener un punto gps nuevo y usar la
                 * bateria del dispositivo o retrasar el envio del mensaje
                 */
                LocationEntity locationEntity = CsTigoApplication.getLocationHelper().findLast();

                /*
                 * en situaciones donde el evento no requiera de una
                 * localizacion, directamente invocamos al metodo que realiza el
                 * envio del mensaje, sin datos de localizacion
                 */
                if (!entity.getRequiresLocation()) {
                    locationResult.gotLocation(null);
                } else if (locationEntity != null) {
                    /*
                     * si existe localizaciones disponibles en la base de datos
                     * interna del movil, verificamos si esta es una
                     * localizacion actual GPS, de al menos 2 minutos atras
                     */
                    Long locationTime = locationEntity.getDateTime().getTime();
                    Long nowTime = new Date().getTime();
                    if (nowTime - locationTime <= TWO_MINUTE) {
                        /*
                         * al ser esta una localizacion GPS reciente, se
                         * reutiliza la misma y se envia a la plataforma solo
                         * esta localizacion
                         */
                        Location location = new Location(locationEntity.getProvider());
                        location.setLatitude(locationEntity.getLatitude());
                        location.setLongitude(locationEntity.getLongitude());
                        location.setAccuracy(locationEntity.getAccuracy());
                        location.setAltitude(locationEntity.getAltitude());
                        location.setBearing(locationEntity.getBearing());
                        location.setSpeed(locationEntity.getSpeed());
                        location.setTime(locationEntity.getDateTime().getTime());
                        locationResult.gotLocation(location);
                    }
                    /*
                     * en el caso que el evento a registrar no requiera que el
                     * usuario quede esperando una localizacion GPS valida, y
                     * las localizaciones en la base de datos interna no son
                     * actuales, solo se enviaran los datos de la celda
                     */
                    else if (!serviceEvent.getRequiresWait()) {
                        locationResult.gotLocation(null);
                    }
                    /*
                     * se envia nulo y no se espera sel SO una localizacion por
                     * cuestiones de optimizacion, ya que existen hilos en
                     * segundo plano que constantemente buscan una localizacion
                     * y seria redundante y no optimo buscar de nuevo
                     */
                    else {
                        locationResult.gotLocation(null);
                    }
                }
                /*
                 * como no se tiene una localizacion valida en la base de datos
                 * del dispositivo y no es requerida para este envio
                 */
                else if (!serviceEvent.getRequiresWait()) {
                    locationResult.gotLocation(null);
                }
                /*
                 * se envia nulo y no se espera sel SO una localizacion por
                 * cuestiones de optimizacion, ya que existen hilos en segundo
                 * plano que constantemente buscan una localizacion y seria
                 * redundante y no optimo buscar de nuevo
                 */
                else {
                    locationResult.gotLocation(null);
                }

            case SEND:

                /*
                 * en esta secccion se tiene el objeto totalmente preparado para
                 * su envio, se almacenan en el objeto messageEntity
                 */

                final ResultManager smsResultManager = new ResultManager() {

                    @Override
                    public void gotResponse(ResultManagerData result) {

                        /*
                         * contemplamos el caso de envio de SMS como canal
                         * alternativo en el caso que el dispositivo no tenga
                         * conectividad de datos o en el caso que explicitamente
                         * se invoque al metodo con el SMS a enviar TODO: podria
                         * encontrarse una alternativa para separar la logica y
                         * sea mas claro
                         */

                        /*
                         * solo seran enviados a traves del canal SMS los
                         * eventos que no esten forzados a usar el canal
                         */

                        messageEntity.setChannel(Channel.SMS.ordinal());
                        CsTigoApplication.getMessageHelper().update(
                                messageEntity);

                        if (messageEntity.getForceInternet() == null
                            || !messageEntity.getForceInternet()) {

                            /*
                             * la informacion sobre el tipo de canal (de
                             * preferencia o exlusividad) a utilizar se
                             * alamacena en el mensaje, en el caso que la opcion
                             * de forzar enviar solo via datos este disponible
                             * no se ejecutaran las acciones dentro de este
                             * condicion
                             */

                            SmsManager sms = SmsManager.getDefault();

                            List<String> parts = sms.divideMessage(messageEntity.getMessage());

                            if (CsTigoApplication.messageId >= 10) {
                                CsTigoApplication.messageId = 0;
                            }

                            if (parts.size() == 1) {

                                PendingIntent sentPI = getPendingIntentSMS(
                                        SENT, messageEntity.getMessage(),
                                        messageEntity.getId(), parts.size(), 1);

                                registerReceiver(new BroadcastReceiver() {

                                    @Override
                                    public void onReceive(Context context, Intent arg1) {
                                        switch (getResultCode()) {
                                        case Activity.RESULT_OK:
                                            messageEntity.setState(MessageState.RETRY.ordinal());
                                            break;
                                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                            messageEntity.setState(MessageState.CANCEL.ordinal());
                                            break;
                                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                                            messageEntity.setState(MessageState.CANCEL.ordinal());
                                            break;
                                        case SmsManager.RESULT_ERROR_NULL_PDU:
                                            messageEntity.setState(MessageState.RETRY.ordinal());
                                            break;
                                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                                            messageEntity.setState(MessageState.RETRY.ordinal());
                                            break;
                                        }
                                        CsTigoApplication.getMessageHelper().update(
                                                messageEntity);
                                        context.unregisterReceiver(this);
                                    }
                                }, new IntentFilter(SENT));

                                PendingIntent deliveredPI = getPendingIntentSMS(
                                        DELIVERED, messageEntity.getMessage(),
                                        messageEntity.getId(), parts.size(), 1);

                                registerReceiver(new BroadcastReceiver() {
                                    @Override
                                    public void onReceive(Context context, Intent arg1) {
                                        switch (getResultCode()) {
                                        case Activity.RESULT_OK:
                                            messageEntity.setState(MessageState.DELIVERED.ordinal());
                                            break;
                                        case Activity.RESULT_CANCELED:
                                            messageEntity.setState(MessageState.CANCEL.ordinal());
                                            break;
                                        }
                                        CsTigoApplication.getMessageHelper().update(
                                                messageEntity);
                                        context.unregisterReceiver(this);
                                    }
                                }, new IntentFilter(DELIVERED));

                                String msg = parts.get(0);

                                try {
                                    sms.sendTextMessage(
                                            CsTigoApplication.getGlobalParameterHelper().getShortNumber(),
                                            null, msg, sentPI, deliveredPI);
                                } catch (Exception e) {
                                    mServiceHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            Toast toast = Toast.makeText(
                                                    LocationService.this,
                                                    CsTigoApplication.getContext().getString(
                                                            R.string.invalid_operator),
                                                    Toast.LENGTH_LONG);
                                            toast.show();

                                        }
                                    });
                                }
                            } else {
                                messageEntity.setState(MessageState.DELIVERED.ordinal());

                                CsTigoApplication.getMessageHelper().update(
                                        messageEntity);

                                for (int i = 0; i < parts.size(); i++) {

                                    final MessageEntity messageEntityPart = CsTigoApplication.getMessageHelper().find(
                                            messageEntity.getId());

                                    messageEntityPart.setId(null);
                                    messageEntityPart.setState(MessageState.PREPARED_TO_SEND.ordinal());
                                    CsTigoApplication.getMessageHelper().insert(
                                            messageEntityPart);

                                    PendingIntent sentPI = getPendingIntentSMS(
                                            SENT, messageEntity.getMessage(),
                                            messageEntity.getId(),
                                            parts.size(), i);

                                    // ---when the SMS has been sent---

                                    BroadcastReceiver sentReceiver = new BroadcastReceiver() {

                                        @Override
                                        public void onReceive(Context context, Intent arg1) {
                                            switch (getResultCode()) {
                                            case Activity.RESULT_OK:
                                                messageEntityPart.setState(MessageState.RETRY.ordinal());
                                                CsTigoApplication.getMessageHelper().update(
                                                        messageEntityPart);
                                                break;
                                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                                messageEntityPart.setState(MessageState.CANCEL.ordinal());
                                                CsTigoApplication.getMessageHelper().update(
                                                        messageEntityPart);
                                                break;
                                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                                messageEntityPart.setState(MessageState.CANCEL.ordinal());
                                                CsTigoApplication.getMessageHelper().update(
                                                        messageEntityPart);
                                                break;
                                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                                messageEntityPart.setState(MessageState.RETRY.ordinal());
                                                CsTigoApplication.getMessageHelper().update(
                                                        messageEntityPart);
                                                break;
                                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                                messageEntityPart.setState(MessageState.RETRY.ordinal());
                                                CsTigoApplication.getMessageHelper().update(
                                                        messageEntityPart);
                                                break;
                                            }
                                            context.unregisterReceiver(this);
                                        }

                                    };

                                    registerReceiver(sentReceiver,
                                            new IntentFilter(SENT));

                                    PendingIntent deliveredPI = getPendingIntentSMS(
                                            DELIVERED,
                                            messageEntity.getMessage(),
                                            messageEntity.getId(),
                                            parts.size(), i);

                                    // ---when the SMS has been delivered---

                                    BroadcastReceiver deliveredeReceiver = new BroadcastReceiver() {
                                        @Override
                                        public void onReceive(Context context, Intent arg1) {
                                            switch (getResultCode()) {
                                            case Activity.RESULT_OK:
                                                messageEntityPart.setState(MessageState.DELIVERED.ordinal());
                                                CsTigoApplication.getMessageHelper().update(
                                                        messageEntityPart);
                                                break;
                                            case Activity.RESULT_CANCELED:
                                                messageEntityPart.setState(MessageState.CANCEL.ordinal());
                                                CsTigoApplication.getMessageHelper().update(
                                                        messageEntityPart);
                                                break;
                                            }
                                            context.unregisterReceiver(this);
                                        }
                                    };

                                    registerReceiver(deliveredeReceiver,
                                            new IntentFilter(DELIVERED));

                                    String pattern = "503{0}{1}{2}{3}";

                                    String msg = MessageFormat.format(pattern,
                                            CsTigoApplication.messageId,
                                            parts.size(), i + 1, parts.get(i));

                                    try {
                                        sms.sendTextMessage(
                                                CsTigoApplication.getGlobalParameterHelper().getShortNumber(),
                                                null, msg, sentPI, deliveredPI);
                                    } catch (Exception e) {
                                        mServiceHandler.post(new Runnable() {
                                            @Override
                                            public void run() {

                                                Toast toast = Toast.makeText(
                                                        LocationService.this,
                                                        CsTigoApplication.getContext().getString(
                                                                R.string.invalid_operator),
                                                        Toast.LENGTH_LONG);
                                                toast.show();

                                            }
                                        });
                                    }

                                }
                                CsTigoApplication.messageId++;
                            }

                            /*
                             * en el caso que el usuario deba ser notificado
                             * sobre el SMS enviado
                             */
                            if (serviceEvent.getNotifyMessage()) {
                                mServiceHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(
                                                LocationService.this,
                                                MessageFormat.format(
                                                        CsTigoApplication.getContext().getString(
                                                                R.string.message_sent),
                                                        CsTigoApplication.i18n(serviceEvent.getService().getServicename())),
                                                Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                });
                            }

                            /*
                             * TODO: ningun estado deberia ser modificado en
                             * este punto
                             */
                            CsTigoApplication.getMessageHelper().update(
                                    messageEntity);
                        }

                    }
                };

                /*
                 * esta clase es la encargada de procesar la respuesta de WS
                 * REST o de realizar el envio del SMS en el caso que hay
                 * fallado la peticion REST
                 */
                ResultManager restResultManager = new ResultManager() {

                    /**
                     * el valor de result puede ser la respuesta de la peticion
                     * REST o el sms a enviar.
                     */
                    @Override
                    public void gotResponse(ResultManagerData result) {

                        switch (result.getResultManagerState()) {
                        case OK:
                            BaseServiceBean resultEntity = null;

                            resultEntity = gson.fromJson(result.getData(),
                                    entity.getClass());

                            /*
                             * TODO: analizar si es necesario este mensaje en
                             * este lugar ya que ya se muestra cuando llega la
                             * respuesta. o notificar un mensaje diferente al
                             * usuario que su mensaje sera procesado
                             */
                            if (resultEntity != null) {
                                if (serviceEvent.getNotifyMessage()) {
                                    mServiceHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            Toast toast = Toast.makeText(
                                                    LocationService.this,
                                                    MessageFormat.format(
                                                            CsTigoApplication.getContext().getString(
                                                                    R.string.message_sent),
                                                            CsTigoApplication.i18n(serviceEvent.getService().getServicename())),
                                                    Toast.LENGTH_LONG);
                                            toast.show();
                                        }
                                    });

                                    /*
                                     * una vez procesado el mensaje y convertido
                                     * a una entidad se envia este la clase
                                     * procesadora correspondiente
                                     */
                                    CsTigoApplication.manage(resultEntity);

                                }
                            }

                            messageEntity.setChannel(Channel.REST.ordinal());
                            messageEntity.setState(MessageState.DELIVERED.ordinal());
                            CsTigoApplication.getMessageHelper().update(
                                    messageEntity);

                            break;

                        case ERROR:

                            /*
                             * no se pudo obtener un objeto java de la respuesta
                             * del WSse debe reintentar el envio de la peticion
                             * via SMS
                             */
                            if (!serviceEvent.getForceInternet()
                                && (messageEntity.getForceInternet() == null || !messageEntity.getForceInternet())) {

                                /*
                                 * solo se debe enviar la peticion via paquete
                                 * de datos, ya que el evento esta marcado para
                                 * solo via este canal
                                 */

                                Notifier.info(getClass(),
                                        "La operacion se enviara por SMS.");

                                ResultManagerData resultManagerData = new ResultManagerData(messageEntity.getMessage(), ResultManagerState.OK);

                                smsResultManager.setChannel(Channel.SMS);
                                smsResultManager.gotResponse(resultManagerData);

                            } else {

                                /*
                                 * en el caso que se use el canal de SMS
                                 * alternativamente se verifica si el mensaje
                                 * debe ser notificado al usuario sobre la no
                                 * conectividad de datos. y que su mensaje sera
                                 * enviado en la brevedad posible TODO: agregar
                                 * a la tabla mensajes la misma notificacion
                                 */

                                if (serviceEvent.getNotifyMessage()) {
                                    mServiceHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            Toast toast = Toast.makeText(
                                                    LocationService.this,
                                                    MessageFormat.format(
                                                            CsTigoApplication.getContext().getString(
                                                                    R.string.no_internet_connection),
                                                            CsTigoApplication.i18n(serviceEvent.getService().getServicename())),
                                                    Toast.LENGTH_LONG);
                                            toast.show();
                                        }

                                    });
                                }

                            }

                            break;
                        }

                    }
                };

                /*
                 * en esta seccion se verifica si la peticion sera enviada a
                 * traves de la red de datos o via SMS segun la configuracion
                 * del evento
                 */
                Boolean internet = serviceEvent.getForceInternet()
                    || CsTigoApplication.getGlobalParameterHelper().getInternetEnabled();

                /*
                 * por mas que el evento este configurado para enviar la
                 * peticion a traves de la red de datos se puede dar el caso que
                 * no exista conectividad TODO: esto validar dentro de
                 * WSAsyncTAsk para contemplar lo del retry cuando tenga
                 * conectividad
                 */
                if (!checkOnlineState()) {
                    internet = false;
                }

                /*
                 * en el caso que la peticion este preparada para ser enviada a
                 * traves de la red de datos necesitamos preparar el Looper
                 */
                if (internet && serviceEvent.getInternet()) {

                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }

                    /*
                     * finalmente, enviamos la peticion al WS Rest del BMA
                     */

                    WSAsyncTask task = new WSAsyncTask(serviceEvent.getWsPath(), messageEntity.getJsonData(), restResultManager, operationEntity);
                    task.execute(new String[] {});

                } else

                /*
                 * en situaciones la peticion se hace a traves del canal SMS, en
                 * estas situaciones se verifica que la peticion pueda enviarse
                 * a traves de este canal eso es si la opcion internet esta
                 * deshabilitada y el patron de servicio existe op bien si el
                 * evento a registrar no puede enviarse a traves de la red de
                 * datos y solo via sms
                 */

                if ((!internet && messageEntity.getMessage() != null)
                    || !serviceEvent.getInternet()
                    && messageEntity.getMessage() != null) {

                    /*
                     * hay eventos que solo pueden ser enviados a traves de la
                     * red de datos, pero como no hay conectividad, se muestra
                     * un mensaje al usuario que el mensaje sera enviado luego o
                     * que no posee conexion de datos disponible
                     */
                    if (serviceEvent.getForceInternet()) {

                        /*
                         * en el caso que el mensaje tenga que ser reenviado
                         * mostramos un mensaje al usuario que su mensaje fue
                         * almacenado para un reenvio posterior
                         */
                        if (serviceEvent.getRetry()) {

                            messageEntity.setState(MessageState.RETRY.ordinal());

                            CsTigoApplication.getMessageHelper().update(
                                    messageEntity);

                            if (serviceEvent.getNotifyMessage()) {
                                mServiceHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(
                                                LocationService.this,
                                                MessageFormat.format(
                                                        CsTigoApplication.getContext().getString(
                                                                R.string.message_will_be_sent_later),
                                                        CsTigoApplication.i18n(serviceEvent.getService().getServicename())),
                                                Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                });
                            }
                        }
                        /*
                         * el evento no tiene la opcion de reintento de envio,
                         * por ello se notifica al usuario que no posee
                         * conectividad con la red de datos
                         */
                        else {
                            if (serviceEvent.getNotifyMessage()) {
                                mServiceHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(
                                                LocationService.this,
                                                MessageFormat.format(
                                                        CsTigoApplication.getContext().getString(
                                                                R.string.no_internet_connection),
                                                        CsTigoApplication.i18n(serviceEvent.getService().getServicename())),
                                                Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                });
                            }
                        }

                    }
                    /*
                     * como el evento no esta configurado para ser enviado
                     * exclusivamente a traves de la red de datos se hace el
                     * envio a traves de SMS como canal alternativo
                     */
                    else {
                        /*
                         * TODO:crear un result manager exclusivo para envio de
                         * SMS y no mezclar logica de canales diferentes
                         */

                        ResultManagerData resultManagerData = new ResultManagerData(messageEntity.getMessage(), ResultManagerState.OK);

                        smsResultManager.setChannel(Channel.SMS);
                        smsResultManager.gotResponse(resultManagerData);
                    }
                }

            case ALARM:

                if (!CsTigoApplication.getGlobalParameterHelper().getDeviceEnabled()) {
                    return;
                }
                if (!CsTigoApplication.isAlarmado()) {

                    /*
                     * obtenemos el servicio de alarma del Sistema Operativo
                     */
                    AlarmManager alarms = (AlarmManager) CsTigoApplication.getContext().getSystemService(
                            Context.ALARM_SERVICE);

                    /*
                     * creamos los intent para las alarmas programadas para la
                     * aplicacion
                     */
                    Intent trackerIntent = new Intent(CsTigoApplication.getContext(), AlarmReceiver.class);
                    /*
                     * creamos los intent para las alarmas programadas para la
                     * aplicacion
                     */
                    Intent resendIntent = new Intent(CsTigoApplication.getContext(), ResendSmsReceiver.class);

                    Intent locatorIntent = new Intent(CsTigoApplication.getContext(), LocatorReceiver.class);

                    /*
                     * creamos finalmente los pendigIntent
                     */
                    PendingIntent recurringTrack = PendingIntent.getBroadcast(
                            CsTigoApplication.getContext(), 0, trackerIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT);

                    /*
                     * creamos finalmente los pendigIntent
                     */
                    PendingIntent recurringResendSms = PendingIntent.getBroadcast(
                            CsTigoApplication.getContext(), 0, resendIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT);

                    PendingIntent locator = PendingIntent.getBroadcast(
                            CsTigoApplication.getContext(), 0, locatorIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT);

                    /*
                     * agregamos las alarmas al sistema operativo
                     */

                    /*
                     * se fija la hora en la que correra por primera vez la
                     * alarma
                     */
                    Calendar updateTime = Calendar.getInstance();
                    updateTime.setTime(new Date());
                    updateTime.add(Calendar.SECOND, 5);

                    /*
                     * agregamos las alarmas al sistema operativo
                     */

                    alarms.setInexactRepeating(
                            AlarmManager.RTC_WAKEUP,
                            updateTime.getTimeInMillis(),
                            CsTigoApplication.getGlobalParameterHelper().getPersistentTrackingInterval(),
                            recurringTrack);

                    alarms.setInexactRepeating(
                            AlarmManager.RTC_WAKEUP,
                            updateTime.getTimeInMillis(),
                            CsTigoApplication.getGlobalParameterHelper().getPersistentResendSmsInterval(),
                            recurringResendSms);

                    alarms.setInexactRepeating(
                            AlarmManager.RTC_WAKEUP,
                            updateTime.getTimeInMillis(),
                            CsTigoApplication.getGlobalParameterHelper().getLocatorInterval(),
                            locator);
                    /*
                     * levantamos la bandera de las alarmas activas
                     */
                    CsTigoApplication.setAlarmado(true);

                }
                break;

            }

            super.handleMessage(msg);
        }

        private PendingIntent getPendingIntentSMS(String event, String text, Long id, Integer parts, Integer sequence) {

            Intent sentIntent = new Intent(event);

            sentIntent.putExtra("text", text);
            sentIntent.putExtra("id", id);
            sentIntent.putExtra("parts", parts);
            sentIntent.putExtra("sequence", sequence);

            return PendingIntent.getBroadcast(CsTigoApplication.getContext(),
                    0, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

    }

    private static byte[] convertByteArray__p(int p_int) {
        byte[] l_byte_array = new byte[4];
        int MASK_c = 0xFF;
        for (short i = 0; i <= 3; i++) {
            l_byte_array[i] = (byte) ((p_int >> (8 * i)) & MASK_c);
        }
        return l_byte_array;
    }

    private int getCID(int cellid) {
        byte[] p_bytes = convertByteArray__p(cellid);

        int MASK_c = 0xFF;
        int l_result = 0;
        l_result = p_bytes[0] & MASK_c;
        l_result = l_result + ((p_bytes[1] & MASK_c) << 8);
        return l_result;
    }

}