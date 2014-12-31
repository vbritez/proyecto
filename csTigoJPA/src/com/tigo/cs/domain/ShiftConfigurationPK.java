package com.tigo.cs.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;

@Embeddable
public class ShiftConfigurationPK implements Serializable {

    private static final long serialVersionUID = 5997065598820174174L;

    @SequenceGenerator(name = "shiftconfigurationGen",
            sequenceName = "SHIFT_CONFIGURATION_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "shiftconfigurationGen")
    @Column(name = "SHIFT_CONFIGURATION_COD", unique = true, nullable = false,
            precision = 19)
    private Long shiftConfigurationCod;

    @Column(name = "COD_SHIFT_PERIOD", unique = true, nullable = false,
            precision = 19)
    private Long codShiftPeriod;

    public ShiftConfigurationPK() {
    }

    public Long getShiftConfigurationCod() {
        return this.shiftConfigurationCod;
    }

    public void setShiftConfigurationCod(Long shiftConfigurationCod) {
        this.shiftConfigurationCod = shiftConfigurationCod;
    }

    public Long getCodShiftPeriod() {
        return this.codShiftPeriod;
    }

    public void setCodShiftPeriod(Long codShiftPeriod) {
        this.codShiftPeriod = codShiftPeriod;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ShiftConfigurationPK)) {
            return false;
        }
        ShiftConfigurationPK castOther = (ShiftConfigurationPK) other;
        return (this.shiftConfigurationCod.longValue() == castOther.shiftConfigurationCod.longValue())
            && (this.codShiftPeriod.longValue() == castOther.codShiftPeriod.longValue());

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash
            * prime
            + ((int) (this.shiftConfigurationCod ^ (this.shiftConfigurationCod >>> 32)));
        hash = hash * prime
            + ((int) (this.codShiftPeriod ^ (this.codShiftPeriod >>> 32)));

        return hash;
    }

    @Override
    public String toString() {
        return this.codShiftPeriod.toString() + ", "
            + this.shiftConfigurationCod.toString();
    }
}