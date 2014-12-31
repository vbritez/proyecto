package com.tigo.cs.domain;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "seller")
@NamedQueries({ @NamedQuery(name = "Seller.findByEnabledChr",
        query = "SELECT s FROM Seller s WHERE s.enabledChr = :enabledChr") })
public class Seller implements Serializable {

    private static final long serialVersionUID = -1926675635353846185L;
    @Id
    @SequenceGenerator(name = "sellerGen", sequenceName = "seller_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sellerGen")
    @Basic(optional = false)
    @Column(name = "seller_cod")
    @Searchable(label = "entity.seller.searchable.sellerCod")
    private Long sellerCod;
    @Basic(optional = false)
    @NotEmpty(message = "{entity.seller.constraint.nameChr.NotEmpty}")
    @Column(name = "name_chr")
    @Searchable(label = "entity.seller.searchable.nameChr")
    @PrimarySortedField
    private String nameChr;
    @Basic(optional = false)
    @NotNull(message = "{entity.seller.constraint.cellphoneNum.NotNull}")
    @Column(name = "cellphone_num")
    @Searchable(label = "entity.seller.searchable.cellphoneNum")
    private BigInteger cellphoneNum;
    @Basic(optional = false)
    @Column(name = "enabled_chr")
    private Boolean enabledChr;
    @OneToMany(
            mappedBy = "seller",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Collection<Client> clientCollection;

    public Seller() {
    }

    public Seller(Long sellerCod) {
        this.sellerCod = sellerCod;
    }

    public Seller(Long sellerCod, String nameChr, BigInteger cellphoneNum,
            Boolean enabledChr) {
        this.sellerCod = sellerCod;
        this.nameChr = nameChr;
        this.cellphoneNum = cellphoneNum;
        this.enabledChr = enabledChr;
    }

    public Long getSellerCod() {
        return sellerCod;
    }

    public void setSellerCod(Long sellerCod) {
        this.sellerCod = sellerCod;
    }

    public String getNameChr() {
        return nameChr;
    }

    public void setNameChr(String nameChr) {
        this.nameChr = nameChr;
    }

    public BigInteger getCellphoneNum() {
        return cellphoneNum;
    }

    public void setCellphoneNum(BigInteger cellphoneNum) {
        this.cellphoneNum = cellphoneNum;
    }

    public Boolean getEnabledChr() {
        return enabledChr;
    }

    public void setEnabledChr(Boolean enabledChr) {
        this.enabledChr = enabledChr;
    }

    public Collection<Client> getClientCollection() {
        return clientCollection;
    }

    public void setClientCollection(Collection<Client> clientCollection) {
        this.clientCollection = clientCollection;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Seller other = (Seller) obj;
        if (this.sellerCod != other.sellerCod
            && (this.sellerCod == null || !this.sellerCod.equals(other.sellerCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash
            + (this.sellerCod != null ? this.sellerCod.hashCode() : 0);
        hash = 29 * hash + (this.nameChr != null ? this.nameChr.hashCode() : 0);
        hash = 29 * hash
            + (this.cellphoneNum != null ? this.cellphoneNum.hashCode() : 0);
        hash = 29 * hash
            + (this.enabledChr != null ? this.enabledChr.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return nameChr.concat(" (Cod.: ").concat(sellerCod.toString()).concat(
                ")");
    }
}
