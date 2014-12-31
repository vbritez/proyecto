package com.tigo.cs.view.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
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
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.validation.ConstraintViolationException;

import org.primefaces.context.RequestContext;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Polygon;

import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.commons.ejb.SMSTransmitterException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.PrimarySortedFieldNotFoundException;
import com.tigo.cs.commons.jpa.Searchable;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.util.CellPhoneNumberUtil;
import com.tigo.cs.commons.util.Cypher;
import com.tigo.cs.commons.util.ListDataModelViewCsTigo;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.commons.util.PaginationHelper;
import com.tigo.cs.commons.util.SortHelper;
import com.tigo.cs.commons.util.StringUtils;
import com.tigo.cs.domain.Application;
import com.tigo.cs.domain.Cell;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.Message;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.facade.ApplicationFacade;
import com.tigo.cs.facade.CellFacade;
import com.tigo.cs.facade.GlobalParameterFacade;
import com.tigo.cs.facade.MessageFacade;
import com.tigo.cs.facade.ws.LbsGwServiceFacade;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.ApplicationBean;
import com.tigo.cs.view.metadata.AbstractCrudBean;
import com.tigo.lbs.ws.LocationResponse;

@ManagedBean(name = "trackingOnDemandBean")
@ViewScoped
public class TrackingOnDemandBean extends AbstractServiceBean {

    private static final long serialVersionUID = 3442076598786367577L;
    public static final int COD_SERVICE = 4;
    private static long TIMEOUT = 30000L;
    private static long MAX_TRACK_IDLE_TIME = 30000L;
    private static long ANDROID_MAX_TRACK_IDLE_TIME = 70000L;

    private static final String LBS = "LBS";
    private ServiceValue trackingValue;

    private Boolean showClientMarkers = true;
    private ApplicationBean applicationBean;
    private Application application;
    private DataModel<Userphone> dataModel;
    private String rowQuantSelected;
    private SortHelper sortHelper;
    private String filterSelectedField;
    private List<SelectItem> filterFields;
    private int lastActualPage = -1;
    private Userphone entity;

    private Map<Userphone, ServiceValue> maxServiceValueByUserphone;

    private String trackingType;

    private Double progress = 0.0;
    private Double sumValueLbs = 0.0;
    private Double sumValueAndroid = 0.0;
    private Boolean onlyAndroid;

    @EJB
    private FacadeContainer facadeContainer;
    @EJB
    private GlobalParameterFacade globalParameterFacade;
    @EJB
    private CellFacade cellFacade;
    @EJB
    private ApplicationFacade applicationFacade;
    @EJB
    private MessageFacade messageFacade;
    @EJB
    private LbsGwServiceFacade lbsGwServiceFacade;
    private Map<Long, String> mapUserphoneTrackingType = new HashMap<Long, String>();

    public TrackingOnDemandBean() {
    }

    @Override
    @PostConstruct
    public void init() {

        setCodService(COD_SERVICE);
        setShowMapOnHeader(true);
        getMapModel();

        setPaginationHelper(null);
        setFilterSelectedField("-1");
        setFilterCriteria("");

        sortHelper = new SortHelper();
        try {
            sortHelper.setField(getPrimarySortedField());
            sortHelper.setAscending(primarySortedFieldAsc);
        } catch (PrimarySortedFieldNotFoundException ex) {
            java.util.logging.Logger.getLogger(AbstractCrudBean.class.getName()).log(
                    Level.SEVERE,
                    MessageFormat.format(
                            i18n.iValue("web.client.backingBean.message.Error"),
                            ex.getMessage()), ex);
        }

        applicationBean = ApplicationBean.getInstance();
        application = applicationBean.getApplication();
        addClientMarker();
    }

    public Double getProgress() {
        if (progress > 0.0) {
            FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(
                    "serviceForm:mapGroup");
        }
        if (sumValueLbs != null && sumValueLbs > 0.0) {
            RequestContext.getCurrentInstance().execute("blocker.hide();");
        }
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
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

    public Boolean getShowClientMarkers() {
        if (showClientMarkers == null)
            showClientMarkers = true;
        return showClientMarkers;
    }

    public void setShowClientMarkers(Boolean showClientMarkers) {
        this.showClientMarkers = showClientMarkers;
    }

    @Override
    public String getCabDetailReportWhereCriteria() {
        return "";
    }

    @Override
    public String getDetailWhereCriteria() {
        return "";
    }

    @Override
    public String getDetailReportWhereCriteria() {
        return "";
    }

    @Override
    public void deleteClientMarker() {
        if (isClientMarker()) {
            Client client = getUserweb().getClient();
            Double latitude = getSelectedMarker().getLatlng().getLat();
            Double longitude = getSelectedMarker().getLatlng().getLng();
            try {
                mapMarksFacade.removeByClientLatLng(client, latitude, longitude);

            } catch (ConstraintViolationException ex) {
                setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletringConstraintError"));
                return;
            } catch (Exception e) {
                notifier.log(getClass(), null, Action.ERROR, e.getMessage());
                setErrorMessage(i18n.iValue("web.client.backingbean.error.restriction"));
                ;
                return;
            }
            setMapModel(null);
            getMapModel();
            if (getNextServiceValueByUserphone().size() > 0) {
                for (Object t : getNextServiceValueByUserphone().values()) {
                    localizar((ServiceValue) t);
                }
            } else if (dataModel != null) {
                showHeaderMap();
            }
            if (showClientMarkers)
                addClientMarker();
        }
    }

    private void deleteClientMarkers() {
        setMapModel(null);
        getMapModel();
        if (getNextServiceValueByUserphone().size() > 0) {
            for (Object t : getNextServiceValueByUserphone().values()) {
                localizar((ServiceValue) t);
            }
        } else if (dataModel != null) {
            showHeaderMap();
        }
    }

    public List<Userphone> getSelectedItemList() {
        List<Userphone> uList = new ArrayList<Userphone>();
        for (Userphone userphone : dataModel) {
            if (getSelectedItems().get(userphone.getUserphoneCod()) != null
                && getSelectedItems().get(userphone.getUserphoneCod())) {
                uList.add(userphone);
            }
        }
        return uList;
    }

    public String showHideClientMarkers() {
        if (showClientMarkers)
            addClientMarker();
        else {
            deleteClientMarkers();
        }
        return null;
    }

    public String trackMobiles() {
        showLocalize = true;
        hashUsersTracking = null;
        hashMarkerUser = null;
        hashNotMarkerUser = null;
        lastMarker = null;
        onlyAndroid = false;

        try {
            MAX_TRACK_IDLE_TIME = Long.parseLong(globalParameterFacade.findByCode("tracking.timeout.ota"));
            ANDROID_MAX_TRACK_IDLE_TIME = Long.parseLong(globalParameterFacade.findByCode("tracking.timeout.android"));

            TIMEOUT = MAX_TRACK_IDLE_TIME;
        } catch (GenericFacadeException ex) {
            notifier.signal(getClass(), Action.WARNING,
                    "No se recuperaron los parametros globales de Time Out de Rastreo");
        }

        notifier.info(getClass(), null, "Se rastrearan a las lineas");
        List<Userphone> selectedUserphones = getSelectedItemList();

        if (selectedUserphones == null || selectedUserphones.size() == 0) {
            setWarnMessage(i18n.iValue("tracking.message.MustSelectAtLeastOneUserphone"));
            return null;
        }

        List<Userphone> orderedSelectedUserphones = orderSelectedUserphoneList(selectedUserphones);
        progress = 0.0;
        sumValueLbs = 100.0 / selectedUserphones.size();

        setMapModel(new DefaultMapModel());
        notifier.info(getClass(), null,
                "En el caso que no se rastreen a todas las lineas se realizara un reintento.");
        return trackMobiles(orderedSelectedUserphones, true);
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

    private boolean includeLbsTracking(List<Userphone> selectedUserphones) {
        for (Userphone u : selectedUserphones) {
            if (mapUserphoneTrackingType.get(u.getUserphoneCod()).equals(
                    "tracking.lbs")) {
                return true;
            }
        }
        return false;
    }

    public String trackMobiles(List<Userphone> selectedUserphones, Boolean retry) {
        if (selectedUserphones == null || selectedUserphones.size() == 0) {
            return null;
        }

        /*
         * variable que controla la cantidad de solicitudes al LBS que fueron
         * respondidas
         */
        notifier.info(getClass(), null, MessageFormat.format(
                "Se obtiene la lista de lineas a rastrear. Cantidad:{0}",
                selectedUserphones.size()));

        if (retry) {
            notifier.info(
                    getClass(),
                    null,
                    "Se recrean los objetos que almacenan la informacion de los ultimos rastreos de la linea. ");
            setMaxServiceValueByUserphone(null);
            setNextServiceValueByUserphone(null);
        }

        for (Userphone u : selectedUserphones) {

            notifier.info(
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
                && (facadeContainer.getUserphoneAPI().getAndroidEnabledTracking(
                        u.getUserphoneCod()) || (!facadeContainer.getUserphoneAPI().getAndroidEnabledTracking(
                        u.getUserphoneCod()) && !getTrackingType().trim().equals(
                        LBS)))) {
                getMaxServiceValueByUserphone().put(u, findMaxServiceValue(u));
            }
        }

        boolean needToWait = false;
        for (Userphone u : selectedUserphones) {

            notifier.info(getClass(), null, MessageFormat.format(
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
            notifier.info(getClass(), null,
                    "Se esperara un tiempo antes de buscar las lineas.");
            waitForNextServiceValueByUserphone();
        }

        List<Userphone> retryUserphone = new ArrayList<Userphone>();
        for (Userphone u : selectedUserphones) {
            if (!getNextServiceValueByUserphone().containsKey(u)) {
                if (!retry) {
                    setWarnMessage(MessageFormat.format(
                            i18n.iValue("web.client.backingBean.tracking.messages.CouldNotLocateLine"),
                            u.getCellphoneNum().toString()));
                    notifier.signal(
                            getClass(),
                            Action.WARNING,
                            MessageFormat.format(
                                    i18n.iValue("web.client.backingBean.tracking.messages.CouldNotLocateLine"),
                                    u.getCellphoneNum().toString()));
                } else {
                    retryUserphone.add(u);
                }

            }
        }
        if (retry && retryUserphone.size() > 0) {
            trackMobiles(retryUserphone, false);
        }

        if (getNextServiceValueByUserphone().values().size() > 0) {
            for (Object trackingValue : getNextServiceValueByUserphone().values()) {

                if (((ServiceValue) trackingValue).getColumn2Chr().equals("1")) {
                    notifier.signal(
                            getClass(),
                            Action.INFO,
                            MessageFormat.format(
                                    i18n.iValue("web.client.backingBean.tracking.messages.LineWasLocated"),
                                    ((ServiceValue) trackingValue).getUserphone().getCellphoneNum().toString()));
                } else if (((ServiceValue) trackingValue).getColumn2Chr().equals(
                        "0")) {
                    getHashUsersTracking().put(
                            ((ServiceValue) trackingValue).getUserphone(), "2");
                    getHashNotMarkerUser().put(
                            ((ServiceValue) trackingValue).getUserphone(),
                            trackingValue);
                    notifier.signal(
                            getClass(),
                            Action.INFO,
                            MessageFormat.format(
                                    i18n.iValue("web.client.backingBean.tracking.messages.CouldNotLocateLine"),
                                    ((ServiceValue) trackingValue).getUserphone().getCellphoneNum().toString()));
                }
            }
        }

        progress = 100.0;
        return null;
    }

    public void onComplete() {
        setInfoMessage(i18n.iValue("tracking.message.TrackingHasBeenCompleted"));
        progress = 100.0;
        return;
    }

    private String getTrackingType() {
        if (trackingType == null) {
            try {
                trackingType = globalParameterFacade.findByCode("trackingondemand.type");
            } catch (Exception e) {
                notifier.signal(getClass(), Action.ERROR, e.getMessage(), e);
                trackingType = "LBS";
            }
        }
        return trackingType;
    }

    private ServiceValue findMaxServiceValue(Userphone userphone) {
        return facade.find(facade.getMaxServicevalueToday(userphone));
    }

    private void waitForNextServiceValueByUserphone() {
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

            if (getNextServiceValueByUserphone().values().size() == getSelectedItemList().size()) {
                progress = 100.0;
                break;
            }
            for (Userphone u : getSelectedItemList()) {

                if (facadeContainer.getUserphoneAPI().getAndroidEnabledTracking(
                        u.getUserphoneCod())
                    || (!facadeContainer.getUserphoneAPI().getAndroidEnabledTracking(
                            u.getUserphoneCod()) && !getTrackingType().trim().equals(
                            LBS))) {
                    Long maxServiceValue = getMaxServiceValueByUserphone().get(
                            u) != null ? getMaxServiceValueByUserphone().get(u).getServicevalueCod() : null;
                    Long nextServiceValue = maxServiceValue != null ? facade.getMaxNextServicevalue(
                            u, maxServiceValue) : null;

                    if (nextServiceValue != null && !onlyAndroid) {
                        trackingValue = facade.getServicevalue(nextServiceValue);
                        getNextServiceValueByUserphone().put(u, trackingValue);

                        // progress = progress + sumValueLbs;
                        localizar(trackingValue);
                    }
                }

            }

            if (getNextServiceValueByUserphone().values().size() == getSelectedItemList().size()) {
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

    private boolean trackMobile(Userphone selectedUserphone, boolean forceLBS) {
        boolean needToWait = true;
        if (selectedUserphone != null) {
            boolean androidtrack = false;
            if (!forceLBS) {
                androidtrack = userPhoneFacade.getAndroidEnabledTracking(selectedUserphone.getUserphoneCod());
            }
            try {
                String trackingType = globalParameterFacade.findByCode("trackingondemand.type");
                if (androidtrack) {
                    TIMEOUT = ANDROID_MAX_TRACK_IDLE_TIME;
                    sendAndroidTrackingMessage(selectedUserphone);
                    notifier.signal(getClass(), Action.DEBUG,
                            "Tiene rastreo Android.");
                } else if (!getTrackingType().trim().equals(LBS)) {
                    sendTrackingMessage(selectedUserphone);
                    notifier.signal(getClass(), Action.DEBUG,
                            "No tiene rastreo Android.");
                } else if (getTrackingType().trim().equals(LBS)
                    && !androidtrack) {
                    lbsTracking(selectedUserphone, forceLBS);

                    /* Se incrementa en uno el progress, y se localiza el sv */
                    progress = progress + sumValueLbs;
                    ServiceValue sv = (ServiceValue) getNextServiceValueByUserphone().get(
                            selectedUserphone);
                    localizar(sv);

                    needToWait = false;
                }
                /*
                 * 
                 * Se recupera el modo de rastreo online: OTA o LBS
                 */

                notifier.signal(getClass(), Action.DEBUG,
                        "Tipo de rastreo: ".concat(trackingType));

            } catch (Exception ex) {
                notifier.signal(getClass(), Action.ERROR, ex.getMessage(), ex);
            }
        } else {
            setWarnMessage(i18n.iValue("web.client.backingBean.tracking.messages.MustSelectUser"));
        }
        return needToWait;
    }

    private void lbsTracking(Userphone selectedUserphone, boolean forceLBS) {

        try {

            String trackingStatusMessage = "tracking.status.LBS";
            if (forceLBS) {
                trackingStatusMessage = "tracking.status.AndroidNoApp";
            }

            notifier.signal(getClass(), Action.DEBUG,
                    "Se intenta rastrear por LBS.");
            String notReachableState = globalParameterFacade.findByCode("tracking.lbs.state.notreachable");

            LocationResponse responseLbs;
            try {
                responseLbs = lbsGwServiceFacade.locate(selectedUserphone.getCellphoneNum().toString());
            } catch (Exception e) {
                responseLbs = null;
            }
            if (responseLbs != null
                && !responseLbs.getState().equals(notReachableState)) {
                notifier.info(getClass(),
                        selectedUserphone.getCellphoneNum().toString(),
                        MessageFormat.format(
                                "Se rastreo a la linea. CellId:{0}. LAC:{1}",
                                responseLbs.getCellId(), responseLbs.getLca()));

                Integer iPosicion = responseLbs.getCellId();
                Integer iLC = Integer.parseInt(responseLbs.getLca());
                Integer duration = Integer.parseInt(responseLbs.getAge());

                Cell cell = null;
                cell = cellFacade.getCellId(selectedUserphone, iLC, iPosicion);

                if (cell != null) {
                    Message message = new Message();
                    message.setDateinDat(new Date());
                    message.setDateoutDat(new Date());
                    message.setDestinationChr(applicationBean.getSmscShortNumber());
                    message.setLengthoutNum(0);
                    String messageIn = globalParameterFacade.findByCode("trackingondemand.lbs.messageIn");
                    message.setMessageinChr(messageIn);
                    message.setLengthinNum(messageIn.length());
                    message.setMessageoutChr(null);
                    message.setOriginChr(CellPhoneNumberUtil.correctMsisdnToLocalFormat(selectedUserphone.getCellphoneNum().toString()));
                    message.setUserphone(selectedUserphone);
                    message.setUserweb(null);
                    message.setClient(selectedUserphone.getClient());
                    message.setCell(cell);
                    message.setApplication(applicationFacade.find(6L));
                    messageFacade.create(message);

                    GregorianCalendar gc = new GregorianCalendar();
                    gc.add(Calendar.MINUTE, -duration);
                    SimpleDateFormat sdf = new SimpleDateFormat(applicationBean.getDefaultOutputDateTimeFormat());
                    trackingValue = new ServiceValue();
                    trackingValue.setUserphone(selectedUserphone);
                    trackingValue.setService(serviceFacade.find(4L));
                    trackingValue.setMessage(message);
                    trackingValue.setColumn1Chr(sdf.format(gc.getTime()));
                    trackingValue.setColumn2Chr("1");
                    trackingValue.setColumn3Chr(trackingStatusMessage);
                    trackingValue.setEnabledChr(true);
                    facade.create(trackingValue);
                    getNextServiceValueByUserphone().put(selectedUserphone,
                            trackingValue);
                } else {

                    trackingStatusMessage = "tracking.status.NoLocationNoCellInfo";

                    Message message = new Message();
                    message.setDateinDat(new Date());
                    message.setDateoutDat(new Date());
                    message.setDestinationChr(applicationBean.getSmscShortNumber());
                    message.setLengthoutNum(0);
                    String messageIn = globalParameterFacade.findByCode("trackingondemand.lbs.messageIn");
                    message.setMessageinChr(messageIn);
                    message.setLengthinNum(messageIn.length());
                    message.setMessageoutChr(null);
                    message.setOriginChr(CellPhoneNumberUtil.correctMsisdnToLocalFormat(selectedUserphone.getCellphoneNum().toString()));
                    message.setUserphone(selectedUserphone);
                    message.setUserweb(null);
                    message.setClient(selectedUserphone.getClient());
                    message.setCell(null);
                    message.setApplication(applicationFacade.find(6L));
                    messageFacade.create(message);

                    trackingValue = new ServiceValue();
                    trackingValue.setUserphone(selectedUserphone);
                    trackingValue.setService(serviceFacade.find(4L));
                    trackingValue.setMessage(message);
                    trackingValue.setColumn2Chr("0");
                    trackingValue.setColumn3Chr(trackingStatusMessage);
                    trackingValue.setEnabledChr(true);
                    facade.create(trackingValue);
                    getNextServiceValueByUserphone().put(selectedUserphone,
                            trackingValue);

                }
            } else {

                trackingStatusMessage = "tracking.status.NoLocation";

                Message message = new Message();
                message.setDateinDat(new Date());
                message.setDateoutDat(new Date());
                message.setDestinationChr(applicationBean.getSmscShortNumber());
                message.setLengthoutNum(0);
                String messageIn = globalParameterFacade.findByCode("trackingondemand.lbs.messageIn");
                message.setMessageinChr(messageIn);
                message.setLengthinNum(messageIn.length());
                message.setMessageoutChr(null);
                message.setOriginChr(CellPhoneNumberUtil.correctMsisdnToLocalFormat(selectedUserphone.getCellphoneNum().toString()));
                message.setUserphone(selectedUserphone);
                message.setUserweb(null);
                message.setClient(selectedUserphone.getClient());
                message.setCell(null);
                message.setApplication(applicationFacade.find(6L));
                messageFacade.create(message);

                trackingValue = new ServiceValue();
                trackingValue.setUserphone(selectedUserphone);
                trackingValue.setService(serviceFacade.find(4L));
                trackingValue.setMessage(message);
                trackingValue.setColumn2Chr("0");
                trackingValue.setColumn3Chr(trackingStatusMessage);
                trackingValue.setEnabledChr(true);
                facade.create(trackingValue);
                getNextServiceValueByUserphone().put(selectedUserphone,
                        trackingValue);

            }

        } catch (GenericFacadeException gfc) {
            gfc.printStackTrace();
        }

    }

    private void sendTrackingMessage(Userphone selectedUserphone) throws Exception {

        String baseURI = facadeContainer.getGlobalParameterAPI().findByCode(
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
        wml = wml.replace(":shortNumber",
                ApplicationBean.getInstance().getSmscShortNumber());
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

        System.out.println(i18n.iValue("web.client.backingBean.tracking.messages.Response")
            + responseCode + " - " + responseString);

        in.close();
        out.close();
        conn.disconnect();
    }

    private void sendAndroidTrackingMessage(Userphone userphone) {
        try {
            String key = userphone.getCellphoneNum().toString();
            String locationMessage = "4%*LOC%*{0,number,#}";
            locationMessage = MessageFormat.format(locationMessage,
                    new Date().getTime());
            locationMessage = Cypher.encrypt(key, locationMessage);

            facadeContainer.getSmsQueueAPI().sendToJMS(
                    CellPhoneNumberUtil.correctMsisdnToLocalFormat(String.valueOf(userphone.getCellphoneNum())),
                    application, locationMessage);

        } catch (SMSTransmitterException e) {
        } catch (Exception e) {
        }
    }

    private void localizar(ServiceValue serviceValue) {
        try {
            if (getGeolocalizationAllowed()
                && serviceValue.getColumn2Chr().compareTo("1") == 0) {
                if ((serviceValue.getMessage().getCell() != null || (serviceValue.getMessage().getLatitude() != null && serviceValue.getMessage().getLongitude() != null))) { // inicio
                    boolean oneInsideBounds = false;
                    Map<String, ArrayList<Marker>> positions = new HashMap<String, ArrayList<Marker>>();

                    // int nextMarkerCounter = 1;
                    Polygon polygonArea = null;
                    Marker markerArea = null;
                    Double latitude = serviceValue.getMessage().getLatitude();
                    Double longitude = serviceValue.getMessage().getLongitude();

                    // Message for marker
                    DateFormat sdf = new SimpleDateFormat(ApplicationBean.getInstance().getDefaultOutputDateTimeFormat());
                    String messDescrip = serviceValue.getMessage().getUserphone().getNameChr().concat(
                            " | ").concat(
                            sdf.format(serviceValue.getMessage().getDateinDat()));

                    String messageIn = null;
                    try {
                        messageIn = globalParameterFacade.findByCode("trackingondemand.lbs.messageIn");
                    } catch (Exception e) {
                        messageIn = "";
                    }
                    if (serviceValue.getMessage().getMessageinChr() != null
                        && serviceValue.getMessage().getMessageinChr().equals(
                                messageIn)) {
                        String trackingLbsMsg = "";
                        try {
                            trackingLbsMsg = globalParameterFacade.findByCode("tracking.lbs.description");
                        } catch (Exception e) {
                            trackingLbsMsg = "";
                        }

                        messDescrip = messDescrip.concat(" | ").concat(
                                trackingLbsMsg).concat(
                                serviceValue.getColumn1Chr() != null ? serviceValue.getColumn1Chr() : "");
                    }

                    if (latitude != null && longitude != null) {
                        markerArea = getGPSCellAreaMarker(latitude, longitude,
                                messDescrip, "");
                    } else {

                        // Obtain latitude, longitude and azimuth from cell
                        latitude = serviceValue.getMessage().getCell().getLatitudeNum().doubleValue();
                        longitude = serviceValue.getMessage().getCell().getLongitudeNum().doubleValue();
                        double azimuth = serviceValue.getMessage().getCell().getAzimuthNum().doubleValue();
                        String siteCell = serviceValue.getMessage().getCell().getSiteChr();

                        if (!siteCell.toUpperCase().endsWith("O")) {
                            // SEGMENTED CELL

                            // Cell polygon
                            polygonArea = getCellAreaPolygon(latitude,
                                    longitude, azimuth);

                            // Cell marker
                            markerArea = getCellAreaMarker(latitude, longitude,
                                    azimuth, messDescrip, "");
                        } else {
                            // OMNIDIRECTIONAL CELL

                            // Cell polygon
                            polygonArea = getOmniCellAreaPolygon(latitude,
                                    longitude);

                            // Cell marker
                            markerArea = getOmniCellAreaMarker(latitude,
                                    longitude, messDescrip, "");
                        }

                        // Add the polygon
                        if (polygonArea != null) {
                            // if the polygon already exists, do not add.
                            Polygon existingPolygon = null;
                            for (int i = 0; i < getMapModel().getPolygons().size(); i++) {
                                if (getMapModel().getPolygons().get(i).getPaths().equals(
                                        polygonArea.getPaths())) {
                                    existingPolygon = getMapModel().getPolygons().get(
                                            i);
                                    break;
                                }
                            }

                            if (existingPolygon == null) {
                                getMapModel().addOverlay(polygonArea);
                            }
                        }

                    }// FIN DEL IF

                    // Add the marker
                    if (markerArea != null) {
                        // if the marker already exists, do not add, but instead
                        // add
                        // data to existing one.
                        Marker existingMarker = null;
                        for (int i = 0; i < getMapModel().getMarkers().size(); i++) {
                            if (getMapModel().getMarkers().get(i).getLatlng().equals(
                                    markerArea.getLatlng())) {
                                existingMarker = getMapModel().getMarkers().get(
                                        i);
                                break;
                            }
                        }

                        if (existingMarker == null) {
                            getMapModel().addOverlay(markerArea);
                            // getHashUsersTracking().put(serviceValue.getUserphone(),
                            // true);

                            getHashUsersTracking().put(
                                    serviceValue.getUserphone(),
                                    getListMessageWarning().contains(
                                            serviceValue.getColumn3Chr()) ? "3" : "1");
                            getHashMarkerUser().put(
                                    serviceValue.getUserphone(), markerArea);
                            // nextMarkerCounter++;
                            // Add current marker for drawing polyline later.
                            if (!positions.containsKey(serviceValue.getMessage().getOriginChr())) {
                                positions.put(
                                        serviceValue.getMessage().getOriginChr(),
                                        new ArrayList<Marker>());
                            }
                            positions.get(
                                    serviceValue.getMessage().getOriginChr()).add(
                                    markerArea);
                        } else {
                            if (!((String) existingMarker.getData()).equals("<br>"
                                + messDescrip)) {
                                existingMarker.setData(((String) existingMarker.getData()).concat(
                                        "<br>").concat(messDescrip));
                            }

                            // getHashUsersTracking().put(serviceValue.getUserphone(),
                            // true);

                            getHashUsersTracking().put(
                                    serviceValue.getUserphone(),
                                    getListMessageWarning().contains(
                                            serviceValue.getColumn3Chr()) ? "3" : "1");
                            getHashMarkerUser().put(
                                    serviceValue.getUserphone(), existingMarker);
                        }

                    }

                    // Add polyline
                    if (getMapModel().getMarkers().size() > 0) {
                        if (!oneInsideBounds) {
                            setMapCenter(getMapModel().getMarkers().get(0).getLatlng());
                        }
                        if (getMapModel().getMarkers().size() > 1) {
                            addMarkersPolylineDiferent(positions, getMapModel());
                        }
                    }
                }

                if (showClientMarkers) {
                    addClientMarker();
                }
            }
        } catch (Exception e) {
            notifier.log(getClass(), null, Action.ERROR, e.getMessage());
        }
    }

    public Userphone getEntity() {
        return entity;
    }

    public void setEntity(Userphone entity) {
        this.entity = entity;
    }

    @Override
    public String getPrimarySortedField() throws PrimarySortedFieldNotFoundException {
        if (primarySortedField == null) {
            Field[] fieds = Userphone.class.getDeclaredFields();
            for (Field field : fieds) {
                PrimarySortedField annotation = field.getAnnotation(PrimarySortedField.class);
                if (annotation != null) {
                    primarySortedField = field.getName();
                    primarySortedFieldAsc = annotation.ascending();
                    break;
                }
            }
            if (primarySortedField == null) {
                throw new PrimarySortedFieldNotFoundException(Userphone.class);
            }
        }
        return primarySortedField;
    }

    // --------------------------------------------------------------------------------------
    // LIST AND TABLE METHODS
    // --------------------------------------------------------------------------------------

    @Override
    public PaginationHelper getPaginationHelper() {
        if (paginationHelper == null) {
            int pageSize = rowQuantSelected.length() > 0 ? Integer.valueOf(
                    rowQuantSelected).intValue() : 0;

            paginationHelper = new PaginationHelper(pageSize) {

                String innerWhere = null;
                Integer count = null;

                @Override
                public int getItemsCount() {

                    String where = null;
                    if (!getFilterSelectedField().equals("-1")
                        && getFilterCriteria().length() > 0) {
                        Class<?> fieldClass = getFieldType(getFilterSelectedField());

                        if (fieldClass.equals(Integer.class)
                            || fieldClass.equals(Long.class)
                            || fieldClass.equals(BigInteger.class)) {
                            where = " ,IN (o.clientServiceFunctionalityList) f where o.".concat(
                                    getFilterSelectedField()).concat(" = ").concat(
                                    getFilterCriteria());
                        } else if (fieldClass.equals(String.class)) {
                            where = " ,IN (o.clientServiceFunctionalityList) f where lower(o.".concat(
                                    getFilterSelectedField()).concat(
                                    ") LIKE '%").concat(
                                    getFilterCriteria().toLowerCase()).concat(
                                    "%'");
                        }
                    }
                    if (getAditionalFilter() != null
                        && getAditionalFilter().trim().length() > 0) {
                        if (where == null) {
                            where = " ,IN (o.clientServiceFunctionalityList) f where ";
                        } else {
                            where = where.concat(" AND ");
                        }
                        where = where.concat(" (").concat(
                                getAditionalFilter().trim()).concat(") ");
                    }

                    if (where == null) {
                        where = " ,IN (o.clientServiceFunctionalityList) f where ";
                    } else {
                        where = where.concat(" AND");
                    }
                    List<Classification> classifications = classificationFacade.findByUserwebWithChilds(getUserweb());
                    where = where.concat(" o.client.clientCod = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
                    where = where.concat(" AND o.enabledChr = true AND f.client = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
                    where += " AND f.serviceFunctionality.service.serviceCod = 4 AND f.serviceFunctionality.functionality.functionalityCod = 0 AND EXISTS ( SELECT u FROM Userphone u , IN (u.classificationList) cl WHERE u.userphoneCod = o.userphoneCod AND u.client = o.client AND cl.codClient = o.client AND cl in (:classifications)) ";
                    /*
                     * esto se verifica para no ejecutar la sentencia cada vez
                     * que se solicita la cantidad de registros a mostrar, se
                     * retorna el valor ya en memoria si la sentencia no cambio.
                     * Esto se realiza para no realizar mas de una vez la
                     * consulta a la base de datos
                     */

                    if (innerWhere == null) {
                        innerWhere = where;
                    } else {
                        if (innerWhere.compareTo(where) == 0) {
                            return count;
                        }
                    }
                    count = userPhoneFacade.count(where, classifications);
                    return count;
                }

                @Override
                public DataModel createPageDataModel() {
                    String where = null;
                    if (!getFilterSelectedField().equals("-1")
                        && getFilterCriteria().length() > 0) {
                        Class<?> fieldClass = getFieldType(getFilterSelectedField());

                        if (fieldClass.equals(Integer.class)
                            || fieldClass.equals(Long.class)
                            || fieldClass.equals(BigInteger.class)) {
                            where = "  ,IN (o.clientServiceFunctionalityList) f where o.".concat(
                                    getFilterSelectedField()).concat(" = ").concat(
                                    getFilterCriteria());
                        } else if (fieldClass.equals(String.class)) {
                            where = " ,IN (o.clientServiceFunctionalityList) f where lower(o.".concat(
                                    getFilterSelectedField()).concat(
                                    ") LIKE '%").concat(
                                    getFilterCriteria().toLowerCase()).concat(
                                    "%'");
                        }
                    }
                    if (getAditionalFilter() != null
                        && getAditionalFilter().trim().length() > 0) {
                        if (where == null) {
                            where = " ,IN (o.clientServiceFunctionalityList) f where ";
                        } else {
                            where = where.concat(" AND");
                        }
                        where = where.concat(" (").concat(
                                getAditionalFilter().trim()).concat(") ");
                    }

                    if (where == null) {
                        where = " ,IN (o.clientServiceFunctionalityList) f where ";
                    } else {
                        where = where.concat(" AND");
                    }

                    List<Classification> classifications = classificationFacade.findByUserwebWithChilds(getUserweb());

                    where = where.concat(" o.client.clientCod = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
                    where = where.concat(" AND o.enabledChr = true AND f.client = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
                    where += " AND f.serviceFunctionality.service.serviceCod = 4 AND f.serviceFunctionality.functionality.functionalityCod = 0 AND EXISTS ( SELECT u FROM Userphone u , IN (u.classificationList) cl WHERE u.userphoneCod = o.userphoneCod AND u.client = o.client AND cl.codClient = o.client AND cl in (:classifications)) ";

                    String orderby = "o.".concat(sortHelper.getField()).concat(
                            sortHelper.isAscending() ? " ASC" : " DESC");

                    return new ListDataModelViewCsTigo(userPhoneFacade.findRange(
                            new int[] { getPageFirstItem(), getPageFirstItem()
                                + getPageSize() }, where, orderby,
                            classifications));
                }
            };

            headerMetaDataFromModel();

            if (lastActualPage >= 0) {
                paginationHelper.setActualPage(lastActualPage);
                lastActualPage = -1;
            }
        }

        return paginationHelper;
    }

    @Override
    protected void setPaginationHelper(PaginationHelper paginationHelper) {
        this.paginationHelper = paginationHelper;
    }

    @Override
    public void headerMetaDataFromModel() {
        mapUserphoneTrackingType = new HashMap<Long, String>();
        for (Userphone u : getDataModel()) {
            try {
                if (facadeContainer.getUserphoneAPI().getAndroidEnabledTracking(
                        u.getUserphoneCod())) {
                    mapUserphoneTrackingType.put(u.getUserphoneCod(),
                            "tracking.android");
                } else {
                    mapUserphoneTrackingType.put(u.getUserphoneCod(),
                            "tracking.lbs");
                }
            } catch (Exception ex) {
                notifier.signal(
                        getClass(),
                        Action.ERROR,
                        SecurityBean.getInstance().getLoggedInUserClient(),
                        null,
                        getCurrentViewId(),
                        i18n.iValue("web.client.backingBean.visit.messages.ClientEntityValueIntegrationError"),
                        getSessionId(), getIpAddress(), ex);
            }
        }
    }

    public DataModel<Userphone> getDataModel() {
        if (dataModel == null) {
            dataModel = getPaginationHelper().createPageDataModel();
        }
        return dataModel;
    }

    public void setDataModel(DataModel<Userphone> dataModel) {
        this.dataModel = dataModel;
    }

    public List<SelectItem> getFilterFields() {
        if (filterFields == null) {
            filterFields = new ArrayList<SelectItem>();

            Field[] fieds = Userphone.class.getDeclaredFields();
            for (Field field : fieds) {
                Searchable annotation = field.getAnnotation(Searchable.class);
                if (annotation != null
                    && !field.getName().equalsIgnoreCase("descriptionChr")
                    && !field.getName().equalsIgnoreCase("client")) {
                    if (annotation.position() < 0
                        || annotation.position() > filterFields.size()) {
                        filterFields.add(new SelectItem(field.getName().concat(
                                !annotation.internalField().isEmpty() ? ".".concat(annotation.internalField()) : ""), i18n.iValue(annotation.label())));
                    } else {
                        filterFields.add(
                                annotation.position(),
                                new SelectItem(field.getName().concat(
                                        !annotation.internalField().isEmpty() ? ".".concat(annotation.internalField()) : ""), i18n.iValue(annotation.label())));
                    }
                }
            }
        }

        return filterFields;
    }

    public String getFilterSelectedField() {
        return filterSelectedField;
    }

    public void setFilterSelectedField(String filterSelectedField) {
        /*
         * Se puso esta verificacion porque en algun punto del jsf se setea a
         * null la variable filterSelectedField
         */
        if (filterSelectedField == null) {
            filterSelectedField = this.filterSelectedField != null ? this.filterSelectedField : "-1";
        }
        this.filterSelectedField = filterSelectedField;
    }

    public String nextPage() {
        getPaginationHelper().nextPage();
        dataModel = null; // For data model recreation
        selectedItems = null; // For clearing selection
        headerMetaDataFromModel();
        return null;
    }

    public String previousPage() {
        getPaginationHelper().previousPage();
        dataModel = null; // For data model recreation
        selectedItems = null; // For clearing selection
        headerMetaDataFromModel();
        return null;
    }

    // FILTER AND SORT METHODS
    // --------------------------------------------------------------------------------------
    @Override
    public String applyFilter() {
        if (!filterSelectedField.equals("-1")) {
            Class<?> fieldClass = getFieldType(filterSelectedField);

            if ((fieldClass.equals(Integer.class) && !NumberUtil.isInteger(getFilterCriteria()))
                || (fieldClass.equals(Long.class) && !NumberUtil.isLong(getFilterCriteria()))
                || (fieldClass.equals(BigInteger.class) && !NumberUtil.isBigInteger(getFilterCriteria()))) {
                setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustBeInteger"));
                setFilterCriteria("");
            } else {
                signalRead();
            }
            paginationHelper = null; // For pagination recreation
            dataModel = null; // For data model recreation
            selectedItems = null; // For clearing selection
        } else {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectFindOption"));
        }
        return null;
    }

    @Override
    public String cleanFilter() {
        filterSelectedField = "-1";
        setFilterCriteria("");
        paginationHelper = null; // For pagination recreation
        dataModel = null; // For data model recreation
        selectedItems = null; // For clearing selection
        return null;
    }

    protected Class<?> getFieldType(String fieldName) {
        try {
            String internalFieldName = "";
            if (fieldName.indexOf(".") >= 0) {
                internalFieldName = fieldName.substring(fieldName.indexOf(".") + 1);
                fieldName = fieldName.substring(0, fieldName.indexOf("."));
            }
            String methodName = "get".concat(
                    fieldName.substring(0, 1).toUpperCase()).concat(
                    fieldName.substring(1));

            if (internalFieldName.isEmpty()) {
                return Userphone.class.getMethod(methodName, new Class[0]).getReturnType();
            }

            Class<?> internalClass = Userphone.class.getMethod(methodName,
                    new Class[0]).getReturnType();
            methodName = "get".concat(
                    internalFieldName.substring(0, 1).toUpperCase()).concat(
                    internalFieldName.substring(1));

            return internalClass.getMethod(methodName, new Class[0]).getReturnType();
        } catch (Exception e) {
            return Object.class;
        }
    }

    public void applyQuantity() {
        paginationHelper = null; // For pagination recreation
        selectedItems = null; // For clearing selection
        dataModel = getPaginationHelper().createPageDataModel();
        headerMetaDataFromModel();
    }

    @Override
    public String getRowQuantSelected() {
        if (rowQuantSelected == null) {
            rowQuantSelected = ApplicationBean.getInstance().getDefaultServicePaginationPageSize();
        }
        return rowQuantSelected;
    }

    @Override
    public void setRowQuantSelected(String rowQuantSelected) {
        this.rowQuantSelected = rowQuantSelected;
    }

    @Override
    public List<SelectItem> getRowQuantList() {
        if (rowQuantList == null) {
            rowQuantList = new ArrayList<SelectItem>();
            rowQuantList.add(new SelectItem("15", "15"));
            rowQuantList.add(new SelectItem("25", "25"));
            rowQuantList.add(new SelectItem("50", "50"));
            rowQuantList.add(new SelectItem("100", "100"));
            if (showAllInQuantityList()) {
                rowQuantList.add(new SelectItem(String.valueOf(getPaginationHelper().getItemsCount()), i18n.iValue("web.client.backingBean.message.All")));
            }
        }
        return rowQuantList;
    }

    @Override
    public void setRowQuantList(List<SelectItem> rowQuantList) {
        this.rowQuantList = rowQuantList;
    }

    public Map<Long, String> getMapUserphoneTrackingType() {
        return mapUserphoneTrackingType;
    }

    public void setMapUserphoneTrackingType(Map<Long, String> mapUserphoneTrackingType) {
        this.mapUserphoneTrackingType = mapUserphoneTrackingType;
    }

    @Override
    public SortHelper getSortHelper() {
        return sortHelper;
    }

    @Override
    public void setSortHelper(SortHelper sortHelper) {
        this.sortHelper = sortHelper;
    }

    @Override
    public String applySort() {
        selectedItems = null; // For clearing selection
        dataModel = getPaginationHelper().createPageDataModel();
        headerMetaDataFromModel();
        return null;
    }

    class MyTask extends TimerTask {

        @Override
        public void run() {
            progress = progress + sumValueAndroid;
        }
    }

}
