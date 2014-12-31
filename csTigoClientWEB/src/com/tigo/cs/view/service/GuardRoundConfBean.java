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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.util.DateUtil;
import com.tigo.cs.commons.util.ListDataModelViewCsTigo;
import com.tigo.cs.commons.util.PaginationHelper;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.MetaDataPK;
import com.tigo.cs.domain.MetaMember;
import com.tigo.cs.domain.MetaMemberPK;
import com.tigo.cs.domain.ShiftConfiguration;
import com.tigo.cs.domain.ShiftPeriod;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.facade.GlobalParameterFacade;
import com.tigo.cs.facade.MetaDataFacade;
import com.tigo.cs.facade.MetaMemberFacade;
import com.tigo.cs.facade.ShiftConfigurationFacade;
import com.tigo.cs.facade.ShiftPeriodFacade;
import com.tigo.cs.facade.UserphoneFacade;
import com.tigo.cs.security.Notifier;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.metadata.AbstractCrudBean;

/**
 * 
 * @author Miguel Zorrilla
 * @version $Id: CrudMetaDataGuardBean.java 91 2011-11-28 20:16:12Z
 *          javier.kovacs $
 */
@ManagedBean(name = "guardroundconfBean")
@ViewScoped
public class GuardRoundConfBean extends AbstractCrudBean implements Serializable {
    @EJB
    private ShiftConfigurationFacade shiftconfFacade;
    @EJB
    private ShiftPeriodFacade shiftperiodFacade;
    @EJB
    private UserphoneFacade userphoneFacade;
    @EJB
    private MetaMemberFacade metaMemberFacade;
    @EJB
    protected MetaDataFacade metaDataFacade;
    @EJB
    private GlobalParameterFacade globalParameterFacade;
    @EJB
    private Notifier notifier;
    private static final long serialVersionUID = 2765575061701097148L;
    private static final Long COD_META = 4L;
    private static final Long COD_MEMBER = 1L;
    private ShiftPeriod entityDetail;
    private MetaData userphoneMeta;
    private Userphone selectedUserphone;
    protected List<Userphone> userphoneList;

    private Integer daysOption;
    private ConfigurationData everyDayConf;
    private ShiftConfiguration everyDayShiftConfig;
    private List<ConfigurationData> confList;
    private List<ShiftConfiguration> shiftConfigurationList;
    private List<String> supNumberList;
    private String selectedSupNumber;
    private BigInteger newSupNumber;
    private String concatSupNumbersStr;
    private Boolean assignConfigurations = false;
    private Boolean assignConfigurationsMassively = false;
    // private Boolean newPeriod = false;
    private Boolean isEditing;
    private Date oldDateFrom;
    private Date oldDateTo;
    private String stepFactorInterval;
    private String stepFactorTolerance;

    public GuardRoundConfBean() {
        super(COD_META, COD_MEMBER);
       
    }
    
    @PostConstruct
    public void init() {
        try {
            stepFactorInterval = globalParameterFacade.findByCode("guard.round.conf.factor.interval");
            stepFactorTolerance = globalParameterFacade.findByCode("guard.round.conf.factor.tolerance");
        } catch (GenericFacadeException e) {
            notifier.error(GuardRoundConfBean.class, null,e.getMessage(), e);
        }
       
    }

    @Override
    public String getMetaLabel() {
        if (metaLabel == null) {
            setMetaLabel("web.client.backingBean.guarCrudMetaData.Guard");
        }
        return super.getMetaLabel();
    }

    private DataModel<ShiftPeriod> dataModelDetail;
    private Map<Object, Boolean> selectedItemsDetail;
    private PaginationHelper paginationHelperDetail;
    private int lastActualPage = -1;
    private String tableDetailsTitle;
    private String assignPhoneNumberTitle;

    public String getAssignPhoneNumberTitle() {
        assignPhoneNumberTitle = MessageFormat.format(
                i18n.iValue("web.client.guardroundconf.screen.title.SetPhoneNumber"),
                getEntity().getMetaDataPK().getCodeChr() + ", "
                    + getEntity().getValueChr());
        return assignPhoneNumberTitle;
    }

    public void setAssignPhoneNumberTitle(String assignPhoneNumberTitle) {
        this.assignPhoneNumberTitle = assignPhoneNumberTitle;
    }

    public String getTableDetailsTitle() {
        if (assignConfigurationsMassively)
            tableDetailsTitle = i18n.iValue("web.client.guardroundconf.screen.title.TotaLListOf");
        else if (assignConfigurations
            || (!assignConfigurations && !assignConfigurationsMassively))
            tableDetailsTitle = MessageFormat.format(
                    i18n.iValue("web.client.guardroundconf.screen.title.ListOf"),
                    getEntity().getMetaDataPK().getCodeChr() + ", "
                        + getEntity().getValueChr());
        return tableDetailsTitle;
    }

    public void setTableDetailsTitle(String tableDetailsTitle) {
        this.tableDetailsTitle = tableDetailsTitle;
    }

    @Override
    public String cancelEditing() {
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
        assignConfigurations = false;
        assignConfigurationsMassively = false;
        setSelectedItemsDetail(null);
    }

    public String newEntityDetail() {
        entityDetail = new ShiftPeriod();
        daysOption = -1;
        setSupNumberList(null);
        isEditing = false;
        return null;
    }

    public String editEntityDetail() {
        if (getSelectedItemDetail() == null)
            return null;

        entityDetail = getSelectedItemDetail();
        shiftConfigurationList = shiftconfFacade.getShiftConfListByShiftPeriod(entityDetail.getShiftPeriodCod());
        if (shiftConfigurationList.size() == 1
            && shiftConfigurationList.get(0).getDays().equals("1234567")) {
            everyDayConf = new ConfigurationData();
            everyDayShiftConfig = shiftConfigurationList.get(0);
            daysOption = 1; // se indica que se selecciona la opcion 'Todos los
                            // dias'
            everyDayConf.setDuration(convertMinutesToHour(everyDayShiftConfig.getDuration()));
            everyDayConf.setInterval(everyDayShiftConfig.getIntervalTime());
            everyDayConf.setStartTime(everyDayShiftConfig.getStartTime());
            everyDayConf.setTolerance(everyDayShiftConfig.getToleranceTime());
        } else {
            daysOption = 2; // se indica que se selecciona la opcion
                            // 'Especificar dias de la semana'
            setConfList(null);
            // getConfList();
            for (ShiftConfiguration sc : shiftConfigurationList) {
                createConfigurationData(sc);
            }
        }
        setSupervisorNumberList(shiftConfigurationList.get(0).getSupervisors());
        oldDateFrom = entityDetail.getDateFrom();
        oldDateTo = entityDetail.getDateTo();
        isEditing = true;
        return null;
    }

    private void setSupervisorNumberList(String supervisorNumbers) {
        if (supervisorNumbers != null) {
            String[] numbers = supervisorNumbers.split(",");
            setSupNumberList(null);
            for (String s : numbers) {
                getSupNumberList().add(s);
            }
        }
    }

    @Override
    public String editEntity() {
        String retVal = super.editEntity();

        try {
            MetaData userphoneMD = null;
            List<MetaData> selectedGuardList = getSelectedItemList();
            for (MetaData metaData : selectedGuardList) {
                userphoneMD = metaDataFacade.findByClientMetaMemberAndCode(
                        SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                        4L, 2L, metaData.getMetaDataPK().getCodeChr());
                if (userphoneMD == null) {
                    setWarnMessage(MessageFormat.format(
                            i18n.iValue("web.client.guardroundconf.service.message.GuardWithNoPhoneNumber"),
                            metaData.getMetaDataPK().getCodeChr()));
                    setEntity(null);
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dataModelDetail = null;
        paginationHelperDetail = null;

        return retVal;
    }

    public String cancelEditingDetail() {
        entityDetail = null;
        isEditing = false;
        clearData();
        resetVariables();
        return null;
    }

    public String deleteEditingDetail() {
        List<ShiftPeriod> selectedDetailList = getSelectedItemDetailList();
        if (selectedDetailList == null || selectedDetailList.size() == 0) {
            setWarnMessage(i18n.iValue("web.client.guardroundconf.service.message.NotRegistrySelectedError"));
            return null;
        }

        /*
         * BORRA LOGICAMENTE LOS SHIFTPERIODS SELECCIONADOS, NO BORRA
         * FISICAMENTE. Es decir, pone status 0 a los shiftConfigurations
         * asignados al shiftperiod, y al shiftperiod.
         */
        try {
            for (ShiftPeriod sp : getSelectedItemDetailList()) {
                shiftperiodFacade.deleteEntity(sp.getShiftPeriodCod());
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
            setWarnMessage(i18n.iValue("web.client.guardroundconf.service.message.ValidationError"));
            return false;
        }

        // SE VALIDA QUE LA FECHA DESDE SEA MENOR O IGUAL A LA FECHA HASTA
        if (entityDetail.getDateFrom().compareTo(entityDetail.getDateTo()) > 0) {
            setWarnMessage(i18n.iValue("web.client.guardroundconf.service.message.DateValidationMessage"));
            return false;
        }

        // SE VERIFICA QUE SE HAYA ELEGIDO UNA OPCION PARA LOS DIAS DE LA SEMANA
        if (daysOption == -1) {
            setWarnMessage(i18n.iValue("web.client.guardroundconf.service.message.MustSelectDayOption"));
            return false;
        }

        // VERIFICAR QUE LA TOLERANCIA NO SEA MAYOR QUE EL INTERVALO PARA CONFIG
        // POR DIA
        if (confList != null && daysOption == 2) {
            Boolean atLeastOneSelected = false;
            for (ConfigurationData cd : confList) {
                if (cd.getChecked()) {
                    if (cd.getTolerance() <= 0) {
                        setWarnMessage(i18n.iValue("web.client.guardroundconf.service.message.ValidateToleranceGreaterZero"));
                        return false;
                    }
                    if (cd.getInterval() <= 0) {
                        setWarnMessage(i18n.iValue("web.client.guardroundconf.service.message.ValidateIntervalGreaterZero"));
                        return false;
                    }
                    Calendar cal0000 = Calendar.getInstance();
                    cal0000.set(Calendar.HOUR_OF_DAY, 0);
                    cal0000.set(Calendar.MINUTE, 0);
                    String time0000 = DateUtil.getFormattedDate(
                            cal0000.getTime(), "HH:mm");
                    String time0000_ = DateUtil.getFormattedDate(
                            cd.getDuration(), "HH:mm");
                    if (time0000.equals(time0000_)) {
                        setWarnMessage(i18n.iValue("web.client.guardroundconf.service.message.ValidateDurationGreaterZero"));
                        return false;
                    }
                    if (cd.getTolerance() > cd.getInterval()) {
                        setWarnMessage(i18n.iValue("web.client.guardroundconf.service.message.ValidateTolerance"));
                        return false;
                    }
                    atLeastOneSelected = true;
                }
            }
            if (!atLeastOneSelected) {
                setWarnMessage(i18n.iValue("web.client.guardroundconf.service.message.AtLeastOneDaySelectedValidation"));
                return false;
            }
        }

        // SE VERIFICA QUE LA TOLERANCIA NO SEA MAYOR QUE EL INTERVALO PARA
        // TODOS LOS DIAS
        if (everyDayConf != null && daysOption == 1) {
            if (everyDayConf.getTolerance() <= 0) {
                setWarnMessage(i18n.iValue("web.client.guardroundconf.service.message.ValidateToleranceGreaterZero"));
                return false;
            }
            if (everyDayConf.getInterval() <= 0) {
                setWarnMessage(i18n.iValue("web.client.guardroundconf.service.message.ValidateIntervalGreaterZero"));
                return false;
            }
            Calendar cal0000 = Calendar.getInstance();
            cal0000.set(Calendar.HOUR_OF_DAY, 0);
            cal0000.set(Calendar.MINUTE, 0);
            String time0000 = DateUtil.getFormattedDate(cal0000.getTime(),
                    "HH:mm");
            String time0000_ = DateUtil.getFormattedDate(
                    everyDayConf.getDuration(), "HH:mm");
            if (time0000.equals(time0000_)) {
                setWarnMessage(i18n.iValue("web.client.guardroundconf.service.message.ValidateDurationGreaterZero"));
                return false;
            }
            if (everyDayConf.getTolerance() > everyDayConf.getInterval()) {
                setWarnMessage(i18n.iValue("web.client.guardroundconf.service.message.ValidateTolerance"));
                return false;
            }
        }

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
            List<ShiftConfiguration> scList = new ArrayList<ShiftConfiguration>();

            if (daysOption == 1) { // EN CASO DE QUE SE HAYA SELECCIONADO 'TODOS
                                   // LOS DIAS'
                scList.add(getShiftConfigurationForEveryDayOption());
            } else if (daysOption == 2) {
                scList.addAll(getGroupedShiftConfiguration());
            }

            // HACER QUE EL DATETO SEA HASTA LAS 23:59:59
            entityDetail.setDateTo(setHourToDate(entityDetail.getDateTo(),
                    "23:59:59"));
            entityDetail.setStatus(true);
            entityDetail.setClient(SecurityBean.getInstance().getLoggedInUserClient().getClient());
            entityDetail.setShiftConfigurations(scList);
            String result = shiftconfFacade.saveEditing(
                    entityDetail,
                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                    getEntity().getMetaDataPK().getCodeChr());
            if (result == null) {
                setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.SaveSuccess"));
                clearData();
                resetPaginationDetail();
            } else
                setWarnMessage(i18n.iValue("web.client.guardroundconf.service.message.GuardConfigurationConflicts")
                    + result);
        } catch (GenericFacadeException e) {
            setErrorMessage(i18n.iValue("web.client.backingBean.message.Error"));
        } catch (Exception e) {
            setErrorMessage(i18n.iValue("web.client.backingBean.message.Error"));
        }
        return null;
    }

    private ShiftConfiguration getShiftConfigurationForEveryDayOption() {
        getEveryDayShiftConfig();
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

        everyDayShiftConfig.setStartTime(gcNOW.getTime());
        everyDayShiftConfig.setNextExecutionTime(gcNOW.getTime());

        everyDayShiftConfig.setDuration(convertHourToMinutes(everyDayConf.getDuration()));
        everyDayShiftConfig.setDays("1234567");
        everyDayShiftConfig.setStatus(true);
        everyDayShiftConfig.setIntervalTime(everyDayConf.getInterval());
        everyDayShiftConfig.setToleranceTime(everyDayConf.getTolerance());
        everyDayShiftConfig.setDescription(getDescription(
                "Lun,Mar,Mier,Jue,Vie",
                DateUtil.getFormattedDate(everyDayConf.getDuration(), "HH:mm"),
                everyDayConf.getInterval(), everyDayConf.getTolerance()));

        everyDayShiftConfig.setSupervisors(getConcatSupNumbersStr());
        everyDayShiftConfig.setStartMinutes(convertHourToMinutes(everyDayConf.getStartTime()));

        GregorianCalendar gcEndTime = new GregorianCalendar();
        gcEndTime.setTime(everyDayShiftConfig.getStartTime());
        gcEndTime.add(
                Calendar.MINUTE,
                (int) (everyDayShiftConfig.getDuration() + everyDayShiftConfig.getToleranceTime()));
        everyDayShiftConfig.setEndTime(gcEndTime.getTime());

        gcNOW.add(Calendar.MINUTE,
                everyDayShiftConfig.getToleranceTime().intValue());
        everyDayShiftConfig.setNextExecutionToleranceTime(gcNOW.getTime());
        everyDayShiftConfig.setRunning(false);

        return everyDayShiftConfig;
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

    @Override
    public String addToList() {
        if (newSupNumber != null)
            getSupNumberList().add(newSupNumber.toString());
        newSupNumber = null;
        return null;
    }

    @Override
    public String removeFromList() {
        if (selectedSupNumber != null)
            getSupNumberList().remove(selectedSupNumber);
        newSupNumber = null;
        return null;
    }

    public void createConfigurationData(ShiftConfiguration sc) {
        List<String> days = getDaysOfWeekListFromColumn(sc.getDays());
        for (String day : days) {
            for (ConfigurationData cd : getConfList()) {
                if (day.equals(cd.getDayOfWeek().toString())) {
                    cd.setChecked(true);
                    cd.setDuration(convertMinutesToHour(sc.getDuration()));
                    cd.setStartTime(sc.getStartTime());
                    cd.setInterval(sc.getIntervalTime());
                    cd.setTolerance(sc.getToleranceTime());
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

    private List<ShiftConfiguration> getGroupedShiftConfiguration() {
        List<ConfigurationData> cdFinalList = new ArrayList<ConfigurationData>();
        List<ShiftConfiguration> scFinalList = new ArrayList<ShiftConfiguration>();

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
            ShiftConfiguration sc = new ShiftConfiguration();
            // sc.setDays(data.getDayOfWeekString());
            sc.setDays(data.getDays());
            sc.setDuration(convertHourToMinutes(data.getDuration()));
            sc.setIntervalTime(data.getInterval());
            sc.setToleranceTime(data.getTolerance());

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
            sc.setSupervisors(getConcatSupNumbersStr());
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

    private Date convertMinutesToHour(Long min) {
        Long hoursNum = ((min * 60) / 3600) % 24;
        Long minutesNum = (((min * 60) % 3600) / 60);
        Date hour = DateUtil.getDateFromString(hoursNum + ":" + minutesNum,
                "HH:mm");
        return hour;
    }

    public String assignPeriods() {
        setEntity(null);
        for (MetaData metaData : getDataModel()) {
            if (getSelectedItems().get(metaData.getMetaDataPK())) {
                if (getEntity() == null) {
                    setEntity(facade.find(metaData.getMetaDataPK()));
                } else {
                    setEntity(null);
                    setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectJustOneEdit"));
                    return null;
                }
            }
        }
        if (getEntity() == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneEdit"));
            return null;
        }

        // Se verifica que el guardia tenga asignado un nro telefonico
        try {
            MetaData userphoneMD = null;
            List<MetaData> selectedGuardList = getSelectedItemList();
            for (MetaData metaData : selectedGuardList) {
                userphoneMD = metaDataFacade.findByClientMetaMemberAndCode(
                        SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                        4L, 2L, metaData.getMetaDataPK().getCodeChr());
                if (userphoneMD == null) {
                    setWarnMessage(MessageFormat.format(
                            i18n.iValue("web.client.guardroundconf.service.message.GuardWithNoPhoneNumber"),
                            metaData.getMetaDataPK().getCodeChr()));
                    setEntity(null);
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dataModelDetail = null;
        paginationHelperDetail = null;
        assignConfigurations = true;
        return null;
    }

    public String saveAssignPeriods() {
        // TRAER LOS GUARDIAS SELECCIONADOS
        List<MetaData> selectedGuardList = getSelectedItemList();
        List<ShiftPeriod> selectedShiftPeriodList = getSelectedItemDetailList();

        // VERIFICAR QUE NO HAYA CONFLICTOS CON LOS GUARDIAS
        String conflictMsg = "";
        Boolean existConflict = false;
        for (MetaData md : selectedGuardList) {
            List<ShiftPeriod> guardSpList = shiftperiodFacade.getShiftPeriodListByGuard(md.getMetaDataPK().getCodeChr());
            for (ShiftPeriod sp : selectedShiftPeriodList) {
                if (!guardSpList.contains(sp)) {
                    if (shiftconfFacade.conflictInPeriod(
                            md.getMetaDataPK().getCodeChr(),
                            sp.getShiftPeriodCod())) {
                        conflictMsg += MessageFormat.format(
                                i18n.iValue("web.client.guardroundconf.service.message.guardConflictsMessage"),
                                md.getMetaDataPK().getCodeChr(),
                                sp.getDescription());
                        // conflictMsg += " Existe conflicto con el guardia " +
                        // md.getMetaDataPK().getCodeChr() + " en el periodo '"
                        // + sp.getDescription() + "'. \n";
                        existConflict = true;
                    }
                }

            }
        }
        if (existConflict) {
            setWarnMessage(conflictMsg);
            cancelEditing();
            entityDetail = null;
            return null;
        }

        // REMUEVE LOS SHIFTPERIODS QUE YA NO ESTAN SELECCIONADOS
        for (ShiftPeriod sp : getNotSelectedItemDetailList()) {
            List<MetaData> mdList = shiftperiodFacade.getMetaDatalistByShiftPeriod(sp.getShiftPeriodCod());
            for (MetaData md : selectedGuardList) {
                if (mdList.contains(md))
                    mdList.remove(md);
            }
            sp.setMetaDatas(mdList);
            shiftperiodFacade.edit(sp);
        }

        // AGREGA TODOS LOS SHIFTPERIODS A LOS GUARDIAS
        for (ShiftPeriod sp : selectedShiftPeriodList) {
            List<MetaData> mdList = null;
            mdList = shiftperiodFacade.getMetaDatalistByShiftPeriod(sp.getShiftPeriodCod());
            for (MetaData md : selectedGuardList) {
                if (mdList == null)
                    mdList = new ArrayList<MetaData>();
                if (!mdList.contains(md))
                    mdList.add(md);

            }
            sp.setMetaDatas(mdList);
            shiftperiodFacade.edit(sp);
        }

        setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.SaveSuccess"));
        // setSelectedItemsDetail(null);
        entityDetail = null;
        resetVariables();
        super.cancelEditing();
        return null;
    }

    public String assignPeriodsMassively() {
        setEntity(null);
        for (MetaData metaData : getDataModel()) {
            if (getSelectedItems().get(metaData.getMetaDataPK())) {
                if (getEntity() == null) {
                    setEntity(facade.find(metaData.getMetaDataPK()));
                }
            }
        }
        if (getEntity() == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneEdit"));
            return null;
        }

        // Se verifica que los guardias tengan asignado un nro telefonico
        try {
            MetaData userphoneMD = null;
            List<MetaData> selectedGuardList = getSelectedItemList();
            for (MetaData metaData : selectedGuardList) {
                userphoneMD = metaDataFacade.findByClientMetaMemberAndCode(
                        SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                        4L, 2L, metaData.getMetaDataPK().getCodeChr());
                if (userphoneMD == null) {
                    setWarnMessage(MessageFormat.format(
                            i18n.iValue("web.client.guardroundconf.service.message.GuardWithNoPhoneNumber"),
                            metaData.getMetaDataPK().getCodeChr()));
                    setEntity(null);
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dataModelDetail = null;
        paginationHelperDetail = null;
        assignConfigurationsMassively = true;
        return null;
    }

    public String removePeriods() {
        List<ShiftPeriod> selectedDetailList = getSelectedItemDetailList();
        if (selectedDetailList == null || selectedDetailList.size() == 0) {
            setWarnMessage(i18n.iValue("web.client.guardroundconf.service.message.NotRegistrySelectedError"));
            return null;
        }

        // REMUEVE LOS SHIFTPERIODS QUE YA NO ESTAN SELECCIONADOS AL GUARDIA
        for (ShiftPeriod sp : getSelectedItemDetailList()) {
            List<MetaData> mdList = shiftperiodFacade.getMetaDatalistByShiftPeriod(sp.getShiftPeriodCod());
            if (mdList.contains(getEntity()))
                mdList.remove(getEntity());
            sp.setMetaDatas(mdList);
            shiftperiodFacade.edit(sp);
        }

        resetPaginationDetail();
        return null;
    }

    public String assignPhoneNumber() {
        setEntity(null);
        for (MetaData metaData : getDataModel()) {
            if (getSelectedItems().get(metaData.getMetaDataPK())) {
                if (getEntity() == null) {
                    setEntity(facade.find(metaData.getMetaDataPK()));
                } else {
                    setEntity(null);
                    setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectJustOneEdit"));
                    return null;
                }
            }
        }
        if (getEntity() == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneEdit"));
            return null;
        }

        try {
            userphoneMeta = metaDataFacade.findByClientMetaMemberAndCode(
                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                    4L, 2L, getEntity().getMetaDataPK().getCodeChr());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userphoneMeta == null)
            userphoneMeta = new MetaData();
        else {
            try {
                selectedUserphone = userphoneFacade.findByCellphoneNum(new BigInteger(userphoneMeta.getValueChr()));
            } catch (GenericFacadeException e) {
            }
        }
        return null;
    }

    public String savePhoneNumber() {
        if (selectedUserphone == null) {
            setWarnMessage(i18n.iValue("web.client.guardroundconf.service.message.MustSelectPhoneNumber"));
            return null;
        }
        List<MetaData> selectedGuardList = getSelectedItemList();
        for (MetaData guard : selectedGuardList) {
            MetaData metaData = null;
            try {
                metaData = metaDataFacade.findByClientMetaMemberAndCode(
                        SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                        4L, 2L, guard.getMetaDataPK().getCodeChr());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (metaData != null) { // el guardia ya posee numero telefonico
                metaData.setValueChr(selectedUserphone.getCellphoneNum().toString()); // se
                                                                                      // modifica
                                                                                      // con
                                                                                      // el
                                                                                      // nuevo
                                                                                      // numero
                                                                                      // seleccionado
                metaDataFacade.edit(metaData);

            } else {
                userphoneMeta.setMetaClient(guard.getMetaClient());
                MetaMemberPK mmpk = new MetaMemberPK(4L, 2L);
                MetaMember mm = metaMemberFacade.find(mmpk);
                userphoneMeta.setMetaMember(mm); // traer el objeto metamember
                                                 // con cod 2

                MetaDataPK mdk = new MetaDataPK();
                mdk.setCodClient(guard.getMetaClient().getClient().getClientCod());
                mdk.setCodeChr(guard.getMetaDataPK().getCodeChr());
                mdk.setCodMember(2L);
                mdk.setCodMeta(4L);
                userphoneMeta.setMetaDataPK(mdk);
                userphoneMeta.setValueChr(selectedUserphone.getCellphoneNum().toString());
                metaDataFacade.create(userphoneMeta);
            }
        }

        setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.SaveSuccess"));
        entityDetail = null;
        userphoneMeta = null;
        selectedUserphone = null;
        super.cancelEditing();
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
                    if (innerWhere == null) {
                        innerWhere = where;
                    } else {
                        if (innerWhere.compareTo(where) == 0) {
                            return count;
                        }
                    }

                    where = where.concat(" AND mc.COD_CLIENT = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
                    where = where.concat(" AND sp.status = 1 ");

                    if (!assignConfigurations && !assignConfigurationsMassively) { // se
                                                                                   // trae
                                                                                   // toda
                                                                                   // la
                                                                                   // lista
                                                                                   // de
                                                                                   // conf
                                                                                   // del
                                                                                   // cliente
                        where = where.concat(
                                " AND md.CODE_CHR = '".concat(getEntity().getMetaDataPK().getCodeChr())).concat(
                                "'");
                    }

                    count = shiftperiodFacade.count(where);
                    return count;
                }

                @Override
                public DataModel createPageDataModel() {
                    String where = "";

                    if (!assignConfigurations && !assignConfigurationsMassively) { // se
                                                                                   // trae
                                                                                   // toda
                                                                                   // la
                                                                                   // lista
                                                                                   // de
                                                                                   // conf
                                                                                   // del
                                                                                   // cliente
                        where = where.concat(
                                " ,IN (o.metaDatas) m WHERE m.metaDataPK.codeChr = '".concat(getEntity().getMetaDataPK().getCodeChr())).concat(
                                "'");
                    } else {
                        where = where.concat(" WHERE 1 = 1 ");
                    }
                    where = where.concat(" AND o.client = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
                    where = where.concat(" AND o.status = true ");

                    String orderby = "o.dateFrom ";

                    return new ListDataModelViewCsTigo(shiftperiodFacade.findRange(
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

    public DataModel<ShiftPeriod> getDataModelDetail() {
        if (dataModelDetail == null) {
            dataModelDetail = getPaginationHelperDetail().createPageDataModel();
            if (assignConfigurations) {
                // SELECCIONAR LOS SHIFTPERIODS ASIGNADOS AL GUARDIA
                List<ShiftPeriod> selectedShiftPeriodList = shiftperiodFacade.getShiftPeriodListByGuard(getEntity().getMetaDataPK().getCodeChr());
                for (ShiftPeriod sp : dataModelDetail) {
                    if (contiene(selectedShiftPeriodList, sp))
                        getSelectedItemsDetail().put(sp.getShiftPeriodCod(),
                                true);
                }
            }
        }

        return dataModelDetail;
    }

    private Boolean contiene(List<ShiftPeriod> spList, ShiftPeriod sp) {
        for (ShiftPeriod shiftPeriod : spList) {
            if (shiftPeriod.getShiftPeriodCod().equals(sp.getShiftPeriodCod()))
                return true;
        }
        return false;
    }

    public void setDataModelDetail(DataModel<ShiftPeriod> dataModelDetail) {
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

    public ShiftPeriod getSelectedItemDetail() {
        entityDetail = null;
        for (ShiftPeriod sp : dataModelDetail) {
            if (getSelectedItemsDetail().get(sp.getShiftPeriodCod())) {
                if (entityDetail == null) {
                    entityDetail = shiftperiodFacade.find(sp.getShiftPeriodCod());
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

    public List<ShiftPeriod> getSelectedItemDetailList() {
        entityDetail = null;
        List<ShiftPeriod> spList = new ArrayList<ShiftPeriod>();
        for (ShiftPeriod sp : dataModelDetail) {
            if (getSelectedItemsDetail().get(sp.getShiftPeriodCod())) {
                entityDetail = shiftperiodFacade.find(sp.getShiftPeriodCod());
                spList.add(sp);
            }
        }
        return spList;
    }

    public List<ShiftPeriod> getNotSelectedItemDetailList() {
        entityDetail = null;
        List<ShiftPeriod> spList = new ArrayList<ShiftPeriod>();
        for (ShiftPeriod sp : dataModelDetail) {
            if (!getSelectedItemsDetail().get(sp.getShiftPeriodCod())) {
                entityDetail = shiftperiodFacade.find(sp.getShiftPeriodCod());
                spList.add(sp);
            }
        }
        return spList;
    }

    public ShiftPeriod getEntityDetail() {
        return entityDetail;
    }

    public void setEntityDetail(ShiftPeriod entityDetailPeriod) {
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
            everyDayConf = new ConfigurationData(null, 0, cal.getTime(), cal.getTime(), 0L, 0L, false, false);
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
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Monday"), 2, cal.getTime(), cal.getTime(), 0L, 0L, false, false));
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Tuesday"), 3, cal.getTime(), cal.getTime(), 0L, 0L, false, false));
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Wednesday"), 4, cal.getTime(), cal.getTime(), 0L, 0L, false, false));
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Thursday"), 5, cal.getTime(), cal.getTime(), 0L, 0L, false, false));
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Friday"), 6, cal.getTime(), cal.getTime(), 0L, 0L, false, false));
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Saturday"), 7, cal.getTime(), cal.getTime(), 0L, 0L, false, false));
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Sunday"), 1, cal.getTime(), cal.getTime(), 0L, 0L, false, false));
        }
        return confList;
    }

    public void setConfList(List<ConfigurationData> confList) {
        this.confList = confList;
    }

    public ShiftConfiguration getEveryDayShiftConfig() {
        if (everyDayShiftConfig == null)
            everyDayShiftConfig = new ShiftConfiguration();
        return everyDayShiftConfig;
    }

    public void setEveryDayShiftConfig(ShiftConfiguration everyDayShiftConfig) {
        this.everyDayShiftConfig = everyDayShiftConfig;
    }

    public List<ShiftConfiguration> getShiftConfigurationList() {
        return shiftConfigurationList;
    }

    public void setShiftConfigurationList(List<ShiftConfiguration> shiftConfigurationList) {
        this.shiftConfigurationList = shiftConfigurationList;
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

    public Boolean getAssignConfigurations() {
        return assignConfigurations;
    }

    public void setAssignConfigurations(Boolean assignConfigurations) {
        this.assignConfigurations = assignConfigurations;
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

    public List<MetaData> getSelectedItemList() {
        List<MetaData> mdList = new ArrayList<MetaData>();
        MetaData entity = null;
        for (MetaData metaData : getDataModel()) {
            if (super.getSelectedItems().get(metaData.getMetaDataPK())) {
                entity = facade.find(metaData.getMetaDataPK());
                mdList.add(metaData);
            }
        }
        return mdList;
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

    @Override
    public Userphone getSelectedUserphone() {
        return selectedUserphone;
    }

    @Override
    public void setSelectedUserphone(Userphone selectedUserphone) {
        this.selectedUserphone = selectedUserphone;
    }

    @Override
    public List<Userphone> getUserphoneList() {
        if (userphoneList == null) {
            userphoneList = userphoneFacade.findByUserwebAndClassification(SecurityBean.getInstance().getLoggedInUserClient());
        }
        return userphoneList;
    }

    @Override
    public void setUserphoneList(List<Userphone> userphoneList) {
        this.userphoneList = userphoneList;
    }

    public String getStepFactorInterval() {
        return stepFactorInterval;
    }

    public void setStepFactorInterval(String stepFactorInterval) {
        this.stepFactorInterval = stepFactorInterval;
    }

    public String getStepFactorTolerance() {
        return stepFactorTolerance;
    }

    public void setStepFactorTolerance(String stepFactorTolerance) {
        this.stepFactorTolerance = stepFactorTolerance;
    }
    
    
}
