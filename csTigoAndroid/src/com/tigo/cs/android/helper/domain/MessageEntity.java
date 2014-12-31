package com.tigo.cs.android.helper.domain;

import java.util.Date;

import com.tigo.cs.api.entities.BaseServiceBean;

public class MessageEntity extends BaseEntity {

    private Integer channel;
    private String destinationNumber;
    private String destinationUrl;
    private Boolean incoming;
    private Date eventDate;
    private Integer state;
    private ServiceEntity service;
    private ServiceEventEntity serviceEvent;
    private String message;
    private String jsonData;
    private Class<? extends BaseServiceBean> entityClass;
    private Boolean forceInternet;

    public MessageEntity() {
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public String getDestinationNumber() {
        return destinationNumber;
    }

    public void setDestinationNumber(String destinationNumber) {
        this.destinationNumber = destinationNumber;
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }

    public void setDestinationUrl(String destinationUrl) {
        this.destinationUrl = destinationUrl;
    }

    public Boolean getIncoming() {
        return incoming;
    }

    public void setIncoming(Boolean incoming) {
        this.incoming = incoming;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public ServiceEventEntity getServiceEvent() {
        return serviceEvent;
    }

    public void setServiceEvent(ServiceEventEntity serviceEvent) {
        this.serviceEvent = serviceEvent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public Class<? extends BaseServiceBean> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<? extends BaseServiceBean> class1) {
        this.entityClass = class1;
    }

    public Boolean getForceInternet() {
        return forceInternet;
    }

    public void setForceInternet(Boolean forceInternet) {
        this.forceInternet = forceInternet;
    }

}
