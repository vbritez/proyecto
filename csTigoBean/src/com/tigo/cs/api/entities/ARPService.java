package com.tigo.cs.api.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.tigo.cs.api.annotations.MetaColumn;
import com.tigo.cs.api.enumerates.MetaNames;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ARPService extends BaseServiceBean {

    private static final long serialVersionUID = 7840216264475972037L;
    private String invoiceNumber;
    @MetaColumn(metaname = MetaNames.ARP_INVOICE_TYPE)
    private String invoiceTypeCode;
    private String invoiceAmmount;
    private List<ARPGuideService> guideList;
    private List<ARPEnrollmentService> enrollmentList;

    public ARPService() {
        super();
        setServiceCod(9);
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceTypeCode() {
        return invoiceTypeCode;
    }

    public void setInvoiceTypeCode(String invoiceTypeCode) {
        this.invoiceTypeCode = invoiceTypeCode;
    }

    public String getInvoiceAmmount() {
        return invoiceAmmount;
    }

    public void setInvoiceAmmount(String invoiceAmmount) {
        this.invoiceAmmount = invoiceAmmount;
    }

    public List<ARPGuideService> getGuideList() {
        return guideList;
    }

    public void setGuideList(List<ARPGuideService> guideList) {
        this.guideList = guideList;
    }

    public List<ARPEnrollmentService> getEnrollmentList() {
        return enrollmentList;
    }

    public void setEnrollmentList(List<ARPEnrollmentService> enrollmentList) {
        this.enrollmentList = enrollmentList;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
