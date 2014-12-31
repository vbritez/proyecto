package com.tigo.cs.api.facade.jms;

import java.io.Serializable;
import java.util.List;

import com.tigo.cs.domain.Application;

public class SMSJmsBean implements Serializable {

    private static final long serialVersionUID = -8959030871364430697L;

    private String cellphoneNum;
    private List<String> cellphoneNumList;
    private Application application;
    private String message;

    public List<String> getCellphoneNumList() {
        return cellphoneNumList;
    }

    public void setCellphoneNumList(List<String> cellphoneNumList) {
        this.cellphoneNumList = cellphoneNumList;
    }

    public String getCellphoneNum() {
        return cellphoneNum;
    }

    public void setCellphoneNum(String cellphoneNum) {
        this.cellphoneNum = cellphoneNum;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
