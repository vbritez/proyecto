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
@Table(name = "DATA_OPERATOR")
@NamedQueries({ @NamedQuery(name = "DataOperator.findAll",
        query = "SELECT d FROM DataOperator d") })
public class DataOperator extends Data implements Serializable {

    private static final long serialVersionUID = 5743173609838068341L;
    @Column(name = "NOMBRE_APELLIDO", length = 255)
    @PrimarySortedField
    @Searchable(label = "web.client.metadataoperator.screen.field.Names")
    @Order(1)
    private String nombre;

    @Column(name = "HABILITADO")
    @Searchable(label = "web.client.metadataoperator.screen.field.Enabled")
    @Order(2)
    private String habilitado;

    @Column(name = "FECHA_MODIFICACION")
    @Order(3)
    private String fechaModificacion;

    @Transient
    private Boolean enabledAux;

    public DataOperator() {
        dataPK = new DataPK();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
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
        DataOperator other = (DataOperator) obj;
        if (nombre == null) {
            if (other.nombre != null)
                return false;
        } else if (!nombre.equals(other.nombre))
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
        return "DataOperator[DataPK=" + dataPK + "]";
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
