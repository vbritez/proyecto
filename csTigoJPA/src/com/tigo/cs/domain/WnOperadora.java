package com.tigo.cs.domain;

import java.io.Serializable;
import javax.persistence.*;

import com.tigo.cs.commons.jpa.PrimarySortedField;

import java.util.List;

@Entity
@Table(name = "WN_OPERADORA")
@NamedQueries({ @NamedQuery(name = "WnOperadora.findAllEnabled",
        query = "Select o FROM WnOperadora o Where o.activo = true ") })
public class WnOperadora implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "WN_OPERADORA_OPERADORACOD_GENERATOR",
            sequenceName = "WNOPERADORA_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "WN_OPERADORA_OPERADORACOD_GENERATOR")
    @Column(name = "OPERADORA_COD")
    private long wnOperadoraCod;

    private Boolean activo;

    @PrimarySortedField
    private String operadora;

    // bi-directional many-to-one association to WnCliente
    @OneToMany(mappedBy = "wnOperadora")
    private List<WnCliente> wnClientes;

    // bi-directional many-to-one association to WnMensaje
    @OneToMany(mappedBy = "wnOperadora")
    private List<WnMensaje> wnMensajes;

    @Transient
    private String tipoMensaje;

    public WnOperadora() {
    }

    public long getWnOperadoraCod() {
        return wnOperadoraCod;
    }

    public void setWnOperadoraCod(long wnOperadoraCod) {
        this.wnOperadoraCod = wnOperadoraCod;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getOperadora() {
        return this.operadora;
    }

    public void setOperadora(String operadora) {
        this.operadora = operadora;
    }

    public String getTipoMensaje() {
        return tipoMensaje;
    }

    public void setTipoMensaje(String tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }

    public List<WnCliente> getWnClientes() {
        return this.wnClientes;
    }

    public void setWnClientes(List<WnCliente> wnClientes) {
        this.wnClientes = wnClientes;
    }

    public WnCliente addWnClientes(WnCliente wnClientes) {
        getWnClientes().add(wnClientes);
        wnClientes.setWnOperadora(this);

        return wnClientes;
    }

    public WnCliente removeWnClientes(WnCliente wnClientes) {
        getWnClientes().remove(wnClientes);
        wnClientes.setWnOperadora(null);

        return wnClientes;
    }

    public List<WnMensaje> getWnMensajes() {
        return this.wnMensajes;
    }

    public void setWnMensajes(List<WnMensaje> wnMensajes) {
        this.wnMensajes = wnMensajes;
    }

    public WnMensaje addWnMensajes(WnMensaje wnMensajes) {
        getWnMensajes().add(wnMensajes);
        wnMensajes.setWnOperadora(this);

        return wnMensajes;
    }

    public WnMensaje removeWnMensajes(WnMensaje wnMensajes) {
        getWnMensajes().remove(wnMensajes);
        wnMensajes.setWnOperadora(null);

        return wnMensajes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + (int) (wnOperadoraCod ^ (wnOperadoraCod >>> 32));
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
        WnOperadora other = (WnOperadora) obj;
        if (wnOperadoraCod != other.wnOperadoraCod)
            return false;
        return true;
    }

}