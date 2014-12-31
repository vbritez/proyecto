package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "client_service_functionality")
@NamedQueries({ @NamedQuery(name = "ClientServiceFunctionality.findAll",
        query = "SELECT c FROM ClientServiceFunctionality c") })
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class ClientServiceFunctionality implements Serializable {

    private static final long serialVersionUID = 4652268726699005599L;
    @EmbeddedId
    protected ClientServiceFunctionalityPK clientServiceFunctionalityPK;
    // bi-directional many-to-many association to Userphone
    @ManyToMany(mappedBy = "clientServiceFunctionalityList")
    private List<Userphone> userphoneList;
    @ManyToMany(mappedBy = "clientServiceFunctionalityList")
    private List<Userweb> userwebList;

    @JoinColumns({ @JoinColumn(name = "cod_service",
            referencedColumnName = "cod_service", insertable = false,
            updatable = false), @JoinColumn(name = "cod_functionality",
            referencedColumnName = "cod_functionality", nullable = false,
            insertable = false, updatable = false) })
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private ServiceFunctionality serviceFunctionality;

    @JoinColumn(name = "cod_client", referencedColumnName = "client_cod",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Client client;
    @ManyToMany(mappedBy = "clientServiceFunctionalityList")
    private List<RoleClient> roleClientList;

    public ClientServiceFunctionality() {
    }

    public ClientServiceFunctionality(
            ClientServiceFunctionalityPK clientServiceFunctionalityPK) {
        this.clientServiceFunctionalityPK = clientServiceFunctionalityPK;
    }

    public ClientServiceFunctionality(long codClient, long codService,
            long codFunctionality) {
        this.clientServiceFunctionalityPK = new ClientServiceFunctionalityPK(codClient, codService, codFunctionality);
    }

    public ClientServiceFunctionalityPK getClientServiceFunctionalityPK() {
        return clientServiceFunctionalityPK;
    }

    public void setClientServiceFunctionalityPK(ClientServiceFunctionalityPK clientServiceFunctionalityPK) {
        this.clientServiceFunctionalityPK = clientServiceFunctionalityPK;
    }

    public List<Userphone> getUserphoneList() {
        return userphoneList;
    }

    public void setUserphoneList(List<Userphone> userphoneList) {
        this.userphoneList = userphoneList;
    }

    public List<Userweb> getUserwebList() {
        return userwebList;
    }

    public void setUserwebList(List<Userweb> userwebList) {
        this.userwebList = userwebList;
    }

    public ServiceFunctionality getServiceFunctionality() {
        return serviceFunctionality;
    }

    public void setServiceFunctionality(ServiceFunctionality serviceFunctionality) {
        this.serviceFunctionality = serviceFunctionality;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<RoleClient> getRoleClientList() {
        return roleClientList;
    }

    public void setRoleClientList(List<RoleClient> roleClientList) {
        this.roleClientList = roleClientList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClientServiceFunctionality other = (ClientServiceFunctionality) obj;
        if (this.clientServiceFunctionalityPK != other.clientServiceFunctionalityPK
            && (this.clientServiceFunctionalityPK == null || !this.clientServiceFunctionalityPK.equals(other.clientServiceFunctionalityPK))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67
            * hash
            + (this.clientServiceFunctionalityPK != null ? this.clientServiceFunctionalityPK.hashCode() : 0);

        return hash;
    }

    @Override
    public String toString() {
        return "ClientServiceFunctionality[clientServiceFunctionalityPK="
            + clientServiceFunctionalityPK + "]";
    }
}
