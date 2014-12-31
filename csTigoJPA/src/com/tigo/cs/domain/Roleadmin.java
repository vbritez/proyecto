package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "roleadmin")
public class Roleadmin implements Serializable {

    private static final long serialVersionUID = -3966940901557651200L;
    @Id
    @SequenceGenerator(name = "roleadminGen", sequenceName = "roleadmin_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "roleadminGen")
    @Basic(optional = false)
    @Column(name = "roleadmin_cod")
    @Searchable(label = "entity.roleadmin.searchable.roleadminCod")
    private Long roleadminCod;
    @NotEmpty(message = "{entity.roleadmin.constraint.descriptionChr.NotEmpty}")
    @Column(name = "description_chr")
    @Searchable(label = "entity.roleadmin.searchable.descriptionChr")
    @PrimarySortedField
    private String descriptionChr;
    @OneToMany(mappedBy = "roleadmin", cascade = CascadeType.ALL)
    private Collection<RoleadminScreen> roleadminScreenCollection;
    @ManyToMany(mappedBy = "roleadminList")
    private List<Useradmin> useradminList;

    public Roleadmin() {
    }

    public Roleadmin(Long roleadminCod) {
        this.roleadminCod = roleadminCod;
    }

    public Roleadmin(Long roleadminCod, String descriptionChr) {
        this.roleadminCod = roleadminCod;
        this.descriptionChr = descriptionChr;
    }

    public Long getRoleadminCod() {
        return roleadminCod;
    }

    public void setRoleadminCod(Long roleadminCod) {
        this.roleadminCod = roleadminCod;
    }

    public String getDescriptionChr() {
        return descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public Collection<RoleadminScreen> getRoleadminScreenCollection() {
        return roleadminScreenCollection;
    }

    public void setRoleadminScreenCollection(Collection<RoleadminScreen> roleadminScreenCollection) {
        this.roleadminScreenCollection = roleadminScreenCollection;
    }

    public List<Useradmin> getUseradminList() {
        return useradminList;
    }

    public void setUseradminList(List<Useradmin> useradminList) {
        this.useradminList = useradminList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Roleadmin other = (Roleadmin) obj;
        if (this.roleadminCod != other.roleadminCod
            && (this.roleadminCod == null || !this.roleadminCod.equals(other.roleadminCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash
            + (this.roleadminCod != null ? this.roleadminCod.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return descriptionChr.concat(" (Cod.: ").concat(roleadminCod.toString()).concat(
                ")");
    }
}
