package com.tigo.cs.android.helper.domain;

public class DynamicFormEntryTypeEntity extends BaseEntity {

    private Long dynamicFormEntryTypeCod;
    private String name;
    private String description;
    private Boolean persistible;

    public DynamicFormEntryTypeEntity() {

    }

    public Long getDynamicFormEntryTypeCod() {
        return dynamicFormEntryTypeCod;
    }

    public void setDynamicFormEntryTypeCod(Long dynamicFormEntryTypeCod) {
        this.dynamicFormEntryTypeCod = dynamicFormEntryTypeCod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getPersistible() {
        return persistible;
    }

    public void setPersistible(Boolean persistible) {
        this.persistible = persistible;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime
            * result
            + ((dynamicFormEntryTypeCod == null) ? 0 : dynamicFormEntryTypeCod.hashCode());
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
        DynamicFormEntryTypeEntity other = (DynamicFormEntryTypeEntity) obj;
        if (dynamicFormEntryTypeCod == null) {
            if (other.dynamicFormEntryTypeCod != null)
                return false;
        } else if (!dynamicFormEntryTypeCod.equals(other.dynamicFormEntryTypeCod))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
