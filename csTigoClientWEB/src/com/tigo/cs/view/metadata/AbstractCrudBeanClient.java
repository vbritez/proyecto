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

import net.sf.jasperreports.engine.JRParameter;

import org.apache.log4j.Logger;

import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.PrimarySortedFieldNotFoundException;
import com.tigo.cs.commons.jpa.Searchable;
import com.tigo.cs.commons.jpa.WarningTypeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.report.ReportType;
import com.tigo.cs.commons.util.ListDataModelViewCsTigo;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.commons.util.PaginationHelper;
import com.tigo.cs.commons.util.SortHelper;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.facade.I18nBean;
import com.tigo.cs.facade.PhoneFacade;
import com.tigo.cs.security.Notifier;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.ApplicationBean;

/**
 * 
 * @author Miguel Zorrilla
 * @version $Id: AbstractCrudBeanClient.java 91 2011-11-28 20:16:12Z
 *          javier.kovacs $
 */
public abstract class AbstractCrudBeanClient<T, F extends AbstractAPI<T>> extends SMBaseBean {

    private static final long serialVersionUID = 4487639054036437809L;
    protected String okMessageOnDelete;
    protected String okMessageOnDeleteByGroup;
    protected String warnMessageOnDeleteConstraintByGroup;
    protected String warnMessageOnDeleteConstraint;
    protected String errorMessageOnDelete;
    protected String errorMessageOnDeleteByGroup;
    private T entity;
    private F facade;
    private DataModel<T> dataModel;
    private Map<Object, Boolean> selectedItems;
    protected PaginationHelper paginationHelper;
    private SortHelper sortHelper;
    private String filterSelectedField;
    private String filterCriteria;
    private String aditionalFilter;
    Class<T> entityClass;
    Class<F> facadeClass;
    String keyMethod;
    private List<SelectItem> filterFields;
    private String primarySortedField;
    private boolean primarySortedFieldAsc = true;
    protected Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    // private final Boolean createAllowed = null;
    // private final Boolean updateAllowed = null;
    // private final Boolean deleteAllowed = null;
    private List<SelectItem> rowQuantList;
    private String rowQuantSelected;
    private String pdfReportName;
    private String xlsReportName;
    private final Userweb userweb = SecurityBean.getInstance().getLoggedInUserClient();
    private int lastActualPage = -1;
    @EJB
    protected Notifier notifier;
    @EJB
    private PhoneFacade phoneFacade;
    @EJB
    protected I18nBean i18n;

    public enum Operation {

        CREATE, READ, UPDATE, DELETE
    };

    public void reset(String msg) {
        setSuccessMessage(i18n.iValue(msg));
        entity = null;
        filterSelectedField = "-1";
        filterCriteria = "";
        paginationHelper = null; // For pagination recreation
        dataModel = null; // For data model recreation
        selectedItems = null; // For clearing selection

    }
    
    public void reloadPaginationHelper() {
        entity = null;
        filterSelectedField = "-1";
        filterCriteria = "";
        paginationHelper = null; // For pagination recreation
        dataModel = null; // For data model recreation
        selectedItems = null; // For clearing selection        
    }

    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------
    public AbstractCrudBeanClient(Class<T> entityClass, Class<F> facadeClass) {
        this.entityClass = entityClass;
        this.facadeClass = facadeClass;
        this.keyMethod = "get".concat(entityClass.getSimpleName()).concat("Cod");
        initialize();
    }

    public AbstractCrudBeanClient(Class<T> entityClass, String keyMethod) {
        this.entityClass = entityClass;
        this.keyMethod = keyMethod;
        initialize();
    }

    private void initialize() {
        facade = AbstractAPI.getLocalInstance(facadeClass);
        if (facade == null) {
            logger.error(MessageFormat.format(
                    i18n.iValue("web.client.backingBean.message.Error"),
                    entityClass.getSimpleName()));
        }
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
                    MessageFormat.format(
                            i18n.iValue("web.client.backingBean.message.Error"),
                            ex.getMessage()), ex);
        }
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

    public int getLastActualPage() {
        return lastActualPage;
    }

    public void setLastActualPage(int lastActualPage) {
        this.lastActualPage = lastActualPage;
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

                String innerWhere = null;
                Integer count = null;

                @Override
                public int getItemsCount() {

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
                            where = where.concat(" AND ");
                        }
                        where = where.concat(" (").concat(
                                aditionalFilter.trim()).concat(") ");
                    }

                    if (where == null) {
                        where = "where ";
                    } else {
                        where = where.concat(" AND");
                    }
                    where = where.concat(" o.client.clientCod = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));

                    /*
                     * esto se verifica para no ejecutar la sentencia cada vez
                     * que se solicita la cantidad de registros a mostrar, se
                     * retorna el valor ya en memoria si la sentencia no cambio.
                     * Esto se realiza para no realizar mas de una vez la
                     * consulta a la base de datos
                     */

                    if (innerWhere == null) {
                        innerWhere = where;
                    } else {
                        if (innerWhere.compareTo(where) == 0) {
                            return count;
                        }
                    }
                    count = facade.count(where);
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

                    if (where == null) {
                        where = "where ";
                    } else {
                        where = where.concat(" AND");
                    }
                    where = where.concat(" o.client.clientCod = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));

                    String orderby = "o.".concat(sortHelper.getField()).concat(
                            sortHelper.isAscending() ? " ASC" : " DESC");

                    return new ListDataModelViewCsTigo(facade.findRange(
                            new int[] { getPageFirstItem(), getPageFirstItem()
                                + getPageSize() }, where, orderby));
                }
            };
            if (lastActualPage >= 0) {
                paginationHelper.setActualPage(lastActualPage);
                lastActualPage = -1;
            }
        }

        return paginationHelper;
    }

    protected void setPaginationHelper(PaginationHelper paginationHelper) {
        this.paginationHelper = paginationHelper;
    }

    public DataModel<T> getDataModel() {
        if (dataModel == null) {
            dataModel = getPaginationHelper().createPageDataModel();
        }

        return dataModel;
    }

    public void setDataModel(DataModel<T> dataModel) {
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

            Field[] fieds = entityClass.getDeclaredFields();
            for (Field field : fieds) {
                Searchable annotation = field.getAnnotation(Searchable.class);
                if (annotation != null) {
                    if (annotation.position() < 0
                        || annotation.position() > filterFields.size()) {
                        filterFields.add(new SelectItem(field.getName().concat(
                                !annotation.internalField().isEmpty() ? ".".concat(annotation.internalField()) : ""), i18n.iValue(annotation.label())));
                    } else {
                        filterFields.add(
                                annotation.position(),
                                new SelectItem(field.getName().concat(
                                        !annotation.internalField().isEmpty() ? ".".concat(annotation.internalField()) : ""), i18n.iValue(annotation.label())));
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
        /*
         * Se puso esta verificacion porque en algun punto del jsf se setea a
         * null la variable filterSelectedField
         */
        if (filterSelectedField == null)
            filterSelectedField = this.filterSelectedField != null ? this.filterSelectedField : "-1";
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

    public String generateReport(ReportType reportType, String reportName) {
        Class<?> fieldClass = getFieldType(getFilterSelectedField());
        // String parametersDesc = "";
        // Prepare where clause
        String whereCluse = "";
        if (!fieldClass.equals(Object.class)) {
            String filterAttributeColumnName = getAttributeColumnName(getFilterSelectedField());
            if (getFilterSelectedField().indexOf(".") < 0) {
                filterAttributeColumnName = "o.".concat(filterAttributeColumnName);
            } else {
                filterAttributeColumnName = "o1.".concat(filterAttributeColumnName);
            }
            if (fieldClass.getSuperclass().equals(Number.class)) {
                whereCluse = " AND ".concat(filterAttributeColumnName).concat(
                        " = ").concat(getFilterCriteria());
                // parametersDesc = filterAttributeColumnName.replaceAll("_",
                // " ").concat(": ").concat(getFilterCriteria()).concat(" | ");
            } else {
                whereCluse = " AND lower(".concat(filterAttributeColumnName).concat(
                        ") LIKE '%").concat(getFilterCriteria().toLowerCase()).concat(
                        "%'");
                // parametersDesc = filterAttributeColumnName.replaceAll("_",
                // " ").concat(" contiene: \"").concat(getFilterCriteria()).concat("\" | ");
            }
        }
        if (getReportWhereCriteria() != null) {
            whereCluse = whereCluse.concat(getReportWhereCriteria());
        }

        // Prepare orderby clause
        String sortAttributeColumnName = getAttributeColumnName(getSortHelper().getField());
        if (getSortHelper().getField().indexOf(".") < 0) {
            sortAttributeColumnName = "o.".concat(sortAttributeColumnName);
        } else {
            sortAttributeColumnName = "o1.".concat(sortAttributeColumnName);
        }
        String orderBy = "ORDER BY ".concat(sortAttributeColumnName).concat(
                getSortHelper().isAscending() ? " ASC" : " DESC");
        // parametersDesc +=
        // "Ordenado por: ".concat(sortAttributeColumnName.replaceAll("_",
        // " ")).concat(" en forma ").concat(getSortHelper().isAscending() ?
        // "ascendente" : "descendente");

        // Put params into params map
        Map params = new HashMap();
        params.put("WHERE", whereCluse);
        params.put("ORDER_BY", orderBy);
        // params.put("PARAMETERS_DESCRIPTION", parametersDesc);
        params.put("USER",
                SecurityBean.getInstance().getLoggedInUserClient().getNameChr());
        params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
        params.put(JRParameter.REPORT_RESOURCE_BUNDLE, i18n.getResourceBundle());

        Connection conn = SMBaseBean.getDatabaseConnecction();
        switch (reportType) {
        case PDF:
            // JasperReportUtils.respondPDF(getPdfReportName(), params, true,
            // conn);
            break;
        case XLS:
            // JasperReportUtils.respondXLS(getXlsReportName(), params, true,
            // conn);
            break;
        }
        signalReport(reportType);
        return null;
    }

    public String viewPDF() {
        return generateReport(ReportType.PDF, getPdfReportName());
    }

    public String viewXLS() {
        return generateReport(ReportType.XLS, getXlsReportName());
    }

    // EDITING METHODS
    // --------------------------------------------------------------------------------------
    public String newEntity() {
        // if (!isCreateActionAllowed()) {
        // return null;
        // }
        entity = getJPAEntityInstance();
        return null;
    }

    public String editEntity(String... collectionNames) {
        // if (!isUpdateActionAllowed()) {
        // return null;
        // }
        entity = null;
        Iterator<T> iteratorDataModel = getDataModel().iterator();
        for (; iteratorDataModel.hasNext();) {
            T currentEntity = iteratorDataModel.next();
            Object obj = getKeyValue(currentEntity);
            if (getSelectedItems().get(obj)) {
                if (entity == null) {
                    entity = facade.find(obj, collectionNames);
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
        // if (!isUpdateActionAllowed()) {
        // return null;
        // }

        entity = null;
        Iterator<T> iteratorDataModel = getDataModel().iterator();
        for (; iteratorDataModel.hasNext();) {
            T currentEntity = iteratorDataModel.next();
            Object obj = getKeyValue(currentEntity);
            if (getSelectedItems().get(obj)) {
                if (entity == null) {
                    entity = facade.find(obj);
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

    public String deleteEntities() {
        // if (!isDeleteActionAllowed()) {
        // return null;
        // }
        boolean flagAtLeastOne = false;
        String entitiesNotDeletedInUse = "";
        String entitiesNotDeletedError = "";

        Iterator<T> iteratorDataModel = getDataModel().iterator();
        for (; iteratorDataModel.hasNext();) {
            T currentEntity = iteratorDataModel.next();

            Object obj = getKeyValue(currentEntity);
            if (getSelectedItems().get(obj)) {
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
            lastActualPage = paginationHelper.getActualPage();
            // filterSelectedField = "-1";
            // filterCriteria = "";
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
            Object obj = getKeyValue(entity);
            if (obj == null) {
                facade.create(entity);
                signalCreate();
            } else {
                facade.edit(entity);
                // signalUpdate();
            }
            setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.SaveSuccess"));
            entity = null;
            lastActualPage = paginationHelper.getActualPage();
            // filterSelectedField = "-1";
            // filterCriteria = "";
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
                setErrorMessage(MessageFormat.format(
                        i18n.iValue("web.client.backingBean.message.Error"),
                        ejbe));
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
            setErrorMessage(MessageFormat.format(
                    i18n.iValue("web.client.backingBean.message.Error"), e));
        }

        return null;
    }

    public String deleteEditing() {
        try {
            Object obj = getKeyValue(entity);
            if (obj != null) {
                signalDelete(entity);
                facade.remove(entity);
            } else {
                setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.InexistentRecordDeletingError"));
            }
            setSuccessMessage(getOkMessageOnDelete());
            entity = null;
            lastActualPage = paginationHelper.getActualPage();
            // filterSelectedField = "-1";
            // filterCriteria = "";
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
            setErrorMessage(i18n.iValue(
                    "web.client.backingBean.message.GeneralApplicationError").concat(
                    e.toString()).concat(i18n.iValue(".")));
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

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    protected F getFacade() {
        return facade;
    }

    protected void setFacade(F facade) {
        this.facade = facade;
    }

    // PERMISSIONS METHODS
    // --------------------------------------------------------------------------------------
    // public boolean isCreateActionAllowed() {
    // if (createAllowed == null) {
    // createAllowed = false;
    // Userweb admin = SecurityBean.getInstance().getLoggedInUserClient();
    //
    // try {
    // createAllowed =
    // UseradminFacade.getLocalInstance(UseradminFacade.class).getUserScreenAccess(admin.getUserwebCod(),
    // getCurrentViewId(), "createChr");
    // } catch (GenericFacadeException ex) {
    // this.signal(Action.ERROR,
    // MessageFormat.format(i18n.iValue("web.admin.backingBean.AbstractCrudBean.signal.CheckingCreatePermissionsUserScreen"),
    // admin.getNameChr(), getCurrentViewId()), ex);
    // return createAllowed = false;
    // }
    // }
    // return createAllowed;
    // }
    //
    // public boolean isUpdateActionAllowed() {
    // if (updateAllowed == null) {
    // updateAllowed = false;
    // Userweb admin = SecurityBean.getInstance().getLoggedInUserClient();
    //
    // try {
    // updateAllowed =
    // UseradminFacade.getLocalInstance(UseradminFacade.class).getUserScreenAccess(admin.getUserwebCod(),
    // getCurrentViewId(), "updateChr");
    // } catch (GenericFacadeException ex) {
    // this.signal(Action.ERROR,
    // MessageFormat.format(i18n.iValue("web.admin.backingBean.AbstractCrudBean.signal.CheckingUpdatePermissionsUserScreen"),
    // admin.getNameChr(), getCurrentViewId()), ex);
    // return updateAllowed = false;
    // }
    // }
    // return updateAllowed;
    // }
    //
    // public boolean isDeleteActionAllowed() {
    // if (deleteAllowed == null) {
    // deleteAllowed = false;
    // Userweb admin = SecurityBean.getInstance().getLoggedInUserClient();
    //
    // try {
    // deleteAllowed =
    // UseradminFacade.getLocalInstance(UseradminFacade.class).getUserScreenAccess(admin.getUserwebCod(),
    // getCurrentViewId(), "deleteChr");
    // } catch (GenericFacadeException ex) {
    // this.signal(Action.ERROR,
    // MessageFormat.format(i18n.iValue("web.admin.backingBean.AbstractCrudBean.signal.CheckingDeletePermissionsUserScreen"),
    // admin.getNameChr(), getCurrentViewId()), ex);
    // return deleteAllowed = false;
    // }
    // }
    // return deleteAllowed;
    // }
    // AUDITORY METHODS
    // --------------------------------------------------------------------------------------
    // AUXILIARY GENERIC METHODS
    // --------------------------------------------------------------------------------------
    protected T getJPAEntityInstance() {
        T myInstance = null;
        try {
            myInstance = (T) Class.forName(entityClass.getCanonicalName()).newInstance();
        } catch (ClassNotFoundException ex) {
            this.signal(
                    Action.ERROR,
                    MessageFormat.format(
                            i18n.iValue("web.admin.backingBean.AbstractCrudBean.signal.InstantiationError"),
                            entityClass.getSimpleName()), ex);
        } catch (InstantiationException ex) {
            this.signal(
                    Action.ERROR,
                    MessageFormat.format(
                            i18n.iValue("web.admin.backingBean.AbstractCrudBean.signal.InstantiationError"),
                            entityClass.getSimpleName()), ex);
        } catch (IllegalAccessException ex) {
            this.signal(
                    Action.ERROR,
                    MessageFormat.format(
                            i18n.iValue("web.admin.backingBean.AbstractCrudBean.signal.InstantiationError"),
                            entityClass.getSimpleName()), ex);
        }
        return myInstance;
    }

    protected Field getPrimaryKeyField() {
        Field returnField = null;
        Field[] fieds = entityClass.getDeclaredFields();
        for (Field field : fieds) {
            Id annotation = field.getAnnotation(Id.class);
            EmbeddedId annotationEmb = field.getAnnotation(EmbeddedId.class);
            if (annotation != null || annotationEmb != null) {
                return field;
            }
        }
        return returnField;
    }

    protected Object getKeyValue(T currentEntity) {
        Object obj = null;
        try {
            Method method = currentEntity.getClass().getMethod(keyMethod);
            obj = method.invoke(currentEntity, (Object[]) null);
        } catch (NoSuchMethodException e) {
            this.signal(
                    Action.ERROR,
                    MessageFormat.format(
                            i18n.iValue("web.admin.backingBean.AbstractCrudBean.signal.NoSuchMethod"),
                            keyMethod, currentEntity.getClass().getSimpleName()),
                    e);
        } catch (Exception e) {
            this.signal(
                    Action.ERROR,
                    MessageFormat.format(
                            i18n.iValue("web.admin.backingBean.AbstractCrudBean.signal.InvocationError"),
                            keyMethod, currentEntity.getClass().getSimpleName()),
                    e);
        }

        return obj;
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
                return entityClass.getMethod(methodName, new Class[0]).getReturnType();
            }

            Class<?> internalClass = entityClass.getMethod(methodName,
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
            Field[] fieds = entityClass.getDeclaredFields();
            for (Field field : fieds) {
                PrimarySortedField annotation = field.getAnnotation(PrimarySortedField.class);
                if (annotation != null) {
                    primarySortedField = field.getName();
                    primarySortedFieldAsc = annotation.ascending();
                    break;
                }
            }
            if (primarySortedField == null) {
                throw new PrimarySortedFieldNotFoundException(entityClass);
            }
        }
        return primarySortedField;
    }

    protected <T> List<String> getAttributesNameAndValue(T entity) {
        List<String> atts = new ArrayList<String>();
        Method[] methods = entityClass.getMethods();

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
            Method method = entityClass.getMethod(methodName, (Class<?>[]) null);
            returnValue = executeMethod(entity, method);
        } catch (NoSuchMethodException ex) {
            this.signal(
                    Action.ERROR,
                    MessageFormat.format(
                            i18n.iValue("web.admin.backingBean.AbstractCrudBean.signal.AttributeValueObtentionError"),
                            entityClass.getSimpleName()), ex);
        } catch (SecurityException ex) {
            this.signal(
                    Action.ERROR,
                    MessageFormat.format(
                            i18n.iValue("web.admin.backingBean.AbstractCrudBean.signal.AttributeValueObtentionError"),
                            entityClass.getSimpleName()), ex);
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
            this.signal(
                    Action.ERROR,
                    i18n.iValue(
                            "web.admin.backingBean.AbstractCrudBean.signal.AttributeValueObtentionError").concat(
                            entityClass.getSimpleName()), e);
        }

        return null;
    }

    protected <T> Object executeMethod(T entity, Method method) {
        Object returnValue = null;
        try {
            if (entity == null) {
                java.util.logging.Logger.getLogger(getClass().getSimpleName()).log(
                        Level.SEVERE,
                        i18n.iValue("web.admin.backingBean.AbstractCrudBean.log.EntityAlreadyNull"));
            }
            returnValue = method.invoke(entity, (Object[]) null);
        } catch (IllegalAccessException ex) {
            this.signal(
                    Action.ERROR,
                    MessageFormat.format(
                            i18n.iValue("web.admin.backingBean.AbstractCrudBean.signal.ReflectionError"),
                            method.getName()), ex);
        } catch (IllegalArgumentException ex) {
            this.signal(
                    Action.ERROR,
                    MessageFormat.format(
                            i18n.iValue("web.admin.backingBean.AbstractCrudBean.signal.ReflectionError"),
                            method.getName()), ex);
        } catch (InvocationTargetException ex) {
            this.signal(
                    Action.ERROR,
                    MessageFormat.format(
                            i18n.iValue("web.admin.backingBean.AbstractCrudBean.signal.ReflectionError"),
                            method.getName()), ex);
        }
        return returnValue;
    }

    // APPLICATION NOTIFICATIONS
    protected void signalCreate() {
        String action = MessageFormat.format(
                i18n.iValue("web.client.backingBean.abstractCrudBean.message.SignalCreate"),
                entityClass.getSimpleName(),
                getAttributeValue(entity, getPrimaryKeyField()));
        String description = i18n.iValue("web.client.backingBean.abstractCrudBean.message.SignalState")
            + Arrays.toString(getAttributesNameAndValue(entity).toArray(
                    new String[0]));
        notifier.signal(this.getClass(), Action.CREATE, userweb, null,
                this.getCurrentViewId(), action.concat(description),
                getSessionId(), getIpAddress());
    }

    protected void signalRead() {
        String action = MessageFormat.format(
                i18n.iValue("web.client.backingBean.abstractCrudBean.message.SignalRead"),
                entityClass.getSimpleName(), filterSelectedField,
                filterCriteria);
        notifier.signal(this.getClass(), Action.READ, userweb, null,
                this.getCurrentViewId(), action, getSessionId(), getIpAddress());
    }

    protected void signalUpdate() {
        String action = MessageFormat.format(
                i18n.iValue("web.client.backingBean.abstractCrudBean.message.SignaUpdate"),
                entityClass.getSimpleName(),
                getAttributeValue(entity, getPrimaryKeyField()));
        String description = i18n.iValue("web.client.backingBean.abstractCrudBean.message.SignalState")
            + Arrays.toString(getAttributesNameAndValue(entity).toArray(
                    new String[0]));
        notifier.signal(this.getClass(), Action.UPDATE, userweb, null,
                this.getCurrentViewId(), action.concat(description),
                getSessionId(), getIpAddress());
    }

    protected void signalDelete(T entity) {
        String action = MessageFormat.format(
                i18n.iValue("web.client.backingBean.abstractCrudBean.message.SignalDelete"),
                entityClass.getSimpleName(),
                getAttributeValue(entity, getPrimaryKeyField()));
        notifier.signal(this.getClass(), Action.DELETE, userweb, null,
                this.getCurrentViewId(), action, getSessionId(), getIpAddress());
    }

    protected void signalReport(ReportType repType) {
        String criterio = ".";
        if (!filterSelectedField.equals("-1")) {
            criterio = i18n.iValue("web.client.backingBean.abstractCrudBean.message.ReportCriteria")
                + filterSelectedField + "=" + filterCriteria;
        }

        String action = i18n.iValue(
                "web.client.backingBean.abstractCrudBean.message.ListOfReport").concat(
                entityClass.getSimpleName().concat(criterio));
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

    public abstract String getReportWhereCriteria();

}
