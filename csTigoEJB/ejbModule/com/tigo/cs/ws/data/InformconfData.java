package com.tigo.cs.ws.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.Date;

/**
 * 
 * @author Miguel Zorrilla
 */
@XStreamAlias("consulta")
public class InformconfData {

    long id;
    String fechaHora;
    String nroTelefono;
    String usuarioTelefono;
    String persona;
    String Nombre;
    String latitud;
    String longitud;

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = DataConf.parse(Nombre);
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

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = DataConf.parse(persona);
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
