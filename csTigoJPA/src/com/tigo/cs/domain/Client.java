package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.NotEmpty;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;
import com.tigo.cs.domain.ussd.UssdMenuEntryCheck;

@Entity
@Table(name = "client")
@NamedQueries({ @NamedQuery(name = "Client.findByEnabledChr",
        query = "SELECT c FROM Client c WHERE c.enabledChr = :enabledChr"), @NamedQuery(
        name = "Client.findByClientCod",
        query = "SELECT c FROM Client c WHERE c.clientCod = :clientCod"), @NamedQuery(
        name = "Client.findBySystemcode",
        query = "SELECT c FROM Client c WHERE c.systemcodeChr = :systemcodeChr") })
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Client implements Serializable {

    private static final long serialVersionUID = 1624844925515021162L;
    @Id
    @SequenceGenerator(name = "clientGen", sequenceName = "client_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clientGen")
    @Basic(optional = false)
    @Column(name = "client_cod")
    @Searchable(label = "entity.client.searchable.clientCod")
    private Long clientCod;
    @Basic(optional = false)
    @Column(name = "systemcode_chr")
    @Searchable(label = "entity.client.searchable.systemcodeChr")
    @NotEmpty(message = "{entity.client.constraint.systemcodeChr.NotEmpty}")
    private String systemcodeChr;
    @Column(name = "name_chr")
    @PrimarySortedField
    @Searchable(label = "entity.client.searchable.nameChr")
    @Basic(optional = false)
    private String nameChr;
    @Column(name = "description_chr")
    @NotEmpty(message = "{entity.client.constraint.descriptionChr.NotEmpty}")
    private String descriptionChr;
    @Column(name = "enabled_chr")
    @Basic(optional = false)
    private Boolean enabledChr;
    @Basic(optional = false)
    @Column(name = "demo_chr")
    private Boolean demoChr;
    @Basic(optional = false)
    @Column(name = "createddate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "demoexpirationdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date demoExpirationDate;
    @Column(name = "disabled_bydemoexp_chr")
    @Basic(optional = false)
    private Boolean disabledByDemoExpChr;
    @Column(name = "demo_count")
    private Integer demoCount;
    @Column(name = "send_confirm")
    @Basic(optional = false)
    private Boolean sendConfirm;
    @Column(name = "REG_NONEXISTENT_META")
    @Basic(optional = false)
    private Boolean regNonexistentMeta;
    @Column(name = "USSD_PROFILE_ID")
    private Long ussdProfileId;
    @JoinColumn(name = "cod_seller", referencedColumnName = "seller_cod")
    @ManyToOne
    private Seller seller;
    @OneToMany(
            mappedBy = "client",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Collection<Userweb> userwebCollection;
    @OneToMany(
            mappedBy = "client",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Collection<Message> messageCollection;
    @OneToMany(
            mappedBy = "client",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Collection<Userphone> userphoneCollection;
    @OneToMany(
            mappedBy = "client",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Collection<Phone> phoneCollection;

    @OneToMany(
            mappedBy = "client",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Collection<ClientInformation> clientInformationCollection;
    @OneToMany(
            mappedBy = "client",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Collection<ClientFile> clientFileCollection;
    @OneToMany(
            mappedBy = "client",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private List<ClientServiceFunctionality> clientServiceFunctionalityList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client",
            fetch = FetchType.EAGER)
    private List<MetaClient> metaClientList;
    @OneToMany(
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH },
            mappedBy = "client")
    private List<ClientParameterValue> clientParameterValueList;
    @JoinTable(name = "CLIENT_METAMEMBER", joinColumns = { @JoinColumn(
            name = "COD_CLIENT", referencedColumnName = "CLIENT_COD") },
            inverseJoinColumns = { @JoinColumn(name = "COD_META",
                    referencedColumnName = "COD_META"), @JoinColumn(
                    name = "COD_MEMBER", referencedColumnName = "MEMBER_COD") })
    @ManyToMany
    private Set<MetaMember> metaMemberSet;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")
    private List<RoleClient> roleClientList;
    @OneToMany(mappedBy = "codClient")
    private List<Classification> classificationList;
    // bi-directional many-to-one association to RouteTypeConf
    @OneToMany(mappedBy = "client")
    private Set<RouteTypeConf> routeTypeConfs;
    @OneToMany(
            mappedBy = "client",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private List<Route> routeList;
    @OneToMany(mappedBy = "client")
    private List<UssdMenuEntryCheck> ussdMenuEntryCheckList;
    // bi-directional many-to-one association to ShiftPeriod
    @OneToMany(mappedBy = "client")
    private Set<ShiftPeriod> shiftPeriods;

    // bi-directional many-to-one association to ClientGoal
    @OneToMany(mappedBy = "client")
    private List<ClientGoal> clientGoals;

    // bi-directional many-to-one association to ClientFeature
    @OneToMany(mappedBy = "client")
    private List<ClientFeature> clientFeatures;

    @Column(name = "CUSTOMER_DOCUMENT_TYPE")
    private String customerDocumentType;
    @Column(name = "CUSTOMER_DOCUMENT_VALUE")
    private String customerDocumentValue;

    @Column(name = "ACCOUNT_TYPE")
    private String accountType;
    @Column(name = "ACCOUNT_NUMBER")
    private String accountNumber;
    @Column(name = "ACCOUNT_DESCRIPTION")
    private String accountDescription;

    @Column(name = "DISABLE_REASON")
    private String disableReason;

    public Client() {
    }

    public Client(Long clientCod) {
        this.clientCod = clientCod;
    }

    public Client(Long clientCod, String systemcodeChr, String nameChr,
            String descriptionChr, Boolean enabledChr, Boolean demoChr) {
        this.clientCod = clientCod;
        this.systemcodeChr = systemcodeChr;
        this.nameChr = nameChr;
        this.descriptionChr = descriptionChr;
        this.enabledChr = enabledChr;
        this.demoChr = demoChr;
    }

    public Long getClientCod() {
        return clientCod;
    }

    public void setClientCod(Long clientCod) {
        this.clientCod = clientCod;
    }

    public Long getUssdProfileId() {
        return ussdProfileId;
    }

    public void setUssdProfileId(Long ussdProfileId) {
        this.ussdProfileId = ussdProfileId;
    }

    public String getSystemcodeChr() {
        return systemcodeChr;
    }

    public void setSystemcodeChr(String systemcodeChr) {
        this.systemcodeChr = systemcodeChr;
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

    public Boolean getDemoChr() {
        return demoChr;
    }

    public void setDemoChr(Boolean demoChr) {
        this.demoChr = demoChr;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getDemoExpirationDate() {
        return demoExpirationDate;
    }

    public void setDemoExpirationDate(Date demoExpirationDate) {
        this.demoExpirationDate = demoExpirationDate;
    }

    public Boolean getDisabledByDemoExpChr() {
        return disabledByDemoExpChr;
    }

    public void setDisabledByDemoExpChr(Boolean disabledByDemoExpChr) {
        this.disabledByDemoExpChr = disabledByDemoExpChr;
    }

    public Integer getDemoCount() {
        return demoCount;
    }

    public void setDemoCount(Integer demoCount) {
        this.demoCount = demoCount;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public Collection<Userweb> getUserwebCollection() {
        return userwebCollection;
    }

    public void setUserwebCollection(Collection<Userweb> userwebCollection) {
        this.userwebCollection = userwebCollection;
    }

    public Collection<Message> getMessageCollection() {
        return messageCollection;
    }

    public void setMessageCollection(Collection<Message> messageCollection) {
        this.messageCollection = messageCollection;
    }

    public Collection<Userphone> getUserphoneCollection() {
        return userphoneCollection;
    }

    public void setUserphoneCollection(Collection<Userphone> userphoneCollection) {
        this.userphoneCollection = userphoneCollection;
    }

    public List<ClientServiceFunctionality> getClientServiceFunctionalityList() {
        return clientServiceFunctionalityList;
    }

    public void setClientServiceFunctionalityList(List<ClientServiceFunctionality> clientServiceFunctionalityList) {
        this.clientServiceFunctionalityList = clientServiceFunctionalityList;
    }

    public Collection<ClientInformation> getClientInformationCollection() {
        return clientInformationCollection;
    }

    public void setClientInformationCollection(Collection<ClientInformation> clientInformationCollection) {
        this.clientInformationCollection = clientInformationCollection;
    }

    public Collection<ClientFile> getClientFileCollection() {
        return clientFileCollection;
    }

    public void setClientFileCollection(Collection<ClientFile> clientFileCollection) {
        this.clientFileCollection = clientFileCollection;
    }

    public List<MetaClient> getMetaClientList() {
        return metaClientList;
    }

    public void setMetaClientList(List<MetaClient> metaClientList) {
        this.metaClientList = metaClientList;
    }

    public List<ClientParameterValue> getClientParameterValueList() {
        return clientParameterValueList;
    }

    public void setClientParameterValueList(List<ClientParameterValue> clientParameterValueList) {
        this.clientParameterValueList = clientParameterValueList;
    }

    public Set<MetaMember> getMetaMemberSet() {
        return metaMemberSet;
    }

    public void setMetaMemberSet(Set<MetaMember> metaMemberSet) {
        this.metaMemberSet = metaMemberSet;
    }

    public List<RoleClient> getRoleClientList() {
        return roleClientList;
    }

    public void setRoleClientList(List<RoleClient> roleClientList) {
        this.roleClientList = roleClientList;
    }

    public Set<RouteTypeConf> getRouteTypeConfs() {
        return routeTypeConfs;
    }

    public void setRouteTypeConfs(Set<RouteTypeConf> routeTypeConfs) {
        this.routeTypeConfs = routeTypeConfs;
    }

    public List<UssdMenuEntryCheck> getUssdMenuEntryCheckList() {
        return ussdMenuEntryCheckList;
    }

    public void setUssdMenuEntryCheckList(List<UssdMenuEntryCheck> ussdMenuEntryCheckList) {
        this.ussdMenuEntryCheckList = ussdMenuEntryCheckList;
    }

    public List<Route> getRouteList() {
        return routeList;
    }

    public void setRouteList(List<Route> routeList) {
        this.routeList = routeList;
    }

    public List<Classification> getClassificationList() {
        return classificationList;
    }

    public void setClassificationList(List<Classification> classificationList) {
        this.classificationList = classificationList;
    }

    public Set<ShiftPeriod> getShiftPeriods() {
        return shiftPeriods;
    }

    public void setShiftPeriods(Set<ShiftPeriod> shiftPeriods) {
        this.shiftPeriods = shiftPeriods;
    }

    public List<ClientGoal> getClientGoals() {
        return clientGoals;
    }

    public void setClientGoals(List<ClientGoal> clientGoals) {
        this.clientGoals = clientGoals;
    }

    @PrePersist
    protected void addCreatedDate() {
        setCreatedDate(new Date());
        setDisabledByDemoExpChr(false);
    }

    public Boolean getSendConfirm() {
        return sendConfirm;
    }

    public void setSendConfirm(Boolean sendConfirm) {
        this.sendConfirm = sendConfirm;
    }

    public Boolean getRegNonexistentMeta() {
        return regNonexistentMeta;
    }

    public void setRegNonexistentMeta(Boolean regNonexistentMeta) {
        this.regNonexistentMeta = regNonexistentMeta;
    }

    public List<ClientFeature> getClientFeatures() {
        return this.clientFeatures;
    }

    public void setClientFeatures(List<ClientFeature> clientFeatures) {
        this.clientFeatures = clientFeatures;
    }

    public Collection<Phone> getPhoneCollection() {
        return phoneCollection;
    }

    public void setPhoneCollection(Collection<Phone> phoneCollection) {
        this.phoneCollection = phoneCollection;
    }

    public String getCustomerDocumentType() {
        return customerDocumentType;
    }

    public void setCustomerDocumentType(String customerDocumentType) {
        this.customerDocumentType = customerDocumentType;
    }

    public String getCustomerDocumentValue() {
        return customerDocumentValue;
    }

    public void setCustomerDocumentValue(String customerDocumentValue) {
        this.customerDocumentValue = customerDocumentValue;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountDescription() {
        return accountDescription;
    }

    public void setAccountDescription(String accountDescription) {
        this.accountDescription = accountDescription;
    }

    public String getDisableReason() {
        return disableReason;
    }

    public void setDisableReason(String disableReason) {
        this.disableReason = disableReason;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Client other = (Client) obj;
        if (this.clientCod != other.clientCod
            && (this.clientCod == null || !this.clientCod.equals(other.clientCod))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash
            + (this.clientCod != null ? this.clientCod.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getNameChr().concat(" (Cod.: ").concat(getClientCod().toString()).concat(
                ")");
    }

}
