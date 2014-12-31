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

@Entity
@Table(name = "MENU_MOVIL_PERIOD")
public class MenuMovilPeriod implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3084476327094041336L;

    @Id
    @Column(name = "MENU_MOVIL_PERIOD_COD")
    @SequenceGenerator(name = "menuMovilPeriodGen",
            sequenceName = "MENU_MOVIL_PERIOD_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "menuMovilPeriodGen")
    private Long menuMovilPeriodCod;

    @Column(name = "END_DATE_PERIOD")
    @Temporal(TemporalType.TIMESTAMP)
    @Basic(optional = false)
    private Date endDatePeriod;

    @Column(name = "QUOTA_TOTAL")
    @Basic(optional = false)
    private Long quotaTotal;

    @Column(name = "QUOTA_USED")
    @Basic(optional = false)
    private Long quotaUsed;

    @Column(name = "START_DATE_PERIOD")
    @Temporal(TemporalType.TIMESTAMP)
    @Basic(optional = false)
    private Date startDatePeriod;

    @Column(name = "ENABLED_CHR")
    @Basic(optional = false)
    private Boolean enabledChr;

    @JoinColumn(name = "COD_MENU_MOVIL_USER",
            referencedColumnName = "MENU_MOVIL_USER_COD")
    @ManyToOne(optional = false)
    private MenuMovilUser menuMovilUser;

    public MenuMovilPeriod() {
    }

    public Long getMenuMovilPeriodCod() {
        return this.menuMovilPeriodCod;
    }

    public void setMenuMovilPeriodCod(Long menuMovilPeriodCod) {
        this.menuMovilPeriodCod = menuMovilPeriodCod;
    }

    public Date getEndDatePeriod() {
        return this.endDatePeriod;
    }

    public void setEndDatePeriod(Date endDatePeriod) {
        this.endDatePeriod = endDatePeriod;
    }

    public Long getQuotaTotal() {
        return this.quotaTotal;
    }

    public void setQuotaTotal(Long quotaTotal) {
        this.quotaTotal = quotaTotal;
    }

    public Long getQuotaUsed() {
        return this.quotaUsed;
    }

    public void setQuotaUsed(Long quotaUsed) {
        this.quotaUsed = quotaUsed;
    }

    public Date getStartDatePeriod() {
        return this.startDatePeriod;
    }

    public void setStartDatePeriod(Date startDatePeriod) {
        this.startDatePeriod = startDatePeriod;
    }

    public Boolean getEnabledChr() {
        return enabledChr;
    }

    public void setEnabledChr(Boolean enabledChr) {
        this.enabledChr = enabledChr;
    }

    public MenuMovilUser getMenuMovilUser() {
        return menuMovilUser;
    }

    public void setMenuMovilUser(MenuMovilUser menuMovilUser) {
        this.menuMovilUser = menuMovilUser;
    }

    @Override
    public String toString() {
        return menuMovilPeriodCod.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MenuMovilPeriod other = (MenuMovilPeriod) obj;
        if (this.menuMovilPeriodCod != other.menuMovilPeriodCod
            && (this.menuMovilPeriodCod == null || !this.menuMovilPeriodCod.equals(other.menuMovilPeriodCod))) {
            return false;
        }
        return true;
    }

}