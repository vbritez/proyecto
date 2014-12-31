package com.tigo.cs.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author Miguel Zorrilla
 * @version $Id$
 */
@Embeddable
public class MetaDataPK implements Serializable, Cloneable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5811833311659592336L;
	@Basic(optional = false)
    @Column(name = "COD_CLIENT")
    private Long codClient;
    @Basic(optional = false)
    @Column(name = "COD_META")
    private Long codMeta;
    @Basic(optional = false)
    @Column(name = "COD_MEMBER")
    private Long codMember;
    @Basic(optional = false)
    @Column(name = "CODE_CHR")
    @NotEmpty(message = "{entity.metaDataPk.constraint.code.NotEmpty}")
    private String codeChr;

    public MetaDataPK() {
    }

    public MetaDataPK(Long codClient, Long codMeta, Long codMember, String codeChr) {
        this.codClient = codClient;
        this.codMeta = codMeta;
        this.codMember = codMember;
        this.codeChr = codeChr;
    }

    public Long getCodClient() {
        return codClient;
    }

    public void setCodClient(Long codClient) {
        this.codClient = codClient;
    }

    public Long getCodMeta() {
        return codMeta;
    }

    public void setCodMeta(Long codMeta) {
        this.codMeta = codMeta;
    }

    public Long getCodMember() {
        return codMember;
    }

    public void setCodMember(Long codMember) {
        this.codMember = codMember;
    }

    public String getCodeChr() {
        return codeChr;
    }

    public void setCodeChr(String codeChr) {
        this.codeChr = codeChr;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MetaDataPK other = (MetaDataPK) obj;
        if (this.codClient != other.codClient && (this.codClient == null || !this.codClient.equals(other.codClient))) {
            return false;
        }
        if (this.codMeta != other.codMeta && (this.codMeta == null || !this.codMeta.equals(other.codMeta))) {
            return false;
        }
        if (this.codMember != other.codMember && (this.codMember == null || !this.codMember.equals(other.codMember))) {
            return false;
        }
        if ((this.codeChr == null) ? (other.codeChr != null) : !this.codeChr.equals(other.codeChr)) {
            return false;
        }
        return true;
    }

    @Override
    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        } catch (CloneNotSupportedException e) {
            // No deberia suceder
        }
        ((MetaDataPK) clone).setCodClient(new Long(codClient));
        ((MetaDataPK) clone).setCodMember(new Long(codMember));
        ((MetaDataPK) clone).setCodMeta(new Long(codMeta));
        ((MetaDataPK) clone).setCodeChr(new String(codeChr.getBytes()));
        return clone;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.codClient != null ? this.codClient.hashCode() : 0);
        hash = 79 * hash + (this.codMeta != null ? this.codMeta.hashCode() : 0);
        hash = 79 * hash + (this.codMember != null ? this.codMember.hashCode() : 0);
        hash = 79 * hash + (this.codeChr != null ? this.codeChr.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "MetaDataPK[codClient=" + codClient + ", codMeta=" + codMeta + ", codMember=" + codMember + ", codeChr=" + codeChr + "]";
    }
}
