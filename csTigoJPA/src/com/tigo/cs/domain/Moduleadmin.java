package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "moduleadmin")
public class Moduleadmin implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "moduleadminGen",
            sequenceName = "moduleadmin_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "moduleadminGen")
    @Basic(optional = false)
    @Column(name = "moduleadmin_cod")
    private Long moduleadminCod;
    @Basic(optional = false)
    @Column(name = "description_chr")
    private String descriptionChr;
    @Basic(optional = false)
    @Column(name = "order_num")
    private Integer orderNum;
    @OneToMany(
            mappedBy = "moduleadmin",
            fetch = FetchType.EAGER,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Collection<Screen> screenCollection;

    public Moduleadmin() {
    }

    public Moduleadmin(Long moduleadminCod) {
        this.moduleadminCod = moduleadminCod;
    }

    public Moduleadmin(Long moduleadminCod, String descriptionChr) {
        this.moduleadminCod = moduleadminCod;
        this.descriptionChr = descriptionChr;
    }

    public Long getModuleadminCod() {
        return moduleadminCod;
    }

    public void setModuleadminCod(Long moduleadminCod) {
        this.moduleadminCod = moduleadminCod;
    }

    public String getDescriptionChr() {
        return descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Collection<Screen> getScreenCollection() {
        return screenCollection;
    }

    public void setScreenCollection(Collection<Screen> screenCollection) {
        this.screenCollection = screenCollection;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Moduleadmin other = (Moduleadmin) obj;
        if (this.moduleadminCod != other.moduleadminCod
            && (this.moduleadminCod == null || !this.moduleadminCod.equals(other.moduleadminCod))) {
            return false;
        }
        if ((this.descriptionChr == null) ? (other.descriptionChr != null) : !this.descriptionChr.equals(other.descriptionChr)) {
            return false;
        }
        if (this.orderNum != other.orderNum
            && (this.orderNum == null || !this.orderNum.equals(other.orderNum))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83
            * hash
            + (this.moduleadminCod != null ? this.moduleadminCod.hashCode() : 0);
        hash = 83
            * hash
            + (this.descriptionChr != null ? this.descriptionChr.hashCode() : 0);
        hash = 83 * hash
            + (this.orderNum != null ? this.orderNum.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return descriptionChr.concat(" (Cod.: ").concat(
                moduleadminCod.toString()).concat(")");
    }
}
