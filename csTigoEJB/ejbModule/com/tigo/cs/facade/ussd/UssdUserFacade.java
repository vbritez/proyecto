package com.tigo.cs.facade.ussd;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import com.tigo.cs.api.facade.ClientAPI;
import com.tigo.cs.api.facade.UssdUserAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.ussd.UssdUser;

@Stateless
@DependsOn("EJBFacadeContainer")
public class UssdUserFacade extends UssdUserAPI {

    @EJB
    protected FacadeContainer ussdFacadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return ussdFacadeContainer;
    }

    public UssdUserFacade() {
    }
    
}
