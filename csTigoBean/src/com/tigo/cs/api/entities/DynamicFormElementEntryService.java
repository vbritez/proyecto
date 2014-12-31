package com.tigo.cs.api.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DynamicFormElementEntryService extends ServiceBean {

    private static final long serialVersionUID = -8386031720764086397L;

    private Long entryCod;
    private String description;
    private Boolean finalEntry;
    private Boolean multiSelectOption;
    private Integer orderNum;
    private String title;
    private Long ownerEntryCod;
    private Long entryTypeCod;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getEntryCod() {
        return entryCod;
    }

    public void setEntryCod(Long entryCod) {
        this.entryCod = entryCod;
    }

    public Long getOwnerEntryCod() {
        return ownerEntryCod;
    }

    public void setOwnerEntryCod(Long ownerEntryCod) {
        this.ownerEntryCod = ownerEntryCod;
    }

    public Long getEntryTypeCod() {
        return entryTypeCod;
    }

    public void setEntryTypeCod(Long entryTypeCod) {
        this.entryTypeCod = entryTypeCod;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
