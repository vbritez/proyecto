package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "invalidmessage")
public class Invalidmessage implements Serializable {

    private static final long serialVersionUID = -8990691345058793685L;
    @Id
    @SequenceGenerator(name = "invalidmessageGen",
            sequenceName = "invalidmessage_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "invalidmessageGen")
    @Basic(optional = false)
    @Column(name = "invalidmessage_cod")
    private Long invalidmessageCod;
    @NotNull
    @Column(name = "datein_dat")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateinDat;
    @Basic(optional = false)
    @Column(name = "dateout_dat")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateoutDat;
    @Basic(optional = false)
    @Column(name = "destination_chr")
    private String destinationChr;
    @Basic(optional = false)
    @Column(name = "lengthin_num")
    private int lengthinNum;
    @Basic(optional = false)
    @Column(name = "lengthout_num")
    private int lengthoutNum;
    @Basic(optional = false)
    @Column(name = "messagein_chr")
    private String messageinChr;
    @Basic(optional = false)
    @Column(name = "messageout_chr")
    private String messageoutChr;
    @Basic(optional = false)
    @Column(name = "origin_chr")
    private String originChr;
    @JoinColumn(name = "cod_cell", referencedColumnName = "cell_cod")
    @ManyToOne(optional = false)
    private Cell cell;

    public Invalidmessage() {
    }

    public Invalidmessage(Long invalidmessageCod) {
        this.invalidmessageCod = invalidmessageCod;
    }

    public Invalidmessage(Long invalidmessageCod, Date dateinDat,
            Date dateoutDat, String destinationChr, int lengthinNum,
            int lengthoutNum, String messageinChr, String messageoutChr,
            String originChr, double azimuthNum, int timingadvanceNum) {
        this.invalidmessageCod = invalidmessageCod;
        this.dateinDat = dateinDat;
        this.dateoutDat = dateoutDat;
        this.destinationChr = destinationChr;
        this.lengthinNum = lengthinNum;
        this.lengthoutNum = lengthoutNum;
        this.messageinChr = messageinChr;
        this.messageoutChr = messageoutChr;
        this.originChr = originChr;
    }

    public Long getInvalidmessageCod() {
        return invalidmessageCod;
    }

    public void setInvalidmessageCod(Long invalidmessageCod) {
        this.invalidmessageCod = invalidmessageCod;
    }

    public Date getDateinDat() {
        return dateinDat;
    }

    public void setDateinDat(Date dateinDat) {
        this.dateinDat = dateinDat;
    }

    public Date getDateoutDat() {
        return dateoutDat;
    }

    public void setDateoutDat(Date dateoutDat) {
        this.dateoutDat = dateoutDat;
    }

    public String getDestinationChr() {
        return destinationChr;
    }

    public void setDestinationChr(String destinationChr) {
        this.destinationChr = destinationChr;
    }

    public int getLengthinNum() {
        return lengthinNum;
    }

    public void setLengthinNum(int lengthinNum) {
        this.lengthinNum = lengthinNum;
    }

    public int getLengthoutNum() {
        return lengthoutNum;
    }

    public void setLengthoutNum(int lengthoutNum) {
        this.lengthoutNum = lengthoutNum;
    }

    public String getMessageinChr() {
        return messageinChr;
    }

    public void setMessageinChr(String messageinChr) {
        this.messageinChr = messageinChr;
    }

    public String getMessageoutChr() {
        return messageoutChr;
    }

    public void setMessageoutChr(String messageoutChr) {
        this.messageoutChr = messageoutChr;
    }

    public String getOriginChr() {
        return originChr;
    }

    public void setOriginChr(String originChr) {
        this.originChr = originChr;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Invalidmessage other = (Invalidmessage) obj;
        if (this.invalidmessageCod != other.invalidmessageCod
            && (this.invalidmessageCod == null || !this.invalidmessageCod.equals(other.invalidmessageCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29
            * hash
            + (this.invalidmessageCod != null ? this.invalidmessageCod.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return messageinChr.concat(" (Cod.: ").concat(
                invalidmessageCod.toString()).concat(")");
    }
}
