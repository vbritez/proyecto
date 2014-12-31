package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "MENU_MOVIL_USER")
public class MenuMovilUser implements Serializable {

    private static final long serialVersionUID = 4278950468504320086L;

    @Id
    @Column(name = "MENU_MOVIL_USER_COD")
    private Long menuMovilUserCod;

    @JoinColumn(name = "COD_APPLICATION",
            referencedColumnName = "APPLICATION_COD")
    @ManyToOne(optional = false)
    private Application application;

    @Basic(optional = false)
    @Column(name = "CREATEDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Basic(optional = false)
    @Column(name = "ENABLED")
    private Boolean enabledChr;

    @Basic(optional = false)
    @Column(name = "PASSWORD_CHR")
    private String passwordChr;

    @Basic(optional = false)
    @Column(name = "USERNAME_CHR")
    private String usernameChr;

    @Basic(optional = false)
    @Column(name = "EXPIRATIONDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;

    @OneToMany(mappedBy = "menuMovilUser")
    private List<MenuMovilPeriod> menuMovilUserPeriodList;

    public MenuMovilUser() {
    }

    public Long getMenuMovilUserCod() {
        return this.menuMovilUserCod;
    }

    public void setMenuMovilUserCod(Long menuMovilUserCod) {
        this.menuMovilUserCod = menuMovilUserCod;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getEnabledChr() {
        return this.enabledChr;
    }

    public void setEnabledChr(Boolean enabledChr) {
        this.enabledChr = enabledChr;
    }

    public String getPasswordChr() {
        return this.passwordChr;
    }

    public void setPasswordChr(String passwordChr) {
        this.passwordChr = passwordChr;
    }

    public String getUsernameChr() {
        return this.usernameChr;
    }

    public void setUsernameChr(String usernameChr) {
        this.usernameChr = usernameChr;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public List<MenuMovilPeriod> getMenuMovilUserPeriodList() {
        return menuMovilUserPeriodList;
    }

    public void setMenuMovilUserPeriodList(List<MenuMovilPeriod> menuMovilUserPeriodList) {
        this.menuMovilUserPeriodList = menuMovilUserPeriodList;
    }

    @Override
    public String toString() {
        return menuMovilUserCod.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MenuMovilUser other = (MenuMovilUser) obj;
        if (this.menuMovilUserCod != other.menuMovilUserCod
            && (this.menuMovilUserCod == null || !this.menuMovilUserCod.equals(other.menuMovilUserCod))) {
            return false;
        }
        return true;
    }

}