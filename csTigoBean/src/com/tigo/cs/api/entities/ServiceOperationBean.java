package com.tigo.cs.api.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceOperationBean extends BaseServiceBean {

    private static final long serialVersionUID = 9052993296899198725L;
    private Long serviceToOperate;
    private Integer position;

    public ServiceOperationBean() {
        super();
        setServiceCod(101);
    }

    public Long getServiceToOperate() {
        return serviceToOperate;
    }

    public void setServiceToOperate(Long serviceToOperate) {
        this.serviceToOperate = serviceToOperate;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}