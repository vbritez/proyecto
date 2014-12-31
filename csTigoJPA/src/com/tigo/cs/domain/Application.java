package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@NamedQueries({ @NamedQuery(name = "Application.findByApplicationKey",
        query = "SELECT a FROM Application a WHERE a.key = :key") })
@Entity
@Table(name = "APPLICATION")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Application implements Serializable {

    private static final long serialVersionUID = 894673294511071246L;

    @Id
    @SequenceGenerator(name = "APPLICATION_APPLICATIONCOD_GENERATOR",
            sequenceName = "APPLICATION_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "APPLICATION_APPLICATIONCOD_GENERATOR")
    @Column(name = "APPLICATION_COD", unique = true, nullable = false,
            precision = 19)
    private long applicationCod;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(precision = 1)
    private Boolean enabled;

    @Column(name = "\"KEY\"", length = 255)
    private String key;

    @OneToMany(mappedBy = "application")
    private List<MenuMovilUser> menuMovilUserList;

    public Application() {
    }

    public long getApplicationCod() {
        return this.applicationCod;
    }

    public void setApplicationCod(long applicationCod) {
        this.applicationCod = applicationCod;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<MenuMovilUser> getMenuMovilUserList() {
        return menuMovilUserList;
    }

    public void setMenuMovilUserList(List<MenuMovilUser> menuMovilUserList) {
        this.menuMovilUserList = menuMovilUserList;
    }

}