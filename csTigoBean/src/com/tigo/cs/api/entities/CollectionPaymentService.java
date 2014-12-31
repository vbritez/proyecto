package com.tigo.cs.api.entities;

import java.text.MessageFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.tigo.cs.api.annotations.MetaColumn;
import com.tigo.cs.api.enumerates.MetaNames;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionPaymentService extends ServiceBean {

    private static final long serialVersionUID = -3334381046200951303L;

    private Integer id;

    private String collectionType;
    private String collectionTypeAmmount;
    @MetaColumn(metaname = MetaNames.BANK)
    private String bankCode;
    private String checkNumber;
    private String checkDate;
    private String checkExpirationDate;
    private String observation;

    public CollectionPaymentService() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }

    public String getCollectionTypeAmmount() {
        return collectionTypeAmmount;
    }

    public void setCollectionTypeAmmount(String collectionTypeAmmount) {
        this.collectionTypeAmmount = collectionTypeAmmount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public String getCheckExpirationDate() {
        return checkExpirationDate;
    }

    public void setCheckExpirationDate(String checkExpirationDate) {
        this.checkExpirationDate = checkExpirationDate;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    @Override
    public String toString() {
        String pattern = "\"collectionType\":\"{0}\",\"collectionTypeAmmount\":\"{1}\",\"bankCode\":\"{2}\",\"checkNumber\":\"{3}\",\"checkDate\":\"{4}\",\"checkExpirationDate\":\"{5}\",\"observation\":\"{6}\"";
        return MessageFormat.format(pattern, collectionType,
                collectionTypeAmmount, bankCode, checkNumber, checkDate,
                checkExpirationDate, observation);
    }
}
