package com.tigo.cs.api.entities;

import java.text.MessageFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DeliveryDetailService extends ServiceBean {

    private static final long serialVersionUID = -2424252593987635281L;
    private String remissionNumber;

    public DeliveryDetailService() {

    }

    public String getRemissionNumber() {
        return remissionNumber;
    }

    public void setRemissionNumber(String remissionNumber) {
        this.remissionNumber = remissionNumber;
    }

    @Override
    public String toString() {
        String pattern = "\"remissionNumber\":\"{0}";
        return MessageFormat.format(pattern, remissionNumber);
    }
}
