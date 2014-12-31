package com.tigo.cs.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the USERPHONE_META database table.
 * 
 */
@Embeddable
public class UserphoneMetaPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final Long serialVersionUID = 1L;

	@Column(name="COD_USERPHONE")
	private Long codUserphone;

	@Column(name="COD_META")
	private Long codMeta;

    public UserphoneMetaPK() {
    }
	public Long getCodUserphone() {
		return this.codUserphone;
	}
	public void setCodUserphone(Long codUserphone) {
		this.codUserphone = codUserphone;
	}
	public Long getCodMeta() {
		return this.codMeta;
	}
	public void setCodMeta(Long codMeta) {
		this.codMeta = codMeta;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof UserphoneMetaPK)) {
			return false;
		}
		UserphoneMetaPK castOther = (UserphoneMetaPK)other;
		return 
			(this.codUserphone == castOther.codUserphone)
			&& (this.codMeta == castOther.codMeta);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.codUserphone ^ (this.codUserphone >>> 32)));
		hash = hash * prime + ((int) (this.codMeta ^ (this.codMeta >>> 32)));
		
		return hash;
    }
}