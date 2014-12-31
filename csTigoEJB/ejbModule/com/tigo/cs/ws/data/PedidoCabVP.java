package com.tigo.cs.ws.data;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Miguel Zorrilla
 */
public class PedidoCabVP {

    long id;
    String fechaHora;
    String tipoFactura;
    String listaPrecio;
    String totalPedido;
    String latitud;
    String longitud;
    @XStreamImplicit(itemFieldName = "detalle")
    List<PedidoDet> detalles;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = DataConf.parse(DataConf.getDatetimeAsString(fechaHora));
    }

    public String getListaPrecio() {
        return listaPrecio;
    }

    public void setListaPrecio(String listaPrecio) {
        this.listaPrecio = DataConf.parse(listaPrecio);
    }

    public String getTipoFactura() {
        return tipoFactura;
    }

    public void setTipoFactura(String tipoFactura) {
        this.tipoFactura = DataConf.parse(tipoFactura);
    }

    public String getTotalPedido() {
        return totalPedido;
    }

    public void setTotalPedido(String totalPedido) {
        this.totalPedido = DataConf.parse(totalPedido);
    }

    public List<PedidoDet> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<PedidoDet> detalles) {
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
}
