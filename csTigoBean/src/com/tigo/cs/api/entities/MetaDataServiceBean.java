package com.tigo.cs.api.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MetaDataServiceBean extends BaseServiceBean {

    private static final long serialVersionUID = -3453890408556584880L;
    private Long memberCod;
    private String code;
    private String value;

    public MetaDataServiceBean() {
        super();
        setServiceCod(99);
        setAndroid(null);
        setSendSMS(null);
        setEncryptResponse(null);
        setUssd(null);
        setServiceCod(null);
        setRequiresLocation(null);
    }

    public Long getMemberCod() {
        return memberCod;
    }

    public void setMemberCod(Long memberCod) {
        this.memberCod = memberCod;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
