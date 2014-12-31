package com.tigo.cs.ws.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Miguel Zorrilla
 */
@XStreamAlias("asistencia")
public class AsistenciaCabData {
    long id;
    String fecha;
    String nroTelefono;
    String usuarioTelefono;
    String codigoEmpleado;
    @XStreamImplicit(itemFieldName = "detalle")
    List<AsistenciaDetData> detalles;

    public String getCodigoEmpleado() {
        return codigoEmpleado;
    }

    public void setCodigoEmpleado(String codigoEmpleado) {
        this.codigoEmpleado = DataConf.parse(codigoEmpleado);
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = DataConf.parse(fecha);
    }

    public void setFecha(Date fechaHora) {
        this.fecha = DataConf.parse(DataConf.getDateAsString(fechaHora));
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

    public String getUsuarioTelefono() {
        return usuarioTelefono;
    }

    public void setUsuarioTelefono(String usuarioTelefono) {
        this.usuarioTelefono = DataConf.parse(usuarioTelefono);
    }

    public List<AsistenciaDetData> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<AsistenciaDetData> detalles) {
        this.detalles = DataConf.parse(detalles);
    }

}
