package com.tigo.cs.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.AndroidVersionAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
public class AndroidVersionFacade extends AndroidVersionAPI {
    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public AndroidVersionFacade() {

    }
}
