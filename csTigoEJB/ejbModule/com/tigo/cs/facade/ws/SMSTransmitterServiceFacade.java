package com.tigo.cs.facade.ws;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.ws.SMSTransmitterServiceAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
public class SMSTransmitterServiceFacade extends SMSTransmitterServiceAPI {

    private static final long serialVersionUID = 6035129831778128657L;
    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public SMSTransmitterServiceFacade() {
    }

    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();
    }

}
