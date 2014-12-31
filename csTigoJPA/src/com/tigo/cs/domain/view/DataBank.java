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
@Table(name = "DATA_BANK")
public class DataBank extends Data implements Serializable {

    private static final long serialVersionUID = 7650034622150864194L;
    @Column(name = "BANCO", length = 255)
    @Searchable(label = "web.client.metadatabank.screen.field.NombreBanco")
    @PrimarySortedField
    @Order(1)
    private String banco;

    public DataBank() {
        dataPK = new DataPK();
    }

    public String getBanco() {
        return this.banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

}