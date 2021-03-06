package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "meta_data")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(
        name = "MetaData.findByCodClientCodMetaCodMemberCodeChr",
        query = "SELECT m FROM MetaData m WHERE m.metaDataPK.codClient = :codClient AND m.metaDataPK.codMeta = :codMeta AND m.metaDataPK.codMember = :codMember AND m.metaDataPK.codeChr = :codeChr ORDER BY m.metaDataPK.codeChr"), @NamedQuery(
        name = "MetaData.findByCodClientCodMetaCodMemberValueChr",
        query = "SELECT m FROM MetaData m WHERE m.metaDataPK.codClient = :codClient AND m.metaDataPK.codMeta = :codMeta AND m.metaDataPK.codMember = :codMember AND m.valueChr = :valueChr ORDER BY m.metaDataPK.codeChr"), @NamedQuery(
        name = "MetaData.findByCodClientCodMetaCodeChr",
        query = "SELECT m FROM MetaData m WHERE m.metaDataPK.codClient = :codClient AND m.metaDataPK.codMeta = :codMeta AND m.metaDataPK.codeChr = :codeChr ORDER BY m.metaDataPK.codMember ASC"), @NamedQuery(
        name = "MetaData.findByCodClientCodMetaCodMember",
        query = "SELECT m FROM MetaData m WHERE m.metaDataPK.codClient = :codClient AND m.metaDataPK.codMeta = :codMeta AND m.metaDataPK.codMember = :codMember"), @NamedQuery(
        name = "MetaData.findByCodClientCodMeta",
        query = "SELECT m FROM MetaData m LEFT JOIN FETCH m.userphones WHERE m.metaDataPK.codClient = :codClient AND m.metaDataPK.codMeta = :codMeta"), @NamedQuery(
        name = "MetaData.deleteEntitiesMass",
        query = "DELETE FROM MetaData m WHERE m.metaDataPK.codClient = :codClient AND m.metaDataPK.codMeta = :codMeta"), @NamedQuery(
        name = "MetaData.deleteMetaDataByCodeChr",
        query = "DELETE FROM MetaData m WHERE m.metaDataPK.codClient = :codClient AND m.metaDataPK.codMeta = :codMeta AND m.metaDataPK.codeChr = :codeChr"), @NamedQuery(
        name = "MetaData.associatedMeta",
        query = "SELECT distinct u From MetaData m, in (m.userphones) u where m.metaDataPK.codClient = :codClient and m.metaDataPK.codMeta = :codMeta and m.metaDataPK.codMember = :codMember and m.metaDataPK.codeChr = :codeChr"), @NamedQuery(
        name = "MetaData.associatedMetaUserphone",
        query = "SELECT distinct u From MetaData m, in (m.userphones) u where m.metaDataPK.codClient = :codClient and m.metaDataPK.codMeta = :codMeta and m.metaDataPK.codMember = :codMember and m.metaDataPK.codeChr = :codeChr and u.userphoneCod = :userphoneCod"), @NamedQuery(
        name = "MetaData.deleteMetaDataByMember",
        query = "DELETE FROM MetaData m WHERE m.metaDataPK.codClient = :codClient AND m.metaDataPK.codMeta = :codMeta AND m.metaDataPK.codeChr = :codeChr AND m.metaDataPK.codMember = :codMember"), @NamedQuery(
        name = "MetaData.findByClientMemberValue",
        query = "SELECT m FROM MetaData m WHERE m.metaDataPK.codClient = :codClient AND m.metaDataPK.codMember = :codMember AND m.valueChr = :valueChr") })
public class MetaData implements Serializable {

    private static final long serialVersionUID = 5819658769345289510L;
    @EmbeddedId
    @Searchable(label = "entity.metadata.searchable.codeChr",
            internalField = "codeChr")
    protected MetaDataPK metaDataPK;
    @PrimarySortedField
    @Searchable(label = "entity.metadata.searchable.valueChr")
    @Column(name = "VALUE_CHR")
    private String valueChr;
    @JoinColumns({ @JoinColumn(name = "COD_META",
            referencedColumnName = "COD_META", insertable = false,
            updatable = false), @JoinColumn(name = "COD_MEMBER",
            referencedColumnName = "MEMBER_COD", insertable = false,
            updatable = false) })
    @ManyToOne(optional = false)
    private MetaMember metaMember;
    @JoinColumns({ @JoinColumn(name = "COD_META",
            referencedColumnName = "COD_META", insertable = false,
            updatable = false), @JoinColumn(name = "COD_CLIENT",
            referencedColumnName = "COD_CLIENT", insertable = false,
            updatable = false) })
    @ManyToOne(optional = false)
    private MetaClient metaClient;

    // bi-directional many-to-many association to ShiftPeriod
    @ManyToMany(mappedBy = "metaDatas")
    private List<ShiftPeriod> shiftPeriods;

    @ManyToMany(mappedBy = "metaData",
            cascade = { CascadeType.MERGE, CascadeType.REMOVE })
    private List<Userphone> userphones;

    // @OneToOne(optional = true)
    // @NotFound(action = NotFoundAction.IGNORE)
    // @JoinColumn(name = "COD_MAP_MARK", referencedColumnName="MAP_MARK_COD")
    //

    @JoinColumn(name = "cod_map_mark", referencedColumnName = "map_mark_cod")
    @OneToOne(optional = true)
    private MapMark mapMark;

    @ManyToMany(mappedBy = "metaData",
            cascade = { CascadeType.MERGE, CascadeType.REMOVE })
    private List<MetaData> metadatas;

    @ManyToMany
    @JoinTable(
            name = "META_DATA_META_DATA",
            joinColumns = { @JoinColumn(name = "COD_CLIENT_2",
                    referencedColumnName = "COD_CLIENT"), @JoinColumn(
                    name = "COD_MEMBER_2", referencedColumnName = "COD_MEMBER"), @JoinColumn(
                    name = "COD_META_2", referencedColumnName = "COD_META"), @JoinColumn(
                    name = "CODE_CHR_2", referencedColumnName = "CODE_CHR") },
            inverseJoinColumns = { @JoinColumn(name = "COD_CLIENT_1",
                    referencedColumnName = "COD_CLIENT"), @JoinColumn(
                    name = "COD_MEMBER_1", referencedColumnName = "COD_MEMBER"), @JoinColumn(
                    name = "COD_META_1", referencedColumnName = "COD_META"), @JoinColumn(
                    name = "CODE_CHR_1", referencedColumnName = "CODE_CHR") })
    private List<MetaData> metaData;

    public MetaData() {
    }

    public MetaData(MetaDataPK metaDataPK) {
        this.metaDataPK = metaDataPK;
    }

    public MetaData(Long codClient, Long codMeta, Long codMember, String codeChr) {
        this.metaDataPK = new MetaDataPK(codClient, codMeta, codMember, codeChr);
    }

    public MetaDataPK getMetaDataPK() {
        return metaDataPK;
    }

    public void setMetaDataPK(MetaDataPK metaDataPK) {
        this.metaDataPK = metaDataPK;
    }

    public String getValueChr() {
        return valueChr;
    }

    public void setValueChr(String valueChr) {
        this.valueChr = valueChr;
    }

    public MetaMember getMetaMember() {
        return metaMember;
    }

    public void setMetaMember(MetaMember metaMember) {
        this.metaMember = metaMember;
    }

    public MetaClient getMetaClient() {
        return metaClient;
    }

    public void setMetaClient(MetaClient metaClient) {
        this.metaClient = metaClient;
    }

    public List<ShiftPeriod> getShiftPeriods() {
        return shiftPeriods;
    }

    public void setShiftPeriods(List<ShiftPeriod> shiftPeriods) {
        this.shiftPeriods = shiftPeriods;
    }

    public List<Userphone> getUserphones() {
        return userphones;
    }

    public void setUserphones(List<Userphone> userphones) {
        this.userphones = userphones;
    }

    public List<MetaData> getMetadatas() {
        return metadatas;
    }

    public void setMetadatas(List<MetaData> metadatas) {
        this.metadatas = metadatas;
    }

    public List<MetaData> getMetaData() {
        return metaData;
    }

    public void setMetaData(List<MetaData> metaData) {
        this.metaData = metaData;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MetaData other = (MetaData) obj;
        if (this.metaDataPK != other.metaDataPK
            && (this.metaDataPK == null || !this.metaDataPK.equals(other.metaDataPK))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash
            + (this.metaDataPK != null ? this.metaDataPK.hashCode() : 0);

        return hash;
    }

    @Override
    public String toString() {
        return "MetaData[metaDataPK=" + metaDataPK + "]";
    }

    public MapMark getMapMark() {
        return mapMark;
    }

    public void setMapMark(MapMark mapMark) {
        this.mapMark = mapMark;
    }
}
