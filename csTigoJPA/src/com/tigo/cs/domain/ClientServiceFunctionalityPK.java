package com.tigo.cs.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author Miguel Zorrilla
 * @version $Id: ClientServiceFunctionalityPK.java 7 2011-11-18 11:12:15Z
 *          miguel.maciel $
 */
@Embeddable
public class ClientServiceFunctionalityPK implements Serializable {

    private static final long serialVersionUID = -2619150948716934306L;
    @Basic(optional = false)
    @Column(name = "cod_client", unique = true, nullable = false)
    private long codClient;
    @Basic(optional = false)
    @Column(name = "cod_service", unique = true, nullable = false)
    private long codService;
    @Basic(optional = false)
    @Column(name = "cod_functionality", unique = true, nullable = false)
    private long codFunctionality;

    public ClientServiceFunctionalityPK() {
    }

    public ClientServiceFunctionalityPK(long codClient, long codService, long codFunctionality) {
        this.codClient = codClient;
        this.codService = codService;
        this.codFunctionality = codFunctionality;
    }

    public long getCodClient() {
        return codClient;
    }

    public void setCodClient(long codClient) {
        this.codClient = codClient;
    }

    public long getCodService() {
        return codService;
    }

    public void setCodService(long codService) {
        this.codService = codService;
    }

    public long getCodFunctionality() {
        return codFunctionality;
    }

    public void setCodFunctionality(long codFunctionality) {
        this.codFunctionality = codFunctionality;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClientServiceFunctionalityPK other = (ClientServiceFunctionalityPK) obj;
        if (this.codClient != other.codClient) {
            return false;
        }
        if (this.codService != other.codService) {
            return false;
        }
        if (this.codFunctionality != other.codFunctionality) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (int) (this.codClient ^ (this.codClient >>> 32));
        hash = 89 * hash + (int) (this.codService ^ (this.codService >>> 32));
        hash = 89 * hash + (int) (this.codFunctionality ^ (this.codFunctionality >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "ClientServiceFunctionalityPK[codClient=" + codClient + ", codService=" + codService + ", codFunctionality=" + codFunctionality + "]";
    }
}
