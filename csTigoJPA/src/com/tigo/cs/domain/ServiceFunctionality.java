package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.PrimarySortedField;

@Entity
@Table(name = "service_functionality")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(name = "ServiceFunctionality.findAll",
        query = "SELECT s FROM ServiceFunctionality s") })
public class ServiceFunctionality implements Serializable {

    private static final long serialVersionUID = -7487680727560338256L;
    @EmbeddedId
    @PrimarySortedField
    protected ServiceFunctionalityPK serviceFunctionalityPK;
    @JoinColumn(name = "cod_service", referencedColumnName = "service_cod",
            nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Service service;
    @JoinColumn(name = "cod_functionality",
            referencedColumnName = "functionality_cod", nullable = false,
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Functionality functionality;
    @OneToMany(mappedBy = "serviceFunctionality")
    private List<ClientServiceFunctionality> clientServiceFunctionalityList;

    public ServiceFunctionality() {
    }

    public ServiceFunctionality(ServiceFunctionalityPK serviceFunctionalityPK) {
        this.serviceFunctionalityPK = serviceFunctionalityPK;
    }

    public ServiceFunctionality(long codService, long codFunctionality) {
        this.serviceFunctionalityPK = new ServiceFunctionalityPK(codService, codFunctionality);
    }

    public ServiceFunctionality(Service service, Functionality functionality) {
        this.serviceFunctionalityPK = new ServiceFunctionalityPK(service.getServiceCod(), functionality.getFunctionalityCod());
        this.setService(service);
        this.setFunctionality(functionality);
    }

    public ServiceFunctionalityPK getServiceFunctionalityPK() {
        return serviceFunctionalityPK;
    }

    public void setServiceFunctionalityPK(ServiceFunctionalityPK serviceFunctionalityPK) {
        this.serviceFunctionalityPK = serviceFunctionalityPK;
    }

    public Service getService() {
        return service;
    }

    public final void setService(Service service) {
        this.service = service;
    }

    public Functionality getFunctionality() {
        return functionality;
    }

    public final void setFunctionality(Functionality functionality) {
        this.functionality = functionality;
    }

    public List<ClientServiceFunctionality> getClientServiceFunctionalityList() {
        return clientServiceFunctionalityList;
    }

    public void setClientServiceFunctionalityList(List<ClientServiceFunctionality> clientServiceFunctionalityList) {
        this.clientServiceFunctionalityList = clientServiceFunctionalityList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ServiceFunctionality other = (ServiceFunctionality) obj;
        if (this.serviceFunctionalityPK != other.serviceFunctionalityPK
            && (this.serviceFunctionalityPK == null || !this.serviceFunctionalityPK.equals(other.serviceFunctionalityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97
            * hash
            + (this.serviceFunctionalityPK != null ? this.serviceFunctionalityPK.hashCode() : 0);
        hash = 97 * hash + (this.service != null ? this.service.hashCode() : 0);
        hash = 97 * hash
            + (this.functionality != null ? this.functionality.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "ServiceFunctionality[serviceFunctionalityPK="
            + serviceFunctionalityPK + "]";
    }
}
