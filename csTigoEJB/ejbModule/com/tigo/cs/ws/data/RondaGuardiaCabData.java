package com.tigo.cs.ws.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author Miguel Zorrilla
 */
@XStreamAlias("ronda")
public class RondaGuardiaCabData {

    long id;
    String nroTelefono;
    String usuarioTelefono;
    String codGuardia;
    String fechaRonda;
    @XStreamImplicit(itemFieldName = "detalle")
    List<RondaGuardiaDetData> detalle;
    
    public String getCodGuardia() {
        return codGuardia;
    }

    public void setCodGuardia(String codGuardia) {
        this.codGuardia = codGuardia;
    }

    public String getFechaRonda() {
        return fechaRonda;
    }

    public void setFechaRonda(String fechaRonda) {
        this.fechaRonda = DataConf.parse(DataConf.standarizeDateTime("dd/MM/yyyy HH:mm", fechaRonda));
    }
    
    public void setFechaRonda(Date fechaHora) {
        this.fechaRonda = DataConf.parse(DataConf.getDatetimeAsString(fechaHora));
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public List<RondaGuardiaDetData> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<RondaGuardiaDetData> detalle) {
        this.detalle = DataConf.parse(detalle);
    }

}
