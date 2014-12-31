package com.tigo.cs.api.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.tigo.cs.api.annotations.MetaColumn;
import com.tigo.cs.api.enumerates.MetaNames;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AttendanceService extends BaseServiceBean {

    private static final long serialVersionUID = 9109488965682831737L;
    @MetaColumn(metaname = MetaNames.EMPLOYEE)
    private String employeeCode;
    private String observation;
    private String delayMinutes;
    private String extraMinutes;
    private Long delayMilliseconds;
    private Long extraMilliseconds;
    private Integer day;
    private String checkinTime;
    private String checkoutTime;
    private Integer tolerance;
    private String codMetadataHorary;
    private Long hoursWorkMilliseconds;

    public AttendanceService() {
        super();
        setServiceCod(11);
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getDelayMinutes() {
        return delayMinutes;
    }

    public void setDelayMinutes(String delayMinutes) {
        this.delayMinutes = delayMinutes;
    }

    public String getExtraMinutes() {
        return extraMinutes;
    }

    public void setExtraMinutes(String extraMinutes) {
        this.extraMinutes = extraMinutes;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public Long getDelayMilliseconds() {
        return delayMilliseconds;
    }

    public void setDelayMilliseconds(Long delayMilliseconds) {
        this.delayMilliseconds = delayMilliseconds;
    }

    public Long getExtraMilliseconds() {
        return extraMilliseconds;
    }

    public void setExtraMilliseconds(Long extraMilliseconds) {
        this.extraMilliseconds = extraMilliseconds;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public String getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(String checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(String checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public Integer getTolerance() {
        return tolerance;
    }

    public void setTolerance(Integer tolerance) {
        this.tolerance = tolerance;
    }

    public String getCodMetadataHorary() {
        return codMetadataHorary;
    }

    public void setCodMetadataHorary(String codMetadataHorary) {
        this.codMetadataHorary = codMetadataHorary;
    }

    public Long getHoursWorkMilliseconds() {
        return hoursWorkMilliseconds;
    }

    public void setHoursWorkMilliseconds(Long hoursWorkMilliseconds) {
        this.hoursWorkMilliseconds = hoursWorkMilliseconds;
    }

}
