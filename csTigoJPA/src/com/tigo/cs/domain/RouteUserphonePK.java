package com.tigo.cs.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the ROUTE_USERPHONE database table.
 * 
 * @author Miguel Zorrilla
 */
@Embeddable
public class RouteUserphonePK implements Serializable {

	private static final long serialVersionUID = 4511475668377605830L;
	@Column(name = "COD_ROUTE", unique = true, nullable = false, precision = 19)
	private Long codRoute;
	@Column(name = "COD_USERPHONE", unique = true, nullable = false, precision = 19)
	private Long codUserphone;

	public RouteUserphonePK() {
	}

	public RouteUserphonePK(Long codRoute, Long codUserphone) {
		this.codRoute = codRoute;
		this.codUserphone = codUserphone;
	}

	public Long getCodRoute() {
		return this.codRoute;
	}

	public void setCodRoute(Long codRoute) {
		this.codRoute = codRoute;
	}

	public Long getCodUserphone() {
		return this.codUserphone;
	}

	public void setCodUserphone(Long codUserphone) {
		this.codUserphone = codUserphone;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final RouteUserphonePK other = (RouteUserphonePK) obj;
		if (this.codRoute != other.codRoute
				&& (this.codRoute == null || !this.codRoute
						.equals(other.codRoute))) {
			return false;
		}
		if (this.codUserphone != other.codUserphone
				&& (this.codUserphone == null || !this.codUserphone
						.equals(other.codUserphone))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 79 * hash
				+ (this.codRoute != null ? this.codRoute.hashCode() : 0);
		hash = 79
				* hash
				+ (this.codUserphone != null ? this.codUserphone.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return "RouteUserphonePK[codRoute=" + codRoute + ", codUserphone="
				+ codUserphone + "]";
	}
}
