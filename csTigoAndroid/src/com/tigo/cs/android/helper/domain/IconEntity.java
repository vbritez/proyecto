package com.tigo.cs.android.helper.domain;

public class IconEntity extends BaseEntity {

    private String url;
    private String description;
    private Boolean defaultIcon;
    private byte[] image;
    private String code;

    public IconEntity() {
    }    

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

    public Boolean getDefaultIcon() {
        return defaultIcon;
    }

    public void setDefaultIcon(Boolean defaultIcon) {
        this.defaultIcon = defaultIcon;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        IconEntity other = (IconEntity) obj;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

}
