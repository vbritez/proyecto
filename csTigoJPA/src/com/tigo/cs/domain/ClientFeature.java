package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "CLIENT_FEATURE")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class ClientFeature implements Serializable {

    private static final long serialVersionUID = -8445938796637856625L;

    @EmbeddedId
    private ClientFeaturePK clientFeatureCod;

    @Column(name = "MAX_ELEMENT_NUM")
    @PrimarySortedField
    @Basic(optional = false)
    private Integer maxElementNum;

    @Column(name = "MAX_ENTRY_NUM")
    @Basic(optional = false)
    private Integer maxEntryNum;

    @Column(name = "SHORTCUT_NUM")
    @Basic(optional = false)
    @Searchable(label = "entity.clientfeature.searchable.shortcut")
    private Integer shortcutNum;

    @Column(name = "ENABLED_CHR")
    @Basic(optional = false)
    private Boolean enabledChr;

    // bi-directional many-to-one association to Client
    @ManyToOne
    @JoinColumn(name = "COD_CLIENT", nullable = false, insertable = false,
            updatable = false)
    private Client client;

    // bi-directional many-to-one association to Feature
    @ManyToOne
    @JoinColumn(name = "COD_FEATURE", nullable = false, insertable = false,
            updatable = false)
    private Feature feature;

    // bi-directional many-to-one association to FeatureElement
    @OneToMany(mappedBy = "clientFeature")
    private List<FeatureElement> featureElements;

    @Transient
    private String shortcutTransient;

    @Transient
    private String maxElementTransient;

    @Transient
    private String maxEntryTransient;

    public ClientFeature() {
    }

    public ClientFeaturePK getClientFeatureCod() {
        return clientFeatureCod;
    }

    public void setClientFeatureCod(ClientFeaturePK clientFeatureCod) {
        this.clientFeatureCod = clientFeatureCod;
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

    public Integer getShortcutNum() {
        return this.shortcutNum;
    }

    public void setShortcutNum(Integer shortcutNum) {
        this.shortcutNum = shortcutNum;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Feature getFeature() {
        return this.feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public List<FeatureElement> getFeatureElements() {
        return this.featureElements;
    }

    public void setFeatureElements(List<FeatureElement> featureElements) {
        this.featureElements = featureElements;
    }

    public String getShortcutTransient() {
        return shortcutTransient;
    }

    public void setShortcutTransient(String shortcutTransient) {
        this.shortcutTransient = shortcutTransient;
    }

    public String getMaxElementTransient() {

        return maxElementTransient;
    }

    public void setMaxElementTransient(String maxElementTransient) {
        this.maxElementTransient = maxElementTransient;
    }

    public String getMaxEntryTransient() {
        return maxEntryTransient;
    }

    public void setMaxEntryTransient(String maxEntryTransient) {
        this.maxEntryTransient = maxEntryTransient;
    }

    public Boolean getEnabledChr() {
        return enabledChr;
    }

    public void setEnabledChr(Boolean enabledChr) {
        this.enabledChr = enabledChr;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((clientFeatureCod == null) ? 0 : clientFeatureCod.hashCode());
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
        ClientFeature other = (ClientFeature) obj;
        if (clientFeatureCod == null) {
            if (other.clientFeatureCod != null)
                return false;
        } else if (!clientFeatureCod.equals(other.clientFeatureCod))
            return false;
        return true;
    }

}