package com.tigo.cs.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.PrimarySortedField;

@Entity
@Table(name = "FEATURE_VALUE_DATA")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class FeatureValueData implements Serializable {

    private static final long serialVersionUID = -2194761812838952471L;

    @Id
    @SequenceGenerator(
            name = "FEATURE_VALUE_DATA_FEATUREVALUEDATACOD_GENERATOR",
            sequenceName = "FEATURE_VALUE_DATA_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "FEATURE_VALUE_DATA_FEATUREVALUEDATACOD_GENERATOR")
    @Column(name = "FEATURE_VALUE_DATA_COD")
    @PrimarySortedField
    private Long featureValueDataCod;

    @Column(name = "DATA_CHR")
    private String dataChr;

    // bi-directional many-to-one association to FeatureElementEntry
    @ManyToOne
    @JoinColumn(name = "COD_FEATURE_ELEMENT_ENTRY")
    private FeatureElementEntry featureElementEntry;

    // bi-directional many-to-one association to FeatureValue
    @ManyToOne
    @JoinColumn(name = "COD_FEATURE_VALUE")
    private FeatureValue featureValue;

    public FeatureValueData() {
    }

    public long getFeatureValueDataCod() {
        return this.featureValueDataCod;
    }

    public void setFeatureValueDataCod(long featureValueDataCod) {
        this.featureValueDataCod = featureValueDataCod;
    }

    public String getDataChr() {
        return this.dataChr;
    }

    public void setDataChr(String dataChr) {
        this.dataChr = dataChr;
    }

    public FeatureElementEntry getFeatureElementEntry() {
        return this.featureElementEntry;
    }

    public void setFeatureElementEntry(FeatureElementEntry featureElementEntry) {
        this.featureElementEntry = featureElementEntry;
    }

    public FeatureValue getFeatureValue() {
        return this.featureValue;
    }

    public void setFeatureValue(FeatureValue featureValue) {
        this.featureValue = featureValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
            * result
            + ((featureValueDataCod == null) ? 0 : featureValueDataCod.hashCode());
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
        FeatureValueData other = (FeatureValueData) obj;
        if (featureValueDataCod == null) {
            if (other.featureValueDataCod != null)
                return false;
        } else if (!featureValueDataCod.equals(other.featureValueDataCod))
            return false;
        return true;
    }

}