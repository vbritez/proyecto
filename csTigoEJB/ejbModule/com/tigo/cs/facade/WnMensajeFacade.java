package com.tigo.cs.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.WnMensajeAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
public class WnMensajeFacade extends WnMensajeAPI {

    @EJB
    protected FacadeContainer ussdFacadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return ussdFacadeContainer;
    }

    public WnMensajeFacade() {

    }
}
