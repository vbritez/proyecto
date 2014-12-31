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
@Table(name = "DATA_MOTIVE")
public class DataMotive extends Data implements Serializable {

    private static final long serialVersionUID = -2801710548429516995L;
    @Column(name = "MOTIVO", length = 255)
    @PrimarySortedField
    @Searchable(label = "web.client.metadatamotive.screen.field.Motive")
    @Order(1)
    private String motivo;

    public DataMotive() {
        dataPK = new DataPK();
    }

    public String getMotivo() {
        return this.motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

}