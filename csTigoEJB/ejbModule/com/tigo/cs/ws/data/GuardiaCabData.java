package com.tigo.cs.ws.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.List;

/**
 * 
 * @author Miguel Zorrilla
 */
@XStreamAlias("marcacion")
public class GuardiaCabData {

    long id;
    String nroTelefono;
    String usuarioTelefono;
    String fechaHoraApertura;
    String fechaHoraCierre;
    String observacionApertura;
    String observacionCierre;
    String lugar;
    String latitudApertura;
    String longitudApertura;
    @XStreamImplicit(itemFieldName = "detalle")
    List<GuardiaDetData> detalle;

    public String getFechaHoraApertura() {
        return fechaHoraApertura;
    }

    public void setFechaHoraApertura(String fechaHoraApertura) {
        this.fechaHoraApertura = DataConf.parse(DataConf.standarizeDateTime("dd/MM/yyyy HH:mm", fechaHoraApertura));
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

    public String getFechaHoraCierre() {
        return fechaHoraCierre;
    }

    public void setFechaHoraCierre(String fechaHoraCierre) {
        this.fechaHoraCierre = DataConf.parse(DataConf.standarizeDateTime("dd/MM/yyyy HH:mm", fechaHoraCierre));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = DataConf.parse(lugar);
    }

    public List<GuardiaDetData> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<GuardiaDetData> detalle) {
        this.detalle = DataConf.parse(detalle);
    }

    public String getObservacionApertura() {
        return observacionApertura;
    }

    public void setObservacionApertura(String observacionApertura) {
        this.observacionApertura = DataConf.parse(observacionApertura);
    }

    public String getObservacionCierre() {
        return observacionCierre;
    }

    public void setObservacionCierre(String observacionCierre) {
        this.observacionCierre = DataConf.parse(observacionCierre);
    }

    public String getLatitudApertura() {
        return latitudApertura;
    }

    public void setLatitudApertura(String latitudApertura) {
        this.latitudApertura = DataConf.parse(latitudApertura);
    }

    public String getLongitudApertura() {
        return longitudApertura;
    }

    public void setLongitudApertura(String longitudApertura) {
        this.longitudApertura = DataConf.parse(longitudApertura);
    }
}
