package com.tigo.cs.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "route_type_conf")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class RouteTypeConf implements Serializable {

    private static final long serialVersionUID = 1679674904685899902L;
    @EmbeddedId
    private RouteTypeConfPK routeTypeConfPK;
    @Column(name = "VAL", nullable = false, length = 50)
    private String value;
    // bi-directional many-to-one association to Client
    @ManyToOne
    @JoinColumn(name = "COD_CLIENT", nullable = false, insertable = false,
            updatable = false)
    private Client client;
    // bi-directional many-to-one association to RouteType
    @ManyToOne
    @JoinColumn(name = "COD_ROUTE_TYPE", nullable = false, insertable = false,
            updatable = false)
    private RouteType routeType;

    public RouteTypeConf() {
    }

    public RouteTypeConfPK getRouteTypeConfPK() {
        return routeTypeConfPK;
    }

    public void setRouteTypeConfPK(RouteTypeConfPK routeTypeConfPK) {
        this.routeTypeConfPK = routeTypeConfPK;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public RouteType getRouteType() {
        return this.routeType;
    }

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RouteTypeConf other = (RouteTypeConf) obj;
        if (this.routeTypeConfPK != other.routeTypeConfPK
            && (this.routeTypeConfPK == null || !this.routeTypeConfPK.equals(other.routeTypeConfPK))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23
            * hash
            + (this.routeTypeConfPK != null ? this.routeTypeConfPK.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "RouteTypeConf[routeTypeConfPK=" + routeTypeConfPK + "]";
    }
}
