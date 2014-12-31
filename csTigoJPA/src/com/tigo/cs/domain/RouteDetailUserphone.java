package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "route_detail_userphone")
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RouteDetailUserphone implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "ROUTE_DETAIL_USERPHONE_GENERATOR",
            sequenceName = "ROUTE_DETAIL_USERPHONE_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "ROUTE_DETAIL_USERPHONE_GENERATOR")
    @Column(name = "ROUTE_DETAIL_USERPHONE_COD", unique = true,
            nullable = false, precision = 19)
    private Long routeDetailUserphoneCod;
    @Column(name = "OFF_ROUTE", nullable = false, precision = 10)
    private Integer offRoute;
    @Column(name = "RECORD_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date recordDate;
    @Column(name = "TIME_AFTER_LAST_POINT", precision = 19)
    private Long timeAfterLastPoint;
    // bi-directional many-to-one association to RouteDetail
    @ManyToOne
    @JoinColumn(name = "COD_ROUTE_DETAIL")
    private RouteDetail routeDetail;
    // bi-directional many-to-one association to Userphone
    @ManyToOne
    @JoinColumn(name = "COD_USERPHONE")
    private Userphone userphone;

    public RouteDetailUserphone() {
    }

    public Long getRouteDetailUserphoneCod() {
        return routeDetailUserphoneCod;
    }

    public void setRouteDetailUserphoneCod(Long routeDetailUserphoneCod) {
        this.routeDetailUserphoneCod = routeDetailUserphoneCod;
    }

    public Integer getOffRoute() {
        return this.offRoute;
    }

    public void setOffRoute(Integer offRoute) {
        this.offRoute = offRoute;
    }

    public Date getRecordDate() {
        return this.recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public Long getTimeAfterLastPoint() {
        return this.timeAfterLastPoint;
    }

    public void setTimeAfterLastPoint(Long timeAfterLastPoint) {
        this.timeAfterLastPoint = timeAfterLastPoint;
    }

    public RouteDetail getRouteDetail() {
        return this.routeDetail;
    }

    public void setRouteDetail(RouteDetail routeDetail) {
        this.routeDetail = routeDetail;
    }

    public Userphone getUserphone() {
        return this.userphone;
    }

    public void setUserphone(Userphone userphone) {
        this.userphone = userphone;
    }

    @PrePersist
    public void setDate() {
        this.recordDate = new Date();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RouteDetailUserphone other = (RouteDetailUserphone) obj;
        if (this.routeDetailUserphoneCod != other.routeDetailUserphoneCod
            && (this.routeDetailUserphoneCod == null || !this.routeDetailUserphoneCod.equals(other.routeDetailUserphoneCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59
            * hash
            + (this.routeDetailUserphoneCod != null ? this.routeDetailUserphoneCod.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "RouteDetailUserphone{" + "routeDetailUserphoneCod="
            + routeDetailUserphoneCod + "offRoute=" + offRoute + "recordDate="
            + recordDate + "timeAfterLastPoint=" + timeAfterLastPoint
            + "routeDetail=" + routeDetail + "userphone=" + userphone + '}';
    }
}
