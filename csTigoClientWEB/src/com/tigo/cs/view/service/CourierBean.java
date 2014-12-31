package com.tigo.cs.view.service;

import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.security.SecurityBean;

/**
 * 
 * @author Javier Kovacs
 * @version $Id$
 */
@ManagedBean(name = "courierBean")
@ViewScoped
public class CourierBean extends AbstractServiceBean {

    private static final long serialVersionUID = -1977232785236529915L;
    public static final int COD_SERVICE = 18;
    public static final Long COD_META_MOTIVE = 3L;

    private Map<String, String> mapMotives = new HashMap<String, String>();

    private Boolean metaMotiveEnabled;

    public CourierBean() {
        setCodService(COD_SERVICE);
        setShowMapOnHeader(true);
        setPdfReportDetailName("rep_courier_cabdetail");
        setXlsReportDetailName("rep_courier_cabdetail_xls");
        setPdfReportCabDetailName("rep_courier_cabdetail");
        setXlsReportCabDetailName("rep_courier_cabdetail_xls");
        getSortHelper().setField("servicevalueCod");
    }

    // GETTER AND SETTER

    public Map<String, String> getMapMotives() {
        return mapMotives;
    }

    public void setMapMotives(Map<String, String> mapMotives) {
        this.mapMotives = mapMotives;
    }

    @Override
    public void headerMetaDataFromModel() {
        Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        if (getMetaMotiveEnabled()) {

            mapMotives = new HashMap<String, String>();
            if (getDataModelHeader() != null
                && getDataModelHeader().getRowCount() > 0) {
                for (ServiceValue sv : getDataModelHeader()) {
                    try {
                        String key = sv.getColumn3Chr();
                        MetaData md = metaDataFacade.findByClientMetaMemberAndCode(
                                codClient, COD_META_MOTIVE, 1L, key);
                        if (md != null) {
                            String value = md.getValueChr();
                            mapMotives.put(
                                    key,
                                    value == null ? null : value.trim().equals(
                                            "") ? null : value.trim());
                        }
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

}
