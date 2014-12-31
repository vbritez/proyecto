package com.tigo.cs.view.service;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.persistence.Column;
import javax.xml.ws.WebServiceException;

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

import com.tigo.cs.api.enumerates.MetaNames;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
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
import com.tigo.cs.domain.MapMark;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.Service;
import com.tigo.cs.domain.ServiceColumn;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.TmpWsresult;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.facade.ClassificationFacade;
import com.tigo.cs.facade.ClientFileFacade;
import com.tigo.cs.facade.ClientParameterValueFacade;
import com.tigo.cs.facade.GlobalParameterFacade;
import com.tigo.cs.facade.I18nBean;
import com.tigo.cs.facade.IconTypeFacade;
import com.tigo.cs.facade.MapMarkFacade;
import com.tigo.cs.facade.MetaClientFacade;
import com.tigo.cs.facade.MetaDataFacade;
import com.tigo.cs.facade.ServiceColumnFacade;
import com.tigo.cs.facade.ServiceFacade;
import com.tigo.cs.facade.ServiceValueDetailFacade;
import com.tigo.cs.facade.ServiceValueFacade;
import com.tigo.cs.facade.TmpWsresultFacade;
import com.tigo.cs.facade.UserphoneFacade;
import com.tigo.cs.facade.UserwebFacade;
import com.tigo.cs.security.Notifier;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.ApplicationBean;
import com.tigo.cs.ws.client.ClientWS;
import com.tigo.cs.ws.client.ClientWSService;
import com.tigo.cs.ws.client.Customer;

/**
 * 
 * @author Miguel Zorrilla
 */
public abstract class AbstractServiceBean extends SMBaseBean implements Serializable {

    private static final long serialVersionUID = -2942219506208781893L;
    private ServiceValue serviceValue;
    private int codService = 0;
    private Service service;
    private ServiceValueDetail serviceValueDetail;
    @EJB
    protected ServiceValueFacade facade;
    @EJB
    protected ServiceValueDetailFacade facadeDetail;
    @EJB
    protected UserwebFacade userwebFacade;
    @EJB
    protected ServiceColumnFacade serviceColumnFacade;
    @EJB
    protected Notifier notifier;
    @EJB
    protected ServiceFacade serviceFacade;
    @EJB
    protected TmpWsresultFacade tmpWsresultFacade;
    @EJB
    protected ClientFileFacade fileFacade;
    @EJB
    protected ClassificationFacade classificationFacade;
    @EJB
    protected MetaClientFacade metaClientFacade;
    @EJB
    protected MetaDataFacade metaDataFacade;
    @EJB
    protected ClientParameterValueFacade clientParameterValueFacade;
    @EJB
    protected MapMarkFacade mapMarksFacade;
    @EJB
    protected UserphoneFacade userPhoneFacade;
    @EJB
    protected GlobalParameterFacade globalParameterFacade;
    @EJB
    protected I18nBean i18n;
    @EJB
    protected FacadeContainer facadeContainer;
    private DataModel<ServiceValue> dataModelHeader;
    private DataModel<ServiceValueDetail> dataModelDetail;
    protected Map<Object, Boolean> selectedItems;
    protected Map<Object, Boolean> selectedDetailItems;
    protected PaginationHelper paginationHelper;
    protected PaginationHelper paginationHelperDetail;
    protected SortHelper sortHelper;
    protected SortHelper sortHelperDetail;
    private String filterCriteria;
    private String aditionalFilter;
    String keyMethod;
    protected String primarySortedField;
    protected String primarySortedFieldDetail;
    protected boolean primarySortedFieldAsc = true;
    protected boolean primarySortedFieldDetailAsc = true;
    protected List<SelectItem> rowQuantList;
    private String rowQuantSelected;
    private Map<String, ServiceColumn> columnsData;
    private Map<String, ServiceColumn> columnsDataDetail;
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
    private final Userweb userWeb = SecurityBean.getInstance().getLoggedInUserClient();
    // UPDATE para incluir nombre en el globo de google map.

    private Boolean metaEnabled;
    private Boolean wsConexionExists;
    private String URLWS = "";
    protected Integer rowDetailCount = null;
    protected List<Userphone> userphoneList = null;
    protected Userphone selectedUserphone;
    protected BigInteger mobilePhoneNumber;
    protected String usermobileName;
    protected Date dateFrom;
    protected Date dateTo;

    protected Map<Object, String> hashUsersTracking;
    protected Boolean showLocalize = false;
    protected Object selectedItemTracking;
    protected String resume;
    protected Map<Object, Object> hashMarkerUser;
    protected Map<Object, Object> hashNotMarkerUser;
    protected Map<Object, Object> nextServiceValueByUserphone;
    protected Marker lastMarker;
    protected Map<String, String> hashMapGlobalMessageTracking;
    protected String urlIconDefault = "http://maps.google.com/mapfiles/ms/micons/blue-dot.png";
    @EJB
    protected IconTypeFacade iconTypeFacade;
    
    public enum Operation {

        CREATE, READ, UPDATE, DELETE
    };

    public AbstractServiceBean() {
        initialize();
    }

    private void initialize() {
        // Initialize filter criteria with no filter
        filterCriteria = "";

        // Initialize sort with default values
        sortHelper = new SortHelper();
        try {
            sortHelper.setField(getPrimarySortedField());
            sortHelper.setAscending(primarySortedFieldAsc);
        } catch (PrimarySortedFieldNotFoundException ex) {
            java.util.logging.Logger.getLogger(
                    AbstractServiceBean.class.getName()).log(
                    Level.SEVERE,
                    i18n.iValue("web.client.backingBean.message.Error")
                        + ex.getMessage(), ex);
        }
        
        
    }
    
    @PostConstruct
    public void init() {
    	urlIconDefault = iconTypeFacade.findDefaultIcon().getUrl();
    }

    // LIST METHODS
    public Service getService() {
        if (service == null) {
            try {
                service = serviceFacade.findByServiceCod(new Integer(codService).longValue());
            } catch (MoreThanOneResultException ex) {
                notifier.signal(
                        getClass(),
                        Action.ERROR,
                        userWeb,
                        null,
                        getCurrentViewId(),
                        i18n.iValue("web.client.backingBean.abstractServiceBean.message.Error"),
                        getSessionId(), getIpAddress(), ex);
            } catch (GenericFacadeException ex) {
                notifier.signal(
                        getClass(),
                        Action.ERROR,
                        userWeb,
                        null,
                        getCurrentViewId(),
                        i18n.iValue("web.client.backingBean.abstractServiceBean.message.Error"),
                        getSessionId(), getIpAddress(), ex);
            }
        }
        return service;
    }

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
                        // List<Classification> classifications =
                        // classificationFacade.findByUserweb(userWeb);

                        List<Classification> classifications = classificationFacade.findByUserwebWithChilds(userWeb);
                        String where = " WHERE o.enabledChr = true AND o.userphone.client.clientCod = {0} AND o.service.serviceCod = {1} AND o.userphone.userphoneCod = o.message.userphone.userphoneCod AND EXISTS ( SELECT u FROM Userphone u , IN (u.classificationList) cl WHERE u.userphoneCod = o.userphone.userphoneCod AND u.client = o.userphone.client AND cl.codClient = o.userphone.client AND cl in (:classifications)) ";
                        where = MessageFormat.format(where,
                                clientCod.toString(),
                                String.valueOf(getCodService()));
                        where = where.concat(getCabDetailWhereCriteria());
                        count = facade.count(where, classifications);
                    }

                    return count.intValue();
                }

                @Override
                public DataModel createPageDataModel() {
                    Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
                    // List<Classification> classifications =
                    // classificationFacade.findByUserweb(userWeb);

                    List<Classification> classifications = classificationFacade.findByUserwebWithChilds(userWeb);
                    String where = " WHERE o.enabledChr = true AND o.userphone.client.clientCod = {0} AND o.service.serviceCod = {1} AND o.userphone.userphoneCod = o.message.userphone.userphoneCod AND EXISTS ( SELECT u FROM Userphone u , IN (u.classificationList) cl WHERE u.userphoneCod = o.userphone.userphoneCod AND u.client = o.userphone.client AND cl.codClient = o.userphone.client AND cl in (:classifications)) ";
                    where = MessageFormat.format(where, clientCod.toString(),
                            String.valueOf(getCodService()));
                    where = where.concat(getCabDetailWhereCriteria());

                    String orderby = "o.".concat(sortHelper.getField()).concat(
                            sortHelper.isAscending() ? " ASC" : " DESC");

                    /*
                     * EN CASO QUE SEA EL SERVICIO FLOTA SE RECORRE POR CADA
                     * SERVICE VALUE SUS SERVICES VALUES DETAIL PARA RECUPERAR
                     * EL KM DE INICIO Y FIN Y CALCULAR EL TOTAL RECORRIDO
                     */
                    if (getCodService() == 12) {
                        List<ServiceValue> list = facade.findRange(
                                new int[] { getPageFirstItem(), getPageFirstItem()
                                    + getPageSize() }, where, orderby,
                                classifications);

                        for (ServiceValue sv : list) {
                            Long retiro = 0L;
                            Long devolucion = 0L;
                            List<ServiceValueDetail> listSVD = facadeDetail.findByServiceValue(sv);

                            if (listSVD != null) {
                                for (ServiceValueDetail svd : listSVD) {
                                    if (svd != null) {
                                        if (svd.getColumn1Chr().equalsIgnoreCase(
                                                "report.client.fleet.title.Retire")) {
                                            retiro = Long.valueOf(svd.getColumn2Chr());
                                        } else if (svd.getColumn1Chr().equalsIgnoreCase(
                                                "report.client.fleet.title.Return")) {
                                            devolucion = Long.valueOf(svd.getColumn2Chr());
                                        }

                                        if (!devolucion.equals(0L)
                                            && !retiro.equals(0L)) {
                                            sv.setTotalRecorrido(devolucion
                                                - retiro);
                                        }
                                    }

                                }
                            }

                        }

                        return new ListDataModelViewCsTigo(list);
                    }

                    return new ListDataModelViewCsTigo(facade.findRange(
                            new int[] { getPageFirstItem(), getPageFirstItem()
                                + getPageSize() }, where, orderby,
                            classifications));
                }
            };
        }

        return paginationHelper;
    }

    protected void setPaginationHelper(PaginationHelper paginationHelper) {
        this.paginationHelper = paginationHelper;
    }

    public PaginationHelper getPaginationHelperDetail() {
        if (paginationHelperDetail == null) {
            int pageSize = getRowQuantSelected().length() > 0 ? Integer.valueOf(
                    getRowQuantSelected()).intValue() : 0;

            paginationHelperDetail = new PaginationHelper(pageSize) {

                Integer count = null;

                @Override
                public int getItemsCount() {
                    if (count == null) {
                        count = facadeDetail.count(" WHERE o.enabledChr = true AND o.serviceValue.enabledChr = true AND o.serviceValue.servicevalueCod = ".concat(
                                serviceValue.getServicevalueCod().toString()).concat(
                                getDetailWhereCriteria()));
                    }
                    return count;
                }

                @Override
                public DataModel createPageDataModel() {
                    String orderby = "o.".concat(sortHelperDetail.getField()).concat(
                            sortHelperDetail.isAscending() ? " ASC" : " DESC");
                    return new ListDataModelViewCsTigo(facadeDetail.findRange(
                            new int[] { getPageFirstItem(), getPageFirstItem()
                                + getPageSize() },
                            " WHERE o.enabledChr = true AND o.serviceValue.enabledChr = true AND o.serviceValue.servicevalueCod = ".concat(
                                    serviceValue.getServicevalueCod().toString()).concat(
                                    getDetailWhereCriteria()), orderby));
                }
            };
        }

        return paginationHelperDetail;
    }

    protected void setPaginationHelperDetail(PaginationHelper paginationHelperDetail) {
        this.paginationHelperDetail = paginationHelperDetail;
    }

    // ABSTRACT METHOD IMPLEMENTATIONS

    public String getCabDetailWhereCriteria() {
        if ((usermobileName == null || usermobileName.isEmpty())
            && mobilePhoneNumber == null && selectedUserphone == null
            && dateFrom == null && dateTo == null) {
            return " AND 1 = 2 ";
        }
        String where = "";
        if (usermobileName != null && !usermobileName.isEmpty()) {
            where += " AND lower (o.userphone.nameChr) LIKE '%".concat(
                    usermobileName.toLowerCase()).concat("%'");
        }
        if (selectedUserphone != null) {
            where += " AND o.userphone.userphoneCod = ".concat(selectedUserphone.getUserphoneCod().toString());
        }

        if (mobilePhoneNumber != null) {
            where += " AND o.userphone.cellphoneNum = ".concat(mobilePhoneNumber.toString());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
        if (dateFrom != null) {
            where += " AND o.recorddateDat >= '".concat(sdf.format(dateFrom)).concat(
                    "'");
        }
        if (dateTo != null) {
            where += " AND o.recorddateDat <= '".concat(sdf.format(dateTo)).concat(
                    "'");
        }

        return where;
    }

    public String getCabDetailReportWhereCriteria() {
        if ((usermobileName == null || usermobileName.isEmpty())
            && mobilePhoneNumber == null && selectedUserphone == null
            && dateFrom == null && dateTo == null) {
            return " AND 1 = 2 ";
        }
        String where = "";
        
            if (usermobileName != null) {
                where += " AND lower (U.name_Chr) LIKE '%".concat(
                        usermobileName.toLowerCase()).concat("%'");
            }
            if (selectedUserphone != null) {
                where += " AND U.USERPHONE_COD = ".concat(selectedUserphone.getUserphoneCod().toString());
            }
    
            if (mobilePhoneNumber != null) {
                where += " AND U.CELLPHONE_NUM = ".concat(mobilePhoneNumber.toString());
            }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
        if (dateFrom != null) {
            where += " AND SV.recorddate_dat >= '".concat(sdf.format(dateFrom)).concat(
                    "'");
        }
        if (dateTo != null) {
            where += " AND SV.recorddate_dat <= '".concat(sdf.format(dateTo)).concat(
                    "'");
        }

        return where;

    }

    public abstract String getDetailWhereCriteria();

    public abstract String getDetailReportWhereCriteria();

    public ServiceValue getServiceValue() {
        return serviceValue;
    }

    public void setServiceValue(ServiceValue serviceValue) {
        this.serviceValue = serviceValue;
    }

    public int getCodService() {
        return codService;
    }

    public void setCodService(int codService) {
        this.codService = codService;
    }

    public ServiceValueDetail getServiceValueDetail() {
        return serviceValueDetail;
    }

    public void setServiceValueDetail(ServiceValueDetail serviceValueDetail) {
        this.serviceValueDetail = serviceValueDetail;
    }

    public DataModel<ServiceValueDetail> getDataModelDetail() {
        return dataModelDetail;
    }

    public void setDataModelDetail(DataModel<ServiceValueDetail> dataModelDetail) {
        this.dataModelDetail = dataModelDetail;
    }

    public DataModel<ServiceValue> getDataModelHeader() {
        return dataModelHeader;
    }

    public void setDataModelHeader(DataModel<ServiceValue> dataModelHeader) {
        this.dataModelHeader = dataModelHeader;
    }

    public Map<Object, Boolean> getSelectedItems() {
        if (selectedItems == null) {
            selectedItems = new HashMap<Object, Boolean>();
        }

        return selectedItems;
    }

    public void setSelectedItems(Map<Object, Boolean> selectedItems) {
        this.selectedItems = selectedItems;
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

    public SortHelper getSortHelper() {
        return sortHelper;
    }

    public void setSortHelper(SortHelper sortHelper) {
        this.sortHelper = sortHelper;
    }

    public SortHelper getSortHelperDetail() {
        return sortHelperDetail;
    }

    public void setSortHelperDetail(SortHelper sortHelperDetail) {
        this.sortHelperDetail = sortHelperDetail;
    }

    public String getFilterCriteria() {
        return filterCriteria;
    }

    public void setFilterCriteria(String filterCriteria) {
        this.filterCriteria = filterCriteria;
    }

    public String getAditionalFilter() {
        return aditionalFilter;
    }

    public void setAditionalFilter(String aditionalFilter) {
        this.aditionalFilter = aditionalFilter;
    }

    public BigInteger getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(BigInteger mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public String getUsermobileName() {
        return usermobileName;
    }

    public void setUsermobileName(String usermobileName) {
        this.usermobileName = usermobileName;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public List<Userphone> getUserphoneList() {
        if (userphoneList == null) {
            userphoneList = userPhoneFacade.findByUserwebAndClassification(getUserweb());
        }
        return userphoneList;
    }

    public void setUserphoneList(List<Userphone> userphoneList) {
        this.userphoneList = userphoneList;
    }

    public Userphone getSelectedUserphone() {
        return selectedUserphone;
    }

    public void setSelectedUserphone(Userphone selectedUserphone) {
        this.selectedUserphone = selectedUserphone;
    }

    public String previousHeaderPage() {
        getPaginationHelper().previousPage();
        dataModelHeader = getPaginationHelper().createPageDataModel(); // For
                                                                       // data
                                                                       // model
                                                                       // recreation
        selectedItems = null; // For clearing selection
        /*
         * se vuelven a mapear los metadatos al cambiar de pagina
         */
        headerMetaDataFromModel();
        return null;
    }

    public String nextHeaderPage() {
        getPaginationHelper().nextPage();
        dataModelHeader = getPaginationHelper().createPageDataModel(); // For
                                                                       // data
                                                                       // model
                                                                       // recreation
        selectedItems = null; // For clearing selection
        /*
         * se vuelven a mapear los metadatos al cambiar de pagina
         */
        headerMetaDataFromModel();
        return null;
    }

    public String previousDetailPage() {
        getPaginationHelperDetail().previousPage();
        dataModelDetail = getPaginationHelperDetail().createPageDataModel(); // For
                                                                             // data
                                                                             // model
                                                                             // recreation
        selectedDetailItems = null; // For clearing selection
        /*
         * se vuelven a mapear los metadatas en el caso que los detalles tengan
         * mas de una pagina
         */
        detailMetaDataFromModel();
        return null;
    }

    public String nextDetailPage() {
        getPaginationHelperDetail().nextPage();
        dataModelDetail = getPaginationHelperDetail().createPageDataModel(); // For
                                                                             // data
                                                                             // model
                                                                             // recreation
        selectedDetailItems = null; // For clearing selection
        /*
         * se vuelven a mapear los metadatas en el caso que los detalles tengan
         * mas de una pagina
         */
        detailMetaDataFromModel();
        return null;
    }

    public String applyFilter() {
        paginationHelper = null; // For pagination recreation
        selectedItems = null; // For clearing selection
        dataModelHeader = getPaginationHelper().createPageDataModel();
        if (geolocalizationAllowed && showMapOnHeader) {
            mapModel = null;
            getMapModel();
            addClientMarker();
        }
        headerMetaDataFromModel();
        signalRead();
        return null;
    }

    public String cleanFilter() {
        filterCriteria = "";
        paginationHelper = null; // For pagination recreation
        dataModelHeader = null; // For data model recreation
        selectedItems = null; // For clearing selection
        return null;
    }

    public String applySort() {
        selectedItems = null; // For clearing selection
        dataModelHeader = getPaginationHelper().createPageDataModel();
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

    public List<SelectItem> getRowQuantList() {
        if (rowQuantList == null) {
            rowQuantList = new ArrayList<SelectItem>();
            rowQuantList.add(new SelectItem("15", "15"));
            rowQuantList.add(new SelectItem("25", "25"));
            rowQuantList.add(new SelectItem("50", "50"));
            rowQuantList.add(new SelectItem("100", "100"));
            if (showAllInQuantityList()) {
                rowQuantList.add(new SelectItem(String.valueOf(getPaginationHelper().getItemsCount()), i18n.iValue("web.client.backingBean.message.All")));
            }
        }
        return rowQuantList;
    }

    public void setRowQuantList(List<SelectItem> rowQuantList) {
        this.rowQuantList = rowQuantList;
    }

    public String getRowQuantSelected() {
        if (rowQuantSelected == null) {
            rowQuantSelected = ApplicationBean.getInstance().getDefaultServicePaginationPageSize();
        }
        return rowQuantSelected;
    }

    public void setRowQuantSelected(String rowQuantSelected) {
        this.rowQuantSelected = rowQuantSelected;
    }

    public void applyQuantity(ValueChangeEvent evnt) {
        paginationHelper = null; // For pagination recreation
        selectedItems = null; // For clearing selection
        dataModelHeader = getPaginationHelper().createPageDataModel();
    }

    public Map<String, ServiceColumn> getColumnsData() {
        if (columnsData == null) {
            columnsData = new HashMap<String, ServiceColumn>();
            try {
                List<ServiceColumn> columnsDataList = serviceColumnFacade.getColumnData(
                        Long.valueOf(getCodService()), "H");
                for (ServiceColumn data : columnsDataList) {
                    columnsData.put(data.getColumnChr(), data);
                }
            } catch (GenericFacadeException ex) {
                java.util.logging.Logger.getLogger(
                        AbstractServiceBean.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
        return columnsData;
    }

    public void setColumnsData(Map<String, ServiceColumn> columnsData) {
        this.columnsData = columnsData;
    }

    public Map<String, ServiceColumn> getColumnsDataDetail() {
        if (columnsDataDetail == null) {
            columnsDataDetail = new HashMap<String, ServiceColumn>();
            try {
                List<ServiceColumn> columnsDataList = serviceColumnFacade.getColumnData(
                        Long.valueOf(getCodService()), "D");
                for (ServiceColumn data : columnsDataList) {
                    columnsDataDetail.put(data.getColumnChr(), data);
                }
            } catch (GenericFacadeException ex) {
                java.util.logging.Logger.getLogger(
                        AbstractServiceBean.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
        return columnsDataDetail;
    }

    public void setColumnsDataDetail(Map<String, ServiceColumn> columnsDataDetail) {
        this.columnsDataDetail = columnsDataDetail;
    }

    public String cancelDetail() {
        if (showMapOnDetail) {
            mapModel = null;
            mapCenter = null;
            mapZoom = null;
            mapType = null;
        }
        selectedDetailItems = null;
        setServiceValue(null);
        setServiceValueDetail(null);
        setDataModelDetail(null);
        return null;
    }

    // MAPS
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

    private boolean getMetaEnabled() {
        if (metaEnabled == null) {
            metaEnabled = false;
            try {
                Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
                MetaClient mc = metaClientFacade.findByMetaAndClient(codClient,
                        MetaNames.CLIENT.value());
                if (mc != null && mc.getEnabledChr()) {
                    metaEnabled = true;
                }
            } catch (Exception ex) {
                Logger.getLogger(VisitBean.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
        return metaEnabled.booleanValue();
    }

    private boolean getWSConexionExists() {
        if (wsConexionExists == null) {
            wsConexionExists = false;

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

        }
        return wsConexionExists;
    }

    private String getValueByIntegrationMethod(Long codClient, String code, String discriminator) throws MoreThanOneResultException, GenericFacadeException {
        String retValue = null;
        if (getMetaEnabled()) {
            MetaData md = metaDataFacade.findByClientMetaMemberAndCode(
                    codClient, MetaNames.CLIENT.value(), 1L, code);
            if (md != null) {
                retValue = md.getValueChr();
            }
        } else if (getWSConexionExists()) {
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
            ClientWSService wsService = new ClientWSService(url);
            ClientWS port = wsService.getClientWSPort();
            if (discriminator.equals("C")) {
                Customer customer = port.getCustomerByCode(key);
                value = customer != null && customer.getName() != null ? customer.getName().trim() : null;
            } else {
                // Motive motive = port.getMotiveByCode(key);
                // value = motive != null && motive.getName() != null ?
                // motive.getName().trim() : null;
            }

            TmpWsresult tmp = new TmpWsresult();
            tmp.setCodClient(codClient);
            tmp.setSessionId(getSessionId());
            tmp.setDataType(discriminator);
            tmp.setDataId(key);
            tmp.setDataC(value != null && !value.trim().equals("") ? value.trim() : " ");
            tmpWsresultFacade.create(tmp);
        } catch (MalformedURLException ex) {
            notifier.signal(
                    getClass(),
                    Action.ERROR,
                    SecurityBean.getInstance().getLoggedInUserClient(),
                    null,
                    getCurrentViewId(),
                    MessageFormat.format(
                            i18n.iValue("web.client.backingBean.message.WrongWSURL"),
                            "", String.valueOf(codClient)), getSessionId(),
                    getIpAddress(), ex);
            wsConexionExists = Boolean.FALSE;
        } catch (WebServiceException we) {
            notifier.signal(
                    getClass(),
                    Action.ERROR,
                    SecurityBean.getInstance().getLoggedInUserClient(),
                    null,
                    getCurrentViewId(),
                    MessageFormat.format(
                            i18n.iValue("web.client.backingBean.message.WrongWSInvocation"),
                            "", String.valueOf(codClient)), getSessionId(),
                    getIpAddress(), we);
            wsConexionExists = Boolean.FALSE;
        }
        return value;
    }

    public String getMessageDescriptionHeaderMap(ServiceValue sv) {
        DateFormat sdf = new SimpleDateFormat(ApplicationBean.getInstance().getDefaultOutputDateTimeFormat());
        String messDescrip = sv.getMessage().getUserphone().getNameChr().concat(
                " | ").concat(sdf.format(sv.getMessage().getDateinDat()));
        return messDescrip;
    }

    public String getMessageDescriptionDetailMap(ServiceValueDetail svd) {
        DateFormat sdf = new SimpleDateFormat(ApplicationBean.getInstance().getDefaultOutputDateTimeFormat());
        String messDescrip = svd.getMessage().getUserphone().getNameChr().concat(
                " | ").concat(sdf.format(svd.getMessage().getDateinDat()));
        return messDescrip;
    }

    public String showHeaderMap() {
        try {
            if (getGeolocalizationAllowed()) {
                Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();

                mapModel = new DefaultMapModel();
                boolean oneInsideBounds = false;
                Map<String, ArrayList<Marker>> positions = new HashMap<String, ArrayList<Marker>>();

                Iterator<ServiceValue> iterator = getDataModelHeader().iterator();
                int nextMarkerCounter = 1;
                showLocalize = true;
                hashUsersTracking = null;
                hashMarkerUser = null;
                hashNotMarkerUser = null;
                lastMarker = null;

                while (iterator.hasNext()) {
                    ServiceValue currServiceValue = iterator.next();
                    if (getSelectedItems().containsKey(
                            currServiceValue.getServicevalueCod())
                        && getSelectedItems().get(
                                currServiceValue.getServicevalueCod())) {

                        if ((currServiceValue.getMessage().getCell() != null || (currServiceValue.getMessage().getLatitude() != null && currServiceValue.getMessage().getLongitude() != null))) { // inicio

                            // Message for marker
                            String messDescrip = getMessageDescriptionHeaderMap(currServiceValue);
                            Polygon polygonArea = null;
                            Marker markerArea = null;
                            Double latitude = currServiceValue.getMessage().getLatitude();
                            Double longitude = currServiceValue.getMessage().getLongitude();

                            if (latitude != null && longitude != null) {
                                markerArea = getGPSCellAreaMarker(latitude,
                                        longitude, messDescrip,
                                        String.valueOf(nextMarkerCounter)); // inicio
                                                                            // if
                                                                            // 2
                            } else {
                                // Obtain latitude, longitude and azimuth from
                                // cell
                                latitude = currServiceValue.getMessage().getCell().getLatitudeNum().doubleValue();
                                longitude = currServiceValue.getMessage().getCell().getLongitudeNum().doubleValue();
                                double azimuth = currServiceValue.getMessage().getCell().getAzimuthNum().doubleValue();
                                String siteCell = currServiceValue.getMessage().getCell().getSiteChr();

                                /*
                                 * Si termina con O siginifica que es
                                 * Omnidireccional, entonces se imprime en un
                                 * circulo en un radio de 5 km
                                 */
                                if (!siteCell.toUpperCase().endsWith("O")) { // inicio
                                                                             // if
                                                                             // 3
                                    // SEGMENTED CELL

                                    // Cell polygon
                                    polygonArea = getCellAreaPolygon(latitude,
                                            longitude, azimuth);

                                    // Cell marker
                                    markerArea = getCellAreaMarker(latitude,
                                            longitude, azimuth, messDescrip,
                                            String.valueOf(nextMarkerCounter));

                                } else {
                                    // OMNIDIRECTIONAL CELL

                                    // Cell polygon
                                    polygonArea = getOmniCellAreaPolygon(
                                            latitude, longitude);

                                    // Cell marker
                                    markerArea = getOmniCellAreaMarker(
                                            latitude, longitude, messDescrip,
                                            String.valueOf(nextMarkerCounter));

                                }// fin if 3

                                // Add the polygon
                                if (polygonArea != null) { // inicio if 4
                                    // if the polygon already exists, do not
                                    // add.
                                    Polygon existingPolygon = null;
                                    for (int i = 0; i < getMapModel().getPolygons().size(); i++) {
                                        if (getMapModel().getPolygons().get(i).getPaths().equals(
                                                polygonArea.getPaths())) {
                                            existingPolygon = getMapModel().getPolygons().get(
                                                    i);
                                            break;
                                        }
                                    }

                                    if (existingPolygon == null) { // inicio if
                                                                   // 5
                                        getMapModel().addOverlay(polygonArea);
                                    }// fin if 5
                                }// fin if 4

                            }// fin if 2

                            // Add the marker
                            if (markerArea != null) { // inicio if 6
                                // if the marker already exists, do not add, but
                                // instead
                                // add data to existing one.
                                Marker existingMarker = null;
                                for (int i = 0; i < getMapModel().getMarkers().size(); i++) {
                                    if (getMapModel().getMarkers().get(i).getLatlng().equals(
                                            markerArea.getLatlng())) {
                                        existingMarker = getMapModel().getMarkers().get(
                                                i);// inicio if 7
                                        break;
                                    }// fin if 7
                                }

                                if (existingMarker == null) { // inicio if 8
                                    getMapModel().addOverlay(markerArea);
                                    /* Tiene datos de rastreo */

                                    getHashUsersTracking().put(
                                            currServiceValue,
                                            getListMessageWarning().contains(
                                                    currServiceValue.getColumn3Chr()) ? "3" : "1");
                                    getHashMarkerUser().put(currServiceValue,
                                            markerArea);
                                    getNextServiceValueByUserphone().put(
                                            currServiceValue, currServiceValue);

                                    nextMarkerCounter++;
                                    if (lastBounds != null) { // inicio if 9
                                        boolean inside = markerArea.getLatlng().getLat() > lastBounds.getSouthWest().getLat();
                                        inside = inside
                                            && markerArea.getLatlng().getLat() < lastBounds.getNorthEast().getLat();
                                        inside = inside
                                            && markerArea.getLatlng().getLng() > lastBounds.getSouthWest().getLng();
                                        inside = inside
                                            && markerArea.getLatlng().getLng() < lastBounds.getNorthEast().getLng();

                                        if (inside) { // inicio if 10
                                            oneInsideBounds = true;
                                        }// fin if 10
                                    }// fin if 9

                                    // Add current marker for drawing polyline
                                    // later.
                                    if (!positions.containsKey(currServiceValue.getMessage().getOriginChr())) {
                                        positions.put(
                                                currServiceValue.getMessage().getOriginChr(),
                                                new ArrayList<Marker>());
                                    }
                                    positions.get(
                                            currServiceValue.getMessage().getOriginChr()).add(
                                            markerArea);
                                } else {
                                    existingMarker.setData(((String) existingMarker.getData()).concat(
                                            "<br>").concat(messDescrip));
                                    getHashUsersTracking().put(
                                            currServiceValue,
                                            getListMessageWarning().contains(
                                                    currServiceValue.getColumn3Chr()) ? "3" : "1");
                                    getHashMarkerUser().put(currServiceValue,
                                            existingMarker);
                                    getNextServiceValueByUserphone().put(
                                            currServiceValue, currServiceValue);
                                }// fin if 8

                            }// fin if 6
                        } else {
                            getHashUsersTracking().put(currServiceValue, "2");
                            getHashNotMarkerUser().put(currServiceValue,
                                    currServiceValue);
                            getNextServiceValueByUserphone().put(
                                    currServiceValue, currServiceValue);

                        }// fin if 1
                    }
                }// fin while

                // Add polyline
                if (getMapModel().getMarkers().size() > 0) {
                    if (!oneInsideBounds) {
                        mapCenter = mapModel.getMarkers().get(0).getLatlng();
                    }
                    if (getMapModel().getMarkers().size() > 1) {
                        addMarkersPolylineDiferent(positions, getMapModel());
                    }
                }

            }// fin if principal
            addClientMarker();
        } catch (Exception e) {

        }

        return null;
    }

    public String showDetailMap() {
        if (getGeolocalizationAllowed()) {
            Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();

            mapModel = new DefaultMapModel();
            boolean oneInsideBounds = false;
            Iterator<ServiceValueDetail> iterator = null;
            try{
            	iterator = getDataModelDetail().iterator();
            }catch(Exception e) {
            	
            }
            if(iterator != null) {
            
            int nextMarkerCounter = 1;
            hashUsersTracking = null;
            showLocalize = true;
            while (iterator.hasNext()) {
                ServiceValueDetail currServiceDetailValue = iterator.next();
                if (getSelectedDetailItems().containsKey(
                        currServiceDetailValue.getServicevaluedetailCod())
                    && getSelectedDetailItems().get(
                            currServiceDetailValue.getServicevaluedetailCod())) {

                    if (currServiceDetailValue.getMessage().getCell() != null
                        || (currServiceDetailValue.getMessage().getLatitude() != null && currServiceDetailValue.getMessage().getLongitude() != null)) { // inicio
                                                                                                                                                        // if
                                                                                                                                                        // 1
                        // Obtain latitude, longitude and azimuth from cell
                        // Message for marker
                        String messDescrip = getMessageDescriptionDetailMap(currServiceDetailValue);
                        Polygon polygonArea = null;
                        Marker markerArea = null;
                        Double latitude = currServiceDetailValue.getMessage().getLatitude();
                        Double longitude = currServiceDetailValue.getMessage().getLongitude();

                        if (latitude != null && longitude != null) { // inicio
                                                                     // if 2
                            markerArea = getGPSCellAreaMarker(latitude,
                                    longitude, messDescrip,
                                    String.valueOf(nextMarkerCounter));
                        } else {
                            latitude = currServiceDetailValue.getMessage().getCell().getLatitudeNum().doubleValue();
                            longitude = currServiceDetailValue.getMessage().getCell().getLongitudeNum().doubleValue();
                            double azimuth = currServiceDetailValue.getMessage().getCell().getAzimuthNum().doubleValue();
                            String siteCell = currServiceDetailValue.getMessage().getCell().getSiteChr();

                            if (!siteCell.toUpperCase().endsWith("O")) { // inicio
                                                                         // if 3
                                // SEGMENTED CELL

                                // Cell polygon
                                polygonArea = getCellAreaPolygon(latitude,
                                        longitude, azimuth);

                                // Cell marker
                                markerArea = getCellAreaMarker(latitude,
                                        longitude, azimuth, messDescrip,
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
                            }// fin if 3

                            // Add the polygon
                            if (polygonArea != null) { // inicio if 4
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
                            }// fin if 4

                        }// fin if 2

                        // Add the marker
                        if (markerArea != null) { // inicio if 5
                            // if the marker already exists, do not add, but
                            // instead
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
                                if (lastBounds != null) {
                                    boolean inside = markerArea.getLatlng().getLat() > lastBounds.getSouthWest().getLat();
                                    inside = inside
                                        && markerArea.getLatlng().getLat() < lastBounds.getNorthEast().getLat();
                                    inside = inside
                                        && markerArea.getLatlng().getLng() > lastBounds.getSouthWest().getLng();
                                    inside = inside
                                        && markerArea.getLatlng().getLng() < lastBounds.getNorthEast().getLng();

                                    if (inside) {
                                        oneInsideBounds = true;
                                    }
                                }
                            } else {
                                existingMarker.setData(((String) existingMarker.getData()).concat(
                                        "<br>").concat(messDescrip));

                            }

                        }// fin if 5

                    } // fin if 1
                }
            }// fin while

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
        }// fin if principal
        }
        addClientMarker();
        return null;
    }

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
            geoPoint = GeoUtil.moveGeoPoint(latitude, longitude,
                    calculatedAzimuth, sectorAreaDistance);

            // Add area point to polygon
            polygon.getPaths().add(
                    new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
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
            polygon.getPaths().add(
                    new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
        }

        return polygon;
    }

    protected Marker getCellAreaMarker(double latitude, double longitude, double azimuth, String messageDetail) {
        return getCellAreaMarker(latitude, longitude, azimuth, messageDetail,
                null);
    }

    protected Marker getCellAreaMarker(double latitude, double longitude, double azimuth, String messageDetail, String markerLabel) {
        // Obtain global parameters
        double sectorAreaDistance = Double.valueOf(ApplicationBean.getInstance().getMapCellSectorAreaDistance());

        // Calculate marker point
        GeoUtil.GeoPoint geoPoint = GeoUtil.moveGeoPoint(latitude, longitude,
                azimuth, sectorAreaDistance / 2d);

        // Prepare marker
        Marker marker = new Marker(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
        marker.setTitle(i18n.iValue("web.client.backingBean.abstractServiceBean.message.MessageSendZone"));
        marker.setData("<br>".concat(messageDetail));
        if (markerLabel != null && markerLabel.trim().length() > 0) {
            String markerTextColor = ApplicationBean.getInstance().getMapMarkerTextColor().replaceAll(
                    "#", "");
            String markerBgColor = ApplicationBean.getInstance().getMapMarkerBgColor().replaceAll(
                    "#", "");
            marker.setIcon("https://chart.googleapis.com/chart?chst=d_map_pin_letter&chld=".concat(
                    markerLabel.trim()).concat("|").concat(markerBgColor).concat(
                    "|").concat(markerTextColor));
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
            String markerTextColor = ApplicationBean.getInstance().getMapMarkerTextColor().replaceAll(
                    "#", "");
            String markerBgColor = ApplicationBean.getInstance().getMapMarkerBgColor().replaceAll(
                    "#", "");
            marker.setIcon("https://chart.googleapis.com/chart?chst=d_map_pin_letter&chld=".concat(
                    markerLabel.trim()).concat("|").concat(markerBgColor).concat(
                    "|").concat(markerTextColor));
        }

        return marker;
    }

    protected Marker getGPSCellAreaMarker(double latitude, double longitude, String messageDetail, String markerLabel) {
        // Prepare marker
        Marker marker = new Marker(new LatLng(latitude, longitude));
        marker.setTitle(i18n.iValue("web.client.backingBean.abstractServiceBean.message.MessageSendZone"));
        marker.setData("<br>".concat(messageDetail));
        if (markerLabel != null && markerLabel.trim().length() > 0) {
            String markerTextColor = ApplicationBean.getInstance().getMapMarkerTextColor().replaceAll(
                    "#", "");
            String markerBgColor = ApplicationBean.getInstance().getMapMarkerBgColor().replaceAll(
                    "#", "");
            marker.setIcon("https://chart.googleapis.com/chart?chst=d_map_pin_letter&chld=".concat(
                    markerLabel.trim()).concat("|").concat(markerBgColor).concat(
                    "|").concat(markerTextColor));
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
                geolocalizationAllowed = userwebFacade.getUserRolePrivileges(
                        SecurityBean.getInstance().getLoggedInUserClient().getUserwebCod(),
                        Long.valueOf(String.valueOf(getCodService())), 1L);
            } catch (GenericFacadeException ex) {
                java.util.logging.Logger.getLogger(
                        AbstractServiceBean.class.getName()).log(Level.SEVERE,
                        null, ex);
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
            MapMark mapMarks = mapMarksFacade.findByClientLatLng(
                    userWeb.getClient(), latitude, longitude);

            if (mapMarks == null) {

                mapMarks = new MapMark();
                mapMarks.setClient(userWeb.getClient());
                mapMarks.setLatitudeNum(latitude);
                mapMarks.setLongitudeNum(longitude);
                mapMarks.setUserweb(userWeb);
                mapMarks.setTitleChr(selectedMarker.getTitle());
                mapMarks.setDescriptionChr(selectedMarker.getData().toString());
                mapMarksFacade.create(mapMarks);
            } else {
                mapMarksFacade.editByClientLatLng(userWeb.getClient(),
                        latitude, longitude, selectedMarker.getTitle(),
                        selectedMarker.getData().toString());
            }
        }
    }

    public void deleteClientMarker() {
        if (clientMarker) {
            Client client = userWeb.getClient();
            Double latitude = selectedMarker.getLatlng().getLat();
            Double longitude = selectedMarker.getLatlng().getLng();
            try {
//            	MapMark mapMark = mapMarksFacade.findByClientLatLng(client, latitude, longitude);
//            	mapMark = mapMarksFacade.find(mapMark.getMapMarkCod(),"metaDataList","routeDetails");
//            	
//            	if((mapMark.getMetaDataList() != null && mapMark.getMetaDataList().size() > 0)
//            		|| (mapMark.getRouteDetails() != null && mapMark.getRouteDetails().size() > 0)) {
//            		notifier.log(getClass(), null, Action.ERROR, i18n.iValue("web.client.backingbean.error.restriction.integrity"));
//                    setErrorMessage(i18n.iValue("web.client.backingbean.error.restriction.integrity"));
//            	}else{
//            		mapMarksFacade.remove(mapMark);
//            	}
            	
                mapMarksFacade.removeByClientLatLng(client, latitude, longitude);
            } catch (Exception e) {
                notifier.log(getClass(), null, Action.ERROR, e.getMessage());
                setErrorMessage(i18n.iValue("web.client.backingbean.error.restriction.integrity"));
            }

            if (geolocalizationAllowed && showMapOnHeader) {
                showHeaderMap();
                addClientMarker();
            }
            if (geolocalizationAllowed && showMapOnDetail) {
                showDetailMap();
                addClientMarker();
            }
        }
    }
    
    public HashMap<Long,String> map;
    public HashMap<Long,String> getHashMapMetaData(){
    	if (map == null){
    		map = new HashMap<Long, String>();
    		List<MetaData> list = metaDataFacade.findByClientMetaMember(userWeb.getClient().getClientCod(), 1L, 1L);
    		if (list != null){
    			for (MetaData metaData : list) {
    				if (metaData.getMapMark() != null){
    					MetaData md = metaDataFacade.findByClientMetaMemberAndCode(userWeb.getClient().getClientCod(), 1L, -1L, metaData.getMetaDataPK().getCodeChr());
    					if (md != null){
    						map.put(metaData.getMapMark().getMapMarkCod(), md.getValueChr());
    					}
    				}
				}
    		}
    	}
    	return map;
    }

    public void addClientMarker() {
        List<MapMark> mapMarks = mapMarksFacade.findClientMarks(userWeb.getClient());
        for (MapMark mapMark : mapMarks) {
			if (getHashMapMetaData().containsKey(mapMark.getMapMarkCod())) {
//				MetaData m = metaDataFacade
//						.findByClientMetaMemberAndCode(metaData.getMetaDataPK().getCodClient(), metaData.getMetaDataPK().getCodMeta(),
//								-1L, metaData.getMetaDataPK().getCodeChr());
//				if (m != null) {
					ClientMarker markerClient = new ClientMarker(
							new LatLng(mapMark.getLatitudeNum(),
									mapMark.getLongitudeNum()), getHashMapMetaData().get(mapMark.getMapMarkCod()));
					markerClient.setTitle(mapMark.getTitleChr());
					markerClient.setData(mapMark.getDescriptionChr());
					mapModel.addOverlay(markerClient);
//				}else{
//					ClientMarker markerClient = new ClientMarker(new LatLng(
//							mapMark.getLatitudeNum(), mapMark.getLongitudeNum()),urlIconDefault);
//					markerClient.setTitle(mapMark.getTitleChr());
//					markerClient.setData(mapMark.getDescriptionChr());
//					mapModel.addOverlay(markerClient);
//				}

			} else {
				ClientMarker markerClient = new ClientMarker(new LatLng(
						mapMark.getLatitudeNum(), mapMark.getLongitudeNum()),urlIconDefault);
				markerClient.setTitle(mapMark.getTitleChr());
				markerClient.setData(mapMark.getDescriptionChr());
				mapModel.addOverlay(markerClient);
			}
            

        }
    }

    // END MAPS
    // REPORTS
    public String getPdfReportCabDetailName() {
        return pdfReportCabDetailName;
    }

    public void setPdfReportCabDetailName(String pdfReportName) {
        this.pdfReportCabDetailName = pdfReportName;
    }

    public String getXlsReportCabDetailName() {
        return xlsReportCabDetailName;
    }

    public void setXlsReportCabDetailName(String xlsReportName) {
        this.xlsReportCabDetailName = xlsReportName;
    }

    public String getPdfReportDetailName() {
        return pdfReportDetailName;
    }

    public void setPdfReportDetailName(String pdfReportDetailName) {
        this.pdfReportDetailName = pdfReportDetailName;
    }

    public String getXlsReportDetailName() {
        return xlsReportDetailName;
    }

    public void setXlsReportDetailName(String xlsReportDetailName) {
        this.xlsReportDetailName = xlsReportDetailName;
    }

    public String viewPDFCabDetail() {
        signalReport(ReportType.PDF);
        return generateCabDetailReport(getPdfReportCabDetailName(),
                ReportType.PDF);
    }

    public String viewXLSCabDetail() {
        signalReport(ReportType.XLS);
        return generateCabDetailReport(getXlsReportCabDetailName(),
                ReportType.XLS);
    }

    public String viewPDFDetail() {
        signalReport(ReportType.PDF);
        return generateReportDetail(getPdfReportDetailName(), ReportType.PDF);
    }

    public String viewXLSDetail() {
        signalReport(ReportType.XLS);
        return generateReportDetail(getXlsReportDetailName(), ReportType.XLS);
    }

    public String getCabDetailReportWhere() {
        Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        String classifications = classificationFacade.getClassificationUserwebString(SecurityBean.getInstance().getLoggedInUserClient());

        String where = " AND SV.ENABLED_CHR = '1' AND U.COD_CLIENT = ".concat(
                clientCod.toString()).concat(" AND SV.COD_SERVICE = ").concat(
                getService().getServiceCod().toString());
        where += MessageFormat.format(" and EXISTS "
            + "(select * from USERPHONE_CLASSIFICATION uc "
            + "inner join CLASSIFICATION c on c.CLASSIFICATION_cod = uc.cod_CLASSIFICATION "
            + "where uc.cod_userphone = u.userphone_cod "
            + "AND c.cod_client = u.cod_client "
            + "and uc.cod_classification in ({0})) ", classifications);

        where = where.concat(getCabDetailReportWhereCriteria());
        return where;
    }

    public String getDetailReportWhere() {
        return " AND svd.enabled_chr = '1' AND svd.cod_servicevalue = ".concat(
                serviceValue.getServicevalueCod().toString()).concat(
                getDetailReportWhereCriteria());
    }

    public String getCabDetailReportOrderBy() {
        String sortAttributeColumnName = getAttributeColumnName(
                ServiceValue.class, getSortHelper().getField());
        if (getSortHelper().getField().indexOf(".") < 0) {
            sortAttributeColumnName = "sv.".concat(sortAttributeColumnName);
        } else {
            sortAttributeColumnName = "svd.".concat(sortAttributeColumnName);
        }
        return "ORDER BY ".concat(sortAttributeColumnName).concat(
                getSortHelper().isAscending() ? " ASC" : " DESC");
    }

    public String getDetailReportOrderBy() {
        String sortAttributeColumnName = getAttributeColumnName(
                ServiceValueDetail.class, getSortHelperDetail().getField());
        if (getSortHelperDetail().getField().indexOf(".") < 0) {
            sortAttributeColumnName = "svd.".concat(sortAttributeColumnName);
        } else {
            sortAttributeColumnName = "sv.".concat(sortAttributeColumnName);
        }
        return "ORDER BY ".concat(sortAttributeColumnName).concat(
                getSortHelperDetail().isAscending() ? " ASC" : " DESC");
    }

    public String generateCabDetailReport(String reportName, ReportType reportType) {
        /*
         * obtenemos la informacion sobre el usuario que solicita el reporte, y
         * el cliente al que pertenece
         */
        Userweb userweb = SecurityBean.getInstance().getLoggedInUserClient();
        Client client = userweb.getClient();
        String userInformation = MessageFormat.format("{0} ({1})",
                userweb.getNameChr(), client.getNameChr());

        Map<Object, Object> params = new HashMap<Object, Object>();
        params.put("WHERE", getCabDetailReportWhere());
        params.put("ORDER_BY", getCabDetailReportOrderBy());
        params.put("USER", userInformation);
        params.put("SUBREPORT_DIR", getReportsPath().concat("/"));
        params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
        params.put(JRParameter.REPORT_RESOURCE_BUNDLE, i18n.getResourceBundle());

        ClientFile cf = fileFacade.getClientLogo(getUserweb().getClient());
        try {
            if (cf != null) {
                params.put("CLIENT_LOGO",
                        JRImageLoader.loadImage(cf.getFileByt()));
            }
        } catch (JRException ex) {
        }

        Connection conn = SMBaseBean.getDatabaseConnecction();
        JasperReportUtils.respondReport(reportName, params, true, conn,
                reportType);

        return null;
    }

    public String generateReportDetail(String reportName, ReportType reportType) {

        /*
         * obtenemos la informacion sobre el usuario que solicita el reporte, y
         * el cliente al que pertenece
         */
        Userweb userweb = SecurityBean.getInstance().getLoggedInUserClient();
        Client client = userweb.getClient();
        String userInformation = MessageFormat.format("{0} ({1})",
                userweb.getNameChr(), client.getNameChr());

        Map<Object, Object> params = new HashMap<Object, Object>();
        params.put("WHERE", getDetailReportWhere());
        params.put("ORDER_BY", getDetailReportOrderBy());
        params.put("USER", userInformation);
        params.put("SUBREPORT_DIR", getReportsPath().concat("/"));
        params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
        params.put(JRParameter.REPORT_RESOURCE_BUNDLE, i18n.getResourceBundle());

        ClientFile cf = fileFacade.getClientLogo(getUserweb().getClient());
        try {
            if (cf != null) {
                params.put("CLIENT_LOGO",
                        JRImageLoader.loadImage(cf.getFileByt()));
            }
        } catch (JRException ex) {
        }
        Connection conn = SMBaseBean.getDatabaseConnecction();
        JasperReportUtils.respondReport(reportName, params, true, conn,
                reportType);
        return null;
    }

    // AUXILIARY GENERIC METHODS
    public String getPrimarySortedField() throws PrimarySortedFieldNotFoundException {
        if (primarySortedField == null) {
            Field[] fieds = ServiceValue.class.getDeclaredFields();
            for (Field field : fieds) {
                PrimarySortedField annotation = field.getAnnotation(PrimarySortedField.class);
                if (annotation != null) {
                    primarySortedField = field.getName();
                    primarySortedFieldAsc = annotation.ascending();
                    break;
                }
            }
            if (primarySortedField == null) {
                throw new PrimarySortedFieldNotFoundException(ServiceValue.class);
            }
        }
        return primarySortedField;
    }

    public String getPrimarySortedFieldDetail() throws PrimarySortedFieldNotFoundException {
        if (primarySortedFieldDetail == null) {
            Field[] fieds = ServiceValueDetail.class.getDeclaredFields();
            for (Field field : fieds) {
                PrimarySortedField annotation = field.getAnnotation(PrimarySortedField.class);
                if (annotation != null) {
                    primarySortedFieldDetail = field.getName();
                    primarySortedFieldDetailAsc = annotation.ascending();
                    break;
                }
            }
            if (primarySortedFieldDetail == null) {
                throw new PrimarySortedFieldNotFoundException(ServiceValue.class);
            }
        }
        return primarySortedFieldDetail;
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
                if (annotation != null && annotation.name() != null
                    && !annotation.name().isEmpty()) {
                    return annotation.name();
                }
            }
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(
                    AbstractServiceBean.class.getName()).log(Level.SEVERE,
                    i18n.iValue("web.client.backingBean.message.Error"), e);
        }

        return null;
    }

    public void setPrimarySortedField(String primarySortedField) {
        this.primarySortedField = primarySortedField;
    }

    public boolean showAllInQuantityList() {
        return false;
    }

    public String viewDetails() {
        serviceValue = null;
        Iterator<ServiceValue> iterator = getDataModelHeader().iterator();
        while (iterator.hasNext()) {
            ServiceValue currServiceValue = iterator.next();
            if (getSelectedItems().get(currServiceValue.getServicevalueCod())) {
                if (serviceValue == null) {
                    serviceValue = facade.find(currServiceValue.getServicevalueCod());
                } else {
                    serviceValue = null;
                    setWarnMessage(i18n.iValue("web.client.backingBean.abstractServiceBean.message.MustSelectJustOne"));
                    return null;
                }
            }
        }
        if (serviceValue == null) {
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
        dataModelDetail = getPaginationHelperDetail().createPageDataModel();
        if (geolocalizationAllowed && showMapOnHeader) {
            showHeaderMap();
            addClientMarker();
        }
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

    /*
     * sobreescribir en cada clase segun los metadatos a incluir en el detalle
     */
    public void detailMetaDataFromModel() {
    }

    public void headerMetaDataFromModel() {
    }

    protected void signalRead() {
        String criterio = getCabDetailWhereCriteria().isEmpty() ? "" : ". ".concat(
                i18n.iValue("web.client.backingBean.abstractServiceBean.message.Criteria")).concat(
                ": ").concat(getCabDetailWhereCriteria());
        String action = "";
        if (getService() != null) {
            action = i18n.iValue(
                    "web.client.backingBean.abstractServiceBean.message.ServiceRecordRequest").concat(
                    " ").concat(getService().getDescriptionChr()).concat(
                    criterio).concat(".");
        } else {
            action = i18n.iValue(
                    "web.client.backingBean.abstractServiceBean.message.HistoricalServicesRecordsRequest").concat(
                    criterio).concat(".");
        }

        notifier.signal(getClass(), Action.READ, userWeb, null,
                this.getCurrentViewId(), action, getSessionId(), getIpAddress());
    }

    protected void signalRead(String action) {
        notifier.signal(getClass(), Action.READ, userWeb, null,
                this.getCurrentViewId(), action, getSessionId(), getIpAddress());
    }

    protected void signalReport(ReportType repType) {
        String criterio = getCabDetailWhereCriteria().isEmpty() ? "" : i18n.iValue(
                "web.client.backingBean.abstractServiceBean.message.Criteria").concat(
                getCabDetailWhereCriteria());
        String action = "";
        if (getService() != null) {
            action = i18n.iValue(
                    "web.client.backingBean.abstractServiceBean.message.ServiceRecordRequest").concat(
                    getService().getDescriptionChr()).concat(criterio).concat(
                    ".");
        } else {
            action = i18n.iValue(
                    "web.client.backingBean.abstractServiceBean.message.HistoricalServicesRecordsRequest").concat(
                    criterio).concat(".");
        }
        notifier.signal(
                this.getClass(),
                Action.REPORT,
                userWeb,
                null,
                this.getCurrentViewId(),
                repType.name().concat(
                        i18n.iValue("web.client.backingBean.abstractServiceBean.message.Report")).concat(
                        action), getSessionId(), getIpAddress());
    }

    protected Userweb getUserweb() {
        return userWeb;
    }

    public Map<Object, String> getHashUsersTracking() {
        if (hashUsersTracking == null) {
            hashUsersTracking = new HashMap<Object, String>();
        }
        return hashUsersTracking;
    }

    public void setHashUsersTracking(Map<Object, String> hashUsersTracking) {
        this.hashUsersTracking = hashUsersTracking;
    }

    public Boolean getShowLocalize() {
        return showLocalize;
    }

    public void setShowLocalize(Boolean showLocalize) {
        this.showLocalize = showLocalize;
    }

    public Object getSelectedItemTracking() {
        return selectedItemTracking;
    }

    public void setSelectedItemTracking(Object selectedItemTracking) {
        this.selectedItemTracking = selectedItemTracking;
    }

    public String applyResumeTrackingSuccess() {
        if (selectedItemTracking != null) {
            Marker marker = null;
            resume = getHashMapGlobalMessageTracking().get(
                    ((ServiceValue) getNextServiceValueByUserphone().get(
                            selectedItemTracking)).getColumn3Chr());
            resume = resume == null ? "" : resume;
            marker = (Marker) getHashMarkerUser().get(selectedItemTracking);

            if (lastMarker != null) {
                getMapModel().getMarkers().remove(lastMarker);
//                String markerTextColor = ApplicationBean.getInstance().getMapMarkerTextColor().replaceAll(
//                        "#", "");
//                String markerBgColor = ApplicationBean.getInstance().getMapMarkerBgColor().replaceAll(
//                        "#", "");
//                lastMarker.setIcon("https://chart.googleapis.com/chart?chst=d_map_pin_letter&chld=".concat(
//                        "|").concat(markerBgColor).concat("|").concat(
//                        markerTextColor));
                lastMarker.setIcon(null);
                lastMarker.setIcon("https://maps.google.com/mapfiles/ms/micons/red-dot.png");
                getMapModel().addOverlay(lastMarker);
            }

            getMapModel().getMarkers().remove(marker);
            marker.setIcon(null);
            marker.setIcon("https://maps.google.com/mapfiles/ms/micons/yellow-dot.png");
            getMapModel().addOverlay(marker);
            lastMarker = marker;
        }
        return null;
    }

    public String applyResumeTrackingNoSuccess() {
        if (selectedItemTracking != null) {
            try {
                /*
                 * el mensaje puede ser nulo, ya que podemos estar mostrando una
                 * localizacion que no tiene mensaje por ser un rastreo viejo
                 */
                resume = getHashMapGlobalMessageTracking().get(
                        ((ServiceValue) getNextServiceValueByUserphone().get(
                                selectedItemTracking)).getColumn3Chr());
            } catch (Exception e) {
                resume = getHashMapGlobalMessageTracking().get(
                        "tracking.status.NoStatusMessage");
            }

            if (lastMarker != null) {
                getMapModel().getMarkers().remove(lastMarker);
                lastMarker.setIcon(null);
                lastMarker.setIcon("https://maps.google.com/mapfiles/ms/micons/red-dot.png");
                getMapModel().addOverlay(lastMarker);
            }

        }
        return null;
    }

    public Map<Object, Object> getNextServiceValueByUserphone() {
        if (nextServiceValueByUserphone == null) {
            nextServiceValueByUserphone = new HashMap<Object, Object>();
        }
        return nextServiceValueByUserphone;
    }

    public void setNextServiceValueByUserphone(Map<Object, Object> nextServiceValueByUserphone) {
        this.nextServiceValueByUserphone = nextServiceValueByUserphone;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public Map<Object, Object> getHashMarkerUser() {
        if (hashMarkerUser == null) {
            hashMarkerUser = new HashMap<Object, Object>();
        }
        return hashMarkerUser;
    }

    public void setHashMarkerUser(Map<Object, Object> hashMarkerUser) {
        this.hashMarkerUser = hashMarkerUser;
    }

    public Map<Object, Object> getHashNotMarkerUser() {
        if (hashNotMarkerUser == null) {
            hashNotMarkerUser = new HashMap<Object, Object>();
        }
        return hashNotMarkerUser;
    }

    public void setHashNotMarkerUser(Map<Object, Object> hashNotMarkerUser) {
        this.hashNotMarkerUser = hashNotMarkerUser;
    }

    public Map<String, String> getHashMapGlobalMessageTracking() {
        if (hashMapGlobalMessageTracking == null) {
            hashMapGlobalMessageTracking = new HashMap<String, String>();
            try {
                hashMapGlobalMessageTracking.put(
                        "tracking.status.NoStatusMessage",
                        globalParameterFacade.findByCode("tracking.status.NoStatusMessage"));
                hashMapGlobalMessageTracking.put("tracking.status.OTA",
                        globalParameterFacade.findByCode("tracking.status.OTA"));
                hashMapGlobalMessageTracking.put("tracking.status.LBS",
                        globalParameterFacade.findByCode("tracking.status.LBS"));
                hashMapGlobalMessageTracking.put(
                        "tracking.status.AndroidCellInfoGpsUnknowState",
                        globalParameterFacade.findByCode("tracking.status.AndroidCellInfoGpsUnknowState"));
                hashMapGlobalMessageTracking.put(
                        "tracking.status.AndroidCellInfoGpsOn",
                        globalParameterFacade.findByCode("tracking.status.AndroidCellInfoGpsOn"));
                hashMapGlobalMessageTracking.put(
                        "tracking.status.AndroidCellInfoGpsOff",
                        globalParameterFacade.findByCode("tracking.status.AndroidCellInfoGpsOff"));
                hashMapGlobalMessageTracking.put(
                        "tracking.status.AndroidGeoPoint",
                        globalParameterFacade.findByCode("tracking.status.AndroidGeoPoint"));
                hashMapGlobalMessageTracking.put(
                        "tracking.status.AndroidNoApp",
                        globalParameterFacade.findByCode("tracking.status.AndroidNoApp"));
                hashMapGlobalMessageTracking.put(
                        "tracking.status.NoLocation",
                        globalParameterFacade.findByCode("tracking.status.NoLocation"));
                hashMapGlobalMessageTracking.put(
                        "tracking.status.NoLocationNoCellInfo",
                        globalParameterFacade.findByCode("tracking.status.NoLocationNoCellInfo"));

            } catch (GenericFacadeException e) {
                notifier.error(getClass(), null, e.getMessage(), e);
            }
        }
        return hashMapGlobalMessageTracking;
    }

    public void setHashMapGlobalMessageTracking(Map<String, String> hashMapGlobalMessageTracking) {
        this.hashMapGlobalMessageTracking = hashMapGlobalMessageTracking;
    }

    private List<String> listMessageWarning;

    public List<String> getListMessageWarning() {
        if (listMessageWarning == null) {
            listMessageWarning = new ArrayList<String>();
            try {
                String msg = globalParameterFacade.findByCode("tracking.status.warning");
                if (msg != null) {
                    StringTokenizer tkn = new StringTokenizer(msg, "|");
                    while (tkn.hasMoreTokens()) {
                        listMessageWarning.add(tkn.nextToken());
                    }
                }
            } catch (GenericFacadeException e) {
                notifier.error(getClass(), null, e.getMessage(), e);
            }
        }

        return listMessageWarning;
    }

}

class ClientMarker extends Marker {

    private static final long serialVersionUID = 3850314913292747045L;

    public ClientMarker(LatLng latLng,String url) {
        super(latLng, null, null, url);
    }

}
