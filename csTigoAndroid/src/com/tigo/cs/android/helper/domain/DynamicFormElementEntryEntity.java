package com.tigo.cs.android.helper.domain;

public class DynamicFormElementEntryEntity extends BaseEntity {

    private Long dynamicFormElementEntryCod;
    private DynamicFormElementEntity dynamicFormElement;
    private String title;
    private String description;
    private Boolean finalEntry;
    private Boolean multiSelectOption;
    private Integer orderNum;
    private DynamicFormElementEntryEntity ownerEntry;
    private DynamicFormEntryTypeEntity entryType;

    public DynamicFormElementEntryEntity() {

    }

    public Long getDynamicFormElementEntryCod() {
        return dynamicFormElementEntryCod;
    }

    public void setDynamicFormElementEntryCod(Long dynamicFormElementEntryCod) {
        this.dynamicFormElementEntryCod = dynamicFormElementEntryCod;
    }

    public DynamicFormElementEntity getDynamicFormElement() {
        return dynamicFormElement;
    }

    public void setDynamicFormElement(DynamicFormElementEntity dynamicFormElement) {
        this.dynamicFormElement = dynamicFormElement;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getFinalEntry() {
        return finalEntry;
    }

    public void setFinalEntry(Boolean finalEntry) {
        this.finalEntry = finalEntry;
    }

    public Boolean getMultiSelectOption() {
        return multiSelectOption;
    }

    public void setMultiSelectOption(Boolean multiSelectOption) {
        this.multiSelectOption = multiSelectOption;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public DynamicFormElementEntryEntity getOwnerEntry() {
        return ownerEntry;
    }

    public void setOwnerEntry(DynamicFormElementEntryEntity ownerEntry) {
        this.ownerEntry = ownerEntry;
    }

    public DynamicFormEntryTypeEntity getEntryType() {
        return entryType;
    }

    public void setEntryType(DynamicFormEntryTypeEntity entryType) {
        this.entryType = entryType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime
            * result
            + ((dynamicFormElementEntryCod == null) ? 0 : dynamicFormElementEntryCod.hashCode());
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
        DynamicFormElementEntryEntity other = (DynamicFormElementEntryEntity) obj;
        if (dynamicFormElementEntryCod == null) {
            if (other.dynamicFormElementEntryCod != null)
                return false;
        } else if (!dynamicFormElementEntryCod.equals(other.dynamicFormElementEntryCod))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
