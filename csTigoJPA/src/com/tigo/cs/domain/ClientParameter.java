package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.PrimarySortedField;

@Entity
@Table(name = "client_parameter")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class ClientParameter implements Serializable {

    private static final long serialVersionUID = -8176567232711796451L;
    @Id
    @Basic(optional = false)
    @Column(name = "CLIENT_PARAMETER_COD")
    @PrimarySortedField
    private String clientParameterCod;
    @Basic(optional = false)
    @Column(name = "DESCRIPTION_CHR")
    private String descriptionChr;
    @OneToMany(
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH },
            mappedBy = "clientParameter")
    private List<ClientParameterValue> clientParameterValueList;

    public ClientParameter() {
    }

    public ClientParameter(String clientParameterCod) {
        this.clientParameterCod = clientParameterCod;
    }

    public ClientParameter(String clientParameterCod, String descriptionChr) {
        this.clientParameterCod = clientParameterCod;
        this.descriptionChr = descriptionChr;
    }

    public String getClientParameterCod() {
        return clientParameterCod;
    }

    public void setClientParameterCod(String clientParameterCod) {
        this.clientParameterCod = clientParameterCod;
    }

    public String getDescriptionChr() {
        return descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public List<ClientParameterValue> getClientParameterValueList() {
        return clientParameterValueList;
    }

    public void setClientParameterValueList(List<ClientParameterValue> clientParameterValueList) {
        this.clientParameterValueList = clientParameterValueList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClientParameter other = (ClientParameter) obj;
        if ((this.clientParameterCod == null) ? (other.clientParameterCod != null) : !this.clientParameterCod.equals(other.clientParameterCod)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67
            * hash
            + (this.clientParameterCod != null ? this.clientParameterCod.hashCode() : 0);
        hash = 67
            * hash
            + (this.descriptionChr != null ? this.descriptionChr.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "ClientParameter[clientParameterCod=" + clientParameterCod + "]";
    }
}
