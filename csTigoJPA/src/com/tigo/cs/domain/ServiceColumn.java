package com.tigo.cs.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "service_column")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(
        name = "ServiceColumn.getColumnData",
        query = "SELECT s FROM ServiceColumn s WHERE s.service.serviceCod = :serviceCod AND s.tableChr = :tableChr") })
public class ServiceColumn implements Serializable {

    private static final long serialVersionUID = -6935427632351609616L;
    @Id
    @SequenceGenerator(name = "serviceColumnGen",
            sequenceName = "service_column_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "serviceColumnGen")
    @Basic(optional = false)
    @Column(name = "service_column_cod")
    private Long serviceColumnCod;
    @JoinColumn(name = "cod_service", referencedColumnName = "service_cod")
    @ManyToOne(optional = false)
    private Service service;
    @NotEmpty(message = "{entity.serviceColumn.constraint.column.NotEmpty}")
    @Column(name = "column_chr")
    private String columnChr;
    @NotEmpty(message = "{entity.serviceColumn.constraint.dataType.NotEmpty}")
    @Column(name = "datatype_chr")
    private String datatypeChr;
    @NotEmpty(message = "{entity.serviceColumn.constraint.table.NotEmpty}")
    @Column(name = "table_chr")
    private String tableChr;
    @NotEmpty(
            message = "{entity.serviceColumn.constraint.description.NotEmpty}")
    @Column(name = "description_chr")
    private String descriptionChr;

    public ServiceColumn() {
    }

    public ServiceColumn(Long serviceColumnCod) {
        this.serviceColumnCod = serviceColumnCod;
    }

    public ServiceColumn(Long serviceColumnCod, String columnChr,
            String datatypeChr, String tableChr, String descriptionChr) {
        this.serviceColumnCod = serviceColumnCod;
        this.columnChr = columnChr;
        this.datatypeChr = datatypeChr;
        this.tableChr = tableChr;
        this.descriptionChr = descriptionChr;
    }

    public Long getServiceColumnCod() {
        return serviceColumnCod;
    }

    public void setServiceColumnCod(Long serviceColumnCod) {
        this.serviceColumnCod = serviceColumnCod;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getColumnChr() {
        return columnChr;
    }

    public void setColumnChr(String columnChr) {
        this.columnChr = columnChr;
    }

    public String getDatatypeChr() {
        return datatypeChr;
    }

    public void setDatatypeChr(String datatypeChr) {
        this.datatypeChr = datatypeChr;
    }

    public String getTableChr() {
        return tableChr;
    }

    public void setTableChr(String tableChr) {
        this.tableChr = tableChr;
    }

    public String getDescriptionChr() {
        return descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ServiceColumn other = (ServiceColumn) obj;
        if (this.serviceColumnCod != other.serviceColumnCod
            && (this.serviceColumnCod == null || !this.serviceColumnCod.equals(other.serviceColumnCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37
            * hash
            + (this.serviceColumnCod != null ? this.serviceColumnCod.hashCode() : 0);
        hash = 37 * hash + (this.service != null ? this.service.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return descriptionChr.concat(" (Cod.: ").concat(
                serviceColumnCod.toString()).concat(")");
    }
}
