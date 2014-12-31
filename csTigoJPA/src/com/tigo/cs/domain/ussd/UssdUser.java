package com.tigo.cs.domain.ussd;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.tigo.cs.domain.Userphone;

/**
 * 
 * @author Javier Kovacs
 * @version $Id$
 */
@Entity
@Table(name = "ussd_user")
public class UssdUser implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "CODE")
    private String code;
    @JoinColumn(name = "USSDAPPLICATION_ID", referencedColumnName = "ID")
    @ManyToOne
    private UssdApplication ussdApplication;
    @OneToMany(mappedBy = "ussdUser")
    private List<UssdMenuEntryUssdUser> ussdMenuEntryUssdUserList;
    @OneToOne
    @JoinColumn(name="ID", referencedColumnName="USERPHONE_COD")
    private Userphone userphone;

    public UssdUser() {
    }

    public UssdUser(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UssdApplication getUssdApplication() {
        return ussdApplication;
    }

    public void setUssdApplication(UssdApplication ussdApplication) {
        this.ussdApplication = ussdApplication;
    }

    public List<UssdMenuEntryUssdUser> getUssdMenuEntryUssdUserList() {
        return ussdMenuEntryUssdUserList;
    }

    public void setUssdMenuEntryUssdUserList(List<UssdMenuEntryUssdUser> ussdMenuEntryUssdUserList) {
        this.ussdMenuEntryUssdUserList = ussdMenuEntryUssdUserList;
    }

    public Userphone getUserphone() {
        return userphone;
    }

    public void setUserphone(Userphone userphone) {
        this.userphone = userphone;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UssdUser other = (UssdUser) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.code == null) ? (other.code != null) : !this.code.equals(other.code)) {
            return false;
        }
        if (this.ussdApplication != other.ussdApplication && (this.ussdApplication == null || !this.ussdApplication.equals(other.ussdApplication))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 97 * hash + (this.code != null ? this.code.hashCode() : 0);
        hash = 97 * hash + (this.ussdApplication != null ? this.ussdApplication.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "UssdUser[id=" + id + "]";
    }
}
