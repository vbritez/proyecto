package com.tigo.cs.ws.data;

/**
 * 
 * @author Miguel Zorrilla
 */
public class ArpDetData {

    String nroGuia;
    String cota;
    String monto;

    public String getCota() {
        return cota;
    }

    public void setCota(String cota) {
        this.cota = DataConf.parse(cota);
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = DataConf.parse(monto);
    }

    public String getNroGuia() {
        return nroGuia;
    }

    public void setNroGuia(String nroGuia) {
        this.nroGuia = DataConf.parse(nroGuia);
    }
}
