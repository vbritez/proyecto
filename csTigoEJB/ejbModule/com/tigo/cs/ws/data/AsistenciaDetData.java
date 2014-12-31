package com.tigo.cs.ws.data;

import java.util.Date;

/**
 * 
 * @author Miguel Zorrilla
 */
public class AsistenciaDetData {
    long id;
    String fechaHora;
    String evento;
    String tipo;
    String latitud;
    String longitud;
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = DataConf.parse(evento);
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = DataConf.parse(tipo);
    }

    public String getLatitud() {
        return latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
    
    

}
