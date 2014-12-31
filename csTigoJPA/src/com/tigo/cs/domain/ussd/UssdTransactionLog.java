package com.tigo.cs.domain.ussd;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Javier Kovacs
 * @version $Id$
 */
@Entity
@Table(name = "ussd_transaction_log")
public class UssdTransactionLog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "FECHA_PROCESO")
    @Temporal(TemporalType.DATE)
    private Date fechaProceso;
    @Column(name = "ACCOUNT")
    private String account;
    @Column(name = "INPUTTEXT")
    private String inputtext;
    @Column(name = "RESULTTEXT")
    private String resulttext;
    @Column(name = "SHORTNUMBER")
    private String shortnumber;
    @Column(name = "MENUPATH")
    private String menupath;
    @JoinColumn(name = "APP_ID", referencedColumnName = "ID")
    @ManyToOne
    private UssdApplication ussdApplication;

    public UssdTransactionLog() {
    }

    public UssdTransactionLog(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaProceso() {
        return fechaProceso;
    }

    public void setFechaProceso(Date fechaProceso) {
        this.fechaProceso = fechaProceso;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getInputtext() {
        return inputtext;
    }

    public void setInputtext(String inputtext) {
        this.inputtext = inputtext;
    }

    public String getResulttext() {
        return resulttext;
    }

    public void setResulttext(String resulttext) {
        this.resulttext = resulttext;
    }

    public String getShortnumber() {
        return shortnumber;
    }

    public void setShortnumber(String shortnumber) {
        this.shortnumber = shortnumber;
    }

    public String getMenupath() {
        return menupath;
    }

    public void setMenupath(String menupath) {
        this.menupath = menupath;
    }

    public UssdApplication getUssdApplication() {
        return ussdApplication;
    }

    public void setUssdApplication(UssdApplication ussdApplication) {
        this.ussdApplication = ussdApplication;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UssdTransactionLog other = (UssdTransactionLog) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.fechaProceso != other.fechaProceso && (this.fechaProceso == null || !this.fechaProceso.equals(other.fechaProceso))) {
            return false;
        }
        if ((this.account == null) ? (other.account != null) : !this.account.equals(other.account)) {
            return false;
        }
        if ((this.inputtext == null) ? (other.inputtext != null) : !this.inputtext.equals(other.inputtext)) {
            return false;
        }
        if ((this.resulttext == null) ? (other.resulttext != null) : !this.resulttext.equals(other.resulttext)) {
            return false;
        }
        if ((this.shortnumber == null) ? (other.shortnumber != null) : !this.shortnumber.equals(other.shortnumber)) {
            return false;
        }
        if ((this.menupath == null) ? (other.menupath != null) : !this.menupath.equals(other.menupath)) {
            return false;
        }
        if (this.ussdApplication != other.ussdApplication && (this.ussdApplication == null || !this.ussdApplication.equals(other.ussdApplication))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 59 * hash + (this.fechaProceso != null ? this.fechaProceso.hashCode() : 0);
        hash = 59 * hash + (this.account != null ? this.account.hashCode() : 0);
        hash = 59 * hash + (this.inputtext != null ? this.inputtext.hashCode() : 0);
        hash = 59 * hash + (this.resulttext != null ? this.resulttext.hashCode() : 0);
        hash = 59 * hash + (this.shortnumber != null ? this.shortnumber.hashCode() : 0);
        hash = 59 * hash + (this.menupath != null ? this.menupath.hashCode() : 0);
        hash = 59 * hash + (this.ussdApplication != null ? this.ussdApplication.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "UssdTransactionLog[id=" + id + "]";
    }
}
