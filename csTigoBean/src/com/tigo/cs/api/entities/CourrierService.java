package com.tigo.cs.api.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.tigo.cs.api.annotations.MetaColumn;
import com.tigo.cs.api.enumerates.MetaNames;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CourrierService extends BaseServiceBean {

    private static final long serialVersionUID = 2783589508654832924L;
    private String receiverName;
    private String observation;

    @MetaColumn(metaname = MetaNames.MOTIVE)
    private String motiveCode;

    private List<OrderDetailService> detail;

    public CourrierService() {
        super();
        setServiceCod(18);
    }

    public String getMotiveCode() {
        return motiveCode;
    }

    public void setMotiveCode(String motiveCode) {
        this.motiveCode = motiveCode;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
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
