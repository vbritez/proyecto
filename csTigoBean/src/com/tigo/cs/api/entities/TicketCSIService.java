package com.tigo.cs.api.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TicketCSIService extends BaseServiceBean {

    private static final long serialVersionUID = 1350487075362615168L;
    private String ticketArea;
    private String ticketCategory;
    private String description;
    private String ticketCode;

    public TicketCSIService() {
        super();
        setServiceCod(19);
    }

    public String getTicketArea() {
        return ticketArea;
    }

    public void setTicketArea(String ticketArea) {
        this.ticketArea = ticketArea;
    }

    public String getTicketCategory() {
        return ticketCategory;
    }

    public void setTicketCategory(String ticketCategory) {
        this.ticketCategory = ticketCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
