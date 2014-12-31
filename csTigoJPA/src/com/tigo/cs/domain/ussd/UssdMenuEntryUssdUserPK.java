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
public class UssdMenuEntryUssdUserPK implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6300416578338169008L;
	@Basic(optional = false)
    @Column(name = "USSDMENUENTRIES_ID")
    private Long ussdmenuentriesId;
    @Basic(optional = false)
    @Column(name = "USSDUSER_ID")
    private Long ussduserId;

    public UssdMenuEntryUssdUserPK() {
    }

    public UssdMenuEntryUssdUserPK(Long ussdmenuentriesId, Long ussduserId) {
        this.ussdmenuentriesId = ussdmenuentriesId;
        this.ussduserId = ussduserId;
    }

    public Long getUssdmenuentriesId() {
        return ussdmenuentriesId;
    }

    public void setUssdmenuentriesId(Long ussdmenuentriesId) {
        this.ussdmenuentriesId = ussdmenuentriesId;
    }

    public Long getUssduserId() {
        return ussduserId;
    }

    public void setUssduserId(Long ussduserId) {
        this.ussduserId = ussduserId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UssdMenuEntryUssdUserPK other = (UssdMenuEntryUssdUserPK) obj;
        if (this.ussdmenuentriesId != other.ussdmenuentriesId && (this.ussdmenuentriesId == null || !this.ussdmenuentriesId.equals(other.ussdmenuentriesId))) {
            return false;
        }
        if (this.ussduserId != other.ussduserId && (this.ussduserId == null || !this.ussduserId.equals(other.ussduserId))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.ussdmenuentriesId != null ? this.ussdmenuentriesId.hashCode() : 0);
        hash = 23 * hash + (this.ussduserId != null ? this.ussduserId.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "UssdMenuEntryUssdUserPK[ussdmenuentriesId=" + ussdmenuentriesId + ", ussduserId=" + ussduserId + "]";
    }
}
