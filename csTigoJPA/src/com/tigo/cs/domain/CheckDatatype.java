package com.tigo.cs.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "check_datatype")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class CheckDatatype implements Serializable {

    private static final long serialVersionUID = 1794461413668745697L;
    @Id
    @Basic(optional = false)
    @Column(name = "CHECK_DATATYPE_COD")
    private BigDecimal checkDatatypeCod;
    @Basic(optional = false)
    @Column(name = "DATATYPE_CHR")
    private String datatypeChr;
    @Basic(optional = false)
    @Column(name = "REGEXP")
    private String regexp;
    @Basic(optional = false)
    @Column(name = "ADITIONAL_VALIDATION")
    private Boolean aditionalValidation;
    @Basic(optional = false)
    @Column(name = "I18N_MESSAGE")
    private String i18nMessage;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "checkDatatype")
    private List<DataCheck> dataCheckList;

    public CheckDatatype() {
    }

    public CheckDatatype(BigDecimal checkDatatypeCod) {
        this.checkDatatypeCod = checkDatatypeCod;
    }

    public CheckDatatype(BigDecimal checkDatatypeCod, String datatypeChr,
            String regexp, Boolean aditionalValidation) {
        this.checkDatatypeCod = checkDatatypeCod;
        this.datatypeChr = datatypeChr;
        this.regexp = regexp;
        this.aditionalValidation = aditionalValidation;
    }

    public BigDecimal getCheckDatatypeCod() {
        return checkDatatypeCod;
    }

    public void setCheckDatatypeCod(BigDecimal checkDatatypeCod) {
        this.checkDatatypeCod = checkDatatypeCod;
    }

    public String getDatatypeChr() {
        return datatypeChr;
    }

    public void setDatatypeChr(String datatypeChr) {
        this.datatypeChr = datatypeChr;
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public Boolean getAditionalValidation() {
        return aditionalValidation;
    }

    public void setAditionalValidation(Boolean aditionalValidation) {
        this.aditionalValidation = aditionalValidation;
    }

    public List<DataCheck> getDataCheckList() {
        return dataCheckList;
    }

    public void setDataCheckList(List<DataCheck> dataCheckList) {
        this.dataCheckList = dataCheckList;
    }

    public String getI18nMessage() {
        return i18nMessage;
    }

    public void setI18nMessage(String i18nMessage) {
        this.i18nMessage = i18nMessage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CheckDatatype other = (CheckDatatype) obj;
        if (this.checkDatatypeCod != other.checkDatatypeCod
            && (this.checkDatatypeCod == null || !this.checkDatatypeCod.equals(other.checkDatatypeCod))) {
            return false;
        }
        if ((this.datatypeChr == null) ? (other.datatypeChr != null) : !this.datatypeChr.equals(other.datatypeChr)) {
            return false;
        }
        if ((this.regexp == null) ? (other.regexp != null) : !this.regexp.equals(other.regexp)) {
            return false;
        }
        if (this.aditionalValidation != other.aditionalValidation
            && (this.aditionalValidation == null || !this.aditionalValidation.equals(other.aditionalValidation))) {
            return false;
        }
        if ((this.i18nMessage == null) ? (other.i18nMessage != null) : !this.i18nMessage.equals(other.i18nMessage)) {
            return false;
        }
        if (this.dataCheckList != other.dataCheckList
            && (this.dataCheckList == null || !this.dataCheckList.equals(other.dataCheckList))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19
            * hash
            + (this.checkDatatypeCod != null ? this.checkDatatypeCod.hashCode() : 0);
        hash = 19 * hash
            + (this.datatypeChr != null ? this.datatypeChr.hashCode() : 0);
        hash = 19 * hash + (this.regexp != null ? this.regexp.hashCode() : 0);
        hash = 19
            * hash
            + (this.aditionalValidation != null ? this.aditionalValidation.hashCode() : 0);
        hash = 19 * hash
            + (this.i18nMessage != null ? this.i18nMessage.hashCode() : 0);
        hash = 19 * hash
            + (this.dataCheckList != null ? this.dataCheckList.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "CheckDatatype[checkDatatypeCod=" + checkDatatypeCod + "]";
    }
}
