package com.tigo.cs.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Miguel Zorrilla
 * @version $Id$
 */
@Embeddable
public class ServiceFunctionalityPK implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5057685607671176272L;
	@Basic(optional = false)
    @Column(name = "cod_service", nullable = false)
    private long codService;
    @Basic(optional = false)
    @Column(name = "cod_functionality", nullable = false)
    private long codFunctionality;

    public ServiceFunctionalityPK() {
    }

    public ServiceFunctionalityPK(long codService, long codFunctionality) {
        this.codService = codService;
        this.codFunctionality = codFunctionality;
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
        final ServiceFunctionalityPK other = (ServiceFunctionalityPK) obj;
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
        hash = 31 * hash + (int) (this.codService ^ (this.codService >>> 32));
        hash = 31 * hash + (int) (this.codFunctionality ^ (this.codFunctionality >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "ServiceFunctionalityPK[codService=" + codService + ", codFunctionality=" + codFunctionality + "]";
    }
}
