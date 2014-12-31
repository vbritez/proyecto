package com.tigo.cs.domain.view;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.tigo.cs.commons.jpa.Order;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DATA_ATTENDANT")
@NamedQueries({ @NamedQuery(name = "DataAttendant.findAll",
        query = "SELECT d FROM DataAttendant d") })
public class DataAttendant extends Data implements Serializable {

    private static final long serialVersionUID = 5716311742451727667L;
    @Column(name = "NOMBRE_APELLIDO", length = 255)
    @PrimarySortedField
    @Searchable(label = "web.client.metadataattendant.screen.field.Names")
    @Order(1)
    private String nombre;
    @Column(name = "NOMBRE_USUARIO", length = 255)
    @Searchable(label = "web.client.metadataattendant.screen.field.Username")
    @Order(2)
    private String nombreUsuario;

    @Column(name = "CONTRASENHA", length = 255)
    @Order(3)
    private String pass;

    @Column(name = "HABILITADO")
    @Searchable(label = "web.client.metadataattendant.screen.field.Enabled")
    @Order(4)
    private String habilitado;

    @Column(name = "FECHA_MODIFICACION")
    @Order(5)
    private String fechaModificacion;
    @Column(name = "REINTENTOS")
    @Order(6)
    private String reintentos;

    @Transient
    private Boolean enabledAux;

    public DataAttendant() {
        dataPK = new DataPK();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
        result = prime * result
            + ((nombreUsuario == null) ? 0 : nombreUsuario.hashCode());
        result = prime * result + ((pass == null) ? 0 : pass.hashCode());
        result = prime * result
            + ((habilitado == null) ? 0 : habilitado.hashCode());
        result = prime * result
            + ((fechaModificacion == null) ? 0 : fechaModificacion.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        DataAttendant other = (DataAttendant) obj;
        if (nombre == null) {
            if (other.nombre != null)
                return false;
        } else if (!nombre.equals(other.nombre))
            return false;
        if (nombreUsuario == null) {
            if (other.nombreUsuario != null)
                return false;
        } else if (!nombreUsuario.equals(other.nombreUsuario))
            return false;
        if (pass == null) {
            if (other.pass != null)
                return false;
        } else if (!pass.equals(other.pass))
            return false;

        if (habilitado == null) {
            if (other.habilitado != null)
                return false;
        } else if (!habilitado.equals(other.habilitado))
            return false;

        if (fechaModificacion == null) {
            if (other.fechaModificacion != null)
                return false;
        } else if (!fechaModificacion.equals(other.fechaModificacion))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DataAttendant[DataPK=" + dataPK + "]";
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(String habilitado) {
        this.habilitado = habilitado;
    }

    public Boolean getEnabledAux() {
        return enabledAux;
    }

    public void setEnabledAux(Boolean enabledAux) {
        this.enabledAux = enabledAux;
    }

    public String getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(String fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getReintentos() {
        return reintentos;
    }

    public void setReintentos(String reintentos) {
        this.reintentos = reintentos;
    }

}
