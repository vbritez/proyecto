package com.tigo.cs.api.facade.jms;

import java.io.Serializable;

import com.tigo.cs.domain.MetaData;

public class MetaDataMessage implements Serializable {

    private static final long serialVersionUID = -6484402455872918413L;
    private MetaData metaData;
    private Long userphoneCode;
    private String value;

    public MetaDataMessage() {

    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getUserphoneCode() {
        return userphoneCode;
    }

    public void setUserphoneCode(Long userphoneCode) {
        this.userphoneCode = userphoneCode;
    }

}
