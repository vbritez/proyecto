package com.tigo.cs.api.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PermissionService extends BaseServiceBean {

    private static final long serialVersionUID = -6204378183197554027L;

    public String responseListClassName;

    public List<String> responseList;

    public PermissionService() {
        super();
        setServiceCod(0);
        setEncryptResponse(true);
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
