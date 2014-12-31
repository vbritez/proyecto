package com.tigo.cs.domain;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

@Entity
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Phone implements Serializable {

    private static final long serialVersionUID = -2709970712284982854L;

    @Id
    @SequenceGenerator(name = "PHONE_PHONECOD_GENERATOR",
            sequenceName = "USERPHONE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "PHONE_PHONECOD_GENERATOR")
    @Column(name = "PHONE_COD")
    private Long phoneCod;

    @Column(name = "CELLPHONE_NUM")
    private BigInteger cellphoneNum;

    @Column(name = "NAME_CHR")
    @Basic(optional = true)
    private String nameChr;

    @JoinColumn(name = "cod_client", referencedColumnName = "client_cod")
    @ManyToOne(optional = false)
    private Client client;

    // bi-directional many-to-many association to PhoneList
    @ManyToMany
    @JoinTable(name = "PHONE_PHONE_LIST", joinColumns = { @JoinColumn(
            name = "COD_PHONE") }, inverseJoinColumns = { @JoinColumn(
            name = "COD_PHONE_LIST") })
    private List<PhoneList> phoneLists;

    @OneToMany(
            mappedBy = "phone",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Collection<FeatureValue> featureValueCollection;

    public Phone() {
    }

    public Long getPhoneCod() {
        return this.phoneCod;
    }

    public void setPhoneCod(Long phoneCod) {
        this.phoneCod = phoneCod;
    }

    public BigInteger getCellphoneNum() {
        return this.cellphoneNum;
    }

    public void setCellphoneNum(BigInteger cellphoneNum) {
        this.cellphoneNum = cellphoneNum;
    }

    public String getNameChr() {
        return this.nameChr;
    }

    public void setNameChr(String nameChr) {
        this.nameChr = nameChr;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<PhoneList> getPhoneLists() {
        return this.phoneLists;
    }

    public void setPhoneLists(List<PhoneList> phoneLists) {
        this.phoneLists = phoneLists;
    }

    public Collection<FeatureValue> getFeatureValueCollection() {
        return featureValueCollection;
    }

    public void setFeatureValueCollection(Collection<FeatureValue> featureValueCollection) {
        this.featureValueCollection = featureValueCollection;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((phoneCod == null) ? 0 : phoneCod.hashCode());
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
        Phone other = (Phone) obj;
        if (phoneCod == null) {
            if (other.phoneCod != null)
                return false;
        } else if (!phoneCod.equals(other.phoneCod))
            return false;
        return true;
    }

}