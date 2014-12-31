package com.tigo.cs.api.entities;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SupervisorService extends BaseServiceBean implements Serializable {

    private static final long serialVersionUID = -1633264781252714261L;
    private Long userphoneCod;
    private String returnMessage;
    private Double latitudeNum;
    private Double longitudeNum;
    private String site;
    private Integer azimuth;
    private Boolean gps = false;

    public SupervisorService() {
        super();
        setServiceCod(25);
        setSendSMS(false);
        setEncryptResponse(true);
    }

    public Long getUserphoneCod() {
        return userphoneCod;
    }

    public void setUserphoneCod(Long userphoneCod) {
        this.userphoneCod = userphoneCod;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public Double getLatitudeNum() {
        return latitudeNum;
    }

    public void setLatitudeNum(Double latitudeNum) {
        this.latitudeNum = latitudeNum;
    }

    public Double getLongitudeNum() {
        return longitudeNum;
    }

    public void setLongitudeNum(Double longitudeNum) {
        this.longitudeNum = longitudeNum;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Integer getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(Integer azimuth) {
        this.azimuth = azimuth;
    }

    public Boolean getGps() {
        return gps;
    }

    public void setGps(Boolean gps) {
        this.gps = gps;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
