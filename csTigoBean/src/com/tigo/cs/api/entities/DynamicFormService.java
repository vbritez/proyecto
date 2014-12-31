package com.tigo.cs.api.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DynamicFormService extends BaseServiceBean {

    private static final long serialVersionUID = -2976883590425810638L;
    private List<DynamicFormElementService> elementBean;
    private DynamicFormValueService valueBean;
    private Long dynamicFormCod;

    public DynamicFormService() {
        super();
        setServiceCod(20);
    }

    public Long getDynamicFormCod() {
        return dynamicFormCod;
    }

    public void setDynamicFormCod(Long dynamicFormCod) {
        this.dynamicFormCod = dynamicFormCod;
    }

    public List<DynamicFormElementService> getElementBean() {
        return elementBean;
    }

    public void setElementBean(List<DynamicFormElementService> elementBean) {
        this.elementBean = elementBean;
    }

    public DynamicFormValueService getValueBean() {
        return valueBean;
    }

    public void setValueBean(DynamicFormValueService valueBean) {
        this.valueBean = valueBean;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
