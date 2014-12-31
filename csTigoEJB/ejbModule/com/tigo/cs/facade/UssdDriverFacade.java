package com.tigo.cs.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.UssdDriverAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
public class UssdDriverFacade extends UssdDriverAPI {

    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public UssdDriverFacade() {
    }

}
