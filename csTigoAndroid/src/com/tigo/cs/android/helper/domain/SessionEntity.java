package com.tigo.cs.android.helper.domain;

import java.util.Date;


public class SessionEntity extends BaseEntity {

    private String cellphoneNumber;
    private String imsi;
    private Date expirationDate;

    public SessionEntity() {
    }

    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    public void setCellphoneNumber(String cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }    

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}    
}
