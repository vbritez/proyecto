package com.tigo.cs.api.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.Gson;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TigoMoneyService extends BaseServiceBean {

    private static final long serialVersionUID = 1073292207301903393L;

    private String cellphoneNumber;
    private String password;
    private String identification;
    private Long birthDate;
    private String address;
    private String city;
    private String loginResponse;
    private String idStatus;
    private String registrationResponse;
    private String message;
    private String name;
    private String registrationChannel;
    private String photoEntity;
    private String source;

    public TigoMoneyService() {
        super();
        setServiceCod(-3);
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

    public Long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Long birthDate) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoEntity() {
        return photoEntity;
    }

    public void setPhotoEntity(String photoEntity) {
        this.photoEntity = photoEntity;
    }

    public String getRegistrationChannel() {
        return registrationChannel;
    }

    public void setRegistrationChannel(String registrationChannel) {
        this.registrationChannel = registrationChannel;
    }

    public String getRegistrationResponse() {
        return registrationResponse;
    }

    public void setRegistrationResponse(String registrationResponse) {
        this.registrationResponse = registrationResponse;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        String hashTmp = hash;
        String appKeyTmp = applicationKey;
        Boolean sendSMSTmp = sendSMS;
        Boolean encryptTMP = encryptResponse;
        String photoEntityTMP = photoEntity;
        this.applicationKey = null;
        this.hash = null;
        this.sendSMS = null;
        this.encryptResponse = null;
        this.photoEntity = null;
        String result = new Gson().toJson(this);
        this.hash = hashTmp;
        this.applicationKey = appKeyTmp;
        this.sendSMS = sendSMSTmp;
        this.encryptResponse = encryptTMP;
        this.photoEntity = photoEntityTMP;
        return result;
    }

    @Override
    public String toStringWithHash() {
        String photoEntityTMP = photoEntity;
        String result = super.toStringWithHash();
        this.photoEntity = photoEntityTMP;
        return result;
    }
}
