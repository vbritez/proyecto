package com.tigo.cs.api.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.tigo.cs.api.annotations.MetaColumn;
import com.tigo.cs.api.enumerates.MetaNames;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VisitService extends BaseServiceBean {

    private static final long serialVersionUID = -7747919023011691375L;

    @MetaColumn(metaname = MetaNames.CLIENT)
    private String clientCode;

    @MetaColumn(metaname = MetaNames.MOTIVE)
    private String motiveCode;

    private String observation;

    public VisitService() {
        super();
        setServiceCod(1);
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getMotiveCode() {
        return motiveCode;
    }

    public void setMotiveCode(String motiveCode) {
        this.motiveCode = motiveCode;
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
