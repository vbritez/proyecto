package com.tigo.cs.facade.rest;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.tigo.cs.api.entities.TigoMoneyService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.TigoMoneyServiceAPI;
import com.tigo.cs.commons.jpa.GenericFacadeException;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.TIGO_MONEY)
public class TigoMoneyProcessor extends TigoMoneyServiceAPI<TigoMoneyService> {

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

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {
        return super.processService();
    }
}
