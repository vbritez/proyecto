package com.tigo.cs.domain.ussd;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.domain.Service;

/**
 * 
 * @author Javier Kovacs
 * @version $Id: UssdServiceCorresp.java 114 2011-11-29 18:56:42Z javier.kovacs
 *          $
 */
@Entity
@Table(name = "ussd_service_corresp")
@NamedQueries({ @NamedQuery(
        name = "UssdServiceCorresp.findByCodService",
        query = "SELECT u FROM UssdServiceCorresp u WHERE u.ussdServiceCorrespPK.codService = :codService") })
public class UssdServiceCorresp implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UssdServiceCorrespPK ussdServiceCorrespPK;
    @JoinColumn(name = "USSDMENUENTRIES_ID", referencedColumnName = "ID",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    @PrimarySortedField
    private UssdMenuEntry ussdMenuEntry;
    // bi-directional many-to-one association to Service
    @ManyToOne
    @JoinColumn(name = "COD_SERVICE", referencedColumnName = "SERVICE_COD",
            insertable = false, updatable = false)
    private Service service;

    public UssdServiceCorresp() {
    }

    public UssdServiceCorresp(UssdServiceCorrespPK ussdServiceCorrespPK) {
        this.ussdServiceCorrespPK = ussdServiceCorrespPK;
    }

    public UssdServiceCorresp(Long codService, Long ussdmenuentriesId) {
        this.ussdServiceCorrespPK = new UssdServiceCorrespPK(codService, ussdmenuentriesId);
    }

    public UssdServiceCorrespPK getUssdServiceCorrespPK() {
        return ussdServiceCorrespPK;
    }

    public void setUssdServiceCorrespPK(UssdServiceCorrespPK ussdServiceCorrespPK) {
        this.ussdServiceCorrespPK = ussdServiceCorrespPK;
    }

    public UssdMenuEntry getUssdMenuEntry() {
        return ussdMenuEntry;
    }

    public void setUssdMenuEntry(UssdMenuEntry ussdMenuEntry) {
        this.ussdMenuEntry = ussdMenuEntry;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UssdServiceCorresp other = (UssdServiceCorresp) obj;
        if (this.ussdServiceCorrespPK != other.ussdServiceCorrespPK
            && (this.ussdServiceCorrespPK == null || !this.ussdServiceCorrespPK.equals(other.ussdServiceCorrespPK))) {
            return false;
        }
        if (this.ussdMenuEntry != other.ussdMenuEntry
            && (this.ussdMenuEntry == null || !this.ussdMenuEntry.equals(other.ussdMenuEntry))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43
            * hash
            + (this.ussdServiceCorrespPK != null ? this.ussdServiceCorrespPK.hashCode() : 0);
        hash = 43 * hash
            + (this.ussdMenuEntry != null ? this.ussdMenuEntry.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "com.tigo.cs.domain.ussd.UssdServiceCorresp[ussdServiceCorrespPK="
            + ussdServiceCorrespPK + "]";
    }
}
