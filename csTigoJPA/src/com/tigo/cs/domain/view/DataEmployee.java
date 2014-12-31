package com.tigo.cs.domain.view;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.Order;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "DATA_EMPLOYEE")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(name = "DataEmployee.findAll",
        query = "SELECT d FROM DataEmployee d") })
public class DataEmployee extends Data implements Serializable {

    private static final long serialVersionUID = -2057224028889782323L;
    @Column(name = "NOMBRE", length = 255)
    @PrimarySortedField
    @Searchable(label = "web.client.metadataemployee.screen.field.Names")
    @Order(1)
    private String nombre;
    @Column(name = "CI", length = 255)
    @Searchable(label = "web.client.metadataemployee.screen.field.CI")
    @Order(2)
    private String ci;
    @Column(name = "AREA", length = 255)
    @Searchable(label = "web.client.metadataemployee.screen.field.Area")
    @Order(3)
    private String area;

    public DataEmployee() {
        dataPK = new DataPK();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataEmployee other = (DataEmployee) obj;
        if ((this.nombre == null) ? (other.nombre != null) : !this.nombre.equals(other.nombre)) {
            return false;
        }
        if ((this.ci == null) ? (other.ci != null) : !this.ci.equals(other.ci)) {
            return false;
        }
        if ((this.area == null) ? (other.area != null) : !this.area.equals(other.area)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.nombre != null ? this.nombre.hashCode() : 0);
        hash = 11 * hash + (this.ci != null ? this.ci.hashCode() : 0);
        hash = 11 * hash + (this.area != null ? this.area.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "DataEmployee[DataPK=" + dataPK + "]";
    }
}
