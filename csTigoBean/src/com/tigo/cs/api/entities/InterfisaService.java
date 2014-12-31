package com.tigo.cs.api.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class InterfisaService extends BaseServiceBean {

    private static final long serialVersionUID = 5738099932289935521L;
    private String document;

    public InterfisaService() {
        super();
        setServiceCod(-2);
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
