package com.tigo.cs.api.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DynamicFormValueService extends BaseServiceBean {

    private static final long serialVersionUID = -7759917380437384397L;

    private Long codDynamicFormElementBean;
    private Long coduserphone;
    private Long codPhone;
    private List<DynamicFormValueDataService> entryList;


    public Long getCodDynamicFormElementBean() {
        return codDynamicFormElementBean;
    }

    public void setCodDynamicFormElementBean(Long codDynamicFormElementBean) {
        this.codDynamicFormElementBean = codDynamicFormElementBean;
    }

    public Long getCoduserphone() {
        return coduserphone;
    }

    public void setCoduserphone(Long coduserphone) {
        this.coduserphone = coduserphone;
    }

    public Long getCodPhone() {
        return codPhone;
    }

    public void setCodPhone(Long codPhone) {
        this.codPhone = codPhone;
    }

    public List<DynamicFormValueDataService> getEntryList() {
        return entryList;
    }

    public void setEntryList(List<DynamicFormValueDataService> entryList) {
        this.entryList = entryList;
    }


    @Override
    public String toString() {
        return super.toString();
    }
}
