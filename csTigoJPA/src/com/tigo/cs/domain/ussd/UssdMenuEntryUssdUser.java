package com.tigo.cs.domain.ussd;

import java.io.Serializable;
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
 * @version $Id$
 */
@Entity
@Table(name = "ussd_menu_entry_ussd_user")
@NamedQueries({
    @NamedQuery(name = "UssdMenuEntryUssdUser.findByUssduserId", query = "SELECT u FROM UssdMenuEntryUssdUser u WHERE u.ussdMenuEntryUssdUserPK.ussduserId = :ussduserId")
})
public class UssdMenuEntryUssdUser implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UssdMenuEntryUssdUserPK ussdMenuEntryUssdUserPK;
    @JoinColumn(name = "USSDUSER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private UssdUser ussdUser;

    public UssdMenuEntryUssdUser() {
    }

    public UssdMenuEntryUssdUser(UssdMenuEntryUssdUserPK ussdMenuEntryUssdUserPK) {
        this.ussdMenuEntryUssdUserPK = ussdMenuEntryUssdUserPK;
    }

    public UssdMenuEntryUssdUser(Long ussdmenuentriesId, Long ussduserId) {
        this.ussdMenuEntryUssdUserPK = new UssdMenuEntryUssdUserPK(ussdmenuentriesId, ussduserId);
    }

    public UssdMenuEntryUssdUserPK getUssdMenuEntryUssdUserPK() {
        return ussdMenuEntryUssdUserPK;
    }

    public void setUssdMenuEntryUssdUserPK(UssdMenuEntryUssdUserPK ussdMenuEntryUssdUserPK) {
        this.ussdMenuEntryUssdUserPK = ussdMenuEntryUssdUserPK;
    }

    public UssdUser getUssdUser() {
        return ussdUser;
    }

    public void setUssdUser(UssdUser ussdUser) {
        this.ussdUser = ussdUser;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UssdMenuEntryUssdUser other = (UssdMenuEntryUssdUser) obj;
        if (this.ussdMenuEntryUssdUserPK != other.ussdMenuEntryUssdUserPK && (this.ussdMenuEntryUssdUserPK == null || !this.ussdMenuEntryUssdUserPK.equals(other.ussdMenuEntryUssdUserPK))) {
            return false;
        }
        if (this.ussdUser != other.ussdUser && (this.ussdUser == null || !this.ussdUser.equals(other.ussdUser))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.ussdMenuEntryUssdUserPK != null ? this.ussdMenuEntryUssdUserPK.hashCode() : 0);
        hash = 89 * hash + (this.ussdUser != null ? this.ussdUser.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "UssdMenuEntryUssdUser[ussdMenuEntryUssdUserPK=" + ussdMenuEntryUssdUserPK + "]";
    }
}
