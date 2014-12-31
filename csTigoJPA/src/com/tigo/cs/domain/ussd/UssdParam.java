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
@Table(name = "ussd_param")
public class UssdParam implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "KEYNAME")
    private String keyname;
    @OneToMany(mappedBy = "ussdParam")
    private List<UssdServerParam> ussdServerParamList;

    public UssdParam() {
    }

    public UssdParam(Long id) {
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

    public String getKeyname() {
        return keyname;
    }

    public void setKeyname(String keyname) {
        this.keyname = keyname;
    }

    public List<UssdServerParam> getUssdServerParamList() {
        return ussdServerParamList;
    }

    public void setUssdServerParamList(List<UssdServerParam> ussdServerParamList) {
        this.ussdServerParamList = ussdServerParamList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UssdParam other = (UssdParam) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.keyname == null) ? (other.keyname != null) : !this.keyname.equals(other.keyname)) {
            return false;
        }
        if (this.ussdServerParamList != other.ussdServerParamList && (this.ussdServerParamList == null || !this.ussdServerParamList.equals(other.ussdServerParamList))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 97 * hash + (this.keyname != null ? this.keyname.hashCode() : 0);
        hash = 97 * hash + (this.ussdServerParamList != null ? this.ussdServerParamList.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "com.tigo.cs.domain.ussd.UssdParam[id=" + id + "]";
    }
}
