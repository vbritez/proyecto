package com.tigo.cs.facade.rest;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.entities.CollectionService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.CollectionServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.COBRANZA)
public class CollectionProcessor extends CollectionServiceAPI<CollectionService> {

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
