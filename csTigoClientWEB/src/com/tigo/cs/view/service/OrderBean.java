package com.tigo.cs.view.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.ws.WebServiceException;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.TmpWsresult;
import com.tigo.cs.facade.ServiceValueFacade;
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
@ManagedBean(name = "orderBean")
@ViewScoped
public class OrderBean extends AbstractServiceBean {

    private static final long serialVersionUID = -1977232785236529915L;
    public static final int COD_SERVICE = 2;
    public static final Long COD_META_CLIENT = 1L;
    public static final Long COD_META_PRODUCT = 2L;

    private Map<String, String> mapClients = new HashMap<String, String>();
    private Map<String, String> mapProducts = new HashMap<String, String>();
    private String URLWS = "";

    private boolean metaClientEnabled = false;
    private boolean metaProductEnabled = false;
    private boolean wsConexionExists = false;
    private boolean integrationMethodForClientValidated = false;
    private boolean integrationMethodForProductValidated = false;
    private Map<ServiceValue, Boolean> markedOrders;
    @EJB
    private ServiceValueFacade svFacade;

    public OrderBean() {
        setCodService(COD_SERVICE);
        setShowMapOnHeader(true);
        setPdfReportDetailName("rep_order_detail");
        setXlsReportDetailName("rep_order_detail_xls");
        setPdfReportCabDetailName("rep_order_cabdetail");
        setXlsReportCabDetailName("rep_order_cabdetail_xls");
        getSortHelper().setField("servicevalueCod");
    }

    // GETTER AND SETTER

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

    public Map<ServiceValue, Boolean> getMarkedOrders() {
        if (markedOrders == null) {
            markedOrders = new HashMap<ServiceValue, Boolean>();
        }

        return markedOrders;
    }

    public void setMarkedOrders(Map<ServiceValue, Boolean> markedOrders) {
        this.markedOrders = markedOrders;
    }

    @Override
    public void headerMetaDataFromModel() {
        Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        validateIntegrationMethodForClient(codClient);

        mapClients = new HashMap<String, String>();
        markedOrders = new HashMap<ServiceValue, Boolean>();
        if (getDataModelHeader() != null
            && getDataModelHeader().getRowCount() > 0) {
            for (ServiceValue sv : getDataModelHeader()) {
                try {
                    String key = sv.getColumn1Chr();
                    String value = getValueByIntegrationMethod(codClient,
                            COD_META_CLIENT, key);
                    mapClients.put(
                            key,
                            value == null ? null : value.trim().equals("") ? null : value.trim());
                    if (sv.getColumn5Chr() != null
                        && sv.getColumn5Chr().equals("1")) {
                        markedOrders.put(sv, true);
                    } else {
                        markedOrders.put(sv, false);
                    }
                } catch (Exception ex) {
                    notifier.signal(
                            getClass(),
                            Action.ERROR,
                            SecurityBean.getInstance().getLoggedInUserClient(),
                            null,
                            getCurrentViewId(),
                            i18n.iValue("web.client.backingBean.orderBean.message.ClientsTempTableError"),
                            getSessionId(), getIpAddress(), ex);
                }
            }
        }
    }

    @Override
    public void detailMetaDataFromModel() {
        Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        validateIntegrationMethodForProduct(codClient);

        mapProducts = new HashMap<String, String>();
        if (getServiceValue() != null) {
            for (ServiceValueDetail svd : getDataModelDetail()) {
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
                            i18n.iValue("web.client.backingBean.orderBean.message.ClientsTempTableError"),
                            getSessionId(), getIpAddress(), ex);
                }
            }
        }

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
                    i18n.iValue("web.client.backingBean.message.WrongWSURL"),
                    i18n.iValue("web.servicename.Order"), codClient);
            notifier.signal(getClass(), Action.ERROR,
                    SecurityBean.getInstance().getLoggedInUserClient(), null,
                    getCurrentViewId(), msg, getSessionId(), getIpAddress(), ex);
            wsConexionExists = Boolean.FALSE;
        } catch (WebServiceException we) {
            String msg = MessageFormat.format(
                    i18n.iValue("web.client.backingBean.message.WrongWSURL"),
                    i18n.iValue("web.servicename.Order"), codClient);
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

    @Override
    public String getCabDetailReportOrderBy() {
        return " ORDER BY sv.SERVICEVALUE_COD ASC, svd.SERVICEVALUEDETAIL_COD";
    }

    @Override
    public String getDetailWhereCriteria() {
        return "";
    }

    @Override
    public String getDetailReportWhereCriteria() {
        return "";
    }

    public String markOrders() {

        Iterator<ServiceValue> it = markedOrders.keySet().iterator();
        while (it.hasNext()) {
            ServiceValue serviceValue = it.next();
            svFacade.markOrder(serviceValue, markedOrders.get(serviceValue));
        }
        setSuccessMessage(i18n.iValue("web.client.backingBean.orderBean.message.MarkedOrders"));
        return null;
    }

    @Override
    public String getMessageDescriptionHeaderMap(ServiceValue sv) {
        DateFormat sdf = new SimpleDateFormat(ApplicationBean.getInstance().getDefaultOutputDateTimeFormat());
        String messDescrip = sv.getMessage().getUserphone().getNameChr().concat(
                " | ").concat(sdf.format(sv.getMessage().getDateinDat()));

        Long codClient = sv.getUserphone().getClient().getClientCod();
        String key = sv.getColumn1Chr();
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

    /**
     * se sobreescribe el metodo para limpiar el mapa en el detalle ya que solo
     * se borra cuando el mapa esta en detalle, pero como automaticamente se
     * muestra la localizacion en el detalle, tambien debemos limpiar el mapa
     */
    @Override
    public String cancelDetail() {

        mapModel = null;
        mapCenter = null;
        mapZoom = null;
        mapType = null;
        getMapModel();
        return super.cancelDetail();

    }

}
