package com.tigo.cs.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the CLIENT_FEATURE database table.
 * 
 */
@Embeddable
public class ClientFeaturePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="COD_CLIENT")
	private long codClient;

	@Column(name="COD_FEATURE")
	private long codFeature;

    public ClientFeaturePK() {
    }
	public long getCodClient() {
		return this.codClient;
	}
	public void setCodClient(long codClient) {
		this.codClient = codClient;
	}
	public long getCodFeature() {
		return this.codFeature;
	}
	public void setCodFeature(long codFeature) {
		this.codFeature = codFeature;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ClientFeaturePK)) {
			return false;
		}
		ClientFeaturePK castOther = (ClientFeaturePK)other;
		return 
			(this.codClient == castOther.codClient)
			&& (this.codFeature == castOther.codFeature);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.codClient ^ (this.codClient >>> 32)));
		hash = hash * prime + ((int) (this.codFeature ^ (this.codFeature >>> 32)));
		
		return hash;
    }
}