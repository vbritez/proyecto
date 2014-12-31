package com.tigo.cs.api.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.tigo.cs.api.annotations.MetaColumn;
import com.tigo.cs.api.enumerates.MetaNames;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionService extends BaseServiceBean {

    private static final long serialVersionUID = 2796061384977309209L;
    @MetaColumn(metaname = MetaNames.CLIENT)
    private String clientCode;
    private String receiptNumber;
    private List<CollectionInvoiceService> invoices;
    private List<CollectionPaymentService> payments;

    public CollectionService() {
        super();
        setServiceCod(5);
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public List<CollectionInvoiceService> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<CollectionInvoiceService> invoices) {
        this.invoices = invoices;
    }

    public List<CollectionPaymentService> getPayments() {
        return payments;
    }

    public void setPayments(List<CollectionPaymentService> payments) {
        this.payments = payments;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
