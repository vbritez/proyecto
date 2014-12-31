package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.PrimarySortedField;

@Entity
@Table(name = "FEATURE")
@NamedQueries({ @NamedQuery(name = "Feature.findAll",
        query = "SELECT distinct f FROM Feature f ") })
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Feature implements Serializable {

    private static final long serialVersionUID = 7949984039310174776L;

    @Id
    @Column(name = "FEATURE_CODE")
    @PrimarySortedField
    private Long featureCode;

    @Column(name = "DESCRIPTION_CHR")
    private String descriptionChr;

    @Column(name = "DISPATCHER_CHR")
    private String dispatcherChr;

    @Column(name = "MAX_ELEMENT_NUM")
    private Integer maxElementNum;

    @Column(name = "MAX_ENTRY_NUM")
    private Integer maxEntryNum;

    @Column(name = "SHOWONMENU_CHR")
    private Boolean showOnMenuChr;

    // bi-directional many-to-one association to ClientFeature
    @OneToMany(mappedBy = "feature")
    private List<ClientFeature> clientFeatures;

    public Feature() {
    }

    public Boolean getShowOnMenuChr() {
        return showOnMenuChr;
    }

    public void setShowOnMenuChr(Boolean showOnMenuChr) {
        this.showOnMenuChr = showOnMenuChr;
    }

    public Long getFeatureCode() {
        return this.featureCode;
    }

    public void setFeatureCode(Long featureCode) {
        this.featureCode = featureCode;
    }

    public String getDescriptionChr() {
        return this.descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public String getDispatcherChr() {
        return this.dispatcherChr;
    }

    public void setDispatcherChr(String dispatcherChr) {
        this.dispatcherChr = dispatcherChr;
    }

    public Integer getMaxElementNum() {
        return this.maxElementNum;
    }

    public void setMaxElementNum(Integer maxElementNum) {
        this.maxElementNum = maxElementNum;
    }

    public Integer getMaxEntryNum() {
        return this.maxEntryNum;
    }

    public void setMaxEntryNum(Integer maxEntryNum) {
        this.maxEntryNum = maxEntryNum;
    }

    public List<ClientFeature> getClientFeatures() {
        return this.clientFeatures;
    }

    public void setClientFeatures(List<ClientFeature> clientFeatures) {
        this.clientFeatures = clientFeatures;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((featureCode == null) ? 0 : featureCode.hashCode());
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
        Feature other = (Feature) obj;
        if (featureCode == null) {
            if (other.featureCode != null)
                return false;
        } else if (!featureCode.equals(other.featureCode))
            return false;
        return true;
    }

}