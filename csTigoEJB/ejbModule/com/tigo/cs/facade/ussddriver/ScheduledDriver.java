package com.tigo.cs.facade.ussddriver;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.entities.ScheduledService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.ScheduledServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.AGENDAMIENTO)
public class ScheduledDriver extends ScheduledServiceAPI<ScheduledService> implements DriverInterface {

    @EJB
    private FacadeContainer facadeContainer;

    private static final String AGENDAMIENTO_FECHA_EVENTO = "AGENDAMIENTO-FECHA-EVENTO";
    private static final String AGENDAMIENTO_HORA_EVENTO = "AGENDAMIENTO-HORA-EVENTO";
    private static final String AGENDAMIENTO_DESCRIPCION = "AGENDAMIENTO-DESCRIPCION";
    private static final String AGENDAMIENTO_FECHA_NOTIFICACION = "AGENDAMIENTO-FECHA-NOTIFICACION";

    @Override
    protected void assignEvent() {
    }

    @Override
    public void convertToBean() {
        getEntity().setDate(getNotNullNodeValueFrom(AGENDAMIENTO_FECHA_EVENTO));
        getEntity().setTime(getNotNullNodeValueFrom(AGENDAMIENTO_HORA_EVENTO));
        getEntity().setDescription(
                getNotNullNodeValueFrom(AGENDAMIENTO_DESCRIPCION));
        getEntity().setNotificationType(
                getNotNullNodeValueFrom(AGENDAMIENTO_FECHA_NOTIFICACION));
    }

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }
}
