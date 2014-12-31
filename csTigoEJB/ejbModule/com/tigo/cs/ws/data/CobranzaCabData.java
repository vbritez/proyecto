package com.tigo.cs.ws.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Miguel Zorrilla
 */
@XStreamAlias("cobranza")
public class CobranzaCabData {

    long id;
    String fechaHora;
    String nroTelefono;
    String usuarioTelefono;
    String cliente;
    String nroRecibo;
    String latitud;
    String longitud;
    @XStreamImplicit(itemFieldName = "factura")
    List<CobranzaFacturaData> facturas;
    @XStreamImplicit(itemFieldName = "cobro")
    List<CobranzaCobrosData> cobros;

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = DataConf.parse(cliente);
    }

    public List<CobranzaCobrosData> getCobros() {
        return cobros;
    }

    public void setCobros(List<CobranzaCobrosData> cobros) {
        this.cobros = DataConf.parse(cobros);
    }

    public List<CobranzaFacturaData> getFacturas() {
        return facturas;
    }

    public void setFacturas(List<CobranzaFacturaData> facturas) {
        this.facturas = DataConf.parse(facturas);
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

    public String getUsuarioTelefono() {
        return usuarioTelefono;
    }

    public void setUsuarioTelefono(String usuarioTelefono) {
        this.usuarioTelefono = DataConf.parse(usuarioTelefono);
    }

    public String getNroRecibo() {
        return nroRecibo;
    }

    public void setNroRecibo(String nroRecibo) {
        this.nroRecibo = DataConf.parse(nroRecibo);
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
