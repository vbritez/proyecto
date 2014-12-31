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
@Table(name = "ussd_menu_entry_state")
public class UssdMenuEntryState implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "MENUENTRYSTATE")
    private String menuentrystate;
    @OneToMany(mappedBy = "owner")
    private List<UssdMenuEntry> ussdMenuEntryList;

    public UssdMenuEntryState() {
    }

    public UssdMenuEntryState(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMenuentrystate() {
        return menuentrystate;
    }

    public void setMenuentrystate(String menuentrystate) {
        this.menuentrystate = menuentrystate;
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
        final UssdMenuEntryState other = (UssdMenuEntryState) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.menuentrystate == null) ? (other.menuentrystate != null) : !this.menuentrystate.equals(other.menuentrystate)) {
            return false;
        }
        if (this.ussdMenuEntryList != other.ussdMenuEntryList && (this.ussdMenuEntryList == null || !this.ussdMenuEntryList.equals(other.ussdMenuEntryList))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 29 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 29 * hash + (this.menuentrystate != null ? this.menuentrystate.hashCode() : 0);
        hash = 29 * hash + (this.ussdMenuEntryList != null ? this.ussdMenuEntryList.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "UssdMenuEntryState[id=" + id + "]";
    }
}
