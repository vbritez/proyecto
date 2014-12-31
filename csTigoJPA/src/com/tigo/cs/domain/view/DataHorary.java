package com.tigo.cs.domain.view;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.tigo.cs.commons.jpa.Order;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DATA_HORARY")
public class DataHorary extends Data implements Serializable {

    private static final long serialVersionUID = 8353437712942487655L;

    @Column(name = "DESCRIPCION", length = 255)
    @Searchable(label = "metadata.horary.field.description")
    @PrimarySortedField
    @Order(1)
    private String descripcion;

    @Column(name = "HABILITADO")
    @Searchable(label = "metadata.horary.field.enabled")
    @Order(2)
    private String habilitado;

    @Column(name = "VIGENCIA_DESDE")
    @Searchable(label = "metadata.horary.field.date.from")
    @Order(3)
    private String vigenciaDesde;

    @Column(name = "VIGENCIA_HASTA")
    @Searchable(label = "metadata.horary.field.date.to")
    @Order(4)
    private String vigenciaHasta;

    @Column(name = "TOLERANCIA_GENERAL", length = 255)
    @Searchable(label = "metadata.horary.field.tolerance")
    @Order(5)
    private String toleranciaGeneral;

    @Column(name = "TODOS_LOS_DIAS", length = 255)
    @Order(6)
    private String todosLosDias;

    @Column(name = "DIAS", length = 255)
    @Order(7)
    private String dias;

    @Column(name = "HORAS_ENTRADAS", length = 255)
    @Order(8)
    private String horasEntrada;

    @Column(name = "HORAS_TRABAJADAS", length = 255)
    @Order(9)
    private String horasTrabajada;

    @Column(name = "TOLERANCIAS", length = 255)
    @Order(10)
    private String tolerancias;

    @Column(name = "AJUSTAR_TOLERANCIA", length = 255)
    @Order(11)
    private String ajustarTolerancia;

    @Transient
    private Date dateFrom;
    @Transient
    private Date dateTo;
    @Transient
    private Boolean enabled;
    @Transient
    private Long tolerance = 0l;
    @Transient
    private Boolean setTolerance;
    @Transient
    private Boolean everyDay;

    public Boolean getEveryDay() {
        return everyDay;
    }

    public void setEveryDay(Boolean everyDay) {
        this.everyDay = everyDay;
    }

    public DataHorary() {
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(String habilitado) {
        this.habilitado = habilitado;
    }

    public String getVigenciaDesde() {
        return vigenciaDesde;
    }

    public void setVigenciaDesde(String vigenciaDesde) {
        this.vigenciaDesde = vigenciaDesde;
    }

    public String getVigenciaHasta() {
        return vigenciaHasta;
    }

    public void setVigenciaHasta(String vigenciaHasta) {
        this.vigenciaHasta = vigenciaHasta;
    }

    public String getToleranciaGeneral() {
        return toleranciaGeneral;
    }

    public void setToleranciaGeneral(String toleranciaGeneral) {
        this.toleranciaGeneral = toleranciaGeneral;
    }

    public String getTodosLosDias() {
        return todosLosDias;
    }

    public void setTodosLosDias(String todosLosDias) {
        this.todosLosDias = todosLosDias;
    }

    public String getDias() {
        return dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }

    public String getHorasEntrada() {
        return horasEntrada;
    }

    public void setHorasEntrada(String horasEntrada) {
        this.horasEntrada = horasEntrada;
    }

    public String getHorasTrabajada() {
        return horasTrabajada;
    }

    public void setHorasTrabajada(String horasTrabajada) {
        this.horasTrabajada = horasTrabajada;
    }

    public String getTolerancias() {
        return tolerancias;
    }

    public void setTolerancias(String tolerancias) {
        this.tolerancias = tolerancias;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getTolerance() {
        return tolerance;
    }

    public void setTolerance(Long tolerance) {
        this.tolerance = tolerance;
    }

    public String getAjustarTolerancia() {
        return ajustarTolerancia;
    }

    public void setAjustarTolerancia(String ajustarTolerancia) {
        this.ajustarTolerancia = ajustarTolerancia;
    }

    public Boolean getSetTolerance() {
        return setTolerance;
    }

    public void setSetTolerance(Boolean setTolerance) {
        this.setTolerance = setTolerance;
    }

    @Override
    public String toString() {
        return dias + " de " + horasEntrada + " a " + horasTrabajada;
    }

}