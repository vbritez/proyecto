package com.tigo.cs.view.service;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;

import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Polygon;

import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.util.ListDataModelViewCsTigo;
import com.tigo.cs.commons.util.PaginationHelper;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.Service;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.ApplicationBean;

/**
 * 
 * @author Javier Kovacs
 * @version $Id$
 */
@ManagedBean(name = "serviceBean")
@ViewScoped
public class ServiceBean extends AbstractServiceBean {

    private static final long serialVersionUID = 8941130380487035926L;
    private List<Service> serviceList = null;
    private Service selectedService;

    private PaginationHelper paginationHelper;

    public ServiceBean() {
        setShowMapOnHeader(true);
        getSortHelper().setField("servicevalueCod");
        getMapModel();
        setPdfReportCabDetailName("rep_service_full");
        setXlsReportCabDetailName("rep_service_full_xls");
    }

    public Service getSelectedService() {
        return selectedService;
    }

    public void setSelectedService(Service selectedService) {
        this.selectedService = selectedService;
    }

    public List<Service> getServiceList() {
        if (serviceList == null) {
            serviceList = serviceFacade.findAll();
            for (Service s : serviceList) {
                //TODO: pasa al lado xhtml
                s.setDescriptionChr(i18n.iValue(s.getDescriptionChr()));
            }
        }
        return serviceList;
    }

    // ABSTRACT METHOD IMPLEMENTATIONS
    @Override
    public String getCabDetailWhereCriteria() {

        String where = " AND o.enabledChr = true AND o.userphone.client.clientCod = "
            + getUserweb().getClient().getClientCod();

        where = where.concat(super.getCabDetailWhereCriteria());
        if (selectedService != null) {
            where += " AND o.enabledChr = true AND o.service.serviceCod = ".concat(selectedService.getServiceCod().toString());
        }
        return where;
    }

    @Override
    public String getCabDetailReportWhere() {
        Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        String classifications = classificationFacade.getClassificationUserwebString(SecurityBean.getInstance().getLoggedInUserClient());

        String where = " AND sv.enabled_chr = '1' AND U.COD_CLIENT = ".concat(clientCod.toString());
        where += MessageFormat.format(" and EXISTS "
            + "(select * from USERPHONE_CLASSIFICATION uc "
            + "where uc.cod_userphone = u.userphone_cod "
            + "and uc.cod_classification in ({0})) ", classifications);

        where = where.concat(getCabDetailReportWhereCriteria());
        return where;
    }

    @Override
    public String getCabDetailReportWhereCriteria() {

        String where = "";

        where = where.concat(super.getCabDetailReportWhereCriteria());

        if (selectedService != null) {
            where += " AND sv.enabled_chr = '1' AND s.SERVICE_COD = ".concat(selectedService.getServiceCod().toString());
        }

        return where;
    }

    @Override
    public String getCabDetailReportOrderBy() {
        String sortAttributeColumnName = getAttributeColumnName(ServiceValue.class, getSortHelper().getField()).toLowerCase();

        if (sortAttributeColumnName.equals("datein_dat")) {
            sortAttributeColumnName = "m.".concat(sortAttributeColumnName);
        } else if (sortAttributeColumnName.equals("cellphone_num")) {
            sortAttributeColumnName = "u.".concat(sortAttributeColumnName);
        } else if (sortAttributeColumnName.equals("name_chr")) {
            sortAttributeColumnName = "u.".concat(sortAttributeColumnName);
        } else if (sortAttributeColumnName.equals("description_chr")) {
            sortAttributeColumnName = "s.".concat(sortAttributeColumnName);
        }

        return "ORDER BY ".concat(sortAttributeColumnName).concat(getSortHelper().isAscending() ? " ASC" : " DESC");
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
    public PaginationHelper getPaginationHelper() {
        if (paginationHelper == null) {
            int pageSize = getRowQuantSelected().length() > 0 ? Integer.valueOf(getRowQuantSelected()).intValue() : 0;

            paginationHelper = new PaginationHelper(pageSize) {
                Integer count = null;

                @Override
                public int getItemsCount() {
                    if (count == null) {
                        Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
//                        List<Classification> classifications = classificationFacade.findByUserweb(getUserweb());

                        List<Classification> classifications = classificationFacade.findByUserwebWithChilds(getUserweb());
                        String where = " WHERE o.enabledChr = true AND EXISTS ( SELECT u FROM Userphone u ,in (u.classificationList) cl WHERE u.userphoneCod = o.userphone.userphoneCod AND cl in (:classifications) ) ";
                        where = MessageFormat.format(where, clientCod.toString());
                        where = where.concat(getCabDetailWhereCriteria());
                        count = facade.count(where, classifications);
                    }
                    return count.intValue();
                }

                @Override
                public DataModel createPageDataModel() {
                    Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
//                    List<Classification> classifications = classificationFacade.findByUserweb(getUserweb());
                    
                    List<Classification> classifications = classificationFacade.findByUserwebWithChilds(getUserweb());
                    String where = " WHERE o.enabledChr = true AND EXISTS ( SELECT u FROM Userphone u ,in (u.classificationList) cl WHERE u.userphoneCod = o.userphone.userphoneCod AND cl in (:classifications) ) ";
                    where = MessageFormat.format(where, clientCod.toString());
                    where = where.concat(getCabDetailWhereCriteria());

                    String orderby = "o.".concat(getSortHelper().getField()).concat(getSortHelper().isAscending() ? " ASC" : " DESC");
                    return new ListDataModelViewCsTigo(
                            facade.findRange(new int[] { getPageFirstItem(), getPageFirstItem()
                                + getPageSize() }, where, orderby, classifications));
                }
            };
        }

        return paginationHelper;
    }

    @Override
    public String showHeaderMap() {
        Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();

        setMapModel(new DefaultMapModel());
        boolean oneInsideBounds = false;
        Map<String, ArrayList<Marker>> positions = new HashMap<String, ArrayList<Marker>>();

        Iterator<ServiceValue> iterator = getDataModelHeader().iterator();
        int nextMarkerCounter = 1;
        while (iterator.hasNext()) {
            ServiceValue currServiceValue = iterator.next();
            if (getSelectedItems().containsKey(currServiceValue.getServicevalueCod())
                && getSelectedItems().get(currServiceValue.getServicevalueCod())
                && currServiceValue.getMessage() != null 
                && (currServiceValue.getMessage().getCell() != null || (currServiceValue.getMessage().getLatitude() != null && currServiceValue.getMessage().getLongitude() != null)) ) {
                               		
				// Message for marker
                DateFormat sdf = new SimpleDateFormat(
                        ApplicationBean.getInstance().getDefaultOutputDateTimeFormat());
                String messDescrip = currServiceValue.getMessage().getUserphone().getNameChr().concat(" | ").concat(sdf.format(currServiceValue.getMessage().getDateinDat()));
                String key = "";
                String value = "";
                switch (getCodService()) {
                case 2:
                    key = currServiceValue.getColumn1Chr();
                    break;
                }

                if (key != null && key.trim().length() > 0) {
                    value = "";
                    try {
                        value = tmpWsresultFacade.findData(codClient, getSessionId(), "C", key);
                        value = value != null && value.trim().length() > 0 ? " - ".concat(value.trim()) : "";
                    } catch (Exception ex) {
                        notifier.signal(getClass(), Action.ERROR, SecurityBean.getInstance().getLoggedInUserClient(), null, getCurrentViewId(), "Error al obtener valores de la tabla temporal para clientes", getSessionId(), getIpAddress(), ex);
                    }

                    messDescrip = messDescrip.concat(" | ").concat(key).concat(value);
                }
                
                // Obtain latitude, longitude and azimuth from cell
            	Double latitude = currServiceValue.getMessage().getLatitude();
				Double longitude = currServiceValue.getMessage().getLongitude();
                Polygon polygonArea = null;
                Marker markerArea = null;		

                if (latitude != null && longitude != null){
					markerArea = getGPSCellAreaMarker(latitude, longitude, messDescrip, String.valueOf(nextMarkerCounter));
				}
				else{    				
    				
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

				}//HASTA ACA EL IF
                
                // Add the marker
                if (markerArea != null) {
                    // if the marker already exists, do not add, but instead add
                    // data to existing one.
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
                setMapCenter(getMapModel().getMarkers().get(0).getLatlng());
            }
            if (getMapModel().getMarkers().size() > 1) {
                addMarkersPolylineDiferent(positions, getMapModel());
            }
        }
        addClientMarker();
        return null;
    }

    @Override
    public String applyFilter() {
        paginationHelper = null; // For pagination recreation
        setSelectedItems(null); // For clearing selection
        setDataModelHeader(getPaginationHelper().createPageDataModel());
        if (isShowMapOnHeader()) {
            setMapModel(null);
            getMapModel();
            addClientMarker();
        }
        signalRead();

        return null;
    }
}
