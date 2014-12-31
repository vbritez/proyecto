package com.tigo.cs.view.service;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.xml.ws.WebServiceException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.util.JRImageLoader;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.PrimarySortedFieldNotFoundException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.report.JasperReportUtils;
import com.tigo.cs.commons.report.ReportType;
import com.tigo.cs.commons.util.ListDataModelViewCsTigo;
import com.tigo.cs.commons.util.PaginationHelper;
import com.tigo.cs.commons.util.SortHelper;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.ClientFile;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.ServiceColumn;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.TmpWsresult;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.ApplicationBean;
import com.tigo.cs.ws.client.ClientWS;
import com.tigo.cs.ws.client.ClientWSService;
import com.tigo.cs.ws.client.Customer;
import com.tigo.cs.ws.client.Product;

/**
 * 
 * @author Javier Kovacs
 * @version $Id$
 */
@ManagedBean(name = "visitOrderBean")
@ViewScoped
public class VisitOrderBean extends AbstractServiceBean {

    private static final long serialVersionUID = 929679112985166522L;
    public static final int COD_SERVICE = 3;
    public static final Long COD_META_CLIENT = 1L;
    public static final Long COD_META_PRODUCT = 2L;
    public static final Long COD_META_MOTIVE = 3L;

    private DataModel<ServiceValueDetail> dataModelSubDetail;
    private ServiceValueDetail serviceValueSubDetail;
    private PaginationHelper paginationHelperSubDetail;
    private SortHelper sortHelperSubDetail;
    private Map<String, ServiceColumn> columnsDataSubDetail;
    private Map<String, ServiceColumn> columnsDataSubDetailHeader;
    private String primarySortedFieldSubDetail;
    private boolean primarySortedFieldSubDetailAsc = true;
    private String pdfReportSubDetailName;
    private String xlsReportSubDetailName;
    private Map<String, String> mapClients = new HashMap<String, String>();
    private Map<String, String> mapProducts = new HashMap<String, String>();
    private Map<String, String> mapMotives = new HashMap<String, String>();
    private Map<String, String> mapEncodingEvents = new HashMap<String, String>();
    private String URLWS = "";
    private boolean metaClientEnabled = false;
    private boolean metaProductEnabled = false;
    private Boolean metaMotiveEnabled = null;
    private boolean wsConexionExists = false;
    private boolean integrationMethodForClientValidated = false;
    private boolean integrationMethodForProductValidated = false;

    public VisitOrderBean() {
        setCodService(COD_SERVICE);
        setShowMapOnDetail(true);

        setPdfReportCabDetailName("rep_visitorder_cabdetail_subdetail");
        setXlsReportCabDetailName("rep_visitorder_cabdetail_subdetail_xls");
        setPdfReportDetailName("rep_visitorder_detail");
        setXlsReportDetailName("rep_visitorder_detail_xls");
        setPdfReportSubDetailName("rep_visitorder_subdetail");
        setXlsReportSubDetailName("rep_visitorder_subdetail_xls");
    }

    @PostConstruct
    public void init() {
        mapEncodingEvents.put(
                "ENTSAL",
                i18n.iValue("web.client.backingBean.visitorder.message.QuickVisit"));
        mapEncodingEvents.put(
                "ENT",
                i18n.iValue("web.client.backingBean.visitorder.message.Entrance"));
        mapEncodingEvents.put("SAL",
                i18n.iValue("web.client.backingBean.visitorder.message.Exit"));
    }

    public Boolean getMetaMotiveEnabled() {
        if (metaMotiveEnabled == null) {
            metaMotiveEnabled = false;
            try {
                MetaClient mc = metaClientFacade.findByMetaAndClient(
                        getUserweb().getClient().getClientCod(),
                        COD_META_MOTIVE);
                if (mc != null && mc.getEnabledChr()) {
                    metaMotiveEnabled = true;
                }
            } catch (Exception ex) {
                notifier.signal(
                        getClass(),
                        Action.ERROR,
                        i18n.iValue("web.client.backingBean.visitorder.messages.VerificationEnablingStateMotiveMetaError"));
            }
        }
        return metaMotiveEnabled;
    }

    public void setMetaMotiveEnabled(Boolean metaMotiveEnabled) {
        this.metaMotiveEnabled = metaMotiveEnabled;
    }

    public Map<String, String> getMapClients() {
        return mapClients;
    }

    public void setMapClients(Map<String, String> mapClients) {
        this.mapClients = mapClients;
    }

    public Map<String, String> getMapProducts() {
        return mapProducts;
    }

    public void setMapProducts(Map<String, String> mapProducts) {
        this.mapProducts = mapProducts;
    }

    public Map<String, String> getMapMotives() {
        if (getMetaMotiveEnabled()) {
            List<MetaData> mds = metaDataFacade.findByClientMetaMember(
                    getUserweb().getClient().getClientCod(), COD_META_MOTIVE,
                    1L);
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

    public Map<String, String> getMapEncodingEvents() {
        return mapEncodingEvents;
    }

    public void setMapEncodingEvents(Map<String, String> mapEncodingEvents) {
        this.mapEncodingEvents = mapEncodingEvents;
    }

    public String getPdfReportSubDetailName() {
        return pdfReportSubDetailName;
    }

    public final void setPdfReportSubDetailName(String pdfReportDetailName) {
        this.pdfReportSubDetailName = pdfReportDetailName;
    }

    public String getXlsReportSubDetailName() {
        return xlsReportSubDetailName;
    }

    public final void setXlsReportSubDetailName(String xlsReportDetailName) {
        this.xlsReportSubDetailName = xlsReportDetailName;
    }

    public DataModel<ServiceValueDetail> getDataModelSubDetail() {
        return dataModelSubDetail;
    }

    public void setDataModelSubDetail(DataModel<ServiceValueDetail> dataModelSubDetail) {
        this.dataModelSubDetail = dataModelSubDetail;
    }

    public ServiceValueDetail getServiceValueSubDetail() {
        return serviceValueSubDetail;
    }

    public void setServiceValueSubDetail(ServiceValueDetail serviceValueSubDetail) {
        this.serviceValueSubDetail = serviceValueSubDetail;
    }

    public SortHelper getSortHelperSubDetail() {
        return sortHelperSubDetail;
    }

    public void setSortHelperSubDetail(SortHelper sortHelperSubDetail) {
        this.sortHelperSubDetail = sortHelperSubDetail;
    }

    public Map<String, ServiceColumn> getColumnsDataSubDetail() {
        if (columnsDataSubDetail == null) {
            columnsDataSubDetail = new HashMap<String, ServiceColumn>();
            try {
                List<ServiceColumn> columnsDataList = serviceColumnFacade.getColumnData(
                        Long.valueOf(getCodService()), "S");
                for (ServiceColumn data : columnsDataList) {
                    columnsDataSubDetail.put(data.getColumnChr(), data);
                }
            } catch (GenericFacadeException ex) {
                java.util.logging.Logger.getLogger(
                        AbstractServiceBean.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
        return columnsDataSubDetail;
    }

    public Map<String, ServiceColumn> getColumnsDataSubDetailHeader() {
        if (columnsDataSubDetailHeader == null) {
            columnsDataSubDetailHeader = new HashMap<String, ServiceColumn>();
            try {
                List<ServiceColumn> columnsDataList = serviceColumnFacade.getColumnData(
                        Long.valueOf(getCodService()), "H");
                for (ServiceColumn data : columnsDataList) {
                    columnsDataSubDetailHeader.put(data.getColumnChr(), data);
                }

            } catch (GenericFacadeException ex) {
                java.util.logging.Logger.getLogger(
                        AbstractServiceBean.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
        return columnsDataSubDetailHeader;
    }

    public PaginationHelper getPaginationHelperSubDetail() {
        if (paginationHelperSubDetail == null) {
            int pageSize = Integer.valueOf(
                    ApplicationBean.getInstance().getDefaultServicePaginationPageSize()).intValue();

            paginationHelperSubDetail = new PaginationHelper(pageSize) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                @Override
                public int getItemsCount() {
                    return facadeDetail.count(" WHERE o.serviceValue.enabledChr = true AND o.serviceValue.column5Chr = '".concat(
                            serviceValueSubDetail.getServicevaluedetailCod().toString()).concat(
                            "'"));
                }

                @Override
                public DataModel createPageDataModel() {
                    String orderby = "o.".concat(sortHelperSubDetail.getField()).concat(
                            sortHelperSubDetail.isAscending() ? " ASC" : " DESC");
                    return new ListDataModelViewCsTigo(facadeDetail.findRange(
                            new int[] { getPageFirstItem(), getPageFirstItem()
                                + getPageSize() },
                            " WHERE o.serviceValue.enabledChr = true AND o.serviceValue.column5Chr = '".concat(
                                    serviceValueSubDetail.getServicevaluedetailCod().toString()).concat(
                                    "'"), orderby));
                }
            };
        }

        return paginationHelperSubDetail;
    }

    public String applySortSubDetail() {
        dataModelSubDetail = getPaginationHelperSubDetail().createPageDataModel();
        return null;
    }

    public String previousSubDetailPage() {
        getPaginationHelperSubDetail().previousPage();
        dataModelSubDetail = getPaginationHelperSubDetail().createPageDataModel();
        return null;
    }

    public String nextSubDetailPage() {
        getPaginationHelperSubDetail().nextPage();
        dataModelSubDetail = getPaginationHelperSubDetail().createPageDataModel();
        return null;
    }

    @Override
    public void detailMetaDataFromModel() {
        Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        validateIntegrationMethodForClient(codClient);

        mapClients = new HashMap<String, String>();
        if (getServiceValue() != null) {
            for (ServiceValueDetail svd : getDataModelDetail()) {
                try {
                    String key = svd.getColumn2Chr();
                    String value = getValueByIntegrationMethod(codClient,
                            COD_META_CLIENT, key);
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
                            i18n.iValue("web.client.backingBean.message.ClientsTempTableError"),
                            getSessionId(), getIpAddress(), ex);
                }
            }
        }
    }

    public String viewSubDetails() {
        serviceValueSubDetail = null;
        Iterator<ServiceValueDetail> iterator = getDataModelDetail().iterator();
        while (iterator.hasNext()) {
            ServiceValueDetail currServiceValueDetail = iterator.next();
            if (getSelectedDetailItems().containsKey(
                    currServiceValueDetail.getServicevaluedetailCod())
                && getSelectedDetailItems().get(
                        currServiceValueDetail.getServicevaluedetailCod())) {
                if (serviceValueSubDetail == null) {
                    if (currServiceValueDetail.getColumn1Chr().equals("SAL")) {
                        try {
                            serviceValueSubDetail = facadeDetail.getSubDetailIncome(
                                    currServiceValueDetail.getServiceValue().getServicevalueCod(),
                                    currServiceValueDetail.getRecorddateDat());// new
                                                                               // SimpleDateFormat("dd-MMM-yy HH:mm").format(currServiceValueDetail.getRecorddateDat()));
                        } catch (GenericFacadeException ex) {
                            Logger.getLogger(VisitOrderBean.class.getName()).log(
                                    Level.SEVERE, null, ex);
                        }
                    } else {
                        serviceValueSubDetail = facadeDetail.find(currServiceValueDetail.getServicevaluedetailCod());
                    }
                } else {
                    serviceValueSubDetail = null;
                    setWarnMessage(i18n.iValue("web.client.backingBean.visitorder.messages.MustSelectJustOne"));
                    return null;
                }
            }
        }
        if (serviceValueSubDetail == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.visitorder.messages.MustSelectOne"));
            return null;
        }

        // Initialize sort with default values
        sortHelperSubDetail = new SortHelper();
        try {
            sortHelperSubDetail.setField(getPrimarySortedFieldSubDetail());
            sortHelperSubDetail.setAscending(primarySortedFieldSubDetailAsc);
        } catch (PrimarySortedFieldNotFoundException ex) {
            java.util.logging.Logger.getLogger(
                    AbstractServiceBean.class.getName()).log(
                    Level.SEVERE,
                    i18n.iValue("web.client.backingBean.message.Error")
                        + ex.getMessage(), ex);
        }

        paginationHelperSubDetail = null;
        dataModelSubDetail = getPaginationHelperSubDetail().createPageDataModel();

        // WS del Cliente
        Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        validateIntegrationMethodForProduct(codClient);

        mapProducts = new HashMap<String, String>();
        if (getServiceValueSubDetail() != null) {
            for (ServiceValueDetail svd : getDataModelSubDetail()) {
                try {
                    String key = svd.getColumn1Chr();
                    String value = getValueByIntegrationMethod(codClient,
                            COD_META_PRODUCT, key);
                    mapProducts.put(
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

        return null;
    }

    private String getValueByIntegrationMethod(Long codClient, Long codMeta, String code) throws MoreThanOneResultException, GenericFacadeException {
        String retValue = null;
        boolean enableStorage = (metaClientEnabled && codMeta.equals(1L))
            || (metaProductEnabled && codMeta.equals(2L));
        if (enableStorage) {
            MetaData md = metaDataFacade.findByClientMetaMemberAndCode(
                    codClient, codMeta, 1L, code);
            if (md != null) {
                retValue = md.getValueChr();
            }
        } else if (wsConexionExists) {
            String discriminator = codMeta.equals(1L) ? "C" : "P";
            retValue = tmpWsresultFacade.findData(codClient, getSessionId(),
                    discriminator, code);
            if (retValue == null) {
                retValue = consumeWS(codClient, codMeta, code);
            }
        }
        return retValue;
    }

    private String consumeWS(Long codClient, Long codMeta, String key) {
        String value = null;
        try {
            URL url = new URL(URLWS);
            ClientWSService service = new ClientWSService(url);
            ClientWS port = service.getClientWSPort();
            String discriminator = "C";
            if (codMeta.equals(1L)) {
                Customer customer = port.getCustomerByCode(key);
                value = customer != null && customer.getName() != null ? customer.getName().trim() : null;
            } else {
                Product product = port.getProductByCode(key);
                value = product != null && product.getName() != null ? product.getName() : null;
                discriminator = "P";
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
                    i18n.iValue("web.servicename.VisitOrder"), codClient);
            notifier.signal(getClass(), Action.ERROR,
                    SecurityBean.getInstance().getLoggedInUserClient(), null,
                    getCurrentViewId(), msg, getSessionId(), getIpAddress(), ex);
            wsConexionExists = Boolean.FALSE;
        } catch (WebServiceException we) {
            String msg = MessageFormat.format(
                    i18n.iValue("web.client.backingBean.message.WrongWSInvocation"),
                    i18n.iValue("web.servicename.VisitOrder"), codClient);
            notifier.signal(getClass(), Action.ERROR,
                    SecurityBean.getInstance().getLoggedInUserClient(), null,
                    getCurrentViewId(), msg, getSessionId(), getIpAddress(), we);
            wsConexionExists = Boolean.FALSE;
        }
        return value;
    }

    private void validateIntegrationMethodForClient(Long codClient) {
        if (!integrationMethodForClientValidated) {
            // Valida si existe META-CLIENT
            try {
                MetaClient mc = metaClientFacade.findByMetaAndClient(codClient,
                        COD_META_CLIENT);
                if (mc != null && mc.getEnabledChr()) {
                    metaClientEnabled = true;
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

            integrationMethodForClientValidated = true;
        }
    }

    private void validateIntegrationMethodForProduct(Long codClient) {
        if (!integrationMethodForProductValidated) {
            // Valida si existe META-CLIENT
            try {
                MetaClient mc = metaClientFacade.findByMetaAndClient(codClient,
                        COD_META_PRODUCT);
                if (mc != null && mc.getEnabledChr()) {
                    metaProductEnabled = true;
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

            integrationMethodForProductValidated = true;
        }
    }

    public String cancelSubDetail() {
        serviceValueSubDetail = null;
        dataModelSubDetail = null;
        return null;
    }

    public String getPrimarySortedFieldSubDetail() throws PrimarySortedFieldNotFoundException {
        if (primarySortedFieldSubDetail == null) {
            Field[] fieds = ServiceValueDetail.class.getDeclaredFields();
            for (Field field : fieds) {
                PrimarySortedField annotation = field.getAnnotation(PrimarySortedField.class);
                if (annotation != null) {
                    primarySortedFieldSubDetail = field.getName();
                    primarySortedFieldSubDetailAsc = annotation.ascending();
                    break;
                }
            }
            if (primarySortedFieldSubDetail == null) {
                throw new PrimarySortedFieldNotFoundException(ServiceValue.class);
            }
        }
        return primarySortedFieldSubDetail;
    }

    @Override
    /**
     * filtro utilizado para la consulta JPQL
     */
    public String getCabDetailWhereCriteria() {

        String where = super.getCabDetailWhereCriteria();
        where = where.concat(" AND o.column1Chr = 'APERTURA'");
        return where;
    }

    @Override
    /**
     * filtro utilizado para la consulta SQL
     */
    public String getCabDetailReportWhereCriteria() {

        String where = super.getCabDetailReportWhereCriteria();
        where = where.concat(" AND sv.COLUMN1_CHR = 'APERTURA'");
        return where;
    }

    @Override
    public String getCabDetailReportWhere() {
        // String where =
        // super.getCabDetailReportWhere().concat(" and sv.column1_chr='APERTURA' and vpdet.COLUMN1_CHR = 'ENT'");
        String where = super.getCabDetailReportWhere().concat(
                " and vpdet.ENABLED_CHR = '1' and vpdet.COLUMN1_CHR = 'ENT'");
        return where;
    }

    @Override
    public String getCabDetailReportOrderBy() {
        return " ORDER BY sv.servicevalue_cod, vpdet.SERVICEVALUEDETAIL_COD, ped.SERVICEVALUEDETAIL_COD ASC";
    }

    @Override
    public String getDetailWhereCriteria() {
        return "";
    }

    @Override
    public String getDetailReportWhereCriteria() {
        return "";
    }

    public String generateSubDetailReport(String reportName, ReportType reportType) {
        // Prepare orderby clause
        String sortAttributeColumnName = getAttributeColumnName(
                ServiceValueDetail.class, getSortHelperSubDetail().getField());
        if (getSortHelperSubDetail().getField().indexOf(".") < 0) {
            sortAttributeColumnName = "o.".concat(sortAttributeColumnName);
        } else {
            sortAttributeColumnName = "o1.".concat(sortAttributeColumnName);
        }
        String orderBy = "ORDER BY ".concat(sortAttributeColumnName).concat(
                getSortHelperSubDetail().isAscending() ? " ASC" : " DESC");

        // Put params into params map
        Map<Object, Object> params = new HashMap<Object, Object>();

        params.put(
                "WHERE",
                " AND o1.column5_chr = '".concat(
                        serviceValueSubDetail.getServicevaluedetailCod().toString()).concat(
                        "'"));
        params.put("ORDER_BY", orderBy);
        params.put(
                "USER",
                SecurityBean.getInstance().getLoggedInUserClient().getNameChr().concat(
                        " (").concat(
                        SecurityBean.getInstance().getLoggedInUserClient().getClient().getNameChr()).concat(
                        ")"));
        params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
        params.put(JRParameter.REPORT_RESOURCE_BUNDLE, i18n.getResourceBundle());

        ClientFile cf = fileFacade.getClientLogo(getUserweb().getClient());
        try {
            if (cf != null) {
                params.put("CLIENT_LOGO",
                        JRImageLoader.loadImage(cf.getFileByt()));
            }
        } catch (JRException ex) {
            Logger.getLogger(VisitBean.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
        Connection conn = SMBaseBean.getDatabaseConnecction();
        JasperReportUtils.respondReport(reportName, params, true, conn,
                reportType);
        return null;
    }

    public String viewPDFSubDetail() {
        signalReport(ReportType.PDF);
        return generateSubDetailReport(getPdfReportSubDetailName(),
                ReportType.PDF);
    }

    public String viewXLSSubDetail() {
        signalReport(ReportType.PDF);
        return generateSubDetailReport(getXlsReportSubDetailName(),
                ReportType.XLS);
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
                String value = getValueByIntegrationMethod(codClient,
                        COD_META_CLIENT, key);
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
}
