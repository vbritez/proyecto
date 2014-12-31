package com.tigo.cs.facade.ussddriver;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.entities.VisitService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.VisitServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.VISITA)
public class VisitDriver extends VisitServiceAPI<VisitService> implements DriverInterface {

    private static final String SC_VISITA = "SC-VISITA";
    private static final String VISITA_IN_CODCLIENTE = "VISITA-IN-CODCLIENTE";
    private static final String VISITA_IN_OBS = "VISITA-IN-OBS";
    private static final String VISITA_FIN_OBS = "VISITA-FIN-OBS";
    private static final String VISITA_FIN_MOTIVO = "VISITA-FIN-MOTIVO";
    private static final String VISITA_RAPIDA_CODCLIENTE = "VISITA-RAPIDA-CODCLIENTE";
    private static final String VISITA_RAPIDA_MOTIVO = "VISITA-RAPIDA-MOTIVO";
    private static final String VISITA_RAPIDA_OBS = "VISITA-RAPIDA-OBS";
   

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    public void convertToBean() {
        getEntity().setClientCode(
                getNotNullNodeValueFrom(VISITA_IN_CODCLIENTE,
                        VISITA_RAPIDA_CODCLIENTE));
        getEntity().setMotiveCode(
                getNotNullNodeValueFrom(VISITA_FIN_MOTIVO, VISITA_RAPIDA_MOTIVO));
        getEntity().setObservation(
                getNotNullNodeValueFrom(VISITA_IN_OBS, VISITA_FIN_OBS,
                        VISITA_RAPIDA_OBS));
    }

    @Override
    protected void assignEvent() {
        String discriminator = getNodeValue(SC_VISITA);
        if (discriminator.equals("VISITA-INICIO")) {
            getEntity().setEvent("ENT");
        } else if (discriminator.equals("VISITA-FIN")) {
            getEntity().setEvent("SAL");
        } else {
            getEntity().setEvent("ENTSAL");
        }

    }

}
