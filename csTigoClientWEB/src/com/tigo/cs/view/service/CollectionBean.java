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
@ManagedBean(name = "collectionBean")
@ViewScoped
public class CollectionBean extends AbstractServiceBean {

    private static final long serialVersionUID = 53649877210219714L;
    public static final int COD_SERVICE = 5;
    public static final Long COD_META_CLIENT = 1L;
    public static final Long COD_META_BANK = 9L;
    //private MetaDataFacade metaDataFacade;
    private Map<String, String> mapClients = new HashMap<String, String>();
    private Map<String, String> mapBanks = new HashMap<String, String>();
    private String URLWS = "";
    private boolean metaClientEnabled = false;
    private Boolean metaBankEnabled = null;
    private boolean wsConexionExists = false;
    private boolean integrationMethodForClientValidated = false;
    private DataModel<ServiceValueDetail> dataModelSubDetail;
    private ServiceValueDetail serviceValueSubDetail;
    private PaginationHelper paginationHelperSubDetail;
    private SortHelper sortHelperSubDetail;
    private Map<String, ServiceColumn> columnsDataSubDetail;
    private String primarySortedFieldSubDetail;
    private boolean primarySortedFieldSubDetailAsc = true;
    private String detailReportWhereCriteria;

    public CollectionBean() {
        setCodService(COD_SERVICE);
        setShowMapOnHeader(true);
        setPdfReportCabDetailName("rep_collection_complete");
        setXlsReportCabDetailName("rep_collection_complete_xls");
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

    public Map<String, String> getMapClients() {
        return mapClients;
    }

    public void setMapClients(Map<String, String> mapClients) {
        this.mapClients = mapClients;
    }

    public Map<String, ServiceColumn> getColumnsDataSubDetail() {
        if (columnsDataSubDetail == null) {
            columnsDataSubDetail = new HashMap<String, ServiceColumn>();
            try {
                List<ServiceColumn> columnsDataList = serviceColumnFacade.getColumnData(Long.valueOf(getCodService()), "S");
                for (ServiceColumn data : columnsDataList) {
                    columnsDataSubDetail.put(data.getColumnChr(), data);
                }
            } catch (GenericFacadeException ex) {
                java.util.logging.Logger.getLogger(AbstractServiceBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return columnsDataSubDetail;
    }

    @Override
    public void headerMetaDataFromModel() {
        Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        validateIntegrationMethodForClient(codClient);

        mapClients = new HashMap<String, String>();
        if (getDataModelHeader() != null
            && getDataModelHeader().getRowCount() > 0) {
            for (ServiceValue sv : getDataModelHeader()) {
                try {
                    String key = sv.getColumn1Chr();
                    String value = getValueByIntegrationMethod(codClient, COD_META_CLIENT, key);
                    mapClients.put(key, value == null ? null : value.trim().equals("") ? null : value.trim());
                } catch (Exception ex) {
                    notifier.signal(getClass(), Action.ERROR, SecurityBean.getInstance().getLoggedInUserClient(), null, getCurrentViewId(), i18n.iValue("web.client.backingBean.message.ClientsTempTableError"), getSessionId(), getIpAddress(), ex);
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

    @Override
    public PaginationHelper getPaginationHelperDetail() {
        if (paginationHelperDetail == null) {
            int pageSize = getRowQuantSelected().length() > 0 ? Integer.valueOf(getRowQuantSelected()).intValue() : 0;

            paginationHelperDetail = new PaginationHelper(pageSize) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                @Override
                public int getItemsCount() {
                    return facadeDetail.count(" WHERE o.serviceValue.enabledChr = true AND o.enabledChr = true AND o.serviceValue.servicevalueCod = ".concat(getServiceValue().getServicevalueCod().toString()).concat(" AND o.column1Chr = 'INVOICE'"));
                }

                @Override
                public DataModel createPageDataModel() {
                    String orderby = "o.".concat(sortHelperDetail.getField()).concat(sortHelperDetail.isAscending() ? " ASC" : " DESC");
                    return new ListDataModelViewCsTigo(
                            facadeDetail.findRange(new int[] { getPageFirstItem(), getPageFirstItem()
                                + getPageSize() }, " WHERE o.serviceValue.enabledChr = true AND o.enabledChr = true AND o.serviceValue.servicevalueCod = ".concat(getServiceValue().getServicevalueCod().toString()).concat(" AND o.column1Chr = 'INVOICE'"), orderby));
                }
            };
        }

        return paginationHelperDetail;
    }
    
    public PaginationHelper getPaginationHelperSubDetail() {
        if (paginationHelperSubDetail == null) {
            int pageSize = getRowQuantSelected().length() > 0 ? Integer.valueOf(getRowQuantSelected()).intValue() : 0;

            paginationHelperSubDetail = new PaginationHelper(pageSize) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                @Override
                public int getItemsCount() {
                    return facadeDetail.count(" WHERE o.serviceValue.enabledChr = true AND o.enabledChr = true AND o.serviceValue.servicevalueCod = ".concat(getServiceValue().getServicevalueCod().toString()).concat(" AND o.column1Chr = 'PAYMENT'"));
                }

                @Override
                public DataModel createPageDataModel() {
                    String orderby = "o.".concat(sortHelperSubDetail.getField()).concat(sortHelperSubDetail.isAscending() ? " ASC" : " DESC");
                    return new ListDataModelViewCsTigo(
                            facadeDetail.findRange(new int[] { getPageFirstItem(), getPageFirstItem()
                                + getPageSize() }, " WHERE o.serviceValue.enabledChr = true AND o.enabledChr = true AND o.serviceValue.servicevalueCod = ".concat(getServiceValue().getServicevalueCod().toString()).concat(" AND o.column1Chr = 'PAYMENT'"), orderby));
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
                throw new PrimarySortedFieldNotFoundException(
                        ServiceValue.class);
            }
        }
        return primarySortedFieldSubDetail;
    }

    public Boolean getMetaBankEnabled() {
        if (metaBankEnabled == null) {
            metaBankEnabled = false;
            try {
                MetaClient mc = metaClientFacade.findByMetaAndClient(getUserweb().getClient().getClientCod(), COD_META_BANK);
                if (mc != null && mc.getEnabledChr()) {
                    metaBankEnabled = true;
                }
            } catch (Exception ex) {
                notifier.signal(getClass(), Action.ERROR, i18n.iValue("web.client.backingBean.collection.messages.VerificationEnablingStateBankMetaError"));
            }
        }
        return metaBankEnabled;
    }

    public void setMetaBankEnabled(Boolean metaBankEnabled) {
        this.metaBankEnabled = metaBankEnabled;
    }

    public void setMapBanks(Map<String, String> mapBanks) {
        this.mapBanks = mapBanks;
    }

    @Override
    public void detailMetaDataFromModel() {
    	super.detailMetaDataFromModel();
    }
    
    public Map<String, String> getMapBanks() {
        if (getMetaBankEnabled()) {
            List<MetaData> mds = metaDataFacade.findByClientMetaMember(getUserweb().getClient().getClientCod(), COD_META_BANK, 1L);
            for (MetaData metaData : mds) {
                mapBanks.put(metaData.getMetaDataPK().getCodeChr(), metaData.getValueChr());
            }
        }
        return mapBanks;
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
            java.util.logging.Logger.getLogger(AbstractServiceBean.class.getName()).log(Level.SEVERE, i18n.iValue("web.client.backingBean.message.Error")
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
        boolean enableStorage = (metaClientEnabled && codMeta.equals(1L));
        if (enableStorage) {
            MetaData md = metaDataFacade.findByClientMetaMemberAndCode(codClient, codMeta, 1L, code);
            if (md != null) {
                retValue = md.getValueChr();
            }
        } else if (wsConexionExists) {
            retValue = tmpWsresultFacade.findData(codClient, getSessionId(), "C", code);
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
            String msg = MessageFormat.format(i18n.iValue("web.client.backingBean.message.WrongWSInvocation"), i18n.iValue("web.servicename.Colections"), codClient);
            notifier.signal(getClass(), Action.ERROR, SecurityBean.getInstance().getLoggedInUserClient(), null, getCurrentViewId(), msg, getSessionId(), getIpAddress(), ex);
            wsConexionExists = Boolean.FALSE;
        } catch (WebServiceException we) {
            String msg = MessageFormat.format(i18n.iValue("web.client.backingBean.message.WrongWSInvocation"), i18n.iValue("web.servicename.Colections"), codClient);
            notifier.signal(getClass(), Action.ERROR, SecurityBean.getInstance().getLoggedInUserClient(), null, getCurrentViewId(), msg, getSessionId(), getIpAddress(), we);
            wsConexionExists = Boolean.FALSE;
        }
        return value;
    }

    private void validateIntegrationMethodForClient(Long codClient) {
        if (!integrationMethodForClientValidated) {
            // Valida si existe META-CLIENT
            try {
                MetaClient mc = metaClientFacade.findByMetaAndClient(codClient, COD_META_CLIENT);
                if (mc != null && mc.getEnabledChr()) {
                    metaClientEnabled = true;
                }
            } catch (Exception ex) {
                Logger.getLogger(VisitBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            // Nueva validación
            
                String enabled = clientParameterValueFacade.findByCode(getUserweb().getClient().getClientCod(), "client.ws.enabled");
                if (enabled != null && enabled.equals("1")) {
                    String url = clientParameterValueFacade.findByCode(getUserweb().getClient().getClientCod(), "client.ws.url");
                    if (url != null && !url.isEmpty()) {
                        wsConexionExists = true;
                        URLWS = url;
                    }
                }
            

            integrationMethodForClientValidated = true;
        }
    }

    @Override
    public String getCabDetailReportOrderBy() {
        return " ORDER BY SV.SERVICEVALUE_COD ASC ";
    }

    @Override
    public String getDetailWhereCriteria() {
        return "";
    }

    @Override
    public String getDetailReportWhereCriteria() {
        return detailReportWhereCriteria;
    }

    public String viewPDFDetailInvoice() {
        setPdfReportDetailName("rep_collection_invoices");
        detailReportWhereCriteria = " AND sv.enabled_chr = '1' AND svd.enabled_chr = '1' AND svd.COLUMN1_CHR = 'INVOICE'";
        return super.viewPDFDetail();
    }

    public String viewXLSDetailInvoice() {
        detailReportWhereCriteria = " AND sv.enabled_chr = '1' AND svd.enabled_chr = '1' AND svd.COLUMN1_CHR = 'INVOICE'";
        setXlsReportDetailName("rep_collection_invoices_xls");
        return super.viewXLSDetail();
    }

    public String viewPDFDetailPayment() {
        detailReportWhereCriteria = " AND sv.enabled_chr = '1' AND svd.enabled_chr = '1' AND svd.COLUMN1_CHR = 'PAYMENT'";
        setPdfReportDetailName("rep_collection_payments");
        return super.viewPDFDetail();
    }

    public String viewXLSDetailPayment() {
        detailReportWhereCriteria = " AND sv.enabled_chr = '1' AND svd.enabled_chr = '1' AND svd.COLUMN1_CHR = 'PAYMENT'";
        setXlsReportDetailName("rep_collection_payments_xls");
        return super.viewXLSDetail();
    }

}
