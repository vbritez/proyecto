package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.tigo.cs.commons.jpa.PrimarySortedField;

@Entity
@Table(name = "route")
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Route implements Serializable {

    private static final long serialVersionUID = 5524820628226348573L;
    @Id
    @SequenceGenerator(name = "ROUTE_ROUTECOD_GENERATOR",
            sequenceName = "ROUTE_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "ROUTE_ROUTECOD_GENERATOR")
    @Column(name = "ROUTE_COD", unique = true, nullable = false, precision = 19)
    @PrimarySortedField
    private Long routeCod;
    @Column(nullable = false, length = 75)
    @NotEmpty(message = "{entity.route.constraint.description.NotEmpty}")
    private String description;
    @Temporal(TemporalType.DATE)
    @Column(name = "FROM_DATE", nullable = false)
    @NotNull(message = "{entity.route.constraint.fromDate.NotNull}")
    private Date fromDate;
    @Column(name = "OFFROUTE_MARKOTION", nullable = false, precision = 10)
    @NotNull(message = "{entity.route.constraint.offrouteMarkoption.NotNull}")
    private Boolean offrouteMarkotion;
    @Temporal(TemporalType.DATE)
    @Column(name = "TO_DATE", nullable = false)
    @NotNull(message = "{entity.route.constraint.toDate.NotNull}")
    private Date toDate;
    // bi-directional many-to-one association to RouteType
    @ManyToOne
    @JoinColumn(name = "COD_ROUTE_TYPE")
    @NotNull(message = "{entity.route.constraint.routeType.notNullMessage}")
    private RouteType routeType;
    // bi-directional many-to-one association to RouteDetail
    @OneToMany(mappedBy = "route")
    private List<RouteDetail> routeDetails;
    // bi-directional many-to-one association to RouteUserphone
    @OneToMany(
            mappedBy = "route",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private List<RouteUserphone> routeUserphones;
    @JoinColumn(name = "cod_client", referencedColumnName = "client_cod")
    @ManyToOne(optional = false)
    private Client client;

    public Route() {
    }

    public Long getRouteCod() {
        return this.routeCod;
    }

    public void setRouteCod(Long routeCod) {
        this.routeCod = routeCod;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getFromDate() {
        return this.fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Boolean getOffrouteMarkotion() {
        return this.offrouteMarkotion;
    }

    public void setOffrouteMarkotion(Boolean offrouteMarkotion) {
        this.offrouteMarkotion = offrouteMarkotion;
    }

    public Date getToDate() {
        return this.toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public RouteType getRouteType() {
        return this.routeType;
    }

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }

    public List<RouteDetail> getRouteDetails() {
        return this.routeDetails;
    }

    public void setRouteDetails(List<RouteDetail> routeDetails) {
        this.routeDetails = routeDetails;
    }

    public List<RouteUserphone> getRouteUserphones() {
        return this.routeUserphones;
    }

    public void setRouteUserphones(List<RouteUserphone> routeUserphones) {
        this.routeUserphones = routeUserphones;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Route other = (Route) obj;
        if (this.routeCod != other.routeCod
            && (this.routeCod == null || !this.routeCod.equals(other.routeCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash
            + (this.routeCod != null ? this.routeCod.hashCode() : 0);

        return hash;
    }

    @Override
    public String toString() {
        return "Route[routeCod=" + routeCod + "]";
    }
}
