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
public class MetaClientPK implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4960409806684293003L;
	@Basic(optional = false)
    @Column(name = "COD_META")
    private Long codMeta;
    @Basic(optional = false)
    @Column(name = "COD_CLIENT")
    private Long codClient;

    public MetaClientPK() {
    }

    public MetaClientPK(Long codMeta, Long codClient) {
        this.codMeta = codMeta;
        this.codClient = codClient;
    }

    public Long getCodMeta() {
        return codMeta;
    }

    public void setCodMeta(Long codMeta) {
        this.codMeta = codMeta;
    }

    public Long getCodClient() {
        return codClient;
    }

    public void setCodClient(Long codClient) {
        this.codClient = codClient;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MetaClientPK other = (MetaClientPK) obj;
        if (this.codMeta != other.codMeta && (this.codMeta == null || !this.codMeta.equals(other.codMeta))) {
            return false;
        }
        if (this.codClient != other.codClient && (this.codClient == null || !this.codClient.equals(other.codClient))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.codMeta != null ? this.codMeta.hashCode() : 0);
        hash = 67 * hash + (this.codClient != null ? this.codClient.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "MetaClientPK[codMeta=" + codMeta + ", codClient=" + codClient + "]";
    }
}
