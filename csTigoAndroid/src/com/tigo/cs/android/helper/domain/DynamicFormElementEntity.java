package com.tigo.cs.android.helper.domain;

import java.util.Date;

public class DynamicFormElementEntity extends BaseEntity {

    private Long dynamicFormElementCod;
    private String description;
    private Date startDate;
    private Date finishDate;

    public DynamicFormElementEntity() {

    }

    public Long getDynamicFormElementCod() {
        return dynamicFormElementCod;
    }

    public void setDynamicFormElementCod(Long dynamicFormElementCod) {
        this.dynamicFormElementCod = dynamicFormElementCod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime
            * result
            + ((dynamicFormElementCod == null) ? 0 : dynamicFormElementCod.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        DynamicFormElementEntity other = (DynamicFormElementEntity) obj;
        if (dynamicFormElementCod == null) {
            if (other.dynamicFormElementCod != null)
                return false;
        } else if (!dynamicFormElementCod.equals(other.dynamicFormElementCod))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
