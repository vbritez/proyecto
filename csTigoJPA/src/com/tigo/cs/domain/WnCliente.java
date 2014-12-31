package com.tigo.cs.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "WN_CLIENTES")
@NamedQueries({ @NamedQuery(name = "WnCliente.deleteAll",
        query = "DELETE FROM WnCliente m "), @NamedQuery(
        name = "WnCliente.findByWnOperadora",
        query = "Select c FROM WnCliente c Where c.wnOperadora = :wnOperadora ") })
public class WnCliente implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "WN_CLIENTES_CLIENTECOD_GENERATOR",
            sequenceName = "WNCLIENT_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "WN_CLIENTES_CLIENTECOD_GENERATOR")
    @Column(name = "CLIENTE_COD")
    @PrimarySortedField
    private long wnClienteCod;

    @Searchable(label = "entity.wnCLiente.searchable.msisdn")
    private String msisdn;

    // bi-directional many-to-one association to WnOperadora
    @ManyToOne
    @JoinColumn(name = "COD_OPERADORA")
    @Searchable(label = "entity.wnCLiente.searchable.wnOperadora",
            internalField = "operadora")
    private WnOperadora wnOperadora;

    public WnCliente() {
    }

    public long getWnClienteCod() {
        return wnClienteCod;
    }

    public void setWnClienteCod(long wnClienteCod) {
        this.wnClienteCod = wnClienteCod;
    }

    public String getMsisdn() {
        return this.msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public WnOperadora getWnOperadora() {
        return this.wnOperadora;
    }

    public void setWnOperadora(WnOperadora wnOperadora) {
        this.wnOperadora = wnOperadora;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (wnClienteCod ^ (wnClienteCod >>> 32));
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
        WnCliente other = (WnCliente) obj;
        if (wnClienteCod != other.wnClienteCod)
            return false;
        return true;
    }

}