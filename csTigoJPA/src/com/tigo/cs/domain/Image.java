package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;

import com.tigo.cs.commons.jpa.PrimarySortedField;

@Entity
public class Image implements Serializable {

    private static final long serialVersionUID = 6594237269251079594L;

    @Id
    @PrimarySortedField
    @SequenceGenerator(name = "IMAGE_IMAGECOD_GENERATOR",
            sequenceName = "IMAGE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "IMAGE_IMAGECOD_GENERATOR")
    @Column(name = "IMAGE_COD")
    private Long imageCod;

    @Lob()
    @Column(name = "BIG_IMAGE_BYT")
    private byte[] bigImageByt;

    @Column(name = "DESCRIPTION_CHR")
    private String descriptionChr;

    @Lob()
    @Column(name = "SMALL_IMAGE_BYT")
    private byte[] smallImageByt;

    @Column(name = "FILENAME_SMALL_IMG")
    private String filenameSmallImage;

    @Column(name = "FILENAME_BIG_IMG")
    private String filenameBigImage;

    // bi-directional many-to-many association to New
    @ManyToMany(mappedBy = "images")
    private List<New> news;

    public Image() {
    }

    public Long getImageCod() {
        return this.imageCod;
    }

    public void setImageCod(Long imageCod) {
        this.imageCod = imageCod;
    }

    public byte[] getBigImageByt() {
        return this.bigImageByt;
    }

    public void setBigImageByt(byte[] bigImageByt) {
        this.bigImageByt = bigImageByt;
    }

    public String getDescriptionChr() {
        return this.descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public byte[] getSmallImageByt() {
        return this.smallImageByt;
    }

    public void setSmallImageByt(byte[] smallImageByt) {
        this.smallImageByt = smallImageByt;
    }

    public List<New> getNews() {
        return this.news;
    }

    public void setNews(List<New> news) {
        this.news = news;
    }

    public String getFilenameSmallImage() {
        return filenameSmallImage;
    }

    public void setFilenameSmallImage(String filenameSmallImage) {
        this.filenameSmallImage = filenameSmallImage;
    }

    public String getFilenameBigImage() {
        return filenameBigImage;
    }

    public void setFilenameBigImage(String filenameBigImage) {
        this.filenameBigImage = filenameBigImage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((imageCod == null) ? 0 : imageCod.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Image other = (Image) obj;
        if (imageCod == null) {
            if (other.imageCod != null)
                return false;
        } else if (!imageCod.equals(other.imageCod))
            return false;
        return true;
    }

}