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
@Table(name = "ussd_driver")
public class UssdDriver implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "DRIVERNAME")
    private String drivername;
    @Column(name = "DRIVERTYPE")
    private String drivertype;
    @Column(name = "EJBPKGS")
    private String ejbpkgs;
    @Column(name = "NAMINGFACTORY")
    private String namingfactory;
    @OneToMany(mappedBy = "ussdDriver")
    private List<UssdMenuEntry> ussdMenuEntryList;
    @OneToMany(mappedBy = "ussdDriver1")
    private List<UssdMenuEntry> ussdMenuEntryList1;
    @JoinColumn(name = "DRIVERHOST_ID", referencedColumnName = "ID")
    @ManyToOne
    private UssdDriverHost ussdDriverHost;
    @OneToMany(mappedBy = "ussdDriver")
    private List<UssdValidation> ussdValidationList;

    public UssdDriver() {
    }

    public UssdDriver(Long id) {
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

    public String getDrivername() {
        return drivername;
    }

    public void setDrivername(String drivername) {
        this.drivername = drivername;
    }

    public String getDrivertype() {
        return drivertype;
    }

    public void setDrivertype(String drivertype) {
        this.drivertype = drivertype;
    }

    public String getEjbpkgs() {
        return ejbpkgs;
    }

    public void setEjbpkgs(String ejbpkgs) {
        this.ejbpkgs = ejbpkgs;
    }

    public String getNamingfactory() {
        return namingfactory;
    }

    public void setNamingfactory(String namingfactory) {
        this.namingfactory = namingfactory;
    }

    public List<UssdMenuEntry> getUssdMenuEntryList() {
        return ussdMenuEntryList;
    }

    public void setUssdMenuEntryList(List<UssdMenuEntry> ussdMenuEntryList) {
        this.ussdMenuEntryList = ussdMenuEntryList;
    }

    public List<UssdMenuEntry> getUssdMenuEntryList1() {
        return ussdMenuEntryList1;
    }

    public void setUssdMenuEntryList1(List<UssdMenuEntry> ussdMenuEntryList1) {
        this.ussdMenuEntryList1 = ussdMenuEntryList1;
    }

    public UssdDriverHost getUssdDriverHost() {
        return ussdDriverHost;
    }

    public void setUssdDriverHost(UssdDriverHost ussdDriverHost) {
        this.ussdDriverHost = ussdDriverHost;
    }

    public List<UssdValidation> getUssdValidationList() {
        return ussdValidationList;
    }

    public void setUssdValidationList(List<UssdValidation> ussdValidationList) {
        this.ussdValidationList = ussdValidationList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UssdDriver other = (UssdDriver) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.drivername == null) ? (other.drivername != null) : !this.drivername.equals(other.drivername)) {
            return false;
        }
        if ((this.drivertype == null) ? (other.drivertype != null) : !this.drivertype.equals(other.drivertype)) {
            return false;
        }
        if ((this.ejbpkgs == null) ? (other.ejbpkgs != null) : !this.ejbpkgs.equals(other.ejbpkgs)) {
            return false;
        }
        if ((this.namingfactory == null) ? (other.namingfactory != null) : !this.namingfactory.equals(other.namingfactory)) {
            return false;
        }
        if (this.ussdMenuEntryList != other.ussdMenuEntryList && (this.ussdMenuEntryList == null || !this.ussdMenuEntryList.equals(other.ussdMenuEntryList))) {
            return false;
        }
        if (this.ussdMenuEntryList1 != other.ussdMenuEntryList1 && (this.ussdMenuEntryList1 == null || !this.ussdMenuEntryList1.equals(other.ussdMenuEntryList1))) {
            return false;
        }
        if (this.ussdDriverHost != other.ussdDriverHost && (this.ussdDriverHost == null || !this.ussdDriverHost.equals(other.ussdDriverHost))) {
            return false;
        }
        if (this.ussdValidationList != other.ussdValidationList && (this.ussdValidationList == null || !this.ussdValidationList.equals(other.ussdValidationList))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 97 * hash + (this.drivername != null ? this.drivername.hashCode() : 0);
        hash = 97 * hash + (this.drivertype != null ? this.drivertype.hashCode() : 0);
        hash = 97 * hash + (this.ejbpkgs != null ? this.ejbpkgs.hashCode() : 0);
        hash = 97 * hash + (this.namingfactory != null ? this.namingfactory.hashCode() : 0);
        hash = 97 * hash + (this.ussdMenuEntryList != null ? this.ussdMenuEntryList.hashCode() : 0);
        hash = 97 * hash + (this.ussdMenuEntryList1 != null ? this.ussdMenuEntryList1.hashCode() : 0);
        hash = 97 * hash + (this.ussdDriverHost != null ? this.ussdDriverHost.hashCode() : 0);
        hash = 97 * hash + (this.ussdValidationList != null ? this.ussdValidationList.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "UssdDriver[id=" + id + "]";
    }
}
