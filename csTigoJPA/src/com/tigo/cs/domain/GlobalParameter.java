package com.tigo.cs.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "global_parameter")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(name = "GlobalParameter.findByCodeChr",
        query = "SELECT g FROM GlobalParameter g WHERE g.codeChr = :codeChr") })
public class GlobalParameter implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "globalParameterGen",
            sequenceName = "global_parameter_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "globalParameterGen")
    @Basic(optional = false)
    @Column(name = "global_parameter_cod")
    private Long globalParameterCod;
    @NotEmpty(message = "{entity.globalParameter.constraint.code.NotEmpty}")
    @Column(name = "code_chr")
    @Searchable(label = "entity.globalParameter.code.searchable")
    @PrimarySortedField
    private String codeChr;
    @NotEmpty(
            message = "{entity.globalParameter.constraint.description.NotEmpty}")
    @Column(name = "description_chr")
    @Searchable(label = "entity.globalParameter.description.searchable")
    private String descriptionChr;
    @NotEmpty(message = "{entity.globalParameter.constraint.value.NotEmpty}")
    @Column(name = "value_chr")
    @Searchable(label = "entity.globalParameter.value.searchable")
    private String valueChr;
    @Column(name = "modifiable_chr")
    private Boolean modifiableChr;

    public GlobalParameter() {
    }

    public GlobalParameter(Long globalParameterCod) {
        this.globalParameterCod = globalParameterCod;
    }

    public GlobalParameter(Long globalParameterCod, String codeChr,
            String descriptionChr, Boolean modifiableChr) {
        this.globalParameterCod = globalParameterCod;
        this.codeChr = codeChr;
        this.descriptionChr = descriptionChr;
        this.modifiableChr = modifiableChr;
    }

    public Long getGlobalParameterCod() {
        return globalParameterCod;
    }

    public void setGlobalParameterCod(Long globalParameterCod) {
        this.globalParameterCod = globalParameterCod;
    }

    public String getCodeChr() {
        return codeChr;
    }

    public void setCodeChr(String codeChr) {
        this.codeChr = codeChr;
    }

    public String getDescriptionChr() {
        return descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public String getValueChr() {
        return valueChr;
    }

    public void setValueChr(String valueChr) {
        this.valueChr = valueChr;
    }

    public Boolean getModifiableChr() {
        return modifiableChr;
    }

    public void setModifiableChr(Boolean modifiableChr) {
        this.modifiableChr = modifiableChr;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GlobalParameter other = (GlobalParameter) obj;
        if (this.globalParameterCod != other.globalParameterCod
            && (this.globalParameterCod == null || !this.globalParameterCod.equals(other.globalParameterCod))) {
            return false;
        }
        if ((this.codeChr == null) ? (other.codeChr != null) : !this.codeChr.equals(other.codeChr)) {
            return false;
        }
        if ((this.descriptionChr == null) ? (other.descriptionChr != null) : !this.descriptionChr.equals(other.descriptionChr)) {
            return false;
        }
        if ((this.valueChr == null) ? (other.valueChr != null) : !this.valueChr.equals(other.valueChr)) {
            return false;
        }
        if ((this.modifiableChr == null) ? (other.modifiableChr != null) : !this.modifiableChr.equals(other.modifiableChr)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79
            * hash
            + (this.globalParameterCod != null ? this.globalParameterCod.hashCode() : 0);
        hash = 79 * hash + (this.codeChr != null ? this.codeChr.hashCode() : 0);
        hash = 79
            * hash
            + (this.descriptionChr != null ? this.descriptionChr.hashCode() : 0);
        hash = 79 * hash
            + (this.valueChr != null ? this.valueChr.hashCode() : 0);
        hash = 79 * hash
            + (this.modifiableChr != null ? this.modifiableChr.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return descriptionChr.concat(" (Cod.: ").concat(codeChr.toString()).concat(
                ")");
    }
}
