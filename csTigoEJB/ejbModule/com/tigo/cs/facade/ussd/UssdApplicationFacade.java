package com.tigo.cs.facade.ussd;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.UssdApplicationAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
@DependsOn("EJBFacadeContainer")
public class UssdApplicationFacade extends UssdApplicationAPI {

    @EJB
    private FacadeContainer ussdFacadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return ussdFacadeContainer;
    }

    public UssdApplicationFacade() {
    }
}
