package com.tigo.cs.api.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.tigo.cs.api.annotations.MetaColumn;
import com.tigo.cs.api.enumerates.MetaNames;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DeliveryService extends BaseServiceBean {

    private static final long serialVersionUID = 7623663251025796753L;
    @MetaColumn(metaname = MetaNames.CLIENT)
    private String clientCode;
    private String observation;
    private List<DeliveryDetailService> details;

    public DeliveryService() {
        super();
        setServiceCod(7);
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public List<DeliveryDetailService> getDetails() {
        return details;
    }

    public void setDetails(List<DeliveryDetailService> details) {
        this.details = details;
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
