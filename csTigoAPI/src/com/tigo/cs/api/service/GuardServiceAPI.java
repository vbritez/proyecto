package com.tigo.cs.api.service;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.tigo.cs.api.entities.GuardService;
import com.tigo.cs.api.enumerate.Restriction;
import com.tigo.cs.api.enumerates.MetaNames;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class GuardServiceAPI<T extends GuardService> extends AbstractServiceAPI<GuardService> {

    protected GuardEvent tranType;
    private ServiceValue currentServiceValue;

    protected enum GuardEvent implements ServiceEvent {

        IN(1, new ServiceProps("guard.name.In", "guard.message.In", "")),
        OUT(2, new ServiceProps("guard.name.Out", "guard.message.Out", "")),
        REGISTRATION(3, new ServiceProps("guard.name.Registration", "guard.message.Registration", ""));
        protected final int value;
        protected final ServiceProps props;

        private GuardEvent(int value, ServiceProps props) {
            this.value = value;
            this.props = props;
        }

        @Override
        public String getSuccessMessage() {
            return props.getSuccesMessage();
        }

        @Override
        public String getNoMatchMessage() {
            return props.getNoMatchMessage();
        }

        @Override
        public String getDescription() {
            return props.getDescription();
        }
    }

    public GuardServiceAPI() {
    }

    @Override
    public GuardService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new GuardService());
            currentServiceValue = null;
        }
        return super.getEntity();
    }

    @Override
    public GuardService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new GuardService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {

        Long ref = null;
        switch (tranType) {
        case OUT:
        case IN:
            currentServiceValue = treatHeader();
            getEntity().setGuardCode(currentServiceValue.getColumn1Chr());
            ref = currentServiceValue.getServicevalueCod();
            break;
        case REGISTRATION:
            List<ServiceValueDetail> details = treatDetails(currentServiceValue);
            returnMessage = tranType.getSuccessMessage();
            ref = details != null && details.size() > 0 ? details.get(0).getServicevaluedetailCod() : null;
            getEntity().setGuardCode(
                    details.get(0).getServiceValue().getColumn1Chr());
            break;

        }

        String guard = getMetaForIntegrationValue(getEntity().getGuardCode(),
                MetaNames.GUARD);

        returnMessage = getFacadeContainer().getI18nAPI().iValue(
                tranType.getSuccessMessage());
        returnMessage = MessageFormat.format(returnMessage, guard, ref);

        addRouteDetail(getUserphone(), getCell());
        return returnMessage;
    }

    @Override
    protected void validate() throws InvalidOperationException {
        super.validate();
        try {
            currentServiceValue = getFacadeContainer().getServiceValueAPI().getCurrentGuardServiceValue(
                    getService(), getUserphone());
        } catch (Exception ex) {
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "guard.ValidtionOperationErrorDueInternalError"));
        }

        switch (tranType) {
        case IN:
            if (currentServiceValue != null) {
                // Se encontró ronda Abierta, NO se puede abrir.
                throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                        "guard.OperationNotAllowedGuardTourOpened"));
            }
            break;
        case OUT:
            if (currentServiceValue == null) {
                // NO existe una marcación Abierta, NO se puede cerrar.
                throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                        "guard.OperationNotAllowedGuardTourDoesntExist"));
            }
            break;
        case REGISTRATION:
            if (currentServiceValue == null) {
                // NO existe una marcacion Abierta, se puede registrar
                // marcaciones.
                throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                        "guard.OperationNotAllowedGuardTourMustBeOpened"));
            }

            /**
             * Valida si el guardia ya ha marcado para la hora.
             * 
             * Al intentar registrar la marcación se da los siguientes casos: a)
             * Si no ha marcado aún y se encuentra en el rango de tiempo
             * permitido para la marcación: entonces se procede con el registro
             * del mensaje.
             * 
             * b) Si no ha marcado aún y NO se encuentra en el rango de tiempo
             * permitido para la marcación: el sistema registró una marcación
             * fuera de tiempo, al momento de corroborar la falta de marcación;
             * por lo que este caso no se puede dar para el guardia (mismo caso
             * que d). Si no marco a tiempo, ya existe un registro acotando esta
             * infracción. El sistema lanza OperationNotAllowedException para
             * indicar el error de procedimiento.
             * 
             * c) Si ya ha marcado satisfactoriamente y se encuentra o no en el
             * rango de tiempo permitido para la marcación: es decir, intenta
             * volver a marcar para la hora, entonces se lanza
             * OperationNotAllowedException con un mensaje que explique el error
             * de procedimiento.
             * 
             * d) Si el sistema ya ha marcado infracción y NO se encuentra en el
             * rango de tiempo permitido para la marcación: es decir, intenta
             * volver a marcar para la hora, entonces se lanza
             * OperationNotAllowedException con un mensaje que explique el error
             * de procedimiento.
             * 
             * Si ya ha marcado y NO se encuentra en el rango de tiempo
             * permitido para la marcación
             */

            int r = 0;
            try {
                r = getFacadeContainer().getServiceValueDetailAPI().validateMarking(
                        currentServiceValue.getServicevalueCod(),
                        getMessage().getDateinDat());
            } catch (Exception e) {
                throw new OperationNotAllowedException(getFacadeContainer().getI18nAPI().iValue(
                        Restriction.GENERIC.getMessage()));
            }
            switch (r) {
            case 0:
                return;
            case 1:
                throw new OperationNotAllowedException(getFacadeContainer().getI18nAPI().iValue(
                        Restriction.GUARD_RE_MARKING_NOT_ALLOWEB.getMessage()));
            case 2:
                throw new OperationNotAllowedException(getFacadeContainer().getI18nAPI().iValue(
                        Restriction.GUARD_UNTIMELY_MARKING.getMessage()));
            }

            break;
        }

    }

    @Override
    protected ServiceValue treatHeader() {
        try {
            if (currentServiceValue == null) {
                currentServiceValue = new ServiceValue();
                currentServiceValue.setService(getService());
                currentServiceValue.setUserphone(getUserphone());
                currentServiceValue.setMessage(getMessage());
                currentServiceValue.setRecorddateDat(validateDate());
                currentServiceValue.setColumn1Chr(getEntity().getGuardCode());
                String datetimePattern = getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "format.datetime.input.default");
                if (datetimePattern != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat(datetimePattern);
                    currentServiceValue.setColumn2Chr(sdf.format(getMessage().getDateinDat()));
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    currentServiceValue.setColumn2Chr(sdf.format(getMessage().getDateinDat()));
                }
                currentServiceValue.setColumn3Chr(getEntity().getObservation());
                currentServiceValue.setColumn4Chr(getEntity().getPlace());
                currentServiceValue = getFacadeContainer().getServiceValueAPI().create(
                        currentServiceValue);

                // CUANDO se crea esta cabecera, también se crea el primer
                // registro de detalle
                treatDetails(currentServiceValue);
            } else {
                String datetimePattern = getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "format.datetime.input.default");
                if (datetimePattern != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat(datetimePattern);
                    currentServiceValue.setColumn5Chr(sdf.format(getMessage().getDateinDat()));
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    currentServiceValue.setColumn5Chr(sdf.format(getMessage().getDateinDat()));
                }
                currentServiceValue.setColumn6Chr(getEntity().getObservation());
                currentServiceValue.setColumn10Chr(getMessage().getMessageCod().toString());
                currentServiceValue = getFacadeContainer().getServiceValueAPI().edit(
                        currentServiceValue);
            }
            return currentServiceValue;
        } catch (Exception e) {
            throw new RuntimeException(getFacadeContainer().getI18nAPI().iValue(
                    "guard.CouldNotCreateCabDueInternalError"), e);
        }
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {
        List<ServiceValueDetail> svdList = new ArrayList<ServiceValueDetail>();
        ServiceValueDetail serviceValueDetail = new ServiceValueDetail();
        serviceValueDetail.setMessage(getMessage());
        serviceValueDetail.setRecorddateDat(validateDate());
        serviceValueDetail.setServiceValue(serviceValue);
        serviceValueDetail.setColumn1Chr(getEntity().getObservation());// observación
        svdList.add(getFacadeContainer().getServiceValueDetailAPI().create(
                serviceValueDetail));

        return svdList;
    }

    @Override
    protected void assignServiceEvent() {
        String discriminator = getEntity().getEvent();
        if (discriminator.equals("ENT")) {
            tranType = GuardEvent.IN;
        } else if (discriminator.equals("SAL")) {
            tranType = GuardEvent.OUT;
        } else {
            tranType = GuardEvent.REGISTRATION;
        }
    }

}
