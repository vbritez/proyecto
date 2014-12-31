package com.tigo.cs.ws.data;

import java.util.Date;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * 
 * @author Miguel Zorrilla
 */
@XStreamAlias("courier")
public class CourierCab {
    long id;
    String fechaHora;
    String nroTelefono;
    String usuarioTelefono;
    String receptor;
    String motivo;
    String observacion;
    String latitud;
    String longitud;
    @XStreamImplicit(itemFieldName = "detalle")
    List<CourierDet> detalles;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }
    
    public void setFechaHora(Date fechaHora) {
        this.fechaHora = DataConf
                .parse(DataConf.getDatetimeAsString(fechaHora));
    }

    public String getNroTelefono() {
        return nroTelefono;
    }

    public void setNroTelefono(String nroTelefono) {
        this.nroTelefono = nroTelefono;
    }

    public String getUsuarioTelefono() {
        return usuarioTelefono;
    }

    public void setUsuarioTelefono(String usuarioTelefono) {
        this.usuarioTelefono = usuarioTelefono;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public List<CourierDet> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<CourierDet> detalles) {
        this.detalles = detalles;
    }

}
