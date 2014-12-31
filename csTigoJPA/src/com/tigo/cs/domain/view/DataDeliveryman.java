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
@Table(name = "DATA_DELIVERYMAN")
public class DataDeliveryman extends Data implements Serializable {

    private static final long serialVersionUID = -6727260615825207693L;
    @Column(name = "NOMBRE", length = 255)
    @Searchable(
            label = "web.client.metadatadeliverer.screen.field.NombreDeliverer")
    @PrimarySortedField
    @Order(1)
    private String nombre;

    public DataDeliveryman() {
        dataPK = new DataPK();
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}