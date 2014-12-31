package com.tigo.cs.facade.rest;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.entities.VisitMedicService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.VisitMedicServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.VISITA_MEDICA)
public class VisitMedicProcessor extends VisitMedicServiceAPI<VisitMedicService> {

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
