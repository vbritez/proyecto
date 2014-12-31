package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "route_userphone")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class RouteUserphone implements Serializable {

    private static final long serialVersionUID = 4509046870384836249L;
    @EmbeddedId
    private RouteUserphonePK routeUserphonePK;
    @Column(name = "ACTUAL_POSITION_NUMBER", precision = 10)
    private Integer actualPositionNumber;
    @Column(name = "ACTUAL_POSITION_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualPositionTime;
    // bi-directional many-to-one association to Route
    @ManyToOne
    @JoinColumn(name = "COD_ROUTE", nullable = false, insertable = false,
            updatable = false)
    private Route route;
    // bi-directional many-to-one association to Userphone
    @ManyToOne
    @JoinColumn(name = "COD_USERPHONE", nullable = false, insertable = false,
            updatable = false)
    private Userphone userphone;

    public RouteUserphone() {
    }

    public RouteUserphone(Long codRoute, Long codUserphone) {
        this.routeUserphonePK = new RouteUserphonePK(codRoute, codUserphone);
    }

    public RouteUserphonePK getRouteUserphonePK() {
        return routeUserphonePK;
    }

    public void setRouteUserphonePK(RouteUserphonePK routeUserphonePK) {
        this.routeUserphonePK = routeUserphonePK;
    }

    public Integer getActualPositionNumber() {
        return this.actualPositionNumber;
    }

    public void setActualPositionNumber(Integer actualPositionNumber) {
        this.actualPositionNumber = actualPositionNumber;
    }

    public Date getActualPositionTime() {
        return this.actualPositionTime;
    }

    public void setActualPositionTime(Date actualPositionTime) {
        this.actualPositionTime = actualPositionTime;
    }

    public Route getRoute() {
        return this.route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Userphone getUserphone() {
        return this.userphone;
    }

    public void setUserphone(Userphone userphone) {
        this.userphone = userphone;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RouteUserphone other = (RouteUserphone) obj;
        if (this.routeUserphonePK != other.routeUserphonePK
            && (this.routeUserphonePK == null || !this.routeUserphonePK.equals(other.routeUserphonePK))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89
            * hash
            + (this.routeUserphonePK != null ? this.routeUserphonePK.hashCode() : 0);

        return hash;
    }

    @Override
    public String toString() {
        return "RouteUserphone[routeUserphonePK=" + routeUserphonePK + "]";
    }
}
