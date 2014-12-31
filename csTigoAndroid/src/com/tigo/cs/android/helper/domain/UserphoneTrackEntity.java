package com.tigo.cs.android.helper.domain;

import java.util.Date;


public class UserphoneTrackEntity extends BaseEntity {

    private Double latitude;
    private Double longitude;
    private String returnMessage;
    private Long userphoneCod;
    private Boolean gps;
    private Date dateTime;

    public UserphoneTrackEntity() {
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

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public Long getUserphoneCod() {
        return userphoneCod;
    }

    public void setUserphoneCod(Long userphoneCod) {
        this.userphoneCod = userphoneCod;
    }

    public Boolean getGps() {
        return gps;
    }

    public void setGps(Boolean gps) {
        this.gps = gps;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
}
