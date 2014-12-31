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
@Table(name = "DATA_CONTACTS")
public class DataContacts extends Data implements Serializable {

    private static final long serialVersionUID = 1601567482259425302L;

    @Column(name = "NOMBRE", length = 255)
    @Searchable(label = "metadata.contacts.field.Name")
    @PrimarySortedField
    @Order(1)
    private String nombre;

    @Column(name = "APELLIDO", length = 255)
    @Searchable(label = "metadata.contacts.field.LastName")
    @Order(2)
    private String apellido;

    @Column(name = "NRO_TELEFONO", length = 255)
    @Searchable(label = "metadata.contacts.field.PhoneNumber")
    @Order(3)
    private String nroTelefono;

    @Column(name = "CELULAR", length = 255)
    @Searchable(label = "metadata.contacts.field.Cellphone")
    @Order(4)
    private String celular;

    @Column(name = "DIRECCION", length = 255)
    @Searchable(label = "metadata.contacts.field.Address")
    @Order(5)
    private String direccion;

    public DataContacts() {
        dataPK = new DataPK();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNroTelefono() {
        return nroTelefono;
    }

    public void setNroTelefono(String nroTelefono) {
        this.nroTelefono = nroTelefono;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return "DataContacts [dataPK=" + dataPK + "]";
    }
}
