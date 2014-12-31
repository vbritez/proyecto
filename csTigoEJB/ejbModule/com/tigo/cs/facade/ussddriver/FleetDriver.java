package com.tigo.cs.facade.ussddriver;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.entities.FleetService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.FleetServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.FLOTA)
public class FleetDriver extends FleetServiceAPI<FleetService> implements DriverInterface {

    @EJB
    private FacadeContainer facadeContainer;

    private static final String FLOTA_OPCIONES = "FLOTA_OPCIONES";
    private static final String FLOTA_MOVILES = "FLOTA_MOVILES";
    private static final String FLOTA_RECORRIDO = "FLOTA_RECORRIDO";
    private static final String FLOTA_MOVILES_OPCIONES = "FLOTA_MOVILES_OPCIONES";
    private static final String FLOTA_MOVILES_OPCIONES_RETIRO = "FLOTA_MOVILES_OPCIONES_RETIRO";
    private static final String FLOTA_MOVILES_OPCIONES_DEVOLUCION = "FLOTA_MOVILES_OPCIONES_DEVOLUCION";
    private static final String FLOTA_CODIGO_CHOFER = "FLOTA_CODIGO_CHOFER";
    private static final String FLOTA_CODIGO_VEHICULO = "FLOTA_CODIGO_VEHICULO";
    private static final String FLOTA_KM_INICIAL = "FLOTA_KM_INICIAL";
    private static final String FLOTA_KM_FINAL = "FLOTA_KM_FINAL";
    private static final String FLOTA_OBSERVACION = "FLOTA_OBSERVACION";
    private static final String FLOTA_CODIGO_CLIENTE = "FLOTA_CODIGO_CLIENTE";
    private static final String FLOTA_KM_RECORRIDO = "FLOTA_KM_RECORRIDO";

    @Override
    protected void assignEvent() {
        String discriminator = getNodeValue(FLOTA_OPCIONES);
        if (discriminator.equals(FLOTA_MOVILES)) {
            discriminator = getNodeValue(FLOTA_MOVILES_OPCIONES);
            if (discriminator.equals(FLOTA_MOVILES_OPCIONES_RETIRO)) {
                getEntity().setEvent("FRT");
            } else if (discriminator.equals(FLOTA_MOVILES_OPCIONES_DEVOLUCION)) {
                getEntity().setEvent("FDV");
            }
        } else if (discriminator.equals(FLOTA_RECORRIDO)) {
            getEntity().setEvent("FRC");
        }
    }

    @Override
    public void convertToBean() {
        getEntity().setClientCode(getNodeValue(FLOTA_CODIGO_CLIENTE));
        getEntity().setDriverCode(getNodeValue(FLOTA_CODIGO_CHOFER));
        getEntity().setVehicleCode(getNodeValue(FLOTA_CODIGO_VEHICULO));
        getEntity().setInitialKm(getNodeValue(FLOTA_KM_INICIAL));
        getEntity().setFinalKm(getNodeValue(FLOTA_KM_FINAL));
        getEntity().setObservation(getNodeValue(FLOTA_OBSERVACION));
        getEntity().setTraveledKm(getNodeValue(FLOTA_KM_RECORRIDO));
    }

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

}
