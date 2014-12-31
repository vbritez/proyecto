package com.tigo.cs.view.service;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.xml.ws.WebServiceException;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.PrimarySortedFieldNotFoundException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.util.ListDataModelViewCsTigo;
import com.tigo.cs.commons.util.PaginationHelper;
import com.tigo.cs.commons.util.SortHelper;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.ServiceColumn;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.TmpWsresult;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.ws.client.ClientWS;
import com.tigo.cs.ws.client.ClientWSService;
import com.tigo.cs.ws.client.Customer;
import com.tigo.cs.ws.client.Product;

/**
 * 
 * @author Javier Kovacs
 * @version $Id$
 */
@ManagedBean(name = "arpBean")
@ViewScoped
public class ArpBean extends AbstractServiceBean {

    private static final long serialVersionUID = 1473080922677314957L;
    public static final int COD_SERVICE = 9;
    public static final Long COD_META_ARP = 6L;
    private Map<String, String> mapTipoFactura = new HashMap<String, String>();
    private String URLWS = "";
    private boolean metaClientEnabled = false;
    private boolean wsConexionExists = false;
    private boolean integrationMethodForClientValidated = false;
    private DataModel<ServiceValueDetail> dataModelSubDetail;
    private ServiceValueDetail serviceValueSubDetail;
    private PaginationHelper paginationHelperSubDetail;
    private SortHelper sortHelperSubDetail;
    private Map<String, ServiceColumn> columnsDataSubDetail;
    private String primarySortedFieldSubDetail;
    private boolean primarySortedFieldSubDetailAsc = true;

    public ArpBean() {
        setCodService(COD_SERVICE);
        setShowMapOnHeader(true);
        setPdfReportCabDetailName("rep_arp_cabdetail");
        setXlsReportCabDetailName("rep_arp_cabdetail_xls");
        setPdfReportDetailName("rep_arp_cabdetail");
        setXlsReportDetailName("rep_arp_cabdetail_xls");
        getSortHelper().setField("servicevalueCod");
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

    public Map<String, String> getMapTipoFactura() {
        return mapTipoFactura;
    }

    public void setMapTipoFactura(Map<String, String> mapTipoFacturas) {
        this.mapTipoFactura = mapTipoFacturas;
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

    @Override
    public void headerMetaDataFromModel() {
        Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        validateIntegrationMethodForClient(codClient);

        mapTipoFactura = new HashMap<String, String>();
        if (getDataModelHeader() != null
            && getDataModelHeader().getRowCount() > 0) {
            for (ServiceValue sv : getDataModelHeader()) {
                try {
                    String key = sv.getColumn2Chr();
                    String value = getValueByIntegrationMethod(codClient,
                            COD_META_ARP, key);
                    mapTipoFactura.put(
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

    public PaginationHelper getPaginationHelperSubDetail() {
        if (paginationHelperSubDetail == null) {
            int pageSize = getRowQuantSelected().length() > 0 ? Integer.valueOf(
                    getRowQuantSelected()).intValue() : 0;

            paginationHelperSubDetail = new PaginationHelper(pageSize) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                @Override
                public int getItemsCount() {
                    return facadeDetail.count(" WHERE o.enabledChr = true AND o.serviceValue.enabledChr = true AND o.serviceValue.servicevalueCod = ".concat(getServiceValue().getServicevalueCod().toString()).concat(" AND o.column1Chr = 'M'"));
                }

                @Override
                public DataModel createPageDataModel() {
                    String orderby = "o.".concat(sortHelperSubDetail.getField()).concat(sortHelperSubDetail.isAscending() ? " ASC" : " DESC");
                    return new ListDataModelViewCsTigo(
                            facadeDetail.findRange(new int[] { getPageFirstItem(), getPageFirstItem()
                                + getPageSize() }, " WHERE o.enabledChr = true AND o.serviceValue.enabledChr = true AND o.serviceValue.servicevalueCod = ".concat(getServiceValue().getServicevalueCod().toString()).concat(" AND o.column1Chr = 'M'"), orderby));
                }
            };
        }

        return paginationHelperSubDetail;
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
    public String viewDetails() {
        String retVal = super.viewDetails();
        viewSubDetails();
        return retVal;
    }

    public String viewSubDetails() {
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

        return null;
    }

    @Override
    public String cancelDetail() {
        cancelSubDetail();
        return super.cancelDetail();
    }

    public String cancelSubDetail() {
        serviceValueSubDetail = null;
        dataModelSubDetail = null;
        return null;
    }

    private String getValueByIntegrationMethod(Long codClient, Long codMeta, String code) throws MoreThanOneResultException, GenericFacadeException {
        String retValue = null;
        boolean enableStorage = (metaClientEnabled && codMeta.equals(6L));
        if (enableStorage) {
            MetaData md = metaDataFacade.findByClientMetaMemberAndCode(
                    codClient, codMeta, 1L, code);
            if (md != null) {
                retValue = md.getValueChr();
            }
        } else if (wsConexionExists) {
            retValue = tmpWsresultFacade.findData(codClient, getSessionId(),
                    "ARP_CV", code);
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
                    i18n.iValue("web.servicename.ARP"), codClient);
            notifier.signal(getClass(), Action.ERROR,
                    SecurityBean.getInstance().getLoggedInUserClient(), null,
                    getCurrentViewId(), msg, getSessionId(), getIpAddress(), ex);
            wsConexionExists = Boolean.FALSE;
        } catch (WebServiceException we) {
            String msg = MessageFormat.format(
                    i18n.iValue("web.client.backingBean.message.WrongWSInvocation"),
                    i18n.iValue("web.servicename.ARP"), codClient);
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
                        COD_META_ARP);
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

    // ABSTRACT METHOD IMPLEMENTATIONS
    @Override
    public String getDetailWhereCriteria() {
        return " AND o.column1Chr = 'GC'";
    }

    @Override
    public String getDetailReportWhereCriteria() {
        return "";
    }

}
