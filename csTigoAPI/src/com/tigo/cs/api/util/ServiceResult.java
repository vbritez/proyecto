package com.tigo.cs.api.util;

import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;



public class ServiceResult{
    
    private String result;
    private ServiceValue serviceValue;
    private ServiceValueDetail serviceValueDetail;
    
    public ServiceResult() {
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public ServiceValue getServiceValue() {
        return serviceValue;
    }

    public void setServiceValue(ServiceValue serviceValue) {
        this.serviceValue = serviceValue;
    }

    public ServiceValueDetail getServiceValueDetail() {
        return serviceValueDetail;
    }

    public void setServiceValueDetail(ServiceValueDetail serviceValueDetail) {
        this.serviceValueDetail = serviceValueDetail;
    }   
}
