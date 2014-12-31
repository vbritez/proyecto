package com.tigo.cs.view.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.util.JRImageLoader;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.tigo.cs.api.facade.jms.FileMetaDataMessage;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.PrimarySortedFieldNotFoundException;
import com.tigo.cs.commons.jpa.Searchable;
import com.tigo.cs.commons.jpa.WarningTypeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.report.JasperReportUtils;
import com.tigo.cs.commons.report.ReportType;
import com.tigo.cs.commons.util.ListDataModelViewCsTigo;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.commons.util.PaginationHelper;
import com.tigo.cs.commons.util.SortHelper;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.ClientFile;
import com.tigo.cs.domain.Meta;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.MetaDataPK;
import com.tigo.cs.domain.MetaMember;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.facade.ClientFileFacade;
import com.tigo.cs.facade.I18nBean;
import com.tigo.cs.facade.MetaClientFacade;
import com.tigo.cs.facade.MetaDataFacade;
import com.tigo.cs.facade.MetaFacade;
import com.tigo.cs.facade.MetaMemberFacade;
import com.tigo.cs.facade.ShiftPeriodFacade;
import com.tigo.cs.facade.UserphoneFacade;
import com.tigo.cs.security.Notifier;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.ApplicationBean;

/**
 * 
 * @author Miguel Zorrilla
 * 
 */
public abstract class AbstractCrudBean extends SMBaseBean {

    private static final long serialVersionUID = 3876256880274313322L;
    protected String okMessageOnDelete;
    protected String okMessageOnDeleteByGroup;
    protected String warnMessageOnDeleteConstraintByGroup;
    protected String warnMessageOnDeleteConstraint;
    protected String errorMessageOnDelete;
    protected String errorMessageOnDeleteByGroup;
    private MetaData entity;
    @EJB
    protected MetaDataFacade facade;
    @EJB
    private MetaClientFacade metaClientFacade;
    @EJB
    private MetaMemberFacade metaMemberFacade;
    @EJB
    protected ClientFileFacade fileFacade;
    @EJB
    protected Notifier notifier;
    @EJB
    protected MetaDataFacade metaDataFacade;
    @EJB
    protected MetaFacade metaFacade;
    @EJB
    protected UserphoneFacade userPhoneFacade;
    @EJB
    protected ShiftPeriodFacade shiftPeriodFacade;
    @EJB
    protected I18nBean i18n;
    @EJB
    protected FacadeContainer facadeContainer;
    protected DataModel<MetaData> dataModel;
    protected Map<Object, Boolean> selectedItems;
    protected PaginationHelper paginationHelper;
    protected SortHelper sortHelper;
    protected String filterSelectedField;
    protected String filterCriteria;
    protected String aditionalFilter;
    private List<SelectItem> filterFields;
    private String primarySortedField;
    private boolean primarySortedFieldAsc = true;
    protected Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private List<SelectItem> rowQuantList;
    private String rowQuantSelected;
    private String pdfReportName = "repcrudmetadata";
    private String xlsReportName = "repcrudmetadata_xls";
    private final Userweb userweb = SecurityBean.getInstance().getLoggedInUserClient();
    private boolean metaOptionSectionVisible = false;
    private String saveUserphone = "false";
    private String messageUserphonesNotExist = "";
    private boolean userphoneNotExist;

    public enum Operation {

        CREATE, READ, UPDATE, DELETE
    };

    private String fileName = null;
    protected UploadedFile file;
    private boolean uploadSectionVisible = false;
    private boolean fileReady = false;
    protected String metaLabel = null;
    protected Long cod_meta;
    protected Long cod_member;
    private List<MetaMember> metamemberList;
    private List<MetaMember> selectedMetaMemberList;
    protected boolean associateSectionVisible = false;
    protected List<Userphone> userphoneList;
    protected Userphone selectedUserphone;
    protected List<Userphone> selectedUserphoneList;
    protected Boolean validatedAllUserphones = false;
    protected List<Userphone> selectedAssociatedUserphones;
    protected List<Userphone> associatedUserphonesList;
    protected Boolean validatedAllUserphonesAssociated = false;

    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------
    public AbstractCrudBean(Long cod_meta, Long cod_member) {
        if (cod_meta == null || cod_member == null) {
            throw new IllegalArgumentException(i18n.iValue("web.client.backingBean.abstractCrudBean.message.NullsAreNotAllowed"));
        }
        this.cod_meta = cod_meta;
        this.cod_member = cod_member;
        String where = "o.metaDataPK.codClient = %s AND o.metaDataPK.codMeta = %s AND o.metaDataPK.codMember = %s";
        aditionalFilter = String.format(where,
                getUserweb().getClient().getClientCod(),
                String.valueOf(cod_meta), String.valueOf(cod_member));
        initialize();
    }

    private void initialize() {

        // Initialize filter criteria with no filter
        filterSelectedField = "-1";
        filterCriteria = "";

        // Initialize sort with default values
        sortHelper = new SortHelper();
        try {
            sortHelper.setField(getPrimarySortedField());
            sortHelper.setAscending(primarySortedFieldAsc);
        } catch (PrimarySortedFieldNotFoundException ex) {
            java.util.logging.Logger.getLogger(AbstractCrudBean.class.getName()).log(
                    Level.SEVERE,
                    i18n.iValue("web.client.backingBean.message.Error")
                        + ex.getMessage(), ex);
        }
    }

    public void setMetaLabel(String metaLabel) {
        this.metaLabel = metaLabel;
    }

    public String getMetaLabel() {
        return metaLabel;
    }

    protected Userweb getUserweb() {
        return userweb;
    }

    public String getErrorMessageOnDelete() {
        if (errorMessageOnDelete == null) {
            errorMessageOnDelete = i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletingError");
        }
        return errorMessageOnDelete;
    }

    public void setErrorMessageOnDelete(String errorMessageOnDelete) {
        this.errorMessageOnDelete = errorMessageOnDelete;
    }

    public String getErrorMessageOnDeleteByGroup() {
        if (errorMessageOnDeleteByGroup == null) {
            errorMessageOnDeleteByGroup = i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletingByGroupError");
        }
        return errorMessageOnDeleteByGroup;
    }

    public void setErrorMessageOnDeleteByGroup(String errorMessageOnDeleteByGroup) {
        this.errorMessageOnDeleteByGroup = errorMessageOnDeleteByGroup;
    }

    public String getOkMessageOnDelete() {
        if (okMessageOnDelete == null) {
            okMessageOnDelete = i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletingSucces");
        }
        return okMessageOnDelete;
    }

    public void setOkMessageOnDelete(String okMessageOnDelete) {
        this.okMessageOnDelete = okMessageOnDelete;
    }

    public String getOkMessageOnDeleteByGroup() {
        if (okMessageOnDeleteByGroup == null) {
            okMessageOnDeleteByGroup = i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletingByGroupSuccess");
        }
        return okMessageOnDeleteByGroup;
    }

    public void setOkMessageOnDeleteByGroup(String okMessageOnDeleteByGroup) {
        this.okMessageOnDeleteByGroup = okMessageOnDeleteByGroup;
    }

    public String getWarnMessageOnDeleteConstraint() {
        if (warnMessageOnDeleteConstraint == null) {
            warnMessageOnDeleteConstraint = i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletringConstraintError");
        }
        return warnMessageOnDeleteConstraint;
    }

    public void setWarnMessageOnDeleteConstraint(String warnMessageOnDeleteConstraint) {
        this.warnMessageOnDeleteConstraint = warnMessageOnDeleteConstraint;
    }

    public String getWarnMessageOnDeleteConstraintByGroup() {
        if (warnMessageOnDeleteConstraintByGroup == null) {
            warnMessageOnDeleteConstraintByGroup = i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletringConstraintByGroupError");
        }
        return warnMessageOnDeleteConstraintByGroup;
    }

    public void setWarnMessageOnDeleteConstraintByGroup(String warnMessageOnDeleteConstraintByGroup) {
        this.warnMessageOnDeleteConstraintByGroup = warnMessageOnDeleteConstraintByGroup;
    }

    // MESSAGE
    // --------------------------------------------------------------------------------------
    // LIST AND TABLE METHODS
    // --------------------------------------------------------------------------------------
    public PaginationHelper getPaginationHelper() {
        if (paginationHelper == null) {
            int pageSize = getRowQuantSelected().length() > 0 ? Integer.valueOf(
                    getRowQuantSelected()).intValue() : 0;

            paginationHelper = new PaginationHelper(pageSize) {

                Integer count = null;

                @Override
                public int getItemsCount() {
                    String where = null;
                    if (count == null) {
                        if (!filterSelectedField.equals("-1")
                            && filterCriteria.length() > 0) {
                            Class<?> fieldClass = getFieldType(filterSelectedField);

                            if (fieldClass.equals(Integer.class)
                                || fieldClass.equals(Long.class)
                                || fieldClass.equals(BigInteger.class)) {
                                where = "where o.".concat(filterSelectedField).concat(
                                        " = ").concat(filterCriteria);
                            } else if (fieldClass.equals(String.class)) {
                                where = "where lower(o.".concat(
                                        filterSelectedField).concat(") LIKE '%").concat(
                                        filterCriteria.toLowerCase()).concat(
                                        "%'");
                            }
                        }
                        if (aditionalFilter != null
                            && aditionalFilter.trim().length() > 0) {
                            if (where == null) {
                                where = "where ";
                            } else {
                                where = where.concat(" AND");
                            }
                            where = where.concat(" (").concat(
                                    aditionalFilter.trim()).concat(") ");
                        }
                        count = facade.count(where);
                    }
                    return count;
                }

                @Override
                public DataModel createPageDataModel() {
                    String where = null;
                    if (!filterSelectedField.equals("-1")
                        && filterCriteria.length() > 0) {
                        Class<?> fieldClass = getFieldType(filterSelectedField);

                        if (fieldClass.equals(Integer.class)
                            || fieldClass.equals(Long.class)
                            || fieldClass.equals(BigInteger.class)) {
                            where = "where o.".concat(filterSelectedField).concat(
                                    " = ").concat(filterCriteria);
                        } else if (fieldClass.equals(String.class)) {
                            where = "where lower(o.".concat(filterSelectedField).concat(
                                    ") LIKE '%").concat(
                                    filterCriteria.toLowerCase()).concat("%'");
                        }
                    }
                    if (aditionalFilter != null
                        && aditionalFilter.trim().length() > 0) {
                        if (where == null) {
                            where = "where ";
                        } else {
                            where = where.concat(" AND");
                        }
                        where = where.concat(" (").concat(
                                aditionalFilter.trim()).concat(") ");
                    }
                    String beginOrderBy = sortHelper.getField().equals(
                            "codeChr") ? "o.metaDataPK." : "o.";
                    String orderby = beginOrderBy.concat(sortHelper.getField()).concat(
                            sortHelper.isAscending() ? " ASC" : " DESC");

                    return new ListDataModelViewCsTigo(facade.findRange(
                            new int[] {
                                    getPageFirstItem(),
                                    getPageFirstItem() + getPageSize() },
                            where, orderby));
                }
            };
        }

        return paginationHelper;
    }

    protected void setPaginationHelper(PaginationHelper paginationHelper) {
        this.paginationHelper = paginationHelper;
    }

    public DataModel<MetaData> getDataModel() {
        if (dataModel == null) {
            dataModel = getPaginationHelper().createPageDataModel();
        }

        return dataModel;
    }

    public void setDataModel(DataModel<MetaData> dataModel) {
        this.dataModel = dataModel;
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

    public List<SelectItem> getFilterFields() {
        if (filterFields == null) {
            filterFields = new ArrayList<SelectItem>();

            Field[] fields = getDataClass().getSuperclass().getDeclaredFields();
            for (Field field : fields) {
                Searchable annotation = field.getAnnotation(Searchable.class);
                if (annotation != null) {
                    String colLabel = this.getMetaLabel() != null
                        && annotation.label().equals("Valor") ? this.getMetaLabel() : i18n.iValue(annotation.label());
                    if (annotation.position() < 0
                        || annotation.position() > filterFields.size()) {
                        filterFields.add(new SelectItem(field.getName().concat(
                                !annotation.internalField().isEmpty() ? ".".concat(annotation.internalField()) : ""), colLabel));
                    } else {
                        filterFields.add(
                                annotation.position(),
                                new SelectItem(field.getName().concat(
                                        !annotation.internalField().isEmpty() ? ".".concat(annotation.internalField()) : ""), colLabel));
                    }
                }
            }

            Field[] fieds = getDataClass().getDeclaredFields();
            for (Field field : fieds) {
                Searchable annotation = field.getAnnotation(Searchable.class);
                if (annotation != null) {
                    String colLabel = this.getMetaLabel() != null
                        && annotation.label().equals("Valor") ? this.getMetaLabel() : i18n.iValue(annotation.label());
                    if (annotation.position() < 0
                        || annotation.position() > filterFields.size()) {
                        filterFields.add(new SelectItem(field.getName().concat(
                                !annotation.internalField().isEmpty() ? ".".concat(annotation.internalField()) : ""), colLabel));
                    } else {
                        filterFields.add(
                                annotation.position(),
                                new SelectItem(field.getName().concat(
                                        !annotation.internalField().isEmpty() ? ".".concat(annotation.internalField()) : ""), colLabel));
                    }
                }
            }
        }

        return filterFields;
    }

    public <M> List<SelectItem> getFilterFieldsPlusMembers() {
        if (filterFields == null) {
            filterFields = new ArrayList<SelectItem>();
            Field[] fieds = getDataClass().getDeclaredFields();
            for (Field field : fieds) {
                Searchable annotation = field.getAnnotation(Searchable.class);
                if (annotation != null) {
                    String colLabel = this.getMetaLabel() != null
                        && annotation.label().equals("Valor") ? this.getMetaLabel() : i18n.iValue(annotation.label());
                    if (annotation.position() < 0
                        || annotation.position() > filterFields.size()) {
                        filterFields.add(new SelectItem(field.getName().concat(
                                !annotation.internalField().isEmpty() ? ".".concat(annotation.internalField()) : ""), colLabel));
                    } else {
                        filterFields.add(
                                annotation.position(),
                                new SelectItem(field.getName().concat(
                                        !annotation.internalField().isEmpty() ? ".".concat(annotation.internalField()) : ""), colLabel));
                    }
                }
            }
        }

        return filterFields;
    }

    public SortHelper getSortHelper() {
        return sortHelper;
    }

    public void setSortHelper(SortHelper sortHelper) {
        this.sortHelper = sortHelper;
    }

    public String getFilterCriteria() {
        return filterCriteria;
    }

    public void setFilterCriteria(String filterCriteria) {
        this.filterCriteria = filterCriteria;
    }

    public String getFilterSelectedField() {
        return filterSelectedField;
    }

    public void setFilterSelectedField(String filterSelectedField) {
        this.filterSelectedField = filterSelectedField;
    }

    public String getAditionalFilter() {
        return aditionalFilter;
    }

    public void setAditionalFilter(String aditionalFilter) {
        this.aditionalFilter = aditionalFilter;
    }

    public String nextPage() {
        getPaginationHelper().nextPage();
        dataModel = null; // For data model recreation
        selectedItems = null; // For clearing selection
        return null;
    }

    public String previousPage() {
        getPaginationHelper().previousPage();
        dataModel = null; // For data model recreation
        selectedItems = null; // For clearing selection
        return null;
    }

    public Userphone getSelectedUserphone() {
        return selectedUserphone;
    }

    public void setSelectedUserphone(Userphone selectedUserphone) {
        this.selectedUserphone = selectedUserphone;
    }

    public List<Userphone> getUserphoneList() {
        if (userphoneList == null) {
            userphoneList = new ArrayList<Userphone>();
        }
        return userphoneList;
    }

    public void setUserphoneList(List<Userphone> userphoneList) {
        this.userphoneList = userphoneList;
    }

    // FILTER AND SORT METHODS
    // --------------------------------------------------------------------------------------
    public String applyFilter() {
        if (!filterSelectedField.equals("-1")) {
            Class<?> fieldClass = getFieldType(filterSelectedField);

            if ((fieldClass.equals(Integer.class) && !NumberUtil.isInteger(filterCriteria))
                || (fieldClass.equals(Long.class) && !NumberUtil.isLong(filterCriteria))
                || (fieldClass.equals(BigInteger.class) && !NumberUtil.isBigInteger(filterCriteria))) {
                setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustBeInteger"));
                filterCriteria = "";
            } else {
                signalRead();
            }
            paginationHelper = null; // For pagination recreation
            dataModel = null; // For data model recreation
            selectedItems = null; // For clearing selection
        } else {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectFindOption"));
        }
        return null;
    }

    public String cleanFilter() {
        filterSelectedField = "-1";
        filterCriteria = "";
        paginationHelper = null; // For pagination recreation
        dataModel = null; // For data model recreation
        selectedItems = null; // For clearing selection
        return null;
    }

    public String applySort() {
        dataModel = null; // For data model recreation
        selectedItems = null; // For clearing selection
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
            rowQuantSelected = ApplicationBean.getInstance().getDefaultCrudPaginationPageSize();
        }
        return rowQuantSelected;
    }

    public void setRowQuantSelected(String rowQuantSelected) {
        this.rowQuantSelected = rowQuantSelected;
    }

    public void applyQuantity(ValueChangeEvent evnt) {
        paginationHelper = null; // For pagination recreation
        dataModel = null; // For data model recreation
        selectedItems = null; // For clearing selection
    }

    public boolean showAllInQuantityList() {
        return false;
    }

    // REPORT METHODS
    // --------------------------------------------------------------------------------------
    public String getPdfReportName() {
        return pdfReportName;
    }

    public void setPdfReportName(String pdfReportName) {
        this.pdfReportName = pdfReportName;
    }

    public String getXlsReportName() {
        return xlsReportName;
    }

    public void setXlsReportName(String xlsReportName) {
        this.xlsReportName = xlsReportName;
    }

    public String getWhereReport() {
        Class<?> fieldClass = getFieldType(getFilterSelectedField());
        String where = "";
        if (!fieldClass.equals(Object.class)) {
            String filterAttributeColumnName = getAttributeColumnName(getFilterSelectedField());
            filterAttributeColumnName = "o.".concat(filterAttributeColumnName);

            if (fieldClass.getSuperclass().equals(Number.class)) {
                where = " AND ".concat(filterAttributeColumnName).concat(" = ").concat(
                        getFilterCriteria());
            } else {
                where = " AND lower(".concat(filterAttributeColumnName).concat(
                        ") LIKE '%").concat(getFilterCriteria().toLowerCase()).concat(
                        "%'");
            }
        }
        if (getReportWhereCriteria() != null) {
            where = where.concat(getReportWhereCriteria());
        }
        return where;
    }

    public String viewPDF() {
        generateReport(getPdfReportName(), ReportType.PDF);
        signalReport(ReportType.PDF);
        return null;
    }

    public String viewXLS() {
        generateReport(getXlsReportName(), ReportType.XLS);
        signalReport(ReportType.XLS);
        return null;
    }

    public String getOrderByReport() {
        // Prepare orderby clause
        String sortAttributeColumnName = getAttributeColumnName(getSortHelper().getField());
        if (getSortHelper().getField().indexOf(".") < 0) {
            sortAttributeColumnName = "o.".concat(sortAttributeColumnName);
        } /*
           * la columna a ordenar es parte de la clave primaria de los metadatas
           */else if (getSortHelper().getField().toUpperCase().contains("PK")) {
            sortAttributeColumnName = "o.".concat(sortAttributeColumnName);
        } else {
            sortAttributeColumnName = "o1.".concat(sortAttributeColumnName);
        }
        return "ORDER BY ".concat(sortAttributeColumnName).concat(
                getSortHelper().isAscending() ? " ASC" : " DESC");
    }

    public String generateReport(String reportName, ReportType reportType) {

        Map<Object, Object> params = new HashMap<Object, Object>();
        params.put("WHERE", getWhereReport());
        params.put("ORDER_BY", getOrderByReport());
        String label = getMetaLabel() != null ? getMetaLabel() : "Valor";
        params.put("META_LABEL", label);
        params.put(
                "TITLE",
                MessageFormat.format(
                        i18n.iValue("web.client.backingBean.abstractCrudBean.message.ListOf"),
                        label));
        params.put("USER",
                SecurityBean.getInstance().getLoggedInUserClient().getNameChr());
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

    // EDITING METHODS
    // --------------------------------------------------------------------------------------
    public String newEntity() {
        entity = new MetaData();
        if (getEntity() != null) {
            getEntity().setMetaDataPK(new MetaDataPK());
        }
        return null;
    }

    public String editEntity(String... collectionNames) {
        entity = null;
        for (MetaData metaData : getDataModel()) {
            if (getSelectedItems().get(metaData.getMetaDataPK())) {
                if (entity == null) {
                    entity = facade.find(metaData.getMetaDataPK(),
                            collectionNames);
                } else {
                    entity = null;
                    setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectJustOneEdit"));
                    return null;
                }
            }
        }
        if (entity == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneEdit"));
        }

        return null;
    }

    public String editEntity() {

        entity = null;
        for (MetaData metaData : dataModel) {
            if (getSelectedItems().get(metaData.getMetaDataPK())) {
                if (entity == null) {
                    entity = facade.find(metaData.getMetaDataPK());
                } else {
                    entity = null;
                    setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectJustOneEdit"));
                    return null;
                }
            }
        }

        if (entity == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneEdit"));
        }

        return null;
    }

    public String deleteEntitiesMass() {
        int retVal;
        try {
            retVal = facade.count(
                    MessageFormat.format(
                            " where o.metaDataPK.codClient = {0} and o.metaDataPK.codMeta = {1} ",
                            getUserweb().getClient().getClientCod(), cod_meta)).intValue();
            facade.deleteUserphoneMetadataRelation(
                    getUserweb().getClient().getClientCod(), cod_meta);
            facade.deleteMetaDataClient(
                    getUserweb().getClient().getClientCod(), cod_meta);
            Meta meta = metaFacade.find(cod_meta);
            setInfoMessage(MessageFormat.format(
                    i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletedRecord"),
                    retVal, meta.getDescriptionChr()));

            filterSelectedField = "-1";
            filterCriteria = "";
            paginationHelper = null; // For pagination recreation
            dataModel = null; // For data model recreation
            selectedItems = null; // For clearing selection
        } catch (Exception e) {
            notifier.log(getClass(), null, Action.ERROR, e.getMessage());
            setErrorMessage(i18n.iValue("web.client.backingbean.error.restriction"));
        }
        return null;
    }

    public String deleteEntities() {

        boolean flagAtLeastOne = false;
        String entitiesNotDeletedInUse = "";
        String entitiesNotDeletedError = "";

        Iterator<MetaData> iteratorDataModel = getDataModel().iterator();
        for (; iteratorDataModel.hasNext();) {
            MetaData currentEntity = iteratorDataModel.next();

            if (getSelectedItems().get(currentEntity.getMetaDataPK())) {
                flagAtLeastOne = true;
                try {
                    facade.remove(currentEntity);
                    signalDelete(currentEntity);
                } catch (EJBException ejbe) {
                    if (ejbe.getCausedByException().getClass().equals(
                            javax.transaction.RollbackException.class)) {
                        entitiesNotDeletedInUse += ", ".concat(currentEntity.toString());
                    } else {
                        entitiesNotDeletedError += ", ".concat(currentEntity.toString());
                    }
                } catch (Exception e) {
                    // @APP NOTIFICATION
                    this.signal(
                            Action.ERROR,
                            i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotDeleteError"),
                            e);
                    entitiesNotDeletedError += ", ".concat(currentEntity.toString());
                }
            }
        }

        if (flagAtLeastOne) {
            filterSelectedField = "-1";
            filterCriteria = "";
            paginationHelper = null; // For pagination recreation
            dataModel = null; // For data model recreation
            selectedItems = null; // For clearing selection

            if (entitiesNotDeletedInUse.length() == 0
                && entitiesNotDeletedError.length() == 0) {
                setSuccessMessage(getOkMessageOnDeleteByGroup());
            } else {
                if (entitiesNotDeletedInUse.length() > 0) {
                    entitiesNotDeletedInUse = entitiesNotDeletedInUse.substring(2);
                    setWarnMessage(getWarnMessageOnDeleteConstraintByGroup().concat(
                            entitiesNotDeletedInUse).concat("."));
                }
                if (entitiesNotDeletedError.length() > 0) {
                    entitiesNotDeletedError = entitiesNotDeletedError.substring(2);
                    setWarnMessage(getErrorMessageOnDeleteByGroup().concat(
                            entitiesNotDeletedError).concat("."));
                    // @APP NOTIFICATION
                    this.signal(
                            Action.ERROR,
                            i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotDeleteError"));
                }
            }
        } else {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneDelete"));
        }

        return null;
    }

    public String saveEditing() {
        try {
            if (entity.getMetaDataPK() == null) {
                facade.create(entity);
                signalCreate();
            } else {
                facade.edit(entity);
                signalUpdate();
            }
            setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.SaveSuccess"));
            entity = null;
            filterSelectedField = "-1";
            filterCriteria = "";
            paginationHelper = null; // For pagination recreation
            dataModel = null; // For data model recreation
            selectedItems = null; // For clearing selection
        } catch (EJBException ejbe) {
            if (ejbe.getCause() != null
                && ejbe.getCause() instanceof WarningTypeException) {
                setWarnMessage(ejbe.getCause().getMessage());
            } else if (ejbe.getCausedByException().getClass().equals(
                    javax.transaction.RollbackException.class)) {
                setWarnMessage(getWarnMessageOnDeleteConstraint());
            } else {
                setErrorMessage(i18n.iValue("web.client.backingBean.message.Error")
                    + ejbe + ".");
                this.signal(
                        Action.ERROR,
                        i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotSaveError"),
                        ejbe);
            }
        } catch (Exception e) {
            this.signal(
                    Action.ERROR,
                    i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotSaveError"),
                    e);
            setErrorMessage(i18n.iValue("web.client.backingBean.message.Error")
                + e + ".");
        }
        return null;
    }

    public String deleteEditing() {
        try {
            if (entity.getMetaDataPK() != null) {
                signalDelete(entity);
                facade.remove(entity);
            } else {
                setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.InexistentRecordDeletingError"));
            }
            setSuccessMessage(getOkMessageOnDelete());
            entity = null;
            filterSelectedField = "-1";
            filterCriteria = "";
            paginationHelper = null; // For pagination recreation
            dataModel = null; // For data model recreation
            selectedItems = null; // For clearing selection
        } catch (EJBException ejbe) {
            if (ejbe.getCausedByException().getClass().equals(
                    javax.transaction.RollbackException.class)) {
                setWarnMessage(getWarnMessageOnDeleteConstraint());
            } else {
                setErrorMessage(getErrorMessageOnDelete());
                this.signal(
                        Action.ERROR,
                        i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotEditError"),
                        ejbe);
            }

        } catch (Exception e) {
            setErrorMessage(MessageFormat.format(
                    i18n.iValue("web.client.backingBean.message.GeneralApplicationError"),
                    e.toString()));
            this.signal(
                    Action.ERROR,
                    i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotEditError"),
                    e);
        }

        return null;
    }

    public String cancelEditing() {
        entity = null;
        return null;
    }

    public MetaData getEntity() {
        return entity;
    }

    public void setEntity(MetaData entity) {
        this.entity = entity;
    }

    protected MetaDataFacade getFacade() {
        return facade;
    }

    protected void setFacade(MetaDataFacade facade) {
        this.facade = facade;
    }

    public List<MetaMember> getMetamemberList() {
        if (metamemberList == null) {
            metamemberList = metaFacade.find(cod_meta).getMetaMemberList();
        }
        return metamemberList;
    }

    public void setMetamemberList(List<MetaMember> metamemberList) {
        this.metamemberList = metamemberList;
    }

    public List<MetaMember> getSelectedMetaMemberList() {
        return selectedMetaMemberList;
    }

    public void setSelectedMetaMemberList(List<MetaMember> selectedMetaMemberList) {
        this.selectedMetaMemberList = selectedMetaMemberList;
    }

    // AUDITORY METHODS
    // --------------------------------------------------------------------------------------
    // AUXILIARY GENERIC METHODS
    // --------------------------------------------------------------------------------------
    protected Field getPrimaryKeyField() {
        Field returnField = null;
        Field[] fieds = MetaData.class.getDeclaredFields();
        for (Field field : fieds) {
            Id annotation = field.getAnnotation(Id.class);
            EmbeddedId annotationEmb = field.getAnnotation(EmbeddedId.class);
            if (annotation != null || annotationEmb != null) {
                return field;
            }
        }
        return returnField;
    }

    protected <M> Class<M> getDataClass() {
        return (Class<M>) MetaData.class;
    }

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
                return getDataClass().getMethod(methodName, new Class[0]).getReturnType();
            }

            Class<?> internalClass = getDataClass().getMethod(methodName,
                    new Class[0]).getReturnType();
            methodName = "get".concat(
                    internalFieldName.substring(0, 1).toUpperCase()).concat(
                    internalFieldName.substring(1));

            return internalClass.getMethod(methodName, new Class[0]).getReturnType();
        } catch (Exception e) {
            return Object.class;
        }
    }

    protected String getPrimarySortedField() throws PrimarySortedFieldNotFoundException {
        if (primarySortedField == null) {
            Field[] fieds = getDataClass().getDeclaredFields();
            for (Field field : fieds) {
                PrimarySortedField annotation = field.getAnnotation(PrimarySortedField.class);
                if (annotation != null) {
                    primarySortedField = field.getName();
                    primarySortedFieldAsc = annotation.ascending();
                    break;
                }
            }
            if (primarySortedField == null) {
                throw new PrimarySortedFieldNotFoundException(MetaData.class);
            }
        }
        return primarySortedField;
    }

    protected <T> List<String> getAttributesNameAndValue(T entity) {
        List<String> atts = new ArrayList<String>();
        Method[] methods = MetaData.class.getMethods();

        for (Method method : methods) {
            if (method.getName().startsWith("get")
                && !method.getReturnType().isInterface()) {
                String fieldName = method.getName().substring(3);
                Object fieldValue = executeMethod(entity, method);
                String item = "{".concat(fieldName).concat(":").concat(
                        fieldValue == null ? "null" : fieldValue.toString()).concat(
                        "}");
                atts.add(item);
            }
        }

        return atts;
    }

    protected <T> Object getAttributeValue(T entity, Field field) {
        String methodName = "get"
            + field.getName().substring(0, 1).toUpperCase().concat(
                    field.getName().substring(1));
        Object returnValue = null;
        try {
            Method method = MetaData.class.getMethod(methodName,
                    (Class<?>[]) null);
            returnValue = executeMethod(entity, method);
        } catch (NoSuchMethodException ex) {
            this.signal(
                    Action.ERROR,
                    MessageFormat.format(
                            i18n.iValue("web.client.backingBean.abstractCrudBean.message.AttributeValueObtainingError"),
                            MetaData.class.getSimpleName()), ex);
        } catch (SecurityException ex) {
            this.signal(
                    Action.ERROR,
                    MessageFormat.format(
                            i18n.iValue("web.client.backingBean.abstractCrudBean.message.AttributeValueObtainingError"),
                            MetaData.class.getSimpleName()), ex);
        }

        return returnValue;
    }

    protected String getAttributeColumnName(String fieldName) {
        try {
            String internalFieldName = "";
            if (fieldName.indexOf(".") >= 0) {
                internalFieldName = fieldName.substring(fieldName.indexOf(".") + 1);
                fieldName = fieldName.substring(0, fieldName.indexOf("."));
            }
            Field field = null;
            try {
                field = getDataClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                field = getDataClass().getSuperclass().getDeclaredField(
                        fieldName);
            }

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
            this.signal(
                    Action.ERROR,
                    MessageFormat.format(
                            i18n.iValue("web.client.backingBean.abstractCrudBean.message.AttributeValueObtainingError"),
                            getDataClass().getSimpleName()), e);
        }

        return null;
    }

    protected <T> Object executeMethod(T entity, Method method) {
        Object returnValue = null;
        try {
            if (entity == null) {
                java.util.logging.Logger.getLogger(getClass().getSimpleName()).log(
                        Level.SEVERE,
                        i18n.iValue("web.client.backingBean.abstractCrudBean.message.NullEntity"));
            }
            returnValue = method.invoke(entity, (Object[]) null);
        } catch (IllegalAccessException ex) {
            this.signal(
                    Action.ERROR,
                    MessageFormat.format(
                            i18n.iValue("web.client.backingBean.abstractCrudBean.message.ReflectionError"),
                            method.getName()), ex);
        } catch (IllegalArgumentException ex) {
            this.signal(
                    Action.ERROR,
                    MessageFormat.format(
                            i18n.iValue("web.client.backingBean.abstractCrudBean.message.ReflectionError"),
                            method.getName()), ex);
        } catch (InvocationTargetException ex) {
            this.signal(
                    Action.ERROR,
                    MessageFormat.format(
                            i18n.iValue("web.client.backingBean.abstractCrudBean.message.ReflectionError"),
                            method.getName()), ex);
        }
        return returnValue;
    }

    // APPLICATION NOTIFICATIONS
    protected void signalCreate() {
        String action = MessageFormat.format(
                i18n.iValue("web.client.backingBean.abstractCrudBean.message.SignalCreate"),
                MetaData.class.getSimpleName(),
                getAttributeValue(entity, getPrimaryKeyField()));
        String description = MessageFormat.format(
                i18n.iValue("web.client.backingBean.abstractCrudBean.message.SignalState"),
                Arrays.toString(getAttributesNameAndValue(entity).toArray(
                        new String[0])));
        notifier.signal(this.getClass(), Action.CREATE, userweb, null,
                this.getCurrentViewId(), action.concat(description),
                getSessionId(), getIpAddress());
    }

    protected void signalRead() {
        String action = MessageFormat.format(
                i18n.iValue("web.client.backingBean.abstractCrudBean.message.SignalRead"),
                MetaData.class.getSimpleName(), filterSelectedField,
                filterCriteria);
        notifier.signal(this.getClass(), Action.READ, userweb, null,
                this.getCurrentViewId(), action, getSessionId(), getIpAddress());
    }

    protected void signalUpdate() {
        String action = MessageFormat.format(
                i18n.iValue("web.client.backingBean.abstractCrudBean.message.SignaUpdate"),
                MetaData.class.getSimpleName(),
                getAttributeValue(entity, getPrimaryKeyField()));
        String description = MessageFormat.format(
                i18n.iValue("web.client.backingBean.abstractCrudBean.message.SignalState"),
                Arrays.toString(getAttributesNameAndValue(entity).toArray(
                        new String[0])));
        notifier.signal(this.getClass(), Action.UPDATE, userweb, null,
                this.getCurrentViewId(), action.concat(description),
                getSessionId(), getIpAddress());
    }

    protected void signalDelete(MetaData entity) {
        String action = MessageFormat.format(
                i18n.iValue("web.client.backingBean.abstractCrudBean.message.SignalDelete"),
                MetaData.class.getSimpleName(),
                getAttributeValue(entity, getPrimaryKeyField()));
        notifier.signal(this.getClass(), Action.DELETE, userweb, null,
                this.getCurrentViewId(), action, getSessionId(), getIpAddress());
    }

    protected void signalReport(ReportType repType) {
        String criterio = ".";
        if (!filterSelectedField.equals("-1")) {
            criterio = MessageFormat.format(
                    i18n.iValue("web.client.backingBean.abstractCrudBean.message.ReportCriteria"),
                    filterSelectedField, filterCriteria);
        }

        String action = MessageFormat.format(
                i18n.iValue("web.client.backingBean.abstractCrudBean.message.ListOfReport"),
                MetaData.class.getSimpleName(), criterio);
        notifier.signal(
                this.getClass(),
                Action.REPORT,
                userweb,
                null,
                this.getCurrentViewId(),
                repType.name().concat(
                        i18n.iValue("web.client.backingBean.abstractServiceBean.message.Report")).concat(
                        action), getSessionId(), getIpAddress());
    }

    protected void signal(Action action, String message) {
        notifier.signal(this.getClass(), action, userweb, null,
                this.getCurrentViewId(), message, getSessionId(),
                getIpAddress());
    }

    protected void signal(Action action, String message, Exception e) {
        notifier.signal(this.getClass(), action, userweb, null,
                this.getCurrentViewId(), message, getSessionId(),
                getIpAddress(), e);
    }

    public String getReportWhereCriteria() {
        if (getDataClass().getSimpleName().equals("MetaData")) {
            String filter = "AND o.COD_CLIENT = %s AND o.COD_META = %s AND o.COD_MEMBER = %s";
            return String.format(filter,
                    getUserweb().getClient().getClientCod(),
                    String.valueOf(cod_meta), String.valueOf(cod_member));
        } else {
            String filter = "AND o.COD_CLIENT = %s AND o.COD_META = %s ";
            return String.format(filter,
                    getUserweb().getClient().getClientCod(),
                    String.valueOf(cod_meta));
        }

    }

    public String save() {
        try {
            Client client = getUserweb().getClient();
            MetaDataPK pk = getEntity().getMetaDataPK();
            pk.setCodMember(cod_member);
            pk.setCodMeta(cod_meta);
            if (getEntity().getMetaDataPK().getCodClient() == null) {
                pk.setCodClient(client.getClientCod());
                // es nuevo... se verifica que no exista ya en la bd
                if (getFacade().find(pk) != null) {
                    setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.AlreadyExistRecord"));
                    pk.setCodClient(null);// para que al intentar de vuelta la
                                          // misma operación obtenga el mismo
                                          // msj.
                    return null;
                }
            }
            MetaClient metaClient = metaClientFacade.findByMetaAndClient(
                    client.getClientCod(), cod_meta);
            MetaMember metaMember = metaMemberFacade.findByCodMetaAndMemberCod(
                    cod_meta, cod_member);
            getEntity().setMetaClient(metaClient);
            getEntity().setMetaMember(metaMember);

            return saveEditing();
        } catch (GenericFacadeException ex) {
            signal(Action.ERROR,
                    i18n.iValue("web.client.backingBean.abstractCrudBean.message.SavingError"));
        } catch (Exception e) {
            signal(Action.ERROR,
                    i18n.iValue("web.client.backingBean.abstractCrudBean.message.SavingError"));
        }
        return null;
    }

    public String saveMetaOptions() {
        metaOptionSectionVisible = false;
        return null;
    }

    public String cancelMetaOptions() {
        setSelectedMetaMemberList(null);
        return null;
    }

    public String procesarArchivo() {
        if (file != null) {

            FileMetaDataMessage fileMetaDataMessage = new FileMetaDataMessage();
            fileMetaDataMessage.setClientCod(getUserweb().getClient().getClientCod());
            fileMetaDataMessage.setBytes(file.getContents());
            fileMetaDataMessage.setMetaCod(cod_meta);
            fileMetaDataMessage.setProcessUserphone(saveUserphone.equals("true"));

            if (getUserweb().getCellphoneNum() == null) {
                setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustHaveCellphoneNum"));
                return null;
            }
            fileMetaDataMessage.setUserwebCellphoneNum(getUserweb().getCellphoneNum().toString());
            fileMetaDataMessage.setApplication(ApplicationBean.getInstance().getApplication());
            // facadeContainer.getFileMetaDataQueueAPI().sendToJMS(
            // fileMetaDataMessage);

            facadeContainer.getFileMetaDataAPI().procesarCargaMasiva(
                    fileMetaDataMessage);

            setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.FileProcessing"));

            uploadSectionVisible = false;
            file = null;
            entity = null;

        }
        return null;
    }

    public String getFileName() {
        return file != null ? file.getFileName() : "";
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void handleFileUpload(FileUploadEvent event) {
        if (event.getFile() != null) {
            file = event.getFile();
            fileReady = true;
        } else {
            fileReady = false;
        }
    }

    public boolean isUploadSectionVisible() {
        return uploadSectionVisible;
    }

    public void setUploadSectionVisible(boolean uploadSectionVisible) {
        this.uploadSectionVisible = uploadSectionVisible;
    }

    public boolean isAssociateSectionVisible() {
        return associateSectionVisible;
    }

    public void setAssociateSectionVisible(boolean associateSectionVisible) {
        this.associateSectionVisible = associateSectionVisible;
    }

    public String cancelUpload() {
        uploadSectionVisible = false;
        file = null;
        return this.cancelEditing();
    }

    public boolean isMetaOptionSectionVisible() {
        return metaOptionSectionVisible;
    }

    public void setMetaOptionSectionVisible(boolean metaOptionSectionVisible) {
        this.metaOptionSectionVisible = metaOptionSectionVisible;
    }

    public String showUploadSection() {
        uploadSectionVisible = true;
        return null;
    }

    public String showMetaOptionSection() {
        metaOptionSectionVisible = true;
        return null;
    }

    public boolean isFileReady() {
        return fileReady;
    }

    protected void createOrSaveMetaData(MetaData metaData) throws Exception {
        try {
            if (getFacade().find(metaData.getMetaDataPK()) == null) {
                // CREATE ACTION
                facade.create(metaData);
                String action = MessageFormat.format(
                        i18n.iValue("web.client.backingBean.abstractCrudBean.message.SignalCreate"),
                        MetaData.class.getSimpleName(),
                        getAttributeValue(metaData, getPrimaryKeyField()));
                String description = MessageFormat.format(
                        i18n.iValue("web.client.backingBean.abstractCrudBean.message.SignalState"),
                        Arrays.toString(getAttributesNameAndValue(metaData).toArray(
                                new String[0])));
                notifier.signal(this.getClass(), Action.CREATE, userweb, null,
                        this.getCurrentViewId(), action.concat(description),
                        getSessionId(), getIpAddress());

            } else {
                // UPDATE ACTION
                facade.edit(metaData);
                String action = MessageFormat.format(
                        i18n.iValue("web.client.backingBean.abstractCrudBean.message.SignaUpdate"),
                        MetaData.class.getSimpleName(),
                        getAttributeValue(metaData, getPrimaryKeyField()));
                String description = MessageFormat.format(
                        i18n.iValue("web.client.backingBean.abstractCrudBean.message.SignalState"),
                        Arrays.toString(getAttributesNameAndValue(metaData).toArray(
                                new String[0])));
                notifier.signal(this.getClass(), Action.UPDATE, userweb, null,
                        this.getCurrentViewId(), action.concat(description),
                        getSessionId(), getIpAddress());
            }
        } catch (EJBException ejbe) {
            if (ejbe.getCause() != null
                && ejbe.getCause() instanceof WarningTypeException) {
                setWarnMessage(ejbe.getCause().getMessage());
            } else if (ejbe.getCausedByException().getClass().equals(
                    javax.transaction.RollbackException.class)) {
                setWarnMessage(getWarnMessageOnDeleteConstraint());
            } else {
                setErrorMessage(i18n.iValue("web.client.backingBean.message.Error")
                    + ejbe + ".");
                this.signal(
                        Action.ERROR,
                        i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotSaveError"),
                        ejbe);
            }
            throw new Exception(i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotSaveError"), ejbe);
        } catch (Exception e) {
            this.signal(
                    Action.ERROR,
                    i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotSaveError"),
                    e);
            setErrorMessage(i18n.iValue("web.client.backingBean.message.Error")
                + e + ".");
            throw e;
        }

    }

    public String deleteMetaDatasByCodeChr(String codeChr) {
        try {
            facade.deleteMetaDatasByCodeChr(
                    getUserweb().getClient().getClientCod(), cod_meta, codeChr);
            setSuccessMessage(getOkMessageOnDelete());
        } catch (Exception e) {
            setErrorMessage(getErrorMessageOnDelete());
            this.signal(
                    Action.ERROR,
                    i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotEditError"),
                    e);
        }
        filterSelectedField = "-1";
        filterCriteria = "";
        paginationHelper = null; // For pagination recreation
        dataModel = null; // For data model recreation

        return null;
    }

    public String deleteMetaDatasByCodeChrFromDeleteEntitiesPlusMember(String codeChr) {
        try {
            facade.deleteMetaDatasByCodeChr(
                    getUserweb().getClient().getClientCod(), cod_meta, codeChr);
        } catch (Exception e) {
            this.signal(
                    Action.ERROR,
                    i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotEditError"),
                    e);
        }
        filterSelectedField = "-1";
        filterCriteria = "";
        paginationHelper = null; // For pagination recreation
        dataModel = null; // For data model recreation

        return null;
    }

    public List<Userphone> getSelectedUserphoneList() {
        if (selectedUserphoneList == null) {
            selectedUserphoneList = new ArrayList<Userphone>();
        }
        return selectedUserphoneList;
    }

    public void setSelectedUserphoneList(List<Userphone> selectedUserphoneList) {
        this.selectedUserphoneList = selectedUserphoneList;
    }

    public Boolean getValidatedAllUserphones() {
        return validatedAllUserphones;
    }

    public void setValidatedAllUserphones(Boolean validatedAllUserphones) {
        this.validatedAllUserphones = validatedAllUserphones;
    }

    public List<Userphone> getSelectedAssociatedUserphones() {
        if (selectedAssociatedUserphones == null) {
            selectedAssociatedUserphones = new ArrayList<Userphone>();
        }
        return selectedAssociatedUserphones;
    }

    public void setSelectedAssociatedUserphones(List<Userphone> selectedAssociatedUserphones) {
        this.selectedAssociatedUserphones = selectedAssociatedUserphones;
    }

    public List<Userphone> getAssociatedUserphonesList() {
        if (associatedUserphonesList == null) {
            associatedUserphonesList = new ArrayList<Userphone>();
        }
        return associatedUserphonesList;
    }

    public void setAssociatedUserphonesList(List<Userphone> associatedUserphonesList) {
        this.associatedUserphonesList = associatedUserphonesList;
    }

    public Boolean getValidatedAllUserphonesAssociated() {
        return validatedAllUserphonesAssociated;
    }

    public void setValidatedAllUserphonesAssociated(Boolean validatedAllUserphonesAssociated) {
        this.validatedAllUserphonesAssociated = validatedAllUserphonesAssociated;
    }

    public String addToList() {
        if (validatedAllUserphones) {
            for (Userphone u : userphoneList) {
                getAssociatedUserphonesList().add(u);
            }
            userphoneList = null;
            selectedUserphoneList = null;
            validatedAllUserphones = false;
            return null;
        }

        if (selectedUserphoneList != null && selectedUserphoneList.size() > 0) {
            for (Userphone u : selectedUserphoneList) {
                getAssociatedUserphonesList().add(u);
                getUserphoneList().remove(u);
            }
            selectedUserphoneList = null;
        }
        return null;
    }

    public String removeFromList() {

        if (validatedAllUserphonesAssociated) {
            for (Userphone u : associatedUserphonesList) {
                getUserphoneList().add(u);
            }
            associatedUserphonesList = null;
            selectedAssociatedUserphones = null;
            validatedAllUserphonesAssociated = false;
            return null;
        }

        if (selectedAssociatedUserphones != null
            && selectedAssociatedUserphones.size() > 0) {

            for (Userphone u : selectedAssociatedUserphones) {
                getAssociatedUserphonesList().remove(u);
                getUserphoneList().add(u);
            }

            selectedAssociatedUserphones = null;
        }
        return null;
    }

    public String getSaveUserphone() {
        return saveUserphone;
    }

    public void setSaveUserphone(String saveUserphone) {
        this.saveUserphone = saveUserphone;
    }

    public String getMessageUserphonesNotExist() {
        return messageUserphonesNotExist;
    }

    public void setMessageUserphonesNotExist(String messageUserphonesNotExist) {
        this.messageUserphonesNotExist = messageUserphonesNotExist;
    }

    public boolean isUserphoneNotExist() {
        return userphoneNotExist;
    }

    public void setUserphoneNotExist(boolean userphoneNotExist) {
        this.userphoneNotExist = userphoneNotExist;
    }
}
