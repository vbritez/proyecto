package com.tigo.cs.api.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DynamicFormValueDataService extends ServiceBean {

    private static final long serialVersionUID = -8386031720764086397L;

    private Long codFeatureElementEntryBean;
    private String data;

    public Long getCodFeatureElementEntryBean() {
        return codFeatureElementEntryBean;
    }

    public void setCodFeatureElementEntryBean(Long codFeatureElementEntryBean) {
        this.codFeatureElementEntryBean = codFeatureElementEntryBean;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
            * result
            + ((codFeatureElementEntryBean == null) ? 0 : codFeatureElementEntryBean.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DynamicFormValueDataService other = (DynamicFormValueDataService) obj;
        if (codFeatureElementEntryBean == null) {
            if (other.codFeatureElementEntryBean != null)
                return false;
        } else if (!codFeatureElementEntryBean.equals(other.codFeatureElementEntryBean))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
