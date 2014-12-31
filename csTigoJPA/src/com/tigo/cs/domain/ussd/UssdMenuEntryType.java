package com.tigo.cs.domain.ussd;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Javier Kovacs
 * @version $Id$
 */
@Entity
@Table(name = "ussd_menu_entry_type")
public class UssdMenuEntryType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
    @OneToMany(mappedBy = "ussdMenuEntryType")
    private List<UssdMenuEntry> ussdMenuEntryList;

    public UssdMenuEntryType() {
    }

    public UssdMenuEntryType(Long id) {
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

    public List<UssdMenuEntry> getUssdMenuEntryList() {
        return ussdMenuEntryList;
    }

    public void setUssdMenuEntryList(List<UssdMenuEntry> ussdMenuEntryList) {
        this.ussdMenuEntryList = ussdMenuEntryList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UssdMenuEntryType other = (UssdMenuEntryType) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if (this.ussdMenuEntryList != other.ussdMenuEntryList && (this.ussdMenuEntryList == null || !this.ussdMenuEntryList.equals(other.ussdMenuEntryList))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 79 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 79 * hash + (this.ussdMenuEntryList != null ? this.ussdMenuEntryList.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "UssdMenuEntryType[id=" + id + "]";
    }
}
