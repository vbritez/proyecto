package com.tigo.cs.ws.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.List;

/**
 * 
 * @author Miguel Zorrilla
 */
@XStreamAlias("flota")
public class FlotaCabData {

    long id;
    String fecha;
    String nroTelefono;
    String usuarioTelefono;
    String codigoChofer;
    String codigoVehiculo;
    String kilometrajeInicial;
    String kilometrajeFinal;
    String observacionDevolucion;
    String fechaHoraRetiro;
    String fechaHoraDevolucion;
    String duracionRecorrido;
    String latitudApertura;
    String longitudApertura;
    @XStreamImplicit(itemFieldName = "tramo")
    List<FlotaTramoData> tramos;

    public String getCodigoChofer() {
        return codigoChofer;
    }

    public void setCodigoChofer(String codigoChofer) {
        this.codigoChofer = DataConf.parse(codigoChofer);
    }

    public String getCodigoVehiculo() {
        return codigoVehiculo;
    }

    public void setCodigoVehiculo(String codigoVehiculo) {
        this.codigoVehiculo = DataConf.parse(codigoVehiculo);
    }

    public String getDuracionRecorrido() {
        return duracionRecorrido;
    }

    public void setDuracionRecorrido(String duracionRecorrido) {
        this.duracionRecorrido = DataConf.parse(duracionRecorrido);
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = DataConf.parse(fecha);
    }

    public String getFechaHoraDevolucion() {
        return fechaHoraDevolucion;
    }

    public void setFechaHoraDevolucion(String fechaHoraDevolucion) {
        this.fechaHoraDevolucion = DataConf.parse(DataConf.standarizeDateTime("dd/MM/yyyy HH:mm", fechaHoraDevolucion));
    }

    public String getFechaHoraRetiro() {
        return fechaHoraRetiro;
    }

    public void setFechaHoraRetiro(String fechaHoraRetiro) {
        this.fechaHoraRetiro = DataConf.parse(DataConf.standarizeDateTime("dd/MM/yyyy HH:mm", fechaHoraRetiro));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKilometrajeFinal() {
        return kilometrajeFinal;
    }

    public void setKilometrajeFinal(String kilometrajeFinal) {
        this.kilometrajeFinal = DataConf.parse(kilometrajeFinal);
    }

    public String getKilometrajeInicial() {
        return kilometrajeInicial;
    }

    public void setKilometrajeInicial(String kilometrajeInicial) {
        this.kilometrajeInicial = DataConf.parse(kilometrajeInicial);
    }

    public String getNroTelefono() {
        return nroTelefono;
    }

    public void setNroTelefono(String nroTelefono) {
        this.nroTelefono = DataConf.parse(nroTelefono);
    }

    public String getObservacionDevolucion() {
        return observacionDevolucion;
    }

    public void setObservacionDevolucion(String observacion) {
        this.observacionDevolucion = DataConf.parse(observacion);
    }

    public List<FlotaTramoData> getTramos() {
        return tramos;
    }

    public void setTramos(List<FlotaTramoData> tramos) {
        this.tramos = DataConf.parse(tramos);
    }

    public String getUsuarioTelefono() {
        return usuarioTelefono;
    }

    public void setUsuarioTelefono(String usuarioTelefono) {
        this.usuarioTelefono = DataConf.parse(usuarioTelefono);
    }

    public String getLatitudApertura() {
        return latitudApertura;
    }

    public void setLatitudApertura(String latitudApertura) {
        this.latitudApertura = DataConf.parse(latitudApertura);
    }

    public String getLongitudApertura() {
        return longitudApertura;
    }

    public void setLongitudApertura(String longitudApertura) {
        this.longitudApertura = DataConf.parse(longitudApertura);
    }
}
