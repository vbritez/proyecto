package com.tigo.cs.ws.data;

import java.util.Date;

/**
 * 
 * @author Miguel Zorrilla
 */
public class RondaGuardiaDetData {

    long id;
    String fechaHora;
    String observacion;
    String fueraDeTiempo;
    String latitud;
    String longitud;

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = DataConf.parse(DataConf.getDatetimeAsString(fechaHora));
    }

    public String getFueraDeTiempo() {
        return fueraDeTiempo;
    }

    public void setFueraDeTiempo(String fueraDeTiempo) {
        this.fueraDeTiempo = DataConf.parse(fueraDeTiempo);
    }

    public void setFueraDeTiempo(Date fueraDeTiempo) {
        this.fueraDeTiempo = DataConf.parse(DataConf.getDatetimeAsString(fueraDeTiempo));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = DataConf.parse(observacion);
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
