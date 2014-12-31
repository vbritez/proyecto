package com.tigo.cs.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries({ @NamedQuery(
        name = "AndroidVersion.findByVersionName",
        query = "SELECT a FROM AndroidVersion a WHERE a.versionName = :versionName") })
@Entity
@Table(name = "ANDROID_VERSION")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class AndroidVersion implements Serializable {

    private static final long serialVersionUID = -9002446660309085225L;

    @Id
    @Column(name = "ANDROID_VERSION_COD", unique = true, nullable = false,
            precision = 19)
    private long androidVersionCod;

    @Column(name = "VERSION_NAME")
    private String versionName;

    @Column(name = "ENABLED", precision = 1)
    private Boolean enabled;

    @Column(name = "DEPRECATED_MESSAGE", length = 160)
    private String deprecatedMessage;

    @Column(name = "DISABLED_MESSAGE", length = 160)
    private String disabledMessage;

    public AndroidVersion() {
    }

    public long getAndroidVersionCod() {
        return androidVersionCod;
    }

    public void setAndroidVersionCod(long androidVersionCod) {
        this.androidVersionCod = androidVersionCod;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getDeprecatedMessage() {
        return deprecatedMessage;
    }

    public void setDeprcatedMessage(String deprecatedMessage) {
        this.deprecatedMessage = deprecatedMessage;
    }

    public String getDisabledMessage() {
        return disabledMessage;
    }

    public void setDisabledMessage(String disabledMessage) {
        this.disabledMessage = disabledMessage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + (int) (androidVersionCod ^ (androidVersionCod >>> 32));
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
        AndroidVersion other = (AndroidVersion) obj;
        if (androidVersionCod != other.androidVersionCod)
            return false;
        return true;
    }

}