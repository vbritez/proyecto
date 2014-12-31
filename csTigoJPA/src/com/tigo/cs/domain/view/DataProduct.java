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
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DATA_PRODUCT")
@NamedQueries({ @NamedQuery(name = "DataProduct.findAll",
        query = "SELECT d FROM DataProduct d") })
public class DataProduct extends Data implements Serializable {

    private static final long serialVersionUID = -7126670625226777764L;

    @Column(name = "DESCRIPCION", length = 255)
    @Searchable(label = "web.client.metadataproduct.screen.field.Product")
    @PrimarySortedField
    @Order(1)
    private String descripcion;

    @Column(name = "LISTA_PRECIO", length = 255)
    @Searchable(label = "web.client.metadataproduct.screen.field.ListaPrecio")
    @Order(2)
    private String listaPrecio;

    @Column(name = "PRECIO", length = 255)
    @Searchable(label = "web.client.metadataproduct.screen.field.Precio")
    @Order(3)
    private String precio;

    @Column(name = "DESCUENTO", length = 255)
    @Searchable(label = "web.client.metadataproduct.screen.field.Descuento")
    @Order(4)
    private String descuento;

    @Column(name = "DEPOSITO", length = 255)
    @Searchable(label = "web.client.metadataproduct.screen.field.Deposito")
    @Order(5)
    private String deposito;

    public DataProduct() {
        dataPK = new DataPK();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getListaPrecio() {
        return listaPrecio;
    }

    public void setListaPrecio(String listaPrecio) {
        this.listaPrecio = listaPrecio;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getDescuento() {
        return descuento;
    }

    public void setDescuento(String descuento) {
        this.descuento = descuento;
    }

    public String getDeposito() {
        return deposito;
    }

    public void setDeposito(String deposito) {
        this.deposito = deposito;
    }

    @Override
    public String toString() {
        return "com.tigo.cs.domain.view.DataProduct[DataPK=" + dataPK + "]";
    }
}
