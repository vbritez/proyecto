package com.tigo.cs.android.helper;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.helper.domain.GlobalParameterEntity;

public class GlobalParameterHelper extends AbstractHelper<GlobalParameterEntity> {

    private String shortNumber;
    private String dateTimePattern;
    private Boolean serviceShowDisabled;
    private Boolean platformVibrate;
    private Integer gpsAccuracyMeters;
    private Integer gpsAccuracyRangeMeters;
    private Integer gpsMaxAccuracyMeters;
    private Integer gpsTimeout;
    private Integer gpsTimeDimension;
    private Integer smsShowCharLimit;
    private Boolean smsShowAllHistory;
    private Boolean deviceEnabled;
    private String deviceCellphoneNum;
    private Long trackingTimeout;
    private Boolean persistentTracking;
    private Long persistentTrackingInterval;
    private Long persistentUpdateConfigurationInterval;
    private Long persistentResendSmsInterval;
    private Long locatorInterval;
    private Boolean internetEnabled;
    private String internetApnName;
    private String internetApnValue;
    private Integer internetApnDefaultId;
    private Integer internetApnCstigoId;
    private String internetWsHost;
    private Integer internetWsPort;
    private Integer internetWsPortSSL;
    private Integer smsTextLength;
    private Integer collectionMaxDetail;
    /*
     * Rest WS Section
     */
    private Integer restConfigurationServiceTimeoutConnection;
    private Integer restConfigurationServiceTimeoutSocket;
    private Integer restConfigurationVerifyTimeoutConnection;
    private Integer restConfigurationVerifyTimeoutSocket;

    public Integer getRestConfigurationServiceTimeoutConnection() {
        if (restConfigurationServiceTimeoutConnection == null) {
            restConfigurationServiceTimeoutConnection = Integer.parseInt(findByParameterCode(
                    "rest.configuration.service.timeout.connection").getParameterValue());
        }
        return restConfigurationServiceTimeoutConnection;
    }

    public Integer getRestConfigurationServiceTimeoutSocket() {
        if (restConfigurationServiceTimeoutSocket == null) {
            restConfigurationServiceTimeoutSocket = Integer.parseInt(findByParameterCode(
                    "rest.configuration.service.timeout.socket").getParameterValue());
        }
        return restConfigurationServiceTimeoutSocket;
    }

    public Integer getRestConfigurationVerifyTimeoutConnection() {
        if (restConfigurationVerifyTimeoutConnection == null) {
            restConfigurationVerifyTimeoutConnection = Integer.parseInt(findByParameterCode(
                    "rest.configuration.verify.timeout.connection").getParameterValue());
        }
        return restConfigurationVerifyTimeoutConnection;
    }

    public Integer getRestConfigurationVerifyTimeoutSocket() {
        if (restConfigurationVerifyTimeoutSocket == null) {
            restConfigurationVerifyTimeoutSocket = Integer.parseInt(findByParameterCode(
                    "rest.configuration.verify.timeout.socket").getParameterValue());
        }
        return restConfigurationVerifyTimeoutSocket;
    }

    /*
     * END Rest WS Section
     */

    public GlobalParameterHelper(Context context) {
        super(context, "global_parameter");
    }

    public GlobalParameterEntity findByParameterCode(String parameterCode) {
        return executeQuery("code = ?", new String[] { parameterCode });
    }

    @Override
    protected ContentValues getContentValues(GlobalParameterEntity globalParameter) {
        ContentValues values = new ContentValues();
        values.put("_id", globalParameter.getId());
        values.put("code", globalParameter.getCode());
        values.put("description", globalParameter.getDescription());
        values.put("parameter_value", globalParameter.getParameterValue());
        values.put(
                "date_time",
                globalParameter.getDateTime() != null ? globalParameter.getDateTime().getTime() : null);
        return values;
    }

    @Override
    protected GlobalParameterEntity cursorToEntity(Cursor cursor) {
        GlobalParameterEntity globalParameter = new GlobalParameterEntity();
        globalParameter.setId(cursor.getLong(0));
        globalParameter.setCode(cursor.getString(1));
        globalParameter.setDescription(cursor.getString(2));
        globalParameter.setParameterValue(cursor.getString(3));
        globalParameter.setDateTime(new Date(cursor.getLong(4)));
        return globalParameter;
    }

    public String getShortNumber() throws Exception {
        if (shortNumber == null) {
            shortNumber = CsTigoApplication.getOperationHelper().findOperationData().getShortNumber();
        }
        return shortNumber;
    }

    public String getDateTimePattern() {
        if (dateTimePattern == null) {
            dateTimePattern = findByParameterCode("pattern.datetime").getParameterValue();
        }
        return dateTimePattern;
    }

    public Boolean getServiceShowDisabled() {
        serviceShowDisabled = Integer.parseInt(findByParameterCode(
                "service.show_disabled").getParameterValue()) == 1 ? true : false;

        return serviceShowDisabled;
    }

    public GlobalParameterEntity getServiceShowDisabledEntity() {
        return findByParameterCode("service.show_disabled");
    }

    public Boolean getPlatformVibrate() {
        platformVibrate = Integer.parseInt(findByParameterCode(
                "platform.vibrate").getParameterValue()) == 1 ? true : false;
        return platformVibrate;
    }

    public GlobalParameterEntity getPlatformVibrateEntity() {
        return findByParameterCode("platform.vibrate");
    }

    public Integer getGpsAccuracyMeters() {
        if (gpsAccuracyMeters == null) {
            gpsAccuracyMeters = Integer.parseInt(findByParameterCode(
                    "gps.accuracy_meters").getParameterValue());
        }
        return gpsAccuracyMeters;
    }

    public Integer getGpsAccuracyRangeMeters() {
        if (gpsAccuracyRangeMeters == null) {
            gpsAccuracyRangeMeters = Integer.parseInt(findByParameterCode(
                    "gps.range_accuracy_meters").getParameterValue());
        }
        return gpsAccuracyRangeMeters;
    }

    public Integer getGpsMaxAccuracyMeters() {
        if (gpsMaxAccuracyMeters == null) {
            gpsMaxAccuracyMeters = Integer.parseInt(findByParameterCode(
                    "gps.max_accuracy_meters").getParameterValue());
        }
        return gpsMaxAccuracyMeters;
    }

    public Integer getGpsTimeout() {
        if (gpsTimeout == null) {
            gpsTimeout = Integer.parseInt(findByParameterCode("gps.timeout").getParameterValue());
        }
        return gpsTimeout;
    }

    public Integer getGpsTimeDimension() {
        if (gpsTimeDimension == null) {
            gpsTimeDimension = Integer.parseInt(findByParameterCode(
                    "gps.time_dimension").getParameterValue());
        }
        return gpsTimeDimension;
    }

    public Integer getSmsShowCharLimit() {
        if (smsShowCharLimit == null) {
            smsShowCharLimit = Integer.parseInt(findByParameterCode(
                    "sms.show_char_limit").getParameterValue());
        }
        return smsShowCharLimit;
    }

    public Boolean getSmsShowAllHistory() {
        smsShowAllHistory = Integer.parseInt(findByParameterCode(
                "sms.show_all_history").getParameterValue()) == 1 ? true : false;
        return smsShowAllHistory;
    }

    public GlobalParameterEntity getSmsShowAllHistoryEntity() {
        return findByParameterCode("sms.show_all_history");
    }

    public Boolean getDeviceEnabled() {
        deviceEnabled = Integer.parseInt(findByParameterCode("device.enabled").getParameterValue()) == 1 ? true : false;
        return deviceEnabled;
    }

    public GlobalParameterEntity getDeviceEnabledEntity() {
        return findByParameterCode("device.enabled");
    }

    public String getDeviceCellphoneNum() {
        if (deviceCellphoneNum == null
            || (deviceCellphoneNum != null && TextUtils.isEmpty(deviceCellphoneNum))) {
            deviceCellphoneNum = findByParameterCode("device.cellphonenum").getParameterValue();
        }
        return deviceCellphoneNum;
    }

    public GlobalParameterEntity getDeviceCellphoneNumEntity() {
        return findByParameterCode("device.cellphonenum");
    }

    public Long getTrackingTimeout() {
        if (trackingTimeout == null) {
            trackingTimeout = Long.parseLong(findByParameterCode(
                    "tracking.timeout").getParameterValue());
        }
        return trackingTimeout;
    }

    public Boolean getPersistentTracking() {
        persistentTracking = Integer.parseInt(findByParameterCode(
                "tracking.persistent").getParameterValue()) == 1 ? true : false;
        return persistentTracking;
    }

    public Long getPersistentTrackingInterval() {
        persistentTrackingInterval = Long.parseLong(findByParameterCode(
                "tracking.persistent.interval").getParameterValue());

        return persistentTrackingInterval;
    }

    public Long getPersistentUpdateConfigurationInterval() {
        persistentUpdateConfigurationInterval = Long.parseLong(findByParameterCode(
                "configuration.persistent.update.interval").getParameterValue());

        return persistentUpdateConfigurationInterval;
    }

    public Long getPersistentResendSmsInterval() {
        persistentResendSmsInterval = Long.parseLong(findByParameterCode(
                "configuration.persistent.resendsms.interval").getParameterValue());
        return persistentResendSmsInterval;
    }

    public Boolean getInternetEnabled() {
        internetEnabled = Integer.parseInt(getInternetEnabledEntity().getParameterValue()) == 1 ? true : false;
        return internetEnabled;
    }

    public GlobalParameterEntity getInternetEnabledEntity() {
        return findByParameterCode("internet.enabled");
    }

    public String getInternetApnName() {
        internetApnName = getInternetApnNameEntity().getParameterValue();
        return internetApnName;
    }

    public GlobalParameterEntity getInternetApnNameEntity() {
        return findByParameterCode("internet.apn.name");
    }

    public String getInternetApnValue() {
        internetApnValue = getInternetApnValueEntity().getParameterValue();
        return internetApnValue;
    }

    public GlobalParameterEntity getInternetApnValueEntity() {
        return findByParameterCode("internet.apn.value");
    }

    public String getInternetWsHost() throws Exception {
        if (internetWsHost == null) {
            internetWsHost = CsTigoApplication.getOperationHelper().findOperationData().getRestWsLocation();
        }
        return internetWsHost;
    }

    public Integer getInternetApnDefaultId() {
        internetApnDefaultId = Integer.parseInt(getInternetApnDefaultIdEntity().getParameterValue());
        return internetApnDefaultId;
    }

    public GlobalParameterEntity getInternetApnDefaultIdEntity() {
        return findByParameterCode("internet.apn.default");
    }

    public Integer getInternetApnCstigoId() {
        internetApnCstigoId = Integer.parseInt(getInternetApnCstigoIdEntity().getParameterValue());
        return internetApnCstigoId;
    }

    public Integer getInternetWsPort() {
        if (internetWsPort == null) {
            internetWsPort = Integer.parseInt(findByParameterCode(
                    "internet.ws.port").getParameterValue());
        }
        return internetWsPort;
    }

    public Integer getInternetWsPortSSL() {
        if (internetWsPortSSL == null) {
            internetWsPortSSL = Integer.parseInt(findByParameterCode(
                    "internet.ws.port.ssl").getParameterValue());
        }
        return internetWsPortSSL;
    }

    public GlobalParameterEntity getInternetApnCstigoIdEntity() {
        return findByParameterCode("internet.apn.cstigo");
    }

    public Boolean getOrderPricelpha() {
        return Integer.parseInt(getOrderPricelphaEntity().getParameterValue()) == 1 ? true : false;
    }

    public GlobalParameterEntity getOrderPricelphaEntity() {
        return findByParameterCode("service.order.alpha.price");
    }

    public Boolean getCiAlphaNumeric() {
        return Integer.parseInt(getCiAlphaNumericEntity().getParameterValue()) == 1 ? true : false;
    }

    public GlobalParameterEntity getCiAlphaNumericEntity() {
        return findByParameterCode("service.tigomoney.ci.alphanumeric");
    }

    public Boolean getInternetApnChange() {
        return Integer.parseInt(getInternetApnChangeEntity().getParameterValue()) == 1 ? true : false;
    }

    public GlobalParameterEntity getInternetApnChangeEntity() {
        return findByParameterCode("internet.apn.change");
    }

    public Integer getSmsTextLength() {
        if (smsTextLength == null) {
            smsTextLength = Integer.parseInt(findByParameterCode(
                    "sms.text.length").getParameterValue());
        }
        return smsTextLength;
    }

    public Integer getCollectionMaxDetail() {
        if (collectionMaxDetail == null) {
            collectionMaxDetail = Integer.parseInt(findByParameterCode(
                    "service.collection.max_detail_number").getParameterValue());
        }
        return collectionMaxDetail;
    }

    public GlobalParameterEntity getTerportManagerEntity() {
        return findByParameterCode("service.terport.login.manager");
    }

    public Long getLocatorInterval() {
        locatorInterval = Long.parseLong(findByParameterCode(
                "configuration.persistent.locator.interval").getParameterValue());
        return locatorInterval;

    }

}
