package com.tigo.cs.ws.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Miguel Zorrilla
 */
@XStreamAlias("visita")
public class VisitaVPData {

    long id;
    String nroTelefono;
    String usuarioTelefono;
    String fechaHoraEntrada;
    String fechaHoraSalida;
    String cliente;
    String motivo;
    String observacionEntrada;
    String observacionSalida;
    String duracion;
    String latitudEntrada;
    String longitudEntrada;
    String latitudSalida;
    String longitudSalida;
    @XStreamImplicit(itemFieldName = "pedido")
    List<PedidoCabVP> pedidos;

    public String getCliente() {
        return cliente;
    }

    public String getDuracion() {
        return duracion;
    }

    public long getId() {
        return id;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getFechaHoraEntrada() {
        return fechaHoraEntrada;
    }

    public String getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public String getObservacionEntrada() {
        return observacionEntrada;
    }

    public String getObservacionSalida() {
        return observacionSalida;
    }

    public void setCliente(String cliente) {
        this.cliente = DataConf.parse(cliente);
    }

    public void setDuracion(String duracion) {
        this.duracion = DataConf.parse(duracion);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setMotivo(String motivo) {
        this.motivo = DataConf.parse(motivo);
    }

    public void setFechaHoraEntrada(String fechaHoraEntrada) {
        this.fechaHoraEntrada = DataConf.parse(fechaHoraEntrada);
    }

    public void setFechaHoraSalida(String fechaHoraEntrada) {
        this.fechaHoraSalida = DataConf.parse(fechaHoraEntrada);
    }

    public void setFechaHoraEntrada(Date fechaHoraEntrada) {
        this.fechaHoraEntrada = DataConf.parse(DataConf.getDatetimeAsString(fechaHoraEntrada));
    }

    public void setFechaHoraSalida(Date fechaHoraSalida) {
        this.fechaHoraSalida = DataConf.parse(DataConf.getDatetimeAsString(fechaHoraSalida));
    }

    public void setObservacionEntrada(String observacionEntrada) {
        this.observacionEntrada = DataConf.parse(observacionEntrada);
    }

    public void setObservacionSalida(String observacionSalida) {
        this.observacionSalida = DataConf.parse(observacionSalida);
    }

    public List<PedidoCabVP> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<PedidoCabVP> pedidos) {
        this.pedidos = DataConf.parse(pedidos);
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

    public String getLatitudEntrada() {
        return latitudEntrada;
    }

    public void setLatitudEntrada(String latitudEntrada) {
        this.latitudEntrada = DataConf.parse(latitudEntrada);
    }

    public String getLatitudSalida() {
        return latitudSalida;
    }

    public void setLatitudSalida(String latitudSalida) {
        this.latitudSalida = DataConf.parse(latitudSalida);
    }

    public String getLongitudEntrada() {
        return longitudEntrada;
    }

    public void setLongitudEntrada(String longitudEntrada) {
        this.longitudEntrada = DataConf.parse(longitudEntrada);
    }

    public String getLongitudSalida() {
        return longitudSalida;
    }

    public void setLongitudSalida(String longitudSalida) {
        this.longitudSalida = DataConf.parse(longitudSalida);
    }
}
