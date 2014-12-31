package com.tigo.cs.domain.ussd;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Javier Kovacs
 * @version $Id$
 */
@Entity
@Table(name = "ussd_application")
public class UssdApplication implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "CODE")
    private String code;
    @OneToMany(mappedBy = "ussdApplication")
    private List<UssdUser> ussdUserList;
    @OneToMany(mappedBy = "ussdApplication")
    private List<UssdMenuEntry> ussdMenuEntryList;
    @OneToMany(mappedBy = "ussdApplication")
    private List<UssdTransactionLog> ussdTransactionLogList;
    @JoinColumn(name = "APPLICATIONSERVER_ID", referencedColumnName = "ID")
    @ManyToOne
    private UssdApplicationServer ussdApplicationServer;

    //bi-directional many-to-many association to UssdApplication
    @ManyToMany
    @JoinTable(
        name="USSD_APP_APP_SERVER"
        , joinColumns={
            @JoinColumn(name="USSD_APPLICATION")
            }
        , inverseJoinColumns={
            @JoinColumn(name="USSD_APPLICATION_SERVER")
            }
        )
    private List<UssdApplicationServer> ussdApplicationServerList;
        
    
    public UssdApplication() {
    }

    public UssdApplication(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<UssdUser> getUssdUserList() {
        return ussdUserList;
    }

    public void setUssdUserList(List<UssdUser> ussdUserList) {
        this.ussdUserList = ussdUserList;
    }

    public List<UssdMenuEntry> getUssdMenuEntryList() {
        return ussdMenuEntryList;
    }

    public void setUssdMenuEntryList(List<UssdMenuEntry> ussdMenuEntryList) {
        this.ussdMenuEntryList = ussdMenuEntryList;
    }

    public List<UssdTransactionLog> getUssdTransactionLogList() {
        return ussdTransactionLogList;
    }

    public void setUssdTransactionLogList(List<UssdTransactionLog> ussdTransactionLogList) {
        this.ussdTransactionLogList = ussdTransactionLogList;
    }

    public UssdApplicationServer getUssdApplicationServer() {
        return ussdApplicationServer;
    }

    public void setUssdApplicationServer(UssdApplicationServer ussdApplicationServer) {
        this.ussdApplicationServer = ussdApplicationServer;
    }


    public List<UssdApplicationServer> getUssdApplicationServerList() {
        return ussdApplicationServerList;
    }

    public void setUssdApplicationServerList(List<UssdApplicationServer> ussdApplicationServerList) {
        this.ussdApplicationServerList = ussdApplicationServerList;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UssdApplication other = (UssdApplication) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.code == null) ? (other.code != null) : !this.code.equals(other.code)) {
            return false;
        }
        if (this.ussdUserList != other.ussdUserList && (this.ussdUserList == null || !this.ussdUserList.equals(other.ussdUserList))) {
            return false;
        }
        if (this.ussdMenuEntryList != other.ussdMenuEntryList && (this.ussdMenuEntryList == null || !this.ussdMenuEntryList.equals(other.ussdMenuEntryList))) {
            return false;
        }
        if (this.ussdTransactionLogList != other.ussdTransactionLogList && (this.ussdTransactionLogList == null || !this.ussdTransactionLogList.equals(other.ussdTransactionLogList))) {
            return false;
        }
        if (this.ussdApplicationServerList != other.ussdApplicationServerList && (this.ussdApplicationServerList == null || !this.ussdApplicationServerList.equals(other.ussdApplicationServerList))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 89 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 89 * hash + (this.code != null ? this.code.hashCode() : 0);
        hash = 89 * hash + (this.ussdUserList != null ? this.ussdUserList.hashCode() : 0);
        hash = 89 * hash + (this.ussdMenuEntryList != null ? this.ussdMenuEntryList.hashCode() : 0);
        hash = 89 * hash + (this.ussdTransactionLogList != null ? this.ussdTransactionLogList.hashCode() : 0);
        hash = 89 * hash + (this.ussdApplicationServerList != null ? this.ussdApplicationServerList.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "UssdApplication[id=" + id + "]";
    }
}
