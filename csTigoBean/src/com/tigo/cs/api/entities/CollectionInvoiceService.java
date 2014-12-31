package com.tigo.cs.api.entities;

import java.text.MessageFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionInvoiceService extends ServiceBean {

    private static final long serialVersionUID = 2098861484910746754L;
    private Integer id;
    private String invoiceNumber;
    private String invoiceAmmount;

    public CollectionInvoiceService() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceAmmount() {
        return invoiceAmmount;
    }

    public void setInvoiceAmmount(String invoiceAmmount) {
        this.invoiceAmmount = invoiceAmmount;
    }

    @Override
    public String toString() {
        String pattern = "\"invoiceNumber\":\"{0}\",\"invoiceAmmount\":\"{1}\"";
        return MessageFormat.format(pattern, invoiceNumber, invoiceAmmount);
    }
}
