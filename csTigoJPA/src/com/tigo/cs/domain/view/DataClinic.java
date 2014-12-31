package com.tigo.cs.domain.view;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.Order;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "DATA_CLINIC")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(name = "DataClinic.findAll",
        query = "SELECT d FROM DataClinic d") })
public class DataClinic extends Data implements Serializable {

    private static final long serialVersionUID = 8930337598945280150L;

    @Column(name = "NOMBRE", length = 255)
    @Searchable(label = "metadata.clinic.field.Name")
    @PrimarySortedField
    @Order(1)
    private String nombre;

    public DataClinic() {
        dataPK = new DataPK();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "DataClinic [dataPK=" + dataPK + "]";
    }

}
