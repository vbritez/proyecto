package com.tigo.cs.view.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.validation.ConstraintViolationException;

import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.facade.GlobalParameterFacade;
import com.tigo.cs.security.SecurityBean;

@ManagedBean(name = "trackingBean")
@ViewScoped
public class TrackingBean extends AbstractServiceBean {

    private static final long serialVersionUID = 3442076598786367577L;
    public static final int COD_SERVICE = 4;
    @EJB
    private GlobalParameterFacade globalParameterFacade;
    private Map<Long, String> mapUserphoneTrackingType = new HashMap<Long, String>();

    public TrackingBean() {
        setCodService(COD_SERVICE);
        setShowMapOnHeader(true);
        getSortHelper().setField("servicevalueCod");
        getMapModel();
    }

    @PostConstruct
    public void init() {
        addClientMarker();
    }

    @Override
    public String getCabDetailReportWhereCriteria() {
        return "";
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
    public void deleteClientMarker() {
        if (isClientMarker()) {
            Client client = getUserweb().getClient();
            Double latitude = getSelectedMarker().getLatlng().getLat();
            Double longitude = getSelectedMarker().getLatlng().getLng();
            try {
                mapMarksFacade.removeByClientLatLng(client, latitude, longitude);

            } catch (ConstraintViolationException ex) {
                setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletringConstraintError"));
                return;
            } catch (Exception e) {
                notifier.log(getClass(), null, Action.ERROR, e.getMessage());
                setErrorMessage(i18n.iValue("web.client.backingbean.error.restriction"));
                ;
                return;
            }
            setMapModel(null);
            getMapModel();
            if (getDataModelHeader() != null) {
                showHeaderMap();
            }
            addClientMarker();
        }
    }

    @Override
    public List<SelectItem> getRowQuantList() {
        if (rowQuantList == null) {
            rowQuantList = new ArrayList<SelectItem>();
            rowQuantList.add(new SelectItem("15", "15"));
            rowQuantList.add(new SelectItem("25", "25"));
            rowQuantList.add(new SelectItem("50", "50"));
            rowQuantList.add(new SelectItem("100", "100"));
            rowQuantList.add(new SelectItem("200", "200"));
            rowQuantList.add(new SelectItem("300", "300"));
            rowQuantList.add(new SelectItem("400", "400"));
            rowQuantList.add(new SelectItem("500", "500"));

            if (showAllInQuantityList()) {
                rowQuantList.add(new SelectItem(String.valueOf(getPaginationHelper().getItemsCount()), i18n.iValue("web.client.backingBean.message.All")));
            }
        }
        return rowQuantList;
    }

    @Override
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

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy hh:mm:ss aa");
        SimpleDateFormat dateOnlySdf = new SimpleDateFormat("dd-MMM-yy");
        if (dateFrom != null) {
            where += " AND o.message.dateinDat >= '".concat(
                    sdf.format(dateFrom)).concat("'");
            where += " AND o.recorddateDat >= '".concat(
                    dateOnlySdf.format(dateFrom)).concat("'");
        }
        if (dateTo != null) {
            where += " AND o.message.dateinDat <= '".concat(sdf.format(dateTo)).concat(
                    "'");
            where += " AND o.recorddateDat <= '".concat(dateOnlySdf.format(dateTo)).concat(
                    "'");
        }

        return where;
    }

    public Map<Long, String> getMapUserphoneTrackingType() {
        return mapUserphoneTrackingType;
    }

    public void setMapUserphoneTrackingType(Map<Long, String> mapUserphoneTrackingType) {
        this.mapUserphoneTrackingType = mapUserphoneTrackingType;
    }

    @Override
    public void headerMetaDataFromModel() {
        mapUserphoneTrackingType = new HashMap<Long, String>();
        for (ServiceValue sv : getDataModelHeader()) {
            try {
                if (facadeContainer.getUserphoneAPI().getAndroidEnabledTracking(
                        sv.getUserphone().getUserphoneCod())) {
                    mapUserphoneTrackingType.put(
                            sv.getUserphone().getUserphoneCod(),
                            "tracking.android");
                } else {
                    mapUserphoneTrackingType.put(
                            sv.getUserphone().getUserphoneCod(), "tracking.lbs");
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
