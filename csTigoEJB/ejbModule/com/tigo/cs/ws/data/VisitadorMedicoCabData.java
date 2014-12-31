package com.tigo.cs.ws.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Miguel Zorrilla
 */
@XStreamAlias("visitadores")
public class VisitadorMedicoCabData {

    long id;
    String nroTelefono;
    String usuarioTelefono;
    String fecha;
    @XStreamImplicit(itemFieldName = "local")
    List<ConsultorioCabData> consultoriosCab;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = DataConf.parse(DataConf.getDatetimeAsString(fecha));
    }

    public List<ConsultorioCabData> getConsultoriosCab() {
        return consultoriosCab;
    }

    public void setConsultoriosCab(List<ConsultorioCabData> consultoriosCab) {
        this.consultoriosCab = consultoriosCab;
    }

}
