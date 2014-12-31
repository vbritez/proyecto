package com.tigo.cs.facade.ussd;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.UssdMenuEntryAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
@DependsOn("EJBFacadeContainer")
public class UssdMenuEntryFacade extends UssdMenuEntryAPI {

    @EJB
    private FacadeContainer ussdFacadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return ussdFacadeContainer;
    }

    public UssdMenuEntryFacade() {
    }
}
