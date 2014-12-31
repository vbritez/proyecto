package com.tigo.cs.api.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.tigo.cs.api.annotations.MetaColumn;
import com.tigo.cs.api.enumerates.MetaNames;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ShiftGuardService extends BaseServiceBean {

    private static final long serialVersionUID = 4703740929428273043L;

    @MetaColumn(metaname = MetaNames.GUARD)
    private String guardCode;

    private String observation;

    public ShiftGuardService() {
        super();
        setServiceCod(15);
    }

    public String getGuardCode() {
        return guardCode;
    }

    public void setGuardCode(String guardCode) {
        this.guardCode = guardCode;
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
