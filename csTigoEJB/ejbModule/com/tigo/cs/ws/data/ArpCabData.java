package com.tigo.cs.ws.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * 
 * @author Miguel Zorrilla
 */
@XStreamAlias("arp")
public class ArpCabData {

    long id;
    String fechaHora;
    String nroTelefono;
    String usuarioTelefono;
    String nroFactura;
    String tipoFactura;
    String monto;
    String latitud;
    String longitud = "";
    @XStreamImplicit(itemFieldName = "guia-cota")
    List<ArpDetData> detalles = new ArrayList<ArpDetData>();
    @XStreamImplicit(itemFieldName = "matricula")
    List<String> matriculas = new ArrayList<String>();

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

    public List<ArpDetData> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<ArpDetData> detalles) {
        this.detalles = DataConf.parse(detalles);
    }

    public List<String> getMatriculas() {
        return matriculas;
    }

    public void setMatriculas(List<String> matriculas) {
        this.matriculas = DataConf.parse(matriculas);
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = DataConf.parse(monto);
    }

    public String getNroFactura() {
        return nroFactura;
    }

    public void setNroFactura(String nroFactura) {
        this.nroFactura = DataConf.parse(nroFactura);
    }

    public String getNroTelefono() {
        return nroTelefono;
    }

    public void setNroTelefono(String nroTelefono) {
        this.nroTelefono = DataConf.parse(nroTelefono);
    }

    public String getTipoFactura() {
        return tipoFactura;
    }

    public void setTipoFactura(String tipoFactura) {
        this.tipoFactura = DataConf.parse(tipoFactura);
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
