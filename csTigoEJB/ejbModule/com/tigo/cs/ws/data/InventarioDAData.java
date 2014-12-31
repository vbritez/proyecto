package com.tigo.cs.ws.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.Date;

/**
 * 
 * @author Miguel Zorrilla
 */
@XStreamAlias("inventario")
public class InventarioDAData {

    long id;
    String fechaHora;
    String nroTelefono;
    String usuarioTelefono;
    String supeR;
    String repartidor;
    String tipoBandeja;
    String ubicacion;
    String cantidad;
    String latitud;
    String longitud;

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = DataConf.parse(cantidad);
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
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

    public String getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(String repartidor) {
        this.repartidor = DataConf.parse(repartidor);
    }

    public String getSupeR() {
        return supeR;
    }

    public void setSupeR(String supeR) {
        this.supeR = DataConf.parse(supeR);
    }

    public String getTipoBandeja() {
        return tipoBandeja;
    }

    public void setTipoBandeja(String tipoBandeja) {
        this.tipoBandeja = DataConf.parse(tipoBandeja);
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = DataConf.parse(ubicacion);
    }

    public String getUsuarioTelefono() {
        return usuarioTelefono;
    }

    public void setUsuarioTelefono(String usuarioTelefono) {
        this.usuarioTelefono = DataConf.parse(usuarioTelefono);
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
