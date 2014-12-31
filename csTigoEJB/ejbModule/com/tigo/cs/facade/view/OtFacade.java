package com.tigo.cs.facade.view;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.OtAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;


@Stateless
public class OtFacade extends OtAPI {

    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public OtFacade() {
    }
    
}
