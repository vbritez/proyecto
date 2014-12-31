package com.tigo.cs.facade.ws;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import com.tigo.cs.api.facade.ws.TicketCSIAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Singleton
public class TicketCSIFacade extends TicketCSIAPI {

    private static final long serialVersionUID = 8584393088747015384L;
    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public TicketCSIFacade() {
    }

    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();
    }

}
