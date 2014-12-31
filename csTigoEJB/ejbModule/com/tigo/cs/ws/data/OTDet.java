package com.tigo.cs.ws.data;

/**
 * 
 * @author Viviana Britez
 */
public class OTDet {
    long id;
    String evento;
    String observacion;
    
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
    public String getObservacion() {
        return observacion;
    }
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
