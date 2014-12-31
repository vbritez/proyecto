package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.tigo.cs.commons.jpa.PrimarySortedField;

@Entity
@Table(name = "service_value_detail")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class ServiceValueDetail implements Serializable {

    private static final long serialVersionUID = 7291265053031643844L;
    @Id
    @SequenceGenerator(name = "serviceValueDetailGen",
            sequenceName = "service_value_detail_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "serviceValueDetailGen")
    @Basic(optional = false)
    @Column(name = "servicevaluedetail_cod")
    private Long servicevaluedetailCod;
    @Column(name = "column1_chr")
    private String column1Chr;
    @Column(name = "column2_chr")
    private String column2Chr;
    @Column(name = "column3_chr")
    private String column3Chr;
    @Column(name = "column4_chr")
    private String column4Chr;
    @Column(name = "column5_chr")
    private String column5Chr;
    @Column(name = "column6_chr")
    private String column6Chr;
    @Column(name = "column7_chr")
    private String column7Chr;
    @Column(name = "column8_chr")
    private String column8Chr;
    @Column(name = "column9_chr")
    private String column9Chr;
    @Column(name = "column10_chr")
    private String column10Chr;
    @PrimarySortedField
    @Basic(optional = false)
    @Column(name = "recorddate_dat")
    @Temporal(TemporalType.TIMESTAMP)
    private Date recorddateDat;

    @Column(name = "ENABLED_CHR")
    private Boolean enabledChr;

    @JoinColumn(name = "cod_message", referencedColumnName = "message_cod")
    @ManyToOne
    private Message message;
    @JoinColumn(name = "cod_servicevalue",
            referencedColumnName = "servicevalue_cod")
    @ManyToOne(optional = false)
    private ServiceValue serviceValue;

    @Transient
    String durationChr;

    public String getDurationChr() {
        return durationChr;
    }

    public void setDurationChr(String durationChr) {
        this.durationChr = durationChr;
    }

    public ServiceValueDetail() {
    }

    public ServiceValueDetail(Long servicevaluedetailCod) {
        this.servicevaluedetailCod = servicevaluedetailCod;
    }

    public Long getServicevaluedetailCod() {
        return servicevaluedetailCod;
    }

    public void setServicevaluedetailCod(Long servicevaluedetailCod) {
        this.servicevaluedetailCod = servicevaluedetailCod;
    }

    public Boolean getEnabledChr() {
        return enabledChr;
    }

    public void setEnabledChr(Boolean enabledChr) {
        this.enabledChr = enabledChr;
    }

    public String getColumn1Chr() {
        return column1Chr;
    }

    public void setColumn1Chr(String column1Chr) {
        this.column1Chr = column1Chr;
    }

    public String getColumn2Chr() {
        return column2Chr;
    }

    public void setColumn2Chr(String column2Chr) {
        this.column2Chr = column2Chr;
    }

    public String getColumn3Chr() {
        return column3Chr;
    }

    public void setColumn3Chr(String column3Chr) {
        this.column3Chr = column3Chr;
    }

    public String getColumn4Chr() {
        return column4Chr;
    }

    public void setColumn4Chr(String column4Chr) {
        this.column4Chr = column4Chr;
    }

    public String getColumn5Chr() {
        return column5Chr;
    }

    public void setColumn5Chr(String column5Chr) {
        this.column5Chr = column5Chr;
    }

    public String getColumn6Chr() {
        return column6Chr;
    }

    public void setColumn6Chr(String column6Chr) {
        this.column6Chr = column6Chr;
    }

    public String getColumn7Chr() {
        return column7Chr;
    }

    public void setColumn7Chr(String column7Chr) {
        this.column7Chr = column7Chr;
    }

    public String getColumn8Chr() {
        return column8Chr;
    }

    public void setColumn8Chr(String column8Chr) {
        this.column8Chr = column8Chr;
    }

    public String getColumn9Chr() {
        return column9Chr;
    }

    public void setColumn9Chr(String column9Chr) {
        this.column9Chr = column9Chr;
    }

    public String getColumn10Chr() {
        return column10Chr;
    }

    public void setColumn10Chr(String column10Chr) {
        this.column10Chr = column10Chr;
    }

    public Date getRecorddateDat() {
        return recorddateDat;
    }

    public void setRecorddateDat(Date recorddateDat) {
        this.recorddateDat = recorddateDat;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public ServiceValue getServiceValue() {
        return serviceValue;
    }

    public void setServiceValue(ServiceValue serviceValue) {
        this.serviceValue = serviceValue;
    }

    // @PrePersist
    // protected void addCreatedDate() {
    // if (recorddateDat == null) {
    // setRecorddateDat(new Date());
    // }
    // }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ServiceValueDetail other = (ServiceValueDetail) obj;
        if (this.servicevaluedetailCod != other.servicevaluedetailCod
            && (this.servicevaluedetailCod == null || !this.servicevaluedetailCod.equals(other.servicevaluedetailCod))) {
            return false;
        }
        if ((this.column1Chr == null) ? (other.column1Chr != null) : !this.column1Chr.equals(other.column1Chr)) {
            return false;
        }
        if ((this.column2Chr == null) ? (other.column2Chr != null) : !this.column2Chr.equals(other.column2Chr)) {
            return false;
        }
        if ((this.column3Chr == null) ? (other.column3Chr != null) : !this.column3Chr.equals(other.column3Chr)) {
            return false;
        }
        if ((this.column4Chr == null) ? (other.column4Chr != null) : !this.column4Chr.equals(other.column4Chr)) {
            return false;
        }
        if ((this.column5Chr == null) ? (other.column5Chr != null) : !this.column5Chr.equals(other.column5Chr)) {
            return false;
        }
        if ((this.column6Chr == null) ? (other.column6Chr != null) : !this.column6Chr.equals(other.column6Chr)) {
            return false;
        }
        if ((this.column7Chr == null) ? (other.column7Chr != null) : !this.column7Chr.equals(other.column7Chr)) {
            return false;
        }
        if ((this.column8Chr == null) ? (other.column8Chr != null) : !this.column8Chr.equals(other.column8Chr)) {
            return false;
        }
        if ((this.column9Chr == null) ? (other.column9Chr != null) : !this.column9Chr.equals(other.column9Chr)) {
            return false;
        }
        if ((this.column10Chr == null) ? (other.column10Chr != null) : !this.column10Chr.equals(other.column10Chr)) {
            return false;
        }
        if (this.recorddateDat != other.recorddateDat
            && (this.recorddateDat == null || !this.recorddateDat.equals(other.recorddateDat))) {
            return false;
        }
        if (this.message != other.message
            && (this.message == null || !this.message.equals(other.message))) {
            return false;
        }
        if (this.serviceValue != other.serviceValue
            && (this.serviceValue == null || !this.serviceValue.equals(other.serviceValue))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47
            * hash
            + (this.servicevaluedetailCod != null ? this.servicevaluedetailCod.hashCode() : 0);
        hash = 47 * hash
            + (this.column1Chr != null ? this.column1Chr.hashCode() : 0);
        hash = 47 * hash
            + (this.column2Chr != null ? this.column2Chr.hashCode() : 0);
        hash = 47 * hash
            + (this.column3Chr != null ? this.column3Chr.hashCode() : 0);
        hash = 47 * hash
            + (this.column4Chr != null ? this.column4Chr.hashCode() : 0);
        hash = 47 * hash
            + (this.column5Chr != null ? this.column5Chr.hashCode() : 0);
        hash = 47 * hash
            + (this.column6Chr != null ? this.column6Chr.hashCode() : 0);
        hash = 47 * hash
            + (this.column7Chr != null ? this.column7Chr.hashCode() : 0);
        hash = 47 * hash
            + (this.column8Chr != null ? this.column8Chr.hashCode() : 0);
        hash = 47 * hash
            + (this.column9Chr != null ? this.column9Chr.hashCode() : 0);
        hash = 47 * hash
            + (this.column10Chr != null ? this.column10Chr.hashCode() : 0);
        hash = 47 * hash
            + (this.recorddateDat != null ? this.recorddateDat.hashCode() : 0);
        hash = 47 * hash + (this.message != null ? this.message.hashCode() : 0);
        hash = 47 * hash
            + (this.serviceValue != null ? this.serviceValue.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "ServiceValueDetail[servicevaluedetailCod="
            + servicevaluedetailCod + "]";
    }
}
