package com.tigo.cs.android.helper.domain;

import java.util.Date;

public class MetaDataEntity extends BaseEntity {

    private MetaMemberEntity metaMember;
    private String code;
    private String dataValue;
    private Date dateTime;

    public MetaDataEntity() {
    }

    public MetaMemberEntity getMetaMember() {
        return metaMember;
    }

    public void setMetaMember(MetaMemberEntity metaMember) {
        this.metaMember = metaMember;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
