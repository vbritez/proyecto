package com.tigo.cs.api.entities;

import java.io.Serializable;

public class ServiceBean implements Serializable {

    private static final long serialVersionUID = 3169770432569912817L;
    private Integer serviceCod;

    public ServiceBean() {
    }

    public ServiceBean(Integer serviceCod) {
        this.serviceCod = serviceCod;
    }

    public Integer getServiceCod() {
        return serviceCod;
    }

    public void setServiceCod(Integer serviceCod) {
        this.serviceCod = serviceCod;
    }

}
