package com.tigo.cs.view.service;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;

import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Polygon;

import com.tigo.cs.commons.jpa.PrimarySortedFieldNotFoundException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.util.ListDataModelViewCsTigo;
import com.tigo.cs.commons.util.PaginationHelper;
import com.tigo.cs.commons.util.SortHelper;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.view.DataClient;
import com.tigo.cs.facade.ClassificationFacade;
import com.tigo.cs.facade.view.DataStatusFacade;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.ApplicationBean;


@ManagedBean(name = "otViewBean")
@ViewScoped
public class OtViewBean extends AbstractServiceBean implements Serializable {

    static final long serialVersionUID = 1819958394089004180L;
    
    public static final int COD_SERVICE = 16;
    public static final Long META_CLIENT = 1L;
    public static final Long META_ACTIVITY = 11L;
    public static final Long META_STATUS = 12L;
    public static final Long META_ZONE = 13L;
    
    @EJB
    private DataStatusFacade dataStatusFacade;
    @EJB
    protected ClassificationFacade classificationFacade;
    
    private SimpleDateFormat sdFormat = null;
    private DataClient selectedDataClient;
    private List<OtDetail> detalles = new ArrayList<OtDetail>();
    private Client client;
    
    private Map<String, String> mapClients = null;
    private Map<String, String> mapActivities = null;
    private Map<String, String> mapZones = null;
    private Map<String, String> mapStatus = null;
    
    private Boolean metaClientEnabled = null;
    private Boolean metaActivityEnabled = null;
    private Boolean metaZoneEnabled = null;
    private Boolean metaStatusEnabled = null;
    
    public OtViewBean() {
    	setCodService(COD_SERVICE);
        setShowMapOnHeader(true);
        setShowMapOnDetail(true);
        setPdfReportDetailName("rep_ot_detail");
        setXlsReportDetailName("rep_ot_detail_xls");
        setPdfReportCabDetailName("rep_ot_cabdetail");
        setXlsReportCabDetailName("rep_ot_cabdetail_xls");
    }
    
    public Map<String, String> getMapClients() {
        if (getMetaClientEnabled()) {
        	if (mapClients == null){
	            List<MetaData> mds = metaDataFacade.findByClientMetaMember(getUserweb().getClient().getClientCod(), META_CLIENT, 1L);
	            mapClients = new HashMap<String, String>();
	            for (MetaData metaData : mds) {
	                mapClients.put(metaData.getMetaDataPK().getCodeChr(), metaData.getValueChr());
	            }
	        }
        }
        return mapClients;
    }
    
    public Map<String, String> getMapActivities() {
        if (getMetaActivityEnabled()) {
        	if (mapActivities == null){
	            List<MetaData> mds = metaDataFacade.findByClientMetaMember(getUserweb().getClient().getClientCod(), META_ACTIVITY, 1L);
	            mapActivities = new HashMap<String, String>();
	            for (MetaData metaData : mds) {
	                mapActivities.put(metaData.getMetaDataPK().getCodeChr(), metaData.getValueChr());
	            }
	        }
        }	
        return mapActivities;
    }
    
    public Map<String, String> getMapZones() {
        if (getMetaZoneEnabled()) {
        	if (mapZones == null){
	            List<MetaData> mds = metaDataFacade.findByClientMetaMember(getUserweb().getClient().getClientCod(), META_ZONE, 1L);
	            mapZones = new HashMap<String, String>();
	            for (MetaData metaData : mds) {
	                mapZones.put(metaData.getMetaDataPK().getCodeChr(), metaData.getValueChr());
	            }
	        }
        }
        return mapZones;
    }
    
    public Map<String, String> getMapStatus() {
        if (getMetaStatusEnabled()) {
        	if (mapStatus == null){
	            List<MetaData> mds = metaDataFacade.findByClientMetaMember(getUserweb().getClient().getClientCod(), META_STATUS, 1L);
	            mapStatus = new HashMap<String, String>();
	            for (MetaData metaData : mds) {
	                mapStatus.put(metaData.getMetaDataPK().getCodeChr(), metaData.getValueChr());
	            }
        	}
        }
        return mapStatus;
    }
    
    public Boolean getMetaClientEnabled() {
        if (metaClientEnabled == null) {
        	metaClientEnabled = false;
            try {
                MetaClient mc = metaClientFacade.findByMetaAndClient(getUserweb().getClient().getClientCod(), META_CLIENT);
                if (mc != null && mc.getEnabledChr()) {
                	metaClientEnabled = true;
                }
            } catch (Exception ex) {
                notifier.signal(getClass(), Action.ERROR, i18n.iValue("web.client.backingBean.fleet.messages.VerificationEnablingStateVehicleMetaError"));
            }
        }
        return metaClientEnabled;
    }

    public void setMetaClientEnabled(Boolean metaClientEnabled) {
        this.metaClientEnabled = metaClientEnabled;
    }
    
    public Boolean getMetaActivityEnabled() {
        if (metaActivityEnabled == null) {
        	metaActivityEnabled = false;
            try {
                MetaClient mc = metaClientFacade.findByMetaAndClient(getUserweb().getClient().getClientCod(), META_ACTIVITY);
                if (mc != null && mc.getEnabledChr()) {
                	metaActivityEnabled = true;
                }
            } catch (Exception ex) {
                notifier.signal(getClass(), Action.ERROR, i18n.iValue("web.client.backingBean.fleet.messages.VerificationEnablingStateVehicleMetaError"));
            }
        }
        return metaActivityEnabled;
    }

    public void setMetaActivityEnabled(Boolean metaActivityEnabled) {
        this.metaActivityEnabled = metaActivityEnabled;
    }
    
    public Boolean getMetaZoneEnabled() {
        if (metaZoneEnabled == null) {
        	metaZoneEnabled = false;
            try {
                MetaClient mc = metaClientFacade.findByMetaAndClient(getUserweb().getClient().getClientCod(), META_ZONE);
                if (mc != null && mc.getEnabledChr()) {
                	metaZoneEnabled = true;
                }
            } catch (Exception ex) {
                notifier.signal(getClass(), Action.ERROR, i18n.iValue("web.client.backingBean.fleet.messages.VerificationEnablingStateVehicleMetaError"));
            }
        }
        return metaZoneEnabled;
    }

    public void setMetaZoneEnabled(Boolean metaZoneEnabled) {
        this.metaZoneEnabled = metaZoneEnabled;
    }
    
    public Boolean getMetaStatusEnabled() {
        if (metaStatusEnabled == null) {
        	metaStatusEnabled = false;
            try {
                MetaClient mc = metaClientFacade.findByMetaAndClient(getUserweb().getClient().getClientCod(), META_STATUS);
                if (mc != null && mc.getEnabledChr()) {
                	metaStatusEnabled = true;
                }
            } catch (Exception ex) {
                notifier.signal(getClass(), Action.ERROR, i18n.iValue("web.client.backingBean.fleet.messages.VerificationEnablingStateVehicleMetaError"));
            }
        }
        return metaStatusEnabled;
    }

    public void setMetaStatusEnabled(Boolean metaStatusEnabled) {
        this.metaStatusEnabled = metaStatusEnabled;
    }
       
    
    
    public SimpleDateFormat getSdFormat() {
        if (sdFormat == null){
            String otScheduleDateFormat = "";
            try {
                sdFormat = new SimpleDateFormat(ApplicationBean.getInstance().getOtScheduleDateFormat());
            } catch (Exception ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (otScheduleDateFormat == null || otScheduleDateFormat.length() == 0) {
                otScheduleDateFormat = "dd/MM/yyyy HH:mm";
            }
            if (sdFormat == null) {
                sdFormat = new SimpleDateFormat(otScheduleDateFormat);
            }
        }
        return sdFormat;
    }

    public void setSdFormat(SimpleDateFormat sdFormat) {
        this.sdFormat = sdFormat;
    }
    
    public List<OtDetail> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<OtDetail> detalles) {
        this.detalles = detalles;
    }

//    @Override
//    public String editEntity() {
//        String retVal = super.editEntity(); 
//        
//        if (getEntity() != null){
//            selectedDataClient = dataClientFacade.findEntityByClientMetaCodigo(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), 1L, getEntity().getClientCod());
//            setDetalles(new ArrayList<OtDetail>());
//            List<ServiceValueDetail> dets = otFacade.getOtDetails(getEntity().getServicevalueCod());
//            for (ServiceValueDetail svd : dets) {
//                OtDetail det = new OtDetail();
//                det.setDate(svd.getRecorddateDat());
//                det.setEventCod(svd.getColumn1Chr());
//                DataStatus status = otFacade.getDataStatusByCode(svd.getColumn1Chr(), getClient().getClientCod());
//                det.setEventDesc(status != null ? status.getDescripcion() : "");
//                det.setObs(svd.getColumn2Chr());
//                getDetalles().add(det);
//            }
//        }
//        
//        
//        return retVal;
//    }
    
    
    @Override
	public PaginationHelper getPaginationHelper() {
        if (paginationHelper == null) {
            int pageSize = getRowQuantSelected().length() > 0 ? Integer.valueOf(
                    getRowQuantSelected()).intValue() : 0;

            paginationHelper = new PaginationHelper(pageSize) {
                Integer count = null;

                @Override
                public int getItemsCount() {
                    if (count == null) {
                        Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
                        
                        List<Classification> classifications = classificationFacade.findByUserwebWithChilds(getUserweb());
                        String where = " WHERE o.enabledChr = true AND o.column9Chr = {0} AND o.service.serviceCod = {1} ";
                        where = MessageFormat.format(where,
                        		"'" + clientCod.toString() + "'",
                                String.valueOf(getCodService()));      
                        where = where.concat(getCabDetailWhereCriteria());

                        where += " AND ( (EXISTS ( SELECT distinct u FROM Userphone u , IN (u.classificationList) cl  WHERE  cl in (:classifications)   " +
          		 	     	  " AND u.enabledChr = true and o.userphone.userphoneCod = u.userphoneCod ) AND o.column7Chr <> '0') " +
          		 	          " OR (o.column7Chr = '0')) " ;
                        
                        count = facade.count(where, classifications);
                    }

                    return count.intValue();
                }

                @Override
                public DataModel createPageDataModel() {
                    Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
                    
                    List<Classification> classifications = classificationFacade.findByUserwebWithChilds(getUserweb());
                    String where = " WHERE o.enabledChr = true AND o.column9Chr = {0} AND o.service.serviceCod = {1} ";
                    where = MessageFormat.format(where, "'" + clientCod.toString() + "'",
                            String.valueOf(getCodService()));
                    where = where.concat(getCabDetailWhereCriteria());

                    
                    where += " AND ( (EXISTS ( SELECT distinct u FROM Userphone u , IN (u.classificationList) cl  WHERE  cl in (:classifications)   " +
        		 	     	  " AND u.enabledChr = true and o.userphone.userphoneCod = u.userphoneCod ) AND o.column7Chr <> '0') " +
        		 	          " OR (o.column7Chr = '0')) " ;
                    
                    if (sortHelper.getField() != null && (sortHelper.getField().equalsIgnoreCase("sv.recorddateDat") || sortHelper.getField().equalsIgnoreCase("recorddateDat")))
                    	sortHelper.setField("servicevalueCod");
                    
                    String orderby = "o.".concat(sortHelper.getField()).concat(sortHelper.isAscending() ? " ASC" : " DESC");
                    
                    return new ListDataModelViewCsTigo(facade.findRange(
                            new int[] { getPageFirstItem(), getPageFirstItem()
                                + getPageSize() }, where, orderby,
                            classifications));
                }
            };
        }

        return paginationHelper;
    }
    
    
//    @Override
//    public PaginationHelper getPaginationHelper() {
//        if (paginationHelper == null) {
//            int pageSize = getRowQuantSelected().length() > 0 ? Integer.valueOf(getRowQuantSelected()).intValue() : 0;
//
//            paginationHelper = new PaginationHelper(pageSize) {
//
//                String innerWhere = null;
//                Integer count = null;
//
//                @Override
//                public int getItemsCount() {
//
//                    String where = null;
//                    if (!filterSelectedField.equals("-1") && getFilterCriteria().length() > 0) {
//                        Class<?> fieldClass = getFieldType(filterSelectedField);
//
//                        if (fieldClass.equals(Integer.class) || fieldClass.equals(Long.class) || fieldClass.equals(BigInteger.class)) {
//                            where = "where o.".concat(filterSelectedField).concat(" = ").concat(getFilterCriteria());
//                        } else if (fieldClass.equals(String.class)) {
//                            where = "where lower(o.".concat(filterSelectedField).concat(") LIKE '%").concat(getFilterCriteria().toLowerCase()).concat("%'");
//                        }
//                    }
//                    if (getAditionalFilter() != null && getAditionalFilter().trim().length() > 0) {
//                        if (where == null) {
//                            where = "where ";
//                        } else {
//                            where = where.concat(" AND ");
//                        }
//                        where = where.concat(" (").concat(getAditionalFilter().trim()).concat(") ");
//                    }
//                    
//                    
//                    List<Classification> classifications = classificationFacade.findByUserwebWithChilds(SecurityBean.getInstance().getLoggedInUserClient());
//					if (where == null) {
//						where = "where ";
//					} else {
////						where = where.concat(" AND");
//					}
//                        //where = where.concat(" o.client.clientCod = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
//                            
////                        where += "  EXISTS ( SELECT distinct u FROM Userphone u , IN (u.classificationList) cl WHERE  cl in (:classifications) " +
////                        		 "  AND u.enabledChr = true and o.codUserphone = u.userphoneCod )";
//                    
//					where += getWhereCriteria();
//                    where += " AND ( (EXISTS ( SELECT distinct u FROM Userphone u , IN (u.classificationList) cl  WHERE  cl in (:classifications)   " +
//      		 	     	  " AND u.enabledChr = true and o.codUserphone = u.userphoneCod ) AND o.statusCod <> '0') " +
//      		 	          " OR (o.statusCod = '0')) " ;
//
//                    /*
//                     * esto se verifica para no ejecutar la sentencia cada vez
//                     * que se solicita la cantidad de registros a mostrar, se
//                     * retorna el valor ya en memoria si la sentencia no cambio.
//                     * Esto se realiza para no realizar mas de una vez la
//                     * consulta a la base de datos
//                     */
//
//                    if (innerWhere == null) {
//                        innerWhere = where;
//                    } else {
//                        if (innerWhere.compareTo(where) == 0) {
//                            return count;
//                        }
//                    }
//                    if (classifications != null)
//                        count = otFacade.count(where, classifications);
//                    else
//                        count = otFacade.count(where);
//                    return count;
//                }
//
//                @Override
//                public DataModel createPageDataModel() {
//                    String where = null;
//                    if (!filterSelectedField.equals("-1") && getFilterCriteria().length() > 0) {
//                        Class<?> fieldClass = getFieldType(filterSelectedField);
//
//                        if (fieldClass.equals(Integer.class) || fieldClass.equals(Long.class) || fieldClass.equals(BigInteger.class)) {
//                            where = "where o.".concat(filterSelectedField).concat(" = ").concat(getFilterCriteria());
//                        } else if (fieldClass.equals(String.class)) {
//                            where = "where lower(o.".concat(filterSelectedField).concat(") LIKE '%").concat(getFilterCriteria().toLowerCase()).concat("%'");
//                        }
//                    }
//                    if (getAditionalFilter() != null && getAditionalFilter().trim().length() > 0) {
//                        if (where == null) {
//                            where = "where ";
//                        } else {
//                            where = where.concat(" AND");
//                        }
//                        where = where.concat(" (").concat(getAditionalFilter().trim()).concat(") ");
//                    }
//
//                    List<Classification> classifications = classificationFacade.findByUserwebWithChilds(SecurityBean.getInstance().getLoggedInUserClient());
//                    if (where == null) {
//                    	where = "where ";
//                    } else {
////                    	where = where.concat(" AND");
//                        //where = where.concat(" o.client.clientCod = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
//                            
////                        where += "  EXISTS ( SELECT distinct u FROM Userphone u , IN (u.classificationList) cl WHERE  cl in (:classifications) " +
////                                "  AND u.enabledChr = true and o.codUserphone = u.userphoneCod )";
//                    }
//                    
//                    where += getWhereCriteria();
//                    where += " AND ( (EXISTS ( SELECT distinct u FROM Userphone u , IN (u.classificationList) cl  WHERE  cl in (:classifications)   " +
//                    		 	     	  " AND u.enabledChr = true and o.codUserphone = u.userphoneCod ) AND o.statusCod <> '0') " +
//                    		 	" OR (o.statusCod = '0')) " ;
//
//                    String orderby = "o.".concat(getSortHelper().getField()).concat(getSortHelper().isAscending() ? " ASC" : " DESC");
//
//                    return new ListDataModelViewCsTigo(otFacade.findRange(new int[] { getPageFirstItem(), getPageFirstItem() + getPageSize() }, where, orderby, classifications));
//                }
//            };
////            if (getLastActualPage() >= 0) {
////                paginationHelper.setActualPage(getLastActualPage());
////                setLastActualPage(-1);
////            }
//        }
//
//        return paginationHelper;
//    }
    
    protected Class<?> getFieldType(String fieldName) {
        try {
            String internalFieldName = "";
            if (fieldName.indexOf(".") >= 0) {
                internalFieldName = fieldName.substring(fieldName.indexOf(".") + 1);
                fieldName = fieldName.substring(0, fieldName.indexOf("."));
            }
            String methodName = "get".concat(
                    fieldName.substring(0, 1).toUpperCase()).concat(
                    fieldName.substring(1));

            if (internalFieldName.isEmpty()) {
                return ServiceValue.class.getMethod(methodName, new Class[0]).getReturnType();
            }

            Class<?> internalClass = ServiceValue.class.getMethod(methodName,
                    new Class[0]).getReturnType();
            methodName = "get".concat(
                    internalFieldName.substring(0, 1).toUpperCase()).concat(
                    internalFieldName.substring(1));

            return internalClass.getMethod(methodName, new Class[0]).getReturnType();
        } catch (Exception e) {
            return Object.class;
        }
    }    
    
    public DataClient getSelectedDataClient() {
        return selectedDataClient;
    }

    public void setSelectedDataClient(DataClient selectedDataClient) {
        this.selectedDataClient = selectedDataClient;
    }

//    @Override
//    public String applyFilter() {
//        if (getFilterSelectedField().equals("-1") && dateFrom == null && dateTo == null) {
//            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectFindOption"));            
//        } else {
//            setPaginationHelper(null); // For pagination recreation
//            setDataModelHeader(null); // For data model recreation
//            setSelectedItems(null); // For clearing selection
//        }
//        
//       paginationHelper = null;
//              
//       return null;
//    }
    
    @Override
    public String cleanFilter() {
        String retVal = super.cleanFilter();
        dateFrom = null;
        dateTo = null;
        paginationHelper = null;
        setAditionalFilter("o.codClient = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
        return retVal;
    }

    public Client getClient() {
		if (client == null) {
			client = SecurityBean.getInstance().getLoggedInUserClient().getClient();
		}
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
	
	public String getWhereCriteria() {

        String whereCriteria = "";

        if (dateFrom != null) {
            try {
                String dateFromStr = getSdFormat().format(dateFrom);

				whereCriteria += " AND o.createdDate >= to_date('"
						+ dateFromStr + "', 'dd/MM/yyyy hh24:mi:ss') ";
                
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }

        if (dateTo != null) {
            try {
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(dateTo);
                gc.set(Calendar.HOUR_OF_DAY, 23);
                gc.set(Calendar.MINUTE, 59);
                String dateToStr = getSdFormat().format(gc.getTime());
                
				whereCriteria += " AND o.createdDate <= to_date('" + dateToStr + "', 'dd/MM/yyyy hh24:mi:ss') ";
               
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }

        return whereCriteria;
    }
    
    /* ******************************************************************* */
    /* ***********************REPORTES *********************************** */
//    @Override
//    public String generateReport(ReportType reportType, String reportName) {
//
//        Map<Object, Object> params = new HashMap<Object, Object>();
//        params.put("WHERE", getWhereReport());
//        params.put("ORDER_BY", getOrderByReport());
//        params.put("USER",SecurityBean.getInstance().getLoggedInUserClient().getNameChr());
//        params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
//        params.put(JRParameter.REPORT_RESOURCE_BUNDLE, i18n.getResourceBundle());
//
//        ClientFile cf = clientFileFacade.getClientLogo(getClient());
//        try {
//            if (cf != null) {
//                params.put("CLIENT_LOGO",
//                        JRImageLoader.loadImage(cf.getFileByt()));
//            }
//        } catch (JRException ex) {
//        }
//        Connection conn = SMBaseBean.getDatabaseConnecction();
//        JasperReportUtils.respondReport(reportName, params, true, conn,
//                reportType);
//        return null;
//    }
    
    private String getFilterSelectedFieldSql(String filterSelectedField){
    	String result = "";
    	for (int i = 0; i < filterSelectedField.length(); i++) {
    		if(Character.isUpperCase(filterSelectedField.charAt(i))) {
                result += "_" + String.valueOf(filterSelectedField.charAt(i)).toLowerCase();
            }else
            	result += String.valueOf(filterSelectedField.charAt(i));
		}
    	return result;
    }
    
//    @Override
//	public String getCabDetailReportWhere() {
//        String where = null;
//        Long clientCod = getClient().getClientCod();
//        where = " AND o.COD_CLIENT = ".concat(clientCod.toString());
//        
//        if (!getFilterSelectedField().equals("-1") && getFilterCriteria().length() > 0) {
//            Class<?> fieldClass = getFieldType(getFilterSelectedField());
//
//            if (fieldClass.equals(Integer.class) || fieldClass.equals(Long.class) || fieldClass.equals(BigInteger.class)) {
//                where = " AND o.".concat(getFilterSelectedFieldSql(getFilterSelectedField())).concat(" = ").concat(getFilterCriteria());
//            } else if (fieldClass.equals(String.class)) {
//                where = " AND lower(o.".concat(getFilterSelectedFieldSql(getFilterSelectedField())).concat(") LIKE '%").concat(getFilterCriteria().toLowerCase()).concat("%'");
//            }
//        }
//        
//
//        String classifications = classificationFacade.getClassificationUserwebString(SecurityBean.getInstance().getLoggedInUserClient());
//		where += MessageFormat.format(" AND ( (EXISTS (select * from USERPHONE_CLASSIFICATION uc where uc.cod_userphone = u.userphone_cod and uc.cod_classification in ({0})) " +
//							" AND o.STATUS_COD <> '0') " +
//							" OR (o.STATUS_COD = '0') )", classifications);
//
//        if (dateFrom != null) {
//            try {
//				String dateFromStr = sdFormat.format(dateFrom);
//				where += " AND o.CREATED_DATE >= to_date('" + dateFromStr
//						+ "', 'dd/MM/yyyy hh24:mi') ";
//                
//            } catch (Exception e) {
//            }
//        }
//
//        if (dateTo != null) {
//            try {
//                GregorianCalendar gc = new GregorianCalendar();
//                gc.setTime(dateTo);
//                gc.set(Calendar.HOUR_OF_DAY, 23);
//                gc.set(Calendar.MINUTE, 59);
//                String dateToStr = sdFormat.format(gc.getTime());
//				where += " AND o.CREATED_DATE <= to_date('" + dateToStr
//						+ "', 'dd/MM/yyyy hh24:mi') ";
//                
//            } catch (Exception e) {
//            }
//        }
//
//        return where;
//    }
    
    @Override
	public String getCabDetailReportWhere() {
        Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();

        String where = " AND SV.ENABLED_CHR = '1' AND SV.COLUMN9_CHR = '".concat(
                clientCod.toString()).concat("' AND SV.COD_SERVICE = ").concat(
                getService().getServiceCod().toString());

        where = where.concat(getCabDetailReportWhereCriteria());
        return where;
    }
    
    @Override
	public String getCabDetailReportWhereCriteria() {
        if ((usermobileName == null || usermobileName.isEmpty())
            && mobilePhoneNumber == null && selectedUserphone == null
            && dateFrom == null && dateTo == null) {
            return " AND 1 = 2 ";
        }
        String where = "";

        if (usermobileName != null && !usermobileName.isEmpty()) {
            where += " AND lower (U.name_Chr) LIKE '%".concat(
                    usermobileName.toLowerCase()).concat("%'");
        }
        if (selectedUserphone != null) {
            where += " AND U.USERPHONE_COD = ".concat(selectedUserphone.getUserphoneCod().toString());
        }

        if (mobilePhoneNumber != null) {
            where += " AND U.CELLPHONE_NUM = ".concat(mobilePhoneNumber.toString());
        }
        
        if (dateFrom != null) {
            where += " AND trunc(TO_DATE(SV.COLUMN5_CHR,'dd/MM/yyyy hh24:mi'))  >= ".concat("trunc(TO_DATE('"+ getSdFormat().format(dateFrom)).concat("', 'dd/MM/yyyy hh24:mi'))");
        }
        if (dateTo != null) {
            where += " AND trunc(TO_DATE(SV.COLUMN5_CHR,'dd/MM/yyyy hh24:mi')) <= ".concat("trunc(TO_DATE('"+ getSdFormat().format(dateTo)).concat("', 'dd/MM/yyyy hh24:mi'))");
        }
        
        String classifications = classificationFacade.getClassificationUserwebString(SecurityBean.getInstance().getLoggedInUserClient());
		where += MessageFormat.format(" AND ( (EXISTS (select * from USERPHONE_CLASSIFICATION uc where uc.cod_userphone = u.userphone_cod and uc.cod_classification in ({0})) " +
							" AND sv.COLUMN7_CHR <> '0') " +
							" OR (sv.COLUMN7_CHR = '0') )", classifications);


        return where;

    }
    
    
    @Override
	public String getCabDetailReportOrderBy() {
        String sortAttributeColumnName = getAttributeColumnName(ServiceValue.class, getSortHelper().getField());
        if (getSortHelper().getField().indexOf(".") < 0) {
            sortAttributeColumnName = "sv.".concat(sortAttributeColumnName);
        } else {
            sortAttributeColumnName = "svd.".concat(sortAttributeColumnName);
        }
        // provisorio
        if (sortAttributeColumnName != null && sortAttributeColumnName.equalsIgnoreCase("sv.recorddate_dat"))
        	sortAttributeColumnName = "sv.SERVICEVALUE_COD";
        return "ORDER BY ".concat(sortAttributeColumnName).concat(
                getSortHelper().isAscending() ? " ASC" : " DESC").concat(", svd.SERVICEVALUEDETAIL_COD ASC");
    }
    
//    public String getOrderByReport() {
//        String sortAttributeColumnName = getAttributeColumnName(getSortHelper().getField());
//        if (getSortHelper().getField().indexOf(".") < 0) {
//            sortAttributeColumnName = "o.".concat(sortAttributeColumnName);
//        }
//        return "ORDER BY ".concat(sortAttributeColumnName).concat(
//                getSortHelper().isAscending() ? " ASC" : " DESC").concat(", svd.RECORDDATE_DAT ASC");
//    }    
    
    
    
    //MAPA
//    protected MapModel mapModel;
//    protected LatLng mapCenter;
//    protected Integer mapZoom;
//    protected String mapType;
//    private Marker selectedMarker;
//    private boolean clientMarker = false;
//    private boolean editMarker = false;
//    private String clientMarkerDesc;
//    private String clientMarkerTitle;
//    private LatLngBounds lastBounds;
//    protected Boolean geolocalizationAllowed = null;
//    protected boolean showMapOnDetail = false;
//
//    @EJB
//    private MapMarkFacade mapMarksFacade;
//    @EJB
//    private ServiceValueDetailFacade serviceValueDetailFacade;
//    @EJB
//    protected UserwebFacade userwebFacade;
//    @EJB
//    protected DataStatusFacade dataStatusFacade;
    
    
    @Override
	public String showDetailMap() {
    	/*Verificamos si el usuario web tiene el permiso de geolocalizacion para el servicio OT*/
        if (getGeolocalizationAllowed()) {
            Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();

            /*Creamos un objeto de tipo MapModel de primefaces*/
            mapModel = new DefaultMapModel();
            boolean oneInsideBounds = false;
            
            /*Recorremos la lista para verificar que solo se haya seleccionado un registro*/
            setServiceValue(null);
            Iterator<ServiceValue> iterator = getDataModelHeader().iterator();
            while (iterator.hasNext()) {
                ServiceValue currServiceValue = iterator.next();
                if (getSelectedItems().get(currServiceValue.getServicevalueCod())) {
                    if (getServiceValue() == null) {
                        setServiceValue(facade.find(currServiceValue.getServicevalueCod()));
                    } else {
                    	setServiceValue(null);
                        setWarnMessage(i18n.iValue("web.client.backingBean.abstractServiceBean.message.MustSelectJustOne"));
                        return null;
                    }
                }
            }
            if (getServiceValue() == null) {
                setWarnMessage(i18n.iValue("web.client.backingBean.abstractServiceBean.message.MustSelectOne"));
                return null;
            }

            /*Recuperamos todos los serviceValueDetail del codigo de servicevalue del registro de OT seleccionado*/
            List<ServiceValueDetail> serviceValueList = facadeDetail.findByServiceValue(getServiceValue().getServicevalueCod());
            
            if(serviceValueList == null) {
            	serviceValueList = new ArrayList<ServiceValueDetail>();
            }
            
            Iterator<ServiceValueDetail> iteratorDetail = serviceValueList.iterator();
            int nextMarkerCounter = 1;
            setShowMapOnDetail(true);

           /*Recorremos la lista de serviceValueDetail*/
           while (iteratorDetail.hasNext()) {
                ServiceValueDetail currServiceDetailValue = iteratorDetail.next();
                /*Verificamos que el serviceValueDetail no tenga nulo en el campo message, de lo contarario no es un evento localizable o no se pudo localizar*/
                if (currServiceDetailValue.getMessage()!= null &&
                	(currServiceDetailValue.getMessage().getCell() != null || (currServiceDetailValue.getMessage().getLatitude() != null && currServiceDetailValue.getMessage().getLongitude() != null))) {
                    // Obtain latitude, longitude and azimuth from cell
                    // Message for marker
                	
                	/*Traemos la descripcion a mostrar en el mapa por cada punto localizable*/
                    String messDescrip = getMessageDescriptionDetailMap(currServiceDetailValue);
                    Polygon polygonArea = null;
                    Marker markerArea = null;
                    
                    /*Recuperamos la latitude y longitude de la tabla message*/
                    Double latitude = currServiceDetailValue.getMessage().getLatitude();
                    Double longitude = currServiceDetailValue.getMessage().getLongitude();

                    if (latitude != null && longitude != null) {
                        markerArea = getGPSCellAreaMarker(latitude, longitude,
                                messDescrip, String.valueOf(nextMarkerCounter));
                    } else {
                        latitude = currServiceDetailValue.getMessage().getCell().getLatitudeNum().doubleValue();
                        longitude = currServiceDetailValue.getMessage().getCell().getLongitudeNum().doubleValue();
                        double azimuth = currServiceDetailValue.getMessage().getCell().getAzimuthNum().doubleValue();
                        String siteCell = currServiceDetailValue.getMessage().getCell().getSiteChr();

                        if (!siteCell.toUpperCase().endsWith("O")) {
                            // SEGMENTED CELL

                            // Cell polygon
                            polygonArea = getCellAreaPolygon(latitude,
                                    longitude, azimuth);

                            // Cell marker
                            markerArea = getCellAreaMarker(latitude, longitude,
                                    azimuth, messDescrip,
                                    String.valueOf(nextMarkerCounter));
                        } else {
                            // OMNIDIRECTIONAL CELL

                            // Cell polygon
                            polygonArea = getOmniCellAreaPolygon(latitude,
                                    longitude);

                            // Cell marker
                            markerArea = getOmniCellAreaMarker(latitude,
                                    longitude, messDescrip,
                                    String.valueOf(nextMarkerCounter));
                        }

                        // Add the polygon
                        if (polygonArea != null) {
                            // if the polygon already exists, do not add.
                            Polygon existingPolygon = null;
                            for (int i = 0; i < getMapModel().getPolygons().size(); i++) {
                                if (getMapModel().getPolygons().get(i).getPaths().equals(
                                        polygonArea.getPaths())) {
                                    existingPolygon = getMapModel().getPolygons().get(
                                            i);
                                    break;
                                }
                            }

                            if (existingPolygon == null) {
                                getMapModel().addOverlay(polygonArea);
                            }
                        }

                    }// FIN DEL IF

                    // Add the marker
                    if (markerArea != null) {
                        // if the marker already exists, do not add, but instead
                        // add data to existing one.
                        Marker existingMarker = null;
                        for (int i = 0; i < getMapModel().getMarkers().size(); i++) {
                            if (getMapModel().getMarkers().get(i).getLatlng().equals(
                                    markerArea.getLatlng())) {
                                existingMarker = getMapModel().getMarkers().get(
                                        i);
                                break;
                            }
                        }

                        if (existingMarker == null) {
                            getMapModel().addOverlay(markerArea);
                            nextMarkerCounter++;
                            if (getLastBounds() != null) {
                                boolean inside = markerArea.getLatlng().getLat() > getLastBounds().getSouthWest().getLat();
                                inside = inside
                                    && markerArea.getLatlng().getLat() < getLastBounds().getNorthEast().getLat();
                                inside = inside
                                    && markerArea.getLatlng().getLng() > getLastBounds().getSouthWest().getLng();
                                inside = inside
                                    && markerArea.getLatlng().getLng() < getLastBounds().getNorthEast().getLng();

                                if (inside) {
                                    oneInsideBounds = true;
                                }
                            }
                        } else {
                            existingMarker.setData(((String) existingMarker.getData()).concat(
                                    "<br>").concat(messDescrip));
                        }
                    }
                }
            }
            // Add polyline
            if (getMapModel().getMarkers().size() > 0) {
                if (!oneInsideBounds) {
                    mapCenter = mapModel.getMarkers().get(0).getLatlng();
                }
                if (getMapModel().getMarkers().size() > 1) {
                    getMapModel().addOverlay(
                            getMarkersPolyline(getMapModel().getMarkers()));
                }
            }
        }
        addClientMarker();
//        setServiceValue(null);
        return null;
    }
    
    
    @Override
	public String showHeaderMap() {
    	/*Verificamos si el usuario web tiene el permiso de geolocalizacion para el servicio OT*/
        if (getGeolocalizationAllowed()) {
            Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();

            /*Creamos un objeto de tipo MapModel de primefaces*/
            mapModel = new DefaultMapModel();
            boolean oneInsideBounds = false;
            
            /*Recorremos la lista para verificar que solo se haya seleccionado un registro*/
            setServiceValue(null);
            Iterator<ServiceValue> iterator = getDataModelHeader().iterator();
            while (iterator.hasNext()) {
                ServiceValue currServiceValue = iterator.next();
                if (getSelectedItems().get(currServiceValue.getServicevalueCod())) {
                    if (getServiceValue() == null) {
                        setServiceValue(facade.find(currServiceValue.getServicevalueCod()));
                    } else {
                    	setServiceValue(null);
                        setWarnMessage(i18n.iValue("web.client.backingBean.abstractServiceBean.message.MustSelectJustOne"));
                        return null;
                    }
                }
            }
            if (getServiceValue() == null) {
                setWarnMessage(i18n.iValue("web.client.backingBean.abstractServiceBean.message.MustSelectOne"));
                return null;
            }

            /*Recuperamos todos los serviceValueDetail del codigo de servicevalue del registro de OT seleccionado*/
            List<ServiceValueDetail> serviceValueList = facadeDetail.findByServiceValue(getServiceValue().getServicevalueCod());
            
            if(serviceValueList == null) {
            	serviceValueList = new ArrayList<ServiceValueDetail>();
            }
            
            Iterator<ServiceValueDetail> iteratorDetail = serviceValueList.iterator();
            int nextMarkerCounter = 1;
            setShowMapOnDetail(true);

           /*Recorremos la lista de serviceValueDetail*/
           while (iteratorDetail.hasNext()) {
                ServiceValueDetail currServiceDetailValue = iteratorDetail.next();
                /*Verificamos que el serviceValueDetail no tenga nulo en el campo message, de lo contarario no es un evento localizable o no se pudo localizar*/
                if (currServiceDetailValue.getMessage()!= null &&
                	(currServiceDetailValue.getMessage().getCell() != null || (currServiceDetailValue.getMessage().getLatitude() != null && currServiceDetailValue.getMessage().getLongitude() != null))) {
                    // Obtain latitude, longitude and azimuth from cell
                    // Message for marker
                	
                	/*Traemos la descripcion a mostrar en el mapa por cada punto localizable*/
                    String messDescrip = getMessageDescriptionDetailMap(currServiceDetailValue);
                    Polygon polygonArea = null;
                    Marker markerArea = null;
                    
                    /*Recuperamos la latitude y longitude de la tabla message*/
                    Double latitude = currServiceDetailValue.getMessage().getLatitude();
                    Double longitude = currServiceDetailValue.getMessage().getLongitude();

                    if (latitude != null && longitude != null) {
                        markerArea = getGPSCellAreaMarker(latitude, longitude,
                                messDescrip, String.valueOf(nextMarkerCounter));
                    } else {
                        latitude = currServiceDetailValue.getMessage().getCell().getLatitudeNum().doubleValue();
                        longitude = currServiceDetailValue.getMessage().getCell().getLongitudeNum().doubleValue();
                        double azimuth = currServiceDetailValue.getMessage().getCell().getAzimuthNum().doubleValue();
                        String siteCell = currServiceDetailValue.getMessage().getCell().getSiteChr();

                        if (!siteCell.toUpperCase().endsWith("O")) {
                            // SEGMENTED CELL

                            // Cell polygon
                            polygonArea = getCellAreaPolygon(latitude,
                                    longitude, azimuth);

                            // Cell marker
                            markerArea = getCellAreaMarker(latitude, longitude,
                                    azimuth, messDescrip,
                                    String.valueOf(nextMarkerCounter));
                        } else {
                            // OMNIDIRECTIONAL CELL

                            // Cell polygon
                            polygonArea = getOmniCellAreaPolygon(latitude,
                                    longitude);

                            // Cell marker
                            markerArea = getOmniCellAreaMarker(latitude,
                                    longitude, messDescrip,
                                    String.valueOf(nextMarkerCounter));
                        }

                        // Add the polygon
                        if (polygonArea != null) {
                            // if the polygon already exists, do not add.
                            Polygon existingPolygon = null;
                            for (int i = 0; i < getMapModel().getPolygons().size(); i++) {
                                if (getMapModel().getPolygons().get(i).getPaths().equals(
                                        polygonArea.getPaths())) {
                                    existingPolygon = getMapModel().getPolygons().get(
                                            i);
                                    break;
                                }
                            }

                            if (existingPolygon == null) {
                                getMapModel().addOverlay(polygonArea);
                            }
                        }

                    }// FIN DEL IF

                    // Add the marker
                    if (markerArea != null) {
                        // if the marker already exists, do not add, but instead
                        // add data to existing one.
                        Marker existingMarker = null;
                        for (int i = 0; i < getMapModel().getMarkers().size(); i++) {
                            if (getMapModel().getMarkers().get(i).getLatlng().equals(
                                    markerArea.getLatlng())) {
                                existingMarker = getMapModel().getMarkers().get(
                                        i);
                                break;
                            }
                        }

                        if (existingMarker == null) {
                            getMapModel().addOverlay(markerArea);
                            nextMarkerCounter++;
                            if (getLastBounds() != null) {
                                boolean inside = markerArea.getLatlng().getLat() > getLastBounds().getSouthWest().getLat();
                                inside = inside
                                    && markerArea.getLatlng().getLat() < getLastBounds().getNorthEast().getLat();
                                inside = inside
                                    && markerArea.getLatlng().getLng() > getLastBounds().getSouthWest().getLng();
                                inside = inside
                                    && markerArea.getLatlng().getLng() < getLastBounds().getNorthEast().getLng();

                                if (inside) {
                                    oneInsideBounds = true;
                                }
                            }
                        } else {
                            existingMarker.setData(((String) existingMarker.getData()).concat(
                                    "<br>").concat(messDescrip));
                        }
                    }
                }
            }
            // Add polyline
            if (getMapModel().getMarkers().size() > 0) {
                if (!oneInsideBounds) {
                    mapCenter = mapModel.getMarkers().get(0).getLatlng();
                }
                if (getMapModel().getMarkers().size() > 1) {
                    getMapModel().addOverlay(
                            getMarkersPolyline(getMapModel().getMarkers()));
                }
            }
        }
        addClientMarker();
        setServiceValue(null);
        return null;
    }
    
    
    
    // MAPS
    @Override
	public MapModel getMapModel() {
        if (mapModel == null) {
            mapModel = new DefaultMapModel();
            mapCenter = ApplicationBean.getInstance().getMapStartingPoint();
            mapZoom = Integer.parseInt(ApplicationBean.getInstance().getMapStartingZoom());
            mapType = ApplicationBean.getInstance().getMapStartingType();
        }
        return mapModel;
    }

    @Override
	public void setMapModel(MapModel mapModel) {
        this.mapModel = mapModel;
    }

    @Override
	public String getMapCenterStr() {
        if (mapCenter != null) {
            return mapCenter.getLat() + "," + mapCenter.getLng();
        }
        return "0,0";
    }

    @Override
	public LatLng getMapCenter() {
        return mapCenter;
    }

    @Override
	public void setMapCenter(LatLng mapCenter) {
        this.mapCenter = mapCenter;
    }

    @Override
	public Integer getMapZoom() {
        return mapZoom;
    }

    @Override
	public void setMapZoom(Integer mapZoom) {
        this.mapZoom = mapZoom;
    }

    @Override
	public String getMapType() {
        return mapType;
    }

    @Override
	public void setMapType(String mapType) {
        this.mapType = mapType;
    }
    

    @Override
	public void onPointSelect(PointSelectEvent event) {
        LatLng latlng = event.getLatLng();
        ClientMarker nuevaMarca = new ClientMarker(latlng,urlIconDefault);
        mapModel.addOverlay(nuevaMarca);
    }

    
    @Override
	public String getMessageDescriptionDetailMap(ServiceValueDetail svd) {
        DateFormat sdf = new SimpleDateFormat(ApplicationBean.getInstance().getDefaultOutputDateTimeFormat());
        String messDescrip = svd.getMessage().getUserphone().getNameChr().concat(
                " | ").concat(sdf.format(svd.getMessage().getDateinDat()));
        /*Le concatenamos a la descripcion el estado del evento de la OT*/
        //messDescrip  = messDescrip.concat(" | Estado: "+ dataStatusFacade.findByCodigo(svd.getColumn1Chr()).getDescripcion());
        messDescrip = messDescrip.concat(" | Estado: "+ dataStatusFacade.findEntityByClientMetaCodigo(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), 
        																 META_STATUS,
        																 svd.getColumn1Chr()));
        return messDescrip;
    }

	@Override
	public String getDetailWhereCriteria() {
		return "";
	}

	@Override
	public String getDetailReportWhereCriteria() {
		return "";
	}	
	
	
	@Override
	public String viewDetails() {
        setServiceValue(null);
        Iterator<ServiceValue> iterator = getDataModelHeader().iterator();
        while (iterator.hasNext()) {
            ServiceValue currServiceValue = iterator.next();
            if (getSelectedItems().get(currServiceValue.getServicevalueCod())) {
                if (getServiceValue() == null) {
                    setServiceValue(facade.find(currServiceValue.getServicevalueCod()));
                } else {
                	setServiceValue(null);
                    setWarnMessage(i18n.iValue("web.client.backingBean.abstractServiceBean.message.MustSelectJustOne"));
                    return null;
                }
            }
        }
        if (getServiceValue() == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractServiceBean.message.MustSelectOne"));
            return null;
        }

        // Initialize sort with default values
        sortHelperDetail = new SortHelper();
        try {
            sortHelperDetail.setField(getPrimarySortedFieldDetail());
            sortHelperDetail.setAscending(primarySortedFieldDetailAsc);
        } catch (PrimarySortedFieldNotFoundException ex) {
            java.util.logging.Logger.getLogger(
                    AbstractServiceBean.class.getName()).log(
                    Level.SEVERE,
                    i18n.iValue("web.client.backingBean.message.Error")
                        + ex.getMessage(), ex);
        }

        paginationHelperDetail = null;
        setDataModelDetail(getPaginationHelperDetail().createPageDataModel());
//        if (geolocalizationAllowed && showMapOnHeader) {
//            showHeaderMap();
//            addClientMarker();
//        }
        if (geolocalizationAllowed && showMapOnDetail) {
            getMapModel();
            addClientMarker();
        }
        /*
         * se mapean los metadatos del modelo
         */
        detailMetaDataFromModel();

        return null;
    }
	
	@Override
	public String cancelDetail() {
		if (geolocalizationAllowed && showMapOnHeader) {
            mapModel = null;
            getMapModel();
            addClientMarker();
        }
        selectedDetailItems = null;
        setServiceValue(null);
        setServiceValueDetail(null);
        setDataModelDetail(null);
        return null;
    }
}
