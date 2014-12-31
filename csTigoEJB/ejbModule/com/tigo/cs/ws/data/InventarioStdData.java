package com.tigo.cs.ws.data;

import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 
 * @author Miguel Zorrilla
 */
@XStreamAlias("inventario")
public class InventarioStdData {
    long id;
    String fechaHora;
    String nroTelefono;
    String usuarioTelefono;
    String deposito;
    String producto;
    String cantidad;
    String latitud;
    String longitud;

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = DataConf.parse(fechaHora);
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = DataConf.parse(DataConf.getDatetimeAsString(fechaHora));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNroTelefono() {
        return nroTelefono;
    }

    public void setNroTelefono(String nroTelefono) {
        this.nroTelefono = DataConf.parse(nroTelefono);
    }

    public String getUsuarioTelefono() {
        return usuarioTelefono;
    }

    public void setUsuarioTelefono(String usuarioTelefono) {
        this.usuarioTelefono = DataConf.parse(usuarioTelefono);
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = DataConf.parse(cantidad);
    }

    public String getDeposito() {
        return deposito;
    }

    public void setDeposito(String deposito) {
        this.deposito = DataConf.parse(deposito);
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = DataConf.parse(producto);
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = DataConf.parse(latitud);
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = DataConf.parse(longitud);
    }
}
