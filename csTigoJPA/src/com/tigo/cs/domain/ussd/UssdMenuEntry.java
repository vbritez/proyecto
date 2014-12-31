package com.tigo.cs.domain.ussd;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.tigo.cs.domain.Meta;

/**
 * 
 * @author Javier Kovacs
 * @version $Id$
 */
@Entity
@Table(name = "ussd_menu_entry")
public class UssdMenuEntry implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "CODE")
    private String code;
    @Column(name = "DESCRIPTION")
    private String description;
    @Basic(optional = false)
    @Column(name = "FINALMENU")
    private short finalmenu;
    @Column(name = "MENUORDER")
    private BigInteger menuorder;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "ROOTMENU")
    private Short rootmenu;
    
    @JoinColumn(name = "VALIDATOR_ID", referencedColumnName = "ID")
    @ManyToOne
    private UssdValidation ussdValidation;
    
    @JoinColumn(name = "MENUENTRYTYPE_ID", referencedColumnName = "ID")
    @ManyToOne
    private UssdMenuEntryType ussdMenuEntryType;
    
    @JoinColumn(name = "MENUENTRYSTATE_ID", referencedColumnName = "ID")
    @ManyToOne
    private UssdMenuEntryState ussdMenuEntryState;
    @OneToMany(mappedBy = "owner", cascade=CascadeType.REMOVE)
    private List<UssdMenuEntry> childList;
    
    @JoinColumn(name = "OWNER_ID", referencedColumnName = "ID")
    @ManyToOne
    private UssdMenuEntry owner;
    
    @OneToMany(mappedBy = "ussdMenuEntry1")
    private List<UssdMenuEntry> ussdMenuEntryList1;
    
    @JoinColumn(name = "DESTINYNUMBERENTRY_ID", referencedColumnName = "ID")
    @ManyToOne
    private UssdMenuEntry ussdMenuEntry1;
    
    @JoinColumn(name = "DYNAMICENTRYDRIVER_ID", referencedColumnName = "ID")
    @ManyToOne
    private UssdDriver ussdDriver;
    
    @JoinColumn(name = "DRIVER_ID", referencedColumnName = "ID")
    @ManyToOne
    private UssdDriver ussdDriver1;
    
    @JoinColumn(name = "USSDAPPLICACION_ID", referencedColumnName = "ID")
    @ManyToOne
    private UssdApplicationServer ussdApplicationServer;
    
    @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "ID")
    @ManyToOne
    private UssdApplication ussdApplication;
    
    @OneToMany(mappedBy = "ussdMenuEntry")
    private List<UssdServiceCorresp> ussdServiceCorrespList;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ussdMenuEntry")
    private List<UssdMenuEntryCheck> ussdMenuEntryCheckList;
    
    @JoinColumn(name = "COD_META", referencedColumnName = "META_COD")
    @ManyToOne
    private Meta meta;

    public UssdMenuEntry() {
    }

    public UssdMenuEntry(Long id) {
        this.id = id;
    }

    public UssdMenuEntry(Long id, short finalmenu) {
        this.id = id;
        this.finalmenu = finalmenu;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public short getFinalmenu() {
        return finalmenu;
    }

    public void setFinalmenu(short finalmenu) {
        this.finalmenu = finalmenu;
    }

    public BigInteger getMenuorder() {
        return menuorder;
    }

    public void setMenuorder(BigInteger menuorder) {
        this.menuorder = menuorder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Short getRootmenu() {
        return rootmenu;
    }

    public void setRootmenu(Short rootmenu) {
        this.rootmenu = rootmenu;
    }

    public UssdValidation getUssdValidation() {
        return ussdValidation;
    }

    public void setUssdValidation(UssdValidation ussdValidation) {
        this.ussdValidation = ussdValidation;
    }

    public UssdMenuEntryType getUssdMenuEntryType() {
        return ussdMenuEntryType;
    }

    public void setUssdMenuEntryType(UssdMenuEntryType ussdMenuEntryType) {
        this.ussdMenuEntryType = ussdMenuEntryType;
    }

    public UssdMenuEntryState getUssdMenuEntryState() {
        return ussdMenuEntryState;
    }

    public void setUssdMenuEntryState(UssdMenuEntryState ussdMenuEntryState) {
        this.ussdMenuEntryState = ussdMenuEntryState;
    }

    public List<UssdMenuEntry> getChildList() {
        return childList;
    }

    public void setChildList(List<UssdMenuEntry> childList) {
        this.childList = childList;
    }

    public UssdMenuEntry getOwner() {
        return owner;
    }

    public void setOwner(UssdMenuEntry owner) {
        this.owner = owner;
    }

    public List<UssdMenuEntry> getUssdMenuEntryList1() {
        return ussdMenuEntryList1;
    }

    public void setUssdMenuEntryList1(List<UssdMenuEntry> ussdMenuEntryList1) {
        this.ussdMenuEntryList1 = ussdMenuEntryList1;
    }

    public UssdMenuEntry getUssdMenuEntry1() {
        return ussdMenuEntry1;
    }

    public void setUssdMenuEntry1(UssdMenuEntry ussdMenuEntry1) {
        this.ussdMenuEntry1 = ussdMenuEntry1;
    }

    public UssdDriver getUssdDriver() {
        return ussdDriver;
    }

    public void setUssdDriver(UssdDriver ussdDriver) {
        this.ussdDriver = ussdDriver;
    }

    public UssdDriver getUssdDriver1() {
        return ussdDriver1;
    }

    public void setUssdDriver1(UssdDriver ussdDriver1) {
        this.ussdDriver1 = ussdDriver1;
    }

    public UssdApplicationServer getUssdApplicationServer() {
        return ussdApplicationServer;
    }

    public void setUssdApplicationServer(UssdApplicationServer ussdApplicationServer) {
        this.ussdApplicationServer = ussdApplicationServer;
    }

    public UssdApplication getUssdApplication() {
        return ussdApplication;
    }

    public void setUssdApplication(UssdApplication ussdApplication) {
        this.ussdApplication = ussdApplication;
    }

    public List<UssdServiceCorresp> getUssdServiceCorrespList() {
        return ussdServiceCorrespList;
    }

    public void setUssdServiceCorrespList(List<UssdServiceCorresp> ussdServiceCorrespList) {
        this.ussdServiceCorrespList = ussdServiceCorrespList;
    }

    public List<UssdMenuEntryCheck> getUssdMenuEntryCheckList() {
        return ussdMenuEntryCheckList;
    }

    public void setUssdMenuEntryCheckList(List<UssdMenuEntryCheck> ussdMenuEntryCheckList) {
        this.ussdMenuEntryCheckList = ussdMenuEntryCheckList;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UssdMenuEntry other = (UssdMenuEntry) obj;
        if (this.id != other.id
            && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "UssdMenuEntry[id=" + id + "]";
    }
}
