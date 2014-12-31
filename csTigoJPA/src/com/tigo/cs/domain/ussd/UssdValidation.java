package com.tigo.cs.domain.ussd;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Javier Kovacs
 * @version $Id$
 */
@Entity
@Table(name = "ussd_validation")
public class UssdValidation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "REGEXP")
    private String regexp;
    @Column(name = "LOGRESULT")
    private Short logresult;
    @OneToMany(mappedBy = "ussdValidation")
    private List<UssdMenuEntry> ussdMenuEntryList;
    @JoinColumn(name = "DRIVER_ID", referencedColumnName = "ID")
    @ManyToOne
    private UssdDriver ussdDriver;

    public UssdValidation() {
    }

    public UssdValidation(Long id) {
        this.id = id;
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

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public Short getLogresult() {
        return logresult;
    }

    public void setLogresult(Short logresult) {
        this.logresult = logresult;
    }

    public List<UssdMenuEntry> getUssdMenuEntryList() {
        return ussdMenuEntryList;
    }

    public void setUssdMenuEntryList(List<UssdMenuEntry> ussdMenuEntryList) {
        this.ussdMenuEntryList = ussdMenuEntryList;
    }

    public UssdDriver getUssdDriver() {
        return ussdDriver;
    }

    public void setUssdDriver(UssdDriver ussdDriver) {
        this.ussdDriver = ussdDriver;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UssdValidation other = (UssdValidation) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.regexp == null) ? (other.regexp != null) : !this.regexp.equals(other.regexp)) {
            return false;
        }
        if (this.logresult != other.logresult && (this.logresult == null || !this.logresult.equals(other.logresult))) {
            return false;
        }
        if (this.ussdMenuEntryList != other.ussdMenuEntryList && (this.ussdMenuEntryList == null || !this.ussdMenuEntryList.equals(other.ussdMenuEntryList))) {
            return false;
        }
        if (this.ussdDriver != other.ussdDriver && (this.ussdDriver == null || !this.ussdDriver.equals(other.ussdDriver))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 59 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 59 * hash + (this.regexp != null ? this.regexp.hashCode() : 0);
        hash = 59 * hash + (this.logresult != null ? this.logresult.hashCode() : 0);
        hash = 59 * hash + (this.ussdMenuEntryList != null ? this.ussdMenuEntryList.hashCode() : 0);
        hash = 59 * hash + (this.ussdDriver != null ? this.ussdDriver.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "UssdValidation[id=" + id + "]";
    }
}
