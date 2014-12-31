package com.tigo.cs.domain.ussd;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Javier Kovacs
 * @version $Id$
 */
@Entity
@Table(name = "ussd_driver_host")
public class UssdDriverHost implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "DRIVERHOST")
    private String driverhost;
    @Column(name = "DRIVERPORT")
    private String driverport;
    @OneToMany(mappedBy = "ussdDriverHost")
    private List<UssdDriver> ussdDriverList;

    public UssdDriverHost() {
    }

    public UssdDriverHost(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDriverhost() {
        return driverhost;
    }

    public void setDriverhost(String driverhost) {
        this.driverhost = driverhost;
    }

    public String getDriverport() {
        return driverport;
    }

    public void setDriverport(String driverport) {
        this.driverport = driverport;
    }

    public List<UssdDriver> getUssdDriverList() {
        return ussdDriverList;
    }

    public void setUssdDriverList(List<UssdDriver> ussdDriverList) {
        this.ussdDriverList = ussdDriverList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UssdDriverHost other = (UssdDriverHost) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.driverhost == null) ? (other.driverhost != null) : !this.driverhost.equals(other.driverhost)) {
            return false;
        }
        if ((this.driverport == null) ? (other.driverport != null) : !this.driverport.equals(other.driverport)) {
            return false;
        }
        if (this.ussdDriverList != other.ussdDriverList && (this.ussdDriverList == null || !this.ussdDriverList.equals(other.ussdDriverList))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 53 * hash + (this.driverhost != null ? this.driverhost.hashCode() : 0);
        hash = 53 * hash + (this.driverport != null ? this.driverport.hashCode() : 0);
        hash = 53 * hash + (this.ussdDriverList != null ? this.ussdDriverList.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "UssdDriverHost[id=" + id + "]";
    }
}
