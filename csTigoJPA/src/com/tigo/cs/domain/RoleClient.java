package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "role_client")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(name = "RoleClient.findAll",
        query = "SELECT r FROM RoleClient r") })
public class RoleClient implements Serializable {

    private static final long serialVersionUID = 1019763989506292142L;
    @Id
    @SequenceGenerator(name = "roleclientGen", sequenceName = "ROLECLIENT_SEQ",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "roleclientGen")
    @Basic(optional = false)
    @Column(name = "ROLE_CLIENT_COD")
    @Searchable(label = "entity.roleclient.searchable.roleclientCod")
    private Long roleClientCod;
    @Basic(optional = false)
    @Column(name = "DESCRIPTION")
    @Searchable(label = "entity.roleclient.searchable.descriptionChr")
    @PrimarySortedField
    @NotEmpty(
            message = "{entity.roleclient.constraint.descriptionChr.NotEmpty}")
    private String description;
    @Basic(optional = false)
    @Column(name = "ENABLED")
    private Boolean enabled;
    @ManyToMany(mappedBy = "roleClientList")
    private List<Userweb> userwebList;
    @JoinTable(
            name = "ROLE_CLIENT_FUNCTIONALITY",
            joinColumns = { @JoinColumn(name = "COD_ROLE_CLIENT",
                    referencedColumnName = "ROLE_CLIENT_COD") },
            inverseJoinColumns = { @JoinColumn(name = "COD_CLIENT",
                    referencedColumnName = "COD_CLIENT"), @JoinColumn(
                    name = "COD_SERVICE", referencedColumnName = "COD_SERVICE"), @JoinColumn(
                    name = "COD_FUNCTIONALITY",
                    referencedColumnName = "COD_FUNCTIONALITY") })
    @ManyToMany()
    private List<ClientServiceFunctionality> clientServiceFunctionalityList;
    @OneToMany(cascade = { CascadeType.MERGE }, mappedBy = "roleClient")
    private List<RoleClientScreen> roleClientScreenList;
    @JoinColumn(name = "COD_CLIENT", referencedColumnName = "CLIENT_COD")
    @ManyToOne(optional = false)
    private Client client;

    public RoleClient() {
    }

    public RoleClient(Long roleClientCod) {
        this.roleClientCod = roleClientCod;
    }

    public RoleClient(Long roleClientCod, String description, Boolean enabled) {
        this.roleClientCod = roleClientCod;
        this.description = description;
        this.enabled = enabled;
    }

    public Long getRoleClientCod() {
        return roleClientCod;
    }

    public void setRoleClientCod(Long roleClientCod) {
        this.roleClientCod = roleClientCod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;

    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<Userweb> getUserwebList() {
        return userwebList;
    }

    public void setUserwebList(List<Userweb> userwebList) {
        this.userwebList = userwebList;
    }

    public List<ClientServiceFunctionality> getClientServiceFunctionalityList() {
        return clientServiceFunctionalityList;
    }

    public void setClientServiceFunctionalityList(List<ClientServiceFunctionality> clientServiceFunctionalityList) {
        this.clientServiceFunctionalityList = clientServiceFunctionalityList;
    }

    public List<RoleClientScreen> getRoleClientScreenList() {
        return roleClientScreenList;
    }

    public void setRoleClientScreenList(List<RoleClientScreen> roleClientScreenList) {
        this.roleClientScreenList = roleClientScreenList;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RoleClient other = (RoleClient) obj;
        if (this.roleClientCod != other.roleClientCod
            && (this.roleClientCod == null || !this.roleClientCod.equals(other.roleClientCod))) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if (this.enabled != other.enabled
            && (this.enabled == null || !this.enabled.equals(other.enabled))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash
            + (this.roleClientCod != null ? this.roleClientCod.hashCode() : 0);
        hash = 59 * hash
            + (this.description != null ? this.description.hashCode() : 0);
        hash = 59 * hash + (this.enabled != null ? this.enabled.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "RoleClient[roleClientCod=" + roleClientCod + "]";
    }
}
