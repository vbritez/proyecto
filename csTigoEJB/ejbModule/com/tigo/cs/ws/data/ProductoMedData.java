package com.tigo.cs.ws.data;

import java.util.Date;

/**
 * 
 * @author Miguel Zorrilla
 */
public class ProductoMedData {

    long id;
    String codProducto;
    String cantidad;
    String fechaHora;

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = DataConf.parse(cantidad);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public String getCodProducto() {
        return codProducto;
    }

    public void setCodProducto(String producto) {
        this.codProducto = DataConf.parse(producto);
    }
    
    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = DataConf.parse(DataConf.getDatetimeAsString(fechaHora));
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

}
