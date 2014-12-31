package com.tigo.cs.ws.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.Date;

/**
 * 
 * @author Miguel Zorrilla
 */

@XStreamAlias("visita")
public class VisitaData {

    long id;
    String fechaHora;
    String nroTelefono;
    String usuarioTelefono;
    String evento;
    String cliente;
    String motivo;
    String observacion;
    String duracion;
    String latitud;
    String longitud;

    public String getCliente() {
        return cliente;
    }

    public String getDuracion() {
        return duracion;
    }

    public String getEvento() {
        return evento;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public long getId() {
        return id;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setCliente(String cliente) {
        this.cliente = DataConf.parse(cliente);
    }

    public void setDuracion(String duracion) {
        this.duracion = DataConf.parse(duracion);
    }

    public void setEvento(String evento) {
        this.evento = DataConf.parse(evento);
    }
    
    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public void setFechahora(Date fechaHora) {
        this.fechaHora = DataConf.parse(DataConf.getDatetimeAsString(fechaHora));
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setMotivo(String motivo) {
        this.motivo = DataConf.parse(motivo);
    }

    public void setObservacion(String observacion) {
        this.observacion = DataConf.parse(observacion);
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
