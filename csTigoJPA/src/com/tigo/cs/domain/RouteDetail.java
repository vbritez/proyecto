package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.tigo.cs.commons.jpa.PrimarySortedField;

@Entity
@Table(name = "route_detail")
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RouteDetail implements Serializable {

    private static final long serialVersionUID = -8222280383443893449L;
    @Id
    @SequenceGenerator(name = "ROUTE_DETAIL_ROUTEDETAILCOD_GENERATOR",
            sequenceName = "ROUTE_DETAIL_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "ROUTE_DETAIL_ROUTEDETAILCOD_GENERATOR")
    @Column(name = "ROUTE_DETAIL_COD", unique = true, nullable = false,
            precision = 19)
    private Long routeDetailCod;
    @Column(name = "END_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
    @Column(name = "POSITION_NUMBER", nullable = false, precision = 22)
    @PrimarySortedField
    @OrderBy
    private Integer positionNumber;
    @Column(name = "START_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Column(name = "DESCRIPTION")
    private String description;
    // bi-directional many-to-one association to MapMark
    @ManyToOne
    @JoinColumn(name = "COD_MAP_MARK", nullable = false)
    private MapMark mapMark;
    // bi-directional many-to-one association to Route
    @ManyToOne
    @JoinColumn(name = "COD_ROUTE", nullable = false)
    private Route route;
    // bi-directional many-to-one association to RouteDetailUserphone
    @OneToMany(mappedBy = "routeDetail")
    private List<RouteDetailUserphone> routeDetailUserphones;

    public RouteDetail() {
    }

    public Long getRouteDetailCod() {
        return this.routeDetailCod;
    }

    public void setRouteDetailCod(Long routeDetailCod) {
        this.routeDetailCod = routeDetailCod;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getPositionNumber() {
        return this.positionNumber;
    }

    public void setPositionNumber(Integer positionNumber) {
        this.positionNumber = positionNumber;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MapMark getMapMark() {
        return this.mapMark;
    }

    public void setMapMark(MapMark mapMark) {
        this.mapMark = mapMark;
    }

    public Route getRoute() {
        return this.route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public List<RouteDetailUserphone> getRouteDetailUserphones() {
        return this.routeDetailUserphones;
    }

    public void setRouteDetailUserphones(List<RouteDetailUserphone> routeDetailUserphones) {
        this.routeDetailUserphones = routeDetailUserphones;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RouteDetail other = (RouteDetail) obj;
        if (this.routeDetailCod != other.routeDetailCod
            && (this.routeDetailCod == null || !this.routeDetailCod.equals(other.routeDetailCod))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37
            * hash
            + (this.routeDetailCod != null ? this.routeDetailCod.hashCode() : 0);

        return hash;
    }

    @Override
    public String toString() {
        return "RouteDetail[routeDetailCod=" + routeDetailCod + "]";
    }
}
