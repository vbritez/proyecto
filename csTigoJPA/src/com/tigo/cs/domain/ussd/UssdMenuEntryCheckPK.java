package com.tigo.cs.domain.ussd;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Miguel Maciel
 * @version %Id%
 */
@Embeddable
public class UssdMenuEntryCheckPK implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6910732626080590413L;
	@Basic(optional = false)
    @Column(name = "COD_USSD_MENU_ENTRY")
    private BigInteger codUssdMenuEntry;
    @Basic(optional = false)
    @Column(name = "COD_DATA_CHECK")
    private BigInteger codDataCheck;
    @Basic(optional = false)
    @Column(name = "COD_CLIENT")
    private BigInteger codClient;

    public UssdMenuEntryCheckPK() {
    }

    public UssdMenuEntryCheckPK(BigInteger codUssdMenuEntry, BigInteger codDataCheck, BigInteger codClient) {
        this.codUssdMenuEntry = codUssdMenuEntry;
        this.codDataCheck = codDataCheck;
        this.codClient = codClient;
    }

    public BigInteger getCodUssdMenuEntry() {
        return codUssdMenuEntry;
    }

    public void setCodUssdMenuEntry(BigInteger codUssdMenuEntry) {
        this.codUssdMenuEntry = codUssdMenuEntry;
    }

    public BigInteger getCodDataCheck() {
        return codDataCheck;
    }

    public void setCodDataCheck(BigInteger codDataCheck) {
        this.codDataCheck = codDataCheck;
    }

    public BigInteger getCodClient() {
        return codClient;
    }

    public void setCodClient(BigInteger codClient) {
        this.codClient = codClient;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UssdMenuEntryCheckPK other = (UssdMenuEntryCheckPK) obj;
        if (this.codUssdMenuEntry != other.codUssdMenuEntry && (this.codUssdMenuEntry == null || !this.codUssdMenuEntry.equals(other.codUssdMenuEntry))) {
            return false;
        }
        if (this.codDataCheck != other.codDataCheck && (this.codDataCheck == null || !this.codDataCheck.equals(other.codDataCheck))) {
            return false;
        }
        if (this.codClient != other.codClient && (this.codClient == null || !this.codClient.equals(other.codClient))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.codUssdMenuEntry != null ? this.codUssdMenuEntry.hashCode() : 0);
        hash = 29 * hash + (this.codDataCheck != null ? this.codDataCheck.hashCode() : 0);
        hash = 29 * hash + (this.codClient != null ? this.codClient.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "UssdMenuEntryCheckPK[codUssdMenuEntry=" + codUssdMenuEntry + ", codDataCheck=" + codDataCheck + ", codClient=" + codClient + "]";
    }
}
