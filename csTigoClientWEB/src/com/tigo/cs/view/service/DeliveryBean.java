package com.tigo.cs.view.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.ws.WebServiceException;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.TmpWsresult;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.ws.client.ClientWS;
import com.tigo.cs.ws.client.ClientWSService;
import com.tigo.cs.ws.client.Customer;

/**
 * 
 * @author Javier Kovacs
 * @version $Id$
 */
@ManagedBean(name = "deliveryBean")
@ViewScoped
public class DeliveryBean extends AbstractServiceBean {

    private static final long serialVersionUID = -4872007572186952854L;
    public static final int COD_SERVICE = 7;
    public static final Long COD_META_CLIENT = 1L;
    private Map<String, String> mapClients = new HashMap<String, String>();
    private String URLWS = "";
    private boolean metaClientEnabled = false;
    private boolean wsConexionExists = false;
    private boolean integrationMethodForClientValidated = false;

    public DeliveryBean() {
        setCodService(COD_SERVICE);
        setShowMapOnHeader(true);
        setPdfReportCabDetailName("rep_delivery_cabdetail");
        setXlsReportCabDetailName("rep_delivery_cabdetail_xls");
        setPdfReportDetailName("rep_delivery_cabdetail");
        setXlsReportDetailName("rep_delivery_cabdetail_xls");
        getSortHelper().setField("servicevalueCod");
    }

    // GETTER AND SETTER
    public Map<String, String> getMapClients() {
        return mapClients;
    }

    public void setMapClients(Map<String, String> mapClients) {
        this.mapClients = mapClients;
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

    private String getValueByIntegrationMethod(Long codClient, Long codMeta, String code) throws MoreThanOneResultException, GenericFacadeException {
        String retValue = null;
        boolean enableStorage = (metaClientEnabled && codMeta.equals(1L));
        if (enableStorage) {
            MetaData md = metaDataFacade.findByClientMetaMemberAndCode(codClient, codMeta, 1L, code);
            if (md != null) {
                retValue = md.getValueChr();
            }
        } else if (wsConexionExists) {
            String discriminator = codMeta.equals(1L) ? "C" : "";
            retValue = tmpWsresultFacade.findData(codClient, getSessionId(), discriminator, code);
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
            }

            TmpWsresult tmp = new TmpWsresult();
            tmp.setCodClient(codClient);
            tmp.setSessionId(getSessionId());
            tmp.setDataType(discriminator);
            tmp.setDataId(key);
            tmp.setDataC(value != null && !value.trim().equals("") ? value.trim() : " ");
            tmpWsresultFacade.create(tmp);
        } catch (MalformedURLException ex) {
            String msg = MessageFormat.format(i18n.iValue("web.client.backingBean.message.WrongWSInvocation"), i18n.iValue("web.servicename.Delivery"), codClient);
            notifier.signal(getClass(), Action.ERROR, SecurityBean.getInstance().getLoggedInUserClient(), null, getCurrentViewId(), msg, getSessionId(), getIpAddress(), ex);
            wsConexionExists = Boolean.FALSE;
        } catch (WebServiceException we) {
            String msg = MessageFormat.format(i18n.iValue("web.client.backingBean.message.WrongWSInvocation"), i18n.iValue("web.servicename.Delivery"), codClient);
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

    // ABSTRACT METHOD IMPLEMENTATIONS
    @Override
    public String getCabDetailReportOrderBy() {
        return " ORDER BY sv.SERVICEVALUE_COD ASC, svd.SERVICEVALUEDETAIL_COD ";
    }

    @Override
    public String getDetailWhereCriteria() {
        return "";
    }

    @Override
    public String getDetailReportWhereCriteria() {
        return "";
    }

}
