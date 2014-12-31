package com.tigo.cs.facade.ussddriver;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.entities.GuardService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.GuardServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.GUARDIA)
public class GuardDriver extends GuardServiceAPI<GuardService> implements DriverInterface {

    @EJB
    private FacadeContainer facadeContainer;

    public static final String SC_GUARDIA = "SC-GUARDIA";
    public static final String GUARDIA_OPCIONES = "GUARDIA-OPCIONES";
    public static final String GUARDIA_OPCION_INICIAR = "GUARDIA-OPCION-INICIAR";
    public static final String GUARDIA_OPCION_MARCAR = "GUARDIA-OPCION-MARCAR";
    public static final String GUARDIA_OPCION_FINALIZAR = "GUARDIA-OPCION-FINALIZAR";
    public static final String GUARDIA_CODIGO_GUARDIA = "GUARDIA-CODIGO-GUARDIA";
    public static final String GUARDIA_OBSERVACION = "GUARDIA-OBSERVACION";
    private static final String GUARDIA_LUGAR = "GUARDIA-LUGAR";

    @Override
    public void convertToBean() {
        ((GuardService) getEntity()).setGuardCode(getNodeValue(GUARDIA_CODIGO_GUARDIA));
        ((GuardService) getEntity()).setObservation(getNodeValue(GUARDIA_OBSERVACION));
        ((GuardService) getEntity()).setPlace(getNodeValue(GUARDIA_LUGAR));
    }

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    protected void assignEvent() {
        String discriminator = getNodeValue(GUARDIA_OPCIONES);
        if (discriminator.equals(GUARDIA_OPCION_INICIAR)) {
            getEntity().setEvent("ENT");
        } else if (discriminator.equals(GUARDIA_OPCION_FINALIZAR)) {
            getEntity().setEvent("SAL");
        } else {
            getEntity().setEvent("REGISTRATION");
        }
    }
}
