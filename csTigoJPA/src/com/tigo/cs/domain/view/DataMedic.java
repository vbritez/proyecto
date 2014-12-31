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
@Table(name = "DATA_MEDIC")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(name = "DataMedic.findAll",
        query = "SELECT d FROM DataMedic d") })
public class DataMedic extends Data implements Serializable {

    private static final long serialVersionUID = 8930337598945280150L;

    @Column(name = "NOMBRE", length = 255)
    @Searchable(label = "metadata.medic.field.Name")
    @PrimarySortedField
    @Order(1)
    private String nombre;

    @Column(name = "ESPECIALIDAD", length = 255)
    @Searchable(label = "metadata.medic.field.Specialty")
    @Order(2)
    private String especialidad;

    @Column(name = "TITULO", length = 255)
    @Searchable(label = "metadata.medic.field.Degree")
    @Order(3)
    private String titulo;

    public DataMedic() {
        dataPK = new DataPK();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    @Override
    public String toString() {
        return "DataMedic [dataPK=" + dataPK + "]";
    }

}
