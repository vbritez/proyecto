package com.tigo.cs.facade.menumovil;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.MenuMovilPeriodAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@DependsOn("EJBFacadeContainer")
@Stateless
public class MenuMovilPeriodFacade extends MenuMovilPeriodAPI {

    private static final long serialVersionUID = 3385278488131221924L;
    @EJB
    protected FacadeContainer facadeContainer;


    public MenuMovilPeriodFacade() {
    }

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }
}
