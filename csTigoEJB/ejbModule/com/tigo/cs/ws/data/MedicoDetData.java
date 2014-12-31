package com.tigo.cs.ws.data;

import java.util.Date;

/**
 * 
 * @author Miguel Zorrilla
 */
public class MedicoDetData {

    long id;
    String evento;
    String codMotivo;
    String observacion;
    String fechaHora;
    
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
        this.evento = evento;
    }

    public String getCodMotivo() {
        return codMotivo;
    }

    public void setCodMotivo(String codMotivo) {
        this.codMotivo = codMotivo;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
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
