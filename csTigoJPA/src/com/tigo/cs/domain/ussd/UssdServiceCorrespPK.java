package com.tigo.cs.domain.ussd;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Javier Kovacs
 * @version $Id$
 */
@Embeddable
public class UssdServiceCorrespPK implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6422481605932879380L;
	@Basic(optional = false)
    @Column(name = "COD_SERVICE")
    private Long codService;
    @Basic(optional = false)
    @Column(name = "USSDMENUENTRIES_ID")
    private Long ussdmenuentriesId;

    public UssdServiceCorrespPK() {
    }

    public UssdServiceCorrespPK(Long codService, Long ussdmenuentriesId) {
        this.codService = codService;
        this.ussdmenuentriesId = ussdmenuentriesId;
    }

    public Long getCodService() {
        return codService;
    }

    public void setCodService(Long codService) {
        this.codService = codService;
    }

    public Long getUssdmenuentriesId() {
        return ussdmenuentriesId;
    }

    public void setUssdmenuentriesId(Long ussdmenuentriesId) {
        this.ussdmenuentriesId = ussdmenuentriesId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UssdServiceCorrespPK other = (UssdServiceCorrespPK) obj;
        if (this.codService != other.codService && (this.codService == null || !this.codService.equals(other.codService))) {
            return false;
        }
        if (this.ussdmenuentriesId != other.ussdmenuentriesId && (this.ussdmenuentriesId == null || !this.ussdmenuentriesId.equals(other.ussdmenuentriesId))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.codService != null ? this.codService.hashCode() : 0);
        hash = 97 * hash + (this.ussdmenuentriesId != null ? this.ussdmenuentriesId.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "UssdServiceCorrespPK[codService=" + codService + ", ussdmenuentriesId=" + ussdmenuentriesId + "]";
    }
}
