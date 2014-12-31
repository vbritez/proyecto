package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "cell")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Cell implements Serializable {

    private static final long serialVersionUID = -3163421299739510972L;
    @Id
    @SequenceGenerator(name = "cellGen", sequenceName = "cell_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cellGen")
    @Basic(optional = false)
    @Column(name = "cell_cod")
    @Searchable(label = "entity.cell.searchable.cellCod")
    @PrimarySortedField
    private Long cellCod;
    @Basic(optional = false)
    @Column(name = "identity_num")
    @Searchable(label = "entity.cell.searchable.identityNumber")
    private Integer identityNum;
    @Basic(optional = false)
    @Column(name = "site_chr")
    @Searchable(label = "entity.cell.searchable.siteChr")
    private String siteChr;
    @Basic(optional = false)
    @Column(name = "azimuth_num")
    private Integer azimuthNum;
    @Basic(optional = false)
    @Column(name = "latitude_num")
    private Double latitudeNum;
    @Basic(optional = false)
    @Column(name = "longitude_num")
    private Double longitudeNum;
    @Searchable(label = "entity.cell.searchable.departmentChr")
    @Column(name = "department_chr")
    private String departmentChr;
    @Searchable(label = "entity.cell.searchable.distictNameChr")
    @Column(name = "district_name_chr")
    private String districtNameChr;
    @Basic(optional = false)
    @Column(name = "version_num")
    private Integer versionNum;
    // @Basic(optional = true)
    // @Column(name = "recorddate_dat")
    // @Temporal(TemporalType.TIMESTAMP)
    // private Date recorddateDat;

    @OneToMany(
            mappedBy = "cell",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Collection<Message> messageCollection;
    @OneToMany(
            mappedBy = "cell",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Collection<Invalidmessage> invalidmessageCollection;

    @Column(name = "UCID")
    private Integer ucid;

    public Cell() {
    }

    public Cell(Long cellCod) {
        this.cellCod = cellCod;
    }

    public Cell(Long cellCod, Integer identityNum, String siteChr,
            Integer azimuthNum, Double latitudeNum, Double longitudeNum) {
        this.cellCod = cellCod;
        this.identityNum = identityNum;
        this.siteChr = siteChr;
        this.azimuthNum = azimuthNum;
        this.latitudeNum = latitudeNum;
        this.longitudeNum = longitudeNum;
    }

    public Long getCellCod() {
        return cellCod;
    }

    public void setCellCod(Long cellCod) {
        this.cellCod = cellCod;
    }

    public Integer getIdentityNum() {
        return identityNum;
    }

    public void setIdentityNum(Integer identityNum) {
        this.identityNum = identityNum;
    }

    public String getSiteChr() {
        return siteChr;
    }

    public void setSiteChr(String siteChr) {
        this.siteChr = siteChr;
    }

    public Integer getAzimuthNum() {
        return azimuthNum;
    }

    public void setAzimuthNum(Integer azimuthNum) {
        this.azimuthNum = azimuthNum;
    }

    public Double getLatitudeNum() {
        return latitudeNum;
    }

    public void setLatitudeNum(Double latitudeNum) {
        this.latitudeNum = latitudeNum;
    }

    public Double getLongitudeNum() {
        return longitudeNum;
    }

    public void setLongitudeNum(Double longitudeNum) {
        this.longitudeNum = longitudeNum;
    }

    public String getDepartmentChr() {
        return departmentChr;
    }

    public void setDepartmentChr(String departmentChr) {
        this.departmentChr = departmentChr;
    }

    public String getDistrictNameChr() {
        return districtNameChr;
    }

    public void setDistrictNameChr(String districtNameChr) {
        this.districtNameChr = districtNameChr;
    }

    public Integer getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(Integer versionNum) {
        this.versionNum = versionNum;
    }

    public Collection<Message> getMessageCollection() {
        return messageCollection;
    }

    public void setMessageCollection(Collection<Message> messageCollection) {
        this.messageCollection = messageCollection;
    }

    public Collection<Invalidmessage> getInvalidmessageCollection() {
        return invalidmessageCollection;
    }

    public void setInvalidmessageCollection(Collection<Invalidmessage> invalidmessageCollection) {
        this.invalidmessageCollection = invalidmessageCollection;
    }

    // public Date getRecorddateDat() {
    // return recorddateDat;
    // }
    //
    // public void setRecorddateDat(Date recorddateDat) {
    // this.recorddateDat = recorddateDat;
    // }

    public Integer getUcid() {
        return ucid;
    }

    public void setUcid(Integer ucid) {
        this.ucid = ucid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Cell other = (Cell) obj;
        if (this.cellCod != other.cellCod
            && (this.cellCod == null || !this.cellCod.equals(other.cellCod))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.cellCod != null ? this.cellCod.hashCode() : 0);

        return hash;
    }

    @Override
    public String toString() {
        return "Cell [cellCod=" + cellCod + ", identityNum=" + identityNum
            + ", siteChr=" + siteChr + ", azimuthNum=" + azimuthNum
            + ", latitudeNum=" + latitudeNum + ", longitudeNum=" + longitudeNum
            + ", departmentChr=" + departmentChr + ", districtNameChr="
            + districtNameChr + ", versionNum=" + versionNum + ", ucid=" + ucid
            + "]";
    }

    /*
     * Retorna TRUE si son iguales
     */
    public boolean compareTo(Cell obj) {

        boolean result = true;

        /*
         * if (((obj.getAzimuthNum() == null && azimuthNum == null) ||
         * obj.getAzimuthNum().equals( azimuthNum)) && ((obj.getLatitudeNum() ==
         * null && latitudeNum == null) || obj.getLatitudeNum().equals(
         * latitudeNum)) && ((obj.getLongitudeNum() == null && longitudeNum ==
         * null) || obj.getLongitudeNum().equals( longitudeNum)) &&
         * ((obj.getUcid() == null && ucid == null) || obj.getUcid().equals(
         * ucid))) { return true;
         */
        /*
         * if (azimuthNum != null && latitudeNum != null && longitudeNum != null
         * && (obj == null || (obj.getAzimuthNum() != null &&
         * !obj.getAzimuthNum().equals( azimuthNum)) || (obj.getAzimuthNum() ==
         * null && azimuthNum != null) || (obj.getLatitudeNum() != null &&
         * !obj.getLatitudeNum().equals( longitudeNum)) || (obj.getLatitudeNum()
         * == null && longitudeNum != null) || (obj.getLongitudeNum() != null &&
         * !obj.getLongitudeNum().equals( latitudeNum)) ||
         * (obj.getLongitudeNum() == null && latitudeNum != null) // ||
         * (obj.getUcid() != null && ucid != null) || (obj.getUcid() == // null
         * && ucid != null)) )) {
         */

        // return true;
        // }

        if (obj != null) {
            result = true;
        } else {
            result = false;
            return result;
        }

        if (azimuthNum != null && obj.getAzimuthNum() != null && result) {
            result = obj.getAzimuthNum().equals(azimuthNum);
        } else {
            result = false;
            return result;
        }

        if (latitudeNum != null && obj.getLatitudeNum() != null && result) {

            result = obj.getLatitudeNum().equals(latitudeNum);

        } else {
            result = false;
            return result;
        }

        if (longitudeNum != null && obj.getLongitudeNum() != null && result) {

            result = obj.getLatitudeNum().equals(latitudeNum);

        } else {

            result = false;
            return result;
        }

        return result;
    }
    // @Override
    // public String toString() {
    // return
    // getSiteChr().concat(" (Cod.: ").concat(getCellCod().toString()).concat(
    // ")");
    // }

}
