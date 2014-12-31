package com.tigo.cs.api.entities;

import java.text.MessageFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ARPGuideService extends ServiceBean {

    private static final long serialVersionUID = 6640654904903921820L;
    private String guide;
    private String dimension;
    private String ammount;

    public ARPGuideService() {
    }

    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getAmmount() {
        return ammount;
    }

    public void setAmmount(String ammount) {
        this.ammount = ammount;
    }

    @Override
    public String toString() {
        String pattern = "\"guide\":\"{0}\",\"dimension\":\"{1}\",\"ammount\":\"{2}\"";
        return MessageFormat.format(pattern, guide, dimension, ammount);
    }
}
