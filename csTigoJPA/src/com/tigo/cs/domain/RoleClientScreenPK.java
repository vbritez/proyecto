package com.tigo.cs.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class RoleClientScreenPK implements Serializable {

    private static final long serialVersionUID = -6358994724697225592L;
    @Basic(optional = false)
    @Column(name = "COD_ROLE_CLIENT")
    private Long codRoleClient;
    @Basic(optional = false)
    @Column(name = "COD_SCREENCLIENT")
    private Long codScreenclient;

    public RoleClientScreenPK() {
    }

    public RoleClientScreenPK(Long codRoleClient, Long codScreenclient) {
        this.codRoleClient = codRoleClient;
        this.codScreenclient = codScreenclient;
    }

    public Long getCodRoleClient() {
        return codRoleClient;
    }

    public void setCodRoleClient(Long codRoleClient) {
        this.codRoleClient = codRoleClient;
    }

    public Long getCodScreenclient() {
        return codScreenclient;
    }

    public void setCodScreenclient(Long codScreenclient) {
        this.codScreenclient = codScreenclient;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RoleClientScreenPK other = (RoleClientScreenPK) obj;
        if (this.codRoleClient != other.codRoleClient
            && (this.codRoleClient == null || !this.codRoleClient.equals(other.codRoleClient))) {
            return false;
        }
        if (this.codScreenclient != other.codScreenclient
            && (this.codScreenclient == null || !this.codScreenclient.equals(other.codScreenclient))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash
            + (this.codRoleClient != null ? this.codRoleClient.hashCode() : 0);
        hash = 17
            * hash
            + (this.codScreenclient != null ? this.codScreenclient.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "RoleClientScreenPK[codRoleClient=" + codRoleClient
            + ", codScreenclient=" + codScreenclient + "]";
    }
}
