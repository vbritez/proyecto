package com.tigo.cs.domain.ussd;

import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.DataCheck;
import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Miguel Maciel
 * @version %Id%
 */
@Entity
@Table(name = "ussd_menu_entry_check")
public class UssdMenuEntryCheck implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UssdMenuEntryCheckPK ussdMenuEntryCheckPK;
    @JoinColumn(name = "COD_USSD_MENU_ENTRY", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private UssdMenuEntry ussdMenuEntry;
    @JoinColumn(name = "COD_DATA_CHECK", referencedColumnName = "DATA_CHECK_COD", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private DataCheck dataCheck;
    @JoinColumn(name = "COD_CLIENT", referencedColumnName = "CLIENT_COD", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Client client;

    public UssdMenuEntryCheck() {
    }

    public UssdMenuEntryCheck(UssdMenuEntryCheckPK ussdMenuEntryCheckPK) {
        this.ussdMenuEntryCheckPK = ussdMenuEntryCheckPK;
    }

    public UssdMenuEntryCheck(BigInteger codUssdMenuEntry, BigInteger codDataCheck, BigInteger codClient) {
        this.ussdMenuEntryCheckPK = new UssdMenuEntryCheckPK(codUssdMenuEntry, codDataCheck, codClient);
    }

    public UssdMenuEntryCheckPK getUssdMenuEntryCheckPK() {
        return ussdMenuEntryCheckPK;
    }

    public void setUssdMenuEntryCheckPK(UssdMenuEntryCheckPK ussdMenuEntryCheckPK) {
        this.ussdMenuEntryCheckPK = ussdMenuEntryCheckPK;
    }

    public UssdMenuEntry getUssdMenuEntry() {
        return ussdMenuEntry;
    }

    public void setUssdMenuEntry(UssdMenuEntry ussdMenuEntry) {
        this.ussdMenuEntry = ussdMenuEntry;
    }

    public DataCheck getDataCheck() {
        return dataCheck;
    }

    public void setDataCheck(DataCheck dataCheck) {
        this.dataCheck = dataCheck;
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
        final UssdMenuEntryCheck other = (UssdMenuEntryCheck) obj;
        if (this.ussdMenuEntryCheckPK != other.ussdMenuEntryCheckPK && (this.ussdMenuEntryCheckPK == null || !this.ussdMenuEntryCheckPK.equals(other.ussdMenuEntryCheckPK))) {
            return false;
        }
        if (this.ussdMenuEntry != other.ussdMenuEntry && (this.ussdMenuEntry == null || !this.ussdMenuEntry.equals(other.ussdMenuEntry))) {
            return false;
        }
        if (this.dataCheck != other.dataCheck && (this.dataCheck == null || !this.dataCheck.equals(other.dataCheck))) {
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
        hash = 37 * hash + (this.ussdMenuEntryCheckPK != null ? this.ussdMenuEntryCheckPK.hashCode() : 0);
        hash = 37 * hash + (this.ussdMenuEntry != null ? this.ussdMenuEntry.hashCode() : 0);
        hash = 37 * hash + (this.dataCheck != null ? this.dataCheck.hashCode() : 0);
        hash = 37 * hash + (this.client != null ? this.client.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "com.tigo.cs.domain.ussd.UssdMenuEntryCheck[ussdMenuEntryCheckPK=" + ussdMenuEntryCheckPK + "]";
    }
}
