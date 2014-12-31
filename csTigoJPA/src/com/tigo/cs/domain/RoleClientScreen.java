package com.tigo.cs.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "role_client_screen")
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries({ @NamedQuery(name = "RoleClientScreen.findAll",
        query = "SELECT r FROM RoleClientScreen r") })
public class RoleClientScreen implements Serializable {

    private static final long serialVersionUID = 2884005812436992794L;
    @EmbeddedId
    protected RoleClientScreenPK roleClientScreenPK;
    @Basic(optional = false)
    @Column(name = "ACCESSIBLE")
    private Boolean accessible;
    @JoinColumn(name = "COD_SCREENCLIENT",
            referencedColumnName = "SCREENCLIENT_COD", insertable = false,
            updatable = false)
    @ManyToOne(optional = false)
    private Screenclient screenclient;
    @JoinColumn(name = "COD_ROLE_CLIENT",
            referencedColumnName = "ROLE_CLIENT_COD", insertable = false,
            updatable = false)
    @ManyToOne(optional = false)
    private RoleClient roleClient;

    public RoleClientScreen() {
    }

    public RoleClientScreen(RoleClientScreenPK roleClientScreenPK) {
        this.roleClientScreenPK = roleClientScreenPK;
    }

    public RoleClientScreen(RoleClientScreenPK roleClientScreenPK,
            Boolean accessible) {
        this.roleClientScreenPK = roleClientScreenPK;
        this.accessible = accessible;
    }

    public RoleClientScreen(Long codRoleClient, Long codScreenclient) {
        this.roleClientScreenPK = new RoleClientScreenPK(codRoleClient, codScreenclient);
    }

    public RoleClientScreenPK getRoleClientScreenPK() {
        return roleClientScreenPK;
    }

    public void setRoleClientScreenPK(RoleClientScreenPK roleClientScreenPK) {
        this.roleClientScreenPK = roleClientScreenPK;
    }

    public Boolean getAccessible() {
        return accessible;
    }

    public void setAccessible(Boolean accessible) {
        this.accessible = accessible;
    }

    public Screenclient getScreenclient() {
        return screenclient;
    }

    public void setScreenclient(Screenclient screenclient) {
        this.screenclient = screenclient;
    }

    public RoleClient getRoleClient() {
        return roleClient;
    }

    public void setRoleClient(RoleClient roleClient) {
        this.roleClient = roleClient;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RoleClientScreen other = (RoleClientScreen) obj;
        if (this.roleClientScreenPK != other.roleClientScreenPK
            && (this.roleClientScreenPK == null || !this.roleClientScreenPK.equals(other.roleClientScreenPK))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89
            * hash
            + (this.roleClientScreenPK != null ? this.roleClientScreenPK.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "RoleClientScreen[roleClientScreenPK=" + roleClientScreenPK
            + "]";
    }
}
