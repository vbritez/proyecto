package com.tigo.cs.api.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceOperationService extends BaseServiceBean {

    private static final long serialVersionUID = 9052993296899198725L;
    private Long serviceToOperate;
    private Integer position;
    public String responseListClassName;
    public List<String> responseList;

    public ServiceOperationService() {
        super();
        setServiceCod(101);
        setEncryptResponse(true);
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

    public String getResponseListClassName() {
        return responseListClassName;
    }

    public void setResponseListClassName(String responseListClassName) {
        this.responseListClassName = responseListClassName;
    }

    public List<String> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<String> responseList) {
        this.responseList = responseList;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
