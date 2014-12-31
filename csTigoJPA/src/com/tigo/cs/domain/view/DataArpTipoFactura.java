package com.tigo.cs.domain.view;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.Order;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DATA_ARP_TIPO_FACTURA")
public class DataArpTipoFactura extends Data implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "TIPO_FACTURA", length = 255)
    @PrimarySortedField
    @Searchable(
            label = "web.client.metadataarptipofactura.screen.field.TipoFactura")
    @Order(1)
    private String tipoFactura;

    public DataArpTipoFactura() {
        dataPK = new DataPK();
    }

    public String getTipoFactura() {
        return this.tipoFactura;
    }

    public void setTipoFactura(String tipoFactura) {
        this.tipoFactura = tipoFactura;
    }

}