package com.tigo.cs.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.WnOperadoraAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
public class WnOperadoraFacade extends WnOperadoraAPI {

    @EJB
    protected FacadeContainer ussdFacadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return ussdFacadeContainer;
    }

    public WnOperadoraFacade() {

    }
}
