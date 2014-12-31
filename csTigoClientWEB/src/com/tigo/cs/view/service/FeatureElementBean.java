package com.tigo.cs.view.service;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.persistence.Column;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.util.JRImageLoader;

import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.event.map.StateChangeEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.LatLngBounds;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Polygon;
import org.primefaces.model.map.Polyline;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.PrimarySortedFieldNotFoundException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.report.JasperReportUtils;
import com.tigo.cs.commons.report.ReportType;
import com.tigo.cs.commons.util.GeoUtil;
import com.tigo.cs.commons.util.ListDataModelViewCsTigo;
import com.tigo.cs.commons.util.PaginationHelper;
import com.tigo.cs.commons.util.SortHelper;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.ClientFile;
import com.tigo.cs.domain.FeatureElement;
import com.tigo.cs.domain.FeatureValue;
import com.tigo.cs.domain.FeatureValueData;
import com.tigo.cs.domain.MapMark;
import com.tigo.cs.domain.Phone;
import com.tigo.cs.domain.PhoneList;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.facade.ClassificationFacade;
import com.tigo.cs.facade.ClientFileFacade;
import com.tigo.cs.facade.FeatureElementFacade;
import com.tigo.cs.facade.FeatureValueDataFacade;
import com.tigo.cs.facade.FeatureValueFacade;
import com.tigo.cs.facade.IconTypeFacade;
import com.tigo.cs.facade.MapMarkFacade;
import com.tigo.cs.facade.PhoneListFacade;
import com.tigo.cs.facade.UserwebFacade;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.ApplicationBean;
import com.tigo.cs.view.metadata.AbstractCrudBeanClient;

/**
 * 
 * @author Miguel Zorrilla
 * @version $Id$
 */
@ManagedBean(name = "featureElementBean")
@ViewScoped
public class FeatureElementBean extends AbstractCrudBeanClient<FeatureValue, FeatureValueFacade> {

	private static final long serialVersionUID = -466196929898797994L;
	public static final int COD_SERVICE = 100;
	public static final Long META_CLIENT = 1L;
	public static final Long META_MOTIVE = 3L;
	private static final String INTERNAL = "INTERNAL";
    private static final String EXTERNAL = "EXTERNAL";
    private static final String OPEN = "OPEN";
    @EJB
    protected PhoneListFacade phoneListFacade;
    @EJB
    protected ClientFileFacade fileFacade;
    @EJB
    private FeatureValueDataFacade facadeDetail;
    @EJB
    private ClassificationFacade classificationFacade;
    @EJB
    protected MapMarkFacade mapMarksFacade;
	@EJB
    private FeatureElementFacade featureElementFacade;
	@EJB
    private UserwebFacade userwebFacade;
	private Map<String, String> mapEncodingEvents = new HashMap<String, String>();
	private Map<String, String> mapClients = new HashMap<String, String>();
		
	private FeatureElement selectedFeatureElement;
	private List<FeatureElement> featureElementList;
	private Userphone selectedUserphone;
	private Phone selectedPhone;
	private List<Userphone> userphoneList;
	private List<PhoneList> phoneList;
	private List<Phone> phones;
	private String typeOption = OPEN;
	
	private Date dateFrom;
	private Date dateTo;
    private String usermobileName;
    private PaginationHelper paginationHelper;
    private PaginationHelper paginationHelperDetail;
    private DataModel<FeatureValue> dataModel;
	private DataModel<FeatureValueData> dataModelDetail;
	private SortHelper sortHelperDetail;
	private String primarySortedFieldDetail;
	private boolean primarySortedFieldDetailAsc = true;
	private FeatureValueData entityDetail;
	protected Map<Object, Boolean> selectedDetailItems;
	protected Map<Object, Boolean> selectedItems;
	
	// MAPS
    protected MapModel mapModel;
    protected LatLng mapCenter;
    protected Integer mapZoom;
    protected String mapType;
    private Marker selectedMarker;
    private boolean clientMarker = false;
    private boolean editMarker = false;
    private String clientMarkerDesc;
    private String clientMarkerTitle;
    private LatLngBounds lastBounds;
    protected Boolean geolocalizationAllowed = null;
    protected boolean showMapOnHeader = false;
    protected boolean showMapOnDetail = false;    
    // REPORTS
    private String pdfReportCabDetailName;
    private String xlsReportCabDetailName;
    private String pdfReportDetailName;
    private String xlsReportDetailName;
    protected String urlIconDefault = "http://maps.google.com/mapfiles/ms/micons/blue-dot.png";
    @EJB
    protected IconTypeFacade iconTypeFacade;
	
	public FeatureElementBean() {
	    super(FeatureValue.class, FeatureValueFacade.class);
        setPdfReportCabDetailName("rep_feature");
        setXlsReportCabDetailName("rep_feature_xls");
      setPdfReportDetailName("rep_feature_detail");
      setXlsReportDetailName("rep_feature_detail_xls");
        setDataModel(null);
        typeOption = OPEN;
        showMapOnHeader = true;
        showMapOnDetail = false;
	}
	
	@PostConstruct
    public void init() {
    	urlIconDefault = iconTypeFacade.findDefaultIcon().getUrl();
    }
	
	@Override
    public Map<Object, Boolean> getSelectedItems() {
        if (selectedItems == null) {
            selectedItems = new HashMap<Object, Boolean>();
        }

        return selectedItems;
    }

    @Override
    public void setSelectedItems(Map<Object, Boolean> selectedItems) {
        this.selectedItems = selectedItems;
    }
    
	public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public Map<String, String> getMapEncodingEvents() {
		return mapEncodingEvents;
	}

	public void setMapEncodingEvents(Map<String, String> mapEncodingEvents) {
		this.mapEncodingEvents = mapEncodingEvents;
	}

	public Map<String, String> getMapClients() {
		return mapClients;
	}

	public void setMapClients(Map<String, String> mapClients) {
		this.mapClients = mapClients;
	}

	public String viewDetails() {
        setEntity(null);
        Iterator<FeatureValue> iterator = getDataModel().iterator();
        while (iterator.hasNext()) {
            FeatureValue currValue = iterator.next();
            if (getSelectedItems().get(currValue.getFeatureValueCod())) {
                if (getEntity() == null) {
                    setEntity(getFacade().find(currValue.getFeatureValueCod()));
                } else {
                    setEntity(null);
                    setWarnMessage(i18n.iValue("web.client.backingBean.abstractServiceBean.message.MustSelectJustOne"));
                    return null;
                }
            }
        }
        if (getEntity() == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractServiceBean.message.MustSelectOne"));
            return null;
        }

        // Initialize sort with default values
        sortHelperDetail = new SortHelper();
        try {
            sortHelperDetail.setField(getPrimarySortedFieldDetail());
            sortHelperDetail.setAscending(primarySortedFieldDetailAsc);
        } catch (PrimarySortedFieldNotFoundException ex) {
            java.util.logging.Logger.getLogger(AbstractServiceBean.class.getName()).log(Level.SEVERE, i18n.iValue("web.client.backingBean.message.Error") + ex.getMessage(), ex);
        }

        paginationHelperDetail = null;
        dataModelDetail = getPaginationHelperDetail().createPageDataModel();
        if (geolocalizationAllowed && showMapOnHeader) {
            showHeaderMap();
            addClientMarker();
        }
        if (geolocalizationAllowed && showMapOnDetail) {
            getMapModel();
            addClientMarker();
        }

        return null;
    }
	
	public String getPrimarySortedFieldDetail() throws PrimarySortedFieldNotFoundException {
        if (primarySortedFieldDetail == null) {
            Field[] fieds = FeatureValueData.class.getDeclaredFields();
            for (Field field : fieds) {
                PrimarySortedField annotation = field.getAnnotation(PrimarySortedField.class);
                if (annotation != null) {
                    primarySortedFieldDetail = field.getName();
                    primarySortedFieldDetailAsc = annotation.ascending();
                    break;
                }
            }
            if (primarySortedFieldDetail == null) {
                throw new PrimarySortedFieldNotFoundException(FeatureValueData.class);
            }
        }
        return primarySortedFieldDetail;
    }

	public String getCabDetailReportOrderBy() {
		String sortAttributeColumnName = getAttributeColumnName(FeatureValue.class, getSortHelper().getField());
		if(sortAttributeColumnName.equalsIgnoreCase("datein_dat")) {
		    return " ORDER BY ".concat(getSortHelper().isAscending() ? " fvd.COD_FEATURE_VALUE,fee.FEATURE_ELEMENT_ENTRY_COD,m.DATEIN_DAT " : " m.DATEIN_DAT DESC,fvd.COD_FEATURE_VALUE,fee.FEATURE_ELEMENT_ENTRY_COD");
		}else{
		    return " ORDER BY fvd.COD_FEATURE_VALUE,fee.FEATURE_ELEMENT_ENTRY_COD,m.DATEIN_DAT";
		}
		

	}
	
	protected String getAttributeColumnName(Class<?> entityClass, String fieldName) {
        try {
            String internalFieldName = "";
            if (fieldName.indexOf(".") >= 0) {
                internalFieldName = fieldName.substring(fieldName.indexOf(".") + 1);
                fieldName = fieldName.substring(0, fieldName.indexOf("."));
            }

            Field field = entityClass.getDeclaredField(fieldName);
            if (field != null && !internalFieldName.isEmpty()) {
                field = field.getType().getDeclaredField(internalFieldName);
            }

            if (field != null) {
                Column annotation = field.getAnnotation(Column.class);
                if (annotation != null && annotation.name() != null && !annotation.name().isEmpty()) {
                    return annotation.name();
                }
            }
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(AbstractServiceBean.class.getName()).log(Level.SEVERE, i18n.iValue("web.client.backingBean.message.Error"), e);
        }

        return null;
    }
	
	public String generateCabDetailReport(String reportName, ReportType reportType) {
        /*
         * obtenemos la informacion sobre el usuario que solicita el reporte, y
         * el cliente al que pertenece
         */
        Userweb userweb = SecurityBean.getInstance().getLoggedInUserClient();
        Client client = userweb.getClient();
        String userInformation = MessageFormat.format("{0} ({1})", userweb.getNameChr(), client.getNameChr());
        
        Map<Object, Object> params = new HashMap<Object, Object>();
        params.put("WHERE", getCabDetailReportWhere());
        params.put("ORDER_BY", getCabDetailReportOrderBy());
        params.put("USER", userInformation);
        params.put("SUBREPORT_DIR", getReportsPath().concat("/"));
        params.put("REPORT_NAME", selectedFeatureElement != null ? selectedFeatureElement.getDescriptionChr() : "");
        params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
        params.put(JRParameter.REPORT_RESOURCE_BUNDLE, i18n.getResourceBundle());

        ClientFile cf = fileFacade.getClientLogo(userweb.getClient());
        try {
            if (cf != null) {
                params.put("CLIENT_LOGO", JRImageLoader.loadImage(cf.getFileByt()));
            }
        } catch (JRException ex) {
        }

        Connection conn = SMBaseBean.getDatabaseConnecction();
        JasperReportUtils.respondReport(reportName, params, true, conn, reportType);

        return null;
    }
	
	public String viewPDFCabDetail() {
        signalReport(ReportType.PDF);
        return generateCabDetailReport(getPdfReportCabDetailName(), ReportType.PDF);
    }
	
	public String viewXLSCabDetail() {
        signalReport(ReportType.XLS);
        return generateCabDetailReport(getXlsReportCabDetailName(), ReportType.XLS);
    }
	
	public String getCabDetailReportWhere() {
	    String where = "";
	    if (dataModel != null){
	        String classifications = classificationFacade.getClassificationUserwebString(SecurityBean.getInstance().getLoggedInUserClient());
            Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();  
            
            if(selectedFeatureElement != null){
                where = " AND fe.FEATURE_ELEMENT_COD = {0} ";        
                where = MessageFormat.format(where, selectedFeatureElement.getFeatureElementCod().toString());
            }else{
                where = " AND 1=2 ";
                return where;
            }
            
            where = where.concat(" AND fe.COD_CLIENT = {0} ");        
            where = MessageFormat.format(where, clientCod.toString());
            
            where = where.concat(MessageFormat.format(" AND EXISTS (select * from FEATURE_ELEMENT_CLASSIFICATION uc where uc.COD_FEATURE_ELEMENT = fe.FEATURE_ELEMENT_COD and uc.cod_classification in ({0})) ", classifications));
            
            if (typeOption.equals(INTERNAL)){                
                where = where.concat(" AND U.COD_CLIENT = ".concat(clientCod.toString()));
                where = where.concat(MessageFormat.format(" AND EXISTS (select * from USERPHONE_CLASSIFICATION uc where uc.cod_userphone = u.userphone_cod and uc.cod_classification in ({0})) ", classifications));
            }
            
            where = where.concat(getCabDetailReportWhereCriteria());
            
	    }else{
	        where = " AND 1=2 ";
	    }
        return where;
    }
	
	public String getCabDetailReportWhereCriteria() {	    
        if (((usermobileName == null || usermobileName.isEmpty()) 
                && selectedFeatureElement == null 
                && selectedPhone == null 
                && selectedUserphone == null 
                && dateFrom == null 
                && dateTo == null) || selectedFeatureElement == null) {
            return " AND 1 = 2 ";
        }
        String where = "";

        if (usermobileName != null && !usermobileName.isEmpty()) {
            if (typeOption == INTERNAL)
                where += " AND lower (u.name_Chr) LIKE '%".concat(usermobileName.toLowerCase()).concat("%'");
            else if (typeOption == EXTERNAL)
                where += " AND lower (p.name_Chr) LIKE '%".concat(usermobileName.toLowerCase()).concat("%'");
        }
        
        if (typeOption == INTERNAL && selectedUserphone != null) {
            where += " AND u.USERPHONE_COD = ".concat(selectedUserphone.getUserphoneCod().toString());
        }

        if (typeOption == EXTERNAL && selectedPhone != null) {
            where += " AND p.PHONE_COD = ".concat(selectedPhone.getPhoneCod().toString());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm");
        if (dateFrom != null) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(dateFrom);
            gc.set(Calendar.HOUR_OF_DAY, 0);
            gc.set(Calendar.MINUTE, 0);
            gc.set(Calendar.SECOND, 0);
            where += " AND m.DATEIN_DAT >= to_date('".concat(sdf.format(gc.getTime())).concat("', 'dd-MM-yy hh24:mi ')");
        }
        if (dateTo != null) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(dateTo);
            gc.set(Calendar.HOUR_OF_DAY, 23);
            gc.set(Calendar.MINUTE, 59);
            gc.set(Calendar.SECOND, 59);
            where += " AND m.DATEIN_DAT <= to_date('".concat(sdf.format(gc.getTime())).concat("', 'dd-MM-yy hh24:mi ')");
        }

        return where;

    }
		
	public String generateReportDetail(String reportName, ReportType reportType) {

	    /*
         * obtenemos la informacion sobre el usuario que solicita el reporte, y
         * el cliente al que pertenece
         */
        Userweb userweb = SecurityBean.getInstance().getLoggedInUserClient();
        Client client = userweb.getClient();
        String userInformation = MessageFormat.format("{0} ({1})", userweb.getNameChr(), client.getNameChr());

        Map<Object, Object> params = new HashMap<Object, Object>();
        params.put("WHERE", getDetailReportWhere());
        params.put("ORDER_BY", "");
        params.put("USER", userInformation);
        params.put("SUBREPORT_DIR", getReportsPath().concat("/"));
        params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
        params.put(JRParameter.REPORT_RESOURCE_BUNDLE, i18n.getResourceBundle());
        params.put("TITLE", getEntity().getFeatureElement().getDescriptionChr());

        ClientFile cf = fileFacade.getClientLogo(userweb.getClient());
        try {
            if (cf != null) {
                params.put("CLIENT_LOGO", JRImageLoader.loadImage(cf.getFileByt()));
            }
        } catch (JRException ex) {
        }
        Connection conn = SMBaseBean.getDatabaseConnecction();
        JasperReportUtils.respondReport(reportName, params, true, conn, reportType);
        return null;
    }
	
	public String getDetailReportWhere() {
        return " AND fv.FEATURE_VALUE_COD = ".concat(getEntity().getFeatureValueCod().toString());
    }
	
	public String viewPDFDetail() {
        signalReport(ReportType.PDF);
        return generateReportDetail(getPdfReportDetailName(), ReportType.PDF);
    }
    
	public FeatureElement getSelectedFeatureElement() {
        return selectedFeatureElement;
    }
    
	public void setSelectedFeatureElement(FeatureElement selectedFeatureElement) {
        this.selectedFeatureElement = selectedFeatureElement;
    }

    public List<FeatureElement> getFeatureElementList() {
        if (featureElementList == null){
            featureElementList = featureElementFacade.getFeatureElementList(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), true);
        }
        return featureElementList;
    }

    public void setFeatureElementList(List<FeatureElement> featureElementList) {
        this.featureElementList = featureElementList;
    }
    
    public List<Userphone> getUserphoneList() {
        return userphoneList;
    }

    public List<PhoneList> getPhoneList() {
        return phoneList;
    }

    public void setUserphoneList(List<Userphone> userphoneList) {
        this.userphoneList = userphoneList;
    }

    public void setPhoneList(List<PhoneList> phoneList) {
        this.phoneList = phoneList;
    }
    
    public String getTypeOption() {
        return typeOption;
    }

    public void setTypeOption(String typeOption) {
        this.typeOption = typeOption;
    }

    public DataModel<FeatureValueData> getDataModelDetail() {
        return dataModelDetail;
    }

    public void setDataModelDetail(DataModel<FeatureValueData> dataModelDetail) {
        this.dataModelDetail = dataModelDetail;
    }

    public Userphone getSelectedUserphone() {
        return selectedUserphone;
    }

    public Phone getSelectedPhone() {
        return selectedPhone;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public String getUsermobileName() {
        return usermobileName;
    }

    public void setSelectedUserphone(Userphone selectedUserphone) {
        this.selectedUserphone = selectedUserphone;
    }

    public void setSelectedPhone(Phone selectedPhone) {
        this.selectedPhone = selectedPhone;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public void setUsermobileName(String usermobileName) {
        this.usermobileName = usermobileName;
    }
    
    public boolean isPrimarySortedFieldDetailAsc() {
        return primarySortedFieldDetailAsc;
    }

    public void setPrimarySortedFieldDetail(String primarySortedFieldDetail) {
        this.primarySortedFieldDetail = primarySortedFieldDetail;
    }

    public void setPrimarySortedFieldDetailAsc(boolean primarySortedFieldDetailAsc) {
        this.primarySortedFieldDetailAsc = primarySortedFieldDetailAsc;
    }

    public void setGeolocalizationAllowed(Boolean geolocalizationAllowed) {
        this.geolocalizationAllowed = geolocalizationAllowed;
    }

    public FeatureValueData getEntityDetail() {
        return entityDetail;
    }

    public void setEntityDetail(FeatureValueData entityDetail) {
        this.entityDetail = entityDetail;
    }
    
    public Map<Object, Boolean> getSelectedDetailItems() {
        if (selectedDetailItems == null) {
            selectedDetailItems = new HashMap<Object, Boolean>();
        }

        return selectedDetailItems;
    }

    public void setSelectedDetailItems(Map<Object, Boolean> selectedDetailItems) {
        this.selectedDetailItems = selectedDetailItems;
    }

    public SortHelper getSortHelperDetail() {
        return sortHelperDetail;
    }

    public void setSortHelperDetail(SortHelper sortHelperDetail) {
        this.sortHelperDetail = sortHelperDetail;
    }
    
    public String getPdfReportCabDetailName() {
        return pdfReportCabDetailName;
    }

    public String getXlsReportCabDetailName() {
        return xlsReportCabDetailName;
    }

    public String getPdfReportDetailName() {
        return pdfReportDetailName;
    }

    public String getXlsReportDetailName() {
        return xlsReportDetailName;
    }

    public void setPdfReportCabDetailName(String pdfReportCabDetailName) {
        this.pdfReportCabDetailName = pdfReportCabDetailName;
    }

    public void setXlsReportCabDetailName(String xlsReportCabDetailName) {
        this.xlsReportCabDetailName = xlsReportCabDetailName;
    }

    public void setPdfReportDetailName(String pdfReportDetailName) {
        this.pdfReportDetailName = pdfReportDetailName;
    }

    public void setXlsReportDetailName(String xlsReportDetailName) {
        this.xlsReportDetailName = xlsReportDetailName;
    }
    
    //
	// METODOS DE LA CLASE	


    @Override
    public String applyFilter() {
        paginationHelper = null; // For pagination recreation
        setSelectedItems(null); // For clearing selection
        setDataModel(getPaginationHelper().createPageDataModel());
        if (getGeolocalizationAllowed() && showMapOnHeader) {
            mapModel = null;
            getMapModel();
            addClientMarker();
        }
        //headerMetaDataFromModel();
        signalRead();
        return null;
    }
    
    public void onChangeFeatureElement() {
        typeOption = OPEN;
        if (selectedFeatureElement != null && !selectedFeatureElement.getOpenChr()){
            userphoneList = featureElementFacade.getUserphoneList(selectedFeatureElement.getFeatureElementCod());
            
            phoneList = featureElementFacade.getPhoneList(selectedFeatureElement.getFeatureElementCod());
            if (phoneList != null && phoneList.size() > 0){
                phones = new ArrayList<Phone>();
                for (PhoneList pl : phoneList) {
                   List<Phone> lista = phoneListFacade.getPhones(pl.getPhoneListCod());
                   for (Phone phone : lista) {
                       if (!phones.contains(phone))
                           phones.add(phone);
                   }
                }
            }
            
            if (userphoneList != null && userphoneList.size() > 0){
               typeOption = INTERNAL;
            }else
               typeOption = EXTERNAL;
        }
    }

    @Override
    public String getReportWhereCriteria() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PaginationHelper getPaginationHelper() {
        if (paginationHelper == null) {
            int pageSize = getRowQuantSelected().length() > 0 ? Integer.valueOf(getRowQuantSelected()).intValue() : 0;

            paginationHelper = new PaginationHelper(pageSize) {
                Integer count = null;

                @Override
                public int getItemsCount() {
                    List<Classification> classifications = classificationFacade.findByUserwebWithChilds(SecurityBean.getInstance().getLoggedInUserClient());
                    if (count == null) {
                        Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
                        
                        // SE LE AGREGA EL CLIENTE
                        String where = " WHERE o.featureElement.clientFeature.client.clientCod = {0} ";
                        where = MessageFormat.format(where, clientCod.toString()); 
                        
                        // SE LE AGREGA EL FEATURE ELEMENT
                        if (selectedFeatureElement != null){
                            where = where.concat(" AND o.featureElement.featureElementCod = {0} ");
                            where = MessageFormat.format(where, selectedFeatureElement.getFeatureElementCod().toString()); 
                        }else{
                            // SI NO SELECCIONO ALGUN FEATURE, NO MUESTRA NADA
                            where = where.concat(" AND 1=2 ");
                            return 0;
                        }  

                        // SE LE AGREGA LA RESTRICCION DE CLASIFICACION
                        where = where.concat(" AND EXISTS ( SELECT f FROM FeatureElement f , IN (f.classifications) cl WHERE f.featureElementCod = o.featureElement.featureElementCod AND cl in (:classifications)) ");
                        
                        if (typeOption == INTERNAL){
                            where = where.concat(" AND o.userphone.client.clientCod = {0} AND EXISTS ( SELECT u FROM Userphone u , IN (u.classificationList) cl WHERE u.userphoneCod = o.userphone.userphoneCod AND u.client = o.userphone.client AND cl.codClient = o.userphone.client AND cl in (:classifications)) ");
                            where = MessageFormat.format(where, clientCod.toString());
                            
                        }                        
                        where = where.concat(getCabDetailWhereCriteria());
                        count = getFacade().count(where, classifications, dateFrom, dateTo);
                    }

                    return count.intValue();
                }

                @Override
                public DataModel createPageDataModel() {
                    List<Classification> classifications = classificationFacade.findByUserwebWithChilds(SecurityBean.getInstance().getLoggedInUserClient());
                    Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();                    
                    
                    // SE LE AGREGA EL CLIENTE
                    String where = " WHERE o.featureElement.clientFeature.client.clientCod = {0} ";
                    where = MessageFormat.format(where, clientCod.toString()); 
                    
                    // SE LE AGREGA EL FEATURE ELEMENT
                    if (selectedFeatureElement != null){
                        where = where.concat(" AND o.featureElement.featureElementCod = {0} ");
                        where = MessageFormat.format(where, selectedFeatureElement.getFeatureElementCod().toString()); 
                    }else{
                        // SI NO SELECCIONO ALGUN FEATURE, NO MUESTRA NADA
                        where = where.concat(" AND 1=2 ");
                        return new ListDataModelViewCsTigo(getFacade().findRange(new int[] { getPageFirstItem(), getPageFirstItem() + getPageSize() }, where, null, classifications));
                    }  
                    
                	where = where.concat(" AND EXISTS ( SELECT f FROM FeatureElement f , IN (f.classifications) cl WHERE f.featureElementCod = o.featureElement.featureElementCod AND cl in (:classifications)) ");               
                    
                    // SI LA ENCUESTA SELECCIONADA ES INTERNA
                    if (typeOption == INTERNAL){
                        where = where.concat(" AND o.userphone.client.clientCod = {0} AND EXISTS ( SELECT u FROM Userphone u , IN (u.classificationList) cl WHERE u.userphoneCod = o.userphone.userphoneCod AND u.client = o.userphone.client AND cl.codClient = o.userphone.client AND cl in (:classifications)) ");
                        where = MessageFormat.format(where, clientCod.toString());
                        
                    }                    
                    where = where.concat(getCabDetailWhereCriteria());
                    String orderby = "o.".concat(getSortHelper().getField()).concat(getSortHelper().isAscending() ? " ASC" : " DESC");
                    return new ListDataModelViewCsTigo(getFacade().findRange(new int[] { getPageFirstItem(), getPageFirstItem() + getPageSize() }, where, orderby, classifications, dateFrom, dateTo));
                

                }
            };
        }

        return paginationHelper;
    }
        
    @Override
    protected void setPaginationHelper(PaginationHelper paginationHelper) {
        this.paginationHelper = paginationHelper;
    }
    
    @Override
    public DataModel<FeatureValue> getDataModel() {
        return dataModel;
    }
    
    @Override
    public void setDataModel(DataModel<FeatureValue> dataModel) {
        this.dataModel = dataModel;
    }
    
    public String getCabDetailWhereCriteria() {
        if (((usermobileName == null || usermobileName.isEmpty()) 
                && selectedFeatureElement == null 
                && selectedPhone == null 
                && selectedUserphone == null 
                && dateFrom == null 
                && dateTo == null) || selectedFeatureElement == null) {
            return " AND 1 = 2 ";
        }
        String where = "";
        if (usermobileName != null && !usermobileName.isEmpty()) {
            if (typeOption == INTERNAL)
                where += " AND lower (o.userphone.nameChr) LIKE '%".concat(usermobileName.toLowerCase()).concat("%'");
            else if (typeOption == EXTERNAL)
                where += " AND lower (o.phone.nameChr) LIKE '%".concat(usermobileName.toLowerCase()).concat("%'");
                
        }
        if (typeOption == INTERNAL && selectedUserphone != null) {
            where += " AND o.userphone.userphoneCod = ".concat(selectedUserphone.getUserphoneCod().toString());
        }

        if (typeOption == EXTERNAL && selectedPhone != null) {
            where += " AND o.phone.phoneCod = ".concat(selectedPhone.getPhoneCod().toString());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm");
        if (dateFrom != null) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(dateFrom);
            gc.set(Calendar.HOUR_OF_DAY, 0);
            gc.set(Calendar.MINUTE, 0);
            gc.set(Calendar.SECOND, 0);
            where += " AND o.message.dateinDat >= :dateFrom ";
            dateFrom = gc.getTime();
        }
        if (dateTo != null) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(dateTo);
            gc.set(Calendar.HOUR_OF_DAY, 23);
            gc.set(Calendar.MINUTE, 59);
            gc.set(Calendar.SECOND, 59);
            where += " AND o.message.dateinDat <= :dateTo ";
            dateTo = gc.getTime();
        }

        return where;
    }
        
    @Override
    public String applySort() {
        setSelectedItems(null); // For clearing selection
        dataModel = getPaginationHelper().createPageDataModel();
        return null;
    }
    
    public PaginationHelper getPaginationHelperDetail() {
        if (paginationHelperDetail == null) {
            int pageSize = getRowQuantSelected().length() > 0 ? Integer.valueOf(getRowQuantSelected()).intValue() : 0;

            paginationHelperDetail = new PaginationHelper(pageSize) {

                Integer count = null;

                @Override
                public int getItemsCount() {
                    if (count == null) {
                        count = facadeDetail.count(" WHERE o.featureValue.featureValueCod = ".concat(getEntity().getFeatureValueCod().toString()));
                    }
                    return count;
                }

                @Override
                public DataModel createPageDataModel() {
                    String orderby = "o.".concat(sortHelperDetail.getField()).concat(sortHelperDetail.isAscending() ? " ASC" : " DESC");
                    return new ListDataModelViewCsTigo(facadeDetail.findRange(new int[] { getPageFirstItem(), getPageFirstItem() + getPageSize() }, 
                               " WHERE o.featureValue.featureValueCod = ".concat(getEntity().getFeatureValueCod().toString()), orderby));
                }
            };
        }

        return paginationHelperDetail;
    }

    protected void setPaginationHelperDetail(PaginationHelper paginationHelperDetail) {
        this.paginationHelperDetail = paginationHelperDetail;
    }   
    
    public String cancelDetail() {
        if (showMapOnDetail) {
            mapModel = null;
            mapCenter = null;
            mapZoom = null;
            mapType = null;
        }
        selectedDetailItems = null;
        setEntity(null);
        setEntityDetail(null);
        setDataModelDetail(null);
        return null;
    }
    
    public String applyFilterDetail() {
        paginationHelperDetail = null; // For pagination recreation
        selectedDetailItems = null; // For clearing selection
        dataModelDetail = null;// getPaginationHelper().createPageDataModel();
        return null;
    }

    public String applySortDetail() {
        selectedDetailItems = null; // For clearing selection
        dataModelDetail = getPaginationHelperDetail().createPageDataModel();
        return null;
    }
    
    public String previousDetailPage() {
        getPaginationHelperDetail().previousPage();
        dataModelDetail = getPaginationHelperDetail().createPageDataModel(); // For
                                                                             // data
                                                                             // model
                                                                             // recreation
        selectedDetailItems = null; // For clearing selection       
        return null;
    }

    public String nextDetailPage() {
        getPaginationHelperDetail().nextPage();
        dataModelDetail = getPaginationHelperDetail().createPageDataModel(); // For
                                                                             // data
                                                                             // model
                                                                             // recreation
        selectedDetailItems = null; // For clearing selection       
        return null;
    }
    
    public String nextHeaderPage() {
        getPaginationHelper().nextPage();
        dataModel = getPaginationHelper().createPageDataModel(); // For
                                                                       // data
                                                                       // model
                                                                       // recreation
        selectedItems = null;  
        return null;
    }
    
    public String previousHeaderPage() {
        getPaginationHelper().previousPage();
        dataModel = getPaginationHelper().createPageDataModel(); // For
                                                                       // data
                                                                       // model
                                                                       // recreation
        selectedItems = null;  
        return null;
    }
    
    public String viewXLSDetail() {
        signalReport(ReportType.XLS);
        return generateReportDetail(getXlsReportDetailName(), ReportType.XLS);
    }
    
    //MAPAS
    public MapModel getMapModel() {
        if (mapModel == null) {
            mapModel = new DefaultMapModel();
            mapCenter = ApplicationBean.getInstance().getMapStartingPoint();
            mapZoom = Integer.parseInt(ApplicationBean.getInstance().getMapStartingZoom());
            mapType = ApplicationBean.getInstance().getMapStartingType();
        }
        return mapModel;
    }

    public void setMapModel(MapModel mapModel) {
        this.mapModel = mapModel;
    }

    public String getMapCenterStr() {
        if (mapCenter != null) {
            return mapCenter.getLat() + "," + mapCenter.getLng();
        }
        return "0,0";
    }

    public LatLng getMapCenter() {
        return mapCenter;
    }

    public void setMapCenter(LatLng mapCenter) {
        this.mapCenter = mapCenter;
    }

    public Integer getMapZoom() {
        return mapZoom;
    }

    public void setMapZoom(Integer mapZoom) {
        this.mapZoom = mapZoom;
    }

    public String getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public Marker getSelectedMarker() {
        return selectedMarker;
    }

    public void setSelectedMarker(Marker selectedMarker) {
        this.selectedMarker = selectedMarker;
    }

    public LatLngBounds getLastBounds() {
        return lastBounds;
    }

    public void setLastBounds(LatLngBounds lastBounds) {
        this.lastBounds = lastBounds;
    }
    
    public String getMessageDescriptionHeaderMap(FeatureValue fv) {
        DateFormat sdf = new SimpleDateFormat(ApplicationBean.getInstance().getDefaultOutputDateTimeFormat());
        String messDescrip = "";
        if (typeOption == INTERNAL){
        	if (fv.getUserphone() != null){
        		messDescrip = fv.getUserphone().getNameChr() != null 
        				? fv.getUserphone().getNameChr().concat(" | ").concat(sdf.format(fv.getMessage().getDateinDat()))
        				: fv.getUserphone().getCellphoneNum().toString().concat(" | ").concat(sdf.format(fv.getMessage().getDateinDat()));
        	}else{
        		messDescrip = fv.getMessage().getOriginChr().concat(" | ").concat(sdf.format(fv.getMessage().getDateinDat()));
        	}
        }
        else if (typeOption == EXTERNAL){
        	if (fv.getPhone() != null){
	            messDescrip = fv.getPhone().getNameChr() != null 
	            		? fv.getPhone().getNameChr().concat(" | ").concat(sdf.format(fv.getMessage().getDateinDat())) 
	            		: fv.getPhone().getCellphoneNum().toString().concat(" | ").concat(sdf.format(fv.getMessage().getDateinDat()));
        	}else{
        		messDescrip = fv.getMessage().getOriginChr().concat(" | ").concat(sdf.format(fv.getMessage().getDateinDat()));
        	}
        }
        else{
            messDescrip = fv.getMessage().getOriginChr().concat(" | ").concat(sdf.format(fv.getMessage().getDateinDat()));
        }
        return messDescrip;
    }

    public String showHeaderMap() {
        if (getGeolocalizationAllowed()) {
            Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();

            mapModel = new DefaultMapModel();
            boolean oneInsideBounds = false;
            Map<String, ArrayList<Marker>> positions = new HashMap<String, ArrayList<Marker>>();

            Iterator<FeatureValue> iterator = getDataModel().iterator();
            int nextMarkerCounter = 1;
            while (iterator.hasNext()) {
                FeatureValue currServiceValue = iterator.next();
                if (getSelectedItems().containsKey(currServiceValue.getFeatureValueCod()) 
                        && getSelectedItems().get(currServiceValue.getFeatureValueCod()) 
                        && (currServiceValue.getMessage().getCell() != null || (currServiceValue.getMessage().getLatitude() != null && currServiceValue.getMessage().getLongitude() != null)) ) {

                    // Message for marker
                    String messDescrip = getMessageDescriptionHeaderMap(currServiceValue);
                    Polygon polygonArea = null;
                    Marker markerArea = null;
                    Double latitude = currServiceValue.getMessage().getLatitude();
                    Double longitude = currServiceValue.getMessage().getLongitude();
                    
                    if (latitude != null && longitude != null){
                        markerArea = getGPSCellAreaMarker(latitude, longitude, messDescrip, String.valueOf(nextMarkerCounter));
                    } else {
                        // Obtain latitude, longitude and azimuth from cell
                        latitude = currServiceValue.getMessage().getCell().getLatitudeNum().doubleValue();
                        longitude = currServiceValue.getMessage().getCell().getLongitudeNum().doubleValue();
                        double azimuth = currServiceValue.getMessage().getCell().getAzimuthNum().doubleValue();
                        String siteCell = currServiceValue.getMessage().getCell().getSiteChr();
    
    
                        if (!siteCell.toUpperCase().endsWith("O")) {
                            // SEGMENTED CELL
    
                            // Cell polygon
                            polygonArea = getCellAreaPolygon(latitude, longitude, azimuth);
    
                            // Cell marker
                            markerArea = getCellAreaMarker(latitude, longitude, azimuth, messDescrip, String.valueOf(nextMarkerCounter));
                        } else {
                            // OMNIDIRECTIONAL CELL
    
                            // Cell polygon
                            polygonArea = getOmniCellAreaPolygon(latitude, longitude);
    
                            // Cell marker
                            markerArea = getOmniCellAreaMarker(latitude, longitude, messDescrip, String.valueOf(nextMarkerCounter));
                        }
    
                        // Add the polygon
                        if (polygonArea != null) {
                            // if the polygon already exists, do not add.
                            Polygon existingPolygon = null;
                            for (int i = 0; i < getMapModel().getPolygons().size(); i++) {
                                if (getMapModel().getPolygons().get(i).getPaths().equals(polygonArea.getPaths())) {
                                    existingPolygon = getMapModel().getPolygons().get(i);
                                    break;
                                }
                            }
    
                            if (existingPolygon == null) {
                                getMapModel().addOverlay(polygonArea);
                            }
                        }
                    
                    }
                    
                    // Add the marker
                    if (markerArea != null) {
                        // if the marker already exists, do not add, but instead
                        // add data to existing one.
                        Marker existingMarker = null;
                        for (int i = 0; i < getMapModel().getMarkers().size(); i++) {
                            if (getMapModel().getMarkers().get(i).getLatlng().equals(markerArea.getLatlng())) {
                                existingMarker = getMapModel().getMarkers().get(i);
                                break;
                            }
                        }

                        if (existingMarker == null) {
                            getMapModel().addOverlay(markerArea);
                            nextMarkerCounter++;
                            if (lastBounds != null) {
                                boolean inside = markerArea.getLatlng().getLat() > lastBounds.getSouthWest().getLat();
                                inside = inside && markerArea.getLatlng().getLat() < lastBounds.getNorthEast().getLat();
                                inside = inside && markerArea.getLatlng().getLng() > lastBounds.getSouthWest().getLng();
                                inside = inside && markerArea.getLatlng().getLng() < lastBounds.getNorthEast().getLng();

                                if (inside) {
                                    oneInsideBounds = true;
                                }
                            }

                            // Add current marker for drawing polyline later.
                            if (!positions.containsKey(currServiceValue.getMessage().getOriginChr())) {
                                positions.put(currServiceValue.getMessage().getOriginChr(), new ArrayList<Marker>());
                            }
                            positions.get(currServiceValue.getMessage().getOriginChr()).add(markerArea);
                        } else {
                            existingMarker.setData(((String) existingMarker.getData()).concat("<br>").concat(messDescrip));
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
                    addMarkersPolylineDiferent(positions, getMapModel());
                }
            }
        }
        addClientMarker();
        return null;
    }

//    public String showDetailMap() {
//        if (getGeolocalizationAllowed()) {
//            Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
//
//            mapModel = new DefaultMapModel();
//            boolean oneInsideBounds = false;
//
//            Iterator<FeatureValueData> iterator = getDataModelDetail().iterator();
//            int nextMarkerCounter = 1;
//            while (iterator.hasNext()) {
//                FeatureValueData currServiceDetailValue = iterator.next();
//                if (getSelectedDetailItems().containsKey(currServiceDetailValue.getFeatureValueDataCod()) 
//                        && getSelectedDetailItems().get(currServiceDetailValue.getFeatureValueDataCod()) 
//                        && (currServiceDetailValue.getMessage().getCell() != null || (currServiceDetailValue.getMessage().getLatitude() != null && currServiceDetailValue.getMessage().getLongitude() != null)) ) {
//                    // Obtain latitude, longitude and azimuth from cell
//                    // Message for marker
//                    String messDescrip = getMessageDescriptionDetailMap(currServiceDetailValue);
//                    Polygon polygonArea = null;
//                    Marker markerArea = null;
//                    Double latitude = currServiceDetailValue.getMessage().getLatitude();
//                    Double longitude = currServiceDetailValue.getMessage().getLongitude();
//                    
//                    if (latitude != null && longitude != null){
//                        markerArea = getGPSCellAreaMarker(latitude, longitude, messDescrip, String.valueOf(nextMarkerCounter));
//                    } else {
//                        latitude = currServiceDetailValue.getMessage().getCell().getLatitudeNum().doubleValue();
//                        longitude = currServiceDetailValue.getMessage().getCell().getLongitudeNum().doubleValue();
//                        double azimuth = currServiceDetailValue.getMessage().getCell().getAzimuthNum().doubleValue();
//                        String siteCell = currServiceDetailValue.getMessage().getCell().getSiteChr();
//                        
//    
//                        if (!siteCell.toUpperCase().endsWith("O")) {
//                            // SEGMENTED CELL
//    
//                            // Cell polygon
//                            polygonArea = getCellAreaPolygon(latitude, longitude, azimuth);
//    
//                            // Cell marker
//                            markerArea = getCellAreaMarker(latitude, longitude, azimuth, messDescrip, String.valueOf(nextMarkerCounter));
//                        } else {
//                            // OMNIDIRECTIONAL CELL
//    
//                            // Cell polygon
//                            polygonArea = getOmniCellAreaPolygon(latitude, longitude);
//    
//                            // Cell marker
//                            markerArea = getOmniCellAreaMarker(latitude, longitude, messDescrip, String.valueOf(nextMarkerCounter));
//                        }
//    
//                        // Add the polygon
//                        if (polygonArea != null) {
//                            // if the polygon already exists, do not add.
//                            Polygon existingPolygon = null;
//                            for (int i = 0; i < getMapModel().getPolygons().size(); i++) {
//                                if (getMapModel().getPolygons().get(i).getPaths().equals(polygonArea.getPaths())) {
//                                    existingPolygon = getMapModel().getPolygons().get(i);
//                                    break;
//                                }
//                            }
//    
//                            if (existingPolygon == null) {
//                                getMapModel().addOverlay(polygonArea);
//                            }
//                        }
//
//                    }//FIN DEL IF
//                    
//                    // Add the marker
//                    if (markerArea != null) {
//                        // if the marker already exists, do not add, but instead
//                        // add data to existing one.
//                        Marker existingMarker = null;
//                        for (int i = 0; i < getMapModel().getMarkers().size(); i++) {
//                            if (getMapModel().getMarkers().get(i).getLatlng().equals(markerArea.getLatlng())) {
//                                existingMarker = getMapModel().getMarkers().get(i);
//                                break;
//                            }
//                        }
//
//                        if (existingMarker == null) {
//                            getMapModel().addOverlay(markerArea);
//                            nextMarkerCounter++;
//                            if (lastBounds != null) {
//                                boolean inside = markerArea.getLatlng().getLat() > lastBounds.getSouthWest().getLat();
//                                inside = inside && markerArea.getLatlng().getLat() < lastBounds.getNorthEast().getLat();
//                                inside = inside && markerArea.getLatlng().getLng() > lastBounds.getSouthWest().getLng();
//                                inside = inside && markerArea.getLatlng().getLng() < lastBounds.getNorthEast().getLng();
//
//                                if (inside) {
//                                    oneInsideBounds = true;
//                                }
//                            }
//                        } else {
//                            existingMarker.setData(((String) existingMarker.getData()).concat("<br>").concat(messDescrip));
//                        }
//                    }
//                }
//            }
//            // Add polyline
//            if (getMapModel().getMarkers().size() > 0) {
//                if (!oneInsideBounds) {
//                    mapCenter = mapModel.getMarkers().get(0).getLatlng();
//                }
//                if (getMapModel().getMarkers().size() > 1) {
//                    getMapModel().addOverlay(getMarkersPolyline(getMapModel().getMarkers()));
//                }
//            }
//        }
//        addClientMarker();
//        return null;
//    }

    protected Polygon getCellAreaPolygon(double latitude, double longitude, double azimuth) {
        // Obtain global parameters
        String sectorAreaBgColor = ApplicationBean.getInstance().getMapCellSectorAreaBackgroundColor();
        double sectorAreaBgOpacity = Double.valueOf(ApplicationBean.getInstance().getMapCellSectorAreaBackgroundOpacity());
        double sectorAreaAngle = Double.valueOf(ApplicationBean.getInstance().getMapCellSectorAreaAngle());
        double sectorAreaDistance = Double.valueOf(ApplicationBean.getInstance().getMapCellSectorAreaDistance());

        // Prepare polygon
        Polygon polygon = new Polygon();
        polygon.setFillColor(sectorAreaBgColor);
        polygon.setFillOpacity(sectorAreaBgOpacity);
        polygon.setStrokeColor(sectorAreaBgColor);
        polygon.setStrokeOpacity(0.0);

        // Add current point to polygon
        polygon.getPaths().add(new LatLng(latitude, longitude));

        // Prepare common data
        double angleFromAzimuth = sectorAreaAngle / 2d; // Degrees
        int startingAzimuth = (int) Math.floor(azimuth - angleFromAzimuth); // Degrees
        int endingAzimuth = (int) Math.ceil(azimuth + angleFromAzimuth); // Degrees
        GeoUtil.GeoPoint geoPoint;

        for (int i = startingAzimuth; i <= endingAzimuth; i++) {
            // Calculate area point
            int calculatedAzimuth = i;
            calculatedAzimuth += calculatedAzimuth < 0 ? 360 : 0;
            calculatedAzimuth -= calculatedAzimuth > 360 ? 360 : 0;
            geoPoint = GeoUtil.moveGeoPoint(latitude, longitude, calculatedAzimuth, sectorAreaDistance);

            // Add area point to polygon
            polygon.getPaths().add(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
        }

        return polygon;
    }

    protected Polygon getOmniCellAreaPolygon(double latitude, double longitude) {
        // Obtain global parameters
        String areaBgColor = ApplicationBean.getInstance().getMapCellSectorAreaBackgroundColor();
        double areaBgOpacity = Double.valueOf(ApplicationBean.getInstance().getMapCellSectorAreaBackgroundOpacity());
        double areaRadius = Double.valueOf(ApplicationBean.getInstance().getMapCellOmniAreaRadius());

        // Prepare polygon
        Polygon polygon = new Polygon();
        polygon.setFillColor(areaBgColor);
        polygon.setFillOpacity(areaBgOpacity);
        polygon.setStrokeColor(areaBgColor);
        polygon.setStrokeOpacity(0.0);

        // Prepare common data
        GeoUtil.GeoPoint geoPoint;

        // Circle area
        for (int i = 0; i < 360; i++) {
            // Calculate point
            geoPoint = GeoUtil.moveGeoPoint(latitude, longitude, i, areaRadius);

            // Add point to polygon
            polygon.getPaths().add(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
        }

        return polygon;
    }

    protected Marker getCellAreaMarker(double latitude, double longitude, double azimuth, String messageDetail) {
        return getCellAreaMarker(latitude, longitude, azimuth, messageDetail, null);
    }

    protected Marker getCellAreaMarker(double latitude, double longitude, double azimuth, String messageDetail, String markerLabel) {
        // Obtain global parameters
        double sectorAreaDistance = Double.valueOf(ApplicationBean.getInstance().getMapCellSectorAreaDistance());

        // Calculate marker point
        GeoUtil.GeoPoint geoPoint = GeoUtil.moveGeoPoint(latitude, longitude, azimuth, sectorAreaDistance / 2d);

        // Prepare marker
        Marker marker = new Marker(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
        marker.setTitle(i18n.iValue("web.client.backingBean.abstractServiceBean.message.MessageSendZone"));
        marker.setData("<br>".concat(messageDetail));
        if (markerLabel != null && markerLabel.trim().length() > 0) {
            String markerTextColor = ApplicationBean.getInstance().getMapMarkerTextColor().replaceAll("#", "");
            String markerBgColor = ApplicationBean.getInstance().getMapMarkerBgColor().replaceAll("#", "");
            marker.setIcon("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=".concat(markerLabel.trim()).concat("|").concat(markerBgColor).concat("|").concat(markerTextColor));
        }

        return marker;
    }

    protected Marker getOmniCellAreaMarker(double latitude, double longitude, String messageDetail) {
        return getOmniCellAreaMarker(latitude, longitude, messageDetail, null);
    }

    protected Marker getOmniCellAreaMarker(double latitude, double longitude, String messageDetail, String markerLabel) {
        // Prepare marker
        Marker marker = new Marker(new LatLng(latitude, longitude));
        marker.setTitle(i18n.iValue("web.client.backingBean.abstractServiceBean.message.MessageSendZone"));
        marker.setData("<br>".concat(messageDetail));
        if (markerLabel != null && markerLabel.trim().length() > 0) {
            String markerTextColor = ApplicationBean.getInstance().getMapMarkerTextColor().replaceAll("#", "");
            String markerBgColor = ApplicationBean.getInstance().getMapMarkerBgColor().replaceAll("#", "");
            marker.setIcon("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=".concat(markerLabel.trim()).concat("|").concat(markerBgColor).concat("|").concat(markerTextColor));
        }

        return marker;
    }
    
    protected Marker getGPSCellAreaMarker(double latitude, double longitude, String messageDetail, String markerLabel) {
        // Prepare marker
        Marker marker = new Marker(new LatLng(latitude, longitude));
        marker.setTitle(i18n.iValue("web.client.backingBean.abstractServiceBean.message.MessageSendZone"));
        marker.setData("<br>".concat(messageDetail));
        if (markerLabel != null && markerLabel.trim().length() > 0) {
            String markerTextColor = ApplicationBean.getInstance().getMapMarkerTextColor().replaceAll("#", "");
            String markerBgColor = ApplicationBean.getInstance().getMapMarkerBgColor().replaceAll("#", "");
            marker.setIcon("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=".concat(markerLabel.trim()).concat("|").concat(markerBgColor).concat("|").concat(markerTextColor));
        }

        return marker;
    }
    
    protected Polyline getMarkersPolyline(List<Marker> markers) {
        // Obtain global parameters
        String lineColor = ApplicationBean.getInstance().getMapPolylineColor();
        double lineOpacity = Double.valueOf(ApplicationBean.getInstance().getMapPolylineOpacity());
        int lineWeight = Integer.valueOf(ApplicationBean.getInstance().getMapPolylineWeight());

        // Prepare polyline
        Polyline polyline = new Polyline();
        polyline.setStrokeColor(lineColor);
        polyline.setStrokeOpacity(lineOpacity);
        polyline.setStrokeWeight(lineWeight);

        // Add polyline point from markers
        for (Marker marker : markers) {
            polyline.getPaths().add(marker.getLatlng());
        }

        return polyline;
    }

    protected void addMarkersPolylineDiferent(Map<String, ArrayList<Marker>> lines, MapModel destMapModel) {
        // Obtain global parameters
        String lineColor = ApplicationBean.getInstance().getMapPolylineColor();
        double lineOpacity = Double.valueOf(ApplicationBean.getInstance().getMapPolylineOpacity());
        int lineWeight = Integer.valueOf(ApplicationBean.getInstance().getMapPolylineWeight());

        for (String line : lines.keySet()) {
            // Prepare polyline
            Polyline polyline = new Polyline();
            polyline.setStrokeColor(lineColor);
            polyline.setStrokeOpacity(lineOpacity);
            polyline.setStrokeWeight(lineWeight);

            // Add polyline point from markers
            for (Marker marker : lines.get(line)) {
                polyline.getPaths().add(marker.getLatlng());
            }

            destMapModel.addOverlay(polyline);
        }
    }

    public void onMarkerSelect(OverlaySelectEvent event) {
        if (event.getOverlay() instanceof ClientMarker) {
            selectedMarker = (Marker) event.getOverlay();
            clientMarker = true;
            editMarker = false;
        } else if (event.getOverlay() instanceof Marker) {
            selectedMarker = (Marker) event.getOverlay();
            clientMarker = false;
            editMarker = false;
        }
    }

    public boolean isClientMarker() {
        return clientMarker;
    }

    public void setClientMarker(boolean clientMarker) {
        this.clientMarker = clientMarker;
    }

    public String getClientMarkerDesc() {
        return clientMarkerDesc;
    }

    public void setClientMarkerDesc(String clientMarkerDesc) {
        this.clientMarkerDesc = clientMarkerDesc;
    }

    public String getClientMarkerTitle() {
        return clientMarkerTitle;
    }

    public void setClientMarkerTitle(String clientMarkerTitle) {
        this.clientMarkerTitle = clientMarkerTitle;
    }

    public boolean isEditMarker() {
        return editMarker;
    }

    public void setEditMarker(boolean editMarker) {
        this.editMarker = editMarker;
    }

    public boolean isShowMapOnDetail() {
        return showMapOnDetail;
    }

    public void setShowMapOnDetail(boolean showMapOnDetail) {
        this.showMapOnDetail = showMapOnDetail;
    }

    public boolean isShowMapOnHeader() {
        return showMapOnHeader;
    }

    public void setShowMapOnHeader(boolean showMapOnHeader) {
        this.showMapOnHeader = showMapOnHeader;
    }

    public Boolean getGeolocalizationAllowed() {
        if (geolocalizationAllowed == null) {
            try {
                geolocalizationAllowed = userwebFacade.getUserRolePrivileges(SecurityBean.getInstance().getLoggedInUserClient().getUserwebCod(), Long.valueOf(COD_SERVICE), 1L);
            } catch (GenericFacadeException ex) {
                java.util.logging.Logger.getLogger(AbstractServiceBean.class.getName()).log(Level.SEVERE, null, ex);
                geolocalizationAllowed = Boolean.FALSE;
            }
        }
        return geolocalizationAllowed;
    }

    public void onPointSelect(PointSelectEvent event) {
        LatLng latlng = event.getLatLng();
        ClientMarker nuevaMarca = new ClientMarker(latlng,urlIconDefault);
        mapModel.addOverlay(nuevaMarca);
    }

    public void onMapStateChange(StateChangeEvent event) {
        double x = (event.getBounds().getNorthEast().getLat() + event.getBounds().getSouthWest().getLat()) / 2;
        double y = (event.getBounds().getNorthEast().getLng() + event.getBounds().getSouthWest().getLng()) / 2;
        LatLng z = new LatLng(x, y);
        mapCenter = z;
        // mapCenter = event.getBounds().getCenter();
        // mapZoom = event.getZoomLevel();
        // mapType = ((GMap) event.getSource()).getType(); //Doesn't work
        lastBounds = event.getBounds();

        mapZoom = event.getZoomLevel();
    }

    public void editClientMarker() {
        if (clientMarker) {
            this.editMarker = true;
        }
    }

    public void saveClientMarker() {
        if (clientMarker) {
            Double latitude = selectedMarker.getLatlng().getLat();
            Double longitude = selectedMarker.getLatlng().getLng();
            MapMark mapMarks = mapMarksFacade.findByClientLatLng(SecurityBean.getInstance().getLoggedInUserClient().getClient(), latitude, longitude);

            if (mapMarks == null) {

                mapMarks = new MapMark();
                mapMarks.setClient(SecurityBean.getInstance().getLoggedInUserClient().getClient());
                mapMarks.setLatitudeNum(latitude);
                mapMarks.setLongitudeNum(longitude);
                mapMarks.setUserweb(SecurityBean.getInstance().getLoggedInUserClient());
                mapMarks.setTitleChr(selectedMarker.getTitle());
                mapMarks.setDescriptionChr(selectedMarker.getData().toString());
                mapMarksFacade.create(mapMarks);
            } else {
                mapMarksFacade.editByClientLatLng(SecurityBean.getInstance().getLoggedInUserClient().getClient(), latitude, longitude, selectedMarker.getTitle(), selectedMarker.getData().toString());
            }
        }
    }

    public void deleteClientMarker() {
        if (clientMarker) {
            Client client = SecurityBean.getInstance().getLoggedInUserClient().getClient();
            Double latitude = selectedMarker.getLatlng().getLat();
            Double longitude = selectedMarker.getLatlng().getLng();
            try {
                mapMarksFacade.removeByClientLatLng(client, latitude, longitude);
            } catch (Exception e) {
                notifier.log(getClass(),null, Action.ERROR, e.getMessage());
                setErrorMessage(i18n.iValue("web.client.backingbean.error.restriction"));
                return;
            }
            if (geolocalizationAllowed && showMapOnHeader) {
                showHeaderMap();
                addClientMarker();
            }
            if (geolocalizationAllowed && showMapOnDetail) {
                //showDetailMap();
                //addClientMarker();
            }
        }
    }

    public void addClientMarker() {
        List<MapMark> mapMarks = mapMarksFacade.findClientMarks(SecurityBean.getInstance().getLoggedInUserClient().getClient());
        for (MapMark mapMark : mapMarks) {

            ClientMarker markerClient = new ClientMarker(new LatLng(mapMark.getLatitudeNum(), mapMark.getLongitudeNum()),urlIconDefault);

            markerClient.setTitle(mapMark.getTitleChr());
            markerClient.setData(mapMark.getDescriptionChr());
            mapModel.addOverlay(markerClient);

        }
    }

    // END MAPS
}

