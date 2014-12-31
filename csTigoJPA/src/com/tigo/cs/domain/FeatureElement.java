package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "FEATURE_ELEMENT")
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FeatureElement implements Serializable {

    private static final long serialVersionUID = -2687278439895943152L;

    @Id
    @Column(name = "FEATURE_ELEMENT_COD")
    @PrimarySortedField
    @Searchable(label = "entity.featureelement.searchable.featureElemetCod")
    private Long featureElementCod;

    @Column(name = "DESCRIPTION_CHR")
    @Searchable(label = "entity.featureelement.searchable.description")
    private String descriptionChr;

    @Column(name = "ENABLED_CHR")
    private Boolean enabledChr;

    @Temporal(TemporalType.DATE)
    @Column(name = "FINISH_PERIOD_DAT")
    private Date finishPeriodDat;

    @Column(name = "ID_USSD_MENU_ENTRY")
    private Long idUssdMenuEntry;

    @Column(name = "MAX_ENTRY_NUM")
    private Integer maxEntryNum;

    @Column(name = "MAX_FEATURE_VALUE")
    private Integer maxFeatureValue;

    @Column(name = "OPEN_CHR")
    private Boolean openChr;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_PERIOD_DAT")
    private Date startPeriodDat;

    // bi-directional many-to-one association to ClientFeature
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "COD_CLIENT",
            referencedColumnName = "COD_CLIENT"), @JoinColumn(
            name = "COD_FEATURE", referencedColumnName = "COD_FEATURE") })
    private ClientFeature clientFeature;

    // bi-directional many-to-many association to Classification
    @ManyToMany
    @JoinTable(name = "FEATURE_ELEMENT_CLASSIFICATION",
            joinColumns = { @JoinColumn(name = "COD_FEATURE_ELEMENT") },
            inverseJoinColumns = { @JoinColumn(name = "COD_CLASSIFICATION") })
    private List<Classification> classifications;

    // bi-directional many-to-one association to FeatureElementEntry
    @OneToMany(mappedBy = "featureElement")
    private List<FeatureElementEntry> featureElementEntries;

    // bi-directional many-to-many association to PhoneList
    // @ManyToMany(mappedBy="featureElements")
    // private List<PhoneList> phoneLists;
    // bi-directional many-to-many association to FeatureElement
    @ManyToMany
    @JoinTable(name = "PHONE_FEATURE_ELEMENT", joinColumns = { @JoinColumn(
            name = "COD_FEATURE_ELEMENT") },
            inverseJoinColumns = { @JoinColumn(name = "COD_PHONE_LIST") })
    private List<PhoneList> phoneLists;

    // bi-directional many-to-many association to FeatureElement
    @ManyToMany
    @JoinTable(name = "USERPHONE_FEATURE_ELEMENT", joinColumns = { @JoinColumn(
            name = "COD_FEATURE_ELEMENT") },
            inverseJoinColumns = { @JoinColumn(name = "COD_USERPHONE") })
    private List<Userphone> userphones;

    // bi-directional many-to-one association to FeatureElementEntry
    @OneToMany(mappedBy = "featureElement")
    private List<FeatureValue> featureValueList;

    @Transient
    private String descriptionLabel;

    public FeatureElement() {
    }

    public Long getFeatureElementCod() {
        return this.featureElementCod;
    }

    public void setFeatureElementCod(Long featureElementCod) {
        this.featureElementCod = featureElementCod;
    }

    public String getDescriptionChr() {
        return this.descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public Boolean getEnabledChr() {
        return this.enabledChr;
    }

    public void setEnabledChr(Boolean enabledChr) {
        this.enabledChr = enabledChr;
    }

    public Date getFinishPeriodDat() {
        return this.finishPeriodDat;
    }

    public void setFinishPeriodDat(Date finishPeriodDat) {
        this.finishPeriodDat = finishPeriodDat;
    }

    public Long getIdUssdMenuEntry() {
        return this.idUssdMenuEntry;
    }

    public void setIdUssdMenuEntry(Long idUssdMenuEntry) {
        this.idUssdMenuEntry = idUssdMenuEntry;
    }

    public Integer getMaxEntryNum() {
        return this.maxEntryNum;
    }

    public void setMaxEntryNum(Integer maxEntryNum) {
        this.maxEntryNum = maxEntryNum;
    }

    public Integer getMaxFeatureValue() {
        return this.maxFeatureValue;
    }

    public void setMaxFeatureValue(Integer maxFeatureValue) {
        this.maxFeatureValue = maxFeatureValue;
    }

    public Boolean getOpenChr() {
        return this.openChr;
    }

    public void setOpenChr(Boolean openChr) {
        this.openChr = openChr;
    }

    public Date getStartPeriodDat() {
        return this.startPeriodDat;
    }

    public void setStartPeriodDat(Date startPeriodDat) {
        this.startPeriodDat = startPeriodDat;
    }

    public ClientFeature getClientFeature() {
        return this.clientFeature;
    }

    public void setClientFeature(ClientFeature clientFeature) {
        this.clientFeature = clientFeature;
    }

    public List<Classification> getClassifications() {
        return this.classifications;
    }

    public void setClassifications(List<Classification> classifications) {
        this.classifications = classifications;
    }

    public List<FeatureElementEntry> getFeatureElementEntries() {
        return this.featureElementEntries;
    }

    public void setFeatureElementEntries(List<FeatureElementEntry> featureElementEntries) {
        this.featureElementEntries = featureElementEntries;
    }

    public List<PhoneList> getPhoneLists() {
        return this.phoneLists;
    }

    public void setPhoneLists(List<PhoneList> phoneLists) {
        this.phoneLists = phoneLists;
    }

    public List<Userphone> getUserphones() {
        return this.userphones;
    }

    public void setUserphones(List<Userphone> userphones) {
        this.userphones = userphones;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        FeatureElement clone = new FeatureElement();
        clone.setFeatureElementCod(this.featureElementCod);
        clone.setDescriptionChr(this.descriptionChr);
        clone.setEnabledChr(this.enabledChr);
        clone.setFinishPeriodDat(this.finishPeriodDat);
        clone.setIdUssdMenuEntry(this.idUssdMenuEntry);
        clone.setMaxEntryNum(this.maxEntryNum);
        clone.setMaxFeatureValue(this.maxFeatureValue);
        clone.setOpenChr(this.openChr);
        clone.setStartPeriodDat(this.startPeriodDat);
        clone.setClientFeature(this.clientFeature);
        clone.setClassifications(this.classifications);
        clone.setFeatureElementEntries(this.featureElementEntries);
        clone.setPhoneLists(this.phoneLists);
        clone.setUserphones(this.userphones);

        return clone;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((featureElementCod == null) ? 0 : featureElementCod.hashCode());
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
        FeatureElement other = (FeatureElement) obj;
        if (featureElementCod == null) {
            if (other.featureElementCod != null)
                return false;
        } else if (!featureElementCod.equals(other.featureElementCod))
            return false;
        return true;
    }

    public String getDescriptionLabel() {
        if (descriptionChr != null) {
            if (enabledChr) {
                descriptionLabel = descriptionChr.concat(" (Activo) ");
            } else {
                descriptionLabel = descriptionChr.concat(" (Inactivo) ");
            }

        }
        return descriptionLabel;
    }

    public void setDescriptionLabel(String descriptionLabel) {
        this.descriptionLabel = descriptionLabel;
    }

    public List<FeatureValue> getFeatureValueList() {
        return featureValueList;
    }

    public void setFeatureValueList(List<FeatureValue> featureValueList) {
        this.featureValueList = featureValueList;
    }

}