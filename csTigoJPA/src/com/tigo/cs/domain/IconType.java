package com.tigo.cs.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.PrimarySortedField;

@Entity
@Table(name = "ICON_TYPE")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class IconType implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -6743608864377202692L;
    @Id
    @PrimarySortedField
    @Column(name = "ICON_TYPE_COD")
    private Long iconTypeCod;
    @Column
    private String url;
    @Column
    private String description;
    @Lob
    @Column(name = "FILE_BYT")
    private byte[] fileByt;

    @Column
    private Boolean enabled;

    @Column(name = "ICON_DEFAULT")
    private Boolean iconDefault;

    public Long getIconTypeCod() {
        return iconTypeCod;
    }

    public void setIconTypeCod(Long iconTypeCod) {
        this.iconTypeCod = iconTypeCod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setIconDefault(Boolean iconDefault) {
        this.iconDefault = iconDefault;
    }    

    public Boolean getIconDefault() {
        return iconDefault;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public byte[] getFileByt() {
        return fileByt;
    }

    public void setFileByt(byte[] fileByt) {
        this.fileByt = fileByt;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((iconTypeCod == null) ? 0 : iconTypeCod.hashCode());
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
        IconType other = (IconType) obj;
        if (iconTypeCod == null) {
            if (other.iconTypeCod != null)
                return false;
        } else if (!iconTypeCod.equals(other.iconTypeCod))
            return false;
        return true;
    }

}
