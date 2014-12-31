package com.tigo.cs.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the ROUTE_TYPE_CONF database table.
 * 
 * @author Miguel Zorrilla
 * @version $Id$
 */
@Embeddable
public class RouteTypeConfPK implements Serializable {

	private static final long serialVersionUID = 2050609344497250513L;
	@Column(name = "COD_ROUTE_TYPE", unique = true, nullable = false, precision = 19)
	private Long codRouteType;
	@Column(name = "COD_CLIENT", unique = true, nullable = false, precision = 19)
	private Long codClient;
	@Column(name = "PARAM", unique = true, nullable = false, length = 50)
	private String parameter;

	public RouteTypeConfPK() {
	}

	public Long getCodRouteType() {
		return this.codRouteType;
	}

	public void setCodRouteType(Long codRouteType) {
		this.codRouteType = codRouteType;
	}

	public Long getCodClient() {
		return this.codClient;
	}

	public void setCodClient(Long codClient) {
		this.codClient = codClient;
	}

	public String getParameter() {
		return this.parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final RouteTypeConfPK other = (RouteTypeConfPK) obj;
		if (this.codRouteType != other.codRouteType
				&& (this.codRouteType == null || !this.codRouteType
						.equals(other.codRouteType))) {
			return false;
		}
		if (this.codClient != other.codClient
				&& (this.codClient == null || !this.codClient
						.equals(other.codClient))) {
			return false;
		}
		if ((this.parameter == null) ? (other.parameter != null)
				: !this.parameter.equals(other.parameter)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 67
				* hash
				+ (this.codRouteType != null ? this.codRouteType.hashCode() : 0);
		hash = 67 * hash
				+ (this.codClient != null ? this.codClient.hashCode() : 0);
		hash = 67 * hash
				+ (this.parameter != null ? this.parameter.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return "RouteTypeConfPK[codRouteType=" + codRouteType + ", codClient="
				+ codClient + ", parameter=" + parameter + "]";
	}
}
