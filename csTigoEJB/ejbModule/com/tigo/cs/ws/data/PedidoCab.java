package com.tigo.cs.ws.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Miguel Zorrilla
 */
@XStreamAlias("pedido")
public class PedidoCab {
	long id;
	String fechaHora;
	String nroTelefono;
	String usuarioTelefono;
	String cliente;
	String tipoFactura;
	String listaPrecio;
	String totalPedido;
	String marcado;
	String latitud;
	String longitud;
	String observacion;
	String estado;
	@XStreamImplicit(itemFieldName = "detalle")
	List<PedidoDet> detalles;

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = DataConf.parse(cliente);
	}

	public String getFechaHora() {
		return fechaHora;
	}
	
	public void setFechaHora(String fechaHora) {
		this.fechaHora = DataConf.parse(fechaHora);
	}

	public void setFechaHora(Date fechaHora) {
		this.fechaHora = DataConf
				.parse(DataConf.getDatetimeAsString(fechaHora));
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getListaPrecio() {
		return listaPrecio;
	}

	public void setListaPrecio(String listaPrecio) {
		this.listaPrecio = DataConf.parse(listaPrecio);
	}

	public String getMarcado() {
		return marcado;
	}

	public void setMarcado(String marcado) {
		this.marcado = DataConf.parse(marcado);
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

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = DataConf.parse(observacion);
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = DataConf.parse(estado);
	}

}
