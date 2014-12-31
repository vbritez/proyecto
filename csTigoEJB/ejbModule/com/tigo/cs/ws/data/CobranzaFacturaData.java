package com.tigo.cs.ws.data;

/**
 * 
 * @author Miguel Zorrilla
 */
public class CobranzaFacturaData {

    long id;
    String nroFactura;
    String monto;
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
