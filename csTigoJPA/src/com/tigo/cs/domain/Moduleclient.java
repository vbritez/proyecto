package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "moduleclient")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(name = "Moduleclient.findAll",
        query = "SELECT m FROM Moduleclient m"), @NamedQuery(
        name = "Moduleclient.findByModuleclientCod",
        query = "SELECT m FROM Moduleclient m WHERE m.moduleclientCod = :moduleclientCod"), @NamedQuery(
        name = "Moduleclient.findByDescriptionChr",
        query = "SELECT m FROM Moduleclient m WHERE m.descriptionChr = :descriptionChr"), @NamedQuery(
        name = "Moduleclient.findByOrderNum",
        query = "SELECT m FROM Moduleclient m WHERE m.orderNum = :orderNum") })
public class Moduleclient implements Serializable {

    private static final long serialVersionUID = 7454290344323295994L;
    @Id
    @Basic(optional = false)
    @Column(name = "MODULECLIENT_COD")
    private Long moduleclientCod;
    @Basic(optional = false)
    @Column(name = "DESCRIPTION_CHR")
    private String descriptionChr;
    @Basic(optional = false)
    @Column(name = "ORDER_NUM")
    private long orderNum;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codModuleclient",
            fetch = FetchType.EAGER)
    private List<Screenclient> screenclientList;

    public Moduleclient() {
    }

    public Moduleclient(Long moduleclientCod) {
        this.moduleclientCod = moduleclientCod;
    }

    public Moduleclient(Long moduleclientCod, String descriptionChr,
            long orderNum) {
        this.moduleclientCod = moduleclientCod;
        this.descriptionChr = descriptionChr;
        this.orderNum = orderNum;
    }

    public Long getModuleclientCod() {
        return moduleclientCod;
    }

    public void setModuleclientCod(Long moduleclientCod) {
        this.moduleclientCod = moduleclientCod;
    }

    public String getDescriptionChr() {
        return descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public long getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(long orderNum) {
        this.orderNum = orderNum;
    }

    public List<Screenclient> getScreenclientList() {
        return screenclientList;
    }

    public void setScreenclientList(List<Screenclient> screenclientList) {
        this.screenclientList = screenclientList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Moduleclient other = (Moduleclient) obj;
        if (this.moduleclientCod != other.moduleclientCod
            && (this.moduleclientCod == null || !this.moduleclientCod.equals(other.moduleclientCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59
            * hash
            + (this.moduleclientCod != null ? this.moduleclientCod.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Moduleclient[moduleclientCod=" + moduleclientCod + "]";
    }
}
