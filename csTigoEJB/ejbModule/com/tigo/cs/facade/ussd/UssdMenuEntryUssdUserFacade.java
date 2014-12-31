package com.tigo.cs.facade.ussd;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.UssdMenuEntryUssdUserAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
@DependsOn("EJBFacadeContainer")
public class UssdMenuEntryUssdUserFacade extends UssdMenuEntryUssdUserAPI {

    @EJB
    private FacadeContainer ussdFacadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return ussdFacadeContainer;
    }

    public UssdMenuEntryUssdUserFacade() {
    }
}
