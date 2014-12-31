package com.tigo.cs.ws.data;

/**
 * 
 * @author Miguel Zorrilla
 */
public class CobranzaCobrosData {

    long id;
    String tipoCobranza;
    String montoCobrado;
    String banco;
    String cheque;
    String fechaCheque;
    String fechaVencimientoCheque;
    String observacion;

    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = DataConf.parse(banco);
    }

    public String getCheque() {
        return cheque;
    }

    public void setCheque(String cheque) {
        this.cheque = DataConf.parse(cheque);
    }

    public String getFechaCheque() {
        return fechaCheque;
    }

    public void setFechaCheque(String fechaCheque) {
        this.fechaCheque = DataConf.parse(fechaCheque);
    }

    public String getFechaVencimientoCheque() {
        return fechaVencimientoCheque;
    }

    public void setFechaVencimientoCheque(String fechaVencimientoCheque) {
        this.fechaVencimientoCheque = DataConf.parse(fechaVencimientoCheque);
    }

    public String getMontoCobrado() {
        return montoCobrado;
    }

    public void setMontoCobrado(String montoCobrado) {
        this.montoCobrado = DataConf.parse(montoCobrado);
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = DataConf.parse(observacion);
    }

    public String getTipoCobranza() {
        return tipoCobranza;
    }

    public void setTipoCobranza(String tipoCobranza) {
        this.tipoCobranza = DataConf.parse(tipoCobranza);
    }
}
