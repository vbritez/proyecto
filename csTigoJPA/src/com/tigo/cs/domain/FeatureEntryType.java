package com.tigo.cs.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.PrimarySortedField;

@Entity
@Table(name = "FEATURE_ENTRY_TYPE")
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FeatureEntryType implements Serializable {

    private static final long serialVersionUID = 4917115192466847989L;

    @Id
    @Column(name = "FEATURE_ENTRY_TYPE_COD")
    @PrimarySortedField
    private Long featureEntryTypeCod;

    @Column(name = "DESCRIPTION_CHR")
    private String descriptionChr;

    @Column(name = "NAME_CHR")
    private String nameChr;

    @Column(name = "PERSISTIBLE_CHR")
    private Boolean persistibleChr;

    public FeatureEntryType() {
    }

    public Long getFeatureEntryTypeCod() {
        return this.featureEntryTypeCod;
    }

    public void setFeatureEntryTypeCod(Long featureEntryTypeCod) {
        this.featureEntryTypeCod = featureEntryTypeCod;
    }

    public String getDescriptionChr() {
        return this.descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public String getNameChr() {
        return this.nameChr;
    }

    public void setNameChr(String nameChr) {
        this.nameChr = nameChr;
    }

    public Boolean getPersistibleChr() {
        return this.persistibleChr;
    }

    public void setPersistibleChr(Boolean persistibleChr) {
        this.persistibleChr = persistibleChr;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
            * result
            + ((featureEntryTypeCod == null) ? 0 : featureEntryTypeCod.hashCode());
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
        FeatureEntryType other = (FeatureEntryType) obj;
        if (featureEntryTypeCod == null) {
            if (other.featureEntryTypeCod != null)
                return false;
        } else if (!featureEntryTypeCod.equals(other.featureEntryTypeCod))
            return false;
        return true;
    }

}