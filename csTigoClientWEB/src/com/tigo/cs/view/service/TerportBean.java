package com.tigo.cs.view.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.ApplicationBean;

@ManagedBean(name = "terportBean")
@ViewScoped
public class TerportBean extends AbstractServiceBean{

    public static final int COD_SERVICE = 24;
    private Map<String, String> mapAttendants = new HashMap<String, String>();
    private Map<String, String> mapOperators = new HashMap<String, String>();
    private Map<String, String> mapMachines = new HashMap<String, String>();

    public static final Long COD_META_ATTENDANT = 18L;
    public static final Long COD_META_MACHINE = 19L;
    public static final Long COD_META_OPERATOR = 20L;
    
    MetaClient metaClientAttendants = null;
    MetaClient metaClientMachines = null;
    MetaClient metaClientOperators = null;
    
    public TerportBean() {
        setCodService(COD_SERVICE);
        setShowMapOnDetail(true);
        setPdfReportCabDetailName("rep_terport_cabdetail");
        setXlsReportCabDetailName("rep_terport_cabdetail_xls");
        setPdfReportDetailName("rep_terport_detail");
        setXlsReportDetailName("rep_terport_detail_xls");
    }
    
    
    @Override
    public void headerMetaDataFromModel() {
        Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        mapAttendants = new HashMap<String, String>();
        mapOperators = new HashMap<String, String>();
        mapMachines = new HashMap<String, String>();

        if (validateIntegrationMethodForAttendants(codClient)) {
            if (getDataModelHeader() != null
                && getDataModelHeader().getRowCount() > 0) {
                for (ServiceValue sv : getDataModelHeader()) {
                    try {
                        String key = sv.getColumn1Chr();
                        String value = getMetaValue(codClient, key, COD_META_ATTENDANT);
                        mapAttendants.put(key, value == null ? null : value.trim().equals("") ? null : value.trim());
                    } catch (Exception ex) {
                        notifier.signal(getClass(), Action.ERROR, SecurityBean.getInstance().getLoggedInUserClient(), null, getCurrentViewId(), i18n.iValue("web.client.backingBean.message.AttendantError"), getSessionId(), getIpAddress(), ex);
                    }
                }
            }
        }
        
        if (validateIntegrationMethodForMachines(codClient)) {
            if (getDataModelHeader() != null
                && getDataModelHeader().getRowCount() > 0) {
                for (ServiceValue sv : getDataModelHeader()) {
                    try {
                        String key = sv.getColumn3Chr();
                        String value = getMetaValue(codClient, key, COD_META_MACHINE);
                        mapMachines.put(key, value == null ? null : value.trim().equals("") ? null : value.trim());
                    } catch (Exception ex) {
                        notifier.signal(getClass(), Action.ERROR, SecurityBean.getInstance().getLoggedInUserClient(), null, getCurrentViewId(), i18n.iValue("web.client.backingBean.message.MachineError"), getSessionId(), getIpAddress(), ex);
                    }
                }
            }
        }
        
        if (validateIntegrationMethodForOperators(codClient)) {
            if (getDataModelHeader() != null
                && getDataModelHeader().getRowCount() > 0) {
                for (ServiceValue sv : getDataModelHeader()) {
                    try {
                        String key = sv.getColumn2Chr();
                        String value = getMetaValue(codClient, key, COD_META_OPERATOR);
                        mapOperators.put(key, value == null ? null : value.trim().equals("") ? null : value.trim());
                    } catch (Exception ex) {
                        notifier.signal(getClass(), Action.ERROR, SecurityBean.getInstance().getLoggedInUserClient(), null, getCurrentViewId(), i18n.iValue("web.client.backingBean.message.MachineError"), getSessionId(), getIpAddress(), ex);
                    }
                }
            }
        }
    }
    
    private String getMetaValue(Long codClient, String code, Long metaCode) {
        String retValue = null;
        MetaData md;
        try {
            md = metaDataFacade.findByClientMetaMemberAndCode(codClient,
                    metaCode, 1L, code);
        } catch (Exception e) {
            return null;
        }
        if (md != null) {
            retValue = md.getValueChr();
        }
        return retValue;
    }

    
    private boolean validateIntegrationMethodForAttendants(Long codClient) {

        if (metaClientAttendants == null) {
            try {
                metaClientAttendants = metaClientFacade.findByMetaAndClient(
                        codClient, COD_META_ATTENDANT);
            } catch (Exception e) {
                return false;
            }
        }
        return metaClientAttendants.getEnabledChr();

    }

    private boolean validateIntegrationMethodForMachines(Long codClient) {

        if (metaClientMachines == null) {
            try {
                metaClientMachines = metaClientFacade.findByMetaAndClient(
                        codClient, COD_META_MACHINE);
                return metaClientMachines.getEnabledChr();
            } catch (Exception e) {
                return false;
            }
        }
            return metaClientMachines.getEnabledChr();
    }

    private boolean validateIntegrationMethodForOperators(Long codClient) {

        if (metaClientOperators == null) {
            try {
                metaClientOperators = metaClientFacade.findByMetaAndClient(
                        codClient, COD_META_OPERATOR);
            } catch (Exception e) {
                return false;
            }
        }
        return metaClientOperators.getEnabledChr();

    }
    
    @Override
    public String getDetailWhereCriteria() {
        return "";
    }

    @Override
    public String getDetailReportWhereCriteria() {
        return "";
    }

    public Map<String, String> getMapAttendants() {
        return mapAttendants;
    }

    public void setMapAttendants(Map<String, String> mapAttendants) {
        this.mapAttendants = mapAttendants;
    }

    public Map<String, String> getMapOperators() {
        return mapOperators;
    }

    public void setMapOperators(Map<String, String> mapOperators) {
        this.mapOperators = mapOperators;
    }

    public Map<String, String> getMapMachines() {
        return mapMachines;
    }

    public void setMapMachines(Map<String, String> mapMachines) {
        this.mapMachines = mapMachines;
    }
    
    @Override
    public String getMessageDescriptionDetailMap(ServiceValueDetail svd) {
        String messDescrip = "";
        try {
            DateFormat sdf = new SimpleDateFormat(ApplicationBean.getInstance().getDefaultOutputDateTimeFormat());
            Long codClient = svd.getMessage().getUserphone().getClient().getClientCod();
            String attendantCode = getServiceValue().getColumn1Chr();
            String attendant = metaDataFacade.findByClientMetaMemberAndCode(codClient,
                    COD_META_ATTENDANT,1L, attendantCode).getValueChr();
            String operatorCode = getServiceValue().getColumn2Chr();
            String operator = metaDataFacade.findByClientMetaMemberAndCode(codClient,
                    COD_META_OPERATOR,1L, operatorCode).getValueChr();
            String machineCode = getServiceValue().getColumn3Chr();
            String machine = metaDataFacade.findByClientMetaMemberAndCode(codClient,
                    COD_META_MACHINE,1L, machineCode).getValueChr();
            messDescrip = "Encargado: "+attendantCode.concat(
                    attendant != null && attendant.trim().length() > 0 ? " - ".concat(attendant) : "").concat(
                    " Operador: ").concat(operatorCode.concat(
                            operator != null && operator.trim().length() > 0 ? " - ".concat(operator) : "")).concat(" Máquina: ").concat(machineCode.concat(
                                    machine != null && machine.trim().length() > 0 ? " - ".concat(machine) : "")).concat(" Contenedor: ").concat(
                                            svd.getColumn1Chr()).concat(" Nva. Ubicación: ").concat(
                                                    svd.getColumn2Chr()).concat(" Fecha: ").concat(
                    sdf.format(svd.getMessage().getDateinDat()));

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(TerportBean.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } 
        return messDescrip;
    }
    
    
    @Override
    public String getCabDetailReportOrderBy() {
        String sortAttributeColumnName = getAttributeColumnName(
                ServiceValue.class, getSortHelper().getField());
        if (getSortHelper().getField().indexOf(".") < 0) {
            sortAttributeColumnName = ",sv.".concat(sortAttributeColumnName);
        } else {
            sortAttributeColumnName = ",svd.".concat(sortAttributeColumnName);
        }
        return "ORDER BY sv.servicevalue_cod".concat(sortAttributeColumnName).concat(
                getSortHelper().isAscending() ? " ASC" : " DESC");
    }

}
