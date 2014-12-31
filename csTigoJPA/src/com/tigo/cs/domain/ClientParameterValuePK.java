package com.tigo.cs.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author Javier Kovacs
 * @version $Id: ClientParameterValuePK.java 7 2011-11-18 11:12:15Z
 *          miguel.maciel $
 */
@Embeddable
public class ClientParameterValuePK implements Serializable {

	private static final long serialVersionUID = 977166092255336514L;
	@Basic(optional = false)
	@Column(name = "COD_CLIENT")
	private Long codClient;
	@Basic(optional = false)
	@Column(name = "COD_CLIENT_PARAMETER")
	private String codClientParameter;

	public ClientParameterValuePK() {
	}

	public ClientParameterValuePK(Long codClient, String codClientParameter) {
		this.codClient = codClient;
		this.codClientParameter = codClientParameter;
	}

	public Long getCodClient() {
		return codClient;
	}

	public void setCodClient(Long codClient) {
		this.codClient = codClient;
	}

	public String getCodClientParameter() {
		return codClientParameter;
	}

	public void setCodClientParameter(String codClientParameter) {
		this.codClientParameter = codClientParameter;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ClientParameterValuePK other = (ClientParameterValuePK) obj;
		if (this.codClient != other.codClient
				&& (this.codClient == null || !this.codClient
						.equals(other.codClient))) {
			return false;
		}
		if ((this.codClientParameter == null) ? (other.codClientParameter != null)
				: !this.codClientParameter.equals(other.codClientParameter)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 29 * hash
				+ (this.codClient != null ? this.codClient.hashCode() : 0);
		hash = 29
				* hash
				+ (this.codClientParameter != null ? this.codClientParameter
						.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return "ClientParameterValuePK[codClient=" + codClient
				+ ", codClientParameter=" + codClientParameter + "]";
	}
}
