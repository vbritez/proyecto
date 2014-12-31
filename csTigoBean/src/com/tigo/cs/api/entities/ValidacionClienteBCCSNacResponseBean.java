package com.tigo.cs.api.entities;

public class ValidacionClienteBCCSNacResponseBean {
	
	private Boolean validCI;
	private String responseCode;
	private String message;
	private String name;
	private String lastname;
	
	public String getResponseCode() {
		return responseCode;
	}
	public String getMessage() {
		return message;
	}	
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Boolean getValidCI() {
		return validCI;
	}
	public void setValidCI(Boolean validCI) {
		this.validCI = validCI;
	}
	public String getName() {
		return name;
	}
	public String getLastname() {
		return lastname;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}	

	
}
