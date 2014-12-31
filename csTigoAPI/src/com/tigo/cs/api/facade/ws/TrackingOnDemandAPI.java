package com.tigo.cs.api.facade.ws;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.ejb.SMSTransmitterException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.util.CellPhoneNumberUtil;
import com.tigo.cs.commons.util.Cypher;
import com.tigo.cs.commons.util.StringUtils;
import com.tigo.cs.domain.Cell;
import com.tigo.cs.domain.Message;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.Userphone;
import com.tigo.lbs.ws.LocationResponse;

public abstract class TrackingOnDemandAPI extends AbstractAPI<String> implements Serializable {

    private static final long serialVersionUID = -4081499449224587847L;

    protected TrackingOnDemandAPI() {
        super(String.class);

    }

    private static long TIMEOUT = 30000L;
    private static long MAX_TRACK_IDLE_TIME = 30000L;
    private static long ANDROID_MAX_TRACK_IDLE_TIME = 70000L;
    private static final String LBS = "LBS";
    private Map<Long, String> mapUserphoneTrackingType = new HashMap<Long, String>();
    private Map<Userphone, ServiceValue> maxServiceValueByUserphone;
    private Double progress = 0.0;
    private Double sumValueLbs = 0.0;
    private Double sumValueAndroid = 0.0;
    protected Map<Object, Object> nextServiceValueByUserphone;
    private String trackingType;
    protected Map<Object, Object> hashMarkerUser;
    protected Map<Object, Object> hashNotMarkerUser;
    protected Map<Object, String> hashUsersTracking;
    private ServiceValue trackingValue;
    private Boolean onlyAndroid;

    private void init() {
        mapUserphoneTrackingType = new HashMap<Long, String>();
        maxServiceValueByUserphone = null;
        progress = 0.0;
        sumValueLbs = 0.0;
        sumValueAndroid = 0.0;
        nextServiceValueByUserphone = null;
        trackingType = null;
        hashMarkerUser = null;
        hashNotMarkerUser = null;
        hashUsersTracking = null;
        trackingValue = null;
        onlyAndroid = false;
    }

    public List<ServiceValue> trackMobiles(List<Userphone> list) {
        init();
        try {
            MAX_TRACK_IDLE_TIME = Long.parseLong(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "tracking.timeout.ota"));
            ANDROID_MAX_TRACK_IDLE_TIME = Long.parseLong(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "tracking.timeout.android"));

            TIMEOUT = MAX_TRACK_IDLE_TIME;
        } catch (GenericFacadeException ex) {
            getFacadeContainer().getNotifier().signal(getClass(),
                    Action.WARNING,
                    "No se recuperaron los parametros globales de Time Out de Rastreo");
        }

        getFacadeContainer().getNotifier().info(getClass(), null,
                "Se rastrearan a las lineas");

        mapUserphoneTrackingType = new HashMap<Long, String>();
        for (Userphone u : list) {
            try {
                if (getFacadeContainer().getUserphoneAPI().getAndroidEnabledTracking(
                        u.getUserphoneCod())) {
                    mapUserphoneTrackingType.put(u.getUserphoneCod(),
                            "tracking.android");
                } else {
                    mapUserphoneTrackingType.put(u.getUserphoneCod(),
                            "tracking.lbs");
                }
            } catch (Exception ex) {
                getFacadeContainer().getNotifier().signal(getClass(),
                        Action.ERROR, ex.getMessage());
            }
        }

        List<Userphone> orderedSelectedUserphones = orderSelectedUserphoneList(list);
        return trackMobiles(orderedSelectedUserphones, true);
    }

    private boolean trackMobile(Userphone selectedUserphone, boolean forceLBS) {
        boolean needToWait = true;
        if (selectedUserphone != null) {
            boolean androidtrack = false;
            if (!forceLBS) {
                androidtrack = getFacadeContainer().getUserphoneAPI().getAndroidEnabledTracking(
                        selectedUserphone.getUserphoneCod());
            }
            try {
                String trackingType = getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "trackingondemand.type");
                if (androidtrack) {
                    TIMEOUT = ANDROID_MAX_TRACK_IDLE_TIME;
                    sendAndroidTrackingMessage(selectedUserphone);
                    getFacadeContainer().getNotifier().signal(getClass(),
                            Action.DEBUG, "Tiene rastreo Android.");
                } else if (!getTrackingType().trim().equals(LBS)) {
                    sendTrackingMessage(selectedUserphone);
                    getFacadeContainer().getNotifier().signal(getClass(),
                            Action.DEBUG, "No tiene rastreo Android.");
                } else if (getTrackingType().trim().equals(LBS)
                    && !androidtrack) {
                    lbsTracking(selectedUserphone, forceLBS);

                    /* Se incrementa en uno el progress, y se localiza el sv */
                    progress = progress + sumValueLbs;
                    ServiceValue sv = (ServiceValue) getNextServiceValueByUserphone().get(
                            selectedUserphone);

                    needToWait = false;
                }
                /*
                 * 
                 * Se recupera el modo de rastreo online: OTA o LBS
                 */

                getFacadeContainer().getNotifier().signal(getClass(),
                        Action.DEBUG, "Tipo de rastreo: ".concat(trackingType));

            } catch (Exception ex) {
                getFacadeContainer().getNotifier().signal(getClass(),
                        Action.ERROR, ex.getMessage(), ex);
            }
        } else {
            getFacadeContainer().getNotifier().signal(
                    getClass(),
                    Action.WARNING,
                    getFacadeContainer().getI18nAPI().iValue(
                            "web.client.backingBean.tracking.messages.MustSelectUser"));
        }
        return needToWait;
    }

    private List<Userphone> orderSelectedUserphoneList(List<Userphone> selectedUserphones) {
        List<Userphone> lbsList = new ArrayList<Userphone>();
        List<Userphone> androidList = new ArrayList<Userphone>();

        for (Userphone u : selectedUserphones) {
            if (mapUserphoneTrackingType.get(u.getUserphoneCod()).equals(
                    "tracking.lbs"))
                lbsList.add(u);
            else
                androidList.add(u);
        }

        List<Userphone> orderedList = new ArrayList<Userphone>();
        orderedList.addAll(lbsList);
        orderedList.addAll(androidList);

        return orderedList;
    }

    public List<ServiceValue> trackMobiles(List<Userphone> selectedUserphones, Boolean retry) {
        if (selectedUserphones == null || selectedUserphones.size() == 0) {
            return null;
        }

        /*
         * variable que controla la cantidad de solicitudes al LBS que fueron
         * respondidas
         */
        getFacadeContainer().getNotifier().info(
                getClass(),
                null,
                MessageFormat.format(
                        "Se obtiene la lista de lineas a rastrear. Cantidad:{0}",
                        selectedUserphones.size()));

        if (retry) {
            getFacadeContainer().getNotifier().info(
                    getClass(),
                    null,
                    "Se recrean los objetos que almacenan la informacion de los ultimos rastreos de la linea. ");
            setMaxServiceValueByUserphone(null);
            setNextServiceValueByUserphone(null);
        }

        for (Userphone u : selectedUserphones) {

            getFacadeContainer().getNotifier().info(
                    getClass(),
                    null,
                    MessageFormat.format(
                            "Se obtendra el ultimo valor de rastreo del dia. Userphone:{0}",
                            u));

            /*
             * esto deberia realizarse solamente cuando es el primer intento
             * 
             * retry=true
             * 
             * y cuando el rastreo es android o LBS
             */
            if (retry
                && (getFacadeContainer().getUserphoneAPI().getAndroidEnabledTracking(
                        u.getUserphoneCod()) || (!getFacadeContainer().getUserphoneAPI().getAndroidEnabledTracking(
                        u.getUserphoneCod()) && !getTrackingType().trim().equals(
                        LBS)))) {
                getMaxServiceValueByUserphone().put(u, findMaxServiceValue(u));
            }
        }

        boolean needToWait = false;
        for (Userphone u : selectedUserphones) {

            getFacadeContainer().getNotifier().info(
                    getClass(),
                    null,
                    MessageFormat.format(
                            "Se rastreara a la linea. Userphone:{0}", u));
            boolean trackingReturn = trackMobile(u, !retry);
            if (trackingReturn) {
                needToWait = trackingReturn;
            }

            /*
             * si no es un retry, se debe de poner directamente a 100 ya que ya
             * se localizaron todas las lineas android por LBS
             */
            if (!retry)
                progress = 100.0;
        }

        if (needToWait) {
            getFacadeContainer().getNotifier().info(getClass(), null,
                    "Se esperara un tiempo antes de buscar las lineas.");
            waitForNextServiceValueByUserphone(selectedUserphones);
        }

        List<Userphone> retryUserphone = new ArrayList<Userphone>();
        for (Userphone u : selectedUserphones) {
            if (!getNextServiceValueByUserphone().containsKey(u)) {
                if (!retry) {
                    getFacadeContainer().getNotifier().signal(
                            getClass(),
                            Action.WARNING,
                            MessageFormat.format(
                                    getFacadeContainer().getI18nAPI().iValue(
                                            "web.client.backingBean.tracking.messages.CouldNotLocateLine"),
                                    u.getCellphoneNum().toString()));
                } else {
                    retryUserphone.add(u);
                }

            }
        }
        if (retry && retryUserphone.size() > 0) {
            trackMobiles(retryUserphone, false);
        }

        List<ServiceValue> list = null;
        if (getNextServiceValueByUserphone().values().size() > 0) {
            list = new ArrayList<ServiceValue>();
            for (Object trackingValue : getNextServiceValueByUserphone().values()) {

                if (((ServiceValue) trackingValue).getColumn2Chr().equals("1")) {
                    getFacadeContainer().getNotifier().signal(
                            getClass(),
                            Action.INFO,
                            MessageFormat.format(
                                    getFacadeContainer().getI18nAPI().iValue(
                                            "web.client.backingBean.tracking.messages.LineWasLocated"),
                                    ((ServiceValue) trackingValue).getUserphone().getCellphoneNum().toString()));
                } else if (((ServiceValue) trackingValue).getColumn2Chr().equals(
                        "0")) {
                    getHashUsersTracking().put(
                            ((ServiceValue) trackingValue).getUserphone(), "2");
                    getHashNotMarkerUser().put(
                            ((ServiceValue) trackingValue).getUserphone(),
                            trackingValue);
                    getFacadeContainer().getNotifier().signal(
                            getClass(),
                            Action.INFO,
                            MessageFormat.format(
                                    getFacadeContainer().getI18nAPI().iValue(
                                            "web.client.backingBean.tracking.messages.CouldNotLocateLine"),
                                    ((ServiceValue) trackingValue).getUserphone().getCellphoneNum().toString()));
                }

                list.add((ServiceValue) trackingValue);
            }
        }

        progress = 100.0;
        return list;
    }

    private ServiceValue findMaxServiceValue(Userphone userphone) {
        return getFacadeContainer().getServiceValueAPI().find(
                getFacadeContainer().getServiceValueAPI().getMaxServicevalueToday(
                        userphone));
    }

    private void waitForNextServiceValueByUserphone(List<Userphone> list) {
        long startTime = System.currentTimeMillis();

        /*
         * se calcula lo que se va a sumar a la variable progress luego de todos
         * los rastreos LBS, si hay
         */
        sumValueAndroid = (100.0 - progress) / (TIMEOUT / 1000);

        /* Proceso que aumenta por tiempo la variable progress */
        Timer timer = new Timer();
        MyTask t = new MyTask();
        timer.schedule(t, 0, 1000);

        while (true) {

            if (getNextServiceValueByUserphone().values().size() == list.size()) {
                progress = 100.0;
                break;
            }
            for (Userphone u : list) {

                if (getFacadeContainer().getUserphoneAPI().getAndroidEnabledTracking(
                        u.getUserphoneCod())
                    || (!getFacadeContainer().getUserphoneAPI().getAndroidEnabledTracking(
                            u.getUserphoneCod()) && !getTrackingType().trim().equals(
                            LBS))) {
                    Long maxServiceValue = getMaxServiceValueByUserphone().get(
                            u) != null ? getMaxServiceValueByUserphone().get(u).getServicevalueCod() : null;
                    Long nextServiceValue = maxServiceValue != null ? getFacadeContainer().getServiceValueAPI().getMaxNextServicevalue(
                            u, maxServiceValue) : null;

                    if (nextServiceValue != null && onlyAndroid != null
                        && !onlyAndroid) {
                        trackingValue = getFacadeContainer().getServiceValueAPI().getServicevalue(
                                nextServiceValue);
                        getNextServiceValueByUserphone().put(u, trackingValue);

                        progress = progress + sumValueLbs;
                        // localizar(trackingValue);
                    }
                }

            }

            if (getNextServiceValueByUserphone().values().size() == list.size()) {
                progress = 100.0;
                break;
            }

            if (System.currentTimeMillis() - startTime >= TIMEOUT) {
                progress = 100.0;
                break;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                progress = 100.0;
                timer.cancel();
                e.printStackTrace();
            }

        }

        /* si llega a terminar el wait, pse debe cancelar el timer */
        timer.cancel();
    }

    private void lbsTracking(Userphone selectedUserphone, boolean forceLBS) {

        try {

            String trackingStatusMessage = "tracking.status.LBS";
            if (forceLBS) {
                trackingStatusMessage = "tracking.status.AndroidNoApp";
            }

            getFacadeContainer().getNotifier().signal(getClass(), Action.DEBUG,
                    "Se intenta rastrear por LBS.");
            String notReachableState = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "tracking.lbs.state.notreachable");

            LocationResponse responseLbs;
            try {
                responseLbs = getFacadeContainer().getLbsGwServiceAPI().locate(
                        selectedUserphone.getCellphoneNum().toString());
            } catch (Exception e) {
                responseLbs = null;
            }
            if (responseLbs != null
                && !responseLbs.getState().equals(notReachableState)) {
                getFacadeContainer().getNotifier().info(
                        getClass(),
                        selectedUserphone.getCellphoneNum().toString(),
                        MessageFormat.format(
                                "Se rastreo a la linea. CellId:{0}. LAC:{1}",
                                responseLbs.getCellId(), responseLbs.getLca()));

                Integer iPosicion = responseLbs.getCellId();
                Integer iLC = Integer.parseInt(responseLbs.getLca());
                Integer duration = Integer.parseInt(responseLbs.getAge());

                Cell cell = null;
                cell = getFacadeContainer().getCellAPI().getCellId(
                        selectedUserphone, iLC, iPosicion);

                if (cell != null) {
                    Message message = new Message();
                    message.setDateinDat(new Date());
                    message.setDateoutDat(new Date());
                    message.setDestinationChr(getFacadeContainer().getGlobalParameterAPI().findByCode(
                            "sms.shortnumber"));
                    message.setLengthoutNum(0);
                    String messageIn = getFacadeContainer().getGlobalParameterAPI().findByCode(
                            "trackingondemand.lbs.messageIn");
                    message.setMessageinChr(messageIn);
                    message.setLengthinNum(messageIn.length());
                    message.setMessageoutChr(null);
                    message.setOriginChr(CellPhoneNumberUtil.correctMsisdnToLocalFormat(selectedUserphone.getCellphoneNum().toString()));
                    message.setUserphone(selectedUserphone);
                    message.setUserweb(null);
                    message.setClient(selectedUserphone.getClient());
                    message.setCell(cell);
                    message.setApplication(getFacadeContainer().getApplicationAPI().find(
                            6L));
                    getFacadeContainer().getMessageAPI().create(message);

                    GregorianCalendar gc = new GregorianCalendar();
                    gc.add(Calendar.MINUTE, -duration);
                    SimpleDateFormat sdf = new SimpleDateFormat(getFacadeContainer().getGlobalParameterAPI().findByCode(
                            "format.datetime.output.default"));
                    ServiceValue trackingValue = new ServiceValue();
                    trackingValue.setUserphone(selectedUserphone);
                    trackingValue.setService(getFacadeContainer().getServiceAPI().find(
                            4L));
                    trackingValue.setMessage(message);
                    trackingValue.setColumn1Chr(sdf.format(gc.getTime()));
                    trackingValue.setColumn2Chr("1");
                    trackingValue.setColumn3Chr(trackingStatusMessage);
                    trackingValue.setEnabledChr(true);
                    getFacadeContainer().getServiceValueAPI().create(
                            trackingValue);
                    getNextServiceValueByUserphone().put(selectedUserphone,
                            trackingValue);

                } else {

                    trackingStatusMessage = "tracking.status.NoLocationNoCellInfo";

                    Message message = new Message();
                    message.setDateinDat(new Date());
                    message.setDateoutDat(new Date());
                    message.setDestinationChr(getFacadeContainer().getGlobalParameterAPI().findByCode(
                            "sms.shortnumber"));
                    message.setLengthoutNum(0);
                    String messageIn = getFacadeContainer().getGlobalParameterAPI().findByCode(
                            "trackingondemand.lbs.messageIn");
                    message.setMessageinChr(messageIn);
                    message.setLengthinNum(messageIn.length());
                    message.setMessageoutChr(null);
                    message.setOriginChr(CellPhoneNumberUtil.correctMsisdnToLocalFormat(selectedUserphone.getCellphoneNum().toString()));
                    message.setUserphone(selectedUserphone);
                    message.setUserweb(null);
                    message.setClient(selectedUserphone.getClient());
                    message.setCell(null);
                    message.setApplication(getFacadeContainer().getApplicationAPI().find(
                            6L));
                    getFacadeContainer().getMessageAPI().create(message);

                    ServiceValue trackingValue = new ServiceValue();
                    trackingValue.setUserphone(selectedUserphone);
                    trackingValue.setService(getFacadeContainer().getServiceAPI().find(
                            4L));
                    trackingValue.setMessage(message);
                    trackingValue.setColumn2Chr("0");
                    trackingValue.setColumn3Chr(trackingStatusMessage);
                    trackingValue.setEnabledChr(true);
                    getFacadeContainer().getServiceValueAPI().create(
                            trackingValue);
                    getNextServiceValueByUserphone().put(selectedUserphone,
                            trackingValue);

                }
            } else {

                trackingStatusMessage = "tracking.status.NoLocation";

                Message message = new Message();
                message.setDateinDat(new Date());
                message.setDateoutDat(new Date());
                message.setDestinationChr(getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "sms.shortnumber"));
                message.setLengthoutNum(0);
                String messageIn = getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "trackingondemand.lbs.messageIn");
                message.setMessageinChr(messageIn);
                message.setLengthinNum(messageIn.length());
                message.setMessageoutChr(null);
                message.setOriginChr(CellPhoneNumberUtil.correctMsisdnToLocalFormat(selectedUserphone.getCellphoneNum().toString()));
                message.setUserphone(selectedUserphone);
                message.setUserweb(null);
                message.setClient(selectedUserphone.getClient());
                message.setCell(null);
                message.setApplication(getFacadeContainer().getApplicationAPI().find(
                        6L));
                getFacadeContainer().getMessageAPI().create(message);

                ServiceValue trackingValue = new ServiceValue();
                trackingValue.setUserphone(selectedUserphone);
                trackingValue.setService(getFacadeContainer().getServiceAPI().find(
                        4L));
                trackingValue.setMessage(message);
                trackingValue.setColumn2Chr("0");
                trackingValue.setColumn3Chr(trackingStatusMessage);
                trackingValue.setEnabledChr(true);
                getFacadeContainer().getServiceValueAPI().create(trackingValue);
                getNextServiceValueByUserphone().put(selectedUserphone,
                        trackingValue);

            }

        } catch (GenericFacadeException gfc) {
            gfc.printStackTrace();
        }

    }

    private void sendTrackingMessage(Userphone selectedUserphone) throws Exception {

        String baseURI = getFacadeContainer().getGlobalParameterAPI().findByCode(
                "tracking.ota.url");
        URL url = new URL(baseURI);

        // note that plus sign needs to be encoded
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type",
                "multipart/related; boundary=asdlfkjiurwghasf;"
                    + "  type=\"application/xml\"");
        conn.setRequestProperty("Accept",
                "multipart/related; boundary=asdlfkjiurwghasf;"
                    + "  type=\"application/xml\"");

        String wml = StringUtils.convertStreamToString(this.getClass().getResourceAsStream(
                "/resources/push.wib"));
        wml = wml.replace(":cellphoneNum",
                selectedUserphone.getCellphoneNum().toString());
        wml = wml.replace(
                ":shortNumber",
                getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "sms.shortnumber"));
        conn.setRequestProperty("Content-Length",
                Integer.toString(wml.length()));

        conn.setDoOutput(true);
        conn.setDoInput(true);
        OutputStream out = conn.getOutputStream();
        OutputStreamWriter wout = new OutputStreamWriter(out, "UTF-8");

        wout.write(wml);
        wout.flush();
        wout.close();

        InputStream in = conn.getInputStream();
        int responseCode = conn.getResponseCode();
        String responseString = conn.getResponseMessage();

        System.out.println(getFacadeContainer().getI18nAPI().iValue(
                "web.client.backingBean.tracking.messages.Response")
            + responseCode + " - " + responseString);

        in.close();
        out.close();
        conn.disconnect();
    }

    private void sendAndroidTrackingMessage(Userphone userphone) {
        try {
            String key = userphone.getCellphoneNum().toString();
            String locationMessage = "4%*LOC_NO_WAIT%*{0,number,#}";
            locationMessage = MessageFormat.format(locationMessage,
                    new Date().getTime());
            locationMessage = Cypher.encrypt(key, locationMessage);

            getFacadeContainer().getSmsQueueAPI().sendToJMS(
                    CellPhoneNumberUtil.correctMsisdnToLocalFormat(String.valueOf(userphone.getCellphoneNum())),
                    getFacadeContainer().getApplicationAPI().find(3L),
                    locationMessage);

        } catch (SMSTransmitterException e) {

            getFacadeContainer().getNotifier().error(
                    getClass(),
                    null,
                    MessageFormat.format(
                            "No se rastro a la linea. Userphone:{0}", userphone),
                    e);

        } catch (Exception e) {

            getFacadeContainer().getNotifier().error(
                    getClass(),
                    null,
                    MessageFormat.format(
                            "No se rastro a la linea. Userphone:{0}", userphone),
                    e);

        }
    }

    public Map<Long, String> getMapUserphoneTrackingType() {
        return mapUserphoneTrackingType;
    }

    public void setMapUserphoneTrackingType(Map<Long, String> mapUserphoneTrackingType) {
        this.mapUserphoneTrackingType = mapUserphoneTrackingType;
    }

    public Map<Userphone, ServiceValue> getMaxServiceValueByUserphone() {
        if (maxServiceValueByUserphone == null) {
            maxServiceValueByUserphone = new HashMap<Userphone, ServiceValue>();
        }
        return maxServiceValueByUserphone;
    }

    public void setMaxServiceValueByUserphone(Map<Userphone, ServiceValue> maxServiceValueByUserphone) {
        this.maxServiceValueByUserphone = maxServiceValueByUserphone;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public Double getSumValueLbs() {
        return sumValueLbs;
    }

    public void setSumValueLbs(Double sumValueLbs) {
        this.sumValueLbs = sumValueLbs;
    }

    public Double getSumValueAndroid() {
        return sumValueAndroid;
    }

    public void setSumValueAndroid(Double sumValueAndroid) {
        this.sumValueAndroid = sumValueAndroid;
    }

    public Map<Object, Object> getNextServiceValueByUserphone() {
        if (nextServiceValueByUserphone == null) {
            nextServiceValueByUserphone = new HashMap<Object, Object>();
        }
        return nextServiceValueByUserphone;
    }

    public void setNextServiceValueByUserphone(Map<Object, Object> nextServiceValueByUserphone) {
        this.nextServiceValueByUserphone = nextServiceValueByUserphone;
    }

    private String getTrackingType() {
        if (trackingType == null) {
            try {
                trackingType = getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "trackingondemand.type");
            } catch (Exception e) {
                getFacadeContainer().getNotifier().signal(getClass(),
                        Action.ERROR, e.getMessage(), e);
                trackingType = "LBS";
            }
        }
        return trackingType;
    }

    public void setTrackingType(String trackingType) {
        this.trackingType = trackingType;
    }

    public Map<Object, Object> getHashMarkerUser() {
        if (hashMarkerUser == null) {
            hashMarkerUser = new HashMap<Object, Object>();
        }
        return hashMarkerUser;
    }

    public void setHashMarkerUser(Map<Object, Object> hashMarkerUser) {
        this.hashMarkerUser = hashMarkerUser;
    }

    public Map<Object, Object> getHashNotMarkerUser() {
        if (hashNotMarkerUser == null) {
            hashNotMarkerUser = new HashMap<Object, Object>();
        }
        return hashNotMarkerUser;
    }

    public void setHashNotMarkerUser(Map<Object, Object> hashNotMarkerUser) {
        this.hashNotMarkerUser = hashNotMarkerUser;
    }

    public Map<Object, String> getHashUsersTracking() {
        if (hashUsersTracking == null) {
            hashUsersTracking = new HashMap<Object, String>();
        }
        return hashUsersTracking;
    }

    public void setHashUsersTracking(Map<Object, String> hashUsersTracking) {
        this.hashUsersTracking = hashUsersTracking;
    }

    class MyTask extends TimerTask {

        @Override
        public void run() {
            progress = progress + sumValueAndroid;
        }
    }
}
