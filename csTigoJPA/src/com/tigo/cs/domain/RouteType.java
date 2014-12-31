package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.PrimarySortedField;

@Entity
@Table(name = "route_type")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class RouteType implements Serializable {

    private static final long serialVersionUID = -560339445361169886L;
    @Id
    @SequenceGenerator(name = "ROUTE_TYPE_ROUTETYPECOD_GENERATOR",
            sequenceName = "ROUTE_TYPE_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "ROUTE_TYPE_ROUTETYPECOD_GENERATOR")
    @Column(name = "ROUTE_TYPE_COD", unique = true, nullable = false,
            precision = 19)
    @PrimarySortedField
    private Long routeTypeCod;
    @Column(nullable = false, length = 50)
    private String description;
    // bi-directional many-to-one association to Route
    @OneToMany(mappedBy = "routeType")
    private List<Route> routes;
    // bi-directional many-to-one association to RouteTypeConf
    @OneToMany(mappedBy = "routeType")
    private Set<RouteTypeConf> routeTypeConfs;

    public RouteType() {
    }

    public Long getRouteTypeCod() {
        return this.routeTypeCod;
    }

    public void setRouteTypeCod(Long routeTypeCod) {
        this.routeTypeCod = routeTypeCod;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Route> getRoutes() {
        return this.routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public Set<RouteTypeConf> getRouteTypeConfs() {
        return this.routeTypeConfs;
    }

    public void setRouteTypeConfs(Set<RouteTypeConf> routeTypeConfs) {
        this.routeTypeConfs = routeTypeConfs;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RouteType other = (RouteType) obj;
        if (this.routeTypeCod != other.routeTypeCod
            && (this.routeTypeCod == null || !this.routeTypeCod.equals(other.routeTypeCod))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash
            + (this.routeTypeCod != null ? this.routeTypeCod.hashCode() : 0);

        return hash;
    }

    @Override
    public String toString() {
        return "RouteType[routeTypeCod=" + routeTypeCod + "]";
    }
}
