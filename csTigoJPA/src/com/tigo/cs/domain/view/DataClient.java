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
@Table(name = "DATA_CLIENT")
public class DataClient extends Data implements Serializable {

    private static final long serialVersionUID = 8060911426108870612L;

    @Column(name = "CLIENTE", length = 255)
    @PrimarySortedField
    @Searchable(label = "web.client.metadataclient.screen.field.Client")
    @Order(1)
    private String cliente;

    @Column(name = "RUC", length = 255)
    @Searchable(label = "web.client.metadataclient.screen.field.RUC")
    @Order(2)
    private String ruc;

    @Column(name = "DIRECCION", length = 255)
    @Searchable(label = "web.client.metadataclient.screen.field.Address")
    @Order(3)
    private String direccion;

    @Column(name = "TELEFONO", length = 255)
    @Searchable(label = "web.client.metadataclient.screen.field.Telephone")
    @Order(4)
    private String telefono;

    @Column(name = "ZONA", length = 255)
    @Searchable(label = "web.client.metadataclient.screen.field.Zone")
    @Order(5)
    private String zona;

    @Column(name = "NROCONTACTO", length = 255)
    @Searchable(label = "web.client.metadataclient.screen.field.ContactNumber")
    @Order(6)
    private String nrocontacto;

    @Searchable(label = "web.client.metadataclient.screen.field.ContactName")
    @Column(name = "NOMBRECONTACTO", length = 255)
    @Order(7)
    private String nombrecontacto;

    @Searchable(label = "web.client.metadataclient.screen.field.Observation")
    @Column(name = "OBSERVACION", length = 255)
    @Order(8)
    private String observacion;

    public DataClient() {
        dataPK = new DataPK();
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public String getNrocontacto() {
        return nrocontacto;
    }

    public void setNrocontacto(String nrocontacto) {
        this.nrocontacto = nrocontacto;
    }

    public String getNombrecontacto() {
        return nombrecontacto;
    }

    public void setNombrecontacto(String nombrecontacto) {
        this.nombrecontacto = nombrecontacto;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    @Override
    public String toString() {
        return "com.tigo.cs.domain.view.DataClient[DataPK=" + dataPK + "]";
    }

}
