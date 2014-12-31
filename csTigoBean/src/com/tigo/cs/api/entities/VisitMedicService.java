package com.tigo.cs.api.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.tigo.cs.api.annotations.MetaColumn;
import com.tigo.cs.api.enumerates.MetaNames;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VisitMedicService extends BaseServiceBean {

    private static final long serialVersionUID = -8572565401259571743L;

    @MetaColumn(metaname = MetaNames.CLINIC)
    private String placeCode;

    @MetaColumn(metaname = MetaNames.MEDIC)
    private String medicCode;

    @MetaColumn(metaname = MetaNames.MOTIVE)
    private String motiveCode;

    private String initialKm;
    private String observation;
    private String nextVisit;
    private String notificate;
    private String notificationDesc;
    private List<OrderDetailService> detail;

    public VisitMedicService() {
        super();
        setServiceCod(17);
    }

    public String getPlaceCode() {
        return placeCode;
    }

    public void setPlaceCode(String placeCode) {
        this.placeCode = placeCode;
    }

    public String getMedicCode() {
        return medicCode;
    }

    public void setMedicCode(String medicCode) {
        this.medicCode = medicCode;
    }

    public String getMotiveCode() {
        return motiveCode;
    }

    public void setMotiveCode(String motiveCode) {
        this.motiveCode = motiveCode;
    }

    public String getInitialKm() {
        return initialKm;
    }

    public void setInitialKm(String initialKm) {
        this.initialKm = initialKm;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getNextVisit() {
        return nextVisit;
    }

    public void setNextVisit(String nextVisit) {
        this.nextVisit = nextVisit;
    }

    public String getNotificate() {
        return notificate;
    }

    public void setNotificate(String notificate) {
        this.notificate = notificate;
    }

    public String getNotificationDesc() {
        return notificationDesc;
    }

    public void setNotificationDesc(String notificationDesc) {
        this.notificationDesc = notificationDesc;
    }

    public List<OrderDetailService> getDetail() {
        return detail;
    }

    public void setDetail(List<OrderDetailService> detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
