package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.PrimarySortedField;

@Entity
@Table(name = "meta_client")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(
        name = "MetaClient.findByCodMetaAndCodClient",
        query = "SELECT m FROM MetaClient m WHERE m.metaClientPK.codMeta = :codMeta AND m.metaClientPK.codClient = :codClient ORDER BY m.metaClientPK.codMeta"), @NamedQuery(
        name = "MetaClient.findMetaByCodClient",
        query = "SELECT m.meta FROM MetaClient m WHERE m.metaClientPK.codClient = :codClient ORDER BY m.metaClientPK.codMeta"), @NamedQuery(
        name = "MetaClient.findMetaByCodClientAndEnabled",
        query = "SELECT m.meta FROM MetaClient m WHERE m.metaClientPK.codClient = :codClient AND m.enabledChr = :enabledChr ORDER BY m.metaClientPK.codMeta"), @NamedQuery(
        name = "MetaClient.findByCodClientAndEnabled",
        query = "SELECT m FROM MetaClient m WHERE m.metaClientPK.codClient = :codClient AND m.enabledChr = :enabledChr ORDER BY m.metaClientPK.codMeta") })
public class MetaClient implements Serializable {

    private static final long serialVersionUID = -5882783336271715278L;
    @EmbeddedId
    protected MetaClientPK metaClientPK;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "metaClient")
    private List<MetaData> metaDataList;
    @JoinColumn(name = "COD_META", referencedColumnName = "META_COD",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    @PrimarySortedField
    private Meta meta;
    @JoinColumn(name = "COD_CLIENT", referencedColumnName = "CLIENT_COD",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Client client;
    @Column(name = "enabled_chr")
    private Boolean enabledChr;

    public MetaClient() {
    }

    public MetaClient(MetaClientPK metaClientPK) {
        this.metaClientPK = metaClientPK;
    }

    public MetaClient(Long codMeta, Long codClient) {
        this.metaClientPK = new MetaClientPK(codMeta, codClient);
    }

    public MetaClientPK getMetaClientPK() {
        return metaClientPK;
    }

    public void setMetaClientPK(MetaClientPK metaClientPK) {
        this.metaClientPK = metaClientPK;
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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Boolean getEnabledChr() {
        return enabledChr;
    }

    public void setEnabledChr(Boolean enabledChr) {
        this.enabledChr = enabledChr;
    }

    @PrePersist
    protected void prePersist() {
        setEnabledChr(true);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MetaClient other = (MetaClient) obj;
        if (this.metaClientPK != other.metaClientPK
            && (this.metaClientPK == null || !this.metaClientPK.equals(other.metaClientPK))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash
            + (this.metaClientPK != null ? this.metaClientPK.hashCode() : 0);

        return hash;
    }

    @Override
    public String toString() {
        return "MetaClient[metaClientPK=" + metaClientPK + "]";
    }
}
