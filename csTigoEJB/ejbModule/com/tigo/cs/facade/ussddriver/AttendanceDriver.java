package com.tigo.cs.facade.ussddriver;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.entities.AttendanceService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.AttendanceServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.ASISTENCIA)
public class AttendanceDriver extends AttendanceServiceAPI<AttendanceService> implements DriverInterface {

    private static final String ASISTENCIA_CODEMPLEADO = "ASISTENCIA-CODEMPLEADO";
    private static final String ASISTENCIA_EVENTOS = "ASISTENCIA-EVENTOS";
    private static final String ASISTENCIA_PRESENCIA = "ASISTENCIA-PRESENCIA";
    private static final String ASISTENCIA_BREAK = "ASISTENCIA-BREAK";
    private static final String ASISTENCIA_ALMUERZO = "ASISTENCIA-ALMUERZO";
    private static final String ASISTENCIA_PRESENCIA_OPCIONES = "ASISTENCIA-PRESENCIA-OPCIONES";
    private static final String ASISTENCIA_PRESENCIA_INICIO = "ASISTENCIA-PRESENCIA-INICIO";
    private static final String ASISTENCIA_PRESENCIA_FIN = "ASISTENCIA-PRESENCIA-FIN";
    private static final String ASISTENCIA_PRESENCIA_FIN_OBSERVACION = "ASISTENCIA-PRESENCIA-FIN-OBSERVACION";
    private static final String ASISTENCIA_BREAK_OPCIONES = "ASISTENCIA-BREAK-OPCIONES";
    private static final String ASISTENCIA_BREAK_INICIO = "ASISTENCIA-BREAK-INICIO";
    private static final String ASISTENCIA_BREAK_FIN = "ASISTENCIA-BREAK-FIN";
    private static final String ASISTENCIA_ALMUERZO_OPCIONES = "ASISTENCIA-ALMUERZO-OPCIONES";
    private static final String ASISTENCIA_ALMUERZO_INICIO = "ASISTENCIA-ALMUERZO-INICIO";
    private static final String ASISTENCIA_ALMUERZO_FIN = "ASISTENCIA-ALMUERZO-FIN";

    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    protected void assignEvent() {
        String discriminator = getNodeValue(ASISTENCIA_EVENTOS);
        if (discriminator.equals(ASISTENCIA_PRESENCIA)) {
            discriminator = getNodeValue(ASISTENCIA_PRESENCIA_OPCIONES);
            if (discriminator.equals(ASISTENCIA_PRESENCIA_INICIO)) {
                getEntity().setEvent("PI");
            } else if (discriminator.equals(ASISTENCIA_PRESENCIA_FIN)) {
                getEntity().setEvent("PF");
            }
        } else if (discriminator.equals(ASISTENCIA_ALMUERZO)) {
            discriminator = getNodeValue(ASISTENCIA_ALMUERZO_OPCIONES);
            if (discriminator.equals(ASISTENCIA_ALMUERZO_INICIO)) {
                getEntity().setEvent("LI");
            } else if (discriminator.equals(ASISTENCIA_ALMUERZO_FIN)) {
                getEntity().setEvent("LF");
            }
        } else if (discriminator.equals(ASISTENCIA_BREAK)) {
            discriminator = getNodeValue(ASISTENCIA_BREAK_OPCIONES);
            if (discriminator.equals(ASISTENCIA_BREAK_INICIO)) {
                getEntity().setEvent("BI");
            } else if (discriminator.equals(ASISTENCIA_BREAK_FIN)) {
                getEntity().setEvent("BF");
            }
        }
    }

    @Override
    public void convertToBean() {
        getEntity().setEmployeeCode(
                getNotNullNodeValueFrom(ASISTENCIA_CODEMPLEADO));
        getEntity().setObservation(
                getNotNullNodeValueFrom(ASISTENCIA_PRESENCIA_FIN_OBSERVACION));
    }

}
