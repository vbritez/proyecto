package com.tigo.cs.view.service;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.util.DateUtil;
import com.tigo.cs.commons.util.ListDataModelViewCsTigo;
import com.tigo.cs.commons.util.PaginationHelper;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.TrackingConfiguration;
import com.tigo.cs.domain.TrackingPeriod;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.facade.ClassificationFacade;
import com.tigo.cs.facade.GlobalParameterFacade;
import com.tigo.cs.facade.TrackingConfigurationFacade;
import com.tigo.cs.facade.TrackingPeriodFacade;
import com.tigo.cs.facade.UserphoneFacade;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.metadata.AbstractCrudBeanClient;

/**
 * 
 * @author Miguel Zorrilla
 * @version $Id: CrudMetaDataGuardBean.java 91 2011-11-28 20:16:12Z
 *          javier.kovacs $
 */
@ManagedBean(name = "trackingconfBean")
@ViewScoped
public class TrackingConfBean extends AbstractCrudBeanClient<Userphone, UserphoneFacade> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 296909095164968934L;
    @EJB
    private TrackingConfigurationFacade trackingconfFacade;
    @EJB
    private TrackingPeriodFacade trackingperiodFacade;
    @EJB
    private UserphoneFacade userphoneFacade;
    @EJB
    private ClassificationFacade classificationFacade;

    @EJB
    private GlobalParameterFacade gpFacade;

    private TrackingPeriod entityDetail;
    private MetaData userphoneMeta;
    private Userphone selectedUserphone;
    protected List<Userphone> userphoneList;
    private Boolean showConfigTracking = false;

    private Client client;
    private Integer daysOption;
    private ConfigurationData everyDayConf;
    private TrackingConfiguration everyDayTrackingConfig;
    private List<ConfigurationData> confList;
    private List<TrackingConfiguration> trackingConfigurationList;
    private List<String> supNumberList;
    private String selectedSupNumber;
    private BigInteger newSupNumber;
    private String concatSupNumbersStr;
    private Boolean deassignConfigurationsMassively = false;
    private Boolean assignConfigurationsMassively = false;
    private Boolean isEditing;
    private Date oldDateFrom;
    private Date oldDateTo;

    private Integer stepFactor;

    private PaginationHelper paginationHelper;

    public TrackingConfBean() {
        super(Userphone.class, UserphoneFacade.class);
        setAditionalFilter("o.enabledChr = true");
    }

    private DataModel<TrackingPeriod> dataModelDetail;
    private Map<Object, Boolean> selectedItemsDetail;
    private PaginationHelper paginationHelperDetail;
    private int lastActualPage = -1;
    private String tableDetailsTitle;

    public String getTableDetailsTitle() {
        tableDetailsTitle = i18n.iValue("web.client.trackingconf.screen.title.TotaLListOf");
        return tableDetailsTitle;
    }

    public void setTableDetailsTitle(String tableDetailsTitle) {
        this.tableDetailsTitle = tableDetailsTitle;
    }

    @Override
    public String cancelEditing() {
        showConfigTracking = false;
        super.cancelEditing();
        resetVariables();
        return null;
    }

    public String cancelEditingPhoneNumber() {
        super.cancelEditing();
        resetVariables();
        userphoneMeta = null;
        selectedUserphone = null;
        return null;
    }

    public void resetVariables() {
        deassignConfigurationsMassively = false;
        assignConfigurationsMassively = false;
        setSelectedItemsDetail(null);
    }

    public String newEntityDetail() {
        entityDetail = new TrackingPeriod();
        daysOption = -1;
        setSupNumberList(null);
        isEditing = false;

        // se setea porque no se muestra el periodo de validez
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        entityDetail.setDateFrom(gc.getTime());
        setOldDateFrom(gc.getTime());

        gc.set(Calendar.DAY_OF_MONTH, 31);
        gc.set(Calendar.MONTH, 11);
        gc.set(Calendar.YEAR, 2099);
        entityDetail.setDateTo(gc.getTime());
        setOldDateTo(gc.getTime());

        return null;
    }

    public String editEntityDetail() {
        if (getSelectedItemDetail() == null)
            return null;

        entityDetail = getSelectedItemDetail();
        trackingConfigurationList = trackingconfFacade.getTrackingConfListByTrackingPeriod(entityDetail.getTrackingPeriodCod());
        if (trackingConfigurationList.size() == 1
            && trackingConfigurationList.get(0).getDays().equals("1234567")) {
            everyDayConf = new ConfigurationData();
            everyDayTrackingConfig = trackingConfigurationList.get(0);
            daysOption = 1; // se indica que se selecciona la opcion 'Todos los
                            // dias'

            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(everyDayTrackingConfig.getStartTime());
            gc.add(Calendar.MINUTE,
                    everyDayTrackingConfig.getDuration().intValue());
            everyDayConf.setDuration(gc.getTime());
            // everyDayConf.setDuration(convertMinutesToHour(everyDayTrackingConfig.getDuration()));

            everyDayConf.setInterval(everyDayTrackingConfig.getIntervalTime());
            everyDayConf.setStartTime(everyDayTrackingConfig.getStartTime());
            everyDayConf.setTolerance(everyDayTrackingConfig.getToleranceTime());
        } else {
            daysOption = 2; // se indica que se selecciona la opcion
                            // 'Especificar dias de la semana'
            setConfList(null);
            // getConfList();
            for (TrackingConfiguration sc : trackingConfigurationList) {
                createConfigurationData(sc);
            }
        }

        oldDateFrom = entityDetail.getDateFrom();
        oldDateTo = entityDetail.getDateTo();
        isEditing = true;
        return null;
    }

    public String configTracking() {
        showConfigTracking = true;
        dataModelDetail = null;
        paginationHelperDetail = null;

        setEntity(new Userphone());
        return null;
    }

    public String cancelEditingDetail() {
        entityDetail = null;
        isEditing = false;
        clearData();
        resetVariables();
        return null;
    }

    public String deleteEditingDetail() {
        List<TrackingPeriod> selectedDetailList = getSelectedItemDetailList();
        if (selectedDetailList == null || selectedDetailList.size() == 0) {
            setWarnMessage(i18n.iValue("web.client.trackingconf.service.message.NotRegistrySelectedError"));
            return null;
        }

        /*
         * BORRA LOGICAMENTE LOS SHIFTPERIODS SELECCIONADOS, NO BORRA
         * FISICAMENTE. Es decir, pone status 0 a los shiftConfigurations
         * asignados al shiftperiod, y al shiftperiod.
         */
        try {
            for (TrackingPeriod sp : getSelectedItemDetailList()) {
                trackingperiodFacade.deleteEntity(sp.getTrackingPeriodCod());
                String msj = MessageFormat.format(
                        i18n.iValue("web.client.backingBean.message.RegistryDeletedBy"),"Conf. de Rastreos", sp.getTrackingPeriodCod(), SecurityBean.getInstance().getLoggedInUserClient().getUsernameChr());
                notifier.signal(
                        this.getClass(),
                        Action.DELETE,
                        SecurityBean.getInstance().getLoggedInUserClient(),
                        null,
                        getCurrentViewId(),
                        msj,
                        getSessionId(), 
                        getIpAddress());
            }
            clearData();
            resetPaginationDetail();
            setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletingSucces"));
            return null;
        } catch (GenericFacadeException e) {
            setErrorMessage(i18n.iValue("web.client.backingBean.message.Error"));
        } catch (Exception e) {
            setErrorMessage(i18n.iValue("web.client.backingBean.message.Error"));
        }
        return null;
    }

    public Boolean datosValidados() {
        // SE VALIDA QUE SE HAYA INGRESADO UNA DESCRIPCION Y LAS FECHAS
        if (entityDetail.getDescription() == null
            || entityDetail.getDescription().length() == 0
            || entityDetail.getDateFrom() == null
            || entityDetail.getDateTo() == null) {
            setWarnMessage(i18n.iValue("web.client.trackingconf.service.message.ValidationError"));
            return false;
        }

        // SE VALIDA QUE LA FECHA DESDE SEA MENOR O IGUAL A LA FECHA HASTA
        if (entityDetail.getDateFrom().compareTo(entityDetail.getDateTo()) > 0) {
            setWarnMessage(i18n.iValue("web.client.trackingconf.service.message.DateValidationMessage"));
            return false;
        }

        // SE VERIFICA QUE SE HAYA ELEGIDO UNA OPCION PARA LOS DIAS DE LA SEMANA
        if (daysOption == -1) {
            setWarnMessage(i18n.iValue("web.client.trackingconf.service.message.MustSelectDayOption"));
            return false;
        }

        // VERIFICAR QUE LA TOLERANCIA NO SEA MAYOR QUE EL INTERVALO PARA CONFIG
        // POR DIA
        if (confList != null && daysOption == 2) {
            Boolean atLeastOneSelected = false;
            for (ConfigurationData cd : confList) {
                if (cd.getChecked()) {
                    if (cd.getInterval() <= 0) {
                        setWarnMessage(i18n.iValue("web.client.trackingconf.service.message.ValidateIntervalGreaterZero"));
                        return false;
                    }

                    if (!isMultipleOf(cd.getInterval().intValue(),
                            getStepFactor())) {
                        setWarnMessage(MessageFormat.format(
                                i18n.iValue("web.client.trackingconf.service.message.ValidateIntervalMultipleOf"),
                                getStepFactor()));
                        return false;
                    }

                    // Calendar cal0000 = Calendar.getInstance();
                    // cal0000.set(Calendar.HOUR_OF_DAY, 0);
                    // cal0000.set(Calendar.MINUTE, 0);
                    // String time0000 =
                    // DateUtil.getFormattedDate(cal0000.getTime(), "HH:mm");
                    // String time0000_ =
                    // DateUtil.getFormattedDate(cd.getDuration(), "HH:mm");
                    // if (time0000.equals(time0000_)){
                    // setWarnMessage(i18n.iValue("web.client.guardroundconf.service.message.ValidateDurationGreaterZero"));
                    // return false;
                    // }

                    if (!validateDuration(cd.getStartTime(), cd.getDuration())) {
                        setWarnMessage(i18n.iValue("web.client.trackingconf.service.message.ValidateDurationError"));
                        return false;
                    }

                    atLeastOneSelected = true;
                }

            }
            if (!atLeastOneSelected) {
                setWarnMessage(i18n.iValue("web.client.trackingconf.service.message.AtLeastOneDaySelectedValidation"));
                return false;
            }
        }

        // SE VERIFICA QUE LA TOLERANCIA NO SEA MAYOR QUE EL INTERVALO PARA
        // TODOS LOS DIAS
        if (everyDayConf != null && daysOption == 1) {
            if (everyDayConf.getInterval() <= 0) {
                setWarnMessage(i18n.iValue("web.client.trackingconf.service.message.ValidateIntervalGreaterZero"));
                return false;
            }

            if (!isMultipleOf(everyDayConf.getInterval().intValue(),
                    getStepFactor())) {
                setWarnMessage(MessageFormat.format(
                        i18n.iValue("web.client.trackingconf.service.message.ValidateIntervalMultipleOf"),
                        getStepFactor()));
                return false;
            }

            // Calendar cal0000 = Calendar.getInstance();
            // cal0000.set(Calendar.HOUR_OF_DAY, 0);
            // cal0000.set(Calendar.MINUTE, 0);
            // String time0000 = DateUtil.getFormattedDate(cal0000.getTime(),
            // "HH:mm");
            // String time0000_ =
            // DateUtil.getFormattedDate(everyDayConf.getDuration(), "HH:mm");
            // if (time0000.equals(time0000_)){
            // setWarnMessage(i18n.iValue("web.client.guardroundconf.service.message.ValidateDurationGreaterZero"));
            // return false;
            // }

            if (!validateDuration(everyDayConf.getStartTime(),
                    everyDayConf.getDuration())) {
                setWarnMessage(i18n.iValue("web.client.trackingconf.service.message.ValidateDurationError"));
                return false;
            }
        }

        return true;
    }

    private Boolean isMultipleOf(Integer value, Integer multiple) {
        if (value % multiple == 0)
            return true;
        else
            return false;
    }

    private Boolean validateDuration(Date from, Date to) {
        GregorianCalendar gcFrom = new GregorianCalendar();
        GregorianCalendar gcTo = new GregorianCalendar();

        gcFrom.setTime(from);
        gcTo.setTime(to);

        gcFrom.set(Calendar.DAY_OF_MONTH, 1);
        gcFrom.set(Calendar.MONTH, 0);
        gcFrom.set(Calendar.YEAR, 1970);

        gcTo.set(Calendar.DAY_OF_MONTH, 1);
        gcTo.set(Calendar.MONTH, 0);
        gcTo.set(Calendar.YEAR, 1970);

        if (to.before(from) || to.equals(from))
            return false;
        return true;
    }

    public String saveEditingDetail() {
        if (datosValidados()) {
            saveDetail();
        }
        return null;
    }

    public String saveDetail() {
        try {
            isEditing = false;
            List<TrackingConfiguration> scList = new ArrayList<TrackingConfiguration>();

            if (daysOption == 1) { // EN CASO DE QUE SE HAYA SELECCIONADO 'TODOS
                                   // LOS DIAS'
                scList.add(getShiftConfigurationForEveryDayOption());
            } else if (daysOption == 2) {
                scList.addAll(getGroupedTrackingConfiguration());
            }

            // HACER QUE EL DATETO SEA HASTA LAS 23:59:59
            entityDetail.setDateTo(setHourToDate(entityDetail.getDateTo(),
                    "23:59:59"));
            entityDetail.setStatus(true);
            entityDetail.setClient(SecurityBean.getInstance().getLoggedInUserClient().getClient());
            entityDetail.setTrackingConfigurations(scList);

            String result = trackingconfFacade.saveEditing(
                    entityDetail,
                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                    getEntity() != null ? getEntity().getUserphoneCod() : null);

            if (result == null) {
                setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.SaveSuccess"));
                clearData();
                resetPaginationDetail();
            } else
                setWarnMessage(i18n.iValue("web.client.trackingconf.service.message.GuardConfigurationConflicts")
                    + result);
        } catch (GenericFacadeException e) {
            setErrorMessage(i18n.iValue("web.client.backingBean.message.Error"));
        } catch (Exception e) {
            setErrorMessage(i18n.iValue("web.client.backingBean.message.Error"));
        }
        return null;
    }

    private TrackingConfiguration getShiftConfigurationForEveryDayOption() {
        getEveryDayTrackingConfig();
        String startdateStr = DateUtil.getFormattedDate(
                entityDetail.getDateFrom(), "dd/MM/yyyy");
        String starttimeStr = DateUtil.getFormattedDate(
                everyDayConf.getStartTime(), "HH:mm");
        Date startDateTime = DateUtil.getDateFromString(startdateStr + " "
            + starttimeStr, "dd/MM/yyyy HH:mm");

        GregorianCalendar gcNOW = new GregorianCalendar();
        // VERIFICAR QUE LA FECHA DESDE NO SEA FECHA ANTERIOR A HOY
        if (startDateTime.before(new Date())) {
            String nowDateStr = DateUtil.getFormattedDate(new Date(),
                    "dd/MM/yyyy");
            startDateTime = DateUtil.getDateFromString(nowDateStr + " "
                + starttimeStr, "dd/MM/yyyy HH:mm");
        }

        // BUSCAR LA SIGTE FECHA QUE CORRESPONDE AL NEXTDAY EMPEZANDO DESDE EL
        // STARTDATETIME
        gcNOW.setTime(startDateTime);
        Integer actualDayOfWeek = gcNOW.get(Calendar.DAY_OF_WEEK);
        Integer nextDay = nextDay(actualDayOfWeek, "1234567");
        Date newStartTime = DateUtil.getNextDate(startDateTime, nextDay);
        gcNOW.setTime(newStartTime);

        everyDayTrackingConfig.setStartTime(gcNOW.getTime());
        everyDayTrackingConfig.setNextExecutionTime(gcNOW.getTime());

        everyDayTrackingConfig.setDuration(dateDiff(
                everyDayConf.getStartTime(), everyDayConf.getDuration()));

        everyDayTrackingConfig.setDays("1234567");
        everyDayTrackingConfig.setStatus(true);
        everyDayTrackingConfig.setIntervalTime(everyDayConf.getInterval());
        everyDayTrackingConfig.setToleranceTime(0L);
        everyDayTrackingConfig.setDescription(getDescription(
                "Lun,Mar,Mier,Jue,Vie",
                DateUtil.getFormattedDate(everyDayConf.getDuration(), "HH:mm"),
                everyDayConf.getInterval(), everyDayConf.getTolerance()));

        // everyDayTrackingConfig.setSupervisors(getConcatSupNumbersStr());
        everyDayTrackingConfig.setStartMinutes(convertHourToMinutes(everyDayConf.getStartTime()));

        GregorianCalendar gcEndTime = new GregorianCalendar();
        gcEndTime.setTime(everyDayTrackingConfig.getStartTime());
        gcEndTime.add(
                Calendar.MINUTE,
                (int) (everyDayTrackingConfig.getDuration() + everyDayTrackingConfig.getToleranceTime()));
        everyDayTrackingConfig.setEndTime(gcEndTime.getTime());

        gcNOW.add(Calendar.MINUTE,
                everyDayTrackingConfig.getToleranceTime().intValue());
        everyDayTrackingConfig.setNextExecutionToleranceTime(gcNOW.getTime());
        everyDayTrackingConfig.setRunning(false);

        return everyDayTrackingConfig;
    }

    private Date setHourToDate(Date date, String hour) {
        String startdateStr = DateUtil.getFormattedDate(date, "dd/MM/yyyy");
        Date dateTime = DateUtil.getDateFromString(startdateStr + " " + hour,
                "dd/MM/yyyy HH:mm:ss");
        return dateTime;
    }

    public static Integer nextDay(Integer actualDay, String days) {
        String[] daysArray = days.split("(?!^)");
        Integer theNextDay = 8;

        for (int i = daysArray.length - 1; i >= 0; i--) {
            Integer day = Integer.valueOf(daysArray[i]); // 7
            if (actualDay <= day) {
                theNextDay = day;
            }
        }
        if (theNextDay.equals(8)) {
            theNextDay = Integer.valueOf(daysArray[0]);
        }
        return theNextDay;
    }

    public String addToList() {
        if (newSupNumber != null)
            getSupNumberList().add(newSupNumber.toString());
        newSupNumber = null;
        return null;
    }

    public String removeFromList() {
        if (selectedSupNumber != null)
            getSupNumberList().remove(selectedSupNumber);
        newSupNumber = null;
        return null;
    }

    public void createConfigurationData(TrackingConfiguration tc) {
        List<String> days = getDaysOfWeekListFromColumn(tc.getDays());
        for (String day : days) {
            for (ConfigurationData cd : getConfList()) {
                if (day.equals(cd.getDayOfWeek().toString())) {
                    cd.setChecked(true);
                    // cd.setDuration(convertMinutesToHour(tc.getDuration()));

                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTime(tc.getStartTime());
                    gc.add(Calendar.MINUTE, tc.getDuration().intValue());
                    cd.setDuration(gc.getTime());

                    cd.setStartTime(tc.getStartTime());
                    cd.setInterval(tc.getIntervalTime());
                    cd.setTolerance(tc.getToleranceTime());
                    break;
                }
            }
        }

    }

    public List<String> getDaysOfWeekListFromColumn(String days) {
        List<String> daysOfWeek = new ArrayList<String>();
        for (int i = 0; i < days.length(); i++) {
            daysOfWeek.add(String.valueOf(days.charAt(i)));
        }
        return daysOfWeek;
    }

    private String getDescription(String days, String duration, Long interval, Long tolerance) {
        return days + " Dur: " + duration + " hs." + " Interv: " + interval
            + " min." + " Toler: " + tolerance + " min.";
    }

    private ConfigurationData getFirstConfigurationDataChecked() {
        for (ConfigurationData cd : confList) {
            if (cd.getChecked())
                return cd;
        }
        return null;
    }

    private List<TrackingConfiguration> getGroupedTrackingConfiguration() {
        List<ConfigurationData> cdFinalList = new ArrayList<ConfigurationData>();
        List<TrackingConfiguration> scFinalList = new ArrayList<TrackingConfiguration>();

        ConfigurationData firstCD = getFirstConfigurationDataChecked();
        firstCD.setDays(firstCD.getDayOfWeek().toString());
        // confList.get(0).setDays(confList.get(0).getDayOfWeek().toString());
        // cdFinalList.add(confList.get(0));
        cdFinalList.add(firstCD);
        for (ConfigurationData cd : confList) {
            if (cd.getChecked()) {
                for (ConfigurationData cfl : cdFinalList) {
                    if (cd.equals(cfl)) {
                        // String days = cfl.getDayOfWeekString();
                        String days = cfl.getDays();
                        if (days == null)
                            days = cd.getDayOfWeek().toString();
                        else if (days != null
                            && !days.equals(cd.getDayOfWeek().toString())) {
                            days += cd.getDayOfWeek().toString();
                            cfl.setDays(days);
                        }
                        cd.setAdded(true);
                        break;
                    }
                }
                if (!cd.getAdded()) { // si es una configuracion nueva, que no
                                      // esta en la lista final
                    // cd.setDayOfWeekString(cd.getDayOfWeek().toString());
                    cd.setDays(cd.getDayOfWeek().toString());
                    cdFinalList.add(cd);
                }

            }
        }

        GregorianCalendar gcNOW = new GregorianCalendar();

        for (ConfigurationData data : cdFinalList) {
            TrackingConfiguration sc = new TrackingConfiguration();
            // sc.setDays(data.getDayOfWeekString());
            sc.setDays(data.getDays());

            sc.setDuration(dateDiff(data.getStartTime(), data.getDuration()));

            sc.setIntervalTime(data.getInterval());
            sc.setToleranceTime(0L);

            String startdateStr = DateUtil.getFormattedDate(
                    entityDetail.getDateFrom(), "dd/MM/yyyy");
            String starttimeStr = DateUtil.getFormattedDate(
                    data.getStartTime(), "HH:mm");
            Date startDateTime = DateUtil.getDateFromString(startdateStr + " "
                + starttimeStr, "dd/MM/yyyy HH:mm");

            // VERIFICAR QUE LA FECHA DESDE NO SEA FECHA ANTERIOR A HOY
            if (startDateTime.before(new Date())) {
                String nowDateStr = DateUtil.getFormattedDate(new Date(),
                        "dd/MM/yyyy");
                startDateTime = DateUtil.getDateFromString(nowDateStr + " "
                    + starttimeStr, "dd/MM/yyyy HH:mm");
            }

            gcNOW.setTime(startDateTime);
            Integer actualDayOfWeek = gcNOW.get(Calendar.DAY_OF_WEEK);
            Integer nextDay = nextDay(actualDayOfWeek, sc.getDays());
            Date newStartTime = DateUtil.getNextDate(startDateTime, nextDay);
            gcNOW.setTime(newStartTime);

            sc.setStartTime(gcNOW.getTime());
            sc.setNextExecutionTime(gcNOW.getTime());

            GregorianCalendar gcEndTime = new GregorianCalendar();
            gcEndTime.setTime(gcNOW.getTime());
            gcEndTime.add(Calendar.MINUTE,
                    (int) (sc.getDuration() + sc.getToleranceTime()));
            sc.setEndTime(gcEndTime.getTime());

            sc.setStatus(true);
            sc.setDescription(getDescription(data.getDays(),
                    DateUtil.getFormattedDate(data.getDuration(), "HH:mm"),
                    data.getInterval(), data.getTolerance()));
            // sc.setSupervisors(getConcatSupNumbersStr());
            sc.setStartMinutes(convertHourToMinutes(data.getStartTime()));

            gcNOW.add(Calendar.MINUTE, sc.getToleranceTime().intValue());
            sc.setNextExecutionToleranceTime(gcNOW.getTime());
            sc.setRunning(false);
            scFinalList.add(sc);
        }

        return scFinalList;
    }

    private Long convertHourToMinutes(Date duration) {
        String hour = DateUtil.getFormattedDate(duration, "HH:mm");
        String[] hhmm = hour.split(":");
        Long minutes = Long.valueOf(hhmm[0]) * 60 + Long.valueOf(hhmm[1]);

        return minutes;
    }

    private Long dateDiff(Date from, Date to) {
        GregorianCalendar gcFrom = new GregorianCalendar();
        gcFrom.setTime(from);

        GregorianCalendar gcTo = new GregorianCalendar();
        gcTo.setTime(to);

        Integer fromMin = gcFrom.get(Calendar.HOUR_OF_DAY) * 60
            + gcFrom.get(Calendar.MINUTE);
        Integer tomMin = gcTo.get(Calendar.HOUR_OF_DAY) * 60
            + gcTo.get(Calendar.MINUTE);

        return new Long(tomMin - fromMin);
    }

    private Date convertMinutesToHour(Long min) {
        Long hoursNum = ((min * 60) / 3600) % 24;
        Long minutesNum = (((min * 60) % 3600) / 60);
        Date hour = DateUtil.getDateFromString(hoursNum + ":" + minutesNum,
                "HH:mm");
        return hour;
    }

    public String deassignPeriodsMassively() {
        setEntity(null);
        for (Userphone up : getDataModel()) {
            if (getSelectedItems().get(up.getUserphoneCod())) {
                if (getEntity() == null) {
                    setEntity(getFacade().find(up.getUserphoneCod()));
                }
            }
        }
        if (getEntity() == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneEdit"));
            return null;
        }

        dataModelDetail = null;
        paginationHelperDetail = null;
        deassignConfigurationsMassively = true;
        return null;
    }

    public String saveAssignPeriods() {
        // TRAER LOS GUARDIAS SELECCIONADOS
        List<TrackingPeriod> selectedTrackingPeriodList = getSelectedItemDetailList();

        if (selectedTrackingPeriodList == null
            || selectedTrackingPeriodList.size() == 0) {
            setWarnMessage(i18n.iValue("web.client.trackingconf.service.message.NotRegistrySelectedError"));
            return null;
        }

        if (selectedTrackingPeriodList.size() > 1) {
            setWarnMessage(i18n.iValue("web.client.trackingconf.service.message.MustSelectJustOneRegistry"));
            entityDetail = null;
            setSelectedItemsDetail(null);
            return null;
        }

        List<Userphone> selectedUserphoneList = getSelectedItemList();

        // VERIFICAR QUE NO HAYA CONFLICTOS CON LOS GUARDIAS
        // String conflictMsg = "";
        // Boolean existConflict = false;
        // for (Userphone md : selectedUserphoneList) {
        // List<TrackingPeriod> userphoneSpList =
        // trackingperiodFacade.getTrackingPeriodListByUserphone(md.getUserphoneCod().toString());
        // for (TrackingPeriod tp : selectedTrackingPeriodList) {
        // if (!userphoneSpList.contains(tp)){
        // if (trackingconfFacade.conflictInPeriod(md.getUserphoneCod(),
        // tp.getTrackingPeriodCod())){
        // conflictMsg +=
        // MessageFormat.format(i18n.iValue("web.client.guardroundconf.service.message.guardConflictsMessage"),
        // md.getUserphoneCod().toString(), tp.getDescription());
        // //conflictMsg += " Existe conflicto con el guardia " +
        // md.getMetaDataPK().getCodeChr() + " en el periodo '" +
        // sp.getDescription() + "'. \n";
        // existConflict = true;
        // }
        // }
        //
        // }
        // }
        // if (existConflict){
        // setWarnMessage(conflictMsg);
        // cancelEditing();
        // entityDetail = null;
        // return null;
        // }

        // REMUEVE LOS TRACKINGPERIODS QUE YA NO ESTAN SELECCIONADOS
        for (TrackingPeriod tp : getNotSelectedItemDetailList()) {
            List<Userphone> uList = trackingperiodFacade.getUserphoneListByTrackingPeriod(tp.getTrackingPeriodCod());
            for (Userphone md : selectedUserphoneList) {
                if (uList.contains(md))
                    uList.remove(md);
            }
            tp.setUserphones(uList);
            trackingperiodFacade.edit(tp);
        }

        // AGREGA TODOS LOS TRACKINGPERIODS A LOS GUARDIAS
        for (TrackingPeriod tp : selectedTrackingPeriodList) {
            List<Userphone> uList = null;
            uList = trackingperiodFacade.getUserphoneListByTrackingPeriod(tp.getTrackingPeriodCod());
            for (Userphone u : selectedUserphoneList) {

                // se borra si tiene algun tracking asignado
                List<TrackingPeriod> assignedPeriod = trackingperiodFacade.getTrackingPeriodListByUserphone(u.getUserphoneCod());
                for (TrackingPeriod trackingPeriod : assignedPeriod) {
                    List<Userphone> userphones = trackingperiodFacade.getUserphoneListByTrackingPeriod(trackingPeriod.getTrackingPeriodCod());
                    if (userphones.contains(u))
                        userphones.remove(u);
                    trackingPeriod.setUserphones(userphones);
                    trackingperiodFacade.edit(trackingPeriod);
                }

                if (uList == null)
                    uList = new ArrayList<Userphone>();
                if (!uList.contains(u))
                    uList.add(u);
            }
            tp.setUserphones(uList);
            trackingperiodFacade.edit(tp);
        }

        reset("web.client.backingBean.abstractCrudBean.message.SaveSuccess");
        setEntity(null);
        entityDetail = null;
        resetVariables();
        return null;
    }

    public String assignPeriodsMassively() {
        setEntity(null);
        for (Userphone u : getDataModel()) {
            if (getSelectedItems().get(u.getUserphoneCod())) {
                if (getEntity() == null) {
                    setEntity(getFacade().find(u.getUserphoneCod()));
                }
            }
        }
        if (getEntity() == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneEdit"));
            return null;
        }

        dataModelDetail = null;
        paginationHelperDetail = null;
        assignConfigurationsMassively = true;
        return null;
    }

    public String saveDeassignPeriodsMassively() {
        List<TrackingPeriod> selectedDetailList = getSelectedItemDetailList();
        if (selectedDetailList == null || selectedDetailList.size() == 0) {
            setWarnMessage(i18n.iValue("web.client.trackingconf.service.message.NotRegistrySelectedError"));
            return null;
        }

        // REMUEVE LOS SHIFTPERIODS QUE YA NO ESTAN SELECCIONADOS AL GUARDIA
        for (TrackingPeriod sp : selectedDetailList) {
            List<Userphone> uList = trackingperiodFacade.getUserphoneListByTrackingPeriod(sp.getTrackingPeriodCod());
            List<Userphone> selectedUserphoneList = getSelectedItemList();
            for (Userphone userphone : selectedUserphoneList) {
                if (uList.contains(userphone))
                    uList.remove(userphone);
            }
            sp.setUserphones(uList);
            trackingperiodFacade.edit(sp);

        }

        setEntity(null);
        entityDetail = null;
        reset("web.client.backingBean.abstractCrudBean.message.SaveSuccess");
        resetVariables();

        // resetPaginationDetail();
        return null;
    }

    // /--------------------------------------------------------------------------------//
    // --------------------------------------------------------------------------------------
    // LIST AND TABLE METHODS
    // --------------------------------------------------------------------------------------

    public PaginationHelper getPaginationHelperDetail() {
        if (paginationHelperDetail == null) {
            int pageSize = getRowQuantSelected().length() > 0 ? Integer.valueOf(
                    getRowQuantSelected()).intValue() : 0;

            paginationHelperDetail = new PaginationHelper(pageSize) {

                String innerWhere = null;
                Integer count = null;

                @Override
                public int getItemsCount() {

                    String where = "";
                    where = where.concat(" WHERE 1 = 1 ");
                    where = where.concat(" AND o.client = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
                    where = where.concat(" AND o.status = true ");
                    if (innerWhere == null) {
                        innerWhere = where;
                    } else {
                        if (innerWhere.compareTo(where) == 0) {
                            return count;
                        }
                    }

                    // where =
                    // where.concat(" AND mc.COD_CLIENT = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
                    // where = where.concat(" AND sp.status = 1 ");
                    //
                    // if (!assignConfigurations &&
                    // !assignConfigurationsMassively) { //se trae toda la lista
                    // de conf del cliente
                    // where =
                    // where.concat(" AND md.CODE_CHR = '".concat(getEntity().getMetaDataPK().getCodeChr())).concat("'");
                    // }

                    count = trackingperiodFacade.count(where);
                    return count;
                }

                @Override
                public DataModel createPageDataModel() {
                    String where = "";

                    // if (!assignConfigurations &&
                    // !assignConfigurationsMassively){ //se trae toda la lista
                    // de conf del cliente
                    // where =
                    // where.concat(" ,IN (o.userphones) m WHERE m.userphoneCod = '".concat(getEntity().getUserphoneCod().toString())).concat("'");
                    // } else {
                    where = where.concat(" WHERE 1 = 1 ");
                    // }
                    where = where.concat(" AND o.client = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
                    where = where.concat(" AND o.status = true ");

                    String orderby = "o.dateFrom ";

                    return new ListDataModelViewCsTigo(trackingperiodFacade.findRange(
                            new int[] { getPageFirstItem(), getPageFirstItem()
                                + getPageSize() }, where, orderby));
                }
            };
            if (lastActualPage >= 0) {
                paginationHelperDetail.setActualPage(lastActualPage);
                lastActualPage = -1;
            }
        }

        return paginationHelperDetail;
    }

    @Override
    protected void setPaginationHelper(PaginationHelper paginationHelperDetail) {
        this.paginationHelperDetail = paginationHelperDetail;
    }

    public DataModel<TrackingPeriod> getDataModelDetail() {
        if (dataModelDetail == null) {
            dataModelDetail = getPaginationHelperDetail().createPageDataModel();
            // if (assignConfigurations){
            // //SELECCIONAR LOS SHIFTPERIODS ASIGNADOS AL GUARDIA
            // List<TrackingPeriod> selectedShiftPeriodList =
            // trackingperiodFacade.getTrackingPeriodListByUserphone(getEntity().getUserphoneCod().toString());
            // for (TrackingPeriod tp : dataModelDetail) {
            // if (contiene(selectedShiftPeriodList, tp))
            // getSelectedItemsDetail().put(tp.getTrackingPeriodCod(), true);
            // }
            // }
        }

        return dataModelDetail;
    }

    public void setDataModelDetail(DataModel<TrackingPeriod> dataModelDetail) {
        this.dataModelDetail = dataModelDetail;
    }

    public String nextPageDetail() {
        getPaginationHelperDetail().nextPage();
        dataModelDetail = null; // For data model recreation
        selectedItemsDetail = null; // For clearing selection
        return null;
    }

    public void resetPaginationDetail() {
        // lastActualPage = 0;
        // filterSelectedField = "-1";
        // filterCriteria = "";
        entityDetail = null;
        paginationHelperDetail = null; // For pagination recreation
        dataModelDetail = null; // For data model recreation
        selectedItemsDetail = null; // For clearing selection
    }

    public String previousPageDetail() {
        getPaginationHelperDetail().previousPage();
        dataModelDetail = null; // For data model recreation
        selectedItemsDetail = null; // For clearing selection
        return null;
    }

    public Map<Object, Boolean> getSelectedItemsDetail() {
        if (selectedItemsDetail == null) {
            selectedItemsDetail = new HashMap<Object, Boolean>();
        }

        return selectedItemsDetail;
    }

    public void setSelectedItemsDetail(Map<Object, Boolean> selectedItems) {
        this.selectedItemsDetail = selectedItems;
    }

    public String applySortDetail() {
        dataModelDetail = null; // For data model recreation
        selectedItemsDetail = null; // For clearing selection
        return null;
    }

    public TrackingPeriod getSelectedItemDetail() {
        entityDetail = null;
        for (TrackingPeriod sp : dataModelDetail) {
            if (getSelectedItemsDetail().get(sp.getTrackingPeriodCod())) {
                if (entityDetail == null) {
                    entityDetail = trackingperiodFacade.find(sp.getTrackingPeriodCod());
                } else {
                    entityDetail = null;
                    setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectJustOneEdit"));
                    return null;
                }
            }
        }

        if (entityDetail == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneEdit"));
        }

        return entityDetail;
    }

    public List<TrackingPeriod> getSelectedItemDetailList() {
        entityDetail = null;
        List<TrackingPeriod> tpList = new ArrayList<TrackingPeriod>();
        for (TrackingPeriod sp : dataModelDetail) {
            if (getSelectedItemsDetail().get(sp.getTrackingPeriodCod())) {
                entityDetail = trackingperiodFacade.find(sp.getTrackingPeriodCod());
                tpList.add(sp);
            }
        }
        return tpList;
    }

    public List<TrackingPeriod> getNotSelectedItemDetailList() {
        entityDetail = null;
        List<TrackingPeriod> spList = new ArrayList<TrackingPeriod>();
        for (TrackingPeriod sp : dataModelDetail) {
            if (!getSelectedItemsDetail().get(sp.getTrackingPeriodCod())) {
                entityDetail = trackingperiodFacade.find(sp.getTrackingPeriodCod());
                spList.add(sp);
            }
        }
        return spList;
    }

    public TrackingPeriod getEntityDetail() {
        return entityDetail;
    }

    public void setEntityDetail(TrackingPeriod entityDetailPeriod) {
        this.entityDetail = entityDetailPeriod;
    }

    public Integer getDaysOption() {
        return daysOption;
    }

    public void setDaysOption(Integer daysOption) {
        this.daysOption = daysOption;
    }

    public ConfigurationData getEveryDayConf() {
        if (everyDayConf == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            everyDayConf = new ConfigurationData(null, 0, cal.getTime(), cal.getTime(), new Long(getStepFactor()), 0L, false, false);
        }
        return everyDayConf;
    }

    public void setEveryDayConf(ConfigurationData everyDayConf) {
        this.everyDayConf = everyDayConf;
    }

    public List<ConfigurationData> getConfList() {
        if (confList == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            confList = new ArrayList<ConfigurationData>();
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Monday"), 2, cal.getTime(), cal.getTime(), new Long(getStepFactor()), 0L, false, false));
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Tuesday"), 3, cal.getTime(), cal.getTime(), new Long(getStepFactor()), 0L, false, false));
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Wednesday"), 4, cal.getTime(), cal.getTime(), new Long(getStepFactor()), 0L, false, false));
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Thursday"), 5, cal.getTime(), cal.getTime(), new Long(getStepFactor()), 0L, false, false));
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Friday"), 6, cal.getTime(), cal.getTime(), new Long(getStepFactor()), 0L, false, false));
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Saturday"), 7, cal.getTime(), cal.getTime(), new Long(getStepFactor()), 0L, false, false));
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Sunday"), 1, cal.getTime(), cal.getTime(), new Long(getStepFactor()), 0L, false, false));
        }
        return confList;
    }

    public void setConfList(List<ConfigurationData> confList) {
        this.confList = confList;
    }

    public TrackingConfiguration getEveryDayTrackingConfig() {
        if (everyDayTrackingConfig == null)
            everyDayTrackingConfig = new TrackingConfiguration();
        return everyDayTrackingConfig;
    }

    public void setEveryDayShiftConfig(TrackingConfiguration everyDayShiftConfig) {
        this.everyDayTrackingConfig = everyDayShiftConfig;
    }

    public List<TrackingConfiguration> getTrackingConfigurationList() {
        return trackingConfigurationList;
    }

    public void setTrackingConfigurationList(List<TrackingConfiguration> trackingConfigurationList) {
        this.trackingConfigurationList = trackingConfigurationList;
    }

    public List<String> getSupNumberList() {
        if (supNumberList == null) {
            supNumberList = new ArrayList<String>();
        }
        return supNumberList;
    }

    public void setSupNumberList(List<String> supNumberList) {
        this.supNumberList = supNumberList;
    }

    public String getSelectedSupNumber() {
        return selectedSupNumber;
    }

    public void setSelectedSupNumber(String selectedSupNumber) {
        this.selectedSupNumber = selectedSupNumber;
    }

    public BigInteger getNewSupNumber() {
        return newSupNumber;
    }

    public void setNewSupNumber(BigInteger newSupNumber) {
        this.newSupNumber = newSupNumber;
    }

    public String getConcatSupNumbersStr() {
        // String concatStr = "";
        if (supNumberList != null) {
            concatSupNumbersStr = "";
            for (String num : supNumberList) {
                concatSupNumbersStr += num + ",";
            }
            if (concatSupNumbersStr != null && concatSupNumbersStr.length() > 0)
                concatSupNumbersStr = concatSupNumbersStr.substring(0,
                        concatSupNumbersStr.length() - 1);
        }
        return concatSupNumbersStr;
    }

    public void setConcatSupNumbersStr(String concatSupNumbersStr) {
        this.concatSupNumbersStr = concatSupNumbersStr;
    }

    public Boolean getDeassignConfigurationsMassively() {
        return deassignConfigurationsMassively;
    }

    public void setDeassignConfigurationsMassively(Boolean deassignConfigurationsMassively) {
        this.deassignConfigurationsMassively = deassignConfigurationsMassively;
    }

    public Boolean getAssignConfigurationsMassively() {
        return assignConfigurationsMassively;
    }

    public void setAssignConfigurationsMassively(Boolean assignConfigurationsMassively) {
        this.assignConfigurationsMassively = assignConfigurationsMassively;
    }

    public Boolean getIsEditing() {
        return isEditing;
    }

    public void setIsEditing(Boolean isEditing) {
        this.isEditing = isEditing;
    }

    public Date getOldDateFrom() {
        return oldDateFrom;
    }

    public void setOldDateFrom(Date oldDateFrom) {
        this.oldDateFrom = oldDateFrom;
    }

    public Date getOldDateTo() {
        return oldDateTo;
    }

    public void setOldDateTo(Date oldDateTo) {
        this.oldDateTo = oldDateTo;
    }

    public List<Userphone> getSelectedItemList() {
        List<Userphone> uList = new ArrayList<Userphone>();
        for (Userphone userphone : getDataModel()) {
            if (getSelectedItems().get(userphone.getUserphoneCod())) {
                // Userphone entity =
                // getFacade().find(userphone.getUserphoneCod());
                uList.add(userphone);
            }
        }
        return uList;
    }

    public static void main(String args[]) {

        GregorianCalendar calendar = new GregorianCalendar();
        final int currentDayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) - 7) % 7;
        calendar.add(Calendar.DAY_OF_YEAR, currentDayOfWeek);

        System.out.println(calendar.getTime());
    }

    private void clearData() {
        oldDateFrom = null;
        oldDateTo = null;
        setConfList(null);
        getConfList();
        setEveryDayConf(null);
        getEveryDayConf();
    }

    public MetaData getUserphoneMeta() {
        return userphoneMeta;
    }

    public void setUserphoneMeta(MetaData userphoneMeta) {
        this.userphoneMeta = userphoneMeta;
    }

    public Userphone getSelectedUserphone() {
        return selectedUserphone;
    }

    public void setSelectedUserphone(Userphone selectedUserphone) {
        this.selectedUserphone = selectedUserphone;
    }

    public List<Userphone> getUserphoneList() {
        if (userphoneList == null) {
            userphoneList = userphoneFacade.findByUserwebAndClassificationAndService(
                    SecurityBean.getInstance().getLoggedInUserClient(), 4L);
        }
        return userphoneList;
    }

    public void setUserphoneList(List<Userphone> userphoneList) {
        this.userphoneList = userphoneList;
    }

    public Boolean getShowConfigTracking() {
        return showConfigTracking;
    }

    public void setShowConfigTracking(Boolean showConfigTracking) {
        this.showConfigTracking = showConfigTracking;
    }

    public Client getClient() {
        if (client == null) {
            client = SecurityBean.getInstance().getLoggedInUserClient().getClient();
        }
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
        if (this.client != null) {
            setAditionalFilter("o.client.clientCod = ".concat(this.client.getClientCod().toString()));
        }
    }

    public Integer getStepFactor() {
        if (stepFactor == null) {
            try {
                String factor = gpFacade.findByCode("tracking.interval");
                if (factor != null)
                    stepFactor = Integer.parseInt(factor);
                else
                    stepFactor = 10;
            } catch (GenericFacadeException e) {
                stepFactor = 10;
            }
        }
        return stepFactor;
    }

    public void setStepFactor(Integer stepFactor) {
        this.stepFactor = stepFactor;
    }

    @Override
    public String getReportWhereCriteria() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PaginationHelper getPaginationHelper() {
        if (paginationHelper == null) {
            int pageSize = getRowQuantSelected().length() > 0 ? Integer.valueOf(
                    getRowQuantSelected()).intValue() : 0;

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
                            where = " ,IN (o.clientServiceFunctionalityList) f where o.".concat(getFilterSelectedField()).concat(
                                    " = ").concat(getFilterCriteria());
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
                    where = where.concat(" o.enabledChr = true AND o.client.clientCod = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()).concat(" AND f.client = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString())));

                    // se agrega la clasificacion
                    // List<Classification> classifications =
                    // classificationFacade.findByUserweb(SecurityBean.getInstance().getLoggedInUserClient());

                    List<Classification> classifications = classificationFacade.findByUserwebWithChilds(SecurityBean.getInstance().getLoggedInUserClient());
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
                    count = getFacade().count(where, classifications);
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
                            where = " ,IN (o.clientServiceFunctionalityList) f LEFT JOIN FETCH o.trackingPeriods where o.".concat(
                                    getFilterSelectedField()).concat(" = ").concat(
                                    getFilterCriteria());
                        } else if (fieldClass.equals(String.class)) {
                            where = " ,IN (o.clientServiceFunctionalityList) f LEFT JOIN FETCH o.trackingPeriods where lower(o.".concat(
                                    getFilterSelectedField()).concat(
                                    ") LIKE '%").concat(
                                    getFilterCriteria().toLowerCase()).concat(
                                    "%'");
                        }
                    }
                    if (getAditionalFilter() != null
                        && getAditionalFilter().trim().length() > 0) {
                        if (where == null) {
                            where = " ,IN (o.clientServiceFunctionalityList) f LEFT JOIN FETCH o.trackingPeriods where ";
                        } else {
                            where = where.concat(" AND");
                        }
                        where = where.concat(" (").concat(
                                getAditionalFilter().trim()).concat(") ");
                    }

                    if (where == null) {
                        where = " ,IN (o.clientServiceFunctionalityList) f LEFT JOIN FETCH o.trackingPeriods where ";
                    } else {
                        where = where.concat(" AND");
                    }
                    where = where.concat(" o.enabledChr = true AND o.client.clientCod = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()).concat(" AND f.client = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString())));

                    List<Classification> classifications = classificationFacade.findByUserwebWithChilds(SecurityBean.getInstance().getLoggedInUserClient());
                    where += " AND f.serviceFunctionality.service.serviceCod = 4 AND f.serviceFunctionality.functionality.functionalityCod = 0 AND EXISTS ( SELECT u FROM Userphone u , IN (u.classificationList) cl WHERE u.userphoneCod = o.userphoneCod AND u.client = o.client AND cl.codClient = o.client AND cl in (:classifications)) ";

                    String orderby = "o.".concat(getSortHelper().getField()).concat(
                            getSortHelper().isAscending() ? " ASC" : " DESC");

                    return new ListDataModelViewCsTigo(getFacade().findRange(
                            new int[] { getPageFirstItem(), getPageFirstItem()
                                + getPageSize() }, where, orderby,
                            classifications));
                }
            };
            if (lastActualPage >= 0) {
                paginationHelper.setActualPage(lastActualPage);
                lastActualPage = -1;
            }
        }

        return paginationHelper;
    }

    @Override
    public void applyQuantity(ValueChangeEvent evnt) {
        paginationHelper = null; // For pagination recreation
        setDataModel(null); // For data model recreation
        setSelectedItems(null); // For clearing selection
    }

    @Override
    public String cleanFilter() {
        setFilterSelectedField("-1");
        setFilterCriteria("");
        paginationHelper = null; // For pagination recreation
        setDataModel(null); // For data model recreation
        setSelectedItems(null); // For clearing selection
        setAditionalFilter("o.enabledChr = true");
        return null;
    }
}
