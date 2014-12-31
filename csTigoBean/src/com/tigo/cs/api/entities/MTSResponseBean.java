package com.tigo.cs.api.entities;

public class MTSResponseBean {
	
	private Boolean autenticated;
	private Integer responseCode;
	private String message;
	
	public Boolean getAutenticated() {
		return autenticated;
	}
	public Integer getResponseCode() {
		return responseCode;
	}
	public String getMessage() {
		return message;
	}
	public void setAutenticated(Boolean autenticated) {
		this.autenticated = autenticated;
	}
	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
