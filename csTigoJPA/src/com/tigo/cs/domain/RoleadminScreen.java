package com.tigo.cs.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "roleadmin_screen")
public class RoleadminScreen implements Serializable {

    private static final long serialVersionUID = -5310157889386964647L;
    @EmbeddedId
    protected RoleadminScreenPK roleadminScreenPK;
    @Basic(optional = false)
    @Column(name = "create_chr")
    private Boolean createChr;
    @Basic(optional = false)
    @Column(name = "read_chr")
    private Boolean readChr;
    @Basic(optional = false)
    @Column(name = "update_chr")
    private Boolean updateChr;
    @Basic(optional = false)
    @Column(name = "delete_chr")
    private Boolean deleteChr;
    @JoinColumn(name = "cod_screen", referencedColumnName = "screen_cod",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Screen screen;
    @JoinColumn(name = "cod_roleadmin", referencedColumnName = "roleadmin_cod",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Roleadmin roleadmin;

    public RoleadminScreen() {
    }

    public RoleadminScreenPK getRoleadminScreenPK() {
        return roleadminScreenPK;
    }

    public void setRoleadminScreenPK(RoleadminScreenPK roleadminScreenPK) {
        this.roleadminScreenPK = roleadminScreenPK;
    }

    public Boolean getCreateChr() {
        return createChr;
    }

    public void setCreateChr(Boolean createChr) {
        this.createChr = createChr;
    }

    public Boolean getReadChr() {
        return readChr;
    }

    public void setReadChr(Boolean readChr) {
        this.readChr = readChr;
    }

    public Boolean getUpdateChr() {
        return updateChr;
    }

    public void setUpdateChr(Boolean updateChr) {
        this.updateChr = updateChr;
    }

    public Boolean getDeleteChr() {
        return deleteChr;
    }

    public void setDeleteChr(Boolean deleteChr) {
        this.deleteChr = deleteChr;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public Roleadmin getRoleadmin() {
        return roleadmin;
    }

    public void setRoleadmin(Roleadmin roleadmin) {
        this.roleadmin = roleadmin;
    }

    @PrePersist
    protected void prePersist() {
        this.setRoleadminScreenPK(new RoleadminScreenPK(roleadmin.getRoleadminCod(), screen.getScreenCod()));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RoleadminScreen other = (RoleadminScreen) obj;
        if (this.roleadminScreenPK != other.roleadminScreenPK
            && (this.roleadminScreenPK == null || !this.roleadminScreenPK.equals(other.roleadminScreenPK))) {
            return false;
        }
        if ((this.createChr == null) ? (other.createChr != null) : !this.createChr.equals(other.createChr)) {
            return false;
        }
        if ((this.readChr == null) ? (other.readChr != null) : !this.readChr.equals(other.readChr)) {
            return false;
        }
        if ((this.updateChr == null) ? (other.updateChr != null) : !this.updateChr.equals(other.updateChr)) {
            return false;
        }
        if ((this.deleteChr == null) ? (other.deleteChr != null) : !this.deleteChr.equals(other.deleteChr)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97
            * hash
            + (this.roleadminScreenPK != null ? this.roleadminScreenPK.hashCode() : 0);
        hash = 97 * hash
            + (this.createChr != null ? this.createChr.hashCode() : 0);
        hash = 97 * hash + (this.readChr != null ? this.readChr.hashCode() : 0);
        hash = 97 * hash
            + (this.updateChr != null ? this.updateChr.hashCode() : 0);
        hash = 97 * hash
            + (this.deleteChr != null ? this.deleteChr.hashCode() : 0);

        return hash;
    }

    @Override
    public String toString() {
        return "RoleadminScreen[roleadminScreenPK=" + roleadminScreenPK + "]";
    }
}
