package com.tigo.cs.api.entities;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrackingService extends BaseServiceBean implements Serializable {

    private static final long serialVersionUID = -1633264781252714261L;
    private String date;

    public TrackingService() {
        super();
        setServiceCod(4);
        setSendSMS(false);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
