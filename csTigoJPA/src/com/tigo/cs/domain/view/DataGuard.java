package com.tigo.cs.domain.view;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.Order;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "DATA_GUARD")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class DataGuard extends Data implements Serializable {

    private static final long serialVersionUID = -3072342517440227470L;

    @Column(name = "NOMBRE", length = 255)
    @PrimarySortedField
    @Searchable(label = "web.client.metadataguard.screen.field.NombreGuard")
    @Order(1)
    private String nombre;

    @Column(name = "TELEFONO", length = 255)
    @Order(2)
    private String telefono;

    @Column(name = "ESTADO", length = 255)
    @Order(3)
    private String estado;

    public DataGuard() {
        dataPK = new DataPK();
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

}