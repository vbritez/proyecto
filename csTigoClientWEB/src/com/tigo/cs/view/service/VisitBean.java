package com.tigo.cs.view.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.ws.WebServiceException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.util.JRImageLoader;

import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Polygon;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.report.JasperReportUtils;
import com.tigo.cs.commons.report.ReportType;
import com.tigo.cs.commons.util.DateUtil;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.ClientFile;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.TmpWsresult;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.facade.ClientParameterValueFacade;
import com.tigo.cs.facade.MetaClientFacade;
import com.tigo.cs.facade.MetaDataFacade;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.ApplicationBean;
import com.tigo.cs.ws.client.ClientWS;
import com.tigo.cs.ws.client.ClientWSService;
import com.tigo.cs.ws.client.Customer;
import com.tigo.cs.ws.client.Motive;

/**
 * 
 * @author Miguel Zorrilla
 */
@ManagedBean(name = "visitBean")
@ViewScoped
public class VisitBean extends AbstractServiceBean {

    private static final long serialVersionUID = -466196929898797994L;
    public static final int COD_SERVICE = 1;
    public static final Long META_CLIENT = 1L;
    public static final Long META_MOTIVE = 3L;
    @EJB
    private MetaClientFacade metaClientFacade;
    @EJB
    private MetaDataFacade metaDataFacade;
    @EJB
    private ClientParameterValueFacade clientParameterValueFacade;
    private Map<String, String> mapEncodingEvents = new HashMap<String, String>();
    private Map<String, String> mapClients = new HashMap<String, String>();
    private Map<String, String> mapMotives = new HashMap<String, String>();
    private String URLWS = "";
    private boolean metaEnabled = false;
    private Boolean metaMotiveEnabled = null;
    private boolean wsConexionExists = false;
    private boolean integrationMethodValidated = false;
    private String eventType;
    private Boolean findAll = false;

    private String totalDuration = "";

    public VisitBean() {
        setCodService(COD_SERVICE);
        setShowMapOnHeader(true);
        setShowMapOnDetail(true);
        setPdfReportDetailName("rep_visit_detail");
        setXlsReportDetailName("rep_visit_detail_xls");
        setPdfReportCabDetailName("rep_visit_cabdetail");
        setXlsReportCabDetailName("rep_visit_cabdetail_xls");

    }

    @Override
	@PostConstruct
    public void init() {
    	super.init();
        mapEncodingEvents.put("ENTSAL",
                i18n.iValue("web.client.backingBean.visit.message.QuickVisit"));
        mapEncodingEvents.put("ENT",
                i18n.iValue("web.client.backingBean.visit.message.Entrance"));
        mapEncodingEvents.put("SAL",
                i18n.iValue("web.client.backingBean.visit.message.Exit"));
    }

    // GETTER AND SETTER
    public Boolean getMetaMotiveEnabled() {
        if (metaMotiveEnabled == null) {
            metaMotiveEnabled = false;
            try {
                MetaClient mc = metaClientFacade.findByMetaAndClient(
                        getUserweb().getClient().getClientCod(), META_MOTIVE);
                if (mc != null && mc.getEnabledChr()) {
                    metaMotiveEnabled = true;
                }
            } catch (Exception ex) {
                notifier.signal(
                        getClass(),
                        Action.ERROR,
                        i18n.iValue("web.client.backingBean.visit.messages.VerificationEnablingStateMotiveMetaError"));
            }
        }
        return metaMotiveEnabled;
    }

    public void setMetaMotiveEnabled(Boolean metaMotiveEnabled) {
        this.metaMotiveEnabled = metaMotiveEnabled;
    }

    public Map<String, String> getMapEncodingEvents() {
        return mapEncodingEvents;
    }

    public void setMapEncodingEvents(Map<String, String> mapEncodingEvents) {
        this.mapEncodingEvents = mapEncodingEvents;
    }

    public Map<String, String> getMapClients() {
        return mapClients;
    }

    public void setMapClients(Map<String, String> mapClients) {
        this.mapClients = mapClients;
    }

    public Map<String, String> getMapMotives() {
        if (getMetaMotiveEnabled()) {
            List<MetaData> mds = metaDataFacade.findByClientMetaMember(
                    getUserweb().getClient().getClientCod(), META_MOTIVE, 1L);
            for (MetaData metaData : mds) {
                mapMotives.put(metaData.getMetaDataPK().getCodeChr(),
                        metaData.getValueChr());
            }
        }
        return mapMotives;
    }

    public void setMapMotives(Map<String, String> mapMotives) {
        this.mapMotives = mapMotives;
    }

    @Override
    public void detailMetaDataFromModel() {
        Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        validateIntegrationMethod(codClient);

        mapClients = new HashMap<String, String>();
        if (getServiceValue() != null) {
            for (ServiceValueDetail svd : getDataModelDetail()) {
                try {
                    String key = svd.getColumn2Chr();
                    String value = getValueByIntegrationMethod(codClient, key,
                            "C", META_CLIENT);
                    mapClients.put(
                            key,
                            value == null ? null : value.trim().equals("") ? null : value.trim());
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

        mapMotives = new HashMap<String, String>();
        if (getServiceValue() != null) {
            for (ServiceValueDetail svd : getDataModelDetail()) {
                try {
                    String key = svd.getColumn3Chr();
                    String value = getValueByIntegrationMethod(codClient, key,
                            "M", META_MOTIVE);
                    mapMotives.put(
                            key,
                            value == null ? null : value.trim().equals("") ? null : value.trim());
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
    }

    private String getValueByIntegrationMethod(Long codClient, String code, String discriminator, Long meta) throws MoreThanOneResultException, GenericFacadeException {
        String retValue = null;
        if (metaEnabled) {
            MetaData md = metaDataFacade.findByClientMetaMemberAndCode(
                    codClient, meta, 1L, code);
            if (md != null) {
                retValue = md.getValueChr();
            }
        } else if (wsConexionExists) {
            retValue = tmpWsresultFacade.findData(codClient, getSessionId(),
                    discriminator, code);
            if (retValue == null) {
                retValue = consumeClientWS(codClient, code, discriminator);
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
                    i18n.iValue("web.servicename.Visit"), codClient);
            notifier.signal(getClass(), Action.ERROR,
                    SecurityBean.getInstance().getLoggedInUserClient(), null,
                    getCurrentViewId(), msg, getSessionId(), getIpAddress(), ex);
            wsConexionExists = Boolean.FALSE;
        } catch (WebServiceException we) {
            String msg = MessageFormat.format(
                    i18n.iValue("web.client.backingBean.message.WrongWSInvocation"),
                    i18n.iValue("web.servicename.Visit"), codClient);
            notifier.signal(getClass(), Action.ERROR,
                    SecurityBean.getInstance().getLoggedInUserClient(), null,
                    getCurrentViewId(), msg, getSessionId(), getIpAddress(), we);
            wsConexionExists = Boolean.FALSE;
        }
        return value;
    }

    private void validateIntegrationMethod(Long codClient) {
        if (!integrationMethodValidated) {
            // Valida si existe META-CLIENT
            try {
                MetaClient mc = metaClientFacade.findByMetaAndClient(codClient,
                        META_CLIENT);
                if (mc != null && mc.getEnabledChr()) {
                    metaEnabled = true;
                }
            } catch (Exception ex) {
                Logger.getLogger(VisitBean.class.getName()).log(Level.SEVERE,
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

    @Override
    public String getMessageDescriptionDetailMap(ServiceValueDetail svd) {
        DateFormat sdf = new SimpleDateFormat(ApplicationBean.getInstance().getDefaultOutputDateTimeFormat());
        String messDescrip = svd.getMessage().getUserphone().getNameChr().concat(
                " | ").concat(sdf.format(svd.getMessage().getDateinDat()));

        Long codClient = svd.getMessage().getUserphone().getClient().getClientCod();
        String key = svd.getColumn2Chr();
        if (key != null && key.trim().length() > 0) {
            try {
                String value = getValueByIntegrationMethod(codClient, key, "C", META_CLIENT);
                messDescrip = messDescrip.concat(" | ").concat(key).concat(
                        value != null ? " - ".concat(value) : "");
            } catch (MoreThanOneResultException ex) {
                Logger.getLogger(AbstractServiceBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            } catch (GenericFacadeException ex) {
                Logger.getLogger(AbstractServiceBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }

        return messDescrip;
    }

    @Override
    public String viewDetails() {
        if (showMapOnHeader) {
            mapModel = null;
            getMapModel();
            addClientMarker();
        }
        findAll = false;
        setShowMapOnHeader(false);
        super.viewDetails();
        setShowMapOnHeader(true);
        if (getServiceValue() != null) {

            String durationFormat = null;

            durationFormat = clientParameterValueFacade.findByCode(
                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                    "client.duration.format");

            if (durationFormat == null)
                durationFormat = i18n.iValue("web.client.screen.configuration.field.ExtendedDurationFormatValue");

            // calcular duracion
            Long duration = 0L;
            for (ServiceValueDetail svd : getDataModelDetail()) {
                if (svd.getColumn6Chr() != null) {
                    duration += Long.parseLong(svd.getColumn6Chr());
                    svd.setDurationChr(DateUtil.getPeriodWithFormat(
                            durationFormat, 0L,
                            Long.parseLong(svd.getColumn6Chr())));
                }
            }

            if (duration > 0) {
                totalDuration = DateUtil.getPeriodWithFormat(durationFormat,
                        0L, duration);
            }

        }
        return null;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
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
    public String getCabDetailReportOrderBy() {
        String sortAttributeColumnName = getAttributeColumnName(
                ServiceValue.class, getSortHelper().getField());
        if (getSortHelper().getField().indexOf(".") < 0) {
            sortAttributeColumnName = "svd.".concat(sortAttributeColumnName);
        } else {
            sortAttributeColumnName = "sv.".concat(sortAttributeColumnName);
        }
        return "ORDER BY sv.SERVICEVALUE_COD, ".concat(sortAttributeColumnName).concat(
                getSortHelper().isAscending() ? " ASC" : " DESC");
    }

    @Override
    public String generateCabDetailReport(String reportName, ReportType reportType) {
        /*
         * obtenemos la informacion sobre el usuario que solicita el reporte, y
         * el cliente al que pertenece
         */
        Userweb userweb = SecurityBean.getInstance().getLoggedInUserClient();
        Client client = userweb.getClient();
        String userInformation = MessageFormat.format("{0} ({1})",
                userweb.getNameChr(), client.getNameChr());

        String durationFormat = null;

        durationFormat = clientParameterValueFacade.findByCode(
                SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                "client.duration.format");

        if (durationFormat == null)
            durationFormat = i18n.iValue("web.client.screen.configuration.field.ExtendedDurationFormatValue");

        Map<Object, Object> params = new HashMap<Object, Object>();
        params.put("WHERE", getCabDetailReportWhere());
        params.put("ORDER_BY", getCabDetailReportOrderBy());
        params.put("USER", userInformation);
        params.put("SUBREPORT_DIR", getReportsPath().concat("/"));
        params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
        params.put(JRParameter.REPORT_RESOURCE_BUNDLE, i18n.getResourceBundle());
        params.put("DURATION_FORMAT", durationFormat);

        ClientFile cf = fileFacade.getClientLogo(getUserweb().getClient());
        try {
            if (cf != null) {
                params.put("CLIENT_LOGO",
                        JRImageLoader.loadImage(cf.getFileByt()));
            }
        } catch (JRException ex) {
        }

        Connection conn = SMBaseBean.getDatabaseConnecction();
        JasperReportUtils.respondReport(reportName, params, true, conn,
                reportType);

        return null;
    }

    @Override
    public String generateReportDetail(String reportName, ReportType reportType) {

        /*
         * obtenemos la informacion sobre el usuario que solicita el reporte, y
         * el cliente al que pertenece
         */
        Userweb userweb = SecurityBean.getInstance().getLoggedInUserClient();
        Client client = userweb.getClient();
        String userInformation = MessageFormat.format("{0} ({1})",
                userweb.getNameChr(), client.getNameChr());

        String durationFormat = null;

        durationFormat = clientParameterValueFacade.findByCode(
                SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                "client.duration.format");

        if (durationFormat == null)
            durationFormat = i18n.iValue("web.client.screen.configuration.field.ExtendedDurationFormatValue");

        Map<Object, Object> params = new HashMap<Object, Object>();
        params.put("WHERE", getDetailReportWhere());
        params.put("ORDER_BY", getDetailReportOrderBy());
        params.put("USER", userInformation);
        params.put("SUBREPORT_DIR", getReportsPath().concat("/"));
        params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
        params.put(JRParameter.REPORT_RESOURCE_BUNDLE, i18n.getResourceBundle());
        params.put("DURATION_FORMAT", durationFormat);

        ClientFile cf = fileFacade.getClientLogo(getUserweb().getClient());
        try {
            if (cf != null) {
                params.put("CLIENT_LOGO",
                        JRImageLoader.loadImage(cf.getFileByt()));
            }
        } catch (JRException ex) {
        }
        Connection conn = SMBaseBean.getDatabaseConnecction();
        JasperReportUtils.respondReport(reportName, params, true, conn,
                reportType);
        return null;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String cancelDetail() {
        String result = super.cancelDetail();

        if (showMapOnHeader) {
            mapModel = null;
            getMapModel();
            addClientMarker();
            findAll = false;
        }

        return result;
    }

    private void addOnMap(ServiceValue serviceValue, Map<String, ArrayList<Marker>> positions) {
        List<ServiceValueDetail> svdList = facadeContainer.getServiceValueDetailAPI().findByServiceValue(
                serviceValue);

        if(svdList != null) {
        for (ServiceValueDetail svd : svdList) {
            boolean addMark = false;

            if (eventType == null
                || (eventType != null && svd.getColumn1Chr().compareToIgnoreCase(
                        eventType) == 0)) {
                addMark = true;
            }

            if (addMark
                && (svd.getMessage().getCell() != null || (svd.getMessage().getLatitude() != null && svd.getMessage().getLongitude() != null))) {

                // Message for marker
                String messDescrip = getMessageDescriptionDetailMap(svd);
                Polygon polygonArea = null;
                Marker markerArea = null;
                Double latitude = svd.getMessage().getLatitude();
                Double longitude = svd.getMessage().getLongitude();

                if (latitude != null && longitude != null) {
                    markerArea = getGPSCellAreaMarker(latitude, longitude,
                            messDescrip, "");
                } else {
                    // Obtain latitude, longitude and azimuth from cell
                    latitude = svd.getMessage().getCell().getLatitudeNum().doubleValue();
                    longitude = svd.getMessage().getCell().getLongitudeNum().doubleValue();
                    double azimuth = svd.getMessage().getCell().getAzimuthNum().doubleValue();
                    String siteCell = svd.getMessage().getCell().getSiteChr();

                    if (!siteCell.toUpperCase().endsWith("O")) {
                        // SEGMENTED CELL

                        // Cell polygon
                        polygonArea = getCellAreaPolygon(latitude, longitude,
                                azimuth);

                        // Cell marker
                        markerArea = getCellAreaMarker(latitude, longitude,
                                azimuth, messDescrip, "");
                    } else {
                        // OMNIDIRECTIONAL CELL

                        // Cell polygon
                        polygonArea = getOmniCellAreaPolygon(latitude,
                                longitude);

                        // Cell marker
                        markerArea = getOmniCellAreaMarker(latitude, longitude,
                                messDescrip, "");
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

                }

                // Add the marker
                if (markerArea != null) {
                    // if the marker already exists, do not add, but
                    // instead
                    // add data to existing one.
                    Marker existingMarker = null;
                    for (int i = 0; i < getMapModel().getMarkers().size(); i++) {
                        if (getMapModel().getMarkers().get(i).getLatlng().equals(
                                markerArea.getLatlng())) {
                            existingMarker = getMapModel().getMarkers().get(i);
                            break;
                        }
                    }

                    if (existingMarker == null) {
                        getMapModel().addOverlay(markerArea);
                        // nextMarkerCounter++;
                        if (getLastBounds() != null) {
                            boolean inside = markerArea.getLatlng().getLat() > getLastBounds().getSouthWest().getLat();
                            inside = inside
                                && markerArea.getLatlng().getLat() < getLastBounds().getNorthEast().getLat();
                            inside = inside
                                && markerArea.getLatlng().getLng() > getLastBounds().getSouthWest().getLng();
                            inside = inside
                                && markerArea.getLatlng().getLng() < getLastBounds().getNorthEast().getLng();

                            // if (inside) {
                            // oneInsideBounds = true;
                            // }
                        }

                        if (!positions.containsKey(svd.getMessage().getOriginChr())) {
                            positions.put(svd.getMessage().getOriginChr(),
                                    new ArrayList<Marker>());
                        }
                        positions.get(svd.getMessage().getOriginChr()).add(
                                markerArea);
                    } else {
                        existingMarker.setData(((String) existingMarker.getData()).concat(
                                "<br>").concat(messDescrip));
                    }
                }
            }
        }
        }
    }

    @Override
    public String showHeaderMap() {
        if (getGeolocalizationAllowed()) {
            Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();

            mapModel = new DefaultMapModel();
            // boolean oneInsideBounds = false;
            Map<String, ArrayList<Marker>> positions = new HashMap<String, ArrayList<Marker>>();

            Iterator<ServiceValue> iterator = getDataModelHeader().iterator();
            // int nextMarkerCounter = 1;

            if (!findAll) {
                while (iterator.hasNext()) {

                    ServiceValue serviceValue = iterator.next();
                    if (getSelectedItems().get(
                            serviceValue.getServicevalueCod()) != null && !getSelectedItems().get(
                            serviceValue.getServicevalueCod())) {
                        continue;
                    }

                    addOnMap(serviceValue, positions);

                }
            } else {

                Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
                // List<Classification> classifications =
                // classificationFacade.findByUserweb(userWeb);

                List<Classification> classifications = classificationFacade.findByUserwebWithChilds(getUserweb());
                String where = " WHERE o.enabledChr = true AND o.userphone.client.clientCod = {0} AND o.service.serviceCod = {1}  AND EXISTS ( SELECT u FROM Userphone u , IN (u.classificationList) cl WHERE u.userphoneCod = o.userphone.userphoneCod AND u.client = o.userphone.client AND cl.codClient = o.userphone.client AND cl in (:classifications)) ";
                where = MessageFormat.format(where, clientCod.toString(),
                        String.valueOf(getCodService()));
                where = where.concat(getCabDetailWhereCriteria());

                String orderby = "o.".concat(sortHelper.getField()).concat(
                        sortHelper.isAscending() ? " ASC" : " DESC");

                List<ServiceValue> list = facade.findRange(null, where,
                        orderby, classifications);

                for (ServiceValue sv : list) {
                    addOnMap(sv, positions);
                }

            }
        }
        addClientMarker();
        return null;
    }

    public Boolean getFindAll() {
        return findAll;
    }

    public void setFindAll(Boolean findAll) {
        this.findAll = findAll;
    }

}
