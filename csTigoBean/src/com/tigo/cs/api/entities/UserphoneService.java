package com.tigo.cs.api.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserphoneService extends BaseServiceBean {

	private static final long serialVersionUID = -8116604747027663639L;

	private String name;
	private String cellphone;
	private Long userphoneCod;
	private Integer enabled;

	public UserphoneService() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	public Long getUserphoneCod() {
		return userphoneCod;
	}

	public void setUserphoneCod(Long userphoneCod) {
		this.userphoneCod = userphoneCod;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
