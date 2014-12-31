package com.tigo.cs.api.service;

import java.text.MessageFormat;
import java.util.List;

import com.tigo.cs.api.entities.OtService;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.view.Ot;

public abstract class OtServiceAPI<T extends OtService> extends AbstractServiceAPI<OtService> {

    public static final String SC_OT = "SC-OT";
    public static final String ACTIVIDAD_ACTUAL = "ACTIVIDAD_ACTUAL";
    public static final String CONSULTAS = "CONSULTAS";
    public static final String OT_INICIO = "OT-INICIO";
    public static final String OT_SUSPENDER = "OT-SUSPENDER";
    public static final String OT_POSTERGAR = "OT-POSTERGAR";
    public static final String OT_TERMINAR = "OT-TERMINAR";
    public static final String OT_CONSULTA_ACTUAL = "OT-CONSULTA-ACTUAL";
    public static final String OT_CONSULTA_DIA = "OT-CONSULTA-DIA";
    public static final String OBSERVACION = "OBSERVACION";

    private OTEvent tranType;

    /**
     * Defines a set transaction performed by the userphone in the Visit-order
     * service. Allows the specialized treatment of each operation type and
     * resulting message for userphone response.
     */
    protected enum OTEvent implements ServiceEvent {

        ACTIVITY_START(1, new ServiceProps("ot.event.Start", "ot.message.Start", "")),
        ACTIVITY_CANCEL(2, new ServiceProps("ot.event.Suspend", "ot.message.Cancel", "")),
        ACTIVITY_POSTPONE(3, new ServiceProps("ot.event.Postpone", "ot.message.Postpone", "")),
        ACTIVITY_FINALIZE(4, new ServiceProps("ot.event.Finalize", "ot.message.Finalize", "")),
        LIST_ACTUAL_ACTIVITY(5, new ServiceProps("ot.event.ActualActivity", "ot.message.ActualActivity", "")),
        LIST_TODAY_ACTIVITIES(6, new ServiceProps("ot.event.TodayActivities", "ot.message.TodayActivities", ""));
        protected final int value;
        protected final ServiceProps props;

        private OTEvent(int value, ServiceProps props) {
            this.value = value;
            this.props = props;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getSuccessMessage() {
            return props.getSuccesMessage();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getNoMatchMessage() {
            return props.getNoMatchMessage();
        }

        @Override
        public String getDescription() {
            return props.getDescription();
        }
    }

    public enum OTStatus {
        NUEVO(0, "Nuevo"),
        ASIGNADO(1, "Asignado"),
        INICIADO(2, "Iniciado"),
        POSTERGADO(3, "Postergado"),
        CANCELADO(4, "Cancelado"),
        FINALIZADO(5, "Finalizado");

        protected final Integer value;
        protected final String status;

        private OTStatus(Integer value, String status) {
            this.value = value;
            this.status = status;
        }

        public Integer getValue() {
            return value;
        }

        public String getStatus() {
            return status;
        }
    }

    @Override
    public OtService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new OtService());
        }
        return super.getEntity();
    }

    @Override
    public OtService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new OtService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {

        switch (tranType) {
        case ACTIVITY_START:
            Ot ot = getFacadeContainer().getOtAPI().getActualActivity(
                    getUserphone().getUserphoneCod());
            ot = changeOTStatus(ot, OTStatus.INICIADO);
            returnMessage = MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue("ot.message.Start"),
                    ot.getActivityDescription());
            break;

        case ACTIVITY_CANCEL:
            ot = changeOTStatus(
                    getFacadeContainer().getOtAPI().getActualActivity(
                            getUserphone().getUserphoneCod()),
                    OTStatus.CANCELADO);
            returnMessage = MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "ot.message.Cancel"), ot.getActivityDescription());
            break;

        case ACTIVITY_POSTPONE:
            ot = changeOTStatus(
                    getFacadeContainer().getOtAPI().getActualActivity(
                            getUserphone().getUserphoneCod()),
                    OTStatus.POSTERGADO);
            returnMessage = MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "ot.message.Postpone"), ot.getActivityDescription());
            break;

        case ACTIVITY_FINALIZE:
            ot = changeOTStatus(
                    getFacadeContainer().getOtAPI().getActualActivity(
                            getUserphone().getUserphoneCod()),
                    OTStatus.FINALIZADO);
            returnMessage = MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "ot.message.Finalize"), ot.getActivityDescription());
            break;

        case LIST_ACTUAL_ACTIVITY:
            ot = getFacadeContainer().getOtAPI().getActualActivity(
                    getUserphone().getUserphoneCod());
            // if (ot == null) {
            // returnMessage =
            // getFacadeContainer().getI18nAPI().iValue("ot.message.NoPendingActivities");
            // } else {
            String client = ot.getClientDescription() != null ? ot.getClientCod()
                + "," + ot.getClientDescription() : ot.getClientCod();
            returnMessage = MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "ot.message.ActualActivity"),
                    ot.getActivityDescription(), ot.getAssignedDate(), client);
            // }
            break;

        case LIST_TODAY_ACTIVITIES:
            String msg = "";
            client = "";
            List<Ot> ots = getFacadeContainer().getOtAPI().nextAsignedOTs(
                    getUserphone().getUserphoneCod());
            for (Ot nextOt : ots) {
                client = nextOt.getClientDescription() != null ? nextOt.getClientCod()
                    + "," + nextOt.getClientDescription() : nextOt.getClientCod();
                msg += "-"
                    + MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "ot.message.TodayActivities.nexo"),
                            nextOt.getActivityDescription(), client);
            }
            if (msg.isEmpty()) {
                returnMessage = getFacadeContainer().getI18nAPI().iValue(
                        "ot.message.NoPendingActivities");
            } else {
                returnMessage = MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(
                                "ot.message.TodayActivities"), msg);
            }
            break;
        default:
            break;
        }

        return returnMessage;
    }

    @Override
    protected void validate() throws InvalidOperationException {
        super.validate();

        /* se verifica que el userphone tenga una ot asignada o iniciada */
        Ot ot = getFacadeContainer().getOtAPI().getActualActivity(
                getUserphone().getUserphoneCod());
        if (ot == null) {
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "ot.message.NoPendingActivities"));
        }

        /* si la actividad actual ya esta iniciada, no se puede volver a iniciar */
        if (tranType.equals(OTEvent.ACTIVITY_START)
            && ot.getStatusCod().equals(OTStatus.INICIADO.getValue().toString())) {
            throw new InvalidOperationException(MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "ot.message.InvalidOperation"),
                    getFacadeContainer().getI18nAPI().iValue(
                            tranType.getDescription()),
                    OTStatus.INICIADO.getStatus()));
        }

        /* si se quiere finalizar una OT, se tuvo que haber iniciado antes */
        if (tranType.equals(OTEvent.ACTIVITY_FINALIZE)
            && !ot.getStatusCod().equals(
                    OTStatus.INICIADO.getValue().toString())) {
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "ot.message.InitBeforeFinalizing"));
        }
    }

    public Ot changeOTStatus(Ot ot, OTStatus status) {
        ServiceValue sv = null;

        switch (status) {
        case INICIADO:
        case POSTERGADO:
        case CANCELADO:
        case FINALIZADO:
            sv = getFacadeContainer().getServiceValueAPI().find(
                    ot.getServicevalueCod());
            sv.setColumn7Chr(status.getValue().toString());
            getFacadeContainer().getServiceValueAPI().edit(sv);

            ServiceValueDetail svd = new ServiceValueDetail();
            svd.setServiceValue(sv);
            svd.setColumn1Chr(status.getValue().toString());
            svd.setColumn2Chr(getEntity().getObservation());
            svd.setMessage(getMessage());
            svd.setRecorddateDat(validateDate());
            getFacadeContainer().getServiceValueDetailAPI().create(svd);
            return getFacadeContainer().getOtAPI().find(ot.getServicevalueCod());

        default:
            sv = getFacadeContainer().getServiceValueAPI().find(
                    ot.getServicevalueCod());
            sv.setColumn7Chr(status.getValue().toString());
            getFacadeContainer().getServiceValueAPI().edit(sv);

            svd = new ServiceValueDetail();
            svd.setServiceValue(sv);
            svd.setColumn1Chr(status.getValue().toString());
            svd.setMessage(getMessage());
            svd.setRecorddateDat(validateDate());
            getFacadeContainer().getServiceValueDetailAPI().create(svd);
            return getFacadeContainer().getOtAPI().find(sv.getServicevalueCod());
        }
    }

    @Override
    protected void assignServiceEvent() {
        String discriminator = getEntity().getEvent();
        if (discriminator.equals(OT_INICIO)) {
            tranType = OTEvent.ACTIVITY_START;

        } else if (discriminator.equals(OT_SUSPENDER)) {
            tranType = OTEvent.ACTIVITY_CANCEL;

        } else if (discriminator.equals(OT_POSTERGAR)) {
            tranType = OTEvent.ACTIVITY_POSTPONE;

        } else if (discriminator.equals(OT_TERMINAR)) {
            tranType = OTEvent.ACTIVITY_FINALIZE;

        } else if (discriminator.equals(OT_CONSULTA_ACTUAL)) {
            tranType = OTEvent.LIST_ACTUAL_ACTIVITY;

        } else {
            tranType = OTEvent.LIST_TODAY_ACTIVITIES;
        }
    }

    @Override
    protected ServiceValue treatHeader() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {
        // TODO Auto-generated method stub
        return null;
    }

}
