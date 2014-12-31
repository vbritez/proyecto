package com.tigo.cs.facade.ussd;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.UssdServiceCorrespAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
public class UssdServiceCorrespFacade extends UssdServiceCorrespAPI {

    @EJB
    private FacadeContainer ussdFacadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return ussdFacadeContainer;
    }

    public UssdServiceCorrespFacade() {
    }
}
