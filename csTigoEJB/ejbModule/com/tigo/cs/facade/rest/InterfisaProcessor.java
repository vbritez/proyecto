package com.tigo.cs.facade.rest;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.entities.InterfisaService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.InterfisaServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.INTERFISA)
public class InterfisaProcessor extends InterfisaServiceAPI<InterfisaService> {
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
