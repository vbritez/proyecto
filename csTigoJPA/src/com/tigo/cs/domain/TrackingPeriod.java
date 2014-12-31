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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "TRACKING_PERIOD")
@NamedQueries({ @NamedQuery(
        name = "TrackingPeriod.getTrackingPeriodByStatus",
        query = "SELECT tp FROM TrackingPeriod tp WHERE tp.status = :status AND tp.client.enabledChr = true") })
public class TrackingPeriod implements Serializable {

    private static final long serialVersionUID = 8099227508827706693L;

    @Id
    @SequenceGenerator(name = "TRACKING_PERIOD_TRACKINGPERIODCOD_GENERATOR",
            sequenceName = "TRACKING_PERIOD_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "TRACKING_PERIOD_TRACKINGPERIODCOD_GENERATOR")
    @Column(name = "TRACKING_PERIOD_COD")
    private Long trackingPeriodCod;

    @Column(name = "DATE_FROM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFrom;

    @Column(name = "DATE_TO")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTo;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "STATUS", nullable = false, precision = 19)
    private Boolean status;

    // bi-directional many-to-one association to TrackingConfiguration
    @OneToMany(mappedBy = "trackingPeriod")
    private List<TrackingConfiguration> trackingConfigurations;

    // bi-directional many-to-many association to Userphone
    @ManyToMany
    @JoinTable(name = "TRACKING_PERIOD_USERPHONE", joinColumns = { @JoinColumn(
            name = "COD_TRACKING_PERIOD") },
            inverseJoinColumns = { @JoinColumn(name = "COD_USERPHONE") })
    private List<Userphone> userphones;

    @ManyToOne(optional = true)
    @JoinColumn(name = "COD_CLIENT")
    private Client client;

    public TrackingPeriod() {
    }

    public Long getTrackingPeriodCod() {
        return this.trackingPeriodCod;
    }

    public void setTrackingPeriodCod(Long trackingPeriodCod) {
        this.trackingPeriodCod = trackingPeriodCod;
    }

    public Date getDateFrom() {
        return this.dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return this.dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<TrackingConfiguration> getTrackingConfigurations() {
        return this.trackingConfigurations;
    }

    public void setTrackingConfigurations(List<TrackingConfiguration> trackingConfigurations) {
        this.trackingConfigurations = trackingConfigurations;
    }

    public List<Userphone> getUserphones() {
        return this.userphones;
    }

    public void setUserphones(List<Userphone> userphones) {
        this.userphones = userphones;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return description;
    }

}