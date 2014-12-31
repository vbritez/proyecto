package com.tigo.cs.domain;

import java.io.Serializable;
import javax.persistence.*;

import com.tigo.cs.commons.jpa.PrimarySortedField;

@Entity
@Table(name = "WN_MENSAJE")
public class WnMensaje implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "WN_MENSAJE_MENSAJECOD_GENERATOR",
            sequenceName = "WNMENSAJE_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "WN_MENSAJE_MENSAJECOD_GENERATOR")
    @Column(name = "MENSAJE_COD")
    @PrimarySortedField
    private long wnMensajeCod;

    private Boolean activo;

    private String mensaje;

    private String tipomsj;

    // bi-directional many-to-one association to WnOperadora
    @ManyToOne
    @JoinColumn(name = "COD_OPERADORA")
    private WnOperadora wnOperadora;

    public WnMensaje() {
    }

    public long getWnMensajeCod() {
        return wnMensajeCod;
    }

    public void setWnMensajeCod(long wnMensajeCod) {
        this.wnMensajeCod = wnMensajeCod;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getMensaje() {
        return this.mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipomsj() {
        return this.tipomsj;
    }

    public void setTipomsj(String tipomsj) {
        this.tipomsj = tipomsj;
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
        result = prime * result + (int) (wnMensajeCod ^ (wnMensajeCod >>> 32));
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
        WnMensaje other = (WnMensaje) obj;
        if (wnMensajeCod != other.wnMensajeCod)
            return false;
        return true;
    }

}