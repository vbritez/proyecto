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
@Table(name = "DATA_MACHINE")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(name = "DataMachine.findAll",
        query = "SELECT d FROM DataMachine d") })
public class DataMachine extends Data implements Serializable {

    private static final long serialVersionUID = 941992989005776959L;

    @Column(name = "DESCRIPCION", length = 255)
    @PrimarySortedField
    @Searchable(label = "web.client.metadatamachine.screen.field.Description")
    @Order(1)
    private String descripcion;

    @Column(name = "HABILITADO")
    @Searchable(label = "web.client.metadatamachine.screen.field.Enabled")
    @Order(2)
    private String habilitado;

    @Column(name = "FECHA_MODIFICACION")
    @Order(3)
    private String fechaModificacion;

    @Transient
    private Boolean enabledAux;

    public DataMachine() {
        dataPK = new DataPK();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
            + ((descripcion == null) ? 0 : descripcion.hashCode());
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
        DataMachine other = (DataMachine) obj;
        if (descripcion == null) {
            if (other.descripcion != null)
                return false;
        } else if (!descripcion.equals(other.descripcion))
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
        return "DataMachine[DataPK=" + dataPK + "]";
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
}
