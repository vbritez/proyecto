package com.tigo.cs.ws.data;

import java.util.Date;

/**
 * 
 * @author Miguel Zorrilla
 */
public class FlotaTramoData {

    String cliente;
    String kilometrajeActual;
    String duracion;
    String latitud;
    String longitud;
    String fechaHora;

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = DataConf.parse(cliente);
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = DataConf.parse(duracion);
    }

    public String getKilometrajeActual() {
        return kilometrajeActual;
    }

    public void setKilometrajeActual(String kilometrajeActual) {
        this.kilometrajeActual = DataConf.parse(kilometrajeActual);
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

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = DataConf.parse(fechaHora);
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = DataConf.parse(DataConf.getDatetimeAsString(fechaHora));
    }
}
