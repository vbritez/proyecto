package com.tigo.cs.view.service;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;

import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import com.tigo.cs.commons.ejb.FacadeException;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.PrimarySortedFieldNotFoundException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.util.ListDataModelViewCsTigo;
import com.tigo.cs.commons.util.PaginationHelper;
import com.tigo.cs.commons.util.SortHelper;
import com.tigo.cs.domain.MapMark;
import com.tigo.cs.domain.Route;
import com.tigo.cs.domain.RouteDetail;
import com.tigo.cs.domain.RouteType;
import com.tigo.cs.domain.RouteUserphone;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.facade.RouteDetailFacade;
import com.tigo.cs.facade.RouteFacade;
import com.tigo.cs.facade.RouteTypeFacade;
import com.tigo.cs.facade.RouteUserphoneFacade;
import com.tigo.cs.facade.UserphoneFacade;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.ApplicationBean;

/**
 * 
 * @author Miguel Zorrilla
 * @version $Id$
 */
@ManagedBean(name = "roadmapBean")
@ViewScoped
public class RoadmapBean extends AbstractServiceBean {

    private static final long serialVersionUID = -3491521591074103178L;
    @EJB
    private RouteFacade routeFacade;
    @EJB
    private RouteDetailFacade routeDetailFacade;
    private Route route;
    private RouteDetail routeDetail;
    //
    private Date dateFrom;
    private Date dateTo;
    private Boolean today = true;
    private Boolean previous = false;
    private Boolean planned = false;
    private List<Userphone> selectedUserphones;
    private List<Userphone> userphones;
    @EJB
    private UserphoneFacade userphoneFacade;
    @EJB
    private RouteUserphoneFacade routeUserphoneFacade;
    //
    private DataModel<Route> dataModelRoute;
    private DataModel<RouteDetail> dataModelRouteDetail;
    private String whereCriteria = "";
    private boolean roadMapMarker;
    //
    @EJB
    private RouteTypeFacade routeTypeFacade;
    private List<RouteType> routeTypeList;
    private Boolean showMap;

    public RoadmapBean() {
        super();
        getSortHelper().setField("routeCod");

    }

    @PostConstruct
    public void init() {
        applyFilter();
        showMap = false;
    }

    //
    //
    // *************************************************************************
    // GETTER AND SETTER.
    // *************************************************************************
    @Override
    public Date getDateFrom() {
        return dateFrom;
    }

    @Override
    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    @Override
    public Date getDateTo() {
        return dateTo;
    }

    @Override
    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Boolean getToday() {
        return today;
    }

    public void setToday(Boolean today) {
        this.today = today;
    }

    public Boolean getPlanned() {
        return planned;
    }

    public void setPlanned(Boolean planned) {
        this.planned = planned;
    }

    public Boolean getPrevious() {
        return previous;
    }

    public void setPrevious(Boolean previous) {
        this.previous = previous;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public RouteDetail getRouteDetail() {
        return routeDetail;
    }

    public void setRouteDetail(RouteDetail routeDetail) {
        this.routeDetail = routeDetail;
    }

    public DataModel<Route> getDataModelRoute() {
        return dataModelRoute;
    }

    public void setDataModelRoute(DataModel<Route> dataModelRoute) {
        this.dataModelRoute = dataModelRoute;
    }

    public DataModel<RouteDetail> getDataModelRouteDetail() {
        return dataModelRouteDetail;
    }

    public void setDataModelRouteDetail(DataModel<RouteDetail> dataModelRouteDetail) {
        this.dataModelRouteDetail = dataModelRouteDetail;
    }

    public RouteFacade getRouteFacade() {
        return routeFacade;
    }

    public void setRouteFacade(RouteFacade routeFacade) {
        this.routeFacade = routeFacade;
    }

    public List<RouteType> getRouteTypeList() {
        if (routeTypeList == null) {
            routeTypeList = routeTypeFacade.findAll();
        }
        return routeTypeList;
    }

    public void setRouteTypeList(List<RouteType> routeTypeList) {
        this.routeTypeList = routeTypeList;
    }

    public List<Userphone> getSelectedUserphones() {
        if (selectedUserphones == null) {
            selectedUserphones = new ArrayList<Userphone>();
        }

        return selectedUserphones;
    }

    public void setSelectedUserphones(List<Userphone> selectedUserphones) {
        this.selectedUserphones = selectedUserphones;
    }

    public List<Userphone> getUserphones() {
        if (userphones == null) {
            userphones = userphoneFacade.findByUserwebAndClassification(getUserweb());
            if (userphones == null) {
                userphones = new ArrayList<Userphone>();
            }
        }
        return userphones;
    }

    public void setUserphones(List<Userphone> userphones) {
        this.userphones = userphones;
    }

    //
    //
    // *************************************************************************
    // OVERRIDE METHODS OF ABSTRACT SERVICE BEAN
    // *************************************************************************
    @Override
    public String nextHeaderPage() {
        dataModelRoute = null; // resetDataModel();
        return super.nextHeaderPage();
    }

    @Override
    public String previousHeaderPage() {
        dataModelRoute = null; // resetDataModel();
        return super.previousHeaderPage();
    }

    @Override
    public String applyFilter() {
        paginationHelper = null; // For pagination recreation
        selectedItems = null; // For clearing selection
        dataModelRoute = getPaginationHelper().createPageDataModel();
        if (getGeolocalizationAllowed() && showMapOnHeader) {
            mapModel = null;
            getMapModel();
            addClientMarker();
        }
        headerMetaDataFromModel();
        signalRead();
        return null;

    }

    @Override
    public String cleanFilter() {
        dataModelRoute = null;
        return super.cleanFilter();
    }

    @Override
    public String applySort() {
        dataModelRoute = getPaginationHelper().createPageDataModel();// resetDataModel();//
                                                                     // clearSpecificDataModel();
        return super.applySort();
    }

    @Override
    public void applyQuantity(ValueChangeEvent evnt) {
        super.applyQuantity(evnt);
        dataModelRoute = null; // For data model recreation
    }

    @Override
    public PaginationHelper getPaginationHelper() {
        if (paginationHelper == null) {
            int pageSize = getRowQuantSelected().length() > 0 ? Integer.valueOf(getRowQuantSelected()).intValue() : 0;
            whereCriteria = "";
            paginationHelper = new PaginationHelper(pageSize) {

                Integer count = null;

                @Override
                public int getItemsCount() {
                    if (count == null) {
                        Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();

                        String where = "  WHERE o.client.clientCod = {0} ";
                        where = MessageFormat.format(where, clientCod.toString());
                        where = where.concat(getCabDetailWhereCriteria());
                        count = routeFacade.count(where);
                    }
                    return count.intValue();
                }

                @Override
                public DataModel createPageDataModel() {
                    Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();

                    String where = "  WHERE o.client.clientCod = {0} ";
                    where = MessageFormat.format(where, clientCod.toString());
                    where = where.concat(getCabDetailWhereCriteria());

                    String orderby = "o.".concat(sortHelper.getField()).concat(sortHelper.isAscending() ? " ASC" : " DESC");
                    return new ListDataModelViewCsTigo(
                            routeFacade.findRange(new int[] { getPageFirstItem(), getPageFirstItem()
                                + getPageSize() }, where, orderby));
                }
            };
        }

        return paginationHelper;
    }

    @Override
    public String applyFilterDetail() {
        paginationHelperDetail = null; // For pagination recreation
        selectedDetailItems = null; // For clearing selection
        dataModelRouteDetail = null;// getPaginationHelper().createPageDataModel();
        return null;
    }

    @Override
    public String applySortDetail() {
        selectedDetailItems = null; // For clearing selection
        dataModelRouteDetail = getPaginationHelperDetail().createPageDataModel();
        return null;
    }

    @Override
    public String previousDetailPage() {
        getPaginationHelperDetail().previousPage();
        dataModelRouteDetail = getPaginationHelperDetail().createPageDataModel(); // For
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

    @Override
    public String nextDetailPage() {
        getPaginationHelperDetail().nextPage();
        dataModelRouteDetail = getPaginationHelperDetail().createPageDataModel(); // For
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

    @Override
    public PaginationHelper getPaginationHelperDetail() {
        if (paginationHelperDetail == null) {
            int pageSize = getRowQuantSelected().length() > 0 ? Integer.valueOf(getRowQuantSelected()).intValue() : 0;

            paginationHelperDetail = new PaginationHelper(pageSize) {

                Integer count = null;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                @Override
                public int getItemsCount() {
                    if (count == null) {
                        count = routeDetailFacade.count(" WHERE o.route.routeCod= ".concat(route.getRouteCod().toString()).concat(getDetailWhereCriteria()));
                    }
                    return count.intValue();
                }

                @Override
                public DataModel createPageDataModel() {
                    String orderby = "o.".concat(sortHelperDetail.getField()).concat(sortHelperDetail.isAscending() ? " ASC" : " DESC");
                    return new ListDataModelViewCsTigo(
                            routeDetailFacade.findRange(new int[] { getPageFirstItem(), getPageFirstItem()
                                + getPageSize() }, " WHERE o.route.routeCod = ".concat(route.getRouteCod().toString()).concat(getDetailWhereCriteria()), orderby));
                }
            };
        }

        return paginationHelperDetail;
    }

    @Override
    protected void setPaginationHelperDetail(PaginationHelper paginationHelperDetail) {
        this.paginationHelperDetail = paginationHelperDetail;
    }

    @Override
    public String viewDetails() {
        route = null;
        Iterator<Route> iterator = getDataModelRoute().iterator();
        while (iterator.hasNext()) {
            Route currentRoute = iterator.next();
            if (getSelectedItems().get(currentRoute.getRouteCod())) {
                if (route == null) {
                    route = routeFacade.find(currentRoute.getRouteCod());
                } else {
                    route = null;
                    setWarnMessage(i18n.iValue("web.client.backingBean.abstractServiceBean.message.MustSelectJustOne"));
                    return null;
                }
            }
        }
        if (route == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractServiceBean.message.MustSelectOne"));
            return null;
        } else {
            // carga la lista usuario telefonicos asociados a la ruta
            List<RouteUserphone> routeUserphones = routeUserphoneFacade.getRouteUserphonesByUserweb(getUserweb(), route);
            if (routeUserphones != null) {
                for (RouteUserphone routeUserphone : routeUserphones) {
                    getSelectedUserphones().add(routeUserphone.getUserphone());
                }
            }
        }

        // Initialize sort with default values
        sortHelperDetail = new SortHelper();
        try {
            sortHelperDetail.setField(getPrimarySortedFieldDetail());
            sortHelperDetail.setAscending(primarySortedFieldDetailAsc);
        } catch (PrimarySortedFieldNotFoundException ex) {
            java.util.logging.Logger.getLogger(AbstractServiceBean.class.getName()).log(Level.SEVERE, i18n.iValue("web.client.backingBean.message.Error")
                + ex.getMessage(), ex);
        }

        paginationHelperDetail = null;
        dataModelRouteDetail = getPaginationHelperDetail().createPageDataModel();
        mapModel = null;
        getMapModel();
        //addClientMarker();
        showMap = true;

        return null;
    }

    @Override
    public String getPrimarySortedField() throws PrimarySortedFieldNotFoundException {
        if (primarySortedField == null) {
            Field[] fieds = Route.class.getDeclaredFields();
            for (Field field : fieds) {
                PrimarySortedField annotation = field.getAnnotation(PrimarySortedField.class);
                if (annotation != null) {
                    primarySortedField = field.getName();
                    primarySortedFieldAsc = annotation.ascending();
                    break;
                }
            }
            if (primarySortedField == null) {
                throw new PrimarySortedFieldNotFoundException(Route.class);
            }
        }
        return primarySortedField;
    }

    @Override
    public String getPrimarySortedFieldDetail() throws PrimarySortedFieldNotFoundException {
        if (primarySortedFieldDetail == null) {
            Field[] fieds = RouteDetail.class.getDeclaredFields();
            for (Field field : fieds) {
                PrimarySortedField annotation = field.getAnnotation(PrimarySortedField.class);
                if (annotation != null) {
                    primarySortedFieldDetail = field.getName();
                    primarySortedFieldDetailAsc = annotation.ascending();
                    break;
                }
            }
            if (primarySortedFieldDetail == null) {
                throw new PrimarySortedFieldNotFoundException(RouteDetail.class);
            }
        }
        return primarySortedFieldDetail;
    }

    @Override
    public String cancelDetail() {
        if (showMapOnDetail) {
            mapModel = null;
            mapCenter = null;
            mapZoom = null;
            mapType = null;
        }
        selectedDetailItems = null;
        selectedUserphones = null;
        setRoute(null);
        setRouteDetail(null);
        setDataModelRouteDetail(null);
        showMap = false;
        return null;
    }

    //
    //
    // *************************************************************************
    // ABSTRACT METHOD
    // *************************************************************************
    @Override
    public String getCabDetailWhereCriteria() {
        if (whereCriteria == null || whereCriteria.equals("")) {

            if (!today
                && !previous && !planned && dateFrom == null && dateTo == null) {
                whereCriteria = " AND 1 = 2 ";
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
            // Por defecto, se toma la fecha de hoy
            Calendar minDate = Calendar.getInstance();
            Calendar maxDate = Calendar.getInstance();

            if (previous) {
                minDate.add(Calendar.DAY_OF_MONTH, -7);
            }

            if (planned) {
                maxDate.add(Calendar.DAY_OF_MONTH, 7);
            }

            // For oracle
            // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd""); //For
            // postgres
            if (dateFrom != null) {
                minDate.setTime(dateFrom);
                // maxDate.add(Calendar.MONTH, 1);
            }
            if (dateTo != null) {
                maxDate.setTime(dateTo);
                // minDate.add(Calendar.MONTH, -1);
            }

            whereCriteria += " AND ( '"
                + sdf.format(minDate.getTime())
                + "' BETWEEN TRUNC (o.fromDate) AND TRUNC (o.toDate) ";
            whereCriteria += " OR '"
                + sdf.format(maxDate.getTime())
                + "' BETWEEN TRUNC (o.fromDate) AND TRUNC (o.toDate) ";
            whereCriteria += " OR TRUNC (o.fromDate) BETWEEN '"
                + sdf.format(minDate.getTime()) + "' AND '"
                + sdf.format(maxDate.getTime()) + "'";
            whereCriteria += " OR TRUNC (o.toDate) BETWEEN '"
                + sdf.format(minDate.getTime()) + "' AND '"
                + sdf.format(maxDate.getTime()) + "' )";
        }
        return whereCriteria;
    }

    @Override
    public String getDetailWhereCriteria() {
        return "";
    }

    @Override
    public String getDetailReportWhereCriteria() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //
    //
    // *************************************************************************
    // NEW FUNCTIONALITY ACTIONS
    // *************************************************************************

    // Operaciones para detalles de Hoja de Ruta
    public String moveUp() {
        routeDetail = null;
        for (RouteDetail currentRouteDetail : getDataModelRouteDetail()) {
            if (getSelectedDetailItems().get(currentRouteDetail.getRouteDetailCod())) {
                if (routeDetail == null) {
                    routeDetail = routeDetailFacade.find(currentRouteDetail.getRouteDetailCod());
                } else {
                    routeDetail = null;
                    setWarnMessage(i18n.iValue("web.client.roadmap.message.MustSelectJustOne"));
                    return null;
                }
            }
        }
        if (routeDetail != null && routeDetail.getPositionNumber() > 1) {
            try {
                routeDetailFacade.updatePosition(routeDetail, true);
            } catch (FacadeException ex) {
                Logger.getLogger(RoadmapBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            // return viewDetails();
            dataModelRouteDetail = getPaginationHelperDetail().createPageDataModel();
            routeDetail = null;
        }
        return null;
    }

    public String moveDown() {
        routeDetail = null;
        for (RouteDetail currentRouteDetail : getDataModelRouteDetail()) {
            if (getSelectedDetailItems().get(currentRouteDetail.getRouteDetailCod())) {
                if (routeDetail == null) {
                    routeDetail = routeDetailFacade.find(currentRouteDetail.getRouteDetailCod());
                } else {
                    routeDetail = null;
                    setWarnMessage(i18n.iValue("web.client.roadmap.message.MustSelectJustOne"));
                    return null;
                }
            }
        }
        if (routeDetail != null) {
            try {
                RouteDetail routeDetailPosterior = routeDetailFacade.getRouteDetailByPosition(routeDetail.getRoute().getRouteCod(), routeDetail.getPositionNumber() + 1);
                if (routeDetailPosterior != null) {
                    routeDetailFacade.updatePosition(routeDetail, false);
                    dataModelRouteDetail = getPaginationHelperDetail().createPageDataModel();
                    routeDetail = null;
                }
            } catch (FacadeException ex) {
                Logger.getLogger(RoadmapBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return null;
    }

    public String removeRouteDetails() {
        boolean flagAtLeastOne = false;
        String entitiesNotDeletedInUse = "";
        String entitiesNotDeletedError = "";

        for (RouteDetail currentEntity : getDataModelRouteDetail()) {
            if (getSelectedDetailItems().get(currentEntity.getRouteDetailCod())) {
                flagAtLeastOne = true;
                try {
                    routeDetailFacade.remove(currentEntity);
                    notifier.signal(RoadmapBean.class, Action.DELETE, getCurrentViewId(), "Se ha borrado un registro de la entidad route_detail", getIpAddress(), getIpAddress());

                } catch (EJBException ejbe) {
                    if (ejbe.getCausedByException().getClass().equals(javax.transaction.RollbackException.class)) {
                        entitiesNotDeletedInUse += ", ".concat(currentEntity.toString());
                    } else {
                        entitiesNotDeletedError += ", ".concat(currentEntity.toString());
                    }
                } catch (Exception e) {
                    // @APP NOTIFICATION
                    notifier.signal(RoadmapBean.class, Action.ERROR, "Error al borrar detalles de hoja de ruta.", e);
                    entitiesNotDeletedError += ", ".concat(currentEntity.toString());
                }
            }
        }
        if (flagAtLeastOne) {
            if (entitiesNotDeletedInUse.length() == 0
                && entitiesNotDeletedError.length() == 0) {
                setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletingByGroupSuccess"));
            } else {
                if (entitiesNotDeletedInUse.length() > 0) {
                    entitiesNotDeletedInUse = entitiesNotDeletedInUse.substring(2);
                    setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletringConstraintByGroupError").concat(entitiesNotDeletedInUse).concat("."));
                }
                if (entitiesNotDeletedError.length() > 0) {
                    entitiesNotDeletedError = entitiesNotDeletedError.substring(2);
                    setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletingByGroupError").concat(entitiesNotDeletedError).concat("."));
                    // @APP NOTIFICATION
                    notifier.signal(RoadmapBean.class, Action.ERROR, "Error inesperado al borrar");
                }
            }
            dataModelRouteDetail = getPaginationHelperDetail().createPageDataModel();
            routeDetail = null;
            mapModel = null;
            roadMapMarker = false;
        } else {
            setWarnMessage(i18n.iValue("web.client.backingBean.message.MustSelectAtLeastOneToDelete"));
        }
        return null;
    }

    // Operaciones sobre cabecera de Hoja de Ruta
    public String newRoute() {
        try {
            route = new Route();
        } catch (Exception e) {
            setErrorMessage(i18n.iValue("web.client.backingBean.abstractServiceBean.message.NewEntityError"));
        }
        mapModel = null;
        showMap = false;
        return null;
    }

    public String removeRoutes() {
        boolean flagAtLeastOne = false;
        String entitiesNotDeletedInUse = "";
        String entitiesNotDeletedError = "";

        for (Route currentEntity : getDataModelRoute()) {
            if (getSelectedItems().get(currentEntity.getRouteCod())) {
                flagAtLeastOne = true;
                try {
                    routeFacade.remove(currentEntity);
                    notifier.signal(RoadmapBean.class, Action.DELETE, getCurrentViewId(), "Se ha borrado un registro de la entidad route. ", getIpAddress(), getIpAddress());

                } catch (EJBException ejbe) {
                    if (ejbe.getCausedByException().getClass().equals(javax.transaction.RollbackException.class)) {
                        entitiesNotDeletedInUse += ", ".concat(currentEntity.toString());
                    } else {
                        entitiesNotDeletedError += ", ".concat(currentEntity.toString());
                    }
                } catch (Exception e) {
                    // @APP NOTIFICATION
                    notifier.signal(RoadmapBean.class, Action.ERROR, "Error al borrar detalles de hoja de ruta.", e);
                    entitiesNotDeletedError += ", ".concat(currentEntity.toString());
                }
            }
        }
        if (flagAtLeastOne) {
            if (entitiesNotDeletedInUse.length() == 0
                && entitiesNotDeletedError.length() == 0) {
                setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletingByGroupSuccess"));
            } else {
                if (entitiesNotDeletedInUse.length() > 0) {
                    entitiesNotDeletedInUse = entitiesNotDeletedInUse.substring(2);
                    setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletringConstraintByGroupError").concat(entitiesNotDeletedInUse).concat("."));
                }
                if (entitiesNotDeletedError.length() > 0) {
                    entitiesNotDeletedError = entitiesNotDeletedError.substring(2);
                    setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletingByGroupError").concat(entitiesNotDeletedError).concat("."));
                    // @APP NOTIFICATION
                    notifier.signal(RoadmapBean.class, Action.ERROR, "Error inesperado al borrar");
                }
            }
            paginationHelper = null;
            dataModelRoute = getPaginationHelper().createPageDataModel();
            selectedItems = null;
        } else {
            setWarnMessage(i18n.iValue("web.client.backingBean.message.MustSelectAtLeastOneToDelete"));
        }
        return null;
    }

    // Operaciones sobre edicion de una Hoja de Ruta
    public String saveRoute() {
        try {
            if (route != null && route.getRouteCod() == null) {
                route.setClient(getUserweb().getClient());
                route.setRouteUserphones(getMergedRouteUserphones());
                // ruta nueva
                // for (Userphone userphone : selectedUserphones) {
                // if (routeUserphoneFacade.hasRouteToday(userphone, route)) {
                // setWarnMessage(MessageFormat.format(i18n.iValue("web.client.roadmap.message.UserphoneWithRouteToday"),
                // userphone.getNameChr()));
                // return null;
                // }
                // }
                routeFacade.create(route);

            } else {
                // ruta modificada
                // for (Userphone userphone : selectedUserphones) {
                // if (routeUserphoneFacade.hasRouteToday(userphone, route)) {
                // setWarnMessage(MessageFormat.format(i18n.iValue("web.client.roadmap.message.UserphoneWithRouteToday"),
                // userphone.getNameChr()));
                // return null;
                // }
                // }

                route.setRouteUserphones(getMergedRouteUserphones());
                routeFacade.edit(route);
            }
            setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.SaveSuccess"));
        } catch (Exception e) {
            setErrorMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotSaveError"));
            notifier.signal(RoadmapBean.class, Action.ERROR, getUserweb(), null, getCurrentViewId(), "Error al guardar datos de la tabla route", getSessionId(), getIpAddress(), e);
        }
        route = null;
        paginationHelper = null; // For pagination recreation
        dataModelRoute = null; // For data model recreation
        selectedItems = null; // For clearing selection
        paginationHelperDetail = null;
        dataModelRouteDetail = null;
        selectedDetailItems = null;
        selectedUserphones = null;
        showMap = false;
        applyFilter();
        return null;
    }

    public String cancelRoute() {
        route = null;
        selectedUserphones = null;
        showMap = false;
        return null;
    }

    public String removeRoute() {

        try {
            if (route.getRouteCod() != null) {
                notifier.signal(RoadmapBean.class, Action.DELETE, getUserweb(), null, getCurrentViewId(), "Se ha borrado un registro de la tabla route.", getSessionId(), getIpAddress());
                routeFacade.remove(route);
            } else {
                setWarnMessage(i18n.iValue("web.admin.backingBean.AbstractCrudBean.message.InexistentRecordDelete"));
            }
            setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletingSucces"));
            route = null;
            routeDetail = null;
            // paginationHelper = null; //For pagination recreation
            // dataModelRoute = null; //For data model recreation
            selectedItems = null; // For clearing selection
            showMap = false;
        } catch (EJBException ejbe) {
            if (ejbe.getCausedByException().getClass().equals(javax.transaction.RollbackException.class)) {
                setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletringConstraintByGroupError"));
            } else {
                setErrorMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletingError"));
                notifier.signal(RoadmapBean.class, Action.ERROR, getUserweb(), null, getCurrentViewId(), "Error al intentar borrar un registro de la tabla route.", getSessionId(), getIpAddress(), ejbe);
            }

        } catch (Exception e) {
            setErrorMessage(i18n.iValue("HA OCURRIDO UN ERROR: ").concat(e.toString()).concat(i18n.iValue(".")));
            notifier.signal(RoadmapBean.class, Action.ERROR, getUserweb(), null, getCurrentViewId(), "Error inesperado al intentar borrar datos de la tabla route.", getSessionId(), getIpAddress(), e);
        }
        
        route = null;
        paginationHelper = null; // For pagination recreation
        dataModelRoute = null; // For data model recreation
        selectedItems = null; // For clearing selection
        paginationHelperDetail = null;
        dataModelRouteDetail = null;
        selectedDetailItems = null;
        selectedUserphones = null;
        showMap = false;
        applyFilter();

        return null;
    }

    public String applyRouteTimes() {
        if (route != null && route.getRouteType() != null) {
            switch (route.getRouteType().getRouteTypeCod().intValue()) {
            case 1:
                route.setToDate(route.getFromDate());
                break;
            case 2:
                Calendar nextWeekDate = Calendar.getInstance();
                if (route != null && route.getFromDate() != null) {
                    nextWeekDate.setTime(route.getFromDate());
                } else {
                    route.setFromDate(nextWeekDate.getTime());
                }
                nextWeekDate.add(Calendar.DAY_OF_MONTH, 7);
                route.setToDate(nextWeekDate.getTime());
                break;
            case 3:
                Calendar cal = Calendar.getInstance();
                if (route.getFromDate() == null) {
                    route.setFromDate(cal.getTime());
                } else {
                    cal.setTime(route.getFromDate());
                }

                GregorianCalendar gc = new GregorianCalendar(
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                        cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                route.setToDate(gc.getTime());
                gc.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMinimum(Calendar.DAY_OF_MONTH));
                route.setFromDate(gc.getTime());
                break;
            case 4:
                break;
                // route.setFromDate(Calendar.getInstance().getTime());
                // route.setToDate(Calendar.getInstance().getTime());
            default:
                route.getRouteType().setRouteTypeCod(0L);
            }
        }
        return null;
    }

    private List<RouteUserphone> getMergedRouteUserphones() {
        List<RouteUserphone> removableRouteUserphones = new ArrayList<RouteUserphone>();

        // Evaluar si fue des-asignado un userphone para la ruta.
        List<RouteUserphone> actualList = routeUserphoneFacade.getRouteUserphonesByRoute(route.getRouteCod());
        for (RouteUserphone routeUserphone : actualList) {
            if (!selectedUserphones.contains(routeUserphone.getUserphone())) {
                removableRouteUserphones.add(routeUserphone);
            }
        }
        actualList.removeAll(removableRouteUserphones);

        // Evaluar si habria que asignar la ruta a un userphone
        for (Userphone userphone : selectedUserphones) {
            RouteUserphone routeUserphone = new RouteUserphone(
                    route.getRouteCod(), userphone.getUserphoneCod());
            routeUserphone.setRoute(route);
            routeUserphone.setUserphone(userphone);
            if (!actualList.contains(routeUserphone)) {
                actualList.add(routeUserphone);
            }
        }

        return actualList;
    }

    //
    //
    // *************************************************************************
    // MAP METHODS
    // *************************************************************************
    public Boolean getShowMap() {
        return showMap;
    }

    public void setShowMap(Boolean showMap) {
        this.showMap = showMap;
    }

    @Override
    public MapModel getMapModel() {
        if (mapModel == null) {
            mapModel = new DefaultMapModel();
            mapCenter = ApplicationBean.getInstance().getMapStartingPoint();
            mapZoom = Integer.parseInt(ApplicationBean.getInstance().getMapStartingZoom());
            mapType = ApplicationBean.getInstance().getMapStartingType();
            addClientMarker();
        }
        return mapModel;
    }

    @Override
    public void saveClientMarker() {
        super.saveClientMarker();
        setMapModel(null);
        roadMapMarker = false;
    }

    @Override
    public void onMarkerSelect(OverlaySelectEvent event) {
        if (event.getOverlay() instanceof ClientMarker) {
            setSelectedMarker((Marker) event.getOverlay());
            setClientMarker(true);
            setEditMarker(false);
        } else if (event.getOverlay() instanceof RoadMapMarker) {
            setSelectedMarker((Marker) event.getOverlay());
            roadMapMarker = true;
            setClientMarker(false);
            setEditMarker(false);
        }
    }

    public boolean isRoadMapMarker() {
        return roadMapMarker;
    }

    public void setRoadMapMarker(boolean roadMapMarker) {
        this.roadMapMarker = roadMapMarker;
    }

    @Override
    public void addClientMarker() {

        Iterator<RouteDetail> iterator = dataModelRouteDetail.iterator();

        List<Marker> forPolyline = new ArrayList<Marker>();
        // List<MapMark> mapMarks =
        // mapMarksFacade.findClientMarks(getUserweb().getClient());
        List<MapMark> marks = new ArrayList<MapMark>();
        for (; iterator.hasNext();) {
            RouteDetail detail = iterator.next();
            MapMark mapMark = detail.getMapMark();

            RoadMapMarker mapMarker = new RoadMapMarker(
                    new LatLng(mapMark.getLatitudeNum(),
                            mapMark.getLongitudeNum()),
                    detail.getPositionNumber().toString().concat(" - ").concat(mapMark.getTitleChr()),
                    true, getMapZoom().toString());
            mapMarker.setTitle(mapMark.getTitleChr());
            mapMarker.setData(mapMark.getDescriptionChr());
            if (!marks.contains(mapMark)) {
                mapModel.addOverlay(mapMarker);
            }
            forPolyline.add(mapMarker);
            marks.add(mapMark);
        }

        getMapModel().addOverlay(getMarkersPolyline(forPolyline));

        List<MapMark> mapMarks = routeDetailFacade.getNotInRouteDetailMark(route, getUserweb().getClient());
        for (MapMark mapMark : mapMarks) {

            RoadMapMarker mapMarker = new RoadMapMarker(new LatLng(
                    mapMark.getLatitudeNum(), mapMark.getLongitudeNum()),
                    mapMark.getTitleChr(), false, getMapZoom().toString());
            mapMarker.setTitle(mapMark.getTitleChr());
            mapMarker.setData(mapMark.getDescriptionChr());
            mapModel.addOverlay(mapMarker);
        }

    }

    public String addRoadMapMark() {
        if (getSelectedMarker() != null
            && getSelectedMarker() instanceof RoadMapMarker) {
            routeDetailFacade.addDetailToRoute(getUserweb().getClient(), route, getSelectedMarker().getLatlng().getLat(), getSelectedMarker().getLatlng().getLng(), (String) getSelectedMarker().getData());
        }
        dataModelRouteDetail = getPaginationHelperDetail().createPageDataModel();
        routeDetail = null;
        setMapModel(null);
        roadMapMarker = false;
        return null;
    }
}

class RoadMapMarker extends Marker {

    private static final long serialVersionUID = 769277275042978694L;

    public RoadMapMarker(LatLng latLng, String cliente, Boolean inRoute,
            String zoom) {
        super(
                latLng,
                null,
                null,
                FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath().concat("/markImage?".concat("cliente=".concat(cliente))).concat("&inRoute=").concat(inRoute.toString().concat("&zoom=").concat(zoom)));
    }
}
