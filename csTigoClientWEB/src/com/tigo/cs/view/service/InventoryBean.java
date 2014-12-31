package com.tigo.cs.view.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.TmpWsresult;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.ws.client.ClientWS;
import com.tigo.cs.ws.client.ClientWSService;
import com.tigo.cs.ws.client.Customer;
import com.tigo.cs.ws.client.Motive;

/**
 * 
 * @author Javier Kovacs
 * @version $Id$
 */
@ManagedBean(name = "inventoryBean")
@ViewScoped
public class InventoryBean extends AbstractServiceBean {

    private static final long serialVersionUID = 2671989662795451304L;
    public static final int COD_SERVICE = 8;
    public static final Long META_CLIENT = 1L;
    public static final Long META_DISTRIBUTOR = 5L;

    private Map<String, String> mapClients = new HashMap<String, String>();
    private Map<String, String> mapDistributor = new HashMap<String, String>();
    private String URLWS = "";
    private boolean metaEnabled = false;
    private Boolean metaDistributorEnabled = null;
    private boolean wsConexionExists = false;
    private boolean integrationMethodValidated = false;

    public InventoryBean() {
        setCodService(COD_SERVICE);
        setShowMapOnDetail(true);
        setPdfReportDetailName("rep_inventory_detail");
        setXlsReportDetailName("rep_inventory_detail_xls");
        setPdfReportCabDetailName("rep_inventory_cabdetail");
        setXlsReportCabDetailName("rep_inventory_cabdetail_xls");
    }

    // GETTER AND SETTER
    public Boolean getMetaDistributorEnabled() {
        if (metaDistributorEnabled == null) {
            metaDistributorEnabled = false;
            try {
                MetaClient mc = metaClientFacade.findByMetaAndClient(getUserweb().getClient().getClientCod(), META_DISTRIBUTOR);
                if (mc != null && mc.getEnabledChr()) {
                    metaDistributorEnabled = true;
                }
            } catch (Exception ex) {
                notifier.signal(getClass(), Action.ERROR, i18n.iValue("web.client.backingBean.inventory.messages.VerificationEnablingStateDealerMetaError"));
            }
        }
        return metaDistributorEnabled;
    }

    public void setMetaDistributorEnabled(Boolean metaDistributorEnabled) {
        this.metaDistributorEnabled = metaDistributorEnabled;
    }

    public Map<String, String> getMapClients() {
        return mapClients;
    }

    public void setMapClients(Map<String, String> mapClients) {
        this.mapClients = mapClients;
    }

    public Map<String, String> getMapDistributor() {
        if (getMetaDistributorEnabled()) {
            List<MetaData> mds = metaDataFacade.findByClientMetaMember(getUserweb().getClient().getClientCod(), META_DISTRIBUTOR, 1L);
            for (MetaData metaData : mds) {
                mapDistributor.put(metaData.getMetaDataPK().getCodeChr(), metaData.getValueChr());
            }
        }
        return mapDistributor;
    }

    public void setMapDistributor(Map<String, String> mapDistributor) {
        this.mapDistributor = mapDistributor;
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
                    String value = getValueByIntegrationMethod(codClient, key, "C");
                    mapClients.put(key, value == null ? null : value.trim().equals("") ? null : value.trim());
                } catch (Exception ex) {
                    notifier.signal(getClass(), Action.ERROR, SecurityBean.getInstance().getLoggedInUserClient(), null, getCurrentViewId(), i18n.iValue("web.client.backingBean.inventory.messages.ClientEntityValueIntegrationError"), getSessionId(), getIpAddress(), ex);
                }
            }
        }
    }

    private String getValueByIntegrationMethod(Long codClient, String code, String discriminator) throws MoreThanOneResultException, GenericFacadeException {
        String retValue = null;
        if (metaEnabled) {
            MetaData md = metaDataFacade.findByClientMetaMemberAndCode(codClient, META_CLIENT, 1L, code);
            if (md != null) {
                retValue = md.getValueChr();
            }
        } else if (wsConexionExists) {
            retValue = tmpWsresultFacade.findData(codClient, getSessionId(), discriminator, code);
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
            String msg = MessageFormat.format(i18n.iValue("web.client.backingBean.message.WrongWSInvocation"), i18n.iValue("web.servicename.Inventory"), codClient);
            notifier.signal(getClass(), Action.ERROR, SecurityBean.getInstance().getLoggedInUserClient(), null, getCurrentViewId(), msg, getSessionId(), getIpAddress(), ex);
            wsConexionExists = Boolean.FALSE;
        } catch (WebServiceException we) {
            String msg = MessageFormat.format(i18n.iValue("web.client.backingBean.message.WrongWSInvocation"), i18n.iValue("web.servicename.Inventory"), codClient);
            notifier.signal(getClass(), Action.ERROR, SecurityBean.getInstance().getLoggedInUserClient(), null, getCurrentViewId(), msg, getSessionId(), getIpAddress(), we);
            wsConexionExists = Boolean.FALSE;
        }
        return value;
    }

    private void validateIntegrationMethod(Long codClient) {
        if (!integrationMethodValidated) {
            // Valida si existe META-CLIENT
            try {
                MetaClient mc = metaClientFacade.findByMetaAndClient(codClient, META_CLIENT);
                if (mc != null && mc.getEnabledChr()) {
                    metaEnabled = true;
                }
            } catch (Exception ex) {
                Logger.getLogger(VisitBean.class.getName()).log(Level.FATAL, null, ex);
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
        return " ORDER BY sv.SERVICEVALUE_COD ASC, svd.SERVICEVALUEDETAIL_COD";
    }

}
