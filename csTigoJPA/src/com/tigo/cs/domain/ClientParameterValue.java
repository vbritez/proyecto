package com.tigo.cs.domain;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * 
 * @author Javier Kovacs
 * @version $Id: ClientParameterValue.java 7 2011-11-18 11:12:15Z miguel.maciel
 *          $
 */
@Entity
@Table(name = "client_parameter_value")
@NamedQueries({ 
    @NamedQuery(name = "ClientParameterValue.findByCodClientAndCodClientParameter", 
            query = "SELECT c FROM ClientParameterValue c WHERE c.clientParameterValuePK.codClient = :codClient AND c.clientParameterValuePK.codClientParameter = :codClientParameter") })
public class ClientParameterValue implements Serializable {

    private static final long serialVersionUID = -5607571892833298719L;
    @EmbeddedId
    protected ClientParameterValuePK clientParameterValuePK;
    @Column(name = "VALUE_CHR")
    @Searchable(label = "entity.clientParameterValue.searchable.valueChr")
    private String valueChr;
    @JoinColumn(name = "COD_CLIENT_PARAMETER", referencedColumnName = "CLIENT_PARAMETER_COD", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    @Searchable(label = "entity.clientParameterValue.searchable.clientParameter.clientParameterCod", internalField = "clientParameterCod")
    @PrimarySortedField
    private ClientParameter clientParameter;
    @JoinColumn(name = "COD_CLIENT", referencedColumnName = "CLIENT_COD", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Client client;

    public ClientParameterValue() {
    }

    public ClientParameterValue(ClientParameterValuePK clientParameterValuePK) {
        this.clientParameterValuePK = clientParameterValuePK;
    }

    public ClientParameterValue(Long codClient, String codClientParameter) {
        this.clientParameterValuePK = new ClientParameterValuePK(codClient, codClientParameter);
    }

    public ClientParameterValuePK getClientParameterValuePK() {
        return clientParameterValuePK;
    }

    public void setClientParameterValuePK(ClientParameterValuePK clientParameterValuePK) {
        this.clientParameterValuePK = clientParameterValuePK;
    }

    public String getValueChr() {
        return valueChr;
    }

    public void setValueChr(String valueChr) {
        this.valueChr = valueChr;
    }

    public ClientParameter getClientParameter() {
        return clientParameter;
    }

    public void setClientParameter(ClientParameter clientParameter) {
        this.clientParameter = clientParameter;
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
        final ClientParameterValue other = (ClientParameterValue) obj;
        if (this.clientParameterValuePK != other.clientParameterValuePK && (this.clientParameterValuePK == null || !this.clientParameterValuePK.equals(other.clientParameterValuePK))) {
            return false;
        }
        if ((this.valueChr == null) ? (other.valueChr != null) : !this.valueChr.equals(other.valueChr)) {
            return false;
        }
        if (this.clientParameter != other.clientParameter && (this.clientParameter == null || !this.clientParameter.equals(other.clientParameter))) {
            return false;
        }
        if (this.client != other.client && (this.client == null || !this.client.equals(other.client))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.clientParameterValuePK != null ? this.clientParameterValuePK.hashCode() : 0);
        hash = 59 * hash + (this.valueChr != null ? this.valueChr.hashCode() : 0);
        hash = 59 * hash + (this.clientParameter != null ? this.clientParameter.hashCode() : 0);
        hash = 59 * hash + (this.client != null ? this.client.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "ClientParameterValue[clientParameterValuePK=" + clientParameterValuePK + "]";
    }
}
