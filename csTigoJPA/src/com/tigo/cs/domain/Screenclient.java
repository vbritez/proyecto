package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.PrimarySortedField;

@Entity
@Table(name = "screenclient")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(
        name = "Screenclient.findByNosecurityChr",
        query = "SELECT s FROM Screenclient s WHERE s.nosecurityChr = :nosecurityChr"), @NamedQuery(
        name = "Screenclient.findByPageChr",
        query = "SELECT s FROM Screenclient s WHERE s.pageChr = :pageChr") })
public class Screenclient implements Serializable, Comparable<Screenclient> {

    private static final long serialVersionUID = -24280848347361452L;
    @Id
    @Basic(optional = false)
    @Column(name = "SCREENCLIENT_COD")
    private Long screenclientCod;
    @Basic(optional = false)
    @Column(name = "DESCRIPTION_CHR")
    @PrimarySortedField
    private String descriptionChr;
    @Basic(optional = false)
    @Column(name = "PAGE_CHR")
    private String pageChr;
    @Basic(optional = false)
    @Column(name = "ORDER_NUM")
    private long orderNum;
    @Basic(optional = false)
    @Column(name = "NOSECURITY_CHR")
    private Boolean nosecurityChr;
    @Basic(optional = false)
    @Column(name = "SHOWONMENU_CHR")
    private Boolean showonmenuChr;
    @JoinColumn(name = "COD_MODULECLIENT",
            referencedColumnName = "MODULECLIENT_COD")
    @ManyToOne(optional = false)
    private Moduleclient codModuleclient;
    @ManyToOne
    @JoinColumn(name = "cod_service", nullable = true, insertable = false,
            updatable = false)
    private Service service;
    @ManyToOne
    @JoinColumn(name = "cod_meta", nullable = true, insertable = false,
            updatable = false)
    private Meta meta;
    @OneToMany(
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH },
            mappedBy = "screenclient")
    private List<RoleClientScreen> roleClientScreenList;
    @OneToMany(
            mappedBy = "screenClient",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Collection<Auditory> auditoryCollection;

    public Screenclient() {
    }

    public Screenclient(Long screenclientCod) {
        this.screenclientCod = screenclientCod;
    }

    public Screenclient(Long screenclientCod, String descriptionChr,
            String pageChr, long orderNum, Boolean noNeChr,
            Boolean showonmenuChr) {
        this.screenclientCod = screenclientCod;
        this.descriptionChr = descriptionChr;
        this.pageChr = pageChr;
        this.orderNum = orderNum;
        this.nosecurityChr = noNeChr;
        this.showonmenuChr = showonmenuChr;
    }

    public Long getScreenclientCod() {
        return screenclientCod;
    }

    public void setScreenclientCod(Long screenclientCod) {
        this.screenclientCod = screenclientCod;
    }

    public String getDescriptionChr() {
        return descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public String getPageChr() {
        return pageChr;
    }

    public void setPageChr(String pageChr) {
        this.pageChr = pageChr;
    }

    public long getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(long orderNum) {
        this.orderNum = orderNum;
    }

    public Boolean getNosecurityChr() {
        return nosecurityChr;
    }

    public void setNosecurityChr(Boolean nosecurityChr) {
        this.nosecurityChr = nosecurityChr;
    }

    public Boolean getShowonmenuChr() {
        return showonmenuChr;
    }

    public void setShowonmenuChr(Boolean showonmenuChr) {
        this.showonmenuChr = showonmenuChr;
    }

    public Moduleclient getCodModuleclient() {
        return codModuleclient;
    }

    public void setCodModuleclient(Moduleclient codModuleclient) {
        this.codModuleclient = codModuleclient;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public List<RoleClientScreen> getRoleClientScreenList() {
        return roleClientScreenList;
    }

    public void setRoleClientScreenList(List<RoleClientScreen> roleClientScreenList) {
        this.roleClientScreenList = roleClientScreenList;
    }

    public Collection<Auditory> getAuditoryCollection() {
        return auditoryCollection;
    }

    public void setAuditoryCollection(Collection<Auditory> auditoryCollection) {
        this.auditoryCollection = auditoryCollection;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Screenclient other = (Screenclient) obj;
        if (this.screenclientCod != other.screenclientCod
            && (this.screenclientCod == null || !this.screenclientCod.equals(other.screenclientCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79
            * hash
            + (this.screenclientCod != null ? this.screenclientCod.hashCode() : 0);
        hash = 79
            * hash
            + (this.descriptionChr != null ? this.descriptionChr.hashCode() : 0);
        hash = 79 * hash + (this.pageChr != null ? this.pageChr.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "com.tigo.cs.domain.Screenclient[screenclientCod="
            + screenclientCod + "]";
    }

    @Override
    public int compareTo(Screenclient o) {
        return Long.valueOf(this.orderNum - o.orderNum).intValue();
    }
}
