package com.tigo.cs.facade.ussddriver;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.entities.ShiftGuardService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.ShiftGuardServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.GUARDIA_TURNO)
public class ShiftGuardDriver extends ShiftGuardServiceAPI<ShiftGuardService> implements DriverInterface {

    private static final String GUARDIA_CODIGO_GUARDIA = "GUARDIA-CODIGO-GUARDIA";
    private static final String GUARDIA_OBSERVACION = "GUARDIA-OBSERVACION";

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    public void convertToBean() {
        getEntity().setGuardCode(getNodeValue(GUARDIA_CODIGO_GUARDIA));
        getEntity().setObservation(getNodeValue(GUARDIA_OBSERVACION));
    }

    @Override
    protected void assignEvent() {
    }

}
