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
@Table(name = "ussd_gw_ussd")
public class UssdGwUssd implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "GWADDRESS")
    private String gwaddress;
    @Column(name = "GWPORT")
    private Long gwport;
    @OneToMany(mappedBy = "ussdGwUssd")
    private List<UssdServerParam> ussdServerParamList;
    @JoinColumn(name = "USSDAPPLICATION_ID", referencedColumnName = "ID")
    @ManyToOne
    private UssdApplicationServer ussdApplicationServer;

    public UssdGwUssd() {
    }

    public UssdGwUssd(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGwaddress() {
        return gwaddress;
    }

    public void setGwaddress(String gwaddress) {
        this.gwaddress = gwaddress;
    }

    public Long getGwport() {
        return gwport;
    }

    public void setGwport(Long gwport) {
        this.gwport = gwport;
    }

    public List<UssdServerParam> getUssdServerParamList() {
        return ussdServerParamList;
    }

    public void setUssdServerParamList(List<UssdServerParam> ussdServerParamList) {
        this.ussdServerParamList = ussdServerParamList;
    }

    public UssdApplicationServer getUssdApplicationServer() {
        return ussdApplicationServer;
    }

    public void setUssdApplicationServer(UssdApplicationServer ussdApplicationServer) {
        this.ussdApplicationServer = ussdApplicationServer;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UssdGwUssd other = (UssdGwUssd) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.gwaddress == null) ? (other.gwaddress != null) : !this.gwaddress.equals(other.gwaddress)) {
            return false;
        }
        if (this.gwport != other.gwport && (this.gwport == null || !this.gwport.equals(other.gwport))) {
            return false;
        }
        if (this.ussdServerParamList != other.ussdServerParamList && (this.ussdServerParamList == null || !this.ussdServerParamList.equals(other.ussdServerParamList))) {
            return false;
        }
        if (this.ussdApplicationServer != other.ussdApplicationServer && (this.ussdApplicationServer == null || !this.ussdApplicationServer.equals(other.ussdApplicationServer))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.gwaddress != null ? this.gwaddress.hashCode() : 0);
        hash = 97 * hash + (this.gwport != null ? this.gwport.hashCode() : 0);
        hash = 97 * hash + (this.ussdServerParamList != null ? this.ussdServerParamList.hashCode() : 0);
        hash = 97 * hash + (this.ussdApplicationServer != null ? this.ussdApplicationServer.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "com.tigo.cs.domain.ussd.UssdGwUssd[id=" + id + "]";
    }
}
