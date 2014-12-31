package com.tigo.cs.domain;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
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

@Entity
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "useradmin")
@NamedQueries({ @NamedQuery(
        name = "Useradmin.findByEnabledChr",
        query = "SELECT u FROM Useradmin u LEFT JOIN FETCH u.roleadminList WHERE u.enabledChr = :enabledChr"), @NamedQuery(
        name = "Useradmin.findByUsernameChr",
        query = "SELECT u FROM Useradmin u LEFT JOIN FETCH u.roleadminList WHERE lower(u.usernameChr) = :usernameChr") })
public class Useradmin implements Serializable {

    private static final long serialVersionUID = -1851637121141728971L;
    @Id
    @SequenceGenerator(name = "useradminGen", sequenceName = "useradmin_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "useradminGen")
    @Basic(optional = false)
    @Column(name = "useradmin_cod")
    @Searchable(label = "entity.useradmin.searchable.useradminCod")
    private Long useradminCod;
    @Basic(optional = false)
    @NotEmpty(message = "{entity.useradmin.constraint.usernameChr.NotEmpty}")
    @Column(name = "username_chr")
    @Searchable(label = "entity.useradmin.searchable.usernameChr")
    @PrimarySortedField
    private String usernameChr;
    @Basic(optional = false)
    @Column(name = "password_chr")
    private String passwordChr;
    @Basic(optional = false)
    @NotEmpty(message = "{entity.useradmin.constraint.nameChr.NotEmpty}")
    @Column(name = "name_chr")
    @Searchable(label = "entity.useradmin.searchable.nameChr")
    private String nameChr;
    @Basic
    @Column(name = "cellphone_num")
    private BigInteger cellphoneNum;
    @Basic
    @Column(name = "description_chr")
    private String descriptionChr;
    @Basic(optional = false)
    @Column(name = "enabled_chr")
    private Boolean enabledChr;
    @Basic
    @Column(name = "changepassw_chr")
    private Boolean changepasswChr;
    @Basic
    @Column(name = "createddate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Basic
    @Column(name = "expirationdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;
    @Basic
    @Column(name = "pwd_retry_count")
    private Integer pwdRetryCount;
    @Basic(optional = false)
    @Column(name = "LDAP_CHR")
    private Boolean ldapChr;
    @OneToMany(mappedBy = "useradmin")
    private Collection<Auditory> auditoryCollection;
    @OneToMany(mappedBy = "useradmin")
    private Collection<Message> messageCollection;
    @ManyToMany(cascade = { CascadeType.MERGE })
    @JoinTable(name = "useradmin_roleadmin", joinColumns = { @JoinColumn(
            name = "cod_useradmin", referencedColumnName = "useradmin_cod") },
            inverseJoinColumns = { @JoinColumn(name = "cod_roleadmin",
                    referencedColumnName = "roleadmin_cod") })
    private List<Roleadmin> roleadminList;
    @OneToMany(mappedBy = "useradmin")
    private Collection<ClientInformation> clientInformationCollection;
    @OneToMany(
            mappedBy = "useradmin",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private List<PasswordHistory> passwordHistoryList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "useradmin")
    private Collection<ClientFile> clientFileCollection;

    public Useradmin() {
    }

    public Useradmin(Long useradminCod) {
        this.useradminCod = useradminCod;
    }

    public Useradmin(Long useradminCod, String usernameChr, String passwordChr,
            String nameChr, Boolean enabledChr) {
        this.useradminCod = useradminCod;
        this.usernameChr = usernameChr;
        this.passwordChr = passwordChr;
        this.nameChr = nameChr;
        this.enabledChr = enabledChr;
    }

    public Long getUseradminCod() {
        return useradminCod;
    }

    public void setUseradminCod(Long useradminCod) {
        this.useradminCod = useradminCod;
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

    public void setChangepasswChr(Boolean changepasswChr) {
        this.changepasswChr = changepasswChr;
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

    public Integer getPwdRetryCount() {
        return pwdRetryCount;
    }

    public void setPwdRetryCount(Integer pwdRetryCount) {
        this.pwdRetryCount = pwdRetryCount;
    }

    public Boolean getLdapChr() {
        return ldapChr;
    }

    public void setLdapChr(Boolean ldapChr) {
        this.ldapChr = ldapChr;
    }

    public Collection<Auditory> getAuditoryCollection() {
        return auditoryCollection;
    }

    public void setAuditoryCollection(Collection<Auditory> auditoryCollection) {
        this.auditoryCollection = auditoryCollection;
    }

    public Collection<Message> getMessageCollection() {
        return messageCollection;
    }

    public void setMessageCollection(Collection<Message> messageCollection) {
        this.messageCollection = messageCollection;
    }

    public List<Roleadmin> getRoleadminList() {
        return roleadminList;
    }

    public void setRoleadminList(List<Roleadmin> roleadminList) {
        this.roleadminList = roleadminList;
    }

    public Collection<ClientInformation> getClientInformationCollection() {
        return clientInformationCollection;
    }

    public void setClientInformationCollection(List<ClientInformation> clientInformationCollection) {
        this.clientInformationCollection = clientInformationCollection;
    }

    public List<PasswordHistory> getPasswordHistoryList() {
        return passwordHistoryList;
    }

    public void setPasswordHistoryList(List<PasswordHistory> passwordHistoryList) {
        this.passwordHistoryList = passwordHistoryList;
    }

    public Collection<ClientFile> getClientFileCollection() {
        return clientFileCollection;
    }

    public void setClientFileCollection(Collection<ClientFile> clientFileCollection) {
        this.clientFileCollection = clientFileCollection;
    }

    @PrePersist
    protected void prePersist() {
        setPasswordChr("da39a3ee5e6b4b0d3255bfef95601890afd80709");
        setChangepasswChr(true);
        GregorianCalendar gc = new GregorianCalendar();
        Date date = gc.getTime();
        setCreatedDate(date);
        gc.add(Calendar.DAY_OF_MONTH, 30);
        setExpirationDate(gc.getTime());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Useradmin other = (Useradmin) obj;
        if (this.useradminCod != other.useradminCod
            && (this.useradminCod == null || !this.useradminCod.equals(other.useradminCod))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash
            + (this.useradminCod != null ? this.useradminCod.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return nameChr.concat(" (Cod.: ").concat(useradminCod.toString()).concat(
                ")");
    }
}