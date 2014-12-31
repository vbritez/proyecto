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
import com.tigo.cs.ws.client.Guard;

/**
 * 
 * @author Javier Kovacs
 * @version $Id$
 */
@ManagedBean(name = "guardBean")
@ViewScoped
public class GuardBean extends AbstractServiceBean {

    private static final long serialVersionUID = 8734277068056164937L;
    public static final int COD_SERVICE = 6;
    public static final Long COD_META_GUARD = 4L;
    private Map<String, String> mapGuards = new HashMap<String, String>();
    private String URLWS = "";
    private boolean metaGuardEnabled = false;
    private boolean wsConexionExists = false;
    private boolean integrationMethodForGuardValidated = false;

    public GuardBean() {
        setCodService(COD_SERVICE);
        setShowMapOnHeader(true);
        setPdfReportCabDetailName("rep_guard_cabdetail");
        setXlsReportCabDetailName("rep_guard_cabdetail_xls");
        setPdfReportDetailName("rep_guard_detail");
        setXlsReportDetailName("rep_guard_detail_xls");
        getSortHelper().setField("servicevalueCod");
    }

    // GETTER AND SETTER

    public Map<String, String> getMapGuards() {
        return mapGuards;
    }

    public void setMapGuards(Map<String, String> mapGuards) {
        this.mapGuards = mapGuards;
    }

    @Override
    public void headerMetaDataFromModel() {
        Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        validateIntegrationMethodForGuard(codClient);

        mapGuards = new HashMap<String, String>();
        if (getDataModelHeader() != null
            && getDataModelHeader().getRowCount() > 0) {
            for (ServiceValue sv : getDataModelHeader()) {
                try {
                    String key = sv.getColumn1Chr();
                    String value = getValueByIntegrationMethod(codClient, COD_META_GUARD, key);
                    mapGuards.put(key, value == null ? null : value.trim().equals("") ? null : value.trim());
                } catch (Exception ex) {
                    notifier.signal(getClass(), Action.ERROR, SecurityBean.getInstance().getLoggedInUserClient(), null, getCurrentViewId(), i18n.iValue("web.client.backingBean.message.ClientsTempTableError"), getSessionId(), getIpAddress(), ex);
                }
            }
        }
    }

    private String getValueByIntegrationMethod(Long codClient, Long codMeta, String code) throws MoreThanOneResultException, GenericFacadeException {
        String retValue = null;
        boolean enableStorage = (metaGuardEnabled && codMeta.equals(4L));
        if (enableStorage) {
            MetaData md = metaDataFacade.findByClientMetaMemberAndCode(codClient, codMeta, 1L, code);
            if (md != null) {
                retValue = md.getValueChr();
            }
        } else if (wsConexionExists) {
            String discriminator = codMeta.equals(4L) ? "G" : "";
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
            String discriminator = "G";
            if (codMeta.equals(4L)) {
                Guard guard = port.getGuardByCode(key);
                value = guard != null && guard.getName() != null ? guard.getName().trim() : null;
            }

            TmpWsresult tmp = new TmpWsresult();
            tmp.setCodClient(codClient);
            tmp.setSessionId(getSessionId());
            tmp.setDataType(discriminator);
            tmp.setDataId(key);
            tmp.setDataC(value != null && !value.trim().equals("") ? value.trim() : " ");
            tmpWsresultFacade.create(tmp);
        } catch (MalformedURLException ex) {
            String msg = MessageFormat.format(i18n.iValue("web.client.backingBean.message.WrongWSInvocation"), i18n.iValue("web.servicename.Guard"), codClient);
            notifier.signal(getClass(), Action.ERROR, SecurityBean.getInstance().getLoggedInUserClient(), null, getCurrentViewId(), msg, getSessionId(), getIpAddress(), ex);
            wsConexionExists = Boolean.FALSE;
        } catch (WebServiceException we) {
            String msg = MessageFormat.format(i18n.iValue("web.client.backingBean.message.WrongWSInvocation"), i18n.iValue("web.servicename.Guard"), codClient);
            notifier.signal(getClass(), Action.ERROR, SecurityBean.getInstance().getLoggedInUserClient(), null, getCurrentViewId(), msg, getSessionId(), getIpAddress(), we);
            wsConexionExists = Boolean.FALSE;
        }
        return value;
    }

    private void validateIntegrationMethodForGuard(Long codClient) {
        if (!integrationMethodForGuardValidated) {
            // Valida si existe META-CLIENT
            try {
                MetaClient mc = metaClientFacade.findByMetaAndClient(codClient, COD_META_GUARD);
                if (mc != null && mc.getEnabledChr()) {
                    metaGuardEnabled = true;
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

            integrationMethodForGuardValidated = true;
        }
    }

    // ABSTRACT METHOD IMPLEMENTATIONS

    @Override
    public String getCabDetailReportOrderBy() {
        String sortAttributeColumnName = getAttributeColumnName(ServiceValue.class, getSortHelper().getField()).toLowerCase();

        if (sortAttributeColumnName.equals("datein_dat")) {
            sortAttributeColumnName = "M.".concat(sortAttributeColumnName);
        } else if (sortAttributeColumnName.equals("cellphone_num")) {
            sortAttributeColumnName = "U.".concat(sortAttributeColumnName);
        } else if (sortAttributeColumnName.equals("name_chr")) {
            sortAttributeColumnName = "U.".concat(sortAttributeColumnName);
        } else if (sortAttributeColumnName.equals("column1_chr")) {
            sortAttributeColumnName = "SV.".concat(sortAttributeColumnName);
        } else if (sortAttributeColumnName.equals("column2_chr")) {
            sortAttributeColumnName = "SV.".concat(sortAttributeColumnName);
        } else if (sortAttributeColumnName.equals("column3_chr")) {
            sortAttributeColumnName = "SV.".concat(sortAttributeColumnName);
        }

        String orderBy = "ORDER BY ".concat(sortAttributeColumnName).concat(getSortHelper().isAscending() ? " ASC" : " DESC");
        orderBy += " , SVD.RECORDDATE_DAT ASC";
        return orderBy;
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
