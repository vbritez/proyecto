package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "functionality")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(name = "Functionality.findAllSelectable",
        query = "SELECT f FROM Functionality f WHERE f.functionalityCod > 0 ") })
public class Functionality implements Serializable {

    private static final long serialVersionUID = -6389926523822613491L;
    @Id
    @SequenceGenerator(name = "functionalityGen",
            sequenceName = "functionality_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "functionalityGen")
    @Basic(optional = false)
    @Column(name = "functionality_cod")
    @Searchable(label = "entity.functionality.searchable.functionalityCod")
    private Long functionalityCod;
    @PrimarySortedField
    @Searchable(label = "entity.functionality.searchable.descriptionChr")
    @Column(name = "description_chr")
    @Basic(optional = false)
    private String descriptionChr;
    @ManyToMany(mappedBy = "functionalitySet")
    private List<Service> serviceList;

    public Functionality() {
    }

    public Functionality(Long functionalityCod, String descriptionChr) {
        this.functionalityCod = functionalityCod;
        this.descriptionChr = descriptionChr;
    }

    public String getDescriptionChr() {
        return descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public Long getFunctionalityCod() {
        return functionalityCod;
    }

    public void setFunctionalityCod(Long functionalityCod) {
        this.functionalityCod = functionalityCod;
    }

    public List<Service> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<Service> serviceList) {
        this.serviceList = serviceList;
    }

    @Override
    public boolean equals(Object obj) {
        /*
         * solo se deben comparar las claves de la entidad
         */
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Functionality other = (Functionality) obj;
        if (this.functionalityCod != other.functionalityCod
            && (this.functionalityCod == null || !this.functionalityCod.equals(other.functionalityCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71
            * hash
            + (this.functionalityCod != null ? this.functionalityCod.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Functionality[id=" + functionalityCod + "]";
    }
}
