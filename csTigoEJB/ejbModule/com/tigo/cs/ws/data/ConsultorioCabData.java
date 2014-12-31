package com.tigo.cs.ws.data;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Miguel Zorrilla
 */
public class ConsultorioCabData {

    long id;
    String codLocal;
    String fechaHora;
    String latitud;
    String longitud;
    @XStreamImplicit(itemFieldName = "local_detalle")
    List<ConsultorioDetData> detalles;

    @XStreamImplicit(itemFieldName = "medico")
    List<MedicoCabData> medicos;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCodLocal() {
        return codLocal;
    }

    public void setCodLocal(String codLocal) {
        this.codLocal = codLocal;
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

    public List<ConsultorioDetData> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<ConsultorioDetData> detalles) {
        this.detalles = DataConf.parse(detalles);
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

    public List<MedicoCabData> getMedicos() {
        return medicos;
    }

    public void setMedicos(List<MedicoCabData> medicos) {
        this.medicos = medicos;
    }

}
