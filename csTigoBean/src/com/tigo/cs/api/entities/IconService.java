package com.tigo.cs.api.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class IconService extends BaseServiceBean {

    private static final long serialVersionUID = -8116604747027663639L;

    private String url;
    private String description;
    private Integer defaultIcon;
    private byte[] image;
    private String code;
    
    public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

    public Integer getDefaultIcon() {
        return defaultIcon;
    }

    public void setDefaultIcon(Integer defaultIcon) {
        this.defaultIcon = defaultIcon;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
