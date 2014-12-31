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
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "TRACKING_CONFIGURATION")
public class TrackingConfiguration implements Serializable {

    private static final long serialVersionUID = 3132633279933184939L;

    @EmbeddedId
    private TrackingConfigurationPK id;

    @Column(name = "DAYS")
    private String days;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, precision = 19)
    private Long duration;

    @Column(name = "END_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    @Column(name = "INTERVAL_TIME")
    private Long intervalTime;

    @Column(name = "NEXT_EXECUTION_DAY")
    private Long nextExecutionDay;

    @Column(name = "NEXT_EXECUTION_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextExecutionTime;

    @Column(name = "NEXT_EXECUTION_TOLERANCE_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextExecutionToleranceTime;

    private Boolean running;

    @Column(name = "START_MINUTES")
    private Long startMinutes;

    @Column(name = "START_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    private Boolean status;

    @Column(name = "TOLERANCE_TIME")
    private Long toleranceTime;

    // bi-directional many-to-one association to TrackingPeriod
    @ManyToOne
    @JoinColumn(name = "COD_TRACKING_PERIOD", nullable = false,
            insertable = false, updatable = false)
    private TrackingPeriod trackingPeriod;

    public TrackingConfiguration() {
    }

    public TrackingConfigurationPK getId() {
        return this.id;
    }

    public void setId(TrackingConfigurationPK id) {
        this.id = id;
    }

    public String getDays() {
        return this.days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDuration() {
        return this.duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getIntervalTime() {
        return this.intervalTime;
    }

    public void setIntervalTime(Long intervalTime) {
        this.intervalTime = intervalTime;
    }

    public Long getNextExecutionDay() {
        return this.nextExecutionDay;
    }

    public void setNextExecutionDay(Long nextExecutionDay) {
        this.nextExecutionDay = nextExecutionDay;
    }

    public Date getNextExecutionTime() {
        return this.nextExecutionTime;
    }

    public void setNextExecutionTime(Date nextExecutionTime) {
        this.nextExecutionTime = nextExecutionTime;
    }

    public Date getNextExecutionToleranceTime() {
        return this.nextExecutionToleranceTime;
    }

    public void setNextExecutionToleranceTime(Date nextExecutionToleranceTime) {
        this.nextExecutionToleranceTime = nextExecutionToleranceTime;
    }

    public Boolean getRunning() {
        return this.running;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public Long getStartMinutes() {
        return this.startMinutes;
    }

    public void setStartMinutes(Long startMinutes) {
        this.startMinutes = startMinutes;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getToleranceTime() {
        return this.toleranceTime;
    }

    public void setToleranceTime(Long toleranceTime) {
        this.toleranceTime = toleranceTime;
    }

    public TrackingPeriod getTrackingPeriod() {
        return this.trackingPeriod;
    }

    public void setTrackingPeriod(TrackingPeriod trackingPeriod) {
        this.trackingPeriod = trackingPeriod;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TrackingConfiguration other = (TrackingConfiguration) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}