package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "map_mark")
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries({ @NamedQuery(name = "MapMark.findByCodClient",
        query = "SELECT m FROM MapMark m WHERE m.client.clientCod = :codClient"), @NamedQuery(
        name = "MapMark.findByLatitudeNumAndLongitudeNumAndClient",
        query = "SELECT m FROM MapMark m WHERE m.latitudeNum = :latitudeNum AND m.longitudeNum = :longitudeNum AND m.client = :client"), @NamedQuery(
        name = "MapMark.deleteByLatitudeNumAndLongitudeNumAndClient",
        query = "DELETE FROM MapMark m WHERE m.latitudeNum = :latitudeNum AND m.longitudeNum = :longitudeNum AND m.client = :client") })
public class MapMark implements Serializable {

    private static final long serialVersionUID = -6476353641678993804L;
    @Id
    @SequenceGenerator(name = "MAP_MARK_GENERATOR",
            sequenceName = "MAP_MARK_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "MAP_MARK_GENERATOR")
    @Column(name = "MAP_MARK_COD", unique = true, nullable = false,
            precision = 19)
    private Long mapMarkCod;
    @Column(name = "CREATEDATE_DAT", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date createdateDat;
    @Column(name = "DESCRIPTION_CHR", length = 255)
    private String descriptionChr;
    @Column(name = "LATITUDE_NUM", nullable = false, precision = 22, scale = 10)
    private Double latitudeNum;
    @Column(name = "LONGITUDE_NUM", nullable = false, precision = 22,
            scale = 10)
    private Double longitudeNum;
    @Column(name = "TITLE_CHR", length = 255)
    private String titleChr;
    // bi-directional many-to-one association to Client
    @JoinColumn(name = "COD_CLIENT", referencedColumnName = "CLIENT_COD")
    @ManyToOne(optional = false)
    private Client client;
    // bi-directional many-to-one association to RouteDetail
    @OneToMany(mappedBy = "mapMark")
    private Set<RouteDetail> routeDetails;
    @JoinColumn(name = "COD_USERWEB", referencedColumnName = "USERWEB_COD",
            nullable = true)
    @ManyToOne
    private Userweb userweb;

    @OneToOne(mappedBy = "mapMark", cascade = CascadeType.REMOVE)
    private MetaData metaData;

    public MapMark() {
    }

    public Long getMapMarkCod() {
        return this.mapMarkCod;
    }

    public void setMapMarkCod(Long mapMarkCod) {
        this.mapMarkCod = mapMarkCod;
    }

    public Date getCreatedateDat() {
        return createdateDat;
    }

    public void setCreatedateDat(Date createdateDat) {
        this.createdateDat = createdateDat;
    }

    public String getDescriptionChr() {
        return this.descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public Double getLatitudeNum() {
        return this.latitudeNum;
    }

    public void setLatitudeNum(Double latitudeNum) {
        this.latitudeNum = latitudeNum;
    }

    public Double getLongitudeNum() {
        return this.longitudeNum;
    }

    public void setLongitudeNum(Double longitudeNum) {
        this.longitudeNum = longitudeNum;
    }

    public String getTitleChr() {
        return this.titleChr;
    }

    public void setTitleChr(String titleChr) {
        this.titleChr = titleChr;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Userweb getUserweb() {
        return userweb;
    }

    public void setUserweb(Userweb userweb) {
        this.userweb = userweb;
    }

    public Set<RouteDetail> getRouteDetails() {
        return this.routeDetails;
    }

    public void setRouteDetails(Set<RouteDetail> routeDetails) {
        this.routeDetails = routeDetails;
    }

    @PrePersist
    public void setCreateDate() {
        this.createdateDat = new Date();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MapMark other = (MapMark) obj;
        if (this.mapMarkCod != other.mapMarkCod
            && (this.mapMarkCod == null || !this.mapMarkCod.equals(other.mapMarkCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash
            + (this.mapMarkCod != null ? this.mapMarkCod.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "MapMark[mapMarkCod=" + mapMarkCod + "]";
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }
}
