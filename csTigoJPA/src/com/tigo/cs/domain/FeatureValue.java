package com.tigo.cs.domain;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.util.CellPhoneNumberUtil;

@Entity
@Table(name = "FEATURE_VALUE")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class FeatureValue implements Serializable {

    private static final long serialVersionUID = -7296057240544751673L;

    @Id
    @SequenceGenerator(name = "FEATURE_VALUE_FEATUREVALUECOD_GENERATOR",
            sequenceName = "FEATURE_VALUE_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "FEATURE_VALUE_FEATUREVALUECOD_GENERATOR")
    @Column(name = "FEATURE_VALUE_COD")
    @PrimarySortedField
    private Long featureValueCod;

    // bi-directional many-to-one association to FeatureElement
    @ManyToOne
    @JoinColumn(name = "COD_FEATURE_ELEMENT")
    private FeatureElement featureElement;

    // bi-directional many-to-one association to Message
    @ManyToOne
    @JoinColumn(name = "COD_MESSAGE")
    private Message message;

    // bi-directional many-to-one association to Userphone
    @ManyToOne(optional = true)
    @JoinColumn(name = "COD_USERPHONE")
    private Userphone userphone;

    // bi-directional many-to-one association to FeatureValueData
    @OneToMany(mappedBy = "featureValue")
    private List<FeatureValueData> featureValueData;

    // bi-directional many-to-one association to Phone
    @ManyToOne(optional = true)
    @JoinColumn(name = "COD_PHONE")
    private Phone phone;

    @Transient
    private BigInteger cellphoneNum;

    @Transient
    private String nameChr;

    public FeatureValue() {
    }

    public Long getFeatureValueCod() {
        return this.featureValueCod;
    }

    public void setFeatureValueCod(Long featureValueCod) {
        this.featureValueCod = featureValueCod;
    }

    public FeatureElement getFeatureElement() {
        return this.featureElement;
    }

    public void setFeatureElement(FeatureElement featureElement) {
        this.featureElement = featureElement;
    }

    public Message getMessage() {
        return this.message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Userphone getUserphone() {
        return this.userphone;
    }

    public void setUserphone(Userphone userphone) {
        this.userphone = userphone;
    }

    public List<FeatureValueData> getFeatureValueData() {
        return this.featureValueData;
    }

    public void setFeatureValueData(List<FeatureValueData> featureValueData) {
        this.featureValueData = featureValueData;
    }

    public Phone getPhone() {
        return this.phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public BigInteger getCellphoneNum() {
        if (userphone != null)
            return userphone.getCellphoneNum();
        else if (phone != null)
            return phone.getCellphoneNum();
        else if (message != null) {
            return new BigInteger(CellPhoneNumberUtil.correctMsisdnToInternationalFormat(message.getOriginChr()));
        }

        return cellphoneNum;
    }

    public void setCellphoneNum(BigInteger cellphoneNum) {
        this.cellphoneNum = cellphoneNum;
    }

    public String getNameChr() {
        if (userphone != null)
            return userphone.getNameChr();
        if (phone != null)
            return phone.getNameChr();
        return nameChr;
    }

    public void setNameChr(String nameChr) {
        this.nameChr = nameChr;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((featureValueCod == null) ? 0 : featureValueCod.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FeatureValue other = (FeatureValue) obj;
        if (featureValueCod == null) {
            if (other.featureValueCod != null)
                return false;
        } else if (!featureValueCod.equals(other.featureValueCod))
            return false;
        return true;
    }

}