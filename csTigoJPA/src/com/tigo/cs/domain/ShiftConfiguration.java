package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "SHIFT_CONFIGURATION")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class ShiftConfiguration implements Serializable {

    private static final long serialVersionUID = -6109018120696496010L;

    @EmbeddedId
    private ShiftConfigurationPK shiftConfigurationPK;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "DAYS", nullable = false, length = 50)
    private String days;

    @Column(nullable = false, precision = 19)
    private Long duration;

    @Column(name = "START_TIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "NEXT_EXECUTION_TOLERANCE_TIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextExecutionToleranceTime;

    @Column(name = "NEXT_EXECUTION_TIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextExecutionTime;

    @Column(name = "END_TIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    @Column(nullable = false)
    private Boolean status;

    @Column(nullable = false)
    private Boolean running;

    @Column(name = "NEXT_EXECUTION_DAY", precision = 19)
    private Long nextExecutionDay;

    @Column(name = "INTERVAL_TIME", nullable = false, precision = 19)
    private Long intervalTime;

    @Column(name = "TOLERANCE_TIME", nullable = false, precision = 19)
    private Long toleranceTime;

    @Column(name = "START_MINUTES", nullable = false, precision = 19)
    private Long startMinutes;

    @Column(length = 255)
    private String supervisors;

    // bi-directional many-to-one association to ShiftPeriod
    @ManyToOne
    @JoinColumn(name = "COD_SHIFT_PERIOD", nullable = false,
            insertable = false, updatable = false)
    private ShiftPeriod shiftPeriod;

    public ShiftConfiguration() {
    }

    public ShiftConfigurationPK getShiftConfigurationPK() {
        return this.shiftConfigurationPK;
    }

    public void setShiftConfigurationPK(ShiftConfigurationPK pk) {
        this.shiftConfigurationPK = pk;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDays() {
        return this.days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public Long getDuration() {
        return this.duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
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

    public ShiftPeriod getShiftPeriod() {
        return this.shiftPeriod;
    }

    public void setShiftPeriod(ShiftPeriod shiftPeriod) {
        this.shiftPeriod = shiftPeriod;
    }

    public Long getNextExecutionDay() {
        return nextExecutionDay;
    }

    public void setNextExecutionDay(Long nextExecutionDay) {
        this.nextExecutionDay = nextExecutionDay;
    }

    public Long getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(Long intervalTime) {
        this.intervalTime = intervalTime;
    }

    public Long getToleranceTime() {
        return toleranceTime;
    }

    public void setToleranceTime(Long toleranceTime) {
        this.toleranceTime = toleranceTime;
    }

    public String getSupervisors() {
        return supervisors;
    }

    public void setSupervisors(String supervisors) {
        this.supervisors = supervisors;
    }

    public Long getStartMinutes() {
        return startMinutes;
    }

    public void setStartMinutes(Long startMinutes) {
        this.startMinutes = startMinutes;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getNextExecutionToleranceTime() {
        return nextExecutionToleranceTime;
    }

    public void setNextExecutionToleranceTime(Date nextExecutionToleranceTime) {
        this.nextExecutionToleranceTime = nextExecutionToleranceTime;
    }

    public Date getNextExecutionTime() {
        return nextExecutionTime;
    }

    public void setNextExecutionTime(Date nextExecutionTime) {
        this.nextExecutionTime = nextExecutionTime;
    }

    public Boolean getRunning() {
        return running;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    @PrePersist
    public void prePersist() {
        if (intervalTime <= 0) {
            intervalTime = 30L;
        }
    }

    @PostUpdate
    public void postUpdate() {
        if (intervalTime <= 0) {
            intervalTime = 30L;
        }
    }
}