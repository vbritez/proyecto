package com.tigo.cs.view.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
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
 * @author extmii
 * @version $Id$
 */
@ManagedBean(name = "fleetBean")
@ViewScoped
public class FleetBean extends AbstractServiceBean {

    private static final long serialVersionUID = 4197069646702077736L;
    public static final int COD_SERVICE = 12;
    public static final Long COD_META_CLIENT = 1L;
    public static final Long COD_META_DRIVER = 7L;
    public static final Long COD_META_VEHICLE = 8L;

    private Map<String, String> mapClients = new HashMap<String, String>();
    private Map<String, String> mapDrivers = new HashMap<String, String>();
    private Map<String, String> mapVehicles = new HashMap<String, String>();
    private String URLWS = "";

    private boolean metaClientEnabled = false;
    private Boolean metaDriverEnabled = null;
    private Boolean metaVehicleEnabled = null;
    private boolean wsConexionExists = false;
    private boolean integrationMethodForClientValidated = false;

    public FleetBean() {
        setCodService(COD_SERVICE);
        setShowMapOnHeader(false);
        setShowMapOnDetail(true);
        setPdfReportDetailName("rep_fleet_cabdetail");
        setXlsReportDetailName("rep_fleet_cabdetail_xls");
        setPdfReportCabDetailName("rep_fleet_cabdetail");
        setXlsReportCabDetailName("rep_fleet_cabdetail_xls");
        getSortHelper().setField("servicevalueCod");
    }

    // GETTER AND SETTER
    public Map<String, String> getMapClients() {
        return mapClients;
    }

    public void setMapClients(Map<String, String> mapClients) {
        this.mapClients = mapClients;
    }

    public Boolean getMetaDriverEnabled() {
        if (metaDriverEnabled == null) {
            metaDriverEnabled = false;
            try {
                MetaClient mc = metaClientFacade.findByMetaAndClient(getUserweb().getClient().getClientCod(), COD_META_DRIVER);
                if (mc != null && mc.getEnabledChr()) {
                    metaDriverEnabled = true;
                }
            } catch (Exception ex) {
                notifier.signal(getClass(), Action.ERROR, i18n.iValue("web.client.backingBean.fleet.messages.VerificationEnablingStateDriverMetaError"));
            }
        }
        return metaDriverEnabled;
    }

    public void setMetaDriverEnabled(Boolean metaDriverEnabled) {
        this.metaDriverEnabled = metaDriverEnabled;
    }

    public Map<String, String> getMapDrivers() {
        if (getMetaDriverEnabled()) {
            List<MetaData> mds = metaDataFacade.findByClientMetaMember(getUserweb().getClient().getClientCod(), COD_META_DRIVER, 1L);
            for (MetaData metaData : mds) {
                mapDrivers.put(metaData.getMetaDataPK().getCodeChr(), metaData.getValueChr());
            }
        }
        return mapDrivers;
    }

    public void setMapDrivers(Map<String, String> mapDrivers) {
        this.mapDrivers = mapDrivers;
    }

    public void setMetaClientEnabled(boolean metaClientEnabled) {
        this.metaClientEnabled = metaClientEnabled;
    }

    public Boolean getMetaVehicleEnabled() {
        if (metaVehicleEnabled == null) {
            metaVehicleEnabled = false;
            try {
                MetaClient mc = metaClientFacade.findByMetaAndClient(getUserweb().getClient().getClientCod(), COD_META_VEHICLE);
                if (mc != null && mc.getEnabledChr()) {
                    metaVehicleEnabled = true;
                }
            } catch (Exception ex) {
                notifier.signal(getClass(), Action.ERROR, i18n.iValue("web.client.backingBean.fleet.messages.VerificationEnablingStateVehicleMetaError"));
            }
        }
        return metaVehicleEnabled;
    }

    public void setMetaVehicleEnabled(Boolean metaVehicleEnabled) {
        this.metaVehicleEnabled = metaVehicleEnabled;
    }

    public void setMapVehicles(Map<String, String> mapVehicles) {
        this.mapVehicles = mapVehicles;
    }

    public Map<String, String> getMapVehicles() {
        if (getMetaVehicleEnabled()) {
            List<MetaData> mds = metaDataFacade.findByClientMetaMember(getUserweb().getClient().getClientCod(), COD_META_VEHICLE, 1L);
            for (MetaData metaData : mds) {
                mapVehicles.put(metaData.getMetaDataPK().getCodeChr(), metaData.getValueChr());
            }
        }
        return mapVehicles;
    }

    @Override
    public void detailMetaDataFromModel() {
        Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        validateIntegrationMethodForClient(codClient);

        mapClients = new HashMap<String, String>();
        if (getDataModelDetail() != null
            && getDataModelDetail().getRowCount() > 0) {
            for (ServiceValueDetail svd : getDataModelDetail()) {
                try {
                    String key = svd.getColumn1Chr();
                    String value = getValueByIntegrationMethod(codClient, COD_META_CLIENT, key);
                    mapClients.put(key, value == null ? null : value.trim().equals("") ? null : value.trim());
                } catch (Exception ex) {
                    notifier.signal(getClass(), Action.ERROR, SecurityBean.getInstance().getLoggedInUserClient(), null, getCurrentViewId(), i18n.iValue("web.client.backingBean.orderBean.message.ClientsTempTableError"), getSessionId(), getIpAddress(), ex);
                }
            }
        }
    }

    private String getValueByIntegrationMethod(Long codClient, Long codMeta, String code) throws MoreThanOneResultException, GenericFacadeException {
        String retValue = null;
        boolean enableStorage = (metaClientEnabled && codMeta.equals(1L));
        // || (metaProductEnabled && codMeta.equals(2L));
        if (enableStorage) {
            MetaData md = metaDataFacade.findByClientMetaMemberAndCode(codClient, codMeta, 1L, code);
            if (md != null) {
                retValue = md.getValueChr();
            }
        } else if (wsConexionExists) {
            String discriminator = codMeta.equals(1L) ? "C" : "P";
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
            String msg = MessageFormat.format(i18n.iValue("web.client.backingBean.message.WrongWSURL"), i18n.iValue("web.servicename.Order"), codClient);
            notifier.signal(getClass(), Action.ERROR, SecurityBean.getInstance().getLoggedInUserClient(), null, getCurrentViewId(), msg, getSessionId(), getIpAddress(), ex);
            wsConexionExists = Boolean.FALSE;
        } catch (WebServiceException we) {
            String msg = MessageFormat.format(i18n.iValue("web.client.backingBean.message.WrongWSURL"), i18n.iValue("web.servicename.Order"), codClient);
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
        return "";
    }

    @Override
    public String getDetailReportWhereCriteria() {
        return "";
    }

    @Override
    public String getMessageDescriptionDetailMap(ServiceValueDetail svd) {

        DateFormat sdf = new SimpleDateFormat(
                ApplicationBean.getInstance().getDefaultOutputDateTimeFormat());
        String employeeCode = getServiceValue().getColumn1Chr();
        String employee = mapDrivers.get(employeeCode);
        String messDescrip = employeeCode.concat(employee != null
            && employee.trim().length() > 0 ? " - ".concat(employee) : "").concat(" | ").concat(sdf.format(svd.getMessage().getDateinDat()));

        return messDescrip;
    }
    
    
   
}
