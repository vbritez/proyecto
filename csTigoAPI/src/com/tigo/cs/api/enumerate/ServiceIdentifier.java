package com.tigo.cs.api.enumerate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServiceIdentifier {

    public enum Id {

        PERMISSION(0L, true),
        VISITA(1L, false),
        PEDIDO(2L, false),
        VISITA_PEDIDO(3L, false),
        RASTREO(4L, false),
        COBRANZA(5L, false),
        GUARDIA(6L, false),
        DELIVERY(7L, false),
        INVENTARIO_GUARAPI(8L, false),
        ARP(9L, false),
        INVENTARIO_STANDARD(10L, false),
        ASISTENCIA(11L, false),
        FLOTA(12L, false),
        INFORMCONF(13L, false),
        AGENDAMIENTO(14L, false),
        GUARDIA_TURNO(15L, false),
        OT(16L, false),
        VISITA_MEDICA(17L, false),
        COURRIER(18L, false),
        TICKET_CSI(19L, false),
        FORMULARIO_DINAMICO(20L, false),
        TERPORT(24L, false),
        SUPERVISOR(25L, false),
        INTERFISA(-2L, false),
        DATOS(99L, false),
        PRESTACIONES(100L, true),
        OPERACIONES_SERVICIO(101L, true),
        TIGO_MONEY(-3L, false);

        /*
         * El campo value es el codigo del servicio que tiene que ser igual al
         * que esta en la tabla Service.Y el campo validation es hace referencia
         * si el servicio debe estar necesariamente ligado a un userphone
         */
        private final Long value;
        private final Boolean validation;

        private Id(Long value, Boolean validation) {
            this.value = value;
            this.validation = validation;
        }

        public Long value() {
            return value;
        }

        public Boolean getValidation() {
            return validation;
        }
    };

    Id id();
}
