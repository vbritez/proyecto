package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "PHONE_LIST")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(
        name = "PhoneList.findByClient",
        query = "SELECT p FROM PhoneList p WHERE p.client.clientCod = :clientCod"), @NamedQuery(
        name = "PhoneList.findByClientType",
        query = "SELECT p FROM PhoneList p WHERE p.client.clientCod = :clientCod AND p.typeChr = :typeChr") })
public class PhoneList implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7080677308184238911L;

    @Id
    @SequenceGenerator(name = "PHONE_LIST_PHONELISTCOD_GENERATOR",
            sequenceName = "PHONE_LIST_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "PHONE_LIST_PHONELISTCOD_GENERATOR")
    @Column(name = "PHONE_LIST_COD")
    @PrimarySortedField
    private Long phoneListCod;

    @Column(name = "DESCRIPTION_CHR")
    @Searchable(label = "web.client.screen.phoneList.Description")
    private String descriptionChr;

    @Column(name = "TYPE_CHR")
    private String typeChr;

    // bi-directional many-to-many association to FeatureElement
    @ManyToMany(mappedBy = "phoneLists")
    private List<FeatureElement> featureElements;

    // bi-directional many-to-one association to Client
    @ManyToOne
    @JoinColumn(name = "COD_CLIENT")
    private Client client;

    // bi-directional many-to-many association to Phone
    @ManyToMany(mappedBy = "phoneLists")
    private List<Phone> phones;

    @Transient
    private String type;

    public PhoneList() {
    }

    public Long getPhoneListCod() {
        return this.phoneListCod;
    }

    public void setPhoneListCod(Long phoneListCod) {
        this.phoneListCod = phoneListCod;
    }

    public String getDescriptionChr() {
        return this.descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public String getTypeChr() {
        return this.typeChr;
    }

    public void setTypeChr(String typeChr) {
        this.typeChr = typeChr;
    }

    public List<FeatureElement> getFeatureElements() {
        return this.featureElements;
    }

    public void setFeatureElements(List<FeatureElement> featureElements) {
        this.featureElements = featureElements;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Phone> getPhones() {
        return this.phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public String getType() {
        if (typeChr != null) {
            if (typeChr.equals("W")) {
                type = "White List";
            } else {
                type = "Black List";
            }
        }
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((phoneListCod == null) ? 0 : phoneListCod.hashCode());
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
        PhoneList other = (PhoneList) obj;
        if (phoneListCod == null) {
            if (other.phoneListCod != null)
                return false;
        } else if (!phoneListCod.equals(other.phoneListCod))
            return false;
        return true;
    }

}