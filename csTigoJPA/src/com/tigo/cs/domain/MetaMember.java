package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "meta_member")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(
        name = "MetaMember.findByCodMeta",
        query = "SELECT m FROM MetaMember m WHERE m.metaMemberPK.codMeta = :codMeta"), @NamedQuery(
        name = "MetaMember.findSearchableByCodMeta",
        query = "SELECT m FROM MetaMember m WHERE m.metaMemberPK.codMeta = :codMeta AND m.searchableChr = true ORDER BY m.metaMemberPK.memberCod"), @NamedQuery(
        name = "MetaMember.findReturnableByCodMeta",
        query = "SELECT m FROM MetaMember m WHERE m.metaMemberPK.codMeta = :codMeta AND m.returnableChr = true ORDER BY m.metaMemberPK.memberCod"), @NamedQuery(
        name = "MetaMember.findByCodMetaAndMemberCod",
        query = "SELECT m FROM MetaMember m WHERE m.metaMemberPK.codMeta = :codMeta AND m.metaMemberPK.memberCod = :memberCod"), @NamedQuery(
        name = "MetaMember.findByMemberCod",
        query = "SELECT m FROM MetaMember m WHERE m.metaMemberPK.memberCod = :memberCod"), @NamedQuery(
        name = "MetaMember.findByDescriptionChr",
        query = "SELECT m FROM MetaMember m WHERE m.descriptionChr = :descriptionChr"), @NamedQuery(
        name = "MetaMember.findByDatatypeChr",
        query = "SELECT m FROM MetaMember m WHERE m.datatypeChr = :datatypeChr"), @NamedQuery(
        name = "MetaMember.findByLengthNum",
        query = "SELECT m FROM MetaMember m WHERE m.lengthNum = :lengthNum") })
public class MetaMember implements Serializable {

    private static final long serialVersionUID = 6529655300172668625L;
    @EmbeddedId
    protected MetaMemberPK metaMemberPK;
    @Basic(optional = false)
    @Column(name = "DESCRIPTION_CHR")
    private String descriptionChr;
    @Basic(optional = false)
    @Column(name = "DATATYPE_CHR")
    private String datatypeChr;
    @Column(name = "LENGTH_NUM")
    private Short lengthNum;
    @Column(name = "SEARCHABLE_CHR")
    private Boolean searchableChr;
    @Column(name = "RETURNABLE_CHR")
    private Boolean returnableChr;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "metaMember")
    private List<MetaData> metaDataList;
    @JoinColumn(name = "COD_META", referencedColumnName = "META_COD",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Meta meta;
    @ManyToMany(mappedBy = "metaMemberSet")
    private List<Client> clientList;

    public MetaMember() {
    }

    public MetaMember(MetaMemberPK metaMemberPK) {
        this.metaMemberPK = metaMemberPK;
    }

    public MetaMember(MetaMemberPK metaMemberPK, String descriptionChr,
            String datatypeChr) {
        this.metaMemberPK = metaMemberPK;
        this.descriptionChr = descriptionChr;
        this.datatypeChr = datatypeChr;
    }

    public MetaMember(Long codMeta, Long memberCod) {
        this.metaMemberPK = new MetaMemberPK(codMeta, memberCod);
    }

    public MetaMemberPK getMetaMemberPK() {
        return metaMemberPK;
    }

    public void setMetaMemberPK(MetaMemberPK metaMemberPK) {
        this.metaMemberPK = metaMemberPK;
    }

    public String getDescriptionChr() {
        return descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public String getDatatypeChr() {
        return datatypeChr;
    }

    public void setDatatypeChr(String datatypeChr) {
        this.datatypeChr = datatypeChr;
    }

    public Short getLengthNum() {
        return lengthNum;
    }

    public void setLengthNum(Short lengthNum) {
        this.lengthNum = lengthNum;
    }

    public Boolean getSearchableChr() {
        return searchableChr;
    }

    public void setSearchableChr(Boolean searchableChr) {
        this.searchableChr = searchableChr;
    }

    public Boolean getReturnableChr() {
        return returnableChr;
    }

    public void setReturnableChr(Boolean returnableChr) {
        this.returnableChr = returnableChr;
    }

    public List<MetaData> getMetaDataList() {
        return metaDataList;
    }

    public void setMetaDataList(List<MetaData> metaDataList) {
        this.metaDataList = metaDataList;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Client> getClientList() {
        return clientList;
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((metaMemberPK == null) ? 0 : metaMemberPK.hashCode());
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
        MetaMember other = (MetaMember) obj;
        if (metaMemberPK == null) {
            if (other.metaMemberPK != null)
                return false;
        } else if (!metaMemberPK.equals(other.metaMemberPK))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MetaMember[metaMemberPK=" + metaMemberPK + "]";
    }
}
