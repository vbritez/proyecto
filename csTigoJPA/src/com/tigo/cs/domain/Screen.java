package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.tigo.cs.commons.jpa.PrimarySortedField;

@Entity
@Table(name = "screen")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(name = "Screen.findByNosecurityChr",
        query = "SELECT s FROM Screen s WHERE s.nosecurityChr = :nosecurityChr"), @NamedQuery(
        name = "Screen.findByPageChr",
        query = "SELECT s FROM Screen s WHERE s.pageChr = :pageChr") })
public class Screen implements Serializable {

    private static final long serialVersionUID = -6395797484222637248L;
    @Id
    @SequenceGenerator(name = "screenGen", sequenceName = "screen_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "screenGen")
    @Basic(optional = false)
    @Column(name = "screen_cod")
    private Long screenCod;
    @NotEmpty(message = "{entity.screen.constraint.descriptionChr.NotEmpty}")
    @Column(name = "description_chr")
    @PrimarySortedField
    private String descriptionChr;
    @NotEmpty(message = "{entity.screen.constraint.pageChr.NotEmpty}")
    @Column(name = "page_chr")
    private String pageChr;
    @NotNull(message = "{entity.screen.constraint.orderNum.NotNull}")
    @Column(name = "order_num")
    private Integer orderNum;
    @NotEmpty(message = "{entity.screen.constraint.noSecurity.NotEmpty}")
    @Column(name = "nosecurity_chr")
    private Boolean nosecurityChr;
    @NotEmpty(message = "{entity.screen.constraint.showOnMenu.NotEmpty}")
    @Column(name = "showonmenu_chr")
    private Boolean showonmenuChr;
    @OneToMany(
            mappedBy = "screen",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Collection<Auditory> auditoryCollection;
    @JoinColumn(name = "cod_module", referencedColumnName = "moduleadmin_cod")
    @ManyToOne(
            optional = false,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Moduleadmin moduleadmin;
    @OneToMany(
            mappedBy = "screen",
            cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH })
    private Collection<RoleadminScreen> roleadminScreenCollection;

    public Screen() {
    }

    public Screen(Long screenCod) {
        this.screenCod = screenCod;
    }

    public Screen(Long screenCod, String descriptionChr, String pageChr) {
        this.screenCod = screenCod;
        this.descriptionChr = descriptionChr;
        this.pageChr = pageChr;
    }

    public Long getScreenCod() {
        return screenCod;
    }

    public void setScreenCod(Long screenCod) {
        this.screenCod = screenCod;
    }

    public String getDescriptionChr() {
        return descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public String getPageChr() {
        return pageChr;
    }

    public void setPageChr(String pageChr) {
        this.pageChr = pageChr;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Boolean getNosecurityChr() {
        return nosecurityChr;
    }

    public void setNosecurityChr(Boolean nosecurityChr) {
        this.nosecurityChr = nosecurityChr;
    }

    public Boolean getShowonmenuChr() {
        return showonmenuChr;
    }

    public void setShowonmenuChr(Boolean showonmenuChr) {
        this.showonmenuChr = showonmenuChr;
    }

    public Collection<Auditory> getAuditoryCollection() {
        return auditoryCollection;
    }

    public void setAuditoryCollection(Collection<Auditory> auditoryCollection) {
        this.auditoryCollection = auditoryCollection;
    }

    public Moduleadmin getModuleadmin() {
        return moduleadmin;
    }

    public void setModuleadmin(Moduleadmin moduleadmin) {
        this.moduleadmin = moduleadmin;
    }

    public Collection<RoleadminScreen> getRoleadminScreenCollection() {
        return roleadminScreenCollection;
    }

    public void setRoleadminScreenCollection(Collection<RoleadminScreen> roleadminScreenCollection) {
        this.roleadminScreenCollection = roleadminScreenCollection;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Screen other = (Screen) obj;
        if (this.screenCod != other.screenCod
            && (this.screenCod == null || !this.screenCod.equals(other.screenCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash
            + (this.screenCod != null ? this.screenCod.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return descriptionChr.concat(" (Cod.: ").concat(screenCod.toString()).concat(
                ")");
    }
}
