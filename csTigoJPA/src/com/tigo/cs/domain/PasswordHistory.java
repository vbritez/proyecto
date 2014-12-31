package com.tigo.cs.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "password_history")
@NamedQueries({ @NamedQuery(name = "PasswordHistory.findAll",
        query = "SELECT p FROM PasswordHistory p"), @NamedQuery(
        name = "PasswordHistory.findByUseradminAndPasswordChr",
        query = "SELECT p FROM PasswordHistory p WHERE p.passwordChr = :passwordChr AND p.useradmin.useradminCod = :useradminCod"), @NamedQuery(
        name = "PasswordHistory.findByUseradmin",
        query = "SELECT p FROM PasswordHistory p WHERE p.useradmin.useradminCod = :useradminCod ORDER BY p.createdDate DESC") })
public class PasswordHistory implements Serializable {

    private static final long serialVersionUID = -2237063710920458266L;
    @Id
    @SequenceGenerator(name = "passwordHistoryGen",
            sequenceName = "password_history_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "passwordHistoryGen")
    @Basic(optional = false)
    @Column(name = "PASSWORD_HISTORY_COD", nullable = false, precision = 19,
            scale = 0)
    private BigDecimal passwordHistoryCod;
    @Basic(optional = false)
    @Column(name = "PASSWORD_CHR", nullable = false, length = 100)
    private String passwordChr;
    @Column(name = "CREATEDDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @JoinColumn(name = "COD_USERADMIN", referencedColumnName = "USERADMIN_COD",
            nullable = false)
    @ManyToOne(optional = false)
    private Useradmin useradmin;

    public PasswordHistory() {
    }

    public PasswordHistory(BigDecimal passwordHistoryCod) {
        this.passwordHistoryCod = passwordHistoryCod;
    }

    public PasswordHistory(BigDecimal passwordHistoryCod, String passwordChr) {
        this.passwordHistoryCod = passwordHistoryCod;
        this.passwordChr = passwordChr;
    }

    public BigDecimal getPasswordHistoryCod() {
        return passwordHistoryCod;
    }

    public void setPasswordHistoryCod(BigDecimal passwordHistoryCod) {
        this.passwordHistoryCod = passwordHistoryCod;
    }

    public String getPasswordChr() {
        return passwordChr;
    }

    public void setPasswordChr(String passwordChr) {
        this.passwordChr = passwordChr;
    }

    public Date getCreateddate() {
        return createdDate;
    }

    public void setCreateddate(Date createddate) {
        this.createdDate = createddate;
    }

    public Useradmin getUseradmin() {
        return useradmin;
    }

    public void setUseradmin(Useradmin useradmin) {
        this.useradmin = useradmin;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PasswordHistory other = (PasswordHistory) obj;
        if (this.passwordHistoryCod != other.passwordHistoryCod
            && (this.passwordHistoryCod == null || !this.passwordHistoryCod.equals(other.passwordHistoryCod))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71
            * hash
            + (this.passwordHistoryCod != null ? this.passwordHistoryCod.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "PasswordHistory[passwordHistoryCod=" + passwordHistoryCod + "]";
    }
}
