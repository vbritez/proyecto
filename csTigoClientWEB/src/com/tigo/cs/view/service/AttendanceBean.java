package com.tigo.cs.view.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.ws.WebServiceException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.TmpWsresult;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.ApplicationBean;
import com.tigo.cs.ws.client.ClientWS;
import com.tigo.cs.ws.client.ClientWSService;
import com.tigo.cs.ws.client.Customer;
import com.tigo.cs.ws.client.Motive;

/**
 * 
 * @author Javier Kovacs
 * @version $Id$
 */
@ManagedBean(name = "attendanceBean")
@ViewScoped
public class AttendanceBean extends AbstractServiceBean {

    private static final long serialVersionUID = -8942409573304494181L;
    public static final int COD_SERVICE = 11;
    public static final Long META_EMPLOYEE = 7L;
    private Map<String, String> mapEmployee = new HashMap<String, String>();
    private Map<String, String> mapEvents = new HashMap<String, String>();
    private Map<String, String> mapEventTypes = new HashMap<String, String>();
    private String URLWS = "";
    private boolean metaEnabled = false;
    private boolean wsConexionExists = false;
    private boolean integrationMethodValidated = false;

    public AttendanceBean() {
        setCodService(COD_SERVICE);
        setShowMapOnDetail(true);
        setPdfReportDetailName("rep_attendance_detail");
        setXlsReportDetailName("rep_attendance_detail_xls");
        setPdfReportCabDetailName("rep_attendance_cabdetail");
        setXlsReportCabDetailName("rep_attendance_cabdetail_xls");

    }

    @PostConstruct
    public void init() {
        mapEventTypes.put("I",
                i18n.iValue("web.client.attendance.hashmap.eventtype.Init"));
        mapEventTypes.put("F",
                i18n.iValue("web.client.attendance.hashmap.eventtype.Finish"));
        mapEvents.put("P",
                i18n.iValue("web.client.attendance.hashmap.event.Presence"));
        mapEvents.put("B",
                i18n.iValue("web.client.attendance.hashmap.event.Break"));
        mapEvents.put("L",
                i18n.iValue("web.client.attendance.hashmap.event.Lunch"));

    }

    public Map<String, String> getMapEmployee() {
        return mapEmployee;
    }

    public void setMapEmployee(Map<String, String> mapEmployee) {
        this.mapEmployee = mapEmployee;
    }

    public Map<String, String> getMapEvents() {
        return mapEvents;
    }

    public void setMapEvents(Map<String, String> mapEvents) {
        this.mapEvents = mapEvents;
    }

    public Map<String, String> getMapEventTypes() {
        return mapEventTypes;
    }

    public void setMapEventTypes(Map<String, String> mapEventTypes) {
        this.mapEventTypes = mapEventTypes;
    }

    @Override
    public void headerMetaDataFromModel() {
        Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        validateIntegrationMethod(codClient);

        mapEmployee = new HashMap<String, String>();
        if (getDataModelHeader() != null
            && getDataModelHeader().getRowCount() > 0) {
            for (ServiceValue sv : getDataModelHeader()) {
                try {
                    String key = sv.getColumn1Chr();
                    String value = getValueByIntegrationMethod(codClient,
                            META_EMPLOYEE, key);
                    mapEmployee.put(
                            key,
                            value == null ? null : value.trim().equals("") ? null : value.trim());
                } catch (Exception ex) {
                    notifier.signal(
                            getClass(),
                            Action.ERROR,
                            SecurityBean.getInstance().getLoggedInUserClient(),
                            null,
                            getCurrentViewId(),
                            i18n.iValue("web.client.backingBean.message.ClientsTempTableError"),
                            getSessionId(), getIpAddress(), ex);
                }
            }
        }
    }

    private String getValueByIntegrationMethod(Long codClient, Long metaCode, String key) throws MoreThanOneResultException, GenericFacadeException {
        String retValue = null;
        if (metaEnabled) {
            MetaData md = metaDataFacade.findByClientMetaMemberAndCode(
                    codClient, metaCode, 1L, key);
            if (md != null) {
                retValue = md.getValueChr();
            }
        } else if (wsConexionExists) {
            retValue = tmpWsresultFacade.findData(codClient, getSessionId(),
                    "E", key);
            if (retValue == null) {
                retValue = consumeClientWS(codClient, key, "E");
            }
        }
        return retValue;
    }

    private String consumeClientWS(Long codClient, String key, String discriminator) {
        String value = null;
        try {
            URL url = new URL(URLWS);
            ClientWSService service = new ClientWSService(url);
            ClientWS port = service.getClientWSPort();
            if (discriminator.equals("C")) {
                Customer customer = port.getCustomerByCode(key);
                value = customer != null && customer.getName() != null ? customer.getName().trim() : null;
            } else {
                Motive motive = port.getMotiveByCode(key);
                value = motive != null && motive.getName() != null ? motive.getName().trim() : null;
            }

            TmpWsresult tmp = new TmpWsresult();
            tmp.setCodClient(codClient);
            tmp.setSessionId(getSessionId());
            tmp.setDataType(discriminator);
            tmp.setDataId(key);
            tmp.setDataC(value != null && !value.trim().equals("") ? value.trim() : " ");
            tmpWsresultFacade.create(tmp);
        } catch (MalformedURLException ex) {
            String msg = MessageFormat.format(
                    i18n.iValue("web.client.backingBean.message.WrongWSInvocation"),
                    i18n.iValue("web.servicename.Inventory"), codClient);
            notifier.signal(getClass(), Action.ERROR,
                    SecurityBean.getInstance().getLoggedInUserClient(), null,
                    getCurrentViewId(), msg, getSessionId(), getIpAddress(), ex);
            wsConexionExists = Boolean.FALSE;
        } catch (WebServiceException we) {
            String msg = MessageFormat.format(
                    i18n.iValue("web.client.backingBean.message.WrongWSInvocation"),
                    i18n.iValue("web.servicename.Inventory"), codClient);
            notifier.signal(getClass(), Action.ERROR,
                    SecurityBean.getInstance().getLoggedInUserClient(), null,
                    getCurrentViewId(), msg, getSessionId(), getIpAddress(), we);
            wsConexionExists = Boolean.FALSE;
        }
        return value;
    }

    private void validateIntegrationMethod(Long codClient) {
        if (!integrationMethodValidated) {
            // Valida si existe META-EMPLOYEE
            try {
                MetaClient mc = metaClientFacade.findByMetaAndClient(codClient,
                        META_EMPLOYEE);
                if (mc != null && mc.getEnabledChr()) {
                    metaEnabled = true;
                }
            } catch (Exception ex) {
                Logger.getLogger(VisitBean.class.getName()).log(Level.FATAL,
                        null, ex);
            }

            // Nueva validación

            String enabled = clientParameterValueFacade.findByCode(
                    getUserweb().getClient().getClientCod(),
                    "client.ws.enabled");
            if (enabled != null && enabled.equals("1")) {
                String url = clientParameterValueFacade.findByCode(
                        getUserweb().getClient().getClientCod(),
                        "client.ws.url");
                if (url != null && !url.isEmpty()) {
                    wsConexionExists = true;
                    URLWS = url;
                }
            }

            integrationMethodValidated = true;
        }
    }

    // ABSTRACT METHOD IMPLEMENTATIONS
    @Override
    public String getDetailWhereCriteria() {
        return "";
    }

    @Override
    public String getDetailReportWhereCriteria() {
        return "";
    }

    @Override
    public String getCabDetailReportOrderBy() {
        String sortAttributeColumnName = "".concat(getAttributeColumnName(
                ServiceValue.class, getSortHelper().getField()));
        if (getSortHelper().getField().indexOf(".") < 0) {
            sortAttributeColumnName = "sv.".concat(sortAttributeColumnName);
        } else {
            sortAttributeColumnName = "svd.".concat(sortAttributeColumnName);
        }
        String orderBy = "ORDER BY ";
        /*
         * si esta ordenado por empleado, entonces ordenar tambien por fecha
         * ascendente de la cabecera y el detalle
         */
        if (sortAttributeColumnName.toLowerCase().equals("sv.column1_chr")) {
            orderBy = orderBy.concat(sortAttributeColumnName).concat(
                    getSortHelper().isAscending() ? " ASC" : " DESC");
            orderBy = orderBy.concat(",sv.recorddate_dat ASC ");
            orderBy = orderBy.concat(",svd.recorddate_dat ASC");
        } /*
           * si no esta ordenado por empleado, ordenar por el otro campo a
           * ordenar, y la fecha del detalle tambien
           */else if (sortAttributeColumnName.toLowerCase().equals(
                "sv.recorddate_dat")) {
            orderBy = orderBy.concat("sv.column1_chr");
            orderBy = orderBy.concat(",sv.recorddate_dat").concat(
                    getSortHelper().isAscending() ? " ASC" : " DESC");
            orderBy = orderBy.concat(",svd.recorddate_dat ").concat(
                    getSortHelper().isAscending() ? " ASC" : " DESC");
        } /*
           * ordenar por empleado y por la columna selaccionada, si o si se
           * ordena por fecha de detalle ascendente
           */else {
            orderBy = orderBy.concat("sv.column1_chr,");
            orderBy = orderBy.concat(sortAttributeColumnName).concat(
                    getSortHelper().isAscending() ? " ASC" : " DESC");
            orderBy = orderBy.concat(",svd.recorddate_dat ASC");
        }
        return orderBy;
    }

    @Override
    public String getMessageDescriptionDetailMap(ServiceValueDetail svd) {
        String messDescrip = "";
        try {
            DateFormat sdf = new SimpleDateFormat(ApplicationBean.getInstance().getDefaultOutputDateTimeFormat());
            Long codClient = svd.getMessage().getUserphone().getClient().getClientCod();
            String employeeCode = getServiceValue().getColumn1Chr();
            String employee = getValueByIntegrationMethod(codClient,
                    META_EMPLOYEE, employeeCode);
            messDescrip = employeeCode.concat(
                    employee != null && employee.trim().length() > 0 ? " - ".concat(employee) : "").concat(
                    " - ").concat(mapEvents.get(svd.getColumn1Chr())).concat(
                    "(").concat(mapEventTypes.get(svd.getColumn2Chr())).concat(
                    ")").concat(" | ").concat(
                    sdf.format(svd.getMessage().getDateinDat()));

        } catch (MoreThanOneResultException ex) {
            java.util.logging.Logger.getLogger(AttendanceBean.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } catch (GenericFacadeException ex) {
            java.util.logging.Logger.getLogger(AttendanceBean.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        }
        return messDescrip;
    }
}
