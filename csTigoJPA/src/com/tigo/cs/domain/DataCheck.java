package com.tigo.cs.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.tigo.cs.domain.ussd.UssdMenuEntryCheck;

@Entity
@Table(name = "data_check")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class DataCheck implements Serializable {

    private static final long serialVersionUID = 4243642780922923370L;
    @Id
    @Basic(optional = false)
    @Column(name = "DATA_CHECK_COD")
    private BigDecimal dataCheckCod;
    @Basic(optional = false)
    @Column(name = "SIZE_NUM")
    private Long sizeNum;
    @Basic(optional = false)
    @Column(name = "DECIMAL_DIGITS")
    private Long decimalDigits;
    @Basic(optional = false)
    @Column(name = "OPTIONAL")
    private Boolean optional;
    @JoinColumn(name = "COD_CHECK_DATATYPE",
            referencedColumnName = "CHECK_DATATYPE_COD")
    @ManyToOne(optional = false)
    private CheckDatatype checkDatatype;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataCheck")
    private List<UssdMenuEntryCheck> ussdMenuEntryCheckList;

    public DataCheck() {
    }

    public DataCheck(BigDecimal dataCheckCod) {
        this.dataCheckCod = dataCheckCod;
    }

    public DataCheck(BigDecimal dataCheckCod, Long sizeNum, Long decimalDigits,
            Boolean optional) {
        this.dataCheckCod = dataCheckCod;
        this.sizeNum = sizeNum;
        this.decimalDigits = decimalDigits;
        this.optional = optional;
    }

    public BigDecimal getDataCheckCod() {
        return dataCheckCod;
    }

    public void setDataCheckCod(BigDecimal dataCheckCod) {
        this.dataCheckCod = dataCheckCod;
    }

    public Long getSizeNum() {
        return sizeNum;
    }

    public void setSizeNum(Long sizeNum) {
        this.sizeNum = sizeNum;
    }

    public Long getDecimalDigits() {
        return decimalDigits;
    }

    public void setDecimalDigits(Long decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public Boolean getOptional() {
        return optional;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    public CheckDatatype getCheckDatatype() {
        return checkDatatype;
    }

    public void setCheckDatatype(CheckDatatype checkDatatype) {
        this.checkDatatype = checkDatatype;
    }

    public List<UssdMenuEntryCheck> getUssdMenuEntryCheckList() {
        return ussdMenuEntryCheckList;
    }

    public void setUssdMenuEntryCheckList(List<UssdMenuEntryCheck> ussdMenuEntryCheckList) {
        this.ussdMenuEntryCheckList = ussdMenuEntryCheckList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataCheck other = (DataCheck) obj;
        if (this.dataCheckCod != other.dataCheckCod
            && (this.dataCheckCod == null || !this.dataCheckCod.equals(other.dataCheckCod))) {
            return false;
        }
        if (this.sizeNum != other.sizeNum
            && (this.sizeNum == null || !this.sizeNum.equals(other.sizeNum))) {
            return false;
        }
        if (this.decimalDigits != other.decimalDigits
            && (this.decimalDigits == null || !this.decimalDigits.equals(other.decimalDigits))) {
            return false;
        }
        if (this.optional != other.optional
            && (this.optional == null || !this.optional.equals(other.optional))) {
            return false;
        }
        if (this.checkDatatype != other.checkDatatype
            && (this.checkDatatype == null || !this.checkDatatype.equals(other.checkDatatype))) {
            return false;
        }
        if (this.ussdMenuEntryCheckList != other.ussdMenuEntryCheckList
            && (this.ussdMenuEntryCheckList == null || !this.ussdMenuEntryCheckList.equals(other.ussdMenuEntryCheckList))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash
            + (this.dataCheckCod != null ? this.dataCheckCod.hashCode() : 0);
        hash = 61 * hash + (this.sizeNum != null ? this.sizeNum.hashCode() : 0);
        hash = 61 * hash
            + (this.decimalDigits != null ? this.decimalDigits.hashCode() : 0);
        hash = 61 * hash
            + (this.optional != null ? this.optional.hashCode() : 0);
        hash = 61 * hash
            + (this.checkDatatype != null ? this.checkDatatype.hashCode() : 0);
        hash = 61
            * hash
            + (this.ussdMenuEntryCheckList != null ? this.ussdMenuEntryCheckList.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "DataCheck[dataCheckCod=" + dataCheckCod + "]";
    }
}
