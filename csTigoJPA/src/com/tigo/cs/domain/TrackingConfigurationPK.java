package com.tigo.cs.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;

@Embeddable
public class TrackingConfigurationPK implements Serializable {

    private static final long serialVersionUID = 2368905094911082762L;

    @SequenceGenerator(name = "TrackingConfigurationGen",
            sequenceName = "TRACKING_CONFIGURATION_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "TrackingConfigurationGen")
    @Column(name = "TRACKING_CONFIGURATION_COD", unique = true,
            nullable = false, precision = 19)
    private Long trackingConfigurationCod;

    @Column(name = "COD_TRACKING_PERIOD")
    private Long codTrackingPeriod;

    public TrackingConfigurationPK() {
    }

    public Long getTrackingConfigurationCod() {
        return this.trackingConfigurationCod;
    }

    public void setTrackingConfigurationCod(long trackingConfigurationCod) {
        this.trackingConfigurationCod = trackingConfigurationCod;
    }

    public Long getCodTrackingPeriod() {
        return this.codTrackingPeriod;
    }

    public void setCodTrackingPeriod(long codTrackingPeriod) {
        this.codTrackingPeriod = codTrackingPeriod;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TrackingConfigurationPK)) {
            return false;
        }
        TrackingConfigurationPK castOther = (TrackingConfigurationPK) other;
        return (this.trackingConfigurationCod.longValue() == castOther.trackingConfigurationCod.longValue())
            && (this.codTrackingPeriod.longValue() == castOther.codTrackingPeriod.longValue());

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash
            * prime
            + ((int) (this.trackingConfigurationCod ^ (this.trackingConfigurationCod >>> 32)));
        hash = hash
            * prime
            + ((int) (this.codTrackingPeriod ^ (this.codTrackingPeriod >>> 32)));

        return hash;
    }

    @Override
    public String toString() {
        return "TrackingConfigurationPK [trackingConfigurationCod="
            + trackingConfigurationCod + ", codTrackingPeriod="
            + codTrackingPeriod + "]";
    }

}