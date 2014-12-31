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
@Table(name = "DATA_VEHICLE")
public class DataVehicle extends Data implements Serializable {

    private static final long serialVersionUID = 5428083788225444496L;
    @Column(name = "DESCRIPCION", length = 255)
    @PrimarySortedField
    @Searchable(label = "web.client.metadatavehicle.screen.field.ModeloAuto")
    @Order(1)
    private String descripcion;

    public DataVehicle() {
        dataPK = new DataPK();
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}