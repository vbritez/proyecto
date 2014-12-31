package com.tigo.cs.domain.view;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.Order;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "DATA_ICON")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class DataIcon extends Data implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "URL", length = 255)
    @Order(1)
    private String url;

    @Column(name = "DESCRIPCION", length = 255)
    @PrimarySortedField
    @Searchable(label = "web.client.metadataicon.screen.field.description")
    @Order(2)
    private String descripcion;

    public DataIcon() {
        dataPK = new DataPK();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}