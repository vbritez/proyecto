package com.tigo.cs.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Miguel Zorrilla
 * @version $Id$
 */
@Embeddable
public class RoleadminScreenPK implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4133127477314681512L;
	@Basic(optional = false)
    @Column(name = "cod_roleadmin", unique = true, nullable = false)
    private long codRoleadmin;
    @Basic(optional = false)
    @Column(name = "cod_screen", unique = true, nullable = false)
    private long codScreen;

    public RoleadminScreenPK() {
    }

    public RoleadminScreenPK(long codRoleadmin, long codScreen) {
        this.codRoleadmin = codRoleadmin;
        this.codScreen = codScreen;
    }

    public long getCodRoleadmin() {
        return codRoleadmin;
    }

    public void setCodRoleadmin(long codRoleadmin) {
        this.codRoleadmin = codRoleadmin;
    }

    public long getCodScreen() {
        return codScreen;
    }

    public void setCodScreen(long codScreen) {
        this.codScreen = codScreen;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RoleadminScreenPK other = (RoleadminScreenPK) obj;
        if (this.codRoleadmin != other.codRoleadmin) {
            return false;
        }
        if (this.codScreen != other.codScreen) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (int) (this.codRoleadmin ^ (this.codRoleadmin >>> 32));
        hash = 97 * hash + (int) (this.codScreen ^ (this.codScreen >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "RoleadminScreenPK[codRoleadmin=" + codRoleadmin + ", codScreen=" + codScreen + "]";
    }
}
