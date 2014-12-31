package com.tigo.cs.domain.view;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.Order;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DATA_ACTIVITY")
public class DataActivity extends Data implements Serializable {

    private static final long serialVersionUID = 8353437712942487655L;

    @Column(name = "DESCRIPCION", length = 255)
    @Searchable(label = "metadata.activity.field.Description")
    @PrimarySortedField
    @Order(1)
    private String descripcion;

    @Column(name = "DURACION", length = 255)
    @Searchable(label = "metadata.activity.field.Duration")
    @Order(2)
    private String duracion;

    public DataActivity() {
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDuracion() {
        return this.duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    @Override
    public String toString() {
        return descripcion;
    }

}