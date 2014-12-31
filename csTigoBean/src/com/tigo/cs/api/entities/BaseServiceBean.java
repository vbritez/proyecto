package com.tigo.cs.api.entities;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.Gson;

@XmlRootElement
public class BaseServiceBean extends ServiceBean implements Serializable {

    private static final long serialVersionUID = 407458033452839680L;
    protected String event;
    protected String msisdn;
    protected Integer cellId;
    protected Integer lac;
    protected Boolean gpsEnabled;
    protected Double latitude;
    protected Double longitude;
    protected String hash;
    protected Long messageId;
    protected String grossMessage;
    protected Boolean android = false;
    protected String applicationKey;
    protected Boolean sendSMS = true;
    protected Boolean encryptResponse = false;
    protected Boolean ussd = false;
    protected Boolean requiresLocation = true;
    protected String prefix;
    protected String versionName;
    protected Float gpsAccuracy;
    protected String response;
    protected String imei;
    protected String imsi;

    protected Long timestamp;
    protected String deviceHour;
    protected Long arrivedEventDate;
    protected Long generedDate;

    protected String id;

    public BaseServiceBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Integer getCellId() {
        return cellId;
    }

    public void setCellId(Integer cellId) {
        this.cellId = cellId;
    }

    public Integer getLac() {
        return lac;
    }

    public void setLac(Integer lac) {
        this.lac = lac;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getGrossMessage() {
        return grossMessage;
    }

    public void setGrossMessage(String grossMessage) {
        this.grossMessage = grossMessage;
    }

    public Boolean getAndroid() {
        return android;
    }

    public void setAndroid(Boolean android) {
        this.android = android;
    }

    public String getApplicationKey() {
        return applicationKey;
    }

    public void setApplicationKey(String applicationKey) {
        this.applicationKey = applicationKey;
    }

    public Boolean getSendSMS() {
        return sendSMS;
    }

    public void setSendSMS(Boolean sendSMS) {
        this.sendSMS = sendSMS;
    }

    public Boolean getEncryptResponse() {
        return encryptResponse;
    }

    public void setEncryptResponse(Boolean encryptResponse) {
        this.encryptResponse = encryptResponse;
    }

    public Boolean getUssd() {
        return ussd;
    }

    public void setUssd(Boolean ussd) {
        this.ussd = ussd;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Boolean getRequiresLocation() {
        return requiresLocation;
    }

    public void setRequiresLocation(Boolean requiresLocation) {
        this.requiresLocation = requiresLocation;
    }

    public Boolean getGpsEnabled() {
        return gpsEnabled;
    }

    public void setGpsEnabled(Boolean gpsEnabled) {
        this.gpsEnabled = gpsEnabled;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Float getGpsAccuracy() {
        return gpsAccuracy;
    }

    public void setGpsAccuracy(Float gpsAccuracy) {
        this.gpsAccuracy = gpsAccuracy;
    }

    public String getDeviceHour() {
        return deviceHour;
    }

    public void setDeviceHour(String deviceHour) {
        this.deviceHour = deviceHour;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public Long getArrivedEventDate() {
        return arrivedEventDate;
    }

    public void setArrivedEventDate(Long arrivedEventDate) {
        this.arrivedEventDate = arrivedEventDate;
    }

    public Long getGeneredDate() {
        return generedDate;
    }

    public void setGeneredDate(Long generedDate) {
        this.generedDate = generedDate;
    }

    @Override
    public String toString() {
        String hashTmp = hash;
        String appKeyTmp = applicationKey;
        Boolean sendSMSTmp = sendSMS;
        Boolean encryptTMP = encryptResponse;
        this.applicationKey = null;
        this.hash = null;
        this.sendSMS = null;
        this.encryptResponse = null;
        String result = new Gson().toJson(this);
        this.hash = hashTmp;
        this.applicationKey = appKeyTmp;
        this.sendSMS = sendSMSTmp;
        this.encryptResponse = encryptTMP;
        return result;
    }

    public String toStringWithHash() {
        return new Gson().toJson(this);
    }

}
