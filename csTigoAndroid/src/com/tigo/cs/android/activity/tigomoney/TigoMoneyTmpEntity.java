package com.tigo.cs.android.activity.tigomoney;

import java.util.Date;

import android.graphics.Bitmap;

public class TigoMoneyTmpEntity {

    private String cellphoneNumber;
    private String password;
    private String identification;
    private Date birthDate;
    private String address;
    private String city;
    private Bitmap frontPhoto;
    private Bitmap backPhoto;
    private String loginResponse;
    private String idStatus;
    private String message;
    private String frontPhotoFileName;
    private String backPhotoFileName;
    private boolean takeFrontPhotoBool;
    private boolean takeBackPhotoBool;
    private String name;
    
    private Integer lastId;

    public TigoMoneyTmpEntity() {
    }    

    public String getCellphoneNumber() {
		return cellphoneNumber;
	}

	public void setCellphoneNumber(String cellphoneNumber) {
		this.cellphoneNumber = cellphoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIdentification() {
		return identification;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Bitmap getFrontPhoto() {
		return frontPhoto;
	}

	public void setFrontPhoto(Bitmap frontPhoto) {
		this.frontPhoto = frontPhoto;
	}

	public Bitmap getBackPhoto() {
		return backPhoto;
	}

	public void setBackPhoto(Bitmap backPhoto) {
		this.backPhoto = backPhoto;
	}	

	public String getLoginResponse() {
		return loginResponse;
	}

	public void setLoginResponse(String loginResponse) {
		this.loginResponse = loginResponse;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}	

	public String getIdStatus() {
		return idStatus;
	}

	public void setIdStatus(String idStatus) {
		this.idStatus = idStatus;
	}

	public String getFrontPhotoFileName() {
		return frontPhotoFileName;
	}

	public String getBackPhotoFileName() {
		return backPhotoFileName;
	}

	public void setFrontPhotoFileName(String frontPhotoFileName) {
		this.frontPhotoFileName = frontPhotoFileName;
	}

	public void setBackPhotoFileName(String backPhotoFileName) {
		this.backPhotoFileName = backPhotoFileName;
	}

	public boolean isTakeFrontPhotoBool() {
		return takeFrontPhotoBool;
	}

	public boolean isTakeBackPhotoBool() {
		return takeBackPhotoBool;
	}

	public void setTakeFrontPhotoBool(boolean takeFrontPhotoBool) {
		this.takeFrontPhotoBool = takeFrontPhotoBool;
	}

	public void setTakeBackPhotoBool(boolean takeBackPhotoBool) {
		this.takeBackPhotoBool = takeBackPhotoBool;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getLastId() {
		return lastId;
	}

	public void setLastId(Integer lastId) {
		this.lastId = lastId;
	}	
	
	
}
