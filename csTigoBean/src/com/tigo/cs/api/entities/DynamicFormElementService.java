package com.tigo.cs.api.entities;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DynamicFormElementService extends ServiceBean {

    private static final long serialVersionUID = -7759917380437384397L;

    private Long dynamicFormElementCod;
    private String description;
    private Date startDate;
    private Date finishDate;
    private List<DynamicFormElementEntryService> entryList;

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

    public List<DynamicFormElementEntryService> getEntryList() {
        return entryList;
    }

    public void setEntryList(List<DynamicFormElementEntryService> entryList) {
        this.entryList = entryList;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
