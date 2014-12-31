package com.tigo.cs.android.helper.domain;

public class ServiceEventEntity extends BaseEntity {

    private ServiceEntity service;
    private String serviceEventCod;
    private String messagePattern;
    private Boolean internet;
    private String wsPath;
    private Boolean forceInternet;
    private Boolean requiresLocation;
    private Boolean encryptMessage;
    private Boolean notifyMessage;
    private Boolean ignoreGpsDisabled;
    private Boolean jsonResponse;
    private Boolean forceGps;
    private Class activityToOpen;
    private Boolean requiresWait;
    private Boolean retry;
    private String gsonDatetimeFormat;

    public ServiceEventEntity() {
    }

    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public String getServiceEventCod() {
        return serviceEventCod;
    }

    public void setServiceEventCod(String serviceEventCod) {
        this.serviceEventCod = serviceEventCod;
    }

    public String getMessagePattern() {
        return messagePattern;
    }

    public void setMessagePattern(String messagePattern) {
        this.messagePattern = messagePattern;
    }

    public Boolean getInternet() {
        return internet;
    }

    public void setInternet(Boolean internet) {
        this.internet = internet;
    }

    public Boolean getForceInternet() {
        return forceInternet;
    }

    public void setForceInternet(Boolean forceInternet) {
        this.forceInternet = forceInternet;
    }

    public String getWsPath() {
        return wsPath;
    }

    public void setWsPath(String wsPath) {
        this.wsPath = wsPath;
    }

    public Boolean getRequiresLocation() {
        return requiresLocation;
    }

    public void setRequiresLocation(Boolean requiresLocation) {
        this.requiresLocation = requiresLocation;
    }

    public Boolean getEncryptMessage() {
        return encryptMessage;
    }

    public void setEncryptMessage(Boolean encryptMessage) {
        this.encryptMessage = encryptMessage;
    }

    public Boolean getNotifyMessage() {
        return notifyMessage;
    }

    public void setNotifyMessage(Boolean notifyMessage) {
        this.notifyMessage = notifyMessage;
    }

    public Boolean getIgnoreGpsDisabled() {
        return ignoreGpsDisabled;
    }

    public void setIgnoreGpsDisabled(Boolean ignoreGpsDisabled) {
        this.ignoreGpsDisabled = ignoreGpsDisabled;
    }

    public Boolean getJsonResponse() {
        return jsonResponse;
    }

    public void setJsonResponse(Boolean jsonResponse) {
        this.jsonResponse = jsonResponse;
    }

    public Boolean getForceGps() {
        return forceGps;
    }

    public void setForceGps(Boolean forceGps) {
        this.forceGps = forceGps;
    }

    public Class getActivityToOpen() {
        return activityToOpen;
    }

    public void setActivityToOpen(Class activityToOpen) {
        this.activityToOpen = activityToOpen;
    }

    public Boolean getRetry() {
        return retry;
    }

    public void setRetry(Boolean retry) {
        this.retry = retry;
    }

    public Boolean getRequiresWait() {
        return requiresWait;
    }

    public void setRequiresWait(Boolean requiresWait) {
        this.requiresWait = requiresWait;
    }

    public String getGsonDatetimeFormat() {
        return gsonDatetimeFormat;
    }

    public void setGsonDatetimeFormat(String gsonDatetimeFormat) {
        this.gsonDatetimeFormat = gsonDatetimeFormat;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
            + ((serviceEventCod == null) ? 0 : serviceEventCod.hashCode());
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
        ServiceEventEntity other = (ServiceEventEntity) obj;
        if (serviceEventCod == null) {
            if (other.serviceEventCod != null)
                return false;
        } else if (!serviceEventCod.equals(other.serviceEventCod))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
