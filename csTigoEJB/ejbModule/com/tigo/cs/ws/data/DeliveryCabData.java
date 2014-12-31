package com.tigo.cs.ws.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Miguel Zorrilla
 */
@XStreamAlias("entrega")
public class DeliveryCabData {

    long id;
    String fechaHora;
    String nroTelefono;
    String usuarioTelefono;
    String cliente;
    String latitud;
    String longitud;
    @XStreamImplicit(itemFieldName = "remision")
    List<String> remisiones;

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = DataConf.parse(cliente);
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = DataConf.parse(fechaHora);
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = DataConf.getDatetimeAsString(fechaHora);
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

    public List<String> getRemisiones() {
        return remisiones;
    }

    public void setRemisiones(List<String> remisiones) {
        this.remisiones = DataConf.parse(remisiones);
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
