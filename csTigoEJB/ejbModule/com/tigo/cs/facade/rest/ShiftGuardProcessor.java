package com.tigo.cs.facade.rest;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.entities.ShiftGuardService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.ShiftGuardServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.GUARDIA_TURNO)
public class ShiftGuardProcessor extends ShiftGuardServiceAPI<ShiftGuardService> {

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    protected void assignEvent() {
    }

    @Override
    public void convertToBean() {
    }
}
