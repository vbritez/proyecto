package com.tigo.cs.view.report;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.PrimarySortedFieldNotFoundException;
import com.tigo.cs.commons.jpa.Searchable;
import com.tigo.cs.commons.util.DateUtil;
import com.tigo.cs.commons.util.ListDataModelViewCsTigo;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.commons.util.PaginationHelper;
import com.tigo.cs.commons.util.SortHelper;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.ReportConfig;
import com.tigo.cs.domain.ReportConfigMail;
import com.tigo.cs.domain.Screenclient;
import com.tigo.cs.domain.TrackingConfiguration;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.facade.ClassificationFacade;
import com.tigo.cs.facade.I18nBean;
import com.tigo.cs.facade.ModuleclientFacade;
import com.tigo.cs.facade.ReportConfigFacade;
import com.tigo.cs.facade.ScreenClientFacade;
import com.tigo.cs.facade.UserphoneFacade;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.ApplicationBean;
import com.tigo.cs.view.metadata.AbstractCrudBean;
import com.tigo.cs.view.service.ConfigurationData;

@ManagedBean(name = "reportAutomConfigBean")
@ViewScoped
public class ReportAutomConfigBean extends SMBaseBean implements Serializable {

    private static final long serialVersionUID = 296909095164968934L;

    private static final String DAILY = "DAILY";
    private static final String WEEKLY = "WEEKLY";
    private static final String MONTHLY = "MONTHLY";

    @EJB
    private ScreenClientFacade screenClientFacade;
    @EJB
    private UserphoneFacade userphoneFacade;
    @EJB
    private ClassificationFacade classificationFacade;
    @EJB
    private ReportConfigFacade reportConfigFacade;
    @EJB
    private ModuleclientFacade mcFacade;
    @EJB
    private I18nBean i18n;

    private ReportConfig entity;
    private MetaData userphoneMeta;
    private Userphone selectedUserphone;
    protected List<Userphone> userphoneList;
    private Boolean showConfigTracking = false;

    private Client client;
    private Integer daysOption;
    private TrackingConfiguration everyDayTrackingConfig;
    private List<ConfigurationData> confList;
    private List<TrackingConfiguration> trackingConfigurationList;
    private List<String> supNumberList;
    private String concatSupNumbersStr;
    private Boolean deassignConfigurationsMassively = false;
    private Boolean assignConfigurationsMassively = false;
    private Boolean isEditing;
    private Date oldDateFrom;
    private Date oldDateTo;
    private List<SelectItem> rowQuantList;
    private String rowQuantSelected;
    private String filterSelectedField;
    private String filterCriteria;
    private String aditionalFilter;
    private SortHelper sortHelper;
    private List<SelectItem> filterFields;

    private DataModel<ReportConfig> dataModel;
    private Map<Object, Boolean> selectedItems;
    private PaginationHelper paginationHelper;
    private int lastActualPage = -1;
    private String tableDetailsTitle;
    private String primarySortedField;
    private boolean primarySortedFieldAsc = true;

    private Boolean validatedAllReports = false;
    private List<Screenclient> screenclientList;
    private List<Screenclient> selectedScreenclientList;
    private List<Integer> dayOfMonthList;
    private String newMail;
    private List<String> mailList;
    private String selectedMail;
    private List<Classification> classificationList;
    private List<Classification> selectedClassificationList;
    private Boolean validatedAllClassifications = false;

    public ReportAutomConfigBean() {
        initialize();
    }

    private void initialize() {
        // Initialize filter criteria with no filter
        filterSelectedField = "-1";
        filterCriteria = "";

        // Initialize sort with default values
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
    }

    public String getTableDetailsTitle() {
        tableDetailsTitle = i18n.iValue("web.client.trackingconf.screen.title.TotaLListOf");
        return tableDetailsTitle;
    }

    public void setTableDetailsTitle(String tableDetailsTitle) {
        this.tableDetailsTitle = tableDetailsTitle;
    }

    public void resetVariables() {
        setSelectedItems(null);
    }

    public String newEntity() {
        entity = new ReportConfig();
        getMailList();
        // entity.setDayNum(-1);
        return null;
    }

    public String editEntityDetail() {
        if (getSelectedItemDetail() == null)
            return null;

        getMailList();
        List<ReportConfigMail> reportConfigMailList = reportConfigFacade.getReportConfigMailList(entity.getReportConfigCod());
        for (ReportConfigMail mail : reportConfigMailList) {
            mailList.add(mail.getMailChr());
        }
        selectedScreenclientList = reportConfigFacade.getReportConfigScreenclientList(entity.getReportConfigCod());
        selectedClassificationList = reportConfigFacade.getReportConfigClassificationList(entity.getReportConfigCod());

        getScreenclientList();
        getClassificationList();
        if (selectedScreenclientList.size() == screenclientList.size()) {
            validatedAllReports = true;
            selectedScreenclientList = null;
        }

        if (selectedClassificationList.size() == classificationList.size()) {
            validatedAllClassifications = true;
            selectedClassificationList = null;
        }
        return null;
    }

    public String configTracking() {
        showConfigTracking = true;
        dataModel = null;
        paginationHelper = null;

        // setEntity(new Userphone());
        return null;
    }

    public String cancelEditing() {
        entity = null;
        clearData();
        resetVariables();
        return null;
    }

    public String deleteEditingDetail() {
        List<ReportConfig> selectedDetailList = getSelectedItemDetailList();
        if (selectedDetailList == null || selectedDetailList.size() == 0) {
            setWarnMessage(i18n.iValue("web.client.trackingconf.service.message.NotRegistrySelectedError"));
            return null;
        }

        /*
         * BORRA LOGICAMENTE LOS Reportconfig SELECCIONADOS, NO BORRA
         * FISICAMENTE. Es decir, pone status 0 a los shiftConfigurations
         * asignados al shiftperiod, y al shiftperiod.
         */
        try {
            for (ReportConfig sp : getSelectedItemDetailList()) {
                reportConfigFacade.deleteEntity(sp);
            }
            clearData();
            resetPagination();
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
        if ((selectedScreenclientList == null || selectedScreenclientList.size() == 0)
            && !validatedAllReports) {
            setWarnMessage(i18n.iValue("web.client.reportconfig.service.message.ReportValidationError"));
            return false;
        }

        if (entity.getConfigTypeChr() == null) {
            setWarnMessage(i18n.iValue("web.client.reportconfig.service.message.ConfigTypeValidationError"));
            return false;
        }

        if (entity.getTimeDat() == null) {
            setWarnMessage(i18n.iValue("web.client.reportconfig.service.message.TimeValidationError"));
            return false;
        }

        if (entity.getConfigTypeChr() != null
            && entity.getConfigTypeChr().equals(WEEKLY)
            && entity.getDayNum() == null) {
            setWarnMessage(i18n.iValue("web.client.reportconfig.service.message.DayOfWeekValidationError"));
            return false;
        }

        if (entity.getConfigTypeChr() != null
            && entity.getConfigTypeChr().equals(MONTHLY)
            && entity.getDayNum() == null) {
            setWarnMessage(i18n.iValue("web.client.reportconfig.service.message.DayOfMonthValidationError"));
            return false;
        }

        if (mailList == null || mailList.size() == 0) {
            setWarnMessage(i18n.iValue("web.client.reportconfig.service.message.MailValidationError"));
            return false;
        }

        if ((selectedClassificationList == null || selectedClassificationList.size() == 0)
            && !validatedAllClassifications) {
            setWarnMessage(i18n.iValue("web.client.reportconfig.service.message.ClassificationValidationError"));
            return false;
        }

        // SI NO EXISTE OTRA CONFIGURACION CON LOS MISMOS VALORES

        return true;
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

    public String saveEditing() {
        if (datosValidados()) {
            saveDetail();
        }
        return null;
    }

    public String saveDetail() {
        try {
            List<ReportConfigMail> reportConfigMailList = new ArrayList<ReportConfigMail>();
            for (String mail : mailList) {
                if (mail != null && !mail.equals("")) {
                    ReportConfigMail rcm = new ReportConfigMail();
                    rcm.setMailChr(mail.trim());
                    reportConfigMailList.add(rcm);
                }
            }

            entity.setReportConfigMails(reportConfigMailList);
            entity.setClient(SecurityBean.getInstance().getLoggedInUserClient().getClient());
            entity.setUserweb(SecurityBean.getInstance().getLoggedInUserClient());
            entity.setDescriptionChr(getDescription());
            entity.setStartTimeDat(new Date());
            entity.setNextExecutionTimeDat(new Date());

            if (validatedAllReports)
                entity.setScreenclients(screenclientList);
            else
                entity.setScreenclients(selectedScreenclientList);

            if (validatedAllClassifications)
                entity.setClassifications(classificationList);
            else
                entity.setClassifications(selectedClassificationList);

            entity.setStatusChr(true);

            reportConfigFacade.saveEditing(entity);

            setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.SaveSuccess"));
            clearData();
            resetPagination();

        } catch (GenericFacadeException e) {
            setErrorMessage(i18n.iValue("web.client.backingBean.message.Error"));
        } catch (Exception e) {
            setErrorMessage(i18n.iValue("web.client.backingBean.message.Error"));
        }
        return null;
    }

    private String getConfigType(String type) {
        if (type.equals(DAILY))
            return i18n.iValue("web.client.reportconfig.screen.label.Daily");
        if (type.equals(WEEKLY))
            return i18n.iValue("web.client.reportconfig.screen.label.Weekly");
        if (type.equals(MONTHLY))
            return i18n.iValue("web.client.reportconfig.screen.label.Monthly");
        return "";
    }

    private String getDescription() {
        String starttimeStr = DateUtil.getFormattedDate(entity.getTimeDat(),
                "HH:mm");
        String toRet = "CONFIGURACION: ".concat(getConfigType(
                entity.getConfigTypeChr()).concat(". HORA: ").concat(
                starttimeStr).concat("."));
        if (entity.getConfigTypeChr().equals(WEEKLY))
            toRet = toRet.concat(" DIA DE LA SEMANA: ".concat(getDay(entity.getDayNum())));
        if (entity.getConfigTypeChr().equals(MONTHLY))
            toRet = toRet.concat(" DIA DEL MES: ".concat(entity.getDayNum().toString()));

        return toRet;
    }

    private String getDay(Integer dayNum) {
        if (dayNum == 1)
            return i18n.iValue("web.client.guardroundconf.screen.field.Sunday");

        if (dayNum == 2)
            return i18n.iValue("web.client.guardroundconf.screen.field.Monday");

        if (dayNum == 3)
            return i18n.iValue("web.client.guardroundconf.screen.field.Tuesday");

        if (dayNum == 4)
            return i18n.iValue("web.client.guardroundconf.screen.field.Wednesday");

        if (dayNum == 5)
            return i18n.iValue("web.client.guardroundconf.screen.field.Thursday");

        if (dayNum == 6)
            return i18n.iValue("web.client.guardroundconf.screen.field.Friday");

        if (dayNum == 7)
            return i18n.iValue("web.client.guardroundconf.screen.field.Saturday");

        return "";
    }

    private TrackingConfiguration getShiftConfigurationForEveryDayOption() {
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
        if (newMail != null && !newMail.equals(""))
            getMailList().add(newMail);
        newMail = null;
        return null;
    }

    public String removeFromList() {
        if (selectedMail != null)
            getMailList().remove(selectedMail);
        newMail = null;
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

    private ConfigurationData getFirstConfigurationDataChecked() {
        for (ConfigurationData cd : confList) {
            if (cd.getChecked())
                return cd;
        }
        return null;
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

    public String saveAssignPeriods() {

        return null;
    }

    public String saveDeassignPeriodsMassively() {

        return null;
    }

    // /--------------------------------------------------------------------------------//
    // --------------------------------------------------------------------------------------
    // LIST AND TABLE METHODS
    // --------------------------------------------------------------------------------------

    public PaginationHelper getPaginationHelper() {
        if (paginationHelper == null) {
            int pageSize = getRowQuantSelected().length() > 0 ? Integer.valueOf(
                    getRowQuantSelected()).intValue() : 0;

            paginationHelper = new PaginationHelper(pageSize) {

                String innerWhere = null;
                Integer count = null;

                @Override
                public int getItemsCount() {

                    String where = "";
                    if (!filterSelectedField.equals("-1")
                        && filterCriteria.length() > 0) {
                        Class<?> fieldClass = getFieldType(filterSelectedField);

                        if (fieldClass.equals(Integer.class)
                            || fieldClass.equals(Long.class)
                            || fieldClass.equals(BigInteger.class)) {
                            where = "where o.".concat(filterSelectedField).concat(
                                    " = ").concat(filterCriteria);
                        } else if (fieldClass.equals(String.class)) {
                            where = "where lower(o.".concat(filterSelectedField).concat(
                                    ") LIKE '%").concat(
                                    filterCriteria.toLowerCase()).concat("%'");
                        }
                    }

                    if (where.equals("")) {
                        where = where.concat(" WHERE 1 = 1 ");
                    }

                    where = where.concat(" AND o.client = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
                    where = where.concat(" AND o.statusChr = true ");
                    if (innerWhere == null) {
                        innerWhere = where;
                    } else {
                        if (innerWhere.compareTo(where) == 0) {
                            return count;
                        }
                    }

                    count = reportConfigFacade.count(where);
                    return count;
                }

                @Override
                public DataModel createPageDataModel() {

                    String where = "";
                    if (!filterSelectedField.equals("-1")
                        && filterCriteria.length() > 0) {
                        Class<?> fieldClass = getFieldType(filterSelectedField);

                        if (fieldClass.equals(Integer.class)
                            || fieldClass.equals(Long.class)
                            || fieldClass.equals(BigInteger.class)) {
                            where = "where o.".concat(filterSelectedField).concat(
                                    " = ").concat(filterCriteria);
                        } else if (fieldClass.equals(String.class)) {
                            where = "where lower(o.".concat(filterSelectedField).concat(
                                    ") LIKE '%").concat(
                                    filterCriteria.toLowerCase()).concat("%'");
                        }
                    }
                    if (where.equals("")) {
                        where = where.concat(" WHERE 1 = 1 ");
                    }

                    where = where.concat(" AND o.client = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
                    where = where.concat(" AND o.statusChr = true ");

                    // String orderby = "o.startTimeDat ";
                    String orderby = "o.".concat(sortHelper.getField()).concat(
                            sortHelper.isAscending() ? " ASC" : " DESC");
                    return new ListDataModelViewCsTigo(reportConfigFacade.findRange(
                            new int[] { getPageFirstItem(), getPageFirstItem()
                                + getPageSize() }, where, orderby));
                }
            };
            if (lastActualPage >= 0) {
                paginationHelper.setActualPage(lastActualPage);
                lastActualPage = -1;
            }
        }

        return paginationHelper;
    }

    protected void setPaginationHelper(PaginationHelper paginationHelper) {
        this.paginationHelper = paginationHelper;
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
                return ReportConfig.class.getMethod(methodName, new Class[0]).getReturnType();
            }

            Class<?> internalClass = ReportConfig.class.getMethod(methodName,
                    new Class[0]).getReturnType();
            methodName = "get".concat(
                    internalFieldName.substring(0, 1).toUpperCase()).concat(
                    internalFieldName.substring(1));

            return internalClass.getMethod(methodName, new Class[0]).getReturnType();
        } catch (Exception e) {
            return Object.class;
        }
    }

    public DataModel<ReportConfig> getDataModel() {
        if (dataModel == null) {
            dataModel = getPaginationHelper().createPageDataModel();
        }

        return dataModel;
    }

    public void setDataModel(DataModel<ReportConfig> dataModel) {
        this.dataModel = dataModel;
    }

    public String nextPageDetail() {
        getPaginationHelper().nextPage();
        dataModel = null; // For data model recreation
        selectedItems = null; // For clearing selection
        return null;
    }

    public void resetPagination() {
        lastActualPage = 0;
        filterSelectedField = "-1";
        filterCriteria = "";
        entity = null;
        paginationHelper = null; // For pagination recreation
        dataModel = null; // For data model recreation
        selectedItems = null; // For clearing selection
    }

    public String previousPageDetail() {
        getPaginationHelper().previousPage();
        dataModel = null; // For data model recreation
        selectedItems = null; // For clearing selection
        return null;
    }

    public Map<Object, Boolean> getSelectedItems() {
        if (selectedItems == null) {
            selectedItems = new HashMap<Object, Boolean>();
        }

        return selectedItems;
    }

    public void setSelectedItems(Map<Object, Boolean> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public String applySortDetail() {
        dataModel = null; // For data model recreation
        selectedItems = null; // For clearing selection
        return null;
    }

    public ReportConfig getSelectedItemDetail() {
        entity = null;
        for (ReportConfig rc : dataModel) {
            if (getSelectedItems().get(rc.getReportConfigCod())) {
                if (entity == null) {
                    entity = reportConfigFacade.find(rc.getReportConfigCod());
                } else {
                    entity = null;
                    setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectJustOneEdit"));
                    return null;
                }
            }
        }

        if (entity == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneEdit"));
        }

        return entity;
    }

    public List<ReportConfig> getSelectedItemDetailList() {
        entity = null;
        List<ReportConfig> tpList = new ArrayList<ReportConfig>();
        for (ReportConfig rc : dataModel) {
            if (getSelectedItems().get(rc.getReportConfigCod())) {
                entity = reportConfigFacade.find(rc.getReportConfigCod());
                tpList.add(rc);
            }
        }
        return tpList;
    }

    public List<ReportConfig> getNotSelectedItemDetailList() {
        entity = null;
        List<ReportConfig> spList = new ArrayList<ReportConfig>();
        for (ReportConfig sp : dataModel) {
            if (!getSelectedItems().get(sp.getReportConfigCod())) {
                entity = reportConfigFacade.find(sp.getReportConfigCod());
                spList.add(sp);
            }
        }
        return spList;
    }

    public ReportConfig getEntity() {
        return entity;
    }

    public void setEntity(ReportConfig entityPeriod) {
        this.entity = entityPeriod;
    }

    public Integer getDaysOption() {
        return daysOption;
    }

    public void setDaysOption(Integer daysOption) {
        this.daysOption = daysOption;
    }

    public List<ConfigurationData> getConfList() {
        if (confList == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
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

    public String getSelectedMail() {
        return selectedMail;
    }

    public void setSelectedMail(String selectedSupNumber) {
        this.selectedMail = selectedSupNumber;
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
        return uList;
    }

    public String getFilterCriteria() {
        return filterCriteria;
    }

    public void setFilterCriteria(String filterCriteria) {
        this.filterCriteria = filterCriteria;
    }

    public String getFilterSelectedField() {
        return filterSelectedField;
    }

    public void setFilterSelectedField(String filterSelectedField) {
        /*
         * Se puso esta verificacion porque en algun punto del jsf se setea a
         * null la variable filterSelectedField
         */
        if (filterSelectedField == null)
            filterSelectedField = this.filterSelectedField != null ? this.filterSelectedField : "-1";
        this.filterSelectedField = filterSelectedField;
    }

    public String getAditionalFilter() {
        return aditionalFilter;
    }

    public void setAditionalFilter(String aditionalFilter) {
        this.aditionalFilter = aditionalFilter;
    }

    public static void main(String args[]) {

        GregorianCalendar calendar = new GregorianCalendar();
        final int currentDayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) - 7) % 7;
        calendar.add(Calendar.DAY_OF_YEAR, currentDayOfWeek);

        System.out.println(calendar.getTime());
    }

    private void clearData() {
        validatedAllReports = false;
        validatedAllClassifications = false;
        selectedScreenclientList = null;
        selectedClassificationList = null;
        mailList = null;
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
            userphoneList = userphoneFacade.findByUserwebAndClassification(SecurityBean.getInstance().getLoggedInUserClient());
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

    // FILTER AND SORT METHODS
    // --------------------------------------------------------------------------------------
    public String applyFilter() {
        if (!filterSelectedField.equals("-1")) {
            Class<?> fieldClass = getFieldType(filterSelectedField);

            if ((fieldClass.equals(Integer.class) && !NumberUtil.isInteger(filterCriteria))
                || (fieldClass.equals(Long.class) && !NumberUtil.isLong(filterCriteria))
                || (fieldClass.equals(BigInteger.class) && !NumberUtil.isBigInteger(filterCriteria))) {
                setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustBeInteger"));
                filterCriteria = "";
            } else {
                // signalRead();
            }
            paginationHelper = null; // For pagination recreation
            dataModel = null; // For data model recreation
            selectedItems = null; // For clearing selection
        } else {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectFindOption"));
        }
        return null;
    }

    public String cleanFilter() {
        filterSelectedField = "-1";
        filterCriteria = "";
        paginationHelper = null; // For pagination recreation
        dataModel = null; // For data model recreation
        selectedItems = null; // For clearing selection
        return null;
    }

    public String applySort() {
        dataModel = null; // For data model recreation
        selectedItems = null; // For clearing selection
        return null;
    }

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

    public void setRowQuantList(List<SelectItem> rowQuantList) {
        this.rowQuantList = rowQuantList;
    }

    public String getRowQuantSelected() {
        if (rowQuantSelected == null) {
            rowQuantSelected = ApplicationBean.getInstance().getDefaultCrudPaginationPageSize();
        }
        return rowQuantSelected;
    }

    public void setRowQuantSelected(String rowQuantSelected) {
        this.rowQuantSelected = rowQuantSelected;
    }

    public void applyQuantity(ValueChangeEvent evnt) {
        paginationHelper = null; // For pagination recreation
        dataModel = null; // For data model recreation
        selectedItems = null; // For clearing selection
    }

    public boolean showAllInQuantityList() {
        return false;
    }

    protected String getPrimarySortedField() throws PrimarySortedFieldNotFoundException {
        if (primarySortedField == null) {
            Field[] fieds = ReportConfig.class.getDeclaredFields();
            for (Field field : fieds) {
                PrimarySortedField annotation = field.getAnnotation(PrimarySortedField.class);
                if (annotation != null) {
                    primarySortedField = field.getName();
                    primarySortedFieldAsc = annotation.ascending();
                    break;
                }
            }
            if (primarySortedField == null) {
                throw new PrimarySortedFieldNotFoundException(ReportConfig.class);
            }
        }
        return primarySortedField;
    }

    public List<SelectItem> getFilterFields() {
        if (filterFields == null) {
            filterFields = new ArrayList<SelectItem>();

            Field[] fieds = ReportConfig.class.getDeclaredFields();
            for (Field field : fieds) {
                Searchable annotation = field.getAnnotation(Searchable.class);
                if (annotation != null) {
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

    public SortHelper getSortHelper() {
        return sortHelper;
    }

    public void setSortHelper(SortHelper sortHelper) {
        this.sortHelper = sortHelper;
    }

    public Client getClient() {
        if (client == null) {
            client = SecurityBean.getInstance().getLoggedInUserClient().getClient();
        }
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Boolean getValidatedAllReports() {
        return validatedAllReports;
    }

    public void setValidatedAllReports(Boolean validatedAllReports) {
        this.validatedAllReports = validatedAllReports;
    }

    private List<Screenclient> getDoNotShowScreenclientList() {
        List<Screenclient> doNotShowScreenclientList = new ArrayList<Screenclient>();
        try {
            doNotShowScreenclientList.add(screenClientFacade.findByPage("/domain/cstigowebclient/reportautomconf.xhtml"));
            doNotShowScreenclientList.add(screenClientFacade.findByPage("/domain/cstigowebclient/repsmsviewerfull.xhtml"));
            doNotShowScreenclientList.add(screenClientFacade.findByPage("/domain/cstigowebclient/repaudit.xhtml"));
            doNotShowScreenclientList.add(screenClientFacade.findByPage("/domain/cstigowebclient/repfeature.xhtml"));
        } catch (MoreThanOneResultException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (GenericFacadeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return doNotShowScreenclientList;
    }

    public List<Screenclient> getScreenclientList() {
        if (screenclientList == null) {
            try {
                screenclientList = mcFacade.getScreenclientListByModuleAndUserweb(
                        3L,
                        SecurityBean.getInstance().getLoggedInUserClient().getUserwebCod(),
                        false);

                if (screenclientList != null && screenclientList.size() > 0) {
                    for (Screenclient screen : getDoNotShowScreenclientList()) {
                        if (screenclientList.contains(screen))
                            screenclientList.remove(screen);
                    }
                }
            } catch (GenericFacadeException e) {
                e.printStackTrace();
            }
        }
        return screenclientList;
    }

    public List<Screenclient> getSelectedScreenclientList() {
        return selectedScreenclientList;
    }

    public void setScreenclientList(List<Screenclient> screenclientList) {
        this.screenclientList = screenclientList;
    }

    public void setSelectedScreenclientList(List<Screenclient> selectedScreenclientList) {
        this.selectedScreenclientList = selectedScreenclientList;
    }

    public void selectAllReports() {
        if (validatedAllReports)
            selectedScreenclientList = null;
    }

    public void selectAllClassifications() {
        if (validatedAllClassifications)
            selectedClassificationList = null;
    }

    public List<Integer> getDayOfMonthList() {
        if (dayOfMonthList == null) {
            dayOfMonthList = new ArrayList<Integer>();
            for (int i = 1; i <= 31; i++) {
                dayOfMonthList.add(i);
            }
        }
        return dayOfMonthList;
    }

    public void setDayOfMonthList(List<Integer> dayOfMonthList) {
        this.dayOfMonthList = dayOfMonthList;
    }

    public String getNewMail() {
        return newMail;
    }

    public List<String> getMailList() {
        if (mailList == null)
            mailList = new ArrayList<String>();
        return mailList;
    }

    public void setNewMail(String newMail) {
        this.newMail = newMail;
    }

    public void setMailList(List<String> mailList) {
        this.mailList = mailList;
    }

    public List<Classification> getClassificationList() {
        if (classificationList == null) {
            List<Classification> userwebClassification = classificationFacade.findByUserwebWithChilds(SecurityBean.getInstance().getLoggedInUserClient());
            classificationList = new ArrayList<Classification>();
            for (Classification c : userwebClassification) {
                List<Classification> childrenClassif = classificationFacade.getChildsClassification(
                        c, true);
                if (childrenClassif != null) {
                    for (Classification child : childrenClassif) {
                        if (!classificationList.contains(child))
                            classificationList.add(child);
                    }
                }
            }
            userphoneList = null;
        }
        return classificationList;
    }

    public List<Classification> getSelectedClassificationList() {
        return selectedClassificationList;
    }

    public void setClassificationList(List<Classification> classificationList) {
        this.classificationList = classificationList;
    }

    public void setSelectedClassificationList(List<Classification> selectedClassificationList) {
        this.selectedClassificationList = selectedClassificationList;
    }

    public Boolean getValidatedAllClassifications() {
        return validatedAllClassifications;
    }

    public void setValidatedAllClassifications(Boolean validatedAllClassifications) {
        this.validatedAllClassifications = validatedAllClassifications;
    }

}
