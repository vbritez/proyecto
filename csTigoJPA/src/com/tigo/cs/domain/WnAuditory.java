package com.tigo.cs.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "WN_AUDITORY")
public class WnAuditory implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "WN_AUDITORY_AUDITORYCOD_GENERATOR",
            sequenceName = "WNAUDITORY_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "WN_AUDITORY_AUDITORYCOD_GENERATOR")
    @Column(name = "AUDITORY_COD")
    private long auditoryCod;

    @Temporal(TemporalType.DATE)
    private Date auditdate;

    private String estado;

    private String mensaje;

    private String msisdn;

    private String operadora;

    private String tipomsj;

    public WnAuditory() {
    }

    public long getAuditoryCod() {
        return this.auditoryCod;
    }

    public void setAuditoryCod(long auditoryCod) {
        this.auditoryCod = auditoryCod;
    }

    public Date getAuditdate() {
        return this.auditdate;
    }

    public void setAuditdate(Date auditdate) {
        this.auditdate = auditdate;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return this.mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMsisdn() {
        return this.msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getOperadora() {
        return this.operadora;
    }

    public void setOperadora(String operadora) {
        this.operadora = operadora;
    }

    public String getTipomsj() {
        return this.tipomsj;
    }

    public void setTipomsj(String tipomsj) {
        this.tipomsj = tipomsj;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (auditoryCod ^ (auditoryCod >>> 32));
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
        WnAuditory other = (WnAuditory) obj;
        if (auditoryCod != other.auditoryCod)
            return false;
        return true;
    }

}