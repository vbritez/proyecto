package com.tigo.cs.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.ScreenClientAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
public class ScreenClientFacade extends ScreenClientAPI {

    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public ScreenClientFacade() {
    }
}
