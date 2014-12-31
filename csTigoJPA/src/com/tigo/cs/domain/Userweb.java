package com.tigo.cs.domain;

import java.io.Serializable;
import java.math.BigInteger;
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
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "userweb")
@NamedQueries({ @NamedQuery(name = "Userweb.findByEnabledChr",
        query = "SELECT u FROM Userweb u WHERE u.enabledChr = :enabledChr"), @NamedQuery(
        name = "Userweb.findByUsernameChr",
        query = "SELECT u FROM Userweb u WHERE lower(u.usernameChr) = :usernameChr"), @NamedQuery(
        name = "Userweb.findByclientCodAndEnabledChr",
        query = "SELECT u FROM Userweb u WHERE u.client.clientCod = :clientCod AND u.enabledChr = :enabledChr") })
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Userweb implements Serializable {

    private static final long serialVersionUID = -2783846890877532062L;
    @Id
    @SequenceGenerator(name = "userwebGen", sequenceName = "userweb_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "userwebGen")
    @Basic(optional = false)
    @Column(name = "userweb_cod")
    @Searchable(label = "entity.userweb.searchable.userwebCod")
    private Long userwebCod;
    @Searchable(label = "entity.userweb.searchable.usernameChr")
    @PrimarySortedField
    @Basic(optional = false)
    @Column(name = "username_chr")
    @NotEmpty(message = "{entity.userweb.constraint.usernameChr.NotEmpty}")
    private String usernameChr;
    @Column(name = "password_chr")
    @Basic(optional = false)
    private String passwordChr;
    @Searchable(label = "entity.userweb.searchable.nameChr")
    @Column(name = "name_chr")
    @NotEmpty(message = "{entity.userweb.constraint.nameChr.NotEmpty}")
    private String nameChr;
    @NotNull(message = "{entity.userweb.constraint.cellphoneNum.NotNull}")
    @Column(name = "cellphone_num")
    private BigInteger cellphoneNum;
    @NotEmpty(message = "{entity.userweb.constraint.descriptionChr.NotEmpty}")
    @Column(name = "description_chr")
    private String descriptionChr;
    @Column(name = "enabled_chr")
    @Basic(optional = false)
    private Boolean enabledChr;
    @Basic(optional = false)
    @Column(name = "changepassw_chr")
    private Boolean changepasswChr;
    @Basic(optional = false)
    @Column(name = "createddate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Basic
    @Column(name = "expirationdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;
    @Basic
    @Column(name = "pwd_retry_count")
    @Max(value = 99l, message = "{entity.userweb.constraint.pwdRetryCount.Max}")
    private BigInteger pwdRetryCount;
    @Basic
    @Column(name = "mail_chr")
    private String mailChr;
    @Basic
    @Column(name = "changepasshash_chr")
    private String changepasshashChr;
    @Basic(optional = false)
    @Column(name = "admin_num")
    private Boolean adminNum;
    @JoinColumn(name = "cod_client", referencedColumnName = "client_cod")
    @ManyToOne(optional = false)
    @Searchable(label = "entity.userweb.searchable.client",
            internalField = "nameChr")
    private Client client;
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "userweb_classification", joinColumns = { @JoinColumn(
            name = "cod_userweb") }, inverseJoinColumns = { @JoinColumn(
            name = "cod_classification") })
    private List<Classification> classificationList;
    @OneToMany(mappedBy = "userweb")
    private Collection<Auditory> auditoryCollection;
    @OneToMany(mappedBy = "userweb")
    private Collection<Message> messageCollection;
    @JoinTable(
            name = "userweb_client_ser_func",
            joinColumns = { @JoinColumn(name = "cod_userweb") },
            inverseJoinColumns = { @JoinColumn(name = "cod_client",
                    referencedColumnName = "cod_client"), @JoinColumn(
                    name = "cod_service", referencedColumnName = "cod_service"), @JoinColumn(
                    name = "cod_functionality",
                    referencedColumnName = "cod_functionality") })
    @ManyToMany
    private List<ClientServiceFunctionality> clientServiceFunctionalityList;
    @JoinTable(name = "USERWEB_ROLE_CLIENT", joinColumns = { @JoinColumn(
            name = "COD_USERWEB", referencedColumnName = "USERWEB_COD") },
            inverseJoinColumns = { @JoinColumn(name = "COD_ROLE_CLIENT",
                    referencedColumnName = "ROLE_CLIENT_COD") })
    @ManyToMany
    private List<RoleClient> roleClientList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userweb")
    private Collection<ClientFile> clientFileCollection;

    public Userweb() {
    }

    public Userweb(Long userwebCod) {
        this.userwebCod = userwebCod;
    }

    public Userweb(Long userwebCod, String usernameChr, String passwordChr,
            String nameChr, BigInteger cellphoneNum, String descriptionChr,
            Boolean enabledChr) {
        this.userwebCod = userwebCod;
        this.usernameChr = usernameChr;
        this.passwordChr = passwordChr;
        this.nameChr = nameChr;
        this.cellphoneNum = cellphoneNum;
        this.descriptionChr = descriptionChr;
        this.enabledChr = enabledChr;
    }

    public Long getUserwebCod() {
        return userwebCod;
    }

    public void setUserwebCod(Long userwebCod) {
        this.userwebCod = userwebCod;
    }

    public String getUsernameChr() {
        return usernameChr;
    }

    public void setUsernameChr(String usernameChr) {
        this.usernameChr = usernameChr;
    }

    public String getPasswordChr() {
        return passwordChr;
    }

    public void setPasswordChr(String passwordChr) {
        this.passwordChr = passwordChr;
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

    public Boolean getChangepasswChr() {
        return changepasswChr;
    }

    public void setChangepasswChr(Boolean chagepasswChr) {
        this.changepasswChr = chagepasswChr;
    }

    public BigInteger getPwdRetryCount() {
        return pwdRetryCount;
    }

    public void setPwdRetryCount(BigInteger pwdRetryCount) {
        this.pwdRetryCount = pwdRetryCount;
    }

    public Collection<Auditory> getAuditoryCollection() {
        return auditoryCollection;
    }

    public String getChangepasshashChr() {
        return changepasshashChr;
    }

    public void setChangepasshashChr(String changepasshashChr) {
        this.changepasshashChr = changepasshashChr;
    }

    public String getMailChr() {
        return mailChr;
    }

    public void setMailChr(String mailChr) {
        this.mailChr = mailChr;
    }

    public void setAuditoryCollection(Collection<Auditory> auditoryCollection) {
        this.auditoryCollection = auditoryCollection;
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

    public Collection<Message> getMessageCollection() {
        return messageCollection;
    }

    public void setMessageCollection(Collection<Message> messageCollection) {
        this.messageCollection = messageCollection;
    }

    public List<ClientServiceFunctionality> getClientServiceFunctionalityList() {
        return clientServiceFunctionalityList;
    }

    public void setClientServiceFunctionalityList(List<ClientServiceFunctionality> clientServiceFunctionalityList) {
        this.clientServiceFunctionalityList = clientServiceFunctionalityList;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public List<RoleClient> getRoleClientList() {
        return roleClientList;
    }

    public void setRoleClientList(List<RoleClient> roleClientList) {
        this.roleClientList = roleClientList;
    }

    public Boolean getAdminNum() {
        return adminNum;
    }

    public void setAdminNum(Boolean adminNum) {
        this.adminNum = adminNum;
    }

    public Collection<ClientFile> getClientFileCollection() {
        return clientFileCollection;
    }

    public void setClientFileCollection(Collection<ClientFile> clientFileCollection) {
        this.clientFileCollection = clientFileCollection;
    }

    @PrePersist
    protected void addCreatedDate() {
        setCreatedDate(new Date());
        setExpirationDate(new Date());
        // If adding a new user, then password field is an empty value in sha1
        setPasswordChr("da39a3ee5e6b4b0d3255bfef95601890afd80709");
        setChangepasswChr(true);
        setPwdRetryCount(new BigInteger("0"));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Userweb other = (Userweb) obj;
        if (this.userwebCod != other.userwebCod
            && (this.userwebCod == null || !this.userwebCod.equals(other.userwebCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash
            + (this.userwebCod != null ? this.userwebCod.hashCode() : 0);
        hash = 47 * hash
            + (this.usernameChr != null ? this.usernameChr.hashCode() : 0);
        hash = 47 * hash
            + (this.passwordChr != null ? this.passwordChr.hashCode() : 0);
        hash = 47 * hash + (this.nameChr != null ? this.nameChr.hashCode() : 0);
        hash = 47 * hash
            + (this.cellphoneNum != null ? this.cellphoneNum.hashCode() : 0);
        hash = 47
            * hash
            + (this.descriptionChr != null ? this.descriptionChr.hashCode() : 0);
        hash = 47 * hash
            + (this.enabledChr != null ? this.enabledChr.hashCode() : 0);
        hash = 47
            * hash
            + (this.changepasswChr != null ? this.changepasswChr.hashCode() : 0);
        hash = 47 * hash
            + (this.createdDate != null ? this.createdDate.hashCode() : 0);
        hash = 47 * hash + (this.mailChr != null ? this.mailChr.hashCode() : 0);
        hash = 47
            * hash
            + (this.changepasshashChr != null ? this.changepasshashChr.hashCode() : 0);
        hash = 47 * hash
            + (this.adminNum != null ? this.adminNum.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return nameChr.concat(" (Cod.: ").concat(userwebCod.toString()).concat(
                ")");
    }

}
