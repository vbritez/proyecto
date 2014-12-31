package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "meta")
public class Meta implements Serializable {

    private static final long serialVersionUID = -4801568937462184891L;
    @Id
    @SequenceGenerator(name = "metaGen", sequenceName = "meta_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "metaGen")
    @Basic(optional = false)
    @Column(name = "META_COD")
    @Searchable(label = "entity.meta.metaCode.searchable.Message")
    private Long metaCod;
    @PrimarySortedField
    @Searchable(label = "entity.meta.meta.searchable.Message")
    @NotEmpty(message = "{entity.meta.constraint.descriptionChr.NotEmpty}")
    @Basic(optional = false)
    @Column(name = "DESCRIPTION_CHR")
    private String descriptionChr;
    @OneToMany(mappedBy = "meta")
    private List<MetaMember> metaMemberList;
    @OneToMany(mappedBy = "meta")
    private List<MetaClient> metaClientList;
    @OneToMany(mappedBy = "meta")
    private List<Screenclient> screenClientList;

    public Meta() {
    }

    public Meta(Long metaCod) {
        this.metaCod = metaCod;
    }

    public Meta(Long metaCod, String descriptionChr) {
        this.metaCod = metaCod;
        this.descriptionChr = descriptionChr;
    }

    public Long getMetaCod() {
        return metaCod;
    }

    public void setMetaCod(Long metaCod) {
        this.metaCod = metaCod;
    }

    public String getDescriptionChr() {
        return descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public List<MetaMember> getMetaMemberList() {
        return metaMemberList;
    }

    public void setMetaMemberList(List<MetaMember> metaMemberList) {
        this.metaMemberList = metaMemberList;
    }

    public List<MetaClient> getMetaClientList() {
        return metaClientList;
    }

    public void setMetaClientList(List<MetaClient> metaClientList) {
        this.metaClientList = metaClientList;
    }

    public List<Screenclient> getScreenClientList() {
        return screenClientList;
    }

    public void setScreenClientList(List<Screenclient> screenClientList) {
        this.screenClientList = screenClientList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Meta other = (Meta) obj;
        if (this.metaCod != other.metaCod
            && (this.metaCod == null || !this.metaCod.equals(other.metaCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.metaCod != null ? this.metaCod.hashCode() : 0);
        hash = 29
            * hash
            + (this.descriptionChr != null ? this.descriptionChr.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Meta[metaCod=" + metaCod + "]";
    }
}
