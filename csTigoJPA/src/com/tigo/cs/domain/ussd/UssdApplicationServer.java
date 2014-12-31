package com.tigo.cs.domain.ussd;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Javier Kovacs
 * @version $Id$
 */
@Entity
@Table(name = "ussd_application_server")
public class UssdApplicationServer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
    @Basic(optional = false)
    @Column(name = "ACTIVE")
    private short active;
    @OneToMany(mappedBy = "ussdApplicationServer")
    private List<UssdMenuEntry> ussdMenuEntryList;
    @OneToMany(mappedBy = "ussdApplicationServer")
    private List<UssdGwUssd> ussdGwUssdList;
    
    /*@OneToMany(mappedBy = "ussdApplicationServer")
    private List<UssdApplication> ussdApplicationList;*/
    
    //bi-directional many-to-many association to UssdApplicationServer
    @ManyToMany(mappedBy="ussdApplicationServerList")
    private List<UssdApplication> ussdApplicationList;

    public UssdApplicationServer() {
    }

    public UssdApplicationServer(Long id) {
        this.id = id;
    }

    public UssdApplicationServer(Long id, short active) {
        this.id = id;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public short getActive() {
        return active;
    }

    public void setActive(short active) {
        this.active = active;
    }

    public List<UssdMenuEntry> getUssdMenuEntryList() {
        return ussdMenuEntryList;
    }

    public void setUssdMenuEntryList(List<UssdMenuEntry> ussdMenuEntryList) {
        this.ussdMenuEntryList = ussdMenuEntryList;
    }

    public List<UssdGwUssd> getUssdGwUssdList() {
        return ussdGwUssdList;
    }

    public void setUssdGwUssdList(List<UssdGwUssd> ussdGwUssdList) {
        this.ussdGwUssdList = ussdGwUssdList;
    }

    /*public List<UssdApplication> getUssdApplicationList() {
        return ussdApplicationList;
    }

    public void setUssdApplicationList(List<UssdApplication> ussdApplicationList) {
        this.ussdApplicationList = ussdApplicationList;
    }*/

    public List<UssdApplication> getUssdApplicationList() {
        return ussdApplicationList;
    }

    public void setUssdApplicationList(List<UssdApplication> ussdApplicationList) {
        this.ussdApplicationList = ussdApplicationList;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UssdApplicationServer other = (UssdApplicationServer) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if (this.active != other.active) {
            return false;
        }
        if (this.ussdMenuEntryList != other.ussdMenuEntryList && (this.ussdMenuEntryList == null || !this.ussdMenuEntryList.equals(other.ussdMenuEntryList))) {
            return false;
        }
        if (this.ussdGwUssdList != other.ussdGwUssdList && (this.ussdGwUssdList == null || !this.ussdGwUssdList.equals(other.ussdGwUssdList))) {
            return false;
        }
        if (this.ussdApplicationList != other.ussdApplicationList && (this.ussdApplicationList == null || !this.ussdApplicationList.equals(other.ussdApplicationList))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 17 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 17 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 17 * hash + this.active;
        hash = 17 * hash + (this.ussdMenuEntryList != null ? this.ussdMenuEntryList.hashCode() : 0);
        hash = 17 * hash + (this.ussdGwUssdList != null ? this.ussdGwUssdList.hashCode() : 0);
        hash = 17 * hash + (this.ussdApplicationList != null ? this.ussdApplicationList.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "UssdApplicationServer[id=" + id + "]";
    }
}
