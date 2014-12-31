package com.tigo.cs.api.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.tigo.cs.api.annotations.MetaColumn;
import com.tigo.cs.api.enumerates.MetaNames;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FleetService extends BaseServiceBean {

    private static final long serialVersionUID = 5883394065964884369L;
    @MetaColumn(metaname = MetaNames.EMPLOYEE)
    private String driverCode;
    @MetaColumn(metaname = MetaNames.VECHICLE)
    private String vehicleCode;
    @MetaColumn(metaname = MetaNames.CLIENT)
    private String clientCode;
    private String initialKm;
    private String finalKm;
    private String traveledKm;
    private String observation;

    public FleetService() {
        super();
        setServiceCod(12);
    }

    public String getDriverCode() {
        return driverCode;
    }

    public void setDriverCode(String driverCode) {
        this.driverCode = driverCode;
    }

    public String getVehicleCode() {
        return vehicleCode;
    }

    public void setVehicleCode(String vehicleCode) {
        this.vehicleCode = vehicleCode;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getInitialKm() {
        return initialKm;
    }

    public void setInitialKm(String initialKm) {
        this.initialKm = initialKm;
    }

    public String getFinalKm() {
        return finalKm;
    }

    public void setFinalKm(String finalKm) {
        this.finalKm = finalKm;
    }

    public String getTraveledKm() {
        return traveledKm;
    }

    public void setTraveledKm(String traveledKm) {
        this.traveledKm = traveledKm;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
