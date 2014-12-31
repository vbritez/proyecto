package com.tigo.cs.android.helper.domain;

/**
 * Metodo que encapsula a un SERVICIO dentro de la plataforma de soluciones
 * corporativas, debe tener relacion con los datos de la plataforma
 * 
 * @author Miguel Maciel
 * 
 */
public class ServiceEntity extends BaseEntity {

    private Integer servicecod;
    private String servicename;
    private Class className;
    private Boolean platformService;
    private Boolean enabled;
    private Boolean query;
    private Boolean canDelete;
    private Class serviceClass;

    public ServiceEntity() {

    }

    public Integer getServicecod() {
        return servicecod;
    }

    public void setServicecod(Integer servicecod) {
        this.servicecod = servicecod;
    }

    public String getServicename() {
        return servicename;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public Class getClassName() {
        return className;
    }

    public void setClassName(Class className) {
        this.className = className;
    }

    public Boolean getPlatformService() {
        return platformService;
    }

    public void setPlatformService(Boolean platformService) {
        this.platformService = platformService;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getQuery() {
        return query;
    }

    public void setQuery(Boolean query) {
        this.query = query;
    }

    public Boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(Boolean canDelete) {
        this.canDelete = canDelete;
    }

    public Class getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(Class serviceClass) {
        this.serviceClass = serviceClass;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
            + ((servicecod == null) ? 0 : servicecod.hashCode());
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
        ServiceEntity other = (ServiceEntity) obj;
        if (servicecod == null) {
            if (other.servicecod != null)
                return false;
        } else if (!servicecod.equals(other.servicecod))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
