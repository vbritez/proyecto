package com.tigo.cs.domain;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;
import com.tigo.cs.domain.ussd.UssdUser;

@Entity
@Table(name = "userphone")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(name = "Userphone.findByEnabledChr",
        query = "SELECT u FROM Userphone u WHERE u.enabledChr = :enabledChr"), @NamedQuery(
        name = "Userphone.findByCellphoneNum",
        query = "SELECT u FROM Userphone u WHERE u.cellphoneNum = :cellphoneNum"), @NamedQuery(
        name = "Userphone.findByEnabledAndClient",
        query = "SELECT u FROM Userphone u WHERE u.enabledChr = :enabledChr AND u.client = :client") })
public class Userphone implements Serializable {

    private static final long serialVersionUID = 5416984760233747982L;
    @Id
    @SequenceGenerator(name = "userphoneGen", sequenceName = "userphone_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "userphoneGen")
    @Basic(optional = false)
    @Column(name = "userphone_cod")
    @Searchable(label = "entity.userphone.searchable.userphoneCod")
    private Long userphoneCod;
    @NotNull(message = "{entity.userphone.constraint.cellphoneNum.NotNull}")
    @Column(name = "cellphone_num")
    @Searchable(label = "entity.userphone.searchable.cellphoneNum")
    private BigInteger cellphoneNum;
    @NotEmpty(message = "{entity.userphone.constraint.nameChr.NotEmpty}")
    @Column(name = "name_chr")
    @PrimarySortedField
    @Searchable(label = "entity.userphone.searchable.nameChr")
    private String nameChr;
    @Column(name = "description_chr")
    @Searchable(label = "entity.userphone.searchable.descriptionChr")
    private String descriptionChr;
    @Basic(optional = false)
    @Column(name = "enabled_chr")
    private Boolean enabledChr;
    @Basic(optional = false)
    @Column(name = "createddate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "OFFROUTE_MARKOPTION_CHR")
    private Boolean offroadMarkoption;
    @JoinColumn(name = "cod_client", referencedColumnName = "client_cod")
    @ManyToOne(optional = false)
    @Searchable(label = "entity.userphone.searchable.client",
            internalField = "nameChr")
    private Client client;
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "userphone_classification", joinColumns = { @JoinColumn(
            name = "cod_userphone") }, inverseJoinColumns = { @JoinColumn(
            name = "cod_classification") })
    private List<Classification> classificationList;
    @OneToMany(
            mappedBy = "userphone",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Collection<Message> messageCollection;
    // bi-directional many-to-many association to ClientServiceFunctionality

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "userphone_client_ser_func",
            joinColumns = { @JoinColumn(name = "cod_userphone") },
            inverseJoinColumns = { @JoinColumn(name = "cod_client",
                    referencedColumnName = "cod_client"), @JoinColumn(
                    name = "cod_service", referencedColumnName = "cod_service"), @JoinColumn(
                    name = "cod_functionality",
                    referencedColumnName = "cod_functionality") })
    private List<ClientServiceFunctionality> clientServiceFunctionalityList;

    @Column(name = "current_imei")
//    @Transient
    private String currentImei;

     @Column(name = "current_imsi")
//    @Transient
    private String currentImsi;

    @OneToMany(
            mappedBy = "userphone",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Collection<ServiceValue> serviceValueCollection;
    @OneToMany(
            mappedBy = "userphone",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Collection<FeatureValue> featureValueCollection;
    // bi-directional many-to-one association to RouteDetailUserphone
    @OneToMany(mappedBy = "userphone")
    private List<RouteDetailUserphone> routeDetailUserphones;
    // bi-directional many-to-one association to RouteUserphone
    @OneToMany(mappedBy = "userphone")
    private List<RouteUserphone> routeUserphones;
    // bi-directional one-to-one association to UssdUser
    @OneToOne(mappedBy = "userphone")
    private UssdUser ussdUser;
    // @OneToOne(mappedBy = "userphone")
    // private Userweb userweb;

    @Basic(optional = false)
    @Column(name = "DISABLEDDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date disabledDate;

    // bi-directional many-to-many association to TrackingPeriod
    @ManyToMany(mappedBy = "userphones")
    private List<TrackingPeriod> trackingPeriods;

    // bi-directional many-to-many association to TrackingPeriod
    @ManyToMany(mappedBy = "userphones")
    private List<FeatureElement> featureElements;

    @Transient
    private String trackingPeriod;

    @Transient
    private String userphoneGoal;

    // bi-directional many-to-many association to MetaData
    @ManyToMany
    @JoinTable(
            name = "USERPHONE_META_DATA",
            joinColumns = { @JoinColumn(name = "COD_USERPHONE") },
            inverseJoinColumns = { @JoinColumn(name = "COD_CLIENT",
                    referencedColumnName = "COD_CLIENT"), @JoinColumn(
                    name = "COD_MEMBER", referencedColumnName = "COD_MEMBER"), @JoinColumn(
                    name = "COD_META", referencedColumnName = "COD_META"), @JoinColumn(
                    name = "CODIGO", referencedColumnName = "CODE_CHR") })
    private List<MetaData> metaData;

    @OneToMany(mappedBy = "userphone")
    private List<UserphoneMeta> userphoneMetaList;

    // bi-directional many-to-many association to ClientGoal
    @ManyToMany(mappedBy = "userphones")
    private List<ClientGoal> clientGoals;

    public Userphone() {
    }

    public Userphone(Long userphoneCod) {
        this.userphoneCod = userphoneCod;
    }

    public Userphone(Long userphoneCod, BigInteger cellphoneNum,
            String nameChr, Boolean enabledChr) {
        this.userphoneCod = userphoneCod;
        this.cellphoneNum = cellphoneNum;
        this.nameChr = nameChr;
        this.enabledChr = enabledChr;
    }

    public Long getUserphoneCod() {
        return userphoneCod;
    }

    public void setUserphoneCod(Long userphoneCod) {
        this.userphoneCod = userphoneCod;
    }

    public BigInteger getCellphoneNum() {
        return cellphoneNum;
    }

    public void setCellphoneNum(BigInteger cellphoneNum) {
        this.cellphoneNum = cellphoneNum;
    }

    public String getNameChr() {
        return nameChr;
    }

    public void setNameChr(String nameChr) {
        this.nameChr = nameChr;
    }

    public String getDescriptionChr() {
        return descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public Boolean getEnabledChr() {
        return enabledChr;
    }

    public void setEnabledChr(Boolean enabledChr) {
        this.enabledChr = enabledChr;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getOffroadMarkoption() {
        return offroadMarkoption;
    }

    public void setOffroadMarkoption(Boolean offroadMarkoption) {
        this.offroadMarkoption = offroadMarkoption;
    }

    public Collection<Message> getMessageCollection() {
        return messageCollection;
    }

    public void setMessageCollection(Collection<Message> messageCollection) {
        this.messageCollection = messageCollection;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Classification> getClassificationList() {
        return classificationList;
    }

    public void setClassificationList(List<Classification> classificationList) {
        this.classificationList = classificationList;
    }

    public List<ClientServiceFunctionality> getClientServiceFunctionalityList() {
        if (clientServiceFunctionalityList == null) {
            clientServiceFunctionalityList = new ArrayList<ClientServiceFunctionality>();
        }
        return clientServiceFunctionalityList;
    }

    public void setClientServiceFunctionalityList(List<ClientServiceFunctionality> clientServiceFunctionalityList) {
        this.clientServiceFunctionalityList = clientServiceFunctionalityList;
    }

    public Collection<ServiceValue> getServiceValueCollection() {
        return serviceValueCollection;
    }

    public void setServiceValueCollection(Collection<ServiceValue> serviceValueCollection) {
        this.serviceValueCollection = serviceValueCollection;
    }

    public List<RouteDetailUserphone> getRouteDetailUserphones() {
        return routeDetailUserphones;
    }

    public void setRouteDetailUserphones(List<RouteDetailUserphone> routeDetailUserphones) {
        this.routeDetailUserphones = routeDetailUserphones;
    }

    public List<RouteUserphone> getRouteUserphones() {
        return routeUserphones;
    }

    public void setRouteUserphones(List<RouteUserphone> routeUserphones) {
        this.routeUserphones = routeUserphones;
    }

    public UssdUser getUssdUser() {
        return ussdUser;
    }

    public void setUssdUser(UssdUser ussdUser) {
        this.ussdUser = ussdUser;
    }

    public Date getDisabledDate() {
        return disabledDate;
    }

    public void setDisabledDate(Date disabledDate) {
        this.disabledDate = disabledDate;
    }

    public List<MetaData> getMetaData() {
        return metaData;
    }

    public void setMetaData(List<MetaData> metaData) {
        this.metaData = metaData;
    }

    public List<TrackingPeriod> getTrackingPeriods() {
        return trackingPeriods;
    }

    public void setTrackingPeriods(List<TrackingPeriod> trackingPeriods) {
        this.trackingPeriods = trackingPeriods;
    }

    public String getTrackingPeriod() {
        if (trackingPeriods != null && trackingPeriods.size() > 0) {
            for (TrackingPeriod tp : trackingPeriods) {
                if (tp.getStatus()) {
                    return tp.getDescription();
                }
            }
        }
        return null;
    }

    public void setTrackingPeriod(String trackingPeriod) {
        this.trackingPeriod = trackingPeriod;
    }

    public List<ClientGoal> getClientGoals() {
        return clientGoals;
    }

    public void setClientGoals(List<ClientGoal> clientGoals) {
        this.clientGoals = clientGoals;
    }

    public String getUserphoneGoal() {
        if (clientGoals != null && clientGoals.size() > 0) {
            return clientGoals.get(0).getDescription();
        }
        return null;
    }

    public void setUserphoneGoal(String userphoneGoal) {
        this.userphoneGoal = userphoneGoal;
    }

    public Collection<FeatureValue> getFeatureValueCollection() {
        return featureValueCollection;
    }

    public void setFeatureValueCollection(Collection<FeatureValue> featureValueCollection) {
        this.featureValueCollection = featureValueCollection;
    }

    public List<UserphoneMeta> getUserphoneMetaList() {
        return userphoneMetaList;
    }

    public void setUserphoneMetaList(List<UserphoneMeta> userphoneMetaList) {
        this.userphoneMetaList = userphoneMetaList;
    }

    public String getCurrentImei() {
        return currentImei;
    }

    public void setCurrentImei(String currentImei) {
        this.currentImei = currentImei;
    }

    public String getCurrentImsi() {
        return currentImsi;
    }

    public void setCurrentImsi(String currentImsi) {
        this.currentImsi = currentImsi;
    }

    public List<FeatureElement> getFeatureElements() {
        return featureElements;
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
        final Userphone other = (Userphone) obj;
        if (this.userphoneCod != other.userphoneCod
            && (this.userphoneCod == null || !this.userphoneCod.equals(other.userphoneCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash
            + (this.userphoneCod != null ? this.userphoneCod.hashCode() : 0);
        hash = 59 * hash
            + (this.cellphoneNum != null ? this.cellphoneNum.hashCode() : 0);
        hash = 59 * hash + (this.nameChr != null ? this.nameChr.hashCode() : 0);
        hash = 59
            * hash
            + (this.descriptionChr != null ? this.descriptionChr.hashCode() : 0);
        hash = 59 * hash
            + (this.enabledChr != null ? this.enabledChr.hashCode() : 0);
        hash = 59 * hash
            + (this.createdDate != null ? this.createdDate.hashCode() : 0);
        hash = 59
            * hash
            + (this.offroadMarkoption != null ? this.offroadMarkoption.hashCode() : 0);
        hash = 59 * hash + (this.client != null ? this.client.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return nameChr.concat(" (Cod.: ").concat(userphoneCod.toString()).concat(
                ")");
    }

    @PrePersist
    protected void prepersistMethod() {
        if (createdDate == null) {
            setCreatedDate(new Date());
        }
        if (descriptionChr == null || descriptionChr.isEmpty()) {
            descriptionChr = "Ninguna";
        }
        if (enabledChr == null) {
            enabledChr = true;
        }
    }

}
