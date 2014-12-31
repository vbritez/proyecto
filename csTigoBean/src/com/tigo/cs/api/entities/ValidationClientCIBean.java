package com.tigo.cs.api.entities;

import java.io.Serializable;

public class ValidationClientCIBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2354463020436098368L;
    private Boolean validCI;
    private String responseCode;
    private String message;
    private String name;
    private String lastname;
    private String birthday;

    public Boolean getValidCI() {
        return validCI;
    }

    public void setValidCI(Boolean validCI) {
        this.validCI = validCI;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

}
