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
@Table(name = "SHIFT_PERIOD")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(
        name = "ShiftPeriod.getShiftPeriodByStatus",
        query = "SELECT sp FROM ShiftPeriod sp WHERE sp.status = :status AND sp.client.enabledChr = true") })
public class ShiftPeriod implements Serializable {

    private static final long serialVersionUID = 7058119560025639124L;

    @Id
    @SequenceGenerator(name = "SHIFT_PERIOD_SHIFTPERIODCOD_GENERATOR",
            sequenceName = "SHIFT_PERIOD_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "SHIFT_PERIOD_SHIFTPERIODCOD_GENERATOR")
    @Column(name = "SHIFT_PERIOD_COD", unique = true, nullable = false,
            precision = 19)
    private Long shiftPeriodCod;

    @Column(name = "DATE_FROM", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFrom;

    @Column(name = "DATE_TO", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTo;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "STATUS", nullable = false, precision = 19)
    private Boolean status;

    // bi-directional many-to-many association to MetaData
    @ManyToMany
    @JoinTable(
            name = "PERSON_SHIFT_PERIOD",
            joinColumns = { @JoinColumn(name = "COD_SHIFT_PERIOD",
                    nullable = false) },
            inverseJoinColumns = { @JoinColumn(name = "COD_CLIENT",
                    referencedColumnName = "COD_CLIENT", nullable = false), @JoinColumn(
                    name = "COD_MEMBER", referencedColumnName = "COD_MEMBER",
                    nullable = false), @JoinColumn(name = "COD_META",
                    referencedColumnName = "COD_META", nullable = false), @JoinColumn(
                    name = "CODE_CHR", referencedColumnName = "CODE_CHR",
                    nullable = false) })
    private List<MetaData> metaDatas;

    @ManyToOne(optional = true)
    @JoinColumn(name = "COD_CLIENT")
    private Client client;

    // bi-directional many-to-one association to ShiftConfiguration
    @OneToMany(mappedBy = "shiftPeriod")
    private List<ShiftConfiguration> shiftConfigurations;

    public ShiftPeriod() {
    }

    public Long getShiftPeriodCod() {
        return this.shiftPeriodCod;
    }

    public void setShiftPeriodCod(Long shiftPeriodCod) {
        this.shiftPeriodCod = shiftPeriodCod;
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

    public List<MetaData> getMetaDatas() {
        return this.metaDatas;
    }

    public void setMetaDatas(List<MetaData> metaDatas) {
        this.metaDatas = metaDatas;
    }

    public List<ShiftConfiguration> getShiftConfigurations() {
        return this.shiftConfigurations;
    }

    public void setShiftConfigurations(List<ShiftConfiguration> shiftConfigurations) {
        this.shiftConfigurations = shiftConfigurations;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((shiftPeriodCod == null) ? 0 : shiftPeriodCod.hashCode());
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
        ShiftPeriod other = (ShiftPeriod) obj;
        if (shiftPeriodCod == null) {
            if (other.shiftPeriodCod != null)
                return false;
        } else if (!shiftPeriodCod.equals(other.shiftPeriodCod))
            return false;
        return true;
    }

}