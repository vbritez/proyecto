package com.tigo.cs.android.helper.domain;


public class UserphoneEntity extends BaseEntity {

    private String cellphoneNumber;
    private String name;
    private Long userphoneCod;
    private Boolean enabled;

    public UserphoneEntity() {
    }

    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    public void setCellphoneNumber(String cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Long getUserphoneCod() {
		return userphoneCod;
	}

	public void setUserphoneCod(Long userphoneCod) {
		this.userphoneCod = userphoneCod;
	}


    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

}
