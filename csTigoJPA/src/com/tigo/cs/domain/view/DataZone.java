package com.tigo.cs.domain.view;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.Order;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

/**
 * The persistent class for the DATA_ZONE database table.
 * 
 */
@Entity
@Table(name = "DATA_ZONE")
public class DataZone extends Data implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "DESCRIPCION", length = 255)
    @PrimarySortedField
    @Searchable(label = "web.client.metadatazone.screen.field.Description")
    @Order(1)
    private String descripcion;

    public DataZone() {
        dataPK = new DataPK();
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }

}