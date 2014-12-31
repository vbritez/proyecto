package com.tigo.cs.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "USERPHONE_META")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class UserphoneMeta implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private UserphoneMetaPK id;

    @Column(name = "CREATE_CHR")
    private Boolean createChr;

    @Column(name = "DELETE_CHR")
    private Boolean deleteChr;

    @Column(name = "UPDATE_CHR")
    private Boolean updateChr;

    // bi-directional many-to-one association to Meta
    @ManyToOne
    @JoinColumn(name = "COD_META", insertable = false, updatable = false)
    private Meta meta;

    // bi-directional many-to-one association to Userphone
    @ManyToOne
    @JoinColumn(name = "COD_USERPHONE", insertable = false, updatable = false)
    private Userphone userphone;

    public UserphoneMeta() {
    }

    public UserphoneMetaPK getId() {
        return this.id;
    }

    public void setId(UserphoneMetaPK id) {
        this.id = id;
    }

    public Boolean getCreateChr() {
        return this.createChr;
    }

    public void setCreateChr(Boolean createChr) {
        this.createChr = createChr;
    }

    public Boolean getDeleteChr() {
        return this.deleteChr;
    }

    public void setDeleteChr(Boolean deleteChr) {
        this.deleteChr = deleteChr;
    }

    public Boolean getUpdateChr() {
        return this.updateChr;
    }

    public void setUpdateChr(Boolean updateChr) {
        this.updateChr = updateChr;
    }

    public Meta getMeta() {
        return this.meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Userphone getUserphone() {
        return this.userphone;
    }

    public void setUserphone(Userphone userphone) {
        this.userphone = userphone;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserphoneMeta other = (UserphoneMeta) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}