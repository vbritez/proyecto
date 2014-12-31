package com.tigo.cs.api.service;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tigo.cs.api.entities.ScheduledService;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class ScheduledServiceAPI<T extends ScheduledService> extends AbstractServiceAPI<ScheduledService> {

    private static final String AGENDAMIENTO_NOTIFICACION_DIA_ANTES = "AGENDAMIENTO-NOTIFICACION-DIA-ANTES";
    private static final String AGENDAMIENTO_NOTIFICACION_TRES_HORAS_ANTES = "AGENDAMIENTO-NOTIFICACION-TRES-HORAS-ANTES";
    private static final String AGENDAMIENTO_NOTIFICACION_HORA_ANTES = "AGENDAMIENTO-NOTIFICACION-HORA-ANTES";
    String fechaEvento;
    String horaEvento;
    String tipoNotificacion;
    String column1ChrFechaHora;
    private ScheduledEvent tranType;
    String dateHourPattern;

    protected static enum ScheduledEvent implements ServiceEvent {

        SCHEDULE_REGISTRATION(1, new ServiceProps("schedule.name.Register", "schedule.message.Register", ""));
        protected final int value;
        protected final ServiceProps props;

        private ScheduledEvent(int value, ServiceProps props) {
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

    public ScheduledServiceAPI() {
    }

    @Override
    public ScheduledService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new ScheduledService());
        }
        return super.getEntity();
    }

    @Override
    public ScheduledService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new ScheduledService());
        }
        return super.getReturnEntity();
    }

    @Override
    protected ServiceValue treatHeader() {

        Calendar calendar = Calendar.getInstance();
        String column2ChrFechaNotificacion = null;
        try {
            column1ChrFechaHora = getEntity().getDate().concat(" ").concat(
                    getEntity().getTime());

            DateFormat df = new SimpleDateFormat(dateHourPattern);
            Date fechaHoraEvento = df.parse(column1ChrFechaHora);
            calendar.setTime(fechaHoraEvento);

            if (getEntity().getNotificationType().equals(
                    AGENDAMIENTO_NOTIFICACION_DIA_ANTES)) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
            } else if (getEntity().getNotificationType().equals(
                    AGENDAMIENTO_NOTIFICACION_HORA_ANTES)) {
                calendar.add(Calendar.HOUR_OF_DAY, -1);
            } else if (getEntity().getNotificationType().equals(
                    AGENDAMIENTO_NOTIFICACION_TRES_HORAS_ANTES)) {
                calendar.add(Calendar.HOUR_OF_DAY, -3);
            }

            column2ChrFechaNotificacion = df.format(calendar.getTime());

        } catch (ParseException ex) {
            Logger.getLogger(ScheduledServiceAPI.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        ServiceValue serviceValue = new ServiceValue();
        serviceValue.setService(getService());
        serviceValue.setUserphone(getUserphone());
        serviceValue.setMessage(getMessage());
        serviceValue.setRecorddateDat(validateDate());
        serviceValue.setColumn1Chr(column1ChrFechaHora);
        serviceValue.setColumn2Chr(column2ChrFechaNotificacion);
        serviceValue.setColumn3Chr(getEntity().getDescription());
        return getFacadeContainer().getServiceValueAPI().create(serviceValue);
    }

    /*
     * no es necesario implementarlo para este servicio
     */
    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void assignServiceEvent() {
        tranType = ScheduledEvent.SCHEDULE_REGISTRATION;
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {

        dateHourPattern = getFacadeContainer().getGlobalParameterAPI().findByCode(
                "services.format.datetime");

        returnMessage = getFacadeContainer().getI18nAPI().iValue(
                tranType.getSuccessMessage());
        if (validateOperation()) {
            ServiceValue serviceValue = treatHeader();
            returnMessage = MessageFormat.format(returnMessage,
                    serviceValue.getColumn2Chr());
        }
        updateMessage(returnMessage);

        return returnMessage;
    }

    private Boolean validateOperation() throws OperationNotAllowedException {
        try {
            String fechaHora = getEntity().getDate().concat(" ").concat(
                    getEntity().getTime());
            DateFormat df = new SimpleDateFormat(dateHourPattern);
            df.parse(fechaHora);
        } catch (ParseException ex) {
            throw new OperationNotAllowedException(getFacadeContainer().getI18nAPI().iValue(
                    "schedule.message.DateHourPatternMismatch"));
        }
        return true;
    }
}
