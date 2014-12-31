package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "NEWS")
@NamedQueries({ @NamedQuery(name = "New.findByNewCod",
        query = "SELECT n FROM New n WHERE n.newCod = :newCod") })
public class New implements Serializable {

    private static final long serialVersionUID = -3334040199465789377L;

    @Id
    @PrimarySortedField
    @Searchable(label = "entity.news.searchable.newCod")
    @SequenceGenerator(name = "NEWS_NEWSCOD_GENERATOR",
            sequenceName = "NEWS_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "NEWS_NEWSCOD_GENERATOR")
    @Column(name = "NEWS_COD")
    private Long newCod;

    @Column(name = "BODYNEWS_CHR")
    private String bodynewsChr;

    @Column(name = "SUBTITLE_CHR")
    @Searchable(label = "entity.news.searchable.subtitleChr")
    private String subtitleChr;

    @Column(name = "TITLE_CHR")
    @Searchable(label = "entity.news.searchable.titleChr")
    private String titleChr;

    // bi-directional many-to-many association to Image
    @ManyToMany
    @JoinTable(name = "NEWS_IMAGE", joinColumns = { @JoinColumn(
            name = "NEWS_COD") }, inverseJoinColumns = { @JoinColumn(
            name = "IMAGE_COD") })
    private List<Image> images;

    public New() {
    }

    public Long getNewCod() {
        return this.newCod;
    }

    public void setNewCod(Long newsCod) {
        this.newCod = newsCod;
    }

    public String getBodynewsChr() {
        return this.bodynewsChr;
    }

    public void setBodynewsChr(String bodynewsChr) {
        this.bodynewsChr = bodynewsChr;
    }

    public String getSubtitleChr() {
        return this.subtitleChr;
    }

    public void setSubtitleChr(String subtitleChr) {
        this.subtitleChr = subtitleChr;
    }

    public String getTitleChr() {
        return this.titleChr;
    }

    public void setTitleChr(String titleChr) {
        this.titleChr = titleChr;
    }

    public List<Image> getImages() {
        return this.images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((newCod == null) ? 0 : newCod.hashCode());
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
        New other = (New) obj;
        if (newCod == null) {
            if (other.newCod != null)
                return false;
        } else if (!newCod.equals(other.newCod))
            return false;
        return true;
    }

}