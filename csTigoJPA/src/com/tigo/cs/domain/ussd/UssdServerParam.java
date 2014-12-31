package com.tigo.cs.domain.ussd;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Javier Kovacs
 * @version $Id$
 */
@Entity
@Table(name = "ussd_server_param")
public class UssdServerParam implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "PARAMETERVALUE")
    private String parametervalue;
    @JoinColumn(name = "USSDPARAMETER_ID", referencedColumnName = "ID")
    @ManyToOne
    private UssdParam ussdParam;
    @JoinColumn(name = "SERVER_ID", referencedColumnName = "ID")
    @ManyToOne
    private UssdGwUssd ussdGwUssd;

    public UssdServerParam() {
    }

    public UssdServerParam(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParametervalue() {
        return parametervalue;
    }

    public void setParametervalue(String parametervalue) {
        this.parametervalue = parametervalue;
    }

    public UssdParam getUssdParam() {
        return ussdParam;
    }

    public void setUssdParam(UssdParam ussdParam) {
        this.ussdParam = ussdParam;
    }

    public UssdGwUssd getUssdGwUssd() {
        return ussdGwUssd;
    }

    public void setUssdGwUssd(UssdGwUssd ussdGwUssd) {
        this.ussdGwUssd = ussdGwUssd;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UssdServerParam other = (UssdServerParam) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.parametervalue == null) ? (other.parametervalue != null) : !this.parametervalue.equals(other.parametervalue)) {
            return false;
        }
        if (this.ussdParam != other.ussdParam && (this.ussdParam == null || !this.ussdParam.equals(other.ussdParam))) {
            return false;
        }
        if (this.ussdGwUssd != other.ussdGwUssd && (this.ussdGwUssd == null || !this.ussdGwUssd.equals(other.ussdGwUssd))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.parametervalue != null ? this.parametervalue.hashCode() : 0);
        hash = 97 * hash + (this.ussdParam != null ? this.ussdParam.hashCode() : 0);
        hash = 97 * hash + (this.ussdGwUssd != null ? this.ussdGwUssd.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "UssdServerParam[id=" + id + "]";
    }
}
