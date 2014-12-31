package com.tigo.cs.view;

import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.primefaces.model.map.LatLng;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Application;
import com.tigo.cs.facade.ApplicationFacade;
import com.tigo.cs.facade.GlobalParameterFacade;

@ManagedBean(name = "applicationBean")
@ApplicationScoped
public class ApplicationBean extends SMBaseBean {

    private static final long serialVersionUID = 1933540144628297968L;
    @EJB
    private GlobalParameterFacade globalParameterFacade;
    private String defaultCrudPaginationPageSize;
    private String defaultServicePaginationPageSize;
    private String defaultTimeZoneID;
    private TimeZone defaultTimeZone;
    private String defaultOutputDateFormat;
    private String defaultInputDateFormat;
    private String defaultOutputDateTimeFormat;
    private String defaultInputDateTimeFormat;
    private String defaultOutputTimeFormat;
    private String defaultInputTimeFormat;
    private String defaultLocaleLanguage;
    private String defaultLocaleCountry;
    private Locale defaultLocale;
    private String mapCellSectorAreaDistance;
    private String mapCellSectorAreaAngle;
    private String mapCellSectorAreaBackgroundColor;
    private String mapCellSectorAreaBackgroundOpacity;
    private String mapCellOmniAreaRadius;
    private String mapPolylineColor;
    private String mapPolylineOpacity;
    private String mapPolylineWeight;
    private String mapMarkerTextColor;
    private String mapMarkerBgColor;
    private String mapStartingZoom;
    private String mapStartingType;
    private String mapStartingLat;
    private String mapStartingLng;
    private LatLng mapStartingPoint;
    private Integer loginRetryLimit;
    private String fileUploadMaxFileSize;
    private String smscShortNumber;
    private String smsCenterNumber;
    private String defaultDurationFormat;
    private Integer columnWidthChar;
    private String otScheduleDateFormat;
    private String appKey;
    private String mapMarkerTextColorSelected;
    private String mapMarkerBgColorSelected;

    @EJB
    protected ApplicationFacade applicationFacade;
    protected Application application;

    public ApplicationBean() {
    }

    public static ApplicationBean getInstance() {
        return (ApplicationBean) getBean("applicationBean");
    }

    @PostConstruct
    public void postConstruct() {
        application = applicationFacade.find(3L);
    }

    public Application getApplication() {
        return application;
    }

    public Integer getLoginRetryLimit() {
        if (validateSync(loginRetryLimit)) {
            try {
                loginRetryLimit = Integer.parseInt(globalParameterFacade.findByCode("password.loginretry"));

            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            } catch (Exception e) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, e);
            }
        }
        if (loginRetryLimit == null) {
            loginRetryLimit = 3;
        }
        return loginRetryLimit;
    }

    public void setLoginRetryLimit(Integer loginRetryLimit) {
        this.loginRetryLimit = loginRetryLimit;
    }

    public String getDefaultCrudPaginationPageSize() {
        if (validateSync(defaultCrudPaginationPageSize)) {
            try {
                defaultCrudPaginationPageSize = globalParameterFacade.findByCode("pagesize.pagination.crud.default");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (defaultCrudPaginationPageSize == null
                || defaultCrudPaginationPageSize.length() == 0) {
                defaultCrudPaginationPageSize = "15";
            }
        }
        return defaultCrudPaginationPageSize;
    }

    public void setDefaultCrudPaginationPageSize(String defaultCrudPaginationPageSize) {
        this.defaultCrudPaginationPageSize = defaultCrudPaginationPageSize;
    }

    public String getDefaultServicePaginationPageSize() {
        if (validateSync(defaultServicePaginationPageSize)) {
            try {
                defaultServicePaginationPageSize = globalParameterFacade.findByCode("pagesize.pagination.service.default");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (defaultServicePaginationPageSize == null
                || defaultServicePaginationPageSize.length() == 0) {
                defaultServicePaginationPageSize = "15";
            }
        }
        return defaultServicePaginationPageSize;
    }

    public void setDefaultServicePaginationPageSize(String defaultServicePaginationPageSize) {
        this.defaultServicePaginationPageSize = defaultServicePaginationPageSize;
    }

    public String getDefaultTimeZoneID() {
        if (validateSync(defaultTimeZoneID)) {
            try {
                defaultTimeZoneID = globalParameterFacade.findByCode("timezone.default");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (defaultTimeZoneID == null || defaultTimeZoneID.length() == 0) {
                defaultTimeZoneID = "America/Asuncion";
            }
        }
        return defaultTimeZoneID;
    }

    public void setDefaultTimeZoneID(String defaultTimeZone) {
        this.defaultTimeZoneID = defaultTimeZone;
    }

    public TimeZone getDefaultTimeZone() {
        if (validateSync(defaultTimeZone)) {
            defaultTimeZone = TimeZone.getTimeZone(getDefaultTimeZoneID());
        }
        return defaultTimeZone;
    }

    public void setDefaultTimeZone(TimeZone defaultTimeZone) {
        this.defaultTimeZone = defaultTimeZone;
    }

    public String getDefaultOutputDateFormat() {
        if (validateSync(defaultOutputDateFormat)) {
            try {
                defaultOutputDateFormat = globalParameterFacade.findByCode("format.date.output.default");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (defaultOutputDateFormat == null
                || defaultOutputDateFormat.length() == 0) {
                defaultOutputDateFormat = "dd/MM/yyyy";
            }
        }
        return defaultOutputDateFormat;
    }

    public void setDefaultOutputDateFormat(String defaultOutputDateFormat) {
        this.defaultOutputDateFormat = defaultOutputDateFormat;
    }

    public String getDefaultInputDateFormat() {
        if (validateSync(defaultInputDateFormat)) {
            try {
                defaultInputDateFormat = globalParameterFacade.findByCode("format.date.input.default");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (defaultInputDateFormat == null
                || defaultInputDateFormat.length() == 0) {
                defaultInputDateFormat = "dd/MM/yyyy";
            }
        }
        return defaultInputDateFormat;
    }

    public void setDefaultInputDateFormat(String defaultInputDateFormat) {
        this.defaultInputDateFormat = defaultInputDateFormat;
    }

    public String getDefaultOutputDateTimeFormat() {
        if (validateSync(defaultOutputDateTimeFormat)) {
            try {
                defaultOutputDateTimeFormat = globalParameterFacade.findByCode("format.datetime.output.default");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (defaultOutputDateTimeFormat == null
                || defaultOutputDateTimeFormat.length() == 0) {
                defaultOutputDateTimeFormat = "dd/MM/yyyy HH:mm";
            }
        }
        return defaultOutputDateTimeFormat;
    }

    public void setDefaultOutputDateTimeFormat(String defaultOutputDateTimeFormat) {
        this.defaultOutputDateTimeFormat = defaultOutputDateTimeFormat;
    }

    public String getDefaultInputDateTimeFormat() {
        if (validateSync(defaultInputDateTimeFormat)) {
            try {
                defaultInputDateTimeFormat = globalParameterFacade.findByCode("format.datetime.input.default");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (defaultInputDateTimeFormat == null
                || defaultInputDateTimeFormat.length() == 0) {
                defaultInputDateTimeFormat = "dd/MM/yyyy HH:mm";
            }
        }
        return defaultInputDateTimeFormat;
    }

    public void setDefaultInputDateTimeFormat(String defaultInputDateTimeFormat) {
        this.defaultInputDateTimeFormat = defaultInputDateTimeFormat;
    }

    public String getDefaultInputTimeFormat() {
        if (validateSync(defaultInputTimeFormat)) {
            try {
                defaultInputTimeFormat = globalParameterFacade.findByCode("format.time.input.default");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (defaultInputTimeFormat == null
                || defaultInputTimeFormat.length() == 0) {
                defaultInputTimeFormat = "HH:mm";
            }
        }
        return defaultInputTimeFormat;
    }

    public void setDefaultInputTimeFormat(String defaultInputTimeFormat) {
        this.defaultInputTimeFormat = defaultInputTimeFormat;
    }

    public String getDefaultDurationFormat() {
        if (validateSync(defaultDurationFormat)) {
            try {
                defaultDurationFormat = globalParameterFacade.findByCode("format.duration.output.default");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (defaultDurationFormat == null
                || defaultDurationFormat.length() == 0) {
                defaultDurationFormat = "HH:mm:ss";
            }
        }

        return defaultDurationFormat;
    }

    public void setDefaultDurationFormat(String defaultDurationFormat) {
        this.defaultDurationFormat = defaultDurationFormat;
    }

    public String getDefaultOutputTimeFormat() {
        if (validateSync(defaultOutputTimeFormat)) {
            try {
                defaultOutputTimeFormat = globalParameterFacade.findByCode("format.time.output.default");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (defaultOutputTimeFormat == null
                || defaultOutputTimeFormat.length() == 0) {
                defaultOutputTimeFormat = "HH:mm";
            }
        }
        return defaultOutputTimeFormat;
    }

    public void setDefaultOutputTimeFormat(String defaultOutputTimeFormat) {
        this.defaultOutputTimeFormat = defaultOutputTimeFormat;
    }

    public String getDefaultLocaleCountry() {
        if (validateSync(defaultLocaleCountry)) {
            try {
                defaultLocaleCountry = globalParameterFacade.findByCode("locale.country.default");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (defaultLocaleCountry == null
                || defaultLocaleCountry.length() == 0) {
                defaultLocaleCountry = "py";
            }
        }
        return defaultLocaleCountry;
    }

    public void setDefaultLocaleCountry(String defaultLocaleCountry) {
        this.defaultLocaleCountry = defaultLocaleCountry;
    }

    public String getDefaultLocaleLanguage() {
        if (validateSync(defaultLocaleLanguage)) {
            try {
                defaultLocaleLanguage = globalParameterFacade.findByCode("locale.language.default");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (defaultLocaleLanguage == null
                || defaultLocaleLanguage.length() == 0) {
                defaultLocaleLanguage = "es";
            }
        }
        return defaultLocaleLanguage;
    }

    public void setDefaultLocaleLanguage(String defaultLocaleLanguage) {
        this.defaultLocaleLanguage = defaultLocaleLanguage;
    }

    public Locale getDefaultLocale() {
        if (validateSync(defaultLocale)) {
            defaultLocale = new Locale(getDefaultLocaleLanguage(), getDefaultLocaleCountry());
        }
        return defaultLocale;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String getMapCellSectorAreaBackgroundColor() {
        if (validateSync(mapCellSectorAreaBackgroundColor)) {
            try {
                mapCellSectorAreaBackgroundColor = globalParameterFacade.findByCode("map.cell.sector.area.bg.color");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (mapCellSectorAreaBackgroundColor == null
                || mapCellSectorAreaBackgroundColor.length() == 0) {
                mapCellSectorAreaBackgroundColor = "#D3655A";
            }
        }
        return mapCellSectorAreaBackgroundColor;
    }

    public void setMapCellSectorAreaBackgroundColor(String mapCellSectorAreaBackgroundColor) {
        this.mapCellSectorAreaBackgroundColor = mapCellSectorAreaBackgroundColor;
    }

    public String getMapCellSectorAreaBackgroundOpacity() {
        if (validateSync(mapCellSectorAreaBackgroundOpacity)) {
            try {
                mapCellSectorAreaBackgroundOpacity = globalParameterFacade.findByCode("map.cell.sector.area.bg.opacity");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (mapCellSectorAreaBackgroundOpacity == null
                || mapCellSectorAreaBackgroundOpacity.length() == 0) {
                mapCellSectorAreaBackgroundOpacity = "0.3";
            }
        }
        return mapCellSectorAreaBackgroundOpacity;
    }

    public void setMapCellSectorAreaBackgroundOpacity(String mapCellSectorAreaBackgroundOpacity) {
        this.mapCellSectorAreaBackgroundOpacity = mapCellSectorAreaBackgroundOpacity;
    }

    public String getMapCellSectorAreaDistance() {
        if (validateSync(mapCellSectorAreaDistance)) {
            try {
                mapCellSectorAreaDistance = globalParameterFacade.findByCode("map.cell.sector.area.distance");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (mapCellSectorAreaDistance == null
                || mapCellSectorAreaDistance.length() == 0) {
                mapCellSectorAreaDistance = "20000";
            }
        }
        return mapCellSectorAreaDistance;
    }

    public void setMapCellSectorAreaDistance(String mapCellSectorAreaDistance) {
        this.mapCellSectorAreaDistance = mapCellSectorAreaDistance;
    }

    public String getMapCellSectorAreaAngle() {
        if (validateSync(mapCellSectorAreaAngle)) {
            try {
                mapCellSectorAreaAngle = globalParameterFacade.findByCode("map.cell.sector.area.angle");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (mapCellSectorAreaAngle == null
                || mapCellSectorAreaAngle.length() == 0) {
                mapCellSectorAreaAngle = "120";
            }
        }
        return mapCellSectorAreaAngle;
    }

    public void setMapCellSectorAreaAngle(String mapCellSectorAreaRadius) {
        this.mapCellSectorAreaAngle = mapCellSectorAreaRadius;
    }

    public String getMapCellOmniAreaRadius() {
        if (validateSync(mapCellOmniAreaRadius)) {
            try {
                mapCellOmniAreaRadius = globalParameterFacade.findByCode("map.cell.omni.area.radius");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (mapCellOmniAreaRadius == null
                || mapCellOmniAreaRadius.length() == 0) {
                mapCellOmniAreaRadius = "10000";
            }
        }
        return mapCellOmniAreaRadius;
    }

    public void setMapCellOmniAreaRadius(String mapCellOmniAreaRadius) {
        this.mapCellOmniAreaRadius = mapCellOmniAreaRadius;
    }

    public String getMapPolylineColor() {
        if (validateSync(mapPolylineColor)) {
            try {
                mapPolylineColor = globalParameterFacade.findByCode("map.polyline.color");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (mapPolylineColor == null || mapPolylineColor.length() == 0) {
                mapPolylineColor = "#D3655A";
            }
        }
        return mapPolylineColor;
    }

    public void setMapPolylineColor(String mapPolylineColor) {
        this.mapPolylineColor = mapPolylineColor;
    }

    public String getMapPolylineOpacity() {
        if (validateSync(mapPolylineOpacity)) {
            try {
                mapPolylineOpacity = globalParameterFacade.findByCode("map.polyline.opacity");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (mapPolylineOpacity == null || mapPolylineOpacity.length() == 0) {
                mapPolylineOpacity = "0.9";
            }
        }
        return mapPolylineOpacity;
    }

    public void setMapPolylineOpacity(String mapPolylineOpacity) {
        this.mapPolylineOpacity = mapPolylineOpacity;
    }

    public String getMapPolylineWeight() {
        if (validateSync(mapPolylineWeight)) {
            try {
                mapPolylineWeight = globalParameterFacade.findByCode("map.polyline.weight");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (mapPolylineWeight == null || mapPolylineWeight.length() == 0) {
                mapPolylineWeight = "2";
            }
        }
        return mapPolylineWeight;
    }

    public void setMapPolylineWeight(String mapPolylineWeight) {
        this.mapPolylineWeight = mapPolylineWeight;
    }

    public String getMapMarkerBgColor() {
        if (validateSync(mapMarkerBgColor)) {
            try {
                mapMarkerBgColor = globalParameterFacade.findByCode("map.marker.bg.color");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (mapMarkerBgColor == null || mapMarkerBgColor.length() == 0) {
                mapMarkerBgColor = "#FF776B";
            }
        }
        return mapMarkerBgColor;
    }

    public void setMapMarkerBgColor(String mapMarkerBgColor) {
        this.mapMarkerBgColor = mapMarkerBgColor;
    }

    public String getMapMarkerTextColor() {
        if (validateSync(mapMarkerTextColor)) {
            try {
                mapMarkerTextColor = globalParameterFacade.findByCode("map.marker.text.color");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (mapMarkerTextColor == null || mapMarkerTextColor.length() == 0) {
                mapMarkerTextColor = "#000000";
            }
        }
        return mapMarkerTextColor;
    }

    public void setMapMarkerTextColor(String mapMarkerTextColor) {
        this.mapMarkerTextColor = mapMarkerTextColor;
    }

    public String getMapStartingZoom() {
        if (validateSync(mapStartingZoom)) {
            try {
                mapStartingZoom = globalParameterFacade.findByCode("map.zoom.start");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (mapStartingZoom == null || mapStartingZoom.length() == 0) {
                mapStartingZoom = "10";
            }
        }
        return mapStartingZoom;
    }

    public void setMapStartingZoom(String mapStartingZoom) {
        this.mapStartingZoom = mapStartingZoom;
    }

    public String getMapStartingType() {
        if (validateSync(mapStartingType)) {
            try {
                mapStartingType = globalParameterFacade.findByCode("map.type.start");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (mapStartingType == null || mapStartingType.length() == 0) {
                mapStartingType = "ROADMAP";
            }
        }
        return mapStartingType;
    }

    public void setMapStartingType(String mapStartingType) {
        this.mapStartingType = mapStartingType;
    }

    public String getMapStartingLat() {
        if (validateSync(mapStartingLat)) {
            try {
                mapStartingLat = globalParameterFacade.findByCode("map.lat.start");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (mapStartingLat == null || mapStartingLat.length() == 0) {
                mapStartingLat = "-25.32308546707471";
            }
        }
        return mapStartingLat;
    }

    public void setMapStartingLat(String mapStartingLat) {
        this.mapStartingLat = mapStartingLat;
    }

    public String getMapStartingLng() {
        if (validateSync(mapStartingLng)) {
            try {
                mapStartingLng = globalParameterFacade.findByCode("map.lng.start");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (mapStartingLng == null || mapStartingLng.length() == 0) {
                mapStartingLng = "-57.10501624279783";
            }
        }
        return mapStartingLng;
    }

    public void setMapStartingLng(String mapStartingLng) {
        this.mapStartingLng = mapStartingLng;
    }

    public LatLng getMapStartingPoint() {
        if (validateSync(mapStartingPoint)) {
            mapStartingPoint = new LatLng(Double.valueOf(getMapStartingLat()), Double.valueOf(getMapStartingLng()));
        }
        return mapStartingPoint;
    }

    public void setMapStartingPoint(LatLng mapStartingPoint) {
        this.mapStartingPoint = mapStartingPoint;
    }

    public String getFileUploadMaxFileSize() {
        if (validateSync(fileUploadMaxFileSize)) {
            try {
                fileUploadMaxFileSize = globalParameterFacade.findByCode("fileupload.filesize.max");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (fileUploadMaxFileSize == null
                || fileUploadMaxFileSize.length() == 0) {
                fileUploadMaxFileSize = "5120";
            }
        }
        return fileUploadMaxFileSize;
    }

    public void setFileUploadMaxFileSize(String fileUploadMaxFileSize) {
        this.fileUploadMaxFileSize = fileUploadMaxFileSize;
    }

    public String getSmscShortNumber() {
        if (validateSync(smscShortNumber)) {
            try {
                smscShortNumber = globalParameterFacade.findByCode("sms.shortnumber");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (smscShortNumber == null || smscShortNumber.length() == 0) {
                smscShortNumber = "22755";
            }
        }
        return smscShortNumber;
    }

    public void setSmscShortNumber(String smscShortNumber) {
        this.smscShortNumber = smscShortNumber;
    }

    public String getSmsCenterNumber() {
        if (validateSync(smsCenterNumber)) {
            try {
                smsCenterNumber = globalParameterFacade.findByCode("tracking.ota.smscenter.number");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (smsCenterNumber == null || smsCenterNumber.length() == 0) {
                smsCenterNumber = "+595981499003";
            }
        }
        return smsCenterNumber;
    }

    public void setSmsCenterNumber(String smsCenterNumber) {
        this.smsCenterNumber = smsCenterNumber;
    }

    public void forceInitialization() {
        defaultCrudPaginationPageSize = null;
        defaultServicePaginationPageSize = null;
        defaultTimeZoneID = null;
        defaultTimeZone = null;
        defaultOutputDateFormat = null;
        defaultInputDateFormat = null;
        defaultOutputDateTimeFormat = null;
        defaultInputDateTimeFormat = null;
        defaultOutputTimeFormat = null;
        defaultInputTimeFormat = null;
        defaultLocaleLanguage = null;
        defaultLocaleCountry = null;
        defaultLocale = null;
        mapCellSectorAreaDistance = null;
        mapCellSectorAreaAngle = null;
        mapCellSectorAreaBackgroundColor = null;
        mapCellSectorAreaBackgroundOpacity = null;
        mapCellOmniAreaRadius = null;
        mapPolylineColor = null;
        mapPolylineOpacity = null;
        mapPolylineWeight = null;
        mapMarkerBgColor = null;
        mapMarkerTextColor = null;
        mapStartingZoom = null;
        mapStartingType = null;
        mapStartingLat = null;
        mapStartingLng = null;
        mapStartingPoint = null;
        loginRetryLimit = null;
        fileUploadMaxFileSize = null;
        smsCenterNumber = null;
        appKey = null;
        mapMarkerBgColorSelected = null;
        mapMarkerTextColorSelected = null;
    }

    public boolean validateSync(Object prop) {
        return prop == null;
    }

    public Integer getColumnWidthChar() {
        if (validateSync(columnWidthChar)) {
            try {
                columnWidthChar = Integer.parseInt(globalParameterFacade.findByCode("format.columns.width.char"));
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }

        return columnWidthChar;
    }

    public void setColumnWidthChar(Integer columnWidthChar) {
        this.columnWidthChar = columnWidthChar;
    }
	
	public String getOtScheduleDateFormat() {
        if (validateSync(otScheduleDateFormat)) {
            try {
                otScheduleDateFormat = globalParameterFacade.findByCode("ot.schedule.dateformat");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (otScheduleDateFormat == null || otScheduleDateFormat.length() == 0) {
                otScheduleDateFormat = "dd/MM/yyyy HH:mm";
            }
        }
        return otScheduleDateFormat;
    }
	
	  public void setAppKey(String appKey) {
	        this.appKey = appKey;
	    }

	    public String getAppKey() {
	        if (appKey == null) {
	            try {
	                appKey = globalParameterFacade.findByCode("google.api.app.key");
	            } catch (GenericFacadeException ex) {
	                Logger.getLogger(ApplicationBean.class.getName()).log(
	                        Level.SEVERE, null, ex);
	            }
	        }
	        return appKey;
	    }
	    
	    public String getMapMarkerBgColorSelected() {
	        if (validateSync(mapMarkerBgColorSelected)) {
	            try {
	                mapMarkerBgColorSelected = globalParameterFacade.findByCode("map.marker.bg.color.selected");
	            } catch (GenericFacadeException ex) {
	                Logger.getLogger(ApplicationBean.class.getName()).log(
	                        Level.SEVERE, null, ex);
	            }
	            if (mapMarkerBgColorSelected == null || mapMarkerBgColorSelected.length() == 0) {
	                mapMarkerBgColorSelected = "#FFFF00";
	            }
	        }
	        return mapMarkerBgColorSelected;
	    }
	    
	    public String getMapMarkerTextColorSelected() {
	        if (validateSync(mapMarkerTextColorSelected)) {
	            try {
	                mapMarkerTextColorSelected = globalParameterFacade.findByCode("map.marker.text.color.selected");
	            } catch (GenericFacadeException ex) {
	                Logger.getLogger(ApplicationBean.class.getName()).log(
	                        Level.SEVERE, null, ex);
	            }
	            if (mapMarkerTextColorSelected == null || mapMarkerTextColorSelected.length() == 0) {
	                mapMarkerTextColorSelected = "#000000";
	            }
	        }
	        return mapMarkerTextColorSelected;
	    }
}
