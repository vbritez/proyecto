package com.tigo.cs.api.enumerates;

public enum MetaNames {

    CLIENT(1L, "metadata.client"),
    PRODUCT(2L, "metadata.product"),
    MOTIVE(3L, "metadata.motive"),
    GUARD(4L, "metadata.guard"),
    DELIVERER(5L, "metadata.delivery"),
    ARP_INVOICE_TYPE(6L, "metadata.invoiceType"),
    EMPLOYEE(7L, "metadata.employee"),
    VECHICLE(8L, "metadata.vehicle"),
    BANK(9L, "metadata.bank"),
    DEPOSIT(10L, "metadata.deposit"),
    CLINIC(14L, "metadata.place"),
    MEDIC(15L, "metadata.medic"),
    CONTACTS(16L, "metadata.contact"),
    TICKET_CSI_USER(17L, "metadata.ticketCsiUser"),
    MANAGER(18L, "metadata.Manager"),
    OPERATOR(19L, "metadata.Operator"),
    MACHINE(20L, "metadata.Machine");
    private final Long value;
    private final String description;

    MetaNames(Long value, String description) {
        this.value = value;
        this.description = description;
    }

    public Long value() {
        return value;
    }

    public String description() {
        return description;
    }

    public static MetaNames findByValue(Integer val) {
        Long value = (long) val;
        MetaNames meta = null;
        for (MetaNames metaName : MetaNames.values()) {
            if (metaName.value.equals(value)) {
                meta = metaName;
                break;
            }
        }
        return meta;
    }
}
