package com.tigo.cs.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Miguel Zorrilla
 * @version $Id$
 */
@Embeddable
public class MetaMemberPK implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3470133688182779633L;
	@Basic(optional = false)
    @Column(name = "COD_META")
    private Long codMeta;
    @Basic(optional = false)
    @Column(name = "MEMBER_COD")
    private Long memberCod;

    public MetaMemberPK() {
    }

    public MetaMemberPK(Long codMeta, Long memberCod) {
        this.codMeta = codMeta;
        this.memberCod = memberCod;
    }

    public Long getCodMeta() {
        return codMeta;
    }

    public void setCodMeta(Long codMeta) {
        this.codMeta = codMeta;
    }

    public Long getMemberCod() {
        return memberCod;
    }

    public void setMemberCod(Long memberCod) {
        this.memberCod = memberCod;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MetaMemberPK other = (MetaMemberPK) obj;
        if (this.codMeta != other.codMeta && (this.codMeta == null || !this.codMeta.equals(other.codMeta))) {
            return false;
        }
        if (this.memberCod != other.memberCod && (this.memberCod == null || !this.memberCod.equals(other.memberCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.codMeta != null ? this.codMeta.hashCode() : 0);
        hash = 83 * hash + (this.memberCod != null ? this.memberCod.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "MetaMemberPK[codMeta=" + codMeta + ", memberCod=" + memberCod + "]";
    }
}
