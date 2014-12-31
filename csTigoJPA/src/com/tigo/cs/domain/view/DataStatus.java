package com.tigo.cs.domain.view;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.Order;

@Entity
@Table(name = "DATA_STATUS")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class DataStatus extends Data implements Serializable {

    private static final long serialVersionUID = -7389294437524481698L;

    @Column(name = "DESCRIPCION", length = 255)
    @Order(1)
    private String descripcion;

    @Column(name = "CODIGO_INTEGRACION", length = 255)
    @Order(2)
    private String codigoIntegracion;

    public DataStatus() {
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCodigoIntegracion() {
        return codigoIntegracion;
    }

    public void setCodigoIntegracion(String codigoIntegracion) {
        this.codigoIntegracion = codigoIntegracion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
            + ((descripcion == null) ? 0 : descripcion.hashCode());
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
        return true;
    }

    @Override
    public String toString() {
        return descripcion;
    }

}