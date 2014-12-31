package com.tigo.cs.view.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
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
import com.tigo.cs.ws.client.Motive;
import com.tigo.cs.ws.client.Product;

/**
 * 
 * @author Miguel Maciel
 * @version $Id$
 */
@ManagedBean(name = "inventoryStdBean")
@ViewScoped
public class InventoryStdBean extends AbstractServiceBean {

    private static final long serialVersionUID = 7566154006870431839L;
    public static final int COD_SERVICE = 10;
    public static final Long META_DEPOSIT = 10L;
    public static final Long META_PRODUCT = 2L;
    private Map<String, String> mapDeposit = new HashMap<String, String>();
    private Map<String, String> mapProduct = new HashMap<String, String>();
    private String URLWS = "";
    private Boolean metaProductEnabled = false;
    private Boolean metaDepositEnabled = null;
    private boolean wsConexionExists = false;
    private boolean integrationMethodValidatedForProduct = false;
    private boolean integrationMethodValidatedForDeposit = false;

    public InventoryStdBean() {
        setCodService(COD_SERVICE);
        setShowMapOnDetail(true);
        setPdfReportDetailName("rep_inventorystd_detail");
        setXlsReportDetailName("rep_inventorystd_detail_xls");
        setPdfReportCabDetailName("rep_inventorystd_cabdetail");
        setXlsReportCabDetailName("rep_inventorystd_cabdetail_xls");
    }

    // GETTER AND SETTER
    public Map<String, String> getMapProduct() {
        return mapProduct;
    }

    public void setMapProduct(Map<String, String> mapProduct) {
        this.mapProduct = mapProduct;
    }

    public Map<String, String> getMapDeposit() {
        return mapDeposit;
    }

    public void setMapDeposit(Map<String, String> mapDeposit) {
        this.mapDeposit = mapDeposit;
    }

    @Override
    public void detailMetaDataFromModel() {
        Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();

        validateIntegrationMethodForDeposit(codClient);
        validateIntegrationMethodForProduct(codClient);

        mapDeposit = new HashMap<String, String>();
        if (getServiceValue() != null) {
            for (ServiceValueDetail svd : getDataModelDetail()) {
                try {
                    String key = svd.getColumn1Chr();
                    String value = getValueByIntegrationMethodDeposit(
                            codClient, key, "D");
                    mapDeposit.put(
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

        mapProduct = new HashMap<String, String>();
        if (getServiceValue() != null) {
            for (ServiceValueDetail svd : getDataModelDetail()) {
                try {
                    String key = svd.getColumn2Chr();
                    String value = getValueByIntegrationMethodProduct(
                            codClient, key, "P");
                    mapProduct.put(
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

    private String getValueByIntegrationMethodProduct(Long codClient, String code, String discriminator) throws MoreThanOneResultException, GenericFacadeException {
        String retValue = null;

        if (metaProductEnabled) {
            MetaData md = metaDataFacade.findByClientMetaMemberAndCode(
                    codClient, META_PRODUCT, 1L, code);
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

    private String getValueByIntegrationMethodDeposit(Long codClient, String code, String discriminator) throws MoreThanOneResultException, GenericFacadeException {
        String retValue = null;

        if (metaDepositEnabled) {
            MetaData md = metaDataFacade.findByClientMetaMemberAndCode(
                    codClient, META_DEPOSIT, 1L, code);
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
            if (discriminator.equals("P")) {
                Product product = port.getProductByCode(key);
                value = product != null && product.getName() != null ? product.getName().trim() : null;
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
                    i18n.iValue("web.servicename.InventoryStd"), codClient);
            notifier.signal(getClass(), Action.ERROR,
                    SecurityBean.getInstance().getLoggedInUserClient(), null,
                    getCurrentViewId(), msg, getSessionId(), getIpAddress(), ex);
            wsConexionExists = Boolean.FALSE;
        } catch (WebServiceException we) {
            String msg = MessageFormat.format(
                    i18n.iValue("web.client.backingBean.message.WrongWSInvocation"),
                    i18n.iValue("web.servicename.InventoryStd"), codClient);
            notifier.signal(getClass(), Action.ERROR,
                    SecurityBean.getInstance().getLoggedInUserClient(), null,
                    getCurrentViewId(), msg, getSessionId(), getIpAddress(), we);
            wsConexionExists = Boolean.FALSE;
        }
        return value;
    }

    private void validateIntegrationMethodForProduct(Long codClient) {
        if (!integrationMethodValidatedForProduct) {
            // Valida si existe META-PRODUCT
            try {
                MetaClient mc = metaClientFacade.findByMetaAndClient(codClient,
                        META_PRODUCT);
                if (mc != null && mc.getEnabledChr()) {
                    metaProductEnabled = true;
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

            integrationMethodValidatedForProduct = true;
        }

    }

    private void validateIntegrationMethodForDeposit(Long codClient) {
        if (!integrationMethodValidatedForDeposit) {
            // Valida si existe META-DEPOSIT
            try {
                MetaClient mc = metaClientFacade.findByMetaAndClient(codClient,
                        META_DEPOSIT);
                if (mc != null && mc.getEnabledChr()) {
                    metaDepositEnabled = true;
                }
            } catch (Exception ex) {
                Logger.getLogger(VisitBean.class.getName()).log(Level.FATAL,
                        null, ex);
            }

            integrationMethodValidatedForDeposit = true;
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

}
