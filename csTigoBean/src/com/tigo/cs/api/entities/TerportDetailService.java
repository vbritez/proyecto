package com.tigo.cs.api.entities;

import java.text.MessageFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TerportDetailService extends ServiceBean {

    private static final long serialVersionUID = 8629111249123819979L;

    private Integer id;

    private String quantity;
    private String ubication;

    public TerportDetailService() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUbication() {
        return ubication;
    }

    public void setUbication(String ubication) {
        this.ubication = ubication;
    }

    @Override
    public String toString() {
        String pattern = "\"quantity\":\"{0}\",\"ubication\":\"{1}\"";
        return MessageFormat.format(pattern, quantity, ubication);
    }

}
