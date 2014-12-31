package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.tigo.cs.commons.jpa.PrimarySortedField;

@Entity
@Table(name = "classification")
@NamedQueries({ @NamedQuery(
        name = "Classification.findClientRoot",
        query = "SELECT c FROM Classification c WHERE c.codClient = :client AND c.root is NULL"), @NamedQuery(
        name = "Classification.findByClient",
        query = "SELECT c FROM Classification c WHERE c.codClient= :client order by c.root.classificationCod"), @NamedQuery(
        name = "Classification.findEagerByClientCod",
        query = "SELECT distinct c FROM Classification c LEFT JOIN FETCH c.classificationList WHERE c.codClient = :client") })
// @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Classification implements Serializable {

    private static final long serialVersionUID = 3385247633612136578L;
    @Id
    @SequenceGenerator(name = "classificationGen",
            sequenceName = "classification_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "classificationGen")
    @Basic(optional = false)
    @Column(name = "classification_cod")
    @PrimarySortedField
    private Long classificationCod;
    @Basic(optional = false)
    @Column(name = "description_chr")
    @NotEmpty(
            message = "{entity.classification.constraint.descriptionChr.NotEmpty}")
    private String descriptionChr;
    @JoinColumn(name = "COD_CLIENT", referencedColumnName = "CLIENT_COD")
    @ManyToOne(optional = false)
    private Client codClient;
    @Basic(optional = false)
    @Column(name = "depth_num")
    private Integer depthNum;
    @ManyToOne(optional = true)
    @JoinColumn(name = "root", referencedColumnName = "classification_cod")
    private Classification root;
    @OneToMany(mappedBy = "root")
    private List<Classification> classificationList;
    @ManyToMany(mappedBy = "classificationList")
    private List<Userweb> userwebList;
    @ManyToMany(mappedBy = "classificationList")
    private List<Userphone> userphoneList;
    // bi-directional many-to-many association to ReportConfig
    @ManyToMany(mappedBy = "classifications")
    private List<ReportConfig> reportConfigs;
    // bi-directional many-to-many association to FeatureElement
    @ManyToMany(mappedBy = "classifications")
    private List<FeatureElement> featureElements;

    public Classification() {
    }

    public Classification(Long classificationCod) {
        this.classificationCod = classificationCod;
    }

    public Classification(Long classificationCod, String descriptionChr) {
        this.classificationCod = classificationCod;
        this.descriptionChr = descriptionChr;
    }

    public Long getClassificationCod() {
        return classificationCod;
    }

    public void setClassificationCod(Long classificationCod) {
        this.classificationCod = classificationCod;
    }

    public String getDescriptionChr() {
        return descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public Client getCodClient() {
        return codClient;
    }

    public void setCodClient(Client codClient) {
        this.codClient = codClient;
    }

    public Integer getDepthNum() {
        return depthNum;
    }

    public void setDepthNum(Integer depthNum) {
        this.depthNum = depthNum;
    }

    public Classification getRoot() {
        return root;
    }

    public void setRoot(Classification root) {
        this.root = root;
    }

    public List<Userphone> getUserphoneList() {
        return userphoneList;
    }

    public void setUserphoneList(List<Userphone> userphoneList) {
        this.userphoneList = userphoneList;
    }

    public List<Userweb> getUserwebList() {
        return userwebList;
    }

    public void setUserwebList(List<Userweb> userwebList) {
        this.userwebList = userwebList;
    }

    public List<Classification> getClassificationList() {
        return classificationList;
    }

    public void setClassificationList(List<Classification> classificationList) {
        this.classificationList = classificationList;
    }

    public List<ReportConfig> getReportConfigs() {
        return this.reportConfigs;
    }

    public void setReportConfigs(List<ReportConfig> reportConfigs) {
        this.reportConfigs = reportConfigs;
    }

    public List<FeatureElement> getFeatureElements() {
        return this.featureElements;
    }

    public void setFeatureElements(List<FeatureElement> featureElements) {
        this.featureElements = featureElements;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Classification other = (Classification) obj;
        if (this.classificationCod != other.classificationCod
            && (this.classificationCod == null || !this.classificationCod.equals(other.classificationCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79
            * hash
            + (this.classificationCod != null ? this.classificationCod.hashCode() : 0);
        hash = 79
            * hash
            + (this.descriptionChr != null ? this.descriptionChr.hashCode() : 0);
        hash = 79 * hash
            + (this.depthNum != null ? this.depthNum.hashCode() : 0);
        hash = 79 * hash + (this.root != null ? this.root.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        if (this.getDepthNum() != null) {
            return getDescriptionChr().concat(" (Profundidad: ").concat(
                    getDepthNum().toString()).concat(")");
        } else {
            return getDescriptionChr().concat(" (Profundidad maxima) ");
        }
    }
}
