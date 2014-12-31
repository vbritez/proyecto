package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "FEATURE_ELEMENT_ENTRY")
@NamedQueries({ @NamedQuery(
        name = "FeatureElementEntry.findFeatureElementEntryRoot",
        query = "SELECT fe FROM FeatureElementEntry fe WHERE fe.featureElement = :featureElement AND fe.codOwnerEntry is NULL"), @NamedQuery(
        name = "FeatureElementEntry.findEagerFeatureElementEntry",
        query = "SELECT distinct fe FROM FeatureElementEntry fe LEFT JOIN FETCH fe.featureElementEntries WHERE fe.featureElement = :featureElement ORDER BY fe.featureElementEntryCod"), @NamedQuery(
        name = "FeatureElementEntry.findEagerFeatureElementEntryRoot",
        query = "SELECT fe FROM FeatureElementEntry fe LEFT JOIN FETCH fe.featureElementEntries WHERE fe.featureElement = :featureElement AND fe.codOwnerEntry is NULL") })
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FeatureElementEntry implements Serializable, Comparable<FeatureElementEntry> {

    private static final long serialVersionUID = -363150001227222314L;

    @Id
    @Column(name = "FEATURE_ELEMENT_ENTRY_COD")
    private Long featureElementEntryCod;

    @Column(name = "DESCRIPTION_CHR")
    private String descriptionChr;

    @Column(name = "FINAL_CHR")
    private Boolean finalChr;

    @Column(name = "MULTI_SELECT_OPTION")
    private Boolean multiSelectOption;

    @Column(name = "ID_USSD_MENU_ENTRY")
    private Long idUssdMenuEntry;

    @Column(name = "ORDER_NUM")
    private Integer orderNum;

    @Column(name = "TITLE_CHR")
    private String titleChr;

    // bi-directional many-to-one association to DataCheck
    @ManyToOne
    @JoinColumn(name = "COD_DATA_CHECK")
    private DataCheck dataCheck;

    // bi-directional many-to-one association to FeatureElement
    @ManyToOne
    @JoinColumn(name = "COD_FEATURE_ELEMENT")
    private FeatureElement featureElement;

    // bi-directional many-to-one association to FeatureElementEntry
    @ManyToOne
    @JoinColumn(name = "COD_OWNER_ENTRY")
    private FeatureElementEntry codOwnerEntry;

    // bi-directional many-to-one association to FeatureElementEntry
    @OneToMany(mappedBy = "codOwnerEntry")
    private List<FeatureElementEntry> featureElementEntries;

    // bi-directional many-to-one association to FeatureValueData
    @OneToMany(mappedBy = "featureElementEntry")
    private List<FeatureValueData> featureValueData;

    // bi-directional many-to-one association to FeatureElementEntry
    @ManyToOne
    @JoinColumn(name = "COD_FEATURE_ENTRY_TYPE")
    private FeatureEntryType codFeatureEntryType;

    @Transient
    private Long idTransient;

    public FeatureElementEntry() {
    }

    public FeatureElementEntry(String title) {
        this.titleChr = title;
        this.descriptionChr = title;
    }

    public Long getFeatureElementEntryCod() {
        return this.featureElementEntryCod;
    }

    public void setFeatureElementEntryCod(Long featureElementEntryCod) {
        this.featureElementEntryCod = featureElementEntryCod;
    }

    public FeatureEntryType getCodFeatureEntryType() {
        return this.codFeatureEntryType;
    }

    public void setCodFeatureEntryType(FeatureEntryType codFeatureEntryType) {
        this.codFeatureEntryType = codFeatureEntryType;
    }

    public String getDescriptionChr() {
        return this.descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public Boolean getFinalChr() {
        return this.finalChr;
    }

    public void setFinalChr(Boolean finalChr) {
        this.finalChr = finalChr;
    }

    public Boolean getMultiSelectOption() {
        return multiSelectOption;
    }

    public void setMultiSelectOption(Boolean multiSelectOption) {
        this.multiSelectOption = multiSelectOption;
    }

    public Long getIdUssdMenuEntry() {
        return this.idUssdMenuEntry;
    }

    public void setIdUssdMenuEntry(Long idUssdMenuEntry) {
        this.idUssdMenuEntry = idUssdMenuEntry;
    }

    public Integer getOrderNum() {
        return this.orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public String getTitleChr() {
        return this.titleChr;
    }

    public void setTitleChr(String titleChr) {
        this.titleChr = titleChr;
    }

    public DataCheck getDataCheck() {
        return this.dataCheck;
    }

    public void setDataCheck(DataCheck dataCheck) {
        this.dataCheck = dataCheck;
    }

    public FeatureElement getFeatureElement() {
        return this.featureElement;
    }

    public void setFeatureElement(FeatureElement featureElement) {
        this.featureElement = featureElement;
    }

    public FeatureElementEntry getCodOwnerEntry() {
        return this.codOwnerEntry;
    }

    public void setCodOwnerEntry(FeatureElementEntry codOwnerEntry) {
        this.codOwnerEntry = codOwnerEntry;
    }

    public List<FeatureElementEntry> getFeatureElementEntries() {
        return this.featureElementEntries;
    }

    public void setFeatureElementEntries(List<FeatureElementEntry> featureElementEntries) {
        this.featureElementEntries = featureElementEntries;
    }

    public List<FeatureValueData> getFeatureValueData() {
        return this.featureValueData;
    }

    public void setFeatureValueData(List<FeatureValueData> featureValueData) {
        this.featureValueData = featureValueData;
    }

    @Override
    public String toString() {
        return titleChr;
    }

    public Long getIdTransient() {
        return idTransient;
    }

    public void setIdTransient(Long idTransient) {
        this.idTransient = idTransient;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
            * result
            + ((featureElementEntryCod == null) ? 0 : featureElementEntryCod.hashCode());
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
        FeatureElementEntry other = (FeatureElementEntry) obj;
        if (featureElementEntryCod == null) {
            if (other.featureElementEntryCod != null)
                return false;
        } else if (!featureElementEntryCod.equals(other.featureElementEntryCod))
            return false;
        return true;
    }

    @Override
    public int compareTo(FeatureElementEntry o) {

        if (!(o instanceof FeatureElementEntry)) {
            return 0;
        }

        FeatureElementEntry castObject = o;

        return this.getFeatureElementEntryCod().compareTo(
                castObject.getFeatureElementEntryCod());

    }
}