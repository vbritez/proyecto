package com.tigo.cs.domain.view;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author Miguel Zorrilla
 */
@Embeddable
public class DataPK implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -3168428962523602481L;
    @Basic(optional = false)
    @Column(name = "COD_CLIENT")
    private Long codClient;
    @Basic(optional = false)
    @Column(name = "COD_META")
    private Long codMeta;
    @Basic(optional = false)
    @Column(name = "CODIGO")
    private String codigo;

    public DataPK() {
    }

    public DataPK(Long codClient, Long codMeta, String codigo) {
        this.codClient = codClient;
        this.codMeta = codMeta;
        this.codigo = codigo;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataPK other = (DataPK) obj;
        if (this.codClient != other.codClient
            && (this.codClient == null || !this.codClient.equals(other.codClient))) {
            return false;
        }
        if (this.codMeta != other.codMeta
            && (this.codMeta == null || !this.codMeta.equals(other.codMeta))) {
            return false;
        }
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash
            + (this.codClient != null ? this.codClient.hashCode() : 0);
        hash = 23 * hash + (this.codMeta != null ? this.codMeta.hashCode() : 0);
        hash = 23 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "DataPK[codClient=" + codClient + ", codMeta=" + codMeta
            + ", codigo=" + codigo + "]";
    }

}
